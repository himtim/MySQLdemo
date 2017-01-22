package com.example.tim.mysqldemo;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;

public class Register extends AppCompatActivity {
    EditText username, password, password2;
    String str_username, str_password, str_password2;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        username = (EditText)findViewById(R.id.etUserName);
        password = (EditText)findViewById(R.id.etPassword);
        password2 = (EditText)findViewById(R.id.etPassword2);


    }
    public void OnReg(View view){
        String str_username = username.getText().toString();
        String str_password = password.getText().toString();
        String str_password2 = password2.getText().toString();
        String type = "register";
        BackgroundWorker backgroundWorker = new BackgroundWorker(this);
        backgroundWorker.execute(type, str_username, str_password, str_password2);
    }
}