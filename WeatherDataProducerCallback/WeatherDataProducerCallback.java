package WeatherDataProducerCallback;

import org.apache.kafka.clients.producer.RecordMetadata;

public class WeatherDataProducerCallback implements org.apache.kafka.clients.producer.Callback {
    @Override
    public void onCompletion(RecordMetadata recordMetadata, Exception e) {

    }
}
