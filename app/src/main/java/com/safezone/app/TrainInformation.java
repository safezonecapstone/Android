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
    private String street, risk, percentile;
    private ArrayList<String> train_stop= new ArrayList<>();
    private double longitude;
    private double latitude;
//    public TrainInformation ()
//    {
//
//    }

    public TrainInformation(String Street_name, String Train_Risk, String Percentile, double latitude, double Longitude)
    {
        this.street = Street_name;
        this.risk = Train_Risk;
        this.percentile = Percentile;
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

    public String getPercentile()
    {
        return percentile;
    }

    public void setPercentile(String Percentile)
    {
        this.percentile = Percentile;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }
}
