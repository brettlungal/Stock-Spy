package com.bagunit.stockspy;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.google.firebase.auth.FirebaseAuth;

public class SettingsFragment extends Fragment implements View.OnClickListener {

    private Button logout;
    private FirebaseAuth mAuth;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View settingsView = inflater.inflate(R.layout.fragment_settings , container, false);
        mAuth = FirebaseAuth.getInstance();
        logout = (Button)settingsView.findViewById(R.id.logoutButton);
        logout.setOnClickListener(this);

        return settingsView;
    }

    @Override
    public void onClick(View v) {

        switch ( v.getId() ){

            case R.id.logoutButton:
                mAuth.signOut();
                startActivity( new Intent( getActivity() , LoginActivity.class ) );
                break;

        }

    }
}
