package com.example.tim.mysqldemo;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.tim.mysqldemo.models.RoomModel;
import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.appindexing.Thing;
import com.google.android.gms.common.api.GoogleApiClient;

import org.json.JSONArray;
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

public class Lobby extends AppCompatActivity {
    private ListView lvRooms;
    private TextView tvIntro;
    String LobbyID = "1";
    private int mInterval = 5000; // 5 seconds by default, can be changed later
    private Handler mHandler;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lobby);
        lvRooms = (ListView)findViewById(R.id.lvRooms);
        Button btn_logout = (Button)findViewById(R.id.btn_logout);
        String LobbyID = "1";
        String getroom_url = "http://come2jp.com/dominion/showGameRoom.php?LobbyID=" + LobbyID;
        new GetGameRoomTask(this).execute(getroom_url);

        mHandler = new Handler();
        startRepeatingTask();
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        stopRepeatingTask();
    }

    Runnable mStatusChecker = new Runnable() {
        @Override
        public void run() {
            try {
                String getroom_url = "http://come2jp.com/dominion/showGameRoom.php?LobbyID=" + LobbyID;
                new GetGameRoomTask(getApplicationContext()).execute(getroom_url); //this function can change value of mInterval.
            } finally {
                // 100% guarantee that this always happens, even if,   your update method throws an exception
                mHandler.postDelayed(mStatusChecker, mInterval);
            }
        }
    };

    void startRepeatingTask() {
        mStatusChecker.run();
    }

    void stopRepeatingTask() {
        mHandler.removeCallbacks(mStatusChecker);
    }

    public void setLobby(){
        SharedPreferences pref = this.getSharedPreferences("data", MODE_PRIVATE);
        SharedPreferences.Editor editor=pref.edit();
        editor.putString("lid", "1");
        String lid = pref.getString("lid", null);
        tvIntro = (TextView)findViewById(R.id.tvIntro);
        tvIntro.setText("lobby id: "+lid);
    }

    public void OnLogout(View view){
        SharedPreferences pref = this.getSharedPreferences("data", MODE_PRIVATE);
        SharedPreferences.Editor editor=pref.edit();
        editor.remove("SessionID").commit();
        (Lobby.this).finish();
    }

    public class GetGameRoomTask extends AsyncTask<String, String, List<RoomModel>> {
        Context context;
        GetGameRoomTask(Context ctx) {
            context = ctx;
        }
        @Override
        protected List<RoomModel> doInBackground(String... params) {
            BufferedReader reader = null;
            HttpURLConnection connection = null;
            try {
                URL url = new URL(params[0]);
                connection = (HttpURLConnection) url.openConnection();
                connection.connect();
                InputStream stream = connection.getInputStream();
                reader = new BufferedReader(new InputStreamReader(stream));
                StringBuffer buffer = new StringBuffer();
                String line = "";
                while ((line = reader.readLine()) != null) {
                    buffer.append(line);
                }
                JSONArray parentArray = new JSONArray(buffer.toString());

                List<RoomModel> roomModelList = new ArrayList<RoomModel>();

                for (int i = 0; i < parentArray.length(); i++) {

                    JSONObject finalObject = parentArray.getJSONObject(i);
                    RoomModel roomModel = new RoomModel();
                    roomModel.setRid(finalObject.getString("rid"));
                    List<String> playerListA = new ArrayList<String>();
                    List<String> playerListB = new ArrayList<String>();
                    for(int j=0;j<6;j++) {
                        if (finalObject.has(Integer.toString(j))) {
                            JSONObject playerObject = finalObject.getJSONObject(Integer.toString(j));
                            if(playerObject.getString("team").equals("A")) {
                                playerListA.add(playerObject.getString("uname"));
                            }else if(playerObject.getString("team").equals("B")) {
                                playerListB.add(playerObject.getString("uname"));
                            }
                        }
                    }
                    roomModel.setPlayerA(playerListA);
                    roomModel.setPlayerB(playerListB);

                    //adding the final object in the list
                    roomModelList.add(roomModel);
                }
                return roomModelList;

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
            return null;
    }

        @Override
        protected void onPostExecute(List<RoomModel> result) {
            super.onPostExecute(result);
            RoomAdapter adapter = new RoomAdapter(getApplicationContext(), R.layout.row, result);
            lvRooms.setAdapter(adapter);
        }
    }
    public class RoomAdapter extends ArrayAdapter{

        private List<RoomModel> roomModelList;
        private int resource;
        private LayoutInflater inflater;

        public RoomAdapter(Context context, int resource, List<RoomModel> objects) {
            super(context, resource, objects);
            roomModelList = objects;
            this.resource = resource;
            inflater = (LayoutInflater)getSystemService(LAYOUT_INFLATER_SERVICE);
        }

        @NonNull
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if(convertView == null){
                convertView = inflater.inflate(resource, null);
            }
            ImageView ivIcon;
            TextView tvTeamA;
            TextView tvTeamB;

            ivIcon = (ImageView)convertView.findViewById(R.id.ivIcon);
            tvTeamA = (TextView)convertView.findViewById(R.id.tvTeamA);
            tvTeamB = (TextView)convertView.findViewById(R.id.tvTeamB);
            final Button btn_join = (Button) convertView.findViewById(R.id.btn_join);

            tvTeamA.setText("A Team: "+roomModelList.get(position).getPlayerA());
            tvTeamB.setText("B Team: "+roomModelList.get(position).getPlayerB());

            btn_join.setTag(position);
            // Attach the click event handler
            btn_join.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int position = (Integer) view.getTag();
                    // Access the row position here to get the correct data item
                    String rid = roomModelList.get(position).getRid();
                    SharedPreferences pref = getApplicationContext().getSharedPreferences("data", MODE_PRIVATE);
                    String uid = pref.getString("uid", null);
                    new JoinRoomTask(Lobby.this).execute(rid, uid);
                }
            });

            return convertView;

        }
    }

    public class CreateRoomTask extends AsyncTask<String, Void, String> {
        Context context;
        AlertDialog.Builder builder;

        CreateRoomTask(Context ctx) {
            context = ctx;
        }

        @Override
        protected String doInBackground(String... params) {
            String lobbyid = params[0];
            String createroom_url = params[1];
            try {
                URL url = new URL(createroom_url);
                ConnectionHandler conHan = new ConnectionHandler(url);
                conHan.useSession(context);
                String post_data = URLEncoder.encode("LobbyID", "UTF-8") + "=" + URLEncoder.encode(lobbyid, "UTF-8");
                conHan.post(post_data);
                String result = conHan.get();
                return result;
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPreExecute() {
            builder = new AlertDialog.Builder(context);
            builder.setTitle("Create Game Room");
        }

        @Override
        protected void onPostExecute(String result) {
            if (result.equals("fail")) {
                builder.setMessage(result);
                builder.setNegativeButton("Back", null);
            } else {
                builder.setMessage("success");
                builder.setPositiveButton("Enter Game Room", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,
                                        int which) {
                        context.startActivity(new Intent(context, Room.class));
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

    public void OnCreateGameRoom(View view) throws IOException {
        String lobbyid = "1";
        String create_url = "http://come2jp.com/dominion/createGameRoom.php";
        new CreateRoomTask(this).execute(lobbyid, create_url);
    }

    public class JoinRoomTask extends AsyncTask<String, Void, String> {
        Context context;
        AlertDialog.Builder builder;

        JoinRoomTask(Context ctx) {
            context = ctx;
        }

        @Override
        protected String doInBackground(String... params) {
            String rid = params[0];
            String joinroom_url = "http://come2jp.com/dominion/joinGameRoom.php";
            try {
                URL url = new URL(joinroom_url);
                ConnectionHandler conHan = new ConnectionHandler(url);
                conHan.useSession(context);
                String post_data = URLEncoder.encode("rid", "UTF-8") + "=" + URLEncoder.encode(rid, "UTF-8");
                conHan.post(post_data);
                String result = conHan.get();
                return result;
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPreExecute() {
            builder = new AlertDialog.Builder(context);
            builder.setTitle("Join Game Room");
        }

        @Override
        protected void onPostExecute(String result) {
            if (result.equals("fail")) {
                builder.setMessage(result);
                builder.setNegativeButton("Back", null);
            } else {
                builder.setMessage("success");
                builder.setPositiveButton("Enter Game Room", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,
                                        int which) {
                        context.startActivity(new Intent(context, Room.class));
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

}
