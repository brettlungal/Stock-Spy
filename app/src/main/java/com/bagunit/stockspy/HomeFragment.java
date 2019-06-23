package com.bagunit.stockspy;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class HomeFragment extends Fragment implements View.OnClickListener {

    private FirebaseAuth mAuth;
    private TextView welMsg;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View homeView = inflater.inflate(R.layout.fragment_home , container , false );
        mAuth = FirebaseAuth.getInstance();

        FirebaseUser user = mAuth.getCurrentUser();
        String databasePath = user.getEmail().substring(0,user.getEmail().indexOf("@"));
        welMsg = homeView.findViewById(R.id.welcome);
        welMsg.setText("welcome, "+databasePath);



        return homeView;
    }

    @Override
    public void onClick(View v) {

    }
}
