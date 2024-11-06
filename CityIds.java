import org.json.JSONArray;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
/*TODO:
   1. Read cityIds file into json array.
   2. Access objects containing US and store its city id
   3. Return ids array
*/
public class CityIds {
    public static void main(String[] args) {
//        Read json data from file into string
        String content;
        try {
            content = Files.readString(Path.of("city.list.json"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
//        Store in JSONArray and get US city IDs
        JSONArray jsonArray = new JSONArray(content);
        CityIdParser cityIdParser = new CityIdParser(jsonArray);
        ArrayList<Integer> ids = cityIdParser.getIds();
        for (int id : ids) {
            System.out.println(id);
        }
    }
}
