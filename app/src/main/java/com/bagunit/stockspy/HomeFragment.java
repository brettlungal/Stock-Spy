package com.bagunit.stockspy;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.NotificationCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

public class HomeFragment extends Fragment implements View.OnClickListener {

    private FirebaseAuth mAuth;
    private DatabaseReference dBase;
    private TextView welMsg;
    private NotificationChannel channel2;
    private NotificationManager manager;

    private String exchange;
    private String symbol;
    private double price = 0.0;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View homeView = inflater.inflate(R.layout.fragment_home , container , false );
        mAuth = FirebaseAuth.getInstance();

        FirebaseUser user = mAuth.getCurrentUser();
        String databasePath = user.getEmail().substring(0,user.getEmail().indexOf("@"));
        dBase = FirebaseDatabase.getInstance().getReference(databasePath);
        welMsg = homeView.findViewById(R.id.welcome);
        welMsg.setText("welcome, "+databasePath);

        channel2 = new NotificationChannel( "Channel2" , "Channel 2" , NotificationManager.IMPORTANCE_HIGH );
        channel2.setDescription("A stock varied outside of boundry!");
        manager = getActivity().getSystemService(NotificationManager.class);
        manager.createNotificationChannel(channel2);

        homeView.findViewById(R.id.stonks).setOnClickListener(this);

        return homeView;
    }

    public void sendNotification(){

        NotificationCompat.Builder build = new NotificationCompat.Builder(getActivity().getApplicationContext() , "Channel2")
                .setSmallIcon(R.drawable.add).setContentTitle("Stock Spy").setContentText("you touched shit").setPriority(NotificationCompat.PRIORITY_HIGH);

        manager.notify(2 , build.build());

    }

    @Override
    public void onStart() {
        super.onStart();

        dBase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for ( DataSnapshot data : dataSnapshot.getChildren() ){
                    //iterate through the database, pull data and make an api call
                    SpyedStock curr = data.getValue(SpyedStock.class);
                    symbol = curr.getTicker();
                    if ( curr.isTSX() ){
                        exchange = "TSX:";
                    }else {
                        exchange = "NYSE:";
                    }


                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    @Override
    public void onClick(View v) {

        switch ( v.getId() ){

            case R.id.stonks:
                sendNotification();
                break;

        }

    }


    private class CallbackTask extends AsyncTask<String,Integer,String> {

        private int success;
        private StringBuilder apiResult;

        protected String doInBackground(String... params){


            try{
                URL url = new URL(params[0]);
                HttpsURLConnection connection = (HttpsURLConnection)url.openConnection();


                success = connection.getResponseCode();

                if ( success == 200 ){
                    System.out.println("===============YAY SUCCESS=================");
                }
                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                apiResult = new StringBuilder();
                String line = null;
                while ( (line = reader.readLine()) != null ){
                    apiResult.append(line+"\n");
                }

                String data = apiResult.toString();

                JSONObject apiData = new JSONObject(data);
                JSONObject global = apiData.getJSONObject("Global Quote");

                if ( price == 0.0 ){
                    //get inital price but dont change it every call
                    System.out.println("=====================\n"+global.length());
                    price = global.getDouble("05. price");
                }
                System.out.println("=======\nPrice: "+price);

                return apiResult.toString();

            }catch(Exception e){
                e.printStackTrace();
                return e.toString();
            }

        }

        protected void onPostExecute(String result){
            super.onPostExecute(result);
            System.out.println(result);
            if ( success == 200 ) {
                //s.setText(tick.getText().toString().trim()+" is trading for "+price+" per share");
                //sendNotification();
            }else{
                Toast.makeText(getActivity() , "api call fail" , Toast.LENGTH_SHORT).show();

            }
        }

    }


}
