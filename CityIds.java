import org.json.JSONArray;

import java.util.ArrayList;
/*TODO:
   1. Read cityIds file into json array.
   2. Access objects containing US and store its city id
   3. Return ids array
*/
public class CityIds {
    public static void main(String[] args) {
        JSONArray jsonArray = new JSONArray("[{id: 1, name: brandon},{id: 2, name: britney}]");
        CityIdParser cityIdParser = new CityIdParser(jsonArray);
        ArrayList<Integer> ids = cityIdParser.getIds();
        for (int id: ids) {
            System.out.println(id);
        }
    }
}
