package com.myapp.travelize.models;


import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class Places {
    public class Root {
        @SerializedName("html_attributions")
        public List<String> attributions = new ArrayList<>();
        @SerializedName("results")
        public List<Result> results = new ArrayList<>();
        @SerializedName("status")
        public String status;

        public List<String> getAttributions() {
            return attributions;
        }

        public List<Result> getResults() {
            return results;
        }

        public String getStatus() {
            return status;
        }
    }

    public class Result {
        @SerializedName("business_status")
        public String businessStatus;
        @SerializedName("geometry")
        public Geometry geometry;
        @SerializedName("icon")
        public String icon;
        @SerializedName("name")
        public String name;
        @SerializedName("opening_hours")
        public OpeningHour openingHour;
        @SerializedName("photos")
        public List<Photo> photos = new ArrayList<>();
        @SerializedName("place_id")
        public String placeID;
        @SerializedName("plus_code")
        public PlusCode plusCode;
        @SerializedName("price_level")
        public Integer priceLevel;
        @SerializedName("rating")
        public Double rating;
        @SerializedName("reference")
        public String reference;
        @SerializedName("scope")
        public String scope;
        @SerializedName("types")
        public List<String> types = new ArrayList<>();
        @SerializedName("user_ratings_total")
        public Integer totalRatings;
        @SerializedName("vicinity")
        public String vicinity;
    }

    public class Geometry {
        @SerializedName("location")
        public Location location;
        @SerializedName("viewport")
        public Viewport viewport;
    }

    public class Location {
        @SerializedName("lat")
        public Double lat;
        @SerializedName("lng")
        public Double lng;
    }

    public class OpeningHour {
        @SerializedName("open_now")
        public Boolean open;
    }

    public class Photo {
        @SerializedName("height")
        public Integer height;
        @SerializedName("html_attributions")
        public List<String> attributions = new ArrayList<>();
        @SerializedName("photo_reference")
        public String photoReference;
        @SerializedName("width")
        public Integer width;
    }

    public class PlusCode {
        @SerializedName("compound_code")
        public String compoundCode;
        @SerializedName("global_code")
        public String globalCode;
    }

    public class Viewport {
        @SerializedName("northeast")
        public Location northeast;
        @SerializedName("southwest")
        public Location southwest;
    }
}
