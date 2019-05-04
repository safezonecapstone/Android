package com.safezone.app;

public class Routes {

    private String startingAddress;
    private String endingAddress;
    private String rating;
    private String duration;

    Routes(String start, String end, String rate, String time){
        startingAddress=start;
        endingAddress=end;
        rating=rate;
        duration=time;
    }

    public String getDuration() {
        return duration;
    }

    public String getEndingAddress() {
        return endingAddress;
    }

    public String getRating() {
        return rating;
    }

    public String getStartingAddress() {
        return startingAddress;
    }
}
