import org.json.JSONArray;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;

import static java.lang.String.valueOf;

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
//        Create file to store US city IDs in
        File file = new File("cityIds.txt");
        FileWriter fw;
        try {
            fw = new FileWriter(file);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
//        Write ids to file
        for (int id : ids) {
            try {
                fw.write(String.format("%d\n", id));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
