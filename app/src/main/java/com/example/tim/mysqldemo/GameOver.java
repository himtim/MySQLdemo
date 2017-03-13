package com.example.tim.mysqldemo;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;

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

public class GameOver extends AsyncTask<String,Void,String> {
    Context context;
    AlertDialog.Builder builder;
    GameOver (Context ctx) {
        context = ctx;
    }
    @Override
    protected String doInBackground(String... params) {
        String rid = params[0];
        String gameover_url = params[1];
        try {
            URL url = new URL(gameover_url);
            HttpURLConnection httpURLConnection = (HttpURLConnection)url.openConnection();
            httpURLConnection.setRequestMethod("POST");
            httpURLConnection.setDoOutput(true);
            httpURLConnection.setDoInput(true);
            OutputStream outputStream = httpURLConnection.getOutputStream();
            BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));
            String post_data = URLEncoder.encode("rid","UTF-8")+"="+URLEncoder.encode(rid,"UTF-8");
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
        }return null;
    }

    @Override
    protected void onPreExecute() {
        builder= new AlertDialog.Builder(context);
        builder.setTitle("Game Finish");
    }

    @Override
    protected void onPostExecute(String result) {
        builder.setMessage(result);
        if(result.equals("Game Finished")) {
            builder.setPositiveButton("Back to Lobby", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog,
                                    int which) {
                    ((Game) context).finish();
                }
            });
        }else{
            builder.setNegativeButton("Back",null);
        }
        builder.show();
    }

    @Override
    protected void onProgressUpdate(Void... values) {
        super.onProgressUpdate(values);
    }

}
