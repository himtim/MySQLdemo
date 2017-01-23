package com.example.tim.mysqldemo;

import android.os.Bundle;
import android.os.StrictMode;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class Game extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        TextView ScoreTextView = (TextView)findViewById(R.id.ScoreTextView);
    }
    public void OnDominate(View view){
        String gid = "1";
        String uid = "1";
        String point = "4";
        String type = "dominate";
        BackgroundWorker backgroundWorker = new BackgroundWorker(this);
        backgroundWorker.execute(type, gid, uid, point);
    }
    public void OnShowScore(View view){
        String rid = "1";
        String score_url = "http://come2jp.com/dominion/showScore.php?rid="+rid;
        InputStream is = null;
        String line = null;
        String result = null;
        String[] score;
        TextView ScoreTextView = (TextView)findViewById(R.id.ScoreTextView);
        JSONObject json
            try {
                URL url = new URL(score_url);
                HttpURLConnection httpURLConnection = (HttpURLConnection)url.openConnection();
                JSONObject json = jParser.makeHttpRequest(score_url, "GET", );

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            try{
                json = jParser.makeHttpRequest(score_url, "POST", String);
            }catch (Exception e){
                e.printStackTrace();
            }

        try {
            String A_score = json.getString("A_score");
            ScoreTextView.setText(A_score);
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}
