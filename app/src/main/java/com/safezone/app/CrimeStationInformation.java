package com.safezone.app;

import java.util.ArrayList;

// Class to handle the frequency and category
public class CrimeStationInformation {

    //Private Variables
    private String crime_category;
    private int frequency;
    private ArrayList<CrimeDescription> c_description=new ArrayList<>();

    //Constructor
    CrimeStationInformation(String Cat, int Frequency) {
        crime_category = Cat;
        frequency = Frequency;
    }

    //Getter function
    public String getCrime_category(){
        return crime_category;
    }

    public ArrayList<CrimeDescription> getC_description() {
        return c_description;
    }

    public int getFrequency() {return frequency;}

    //Mutator function
    public void addCrimeDescription(CrimeDescription a_crime)
    {
        c_description.add(a_crime);
    }

    //    public void setCategory(String Cat) {
//        crime_category = Cat;
//    }
//    public void setFrequency(int Frequency) { frequency = Frequency;}
//    //public String getDate() {return date;}
//    //public void setDate(String Date) {date = Date;}


}