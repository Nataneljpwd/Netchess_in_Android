package com.example.chessfirebase;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

public class MainActivity extends AppCompatActivity {
    private TextView tvName;
    private TextView tvElo;
    private FirebaseAuth mAuth;
    private Button btRooms;
    private FirebaseDatabase database;
    private DatabaseReference player;
    @Override
    protected void onCreate(Bundle savedInstanceState) {//TODO: add all the buttons
        //maybe delete all the write and read from and to file
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        tvName=findViewById(R.id.tvName);
        tvElo=findViewById(R.id.tvElo);
        btRooms=findViewById(R.id.btBrowseRooms);
        String name=FirebaseAuth.getInstance().getCurrentUser().getDisplayName();
        tvName.setText(name);
        tvElo.setText(sharedPreferences.getInt("Elo",800)+"");
        mAuth = FirebaseAuth.getInstance();
        database=FirebaseDatabase.getInstance();
        player= database.getReference("players/"+name);
        player.setValue(name+":"+sharedPreferences.getInt("Elo",800));//set the value by default to the name and rating so that we read
        //it at the start and know what to display
        btRooms.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this,RoomBrowse.class));
                readFromDB();
                MainActivity.this.finish();
            }
        });
        //readFromDB();
    }
    private String readFromFile(Context context, String fileName) {

        String ret = "";

        try {
            InputStream inputStream = context.openFileInput(fileName+".txt");

            if ( inputStream != null ) {
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                String receiveString = "";
                StringBuilder stringBuilder = new StringBuilder();

                while ( (receiveString = bufferedReader.readLine()) != null ) {
                    stringBuilder.append("\n").append(receiveString);
                }

                inputStream.close();
                ret = stringBuilder.toString();
            }
        }
        catch (FileNotFoundException e) {
            Log.e("login activity", "File not found: " + e.toString());
        } catch (IOException e) {
            Log.e("login activity", "Can not read file: " + e.toString());
        }

        return ret;
    }

    private void writeToFile(String data, Context context, String fileName) {
        try {
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(context.openFileOutput(fileName+".txt", Context.MODE_PRIVATE));
            outputStreamWriter.write(data);
            outputStreamWriter.close();
        }
        catch (IOException e) {
            Log.e("Exception", "File write failed: " + e.toString());
        }
    }
    public void signOut(View v){
        FirebaseAuth.getInstance().signOut();
        startActivity(new Intent(MainActivity.this,Login.class));
        this.finish();
    }

    public void readFromDB(){
        player.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {//occurs when another player joins the room
                //start an activity to roomBrowse
                startActivity(new Intent(MainActivity.this,RoomBrowse.class));
                MainActivity.this.finish();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                //error
                //TODO: try and handle the error (think about how to handle)
            }
        });
    }
}