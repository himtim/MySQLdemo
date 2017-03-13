package com.example.tim.mysqldemo;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;

public class Game extends AppCompatActivity {
    private TextView ScoreTextView;
    private TextView PointTextView;
    private TextView RoomTextView;
    EditText GeneratorIDEt;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        Button btn_dom = (Button)findViewById(R.id.btn_dom);
        Button btn_score = (Button)findViewById(R.id.btn_score);
        Button btn_PointStatus = (Button)findViewById(R.id.btn_PointStatus);
        ScoreTextView = (TextView)findViewById(R.id.ScoreTextView);
        PointTextView = (TextView)findViewById(R.id.PointTextView);
        RoomTextView = (TextView)findViewById(R.id.tvRoom);
        GeneratorIDEt = (EditText)findViewById(R.id.etGeneratorID);
        new SetRoomInfoTask().execute();
    }

    public void OnDominate(View view){
        String GeneratorID = GeneratorIDEt.getText().toString();
        SharedPreferences pref = getApplicationContext().getSharedPreferences("data", MODE_PRIVATE);
        String rid = pref.getString("rid", null);
        String uid = pref.getString("uid", null);
        new OnDominate(this).execute(rid,uid,GeneratorID);
    }
    public void OnShowScore(View view) throws IOException {
        SharedPreferences pref = getApplicationContext().getSharedPreferences("data", MODE_PRIVATE);
        String rid = pref.getString("rid", null);
        String score_url = "http://come2jp.com/dominion/showScore.php?rid="+rid;
        new GetScoreTask().execute(score_url);
    }

    public void OnShowPointStatus(View view) throws IOException {
        SharedPreferences pref = getApplicationContext().getSharedPreferences("data", MODE_PRIVATE);
        String rid = pref.getString("rid", null);
        String point_url = "http://come2jp.com/dominion/showPointStatus.php?rid="+rid;
        new GetPointTask().execute(point_url);
    }

    public void OnGameOver(View view) throws IOException {
        SharedPreferences pref = getApplicationContext().getSharedPreferences("data", MODE_PRIVATE);
        String rid = pref.getString("rid", null);
        String gameover_url = "http://come2jp.com/dominion/GameJudgement.php";
        new GameOver(this).execute(rid,gameover_url);
    }

    public class GetScoreTask extends AsyncTask<String,String,String>{
        @Override
        protected String doInBackground(String... params) {
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
            BufferedReader reader = null;
            HttpURLConnection connection = null;
            try {
                URL url = new URL(params[0]);
                connection = (HttpURLConnection)url.openConnection();
                connection.connect();
                InputStream stream = connection.getInputStream();
                reader = new BufferedReader(new InputStreamReader(stream));
                StringBuffer buffer = new StringBuffer();
                String line ="";
                while((line=reader.readLine()) != null){
                    buffer.append(line);
                }
                JSONObject parentObject = new JSONObject(buffer.toString());
                JSONArray parentArray = parentObject.getJSONArray("generators");
                StringBuffer finalBufferedData = new StringBuffer();
                for (int i=0;i<parentArray.length();i++){
                    JSONObject finalObject = parentArray.getJSONObject(i);
                    String uname = finalObject.getString("uname");
                    String GeneratorName = finalObject.getString("GeneratorName");
                    finalBufferedData.append(" Generator Name:  " + GeneratorName + " User Name: " + uname + "\n");
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
    public class SetRoomInfoTask extends AsyncTask<String,String,String>{

        @Override
        protected String doInBackground(String... params) {
            SharedPreferences pref = getApplicationContext().getSharedPreferences("data", MODE_PRIVATE);
            String rid = pref.getString("rid", null);
            return rid;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            RoomTextView.setText("Room Number: "+result);
        }
    }
    public class OnDominate extends AsyncTask<String,String,String>{

        AlertDialog.Builder builder;
        Context context;
        OnDominate(Context ctx) {
            context = ctx;
        }
        String dominate_url = "http://come2jp.com/dominion/dominate.php";
        @Override
        protected String doInBackground(String... params) {
                try {
                    builder.setTitle("Dominate Status");
                    String rid = params[0];
                    String uid = params[1];
                    String GeneratorID = params[2];
                    URL url = new URL(dominate_url);
                    HttpURLConnection httpURLConnection = (HttpURLConnection)url.openConnection();
                    httpURLConnection.setRequestMethod("POST");
                    httpURLConnection.setDoOutput(true);
                    httpURLConnection.setDoInput(true);
                    OutputStream outputStream = httpURLConnection.getOutputStream();
                    BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));
                    String post_data = URLEncoder.encode("rid","UTF-8")+"="+URLEncoder.encode(rid,"UTF-8")+"&"
                            +URLEncoder.encode("uid","UTF-8")+"="+URLEncoder.encode(uid,"UTF-8")+"&"
                            +URLEncoder.encode("GeneratorID","UTF-8")+"="+URLEncoder.encode(GeneratorID,"UTF-8");
                    bufferedWriter.write(post_data);
                    bufferedWriter.flush();
                    bufferedWriter.close();
                    outputStream.close();
                    InputStream inputStream = httpURLConnection.getInputStream();
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream,"iso-8859-1"));
                    String result="";
                    String line="";
                    while((line = bufferedReader.readLine())!= null) {
                        result += line;
                    }
                    bufferedReader.close();
                    inputStream.close();
                    httpURLConnection.disconnect();
                    return result;
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return null;
            }


        protected void onPreExecute() {
            builder= new AlertDialog.Builder(context);
            builder.setTitle("Dominate Status");
        }
        @Override
        protected void onPostExecute(String result) {
            builder.setMessage(result);
            builder.setNegativeButton("Back",null);
            builder.show();
        }
    }
}
