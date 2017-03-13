package com.example.tim.mysqldemo;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;

public class MainActivity extends AppCompatActivity {
    EditText UserNameEt, PasswordEt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        SharedPreferences pref = getApplicationContext().getSharedPreferences("data", MODE_PRIVATE);
        String uid = pref.getString("uid", null);
        if(!TextUtils.isEmpty(uid)){
            startActivity(new Intent(MainActivity.this, Lobby.class));
        }
        UserNameEt = (EditText)findViewById(R.id.etUserName);
        PasswordEt = (EditText)findViewById(R.id.etPassword);
    }

    public void OnLogin(View view){
        String username = UserNameEt.getText().toString();
        String password = PasswordEt.getText().toString();
        String type = "login";
        AlertDialog.Builder builder;
        builder= new AlertDialog.Builder(MainActivity.this);
        if(!TextUtils.isEmpty(username)&&!TextUtils.isEmpty(password)) {
            BackgroundWorker backgroundWorker = new BackgroundWorker(this);
            backgroundWorker.execute(type, username, password);
        }
        else{
            builder.setTitle("Login Fail");
            builder.setMessage("Please fill in all information!");
            builder.setNegativeButton("Back",null);
            builder.show();
        }
    }

    public void OpenReg(View view){
        startActivity(new Intent(this, Register.class));
    }
    //open game room onclick button
    // /public void OpenGameRoom(View view){
    //    startActivity(new Intent(this, Game.class));
    //}
}

