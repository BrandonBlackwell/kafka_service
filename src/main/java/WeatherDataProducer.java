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
        //  Load city ids
        final ArrayList<String> cityIds = getCityIds();
        //  Create a Kafka producer
        final KafkaProducer<String, JSONObject> producer = getKafkaProducer();
        //  Save topic name for writing to
        final String topic = "USCitiesWeatherData";
        //  Save API Key
        final String API_KEY = System.getenv("API_KEY");

        //  Continuously request data from the weather api
        while (true) {
            HttpClient client = null;
            //  Make API requests for every city
            for (int i = 0; i <= cityIds.size(); i++) {
                i %= cityIds.size();
                String uri = String.format("https://api.openweathermap.org/data/2.5/weather?id=%s&appid=%s&lang=en&units=imperial", cityIds.get(i), API_KEY);

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

    private static KafkaProducer<String, JSONObject> getKafkaProducer() throws UnknownHostException {
        Properties config = new Properties();
        //  Create Producer config object
        final String BROKER          = System.getenv("BROKER");
        final String SCHEMA_REGISTRY = System.getenv("SCHEMA_REGISTRY");

        config.put("client.id", InetAddress.getLocalHost().getHostName());
        config.put("bootstrap.servers", BROKER);
        config.put("key.serializer", "io.confluent.kafka.serializers.KafkaAvroSerializer");
        config.put("value.serializer", "io.confluent.kafka.serializers.KafkaAvroSerializer");
        config.put("schema.registry.url", SCHEMA_REGISTRY);
        config.put("acks", "all");

        //  Create Producer
        return new KafkaProducer<>(config);
    }

    private static ArrayList<String> getCityIds() {
        //  Read in US city ids. Externalize?
        Scanner scanner = null;
        try {
            scanner = new Scanner(new File("cityIds.txt"));
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }

        ArrayList<String> cityIds = new ArrayList<>();

        while (scanner.hasNextLine()) {
            String id = scanner.nextLine();
            cityIds.add(id);
        }
        scanner.close();

        return cityIds;
    }
}
