package com.example.chessfirebase;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

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
    FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_room_browse);

        mAuth=FirebaseAuth.getInstance();
        db=FirebaseDatabase.getInstance();

        name=mAuth.getCurrentUser().getDisplayName();

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
                    }
                });
                bldr.setNegativeButton("Cancel" ,new DialogInterface.OnClickListener(){
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                bldr.show();
                roomRef=db.getReference("rooms/"+roomName+"/white");
            }
        });
    }
}