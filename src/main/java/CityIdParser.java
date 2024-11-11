import org.json.JSONArray;

import java.util.ArrayList;

public class CityIdParser {
    public CityIdParser(final JSONArray jsonArray){
        this.setM_jsonArray(jsonArray);
    };

    public ArrayList<Integer> getIds(){
        ArrayList<Integer> ids = new ArrayList<>();
        JSONArray jsonArray = getM_jsonArray();
        for (int i=0; i<jsonArray.length(); ++i){
            String country = (String) jsonArray.getJSONObject(i).get("country");
            if (country.equals("US")){
                Integer id = (Integer) jsonArray.getJSONObject(i).getInt("id");
                ids.add(id);
            }

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
