package com.bagunit.stockspy;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;


import java.util.List;

public class SpyList extends ArrayAdapter<SpyedStock> {


    private Activity context;
    private List<SpyedStock> stocks;


    public SpyList(Activity context , List<SpyedStock> stocks ){
        super(context , R.layout.list_layout , stocks);
        this.context = context;
        this.stocks = stocks;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();

        View listViewItem = inflater.inflate(R.layout.list_layout , null , true);
        TextView ticker = (TextView)listViewItem.findViewById(R.id.tickerName);
        TextView price = (TextView)listViewItem.findViewById(R.id.price);
       TextView buff = (TextView)listViewItem.findViewById(R.id.buffer);

        SpyedStock spy = stocks.get(position);
        ticker.setText(spy.getTicker());
        price.setText("Price: "+spy.getStaticPrice());
        buff.setText("Buffer: "+spy.getBuffer());

        return listViewItem;
    }
}
