package WeatherDataProducerCallback;

import org.apache.kafka.clients.producer.RecordMetadata;
/*
Implement the Dead Letter Queue (DLQ) Sending Pattern to MariaDB or MongoDB.
Also, integrate the slf4j logger to log error messages.
*/
public class WeatherDataProducerCallback implements org.apache.kafka.clients.producer.Callback {
    @Override
    public void onCompletion(RecordMetadata recordMetadata, Exception e) {

    }
}
