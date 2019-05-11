package com.safezone.app;

import java.io.Serializable;
import java.util.ArrayList;

public class Instructions implements Serializable {

    private String transit_Mode;
    private String instruction;
    private String subway;
    private String destinationStop;

    Instructions(String mode, String instruct){
        transit_Mode=mode;
        instruction=instruct;
    }

    public String getInstruction() {
        return instruction;
    }

    public String getTransit_Mode() {
        return transit_Mode;
    }

    public void setSubway(String subway) {
        this.subway = subway;
    }

    public String getSubway() {
        return subway;
    }

    public void setDestinationStop(String destinationStop) {
        this.destinationStop = destinationStop;
    }

    public String getDestinationStop() {
        return destinationStop;
    }
}
