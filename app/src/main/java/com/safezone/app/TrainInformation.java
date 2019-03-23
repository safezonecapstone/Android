package com.safezone.app;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Vector;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class TrainInformation
{
    private String street, risk, crime_rate;
    private ArrayList<String> train_stop= new ArrayList<>();
    private double longitude;
    private double latitude;
//    public TrainInformation ()
//    {
//
//    }

    public TrainInformation(String Street_name, String Train_Risk, String Train_Crime_Rate, double latitude, double Longitude)
    {
        this.street = Street_name;
        this.risk = Train_Risk;
        this.crime_rate = Train_Crime_Rate;
        this.latitude=latitude;
        this.longitude=Longitude;
    }

    public String getName()
    {
        return street;
    }

    public void setName(String Name)
    {
        this.street = Name;
    }

    public ArrayList<String> getTrainStop()
    {
        return train_stop;
    }

    public void addTrainStop(String TrainStop)
    {
        this.train_stop.add(TrainStop);
    }

    public String getRisk()
    {
        return risk;
    }

    public void setRisk(String Risk)
    {
        this.risk = Risk;
    }

    public String getCrimeRate()
    {
        return crime_rate;
    }

    public void setCrime_rate(String CrimeRate)
    {
        this.crime_rate = CrimeRate;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }
}
