//******Yi Tong*****//
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
            Bundle bundle=intent.getBundleExtra("BUNDLE");
            instruction = (ArrayList<Instructions>) bundle.getSerializable("Instruction"); //Gets the instructions for the route
        }
        if(intent.hasExtra("Source")){
            startingAddress=intent.getStringExtra("Source");
        }
        if(intent.hasExtra("Destination")){
            endingAddress=intent.getStringExtra("Destination");
        }
    }

    //Display the view
    private void populateView(){

        //Set starting address
        TextView from=(TextView)findViewById(R.id.source);
        from.setText(startingAddress);

        //Set destination address
        TextView to=(TextView)findViewById(R.id.destination);
        to.setText(endingAddress);

        //Display the list of steps to get from starting address to destination
        ListView listView=(ListView)findViewById(R.id.listInstruction);
        InstructionsAdapter instructionsAdapter=new InstructionsAdapter(this, instruction);
        listView.setAdapter(instructionsAdapter);
    }
}
