package com.example.tim.mysqldemo;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.tim.mysqldemo.models.RoomModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

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
import java.util.ArrayList;
import java.util.List;

public class Room extends AppCompatActivity {
    private TextView ATeamTextView;
    private TextView BTeamTextView;
    private TextView RoomTextView;

    private RoomModel roomModel = new RoomModel();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_room);
        Button btn_start = (Button)findViewById(R.id.btnStartGame);
        Button btn_change = (Button)findViewById(R.id.btnChangeTeam);

        ATeamTextView = (TextView)findViewById(R.id.tvAteam);
        BTeamTextView = (TextView)findViewById(R.id.tvBteam);
        RoomTextView = (TextView)findViewById(R.id.tvRoom);
        new SetRoomNumberTask().execute();
        new SetRoomPlayerTask(this).execute();
        SharedPreferences pref = getApplicationContext().getSharedPreferences("data", MODE_PRIVATE);
        if(pref.getBoolean("rmAdmin", true)){
            btn_start.setVisibility(View.VISIBLE);
        }

    }
    public void OnStartGame(View view) {
        if(roomModel.getPlayerA().size() == roomModel.getPlayerB().size()){
            new ChangeStatus().execute();
            Intent intent = new Intent(Room.this, Game.class);
            startActivity(intent);
            (Room.this).finish();
        }
        else{
            AlertDialog.Builder builder;
            builder= new AlertDialog.Builder(Room.this);
            builder.setTitle("Start Game Fail");
            builder.setMessage("Players number not equal!");
            builder.setNegativeButton("Back",null);
            builder.show();
        }

    }
    public class ChangeStatus extends AsyncTask<String,Void,String> {
        SharedPreferences pref = getApplicationContext().getSharedPreferences("data", MODE_PRIVATE);
        String rid = pref.getString("rid", null);
        String lid = pref.getString("lid", null);
        @Override
        protected String doInBackground(String... params) {
            try {
                URL url = new URL("http://come2jp.com/dominion/changeStatus.php");
                HttpURLConnection httpURLConnection = (HttpURLConnection)url.openConnection();
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoOutput(true);
                httpURLConnection.setDoInput(true);
                OutputStream outputStream = httpURLConnection.getOutputStream();
                BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));
                String post_data = URLEncoder.encode("rid","UTF-8")+"="+URLEncoder.encode(rid,"UTF-8")+"&"
                        +URLEncoder.encode("lid","UTF-8")+"="+URLEncoder.encode(lid,"UTF-8");
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
        protected void onPostExecute(String result) {
        }
        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
        }

    }
    public void OnChangeTeam(View view){
        SharedPreferences pref = getApplicationContext().getSharedPreferences("data", MODE_PRIVATE);
        String rid = pref.getString("rid", null);
        String uid = pref.getString("uid", null);
        BufferedReader reader = null;
        HttpURLConnection connection = null;
        try {
            URL url = new URL("http://come2jp.com/dominion/changeTeam.php?rid="+rid+"&uid="+uid);
            connection = (HttpURLConnection) url.openConnection();
            connection.connect();
            InputStream stream = connection.getInputStream();
            reader = new BufferedReader(new InputStreamReader(stream));
            StringBuffer buffer = new StringBuffer();
            String line = "";
            while ((line = reader.readLine()) != null) {
                buffer.append(line);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
            try {
                if (reader != null) {
                    reader.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        new SetRoomPlayerTask(this).execute();
        ATeamTextView.setText("A Team member: "+roomModel.getPlayerA());
        BTeamTextView.setText("B Team member: "+roomModel.getPlayerB());
    }
    public class SetRoomNumberTask extends AsyncTask<String,String,String> {
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
    public class SetRoomPlayerTask extends AsyncTask<String,String,RoomModel> {
        Context context;
        SetRoomPlayerTask(Context ctx) {
            context = ctx;
        }
        @Override
        protected RoomModel doInBackground(String... params) {
            SharedPreferences pref = getApplicationContext().getSharedPreferences("data", MODE_PRIVATE);
            String rid = pref.getString("rid", null);
            String showPlayer_url = "http://come2jp.com/dominion/showRoomPlayer.php?rid=" + rid;
            try {
                URL url = new URL(showPlayer_url);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.connect();
                InputStream stream = connection.getInputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
                StringBuffer buffer = new StringBuffer();
                String line = "";
                while ((line = reader.readLine()) != null) {
                    buffer.append(line);
                }
                RoomModel roomModel2 = new RoomModel();
                List<String> playerListA = new ArrayList<String>();
                List<String> playerListB = new ArrayList<String>();
                JSONArray parentArray = new JSONArray(buffer.toString());
                for (int i = 0; i < parentArray.length(); i++) {
                    JSONObject playerObject = parentArray.getJSONObject(i);
                    if (playerObject.getString("team").equals("A")) {
                        playerListA.add(playerObject.getString("uname"));
                    } else if (playerObject.getString("team").equals("B")) {
                        playerListB.add(playerObject.getString("uname"));
                    }
                }
                roomModel2.setPlayerA(playerListA);
                roomModel2.setPlayerB(playerListB);
                return roomModel2;
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }
            @Override
            protected void onPostExecute(RoomModel result) {
                super.onPostExecute(result);
                ATeamTextView.setText("A Team member: "+result.getPlayerA());
                BTeamTextView.setText("B Team member: "+result.getPlayerB());
                roomModel = result;
            }
    }
    public void onQuitRoom(View view) {
        SharedPreferences pref = getApplicationContext().getSharedPreferences("data", MODE_PRIVATE);
        String rid = pref.getString("rid", null);
        String uid = pref.getString("uid", null);
        new QuitRoomTask(this).execute(rid,uid);
    }
    public class QuitRoomTask extends AsyncTask<String,Void,String> {
        Context context;
        AlertDialog.Builder builder;
        QuitRoomTask (Context ctx) {
            context = ctx;
        }
        @Override
        protected String doInBackground(String... params) {
            String quitroom_url = "http://come2jp.com/dominion/quitGameRoom.php";
            try {
                String rid = params[0];
                String uid = params[1];
                URL url = new URL(quitroom_url);
                HttpURLConnection httpURLConnection = (HttpURLConnection)url.openConnection();
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoOutput(true);
                httpURLConnection.setDoInput(true);
                OutputStream outputStream = httpURLConnection.getOutputStream();
                BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));
                String post_data = URLEncoder.encode("rid","UTF-8")+"="+URLEncoder.encode(rid,"UTF-8") + "&"
                        + URLEncoder.encode("uid", "UTF-8") + "=" + URLEncoder.encode(uid, "UTF-8");;
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
            builder.setTitle("Quit Room");
        }

        @Override
        protected void onPostExecute(String result) {
            builder.setMessage(result);
            if(result.equals("success")) {
                builder.setPositiveButton("Back to Lobby", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,
                                        int which) {
                        ((Room) context).finish();
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

}
