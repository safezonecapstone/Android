package com.safezone.app;

import java.io.Serializable;

public class Instructions implements Serializable {

    String transit_Mode;
    String instruction;

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
}
