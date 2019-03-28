package com.safezone.app;

import java.util.ArrayList;

// Class to handle the date, frequency and category
public class CrimeStationInformation {
    private String crime_category;
    private int frequency;
    private ArrayList<CrimeDescription> c_description=new ArrayList<>();

    public CrimeStationInformation() {
    }

    public CrimeStationInformation(String Cat, int Frequency) {
        crime_category = Cat;
        frequency = Frequency;
    }


    public String getCrime_category(){
        return crime_category;
    }

    public void setCategory(String Cat) {
        crime_category = Cat;
    }

    public int getFrequency() {return frequency;}

    public void setFrequency(int Frequency) { frequency = Frequency;}

    //public String getDate() {return date;}

    //public void setDate(String Date) {date = Date;}

    public void addCrimeDescription(CrimeDescription a_crime)
    {
        c_description.add(a_crime);
    }

    public ArrayList<CrimeDescription> getC_description() {
        return c_description;
    }
}