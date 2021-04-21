package app.th.project.drinkingWaterAR.model;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

public class WaterLocation {
    String water_location = "{\n" +
            "   \"response\":{\n" +
            "      \"water_locations\":[\n" +
            "         {\n" +
            "            \"id\":\"1\",\n" +
            "            \"name\":\"Shelby\",\n" +
            "            \"location\":{\n" +
            "               \"description\":\"No of Drinking Water Stations: 5; Total no of saved plastic cups: 10,000\",\n" +
            "               \"lat\":32.606198,\n" +
            "               \"lng\":-85.487029\n" +
            "            }\n" +
            "         },\n" +
            "         {\n" +
            "            \"id\":\"2\",\n" +
            "            \"name\":\"Haley\",\n" +
            "            \"location\":{\n" +
            "               \"description\":\"No of Drinking Water Stations: 8; Total No of saved plastic cups: 50,000\",\n" +
            "               \"lat\":32.603016,\n" +
            "               \"lng\":-85.487211\n" +
            "            }\n" +
            "         }\n" +
            "      ]\n" +
            "   }\n" +
            "}";
    public JsonObject getJsonObjectWater() {
        return jsonObjectWater;
    }
    JsonObject jsonObjectWater = new Gson().fromJson(water_location, JsonObject.class);
}


