package com.bagunit.stockspy;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import org.json.*;

public class AddspyFragment extends Fragment implements View.OnClickListener , AdapterView.OnItemSelectedListener{

    private TextView s;
    private Spinner dropdown;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View addView = inflater.inflate(R.layout.fragment_addspy , container , false );
        s = (TextView)addView.findViewById(R.id.selected);
        dropdown = (Spinner)addView.findViewById(R.id.selector);
        dropdown.setOnItemSelectedListener(this);


        return addView;
    }

    @Override
    public void onClick(View v) {

    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

        switch ( parent.getItemAtPosition(position).toString() ){

            case "TSX":
                s.setText("TSX SELECTED");
                break;

            case "NYSE":
                s.setText("NYSE SELECTED");
                break;

        }

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}
