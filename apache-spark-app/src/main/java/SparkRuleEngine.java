import drools.Drools;
import entities.Client;
import entities.TransactionSentenceForCard;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.spark.api.java.function.MapFunction;
import org.apache.spark.broadcast.Broadcast;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.RowFactory;
import org.apache.spark.sql.SparkSession;
import org.apache.spark.sql.catalyst.encoders.RowEncoder;
import org.apache.spark.sql.streaming.OutputMode;
import org.apache.spark.sql.streaming.StreamingQuery;
import org.apache.spark.sql.streaming.StreamingQueryException;
import org.apache.spark.sql.types.DataTypes;
import org.apache.spark.sql.types.Metadata;
import org.apache.spark.sql.types.StructField;
import org.apache.spark.sql.types.StructType;
import org.kie.api.KieBase;
import scala.reflect.ClassTag;
import scala.reflect.ClassTag$;
import utils.Configuration;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Optional;

// Aplikácia, ktorá slúži k aplikovaniu biznis pravidiel na streamované dáta
public class SparkRuleEngine {

    public static final String dateFormat = "yyyy-MM-dd HH:mm:ss.S";
    private static final StructType OUTPUT_DATA_SCHEMA = new StructType(new StructField[] {
            new StructField("topic", DataTypes.StringType, false, Metadata.empty()),
            new StructField("key", DataTypes.StringType, false, Metadata.empty()),
            new StructField("value", DataTypes.StringType, false, Metadata.empty()),
    });

    public static void main(String[] args) throws IOException, StreamingQueryException {
        // Nastavenie log levelu
        Logger.getLogger("org.apache").setLevel(Level.WARN);

        // Načítanie údajov z konfigurácie
        Configuration conf = Configuration.getInstance();
        String validTransactionTopic = conf.getProperty("kafka.tx-card-topic-valid");
        String invalidTransactionTopic = conf.getProperty("kafka.tx-card-topic-invalid");
        String externalScriptPath = conf.getProperty("rule-engine-app.script-location");

        // Vytvorenie a nastavenie SparkSession na základe údajov z konfigurácie
        SparkSession spark = SparkSession.builder()
                .master(conf.getProperty("spark.master"))
                .appName(conf.getProperty("spark.app-name-rules"))
                .getOrCreate();
        spark.conf().set("spark.sql.shuffle.partitions", conf.getProperty("spark.shuffle.partitions"));

        // Broadcast bázy pravidiel z rule engine-u
        Broadcast<KieBase> broadcastRules = getBroadcastRules(spark);

        // Načítanie klientských dát
        Dataset<Row> clientStream = getClientStream(spark, conf.getProperty("kafka.endpoint"), conf.getProperty("kafka.client-topic"));

        // Načítanie stream-u transakcií
        Dataset<Row> transfersStream = getTransactionStream(spark, conf.getProperty("kafka.endpoint"), conf.getProperty("kafka.tx-card-topic"));

        Dataset<Row> outputStream = transfersStream.join(clientStream, transfersStream.col("tx_clientId").equalTo(clientStream.col("cl_id")))
                .map((MapFunction<Row, Row>) txClRow -> {
                    // Ziskaj objekt transakcie
                    TransactionSentenceForCard tsfc = getTransactionFromJoinedRow(txClRow);
                    // Set default topic-ov pre transakciu
                    tsfc.setTopic(
                        tsfc.getErrorCode().equals("") ? validTransactionTopic: invalidTransactionTopic
                    );
                    // Ziskaj objekt klienta
                    Client client = getClientFromJoinedRow(txClRow);
                    // Ziskaj skore klienta z Python scriptu
                    loadScoreFromExtPySource(client, externalScriptPath);
                    // Pouzi rule engine
                    applyBusinessRules(tsfc, client, broadcastRules);
                    // Vrat namapovany stlpec
                    return getOutputTransactionRow(tsfc);
                }, RowEncoder.apply(OUTPUT_DATA_SCHEMA));

        // Zaslanie výstupu do Apache Kafka
        outputStream.writeStream()
                .format("kafka")
                .option("kafka.bootstrap.servers", conf.getProperty("kafka.endpoint"))
                .option("checkpointLocation", conf.getProperty("spark.rule-engine-checkpoint-directory"))
                .start();

        // Výpis dát na konzolu
        StreamingQuery query = outputStream.writeStream()
                .format("console")
                .outputMode(OutputMode.Append())
                .option("truncate", false)
                .start();

        query.awaitTermination();

    }

    // Získanie broadcast-ovanej bázy pravidiel
    private static Broadcast<KieBase> getBroadcastRules(SparkSession spark) {
        KieBase rules = Drools.loadRules();
        ClassTag<KieBase> classTagKieBase = ClassTag$.MODULE$.apply(KieBase.class);
        return spark.sparkContext().broadcast(rules, classTagKieBase);
    }

    // Získanie klientského stream-u z Apache Kafka
    private static Dataset<Row> getClientStream(SparkSession spark, String kafkaEndpoint, String kafkaTopic) {
        return spark.readStream()
                .format("kafka")
                .option("kafka.bootstrap.servers", kafkaEndpoint)
                .option("subscribe", kafkaTopic)
                .option("startingOffsets", "earliest")
                .load()
                .selectExpr("split(cast(value as string), ',') as values")
                .selectExpr(
                        "values[0] as cl_id",
                        "values[1] as cl_name",
                        "values[2] as cl_surname",
                        "values[3] as cl_age",
                        "values[4] as cl_gender",
                        "values[5] as cl_approvedOverdraft",
                        "cast(values[6] as double) as cl_limit"
                );
    }

    // Získanie transakcií z Apache Kafka
    private static Dataset<Row> getTransactionStream(SparkSession spark, String kafkaEndpoint, String kafkaTopic) {
        return spark.readStream()
                .format("kafka")
                .option("kafka.bootstrap.servers", kafkaEndpoint)
                .option("subscribe", kafkaTopic)
                .option("startingOffsets", "earliest")
                .option("enable.auto.commit", "false")
                .load()
                .selectExpr("split(cast(value as string), ',') as values")
                .selectExpr(
                        "values[0] as tx_id",
                        "to_timestamp(values[1], 'yyyy-MM-dd HH:mm') as tx_transactionDate",
                        "to_timestamp(values[2], 'yyyy-MM-dd HH:mm') as tx_postingDate",
                        "cast(values[3] as double) as tx_amount",
                        "values[4] as tx_currency",
                        "values[5] as tx_type",
                        "values[6] as tx_variableCode",
                        "values[7] as tx_specificCode",
                        "values[8] as tx_constantCode",
                        "values[9] as tx_cardNumber",
                        "values[10] as tx_operationCode",
                        "values[11] as tx_errorCode",
                        "values[12] as tx_merchantCode",
                        "values[13] as tx_clientId"
                );
    }

    // Metóda na získanie transakcie zo spojeného záznamu
    private static TransactionSentenceForCard getTransactionFromJoinedRow(Row joinedRow) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(dateFormat);
        TransactionSentenceForCard tsfc = new TransactionSentenceForCard();
        tsfc.setId(joinedRow.getAs("tx_id"));
        tsfc.setTransactionDate(LocalDateTime.parse(joinedRow.getAs("tx_transactionDate").toString(), formatter));
        tsfc.setPostingDate(LocalDateTime.parse(joinedRow.getAs("tx_postingDate").toString(), formatter));
        tsfc.setAmount(joinedRow.getAs("tx_amount"));
        tsfc.setCurrency(joinedRow.getAs("tx_currency"));
        tsfc.setType(joinedRow.getAs("tx_type"));
        tsfc.setVariableCode(joinedRow.getAs("tx_variableCode"));
        tsfc.setSpecificCode(joinedRow.getAs("tx_specificCode"));
        tsfc.setConstantCode(joinedRow.getAs("tx_constantCode"));
        tsfc.setCardNumber(joinedRow.getAs("tx_cardNumber"));
        tsfc.setOperationCode(joinedRow.getAs("tx_operationCode"));
        tsfc.setErrorCode(joinedRow.getAs("tx_errorCode"));
        tsfc.setMerchantCode(joinedRow.getAs("tx_merchantCode"));
        tsfc.setClientId(joinedRow.getAs("tx_clientId"));
        return tsfc;
    }

    // Metóda na získanie objektu klienta zo spojeného záznamu
    private static Client getClientFromJoinedRow(Row joinedRow) {
        Client client = new Client();
        client.setId(joinedRow.getAs("cl_id"));
        client.setName(joinedRow.getAs("cl_name"));
        client.setSurname(joinedRow.getAs("cl_surname"));
        client.setAge(Integer.parseInt(joinedRow.getAs("cl_age")));
        client.setGender(joinedRow.getAs("cl_gender"));
        client.setApprovedOverdraft(joinedRow.getAs("cl_approvedOverdraft").equals("Y"));
        client.setLimit(joinedRow.getAs("cl_limit"));
        return client;
    }

    // Načítanie skóre použitím Python skriptu
    private static void loadScoreFromExtPySource(Client client, String scriptLocation) throws IOException {
        ProcessBuilder pb = new ProcessBuilder(
                "python3",
                scriptLocation,
                client.getAge().toString(),
                client.getGender()
        );
        Process p = pb.start();
        BufferedReader in = new BufferedReader(new InputStreamReader(p.getInputStream()));
        client.setScore(Double.parseDouble(in.readLine()));
    }

    // Metóda na aplikovanie biznis pravidiel použitím Drools
    private static void applyBusinessRules(TransactionSentenceForCard transaction, Client client, Broadcast<KieBase> rules) {
        Collection<Object> clientAndTransaction = new ArrayList<>();
        clientAndTransaction.add(client);
        clientAndTransaction.add(transaction);
        Drools.applyRules(rules.getValue(), clientAndTransaction);
    }

    // Získanie výstupného záznamu
    private static Row getOutputTransactionRow(TransactionSentenceForCard transaction) {
        return RowFactory.create(
                transaction.getTopic(),
                transaction.getId(),
                getCommaSeparatedOutput(transaction)
        );
    }

    // Transformovanie transakcie na požadovaný (čiarkami oddelený) záznam
    private static String getCommaSeparatedOutput(TransactionSentenceForCard transaction) {
        return transaction.getId()+','+
                transaction.getTransactionDate()+','+
                transaction.getPostingDate()+','+
                transaction.getAmount()+','+
                transaction.getCurrency()+','+
                transaction.getType()+','+
                transaction.getVariableCode()+','+
                transaction.getSpecificCode()+','+
                transaction.getConstantCode()+','+
                transaction.getCardNumber()+','+
                transaction.getOperationCode()+','+
                transaction.getErrorCode()+','+
                transaction.getMerchantCode()+','+
                transaction.getClientId()+','+
                Optional.ofNullable(transaction.getBusinessRuleMessage()).orElse("");
    }

}
