package com.example.tim.mysqldemo;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.view.View;

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

import android.support.v7.app.AppCompatActivity;

import static android.content.Context.MODE_PRIVATE;

public class BackgroundWorker extends AsyncTask<String,Void,String> {
    Context context;
    AlertDialog.Builder builder;
    ConnectionHandler conHan;
    BackgroundWorker (Context ctx) {
        context = ctx;
    }

    @Override
    protected String doInBackground(String...params) {
        String type = params[0];
        String login_url = "http://come2jp.com/dominion/login.php";
        String register_url = "http://come2jp.com/dominion/register.php";
        String dominate_url = "http://come2jp.com/dominion/dominate.php";
        if(type.equals("login")) {
            try {
                String user_name = params[1];
                String password = params[2];
                URL url = new URL(login_url);
                conHan = new ConnectionHandler(url);
                conHan.useSession(context);
                String post_data = URLEncoder.encode("user_name","UTF-8")+"="+URLEncoder.encode(user_name,"UTF-8")+"&"
                        +URLEncoder.encode("password","UTF-8")+"="+URLEncoder.encode(password,"UTF-8");
                conHan.post(post_data);
                String result = conHan.get();
                return result;
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else if(type.equals("register")){
            try {
                String username = params[1];
                String password = params[2];
                String password2 = params[3];
                URL url = new URL(register_url);
                conHan = new ConnectionHandler(url);
                conHan.useSession(context);
                String post_data = URLEncoder.encode("username","UTF-8")+"="+URLEncoder.encode(username,"UTF-8")+"&"
                        +URLEncoder.encode("password","UTF-8")+"="+URLEncoder.encode(password,"UTF-8")+"&"
                        +URLEncoder.encode("password2","UTF-8")+"="+URLEncoder.encode(password2,"UTF-8");
                conHan.post(post_data);
                String result = conHan.get();
                return result;
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    @Override
    protected void onPreExecute() {
        builder= new AlertDialog.Builder(context);
        builder.setTitle("Login Status");
    }

    @Override
    protected void onPostExecute(String result) {
        if(result.equals("fail")) {
            builder.setMessage(result);
            builder.setNegativeButton("Back",null);
        }else{
            builder.setMessage("Success");
            builder.setPositiveButton("Enter Lobby ", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog,
                                    int which) {
                    context.startActivity(new Intent(context, Lobby.class));
                }
            });
        }
        builder.show();
    }

    @Override
    protected void onProgressUpdate(Void... values) {
        super.onProgressUpdate(values);
    }

}