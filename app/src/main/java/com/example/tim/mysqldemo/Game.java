package com.example.tim.mysqldemo;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
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
    private TextView ScoreTextView;
    private TextView PointTextView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        Button btn_dom = (Button)findViewById(R.id.btn_dom);
        Button btn_score = (Button)findViewById(R.id.btn_score);
        Button btn_PointStatus = (Button)findViewById(R.id.btn_PointStatus);
        ScoreTextView = (TextView)findViewById(R.id.ScoreTextView);
        PointTextView = (TextView)findViewById(R.id.PointTextView);
    }
    public void OnDominate(View view){
        String rid = "1";
        String uid = "1";
        String point = "4";
        String type = "dominate";
        BackgroundWorker backgroundWorker = new BackgroundWorker(this);
        backgroundWorker.execute(type, rid, uid, point);
    }
    public void OnShowScore(View view) throws IOException {
        String rid = "1";
        String score_url = "http://come2jp.com/dominion/showScore.php?rid="+rid;
        new GetScoreTask().execute(score_url);
    }

    public void OnShowPointStatus(View view) throws IOException {
        String rid = "1";
        String point_url = "http://come2jp.com/dominion/showPointStatus.php?rid="+rid;
        new GetPointTask().execute(point_url);
    }

    public class GetScoreTask extends AsyncTask<String,String,String>{

        @Override
        protected String doInBackground(String... params) {
            String result = null;
            BufferedReader reader = null;
            HttpURLConnection connection = null;
            try {
                URL url = new URL(params[0]);
                connection = (HttpURLConnection)url.openConnection();
                connection.connect();

                InputStream stream = connection.getInputStream();
                reader = new BufferedReader(new InputStreamReader(stream));

                StringBuilder sb = new StringBuilder();
                StringBuffer buffer = new StringBuffer();

                String line ="";
                while((line=reader.readLine()) != null){
                    buffer.append(line);
                }

                JSONObject parentObject = new JSONObject(buffer.toString());
                //JSONArray parentArray = parentObject.getJSONArray("scores");

                //JSONObject finalObject = parentArray.getJSONObject(0);

                String A_Score = parentObject.getString("A_score");
                String B_Score = parentObject.getString("B_score");

                return "A score: " + A_Score + " B score: " + B_Score;
            }catch (Exception e){
                e.printStackTrace();
            } finally {
                if (connection != null){
                    connection.disconnect();
                }
                try{
                    if(reader != null){
                        reader.close();
                    }
                }catch (IOException e){
                    e.printStackTrace();
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            ScoreTextView.setText(result);
        }
    }
    public class GetPointTask extends AsyncTask<String,String,String>{

        @Override
        protected String doInBackground(String... params) {
            String result = null;
            BufferedReader reader = null;
            HttpURLConnection connection = null;
            try {
                URL url = new URL(params[0]);
                connection = (HttpURLConnection)url.openConnection();
                connection.connect();

                InputStream stream = connection.getInputStream();
                reader = new BufferedReader(new InputStreamReader(stream));

                StringBuilder sb = new StringBuilder();
                StringBuffer buffer = new StringBuffer();

                String line ="";
                while((line=reader.readLine()) != null){
                    buffer.append(line);
                }

                JSONObject parentObject = new JSONObject(buffer.toString());
                JSONArray parentArray = parentObject.getJSONArray("points");

                StringBuffer finalBufferedData = new StringBuffer();
                for (int i=0;i<parentArray.length();i++){
                    JSONObject finalObject = parentArray.getJSONObject(i);
                    String uname = finalObject.getString("uname");
                    String pointID = finalObject.getString("pointID");
                    String pointName = finalObject.getString("pointName");

                    finalBufferedData.append("uname: " + uname + " pointID: " + pointID + " pointName:  " + pointName + "\n");
                }
                return finalBufferedData.toString();
            }catch (Exception e){
                e.printStackTrace();
            } finally {
                if (connection != null){
                    connection.disconnect();
                }
                try{
                    if(reader != null){
                        reader.close();
                    }
                }catch (IOException e){
                    e.printStackTrace();
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            PointTextView.setText(result);
        }
    }
}
