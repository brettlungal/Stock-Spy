package com.bagunit.stockspy;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


public class ProfileActivity extends AppCompatActivity implements View.OnClickListener {

    private FirebaseAuth mAuth;
    private FirebaseDatabase database;
    private DatabaseReference databaseAccess;
    private TextView temp;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        mAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();

        temp = findViewById(R.id.s);

        FirebaseUser user = mAuth.getCurrentUser();
        String databasePath = user.getEmail().substring(0 , user.getEmail().indexOf("@"));
        temp.setText(databasePath);

        databaseAccess = database.getReference(databasePath);


    }

    @Override
    public void onClick(View v) {

    }
}
