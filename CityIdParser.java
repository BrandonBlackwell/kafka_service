import org.json.JSONArray;

import java.util.ArrayList;

public class CityIdParser {
    public CityIdParser(final JSONArray jsonArray){
        this.setM_jsonArray(jsonArray);
    };

    public ArrayList<Integer> getIds(){
        ArrayList<Integer> ids = new ArrayList<>();
        for (int i=0; i<this.m_jsonArray.length(); ++i){
            Integer id = (Integer) this.m_jsonArray.getJSONObject(i).get("id");
            System.out.println(id);
            ids.add(id);
        }
        return ids;
    }

    private JSONArray m_jsonArray;

    public JSONArray getM_jsonArray() {
        return m_jsonArray;
    }

    public void setM_jsonArray(JSONArray m_jsonArray) {
        this.m_jsonArray = m_jsonArray;
    }
}
