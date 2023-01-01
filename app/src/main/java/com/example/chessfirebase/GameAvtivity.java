package com.example.chessfirebase;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;

import com.example.chessfirebase.chessClasses.BoardCell;
import com.example.chessfirebase.chessClasses.Piece;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class GameAvtivity extends AppCompatActivity {
    private static final int REQUEST_READ_PHONE_STATE = 1;
    String roomName;
    private FirebaseAuth mAuth;
    private FirebaseDatabase database;
    private boolean firebaseConn = false;
    private DatabaseReference player;
    FrameLayout frmview;
    customGameView cgv;
    socketPlayer p;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_avtivity);
        StrictMode.ThreadPolicy gfgPolicy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(gfgPolicy);
        Intent i=getIntent();
        mAuth=FirebaseAuth.getInstance();
        frmview = findViewById(R.id.cgv);

            cgv = new customGameView(GameAvtivity.this, frmview.getWidth(), frmview.getHeight());
            frmview.addView(cgv);
            Piece.size=1080.0f/8.0f;
            BoardCell.size=Piece.size;
        if(i.getBooleanExtra("FireBase",false)) {
            firebaseConn = true;
            roomName = i.getExtras().getString("roomName");
            //create a firebasePlayer
        }else{
            p=new socketPlayer();
            Thread th=new Thread(p);
            th.start();

            try {
                Thread.sleep(250);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            cgv.start(p.getBoard());
        }


    }
    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if ( cgv == null && hasFocus) {
            cgv = new customGameView(GameAvtivity.this, frmview.getWidth(), frmview.getHeight());
            frmview.addView(cgv);
            cgv.start(p.getBoard());
        }
    }
}