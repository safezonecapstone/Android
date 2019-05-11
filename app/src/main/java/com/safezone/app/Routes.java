package com.safezone.app;

import java.util.ArrayList;

public class Routes {

    private String startingAddress;
    private String endingAddress;
    private String rating;
    private String duration;
    private ArrayList<Object> instructions=new ArrayList<>();
    //private ArrayList<String> subways=new ArrayList<>();

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

    public void addInstructions(Object instruct){
       instructions.add(instruct);
    }

    public ArrayList<Object> getInstructions() {
        return instructions;
    }

}
