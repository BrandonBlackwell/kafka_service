import org.json.JSONArray;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class CityIdParserTest {
    JSONArray json_array = new JSONArray("[{id: 1, name: a}, {id: 2, name: b}, {id: 3, name: c}]");
    CityIdParser cityIdParser = new CityIdParser(json_array);

    @org.junit.jupiter.api.Test
    void getIds() {
        ArrayList<Integer> result = cityIdParser.getIds();
        ArrayList<Integer> expected = new ArrayList<>();
        expected.add(1);
        expected.add(2);
        expected.add(3);
        assertEquals(expected, result);
    }
}