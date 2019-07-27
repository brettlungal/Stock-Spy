package com.bagunit.stockspy;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.NotificationCompat;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.flags.impl.DataUtils;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.json.*;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.HttpsURLConnection;

public class AddspyFragment extends Fragment implements View.OnClickListener , AdapterView.OnItemSelectedListener{

    private TextView s;
    private Spinner dropdown;
    private EditText tick , buff;
    private double price , theBuff = 0;
    private NotificationChannel channel;
    private NotificationManager manager;
    private FirebaseAuth mAuth;
    private DatabaseReference dBase;
    private boolean isTSX = true;
    private String tickerName;
    private String exchange;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View addView = inflater.inflate(R.layout.fragment_addspy , container , false );
        s = (TextView)addView.findViewById(R.id.selected);
        tick = (EditText)addView.findViewById(R.id.tickerName);
        dropdown = (Spinner)addView.findViewById(R.id.selector);
        buff = (EditText)addView.findViewById(R.id.bufferSize);
        addView.findViewById(R.id.addTick).setOnClickListener(this);
        dropdown.setOnItemSelectedListener(this);

        mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();
        String databasePath = user.getEmail().substring(0,user.getEmail().indexOf("@"));
        dBase = FirebaseDatabase.getInstance().getReference(databasePath);

        channel = new NotificationChannel( "Channel1" , "Channel 1" , NotificationManager.IMPORTANCE_HIGH );
        channel.setDescription("A stock varied outside of boundry!");
        manager = getActivity().getSystemService(NotificationManager.class);
        manager.createNotificationChannel(channel);

        return addView;
    }

    private void hideKeyboard(){
        View v = getActivity().getCurrentFocus();
        if ( v != null ){
            ((InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(v.getWindowToken(),InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

    @Override
    public void onClick(View v) {

        switch ( v.getId() ){

            case R.id.addTick:

                    hideKeyboard();
                    String selectedTicker = tick.getText().toString().trim();

                    String bufferValue = buff.getText().toString().trim();

                    if ( selectedTicker.isEmpty() ){
                        tick.setError("Please enter a ticker");
                        tick.requestFocus();
                        return;
                    }

                    if ( bufferValue.isEmpty() ){
                        buff.setError("Please enter a buffer amount");
                        buff.requestFocus();
                        return;
                    }

                tickerName = tick.getText().toString().trim();
                theBuff = Double.parseDouble(bufferValue);


                    try{
                        if ( isTSX ){

                            new CallbackTask().execute("https://www.alphavantage.co/query?function=GLOBAL_QUOTE&symbol=" + exchange + selectedTicker + "&apikey=81PMPLMOASGL2QKM").get(1000 , TimeUnit.MILLISECONDS);
                        }else {
                            //NASDAQ api

                        }

                }catch (Exception e){
                    //suck a dick
                }
                if ( !(TextUtils.isEmpty(selectedTicker)) && theBuff != 0 ){
                    String id = dBase.push().getKey();
                    SpyedStock stock = new SpyedStock(selectedTicker, price , theBuff , id , isTSX);
                    dBase.child(id).setValue(stock);
                    Toast.makeText(getActivity().getApplicationContext(),"You are now spying on "+selectedTicker,Toast.LENGTH_SHORT).show();
                    tick.setText("");
                    buff.setText("");
                    theBuff = 0;
                }

                break;

        }

    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

        switch ( parent.getItemAtPosition(position).toString() ){

            case "TSX":
                s.setText("TSX SELECTED");
                isTSX = true;
                exchange = "TSX:";
                break;

            case "NYSE":
                s.setText("NYSE SELECTED");
                isTSX = false;
                exchange = "NYSE:";
                break;

        }

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    public void sendNotification(){

        NotificationCompat.Builder build = new NotificationCompat.Builder(getActivity().getApplicationContext() , "Channel1")
                .setSmallIcon(R.drawable.add).setContentTitle("Stock Spy").setContentText(tickerName+ " price has fluctuated outside of bounds!").setPriority(NotificationCompat.PRIORITY_HIGH);

        manager.notify(1 , build.build());

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
                s.setText(tick.getText().toString().trim()+" is trading for "+price+" per share");
                sendNotification();
            }else{
                Toast.makeText(getActivity() , "api call fail" , Toast.LENGTH_SHORT).show();

            }
        }

    }


}



