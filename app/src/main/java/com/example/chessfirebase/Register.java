package com.example.chessfirebase;

import static java.util.Objects.*;

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
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.UserProfileChangeRequest;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.Objects;

public class Register extends AppCompatActivity {
    public EditText etEmail;
    public EditText etPass;
    public EditText etName;
    private Button regBt;
    private FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        regBt=findViewById(R.id.btRegister);
        mAuth=FirebaseAuth.getInstance();
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

        SharedPreferences.Editor editor = sharedPreferences.edit();

        regBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                etEmail = (EditText) findViewById(R.id.etEmail);
                String mail=etEmail.getText().toString();
                etPass=(EditText)findViewById(R.id.etPass);
                String pass=etPass.getText().toString();
                etName=(EditText)findViewById(R.id.etName);
                String name=etName.getText().toString();
                mAuth.createUserWithEmailAndPassword(mail, pass)
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    UserProfileChangeRequest pu=new UserProfileChangeRequest.Builder().setDisplayName(name).build();
                                    FirebaseAuth.getInstance().getCurrentUser().updateProfile(pu).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            editor.putString("mail",mail);
                                            editor.putString("pass",pass);
                                            editor.putInt("elo",800);
                                            editor.commit();
                                            //task is succesfull
                                            start();
                                        }
                                    });
                                } else {
                                    Toast.makeText(Register.this,"failed to create account",Toast.LENGTH_LONG);
                                }
                            }
                        });
            }
        });
    }

    public void login(View v){
        Intent i=new Intent(Register.this,Login.class);
        startActivity(i);
        this.finish();
    }
    public void start(){
        Intent t=new Intent(Register.this,MainActivity.class);
        startActivity(t);
        this.finish();
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
    private String readFromFile(Context context,String fileName) {

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
}