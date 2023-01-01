package com.example.chessfirebase;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.InputType;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class RoomBrowse extends AppCompatActivity {

    private ListView lv;
    private Button btCreateRoom;
    private List<String> roomList;
    private String name;
    private String roomName;

    FirebaseDatabase db;
    DatabaseReference roomRef;
    DatabaseReference player;
    FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_room_browse);

        mAuth=FirebaseAuth.getInstance();
        db=FirebaseDatabase.getInstance();
        player=db.getReference(mAuth.getUid());
        name=mAuth.getCurrentUser().getDisplayName();
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        lv=findViewById(R.id.lvRoomView);
        btCreateRoom=findViewById(R.id.btCreateRoom);

        roomList=new ArrayList<>();
        btCreateRoom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder bldr=new AlertDialog.Builder(RoomBrowse.this);
                bldr.setTitle("Enter room Name:");
                EditText et=new EditText(RoomBrowse.this);
                et.setInputType(InputType.TYPE_CLASS_TEXT);
                bldr.setView(et);
                bldr.setPositiveButton("Create Room", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        roomName=et.getText().toString();
                        btCreateRoom.setEnabled(false);
                    }
                });
                bldr.setNegativeButton("Cancel" ,new DialogInterface.OnClickListener(){
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                        btCreateRoom.setEnabled(true);
                    }
                });
                bldr.show();
                roomRef=db.getReference("rooms/"+roomName+"/white");
                readFromDB();
                roomRef.setValue(mAuth.getCurrentUser().getDisplayName()+":"+sharedPreferences.getInt("Elo",-100));
            }
        });
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                roomName=roomList.get(position);
                roomRef=db.getReference("rooms/"+roomName+"/black");
                readFromDB();
                roomRef.setValue(mAuth.getCurrentUser().getDisplayName()+":"+sharedPreferences.getInt("Elo",-100));
                //possible to add random color pick for the room creation
                /*roomRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(snapshot.hasChild("/white")){
                            roomRef=db.getReference("rooms/"+roomName+"/black");
                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });*/
            }
        });
        updateRoomList();
    }


    public void readFromDB(){//constant;y read from db to update the list
        roomRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {//occurs when another player joins the room
                //
                //start an activity to roomBrowse
                startActivity(new Intent(getApplicationContext(),GameAvtivity.class).putExtra("roomName",roomName));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                //error
                //TODO: try and handle the error (think about how to handle)
                Toast.makeText(getApplicationContext(),"error",Toast.LENGTH_LONG).show();
                btCreateRoom.setEnabled(true);
            }
        });
    }
    public void updateRoomList(){
        roomRef=db.getReference("rooms");
        roomRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                roomList.clear();
                Iterable<DataSnapshot> it=snapshot.getChildren();
                for(DataSnapshot sn:it){
                    roomList.add(sn.getKey());
                    ArrayAdapter<String> adapter=new ArrayAdapter<>(RoomBrowse.this, android.R.layout.simple_list_item_1);
                    lv.setAdapter(adapter);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

}