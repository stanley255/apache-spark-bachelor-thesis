package kafka;

import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.common.serialization.StringSerializer;

import java.util.Properties;
import java.util.concurrent.ExecutionException;

// Trieda pre produkovanie záznamov do Apache Kafka
public class KafkaProducer {

    private final Producer<String, String> producer;
    private Properties props = new Properties();

    public KafkaProducer(String endpoint, String clientId) {
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, endpoint);
        props.put(ProducerConfig.CLIENT_ID_CONFIG, clientId);
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        producer = new org.apache.kafka.clients.producer.KafkaProducer<>(props);
    }

    public RecordMetadata produce(String key, String messageString, String topic) {
        RecordMetadata metadata = null;
        final ProducerRecord<String, String> record = new ProducerRecord<>(topic, key, messageString);
        try {
            metadata = producer.send(record).get();
        } catch (ExecutionException | InterruptedException e) {
            System.out.println(e);
        }
        return metadata;
    }

    public RecordMetadata produce(String messageString, String topic) {
        RecordMetadata metadata = null;
        final ProducerRecord<String, String> record = new ProducerRecord<>(topic, messageString);
        try {
            metadata = producer.send(record).get();
        } catch (ExecutionException | InterruptedException e) {
            System.out.println(e);
        }
        return metadata;
    }

}
