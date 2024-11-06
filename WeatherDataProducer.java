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
        Scanner scanner = new Scanner("cityIds.txt");
        ArrayList<String> city_ids = new ArrayList<>();
        while (scanner.hasNextLine()) {
            String id = scanner.nextLine();
            city_ids.add(id);
        }
        scanner.close();

//        Make API requests
//        for (String city_id: city_ids){}
        final String API_KEY = System.getenv("API_KEY");
        String uri = String.format("https://api.openweathermap.org/data/2.5/weather?id=%d&appid=%s&lang=en&units=imperial", city_id, API_KEY);
        /*TODO:
           1. Grab all US city codes.
           2. Grab all US city data (Use timer to send API request once per sec due to constraints. 60 per min)
           3. Process/filter it
           4. Send to Kafka Broker-Topic*/

        // Try to request data and receive response after creating a httpclient
        HttpClient client = null;
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
            throw new RuntimeException("Something went wrong" + e);
        } finally {
            client = null;
        }

        //
    }
}
