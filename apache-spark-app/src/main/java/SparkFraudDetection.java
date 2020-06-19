import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;
import org.apache.spark.sql.streaming.OutputMode;
import org.apache.spark.sql.streaming.StreamingQuery;
import org.apache.spark.sql.streaming.StreamingQueryException;
import utils.Configuration;

import java.io.IOException;

import static org.apache.spark.sql.functions.*;

// Aplikácia, ktorá filtruje platby pomocou čiernej listiny načítanej z Apache Kafka
public class SparkFraudDetection {

    public static void main(String[] args) throws IOException, StreamingQueryException {
        // Nastavenie log levelu
        Logger.getLogger("org.apache").setLevel(Level.WARN);

        // Načítanie údajov z konfigurácie
        Configuration conf = Configuration.getInstance();
        String validTransactionTopic = conf.getProperty("kafka.tx-transfer-topic-valid");
        String invalidTransactionTopic = conf.getProperty("kafka.tx-transfer-topic-invalid");

        // Vytvorenie a nastavenie SparkSession na základe údajov z konfigurácie
        SparkSession spark = SparkSession.builder()
                .master(conf.getProperty("spark.master"))
                .appName(conf.getProperty("spark.app-name-fraud"))
                .getOrCreate();
        spark.conf().set("spark.sql.shuffle.partitions", conf.getProperty("spark.shuffle.partitions"));

        // Načítanie čiernej listiny
        loadBlackList(spark, conf.getProperty("kafka.endpoint"), conf.getProperty("kafka.blacklist-topic"));

        // Načítanie transakcií
        Dataset<Row> transferStream = loadTransferStream(spark, conf.getProperty("kafka.endpoint"), conf.getProperty("kafka.tx-transfer-topic"));

        // Získanie aktuálnych údajov z čiernej listiny
        Dataset<Row> blackListCurrent = spark.sql("SELECT bl_iban, last(blacklisted) as blacklisted FROM blacklist GROUP BY bl_iban");

        // Napárovanie transakcie a príslušného záznamu z čiernej listiny
        Dataset<Row> transfers = transferStream.join(
                blackListCurrent,
                expr("creditor_iban = bl_iban"),
                "inner"
        ).selectExpr("id", "transaction_date", "posting_date", "amount", "type", "VC", "SC", "CC", "creditor_iban", "debtor_iban", "blacklisted");

        // Vytvorenie výstupného stream-u
        transfers = createOutputTransfers(transfers, validTransactionTopic, invalidTransactionTopic);

        // Zaslanie dát do Apache Kafka
        transfers.writeStream()
                .format("kafka")
                .option("kafka.bootstrap.servers", conf.getProperty("kafka.endpoint"))
                .option("checkpointLocation", conf.getProperty("spark.blacklist-checkpoint-directory"))
                .start();

        // Výpis dát na konzolu
        StreamingQuery query = transfers.writeStream()
                .format("console")
                .outputMode(OutputMode.Append())
                .option("truncate", false)
                .start();

        query.awaitTermination();

    }

    // Načítanie čiernej listiny z Apache Kafka
    public static void loadBlackList(SparkSession spark, String kafkaEndpoint, String kafkaTopic) {
        Dataset<Row> blackList = spark.readStream()
                .format("kafka")
                .option("kafka.bootstrap.servers", kafkaEndpoint)
                .option("subscribe", kafkaTopic)
                .option("startingOffsets", "earliest")
                .load();

        blackList = blackList.selectExpr("split(cast(value as string),',') as value", "cast(timestamp as timestamp) timestamp")
                .selectExpr("value[0] as bl_iban", "value[1] as blacklisted", "timestamp");

        blackList.writeStream()
                .format("memory")
                .queryName("blacklist")
                .outputMode(OutputMode.Append())
                .start();
    }

    // Načítanie transakcií z Apache Kafka
    public static Dataset<Row> loadTransferStream(SparkSession spark, String kafkaEndpoint, String kafkaTopic) {
        Dataset<Row> transferStream = spark.readStream()
                .format("kafka")
                .option("kafka.bootstrap.servers", kafkaEndpoint)
                .option("subscribe", kafkaTopic)
                .option("startingOffsets", "earliest")
                .option("enable.auto.commit", "false")
                .load();

        return transferStream.selectExpr("split(cast(value as string),',') as values")
                .selectExpr(
                        "values[0] as id",
                        "to_timestamp(values[1], 'yyyy-MM-dd HH:mm') as transaction_date",
                        "to_timestamp(values[2], 'yyyy-MM-dd HH:mm') as posting_date",
                        "cast(values[3] as double) as amount",
                        "values[4] as currency",
                        "values[5] as type",
                        "values[6] as VC",
                        "values[7] as SC",
                        "values[8] as CC",
                        "values[9] as creditor_iban",
                        "values[10] as debtor_iban"
                );
    }

    // Metóda na vytvorenie výstupnej transakcie
    public static Dataset<Row> createOutputTransfers(Dataset<Row> transfers, String validTransactionTopic, String invalidTransactionTopic) {
        transfers = transfers.withColumn("topic", when(transfers.col("blacklisted").equalTo("N"), validTransactionTopic).otherwise(invalidTransactionTopic));
        return transfers.select(
                transfers.col("id").as("key"),
                concat(
                        transfers.col("id"), lit(","),
                        transfers.col("transaction_date"), lit(","),
                        transfers.col("posting_date"), lit(","),
                        transfers.col("amount"), lit(","),
                        transfers.col("type"), lit(","),
                        transfers.col("VC"), lit(","),
                        transfers.col("SC"), lit(","),
                        transfers.col("CC"), lit(","),
                        transfers.col("creditor_iban"), lit(","),
                        transfers.col("debtor_iban"), lit(","),
                        transfers.col("blacklisted")
                ).as("value"),
                transfers.col("topic").as("topic")
        );
    }

}
