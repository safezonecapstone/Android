package com.safezone.app;

import java.io.Serializable;

public class CrimeDescription implements Serializable {
    //Private Variables
    private String description_;
    private String date_;

    //Constructor
    CrimeDescription (String Description, String Date) {
        description_ = Description;
        date_ = Date;
    }

    //Getter functions to retrieve private variables
    public String getDate_() {
        return date_;
    }

    public String getDescription_() {
        return description_;
    }
}
