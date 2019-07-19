

package com.example.hereiammccauleyj;

import com.google.gson.annotations.SerializedName;

public class TimeZoneData {

    private int dstOffset;
    private int rawOffset;
    private String timeZoneId;
    private String timeZoneName;

    @SerializedName("body")
    private String text;

    public int getDstOffset() {
        return dstOffset;
    }

    public int getRawOffset() {
        return rawOffset;
    }

    public String getTimeZoneId() {
        return timeZoneId;
    }

    public String getTimeZoneName() {
        return timeZoneName;
    }

    public String getText() {
        return text;
    }
}
