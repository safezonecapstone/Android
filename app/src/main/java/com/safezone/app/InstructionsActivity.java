package com.safezone.app;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

import androidx.appcompat.app.AppCompatActivity;

public class InstructionsActivity extends AppCompatActivity {

    private static final String TAG = "Instruction";
    private ArrayList<Instructions> instruction=new ArrayList<>();
    private String startingAddress;
    private String endingAddress;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_instructions);
        getInstruction();
        populateView();
    }

    public void getInstruction()
    {
        Log.d(TAG, "Entering");
        Intent intent = getIntent();
        if(intent.hasExtra("BUNDLE")) {

            Log.d(TAG, "Entering 2");
            Bundle bundle=intent.getBundleExtra("BUNDLE");
            instruction = (ArrayList<Instructions>) bundle.getSerializable("Instruction"); //Gets the crime description
        }
        if(intent.hasExtra("Source")){
            startingAddress=intent.getStringExtra("Source");
        }
        if(intent.hasExtra("Destination")){
            endingAddress=intent.getStringExtra("Destination");
        }
    }

    private void populateView(){
        TextView from=(TextView)findViewById(R.id.source);
        from.setText(startingAddress);
        TextView to=(TextView)findViewById(R.id.destination);
        to.setText(endingAddress);
        ListView listView=(ListView)findViewById(R.id.listInstruction);
        InstructionsAdapter instructionsAdapter=new InstructionsAdapter(this, instruction);
        listView.setAdapter(instructionsAdapter);
    }
}
