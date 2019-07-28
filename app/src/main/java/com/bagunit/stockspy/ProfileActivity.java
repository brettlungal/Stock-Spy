package com.bagunit.stockspy;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
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
import java.util.concurrent.TimeUnit;

import javax.net.ssl.HttpsURLConnection;

public class ProfileActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener{


    private FirebaseAuth mAuth;
    private DatabaseReference dBase;
    private double currPrice;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();
        String databasePath = user.getEmail().substring( 0 , user.getEmail().indexOf("@"));
        dBase = FirebaseDatabase.getInstance().getReference(databasePath);

        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(this);
        loadFragment( new HomeFragment() );

        Thread apiThread = new Thread(new Runnable() {
            @Override
            public void run() {



                dBase.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        double staticPrice , buffer;
                        String symbol , exchange;

                        for ( DataSnapshot currData : dataSnapshot.getChildren() ){
                            //TODO compare static to current and create notification if necessary
                            SpyedStock currStock = currData.getValue(SpyedStock.class);
                            staticPrice = currStock.getStaticPrice();
                            buffer = currStock.getBuffer();
                            symbol = currStock.getTicker();
                            if ( currStock.isTSX() ){
                                exchange = "TSX:";
                            }else{
                                exchange = "NYSE:";
                            }
                            try{
                                new CallbackTask().execute("https://www.alphavantage.co/query?function=GLOBAL_QUOTE&symbol=" + exchange + symbol + "&apikey=81PMPLMOASGL2QKM").get(1000 , TimeUnit.MILLISECONDS);
                            }catch (Exception e){
                                //oh that little guy? i wouldnt worry about that little guy
                            }


                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });


            }
        });

        apiThread.start();

    }

    private boolean loadFragment(Fragment f){
        if ( f != null ){
            getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainer , f ).commit();
            return true;
        }
        return false;
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {

        Fragment frag = null;

        switch( menuItem.getItemId() ){
            case R.id.navigation_settings:
                frag = new SettingsFragment();
                break;

            case R.id.navigation_portfolio:
                frag = new PortfolioFragment();
                break;

            case R.id.navigation_home:
                frag = new HomeFragment();
                break;

            case R.id.navigation_add:
                frag = new AddspyFragment();
                break;
        }

        return loadFragment(frag);
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

                if ( currPrice == 0.0 ){
                    //get inital price but dont change it every call
                    System.out.println("=====================\n"+global.length());
                    currPrice = global.getDouble("05. price");
                }
                System.out.println("=======\nPrice: "+currPrice);

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
                Toast.makeText(getApplicationContext() , "api call fail" , Toast.LENGTH_SHORT).show();

            }
        }

    }

}
