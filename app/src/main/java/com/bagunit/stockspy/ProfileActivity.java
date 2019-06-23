package com.bagunit.stockspy;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.TextView;

public class ProfileActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(this);
        loadFragment( new HomeFragment() );

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
}
