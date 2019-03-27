package com.safezone.app;

public class CrimeDescription {
    String description_;
    String date_;

    public CrimeDescription (String Description, String Date) {

        description_ = Description;
        date_ = Date;
    }


    public String getDate_() {
        return date_;
    }

    public void setDate_(String date_) {
        this.date_ = date_;
    }

    public String getDescription_() {
        return description_;
    }
}
