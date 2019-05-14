package com.safezone.app;

import java.io.Serializable;
import java.util.ArrayList;

public class Instructions implements Serializable {

    //Private variables
    private String transit_Mode;
    private String instruction;
    private String subway;
    private String destinationStop;

    //Constructor
    Instructions(String mode, String instruct){
        transit_Mode=mode;
        instruction=instruct;
    }

    //Getter functions
    public String getInstruction() {
        return instruction;
    }

    public String getTransit_Mode() {
        return transit_Mode;
    }

    public String getSubway() {
        return subway;
    }

    public String getDestinationStop() {
        return destinationStop;
    }

    //Mutator functions
    public void setSubway(String subway) {
        this.subway = subway;
    }

    public void setDestinationStop(String destinationStop) {
        this.destinationStop = destinationStop;
    }


}
