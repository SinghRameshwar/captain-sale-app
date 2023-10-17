package com.aspl.steel.NewLeadGp;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import com.aspl.steel.MasionMeetStatus.MasonMeetStatusAdapter;
import com.aspl.steel.R;

import org.json.JSONArray;
import org.json.JSONObject;

public class LeadStatusAdapter extends BaseAdapter {
    String LOG_DBG=this.getClass().getSimpleName();
    public JSONArray myList ;
    LayoutInflater inflater;
    Context context;

    public LeadStatusAdapter(Context context, JSONArray myList) {
        super();
        this.myList = myList;
        this.context = context;
        inflater = LayoutInflater.from(this.context);        // only context can also be used
    }

    @Override
    public int getCount() {
        return myList.length();
    }

    @Override
    public Object getItem(int position) {
        try {
            return myList.get(position);
        }catch (Exception e){
            return new JSONObject();
        }
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final MyViewHolder mViewHolder;

        if(convertView == null) {
            convertView = inflater.inflate(R.layout.new_lead_status_cell, parent,false);
            mViewHolder = new MyViewHolder();
            convertView.setTag(mViewHolder);
        } else {
            mViewHolder = (MyViewHolder) convertView.getTag();
        }

        mViewHolder.serno=(TextView)convertView.findViewById(R.id.serial_no);
        mViewHolder.date1=(TextView)convertView.findViewById(R.id.dateTxt);
        mViewHolder.owner_name=(TextView)convertView.findViewById(R.id.owner_name);
        mViewHolder.pstatus2=(TextView)convertView.findViewById(R.id.pstatus2);
        mViewHolder.dealername=(TextView)convertView.findViewById(R.id.dealer_name);
        mViewHolder.sales_name=(TextView)convertView.findViewById(R.id.sales_name);

        try{
            JSONObject jsonObject1= myList.getJSONObject(position);

            mViewHolder.serno.setText(jsonObject1.getString("lead_num"));
            mViewHolder.date1.setText(jsonObject1.getString("date"));
            mViewHolder.owner_name.setText(jsonObject1.getString("owners_name"));
            mViewHolder.pstatus2.setText(jsonObject1.getString("lead_status"));
            mViewHolder.sales_name.setText(jsonObject1.getString("salesmna_name"));

            if (jsonObject1.getString("lead_status").equalsIgnoreCase("Open")) {
                mViewHolder.pstatus2.setTextColor(ContextCompat.getColor(context, R.color.green));
            }else  if (jsonObject1.getString("lead_status").equalsIgnoreCase("Closed")) {
                mViewHolder.pstatus2.setTextColor(ContextCompat.getColor(context, R.color.red));
            }else{
                mViewHolder.pstatus2.setTextColor(ContextCompat.getColor(context, R.color.grey));
            }
            mViewHolder.dealername.setText(jsonObject1.getString("party_name"));

        }catch (Exception e){Log.e("Error Adapter: ",e.toString());}

        return convertView;
    }

    private class MyViewHolder {
        TextView serno,date1,owner_name,pstatus2,dealername,sales_name;
    }

}
