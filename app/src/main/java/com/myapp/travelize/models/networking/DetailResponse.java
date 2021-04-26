package com.myapp.travelize.models.networking;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class DetailResponse {
    public class Root {
        @SerializedName("html_attributions")
        public List<String> attributions = new ArrayList<>();
        @SerializedName("result")
        public Result result;
        @SerializedName("status")
        public String status;

        public List<String> getAttributions() {
            return attributions;
        }

        public Result getResult() {
            return result;
        }

        public String getStatus() {
            return status;
        }
    }

    public class Result {
        @SerializedName("formatted_phone_number")
        public String phoneNo;
        @SerializedName("opening_hours")
        public OpeningHour openingHour;
    }

    public class OpeningHour {
        @SerializedName("open_now")
        public Boolean open;
        @SerializedName("periods")
        @Expose(serialize = false, deserialize = false)
        public List<SubResult> periods = null;
        @SerializedName("weekday_text")
        public List<String> workTimings = new ArrayList<>();
    }

    public class SubResult {
    }
}
