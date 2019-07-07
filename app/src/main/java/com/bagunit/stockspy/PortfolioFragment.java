package com.bagunit.stockspy;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class PortfolioFragment extends Fragment implements View.OnClickListener {


    private FirebaseAuth mAuth;
    private DatabaseReference dBase;
    private List<SpyedStock> stocks;
    private ListView displayList;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View portView = inflater.inflate(R.layout.fragment_portfolio , container , false );
        displayList = portView.findViewById(R.id.listDisplay);
        stocks = new ArrayList<>();
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();
        String databasePath = user.getEmail().substring(0,user.getEmail().indexOf("@"));
        dBase = FirebaseDatabase.getInstance().getReference(databasePath);



        return portView;
    }

    @Override
    public void onClick(View v) {



    }

    @Override
    public void onStart() {
        super.onStart();
        dBase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                stocks.clear();
                for ( DataSnapshot currStock : dataSnapshot.getChildren() ){
                    SpyedStock spy = currStock.getValue(SpyedStock.class);
                    stocks.add(spy);
                }

                SpyList adapter = new SpyList( getActivity() , stocks );
                displayList.setAdapter(adapter);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }
}
