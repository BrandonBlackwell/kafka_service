import WeatherDataProducerCallback.WeatherDataProducerCallback;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.net.InetAddress;
import java.net.URI;
import java.net.UnknownHostException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Properties;
import java.util.Scanner;
import java.util.concurrent.Future;

public class WeatherDataProducer {
    public static void main(String[] Args) throws UnknownHostException {
        //  Read in US city ids. Externalize?
        Scanner scanner = null;
        try {
            scanner = new Scanner(new File("cityIds.txt"));
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }

        ArrayList<String> city_ids = new ArrayList<>();

        while (scanner.hasNextLine()) {
            String id = scanner.nextLine();
            city_ids.add(id);
        }
        scanner.close();

        //  Create Producer config object
        final String BROKER          = System.getenv("BROKER");
        final String SCHEMA_REGISTRY = System.getenv("SCHEMA_REGISTRY");

        Properties config = new Properties();
        config.put("client.id", InetAddress.getLocalHost().getHostName());
        config.put("bootstrap.servers", BROKER);
        config.put("key.serializer", "io.confluent.kafka.serializers.KafkaAvroSerializer");
        config.put("value.serializer", "io.confluent.kafka.serializers.KafkaAvroSerializer");
        config.put("schema.registry.url", SCHEMA_REGISTRY);
        config.put("acks", "all");

        //  Create Producer
        KafkaProducer<String, JSONObject> producer = new KafkaProducer<>(config);
        //  Save topic name for writing to
        String topic = "USCitiesWeatherData";
        //  Save API Key
        final String API_KEY         = System.getenv("API_KEY");

        //  Continuously request data from the weather api
        while (true) {
            HttpClient client = null;
            //  Make API requests for every city
            for (int i = 0; i <= city_ids.size(); i++) {
                i %= city_ids.size();
                String uri = String.format("https://api.openweathermap.org/data/2.5/weather?id=%s&appid=%s&lang=en&units=imperial", city_ids.get(i), API_KEY);

                // Request data using httpclient
                try {
                    client = HttpClient.newBuilder()
                            .version(HttpClient.Version.HTTP_2)
                            .connectTimeout(Duration.ofSeconds(20))
                            .build();

                    HttpRequest request = HttpRequest.newBuilder()
                            .uri(URI.create(uri))
                            .GET()
                            .build();

                    HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

                    //  Save the city name to be used as topic key
                    JSONObject weatherData = new JSONObject(response.body());
                    String cityName = String.valueOf(weatherData.get("name"));

                    ProducerRecord<String, JSONObject> record = new ProducerRecord<>(topic, cityName, weatherData);

                    Future<RecordMetadata> recordMetadataFuture = producer.send(record, new WeatherDataProducerCallback());

                    //  Log the record's metadata using slf4j logging
                } catch (Exception e) {
                    client = null;
                    throw new RuntimeException("Something went wrong" + e);
                }

//              Only 60 api calls per min can be made so to be safe make 30 calls per min.
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }

            }
        }
    }
}
