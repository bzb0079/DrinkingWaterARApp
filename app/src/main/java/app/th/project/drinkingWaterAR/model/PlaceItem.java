package app.th.project.drinkingWaterAR.model;
import com.google.gson.annotations.SerializedName;

import org.jetbrains.annotations.NotNull;

public final class PlaceItem {
    @SerializedName("name")
    @NotNull
    private final String name;
    @SerializedName("description")
    @NotNull
    private final String description;
    @SerializedName("lat")
    @NotNull
    private final String lat;

    @NotNull
    public String getName() {
        return name;
    }

    @NotNull
    public String getDescription() {
        return description;
    }

    @NotNull
    public String getLat() {
        return lat;
    }

    @NotNull
    public String getLon() {
        return lon;
    }
    @SerializedName("lng")
    @NotNull
    private final String lon;


    public PlaceItem(@NotNull String name, @NotNull String description, @NotNull String lat, @NotNull String lon) {
        this.name = name;
        this.description = description;
        this.lat = lat;
        this.lon = lon;
    }
}
