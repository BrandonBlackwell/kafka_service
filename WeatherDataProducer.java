import java.io.File;
import java.io.FileNotFoundException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Scanner;

public class WeatherDataProducer {
    public static void main(String[] Args) {
//        Read in US city ids
        Scanner scanner = null;
        try {
            scanner = new Scanner(new File("cityIds.txt"));
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
        ArrayList<String> city_ids = new ArrayList<>();
        while (scanner.hasNextLine()) {
            String id = scanner.nextLine();
//            System.out.println(id);
            city_ids.add(id);
        }
        scanner.close();

        final String API_KEY = System.getenv("API_KEY");
        while (true) {
            HttpClient client = null;
//        Make API requests
            for (int i = 0; i <= city_ids.size(); i++) {
                i %= city_ids.size();
                String uri = String.format("https://api.openweathermap.org/data/2.5/weather?id=%s&appid=%s&lang=en&units=imperial", city_ids.get(i), API_KEY);

                // Try to request data and receive response after creating a httpclient
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
/*
 TODO:
    1. Send through weather data processor class
    2. Send to Kafka Broker
*/
                    System.out.println(response.body());
                    System.out.println(response.body().getClass());
                } catch (Exception e) {
                    client = null;
                    throw new RuntimeException("Something went wrong" + e);
                }

                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }

            }
        }
    }
}
