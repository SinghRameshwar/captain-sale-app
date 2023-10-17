package com.aspl.steel.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.aspl.steel.R;

import java.util.ArrayList;
import java.util.HashMap;

/**
 *  Created by Arnab Kar on 01 June 2016.
 */
public class gvMonthlySalesReportDateDetailAdapter extends BaseAdapter{
    String LOG_DBG=this.getClass().getSimpleName();
    public ArrayList<HashMap> myList = new ArrayList<>();
    LayoutInflater inflater;
    Context context;
    String monthYear;

    public gvMonthlySalesReportDateDetailAdapter(Context context, ArrayList<HashMap> myList, String monthYear) {
        super();
        this.myList = myList;
        this.context = context;
        inflater = LayoutInflater.from(this.context);        // only context can also be used
        this.monthYear=monthYear;
    }
    @Override
    public int getCount() {
        return myList.size();
    }

    @Override
    public Object getItem(int position) {
        return myList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        MyViewHolder mViewHolder;

        if(convertView == null) {
            convertView = inflater.inflate(R.layout.monthly_report_detail_grid_list, parent,false);
            mViewHolder = new MyViewHolder();
            convertView.setTag(mViewHolder);
        } else {
            mViewHolder = (MyViewHolder) convertView.getTag();
        }

        mViewHolder.day_of_month=(TextView)convertView.findViewById(R.id.day_of_month);
        mViewHolder.monthYear=(TextView)convertView.findViewById(R.id.monthYear);
        mViewHolder.quantity=(TextView)convertView.findViewById(R.id.quantity);
        mViewHolder.mainCard=convertView.findViewById(R.id.mainCard);

        HashMap<String,Object> data;
        data=((HashMap)myList.get(position));

        mViewHolder.monthYear.setText(data.get("location").toString());
        mViewHolder.quantity.setText(data.get("quantity").toString());
        mViewHolder.day_of_month.setText(data.get("dealerName").toString());
        if(mViewHolder.quantity.getText().toString().equals("0")){
            mViewHolder.mainCard.setAlpha(0.5f);
        }else {
            mViewHolder.mainCard.setAlpha(1.0f);
        }

        return convertView;
    }

    private class MyViewHolder {
        TextView day_of_month,monthYear,quantity;
        View mainCard;
    }

}
