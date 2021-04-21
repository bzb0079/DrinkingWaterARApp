package app.th.project.drinkingWaterAR.model;
import com.google.gson.*;
import java.util.ArrayList;
import java.util.List;


public class PlaceList {

public static List<PlaceItem> getBuildings() {
        List<PlaceItem> list = new ArrayList<>();
        WaterLocation obj = new WaterLocation();
        JsonArray venues = obj.getJsonObjectWater().getAsJsonObject("response").getAsJsonArray("water_locations");
        venues.forEach(item -> {
            list.add(getBuilding(item.getAsJsonObject()));
        });
//        Log.d("here is the list" , list.toString());
        return list;
        }

private static PlaceItem getBuilding(JsonObject waterLocationJSON) {

        String name =  waterLocationJSON.getAsJsonPrimitive("name").getAsString();
        JsonObject location = waterLocationJSON.getAsJsonObject("location");
        String address = location.has("description")? location.getAsJsonPrimitive("description").getAsString() : "not set"; //"Shelby; Total No of drinking water station : 6;" +
        //"Total no of plastic glasses saved: 122534658 "
        String lat =  location.getAsJsonPrimitive("lat").getAsString(); //32.606198
        String lon = location.getAsJsonPrimitive("lng").getAsString(); //-85.487029
        return new PlaceItem(name, address, lat, lon);
        }


}