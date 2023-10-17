package com.aspl.steel.MasionMeetStatus;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import com.aspl.steel.DealerVisitPRtp.VisitPListAdapter;
import com.aspl.steel.R;

import org.json.JSONArray;
import org.json.JSONObject;

public class MasonMeetStatusAdapter extends BaseAdapter {
    String LOG_DBG=this.getClass().getSimpleName();
    public JSONArray myList ;
    LayoutInflater inflater;
    Context context;

    public MasonMeetStatusAdapter(Context context, JSONArray myList) {
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
            convertView = inflater.inflate(R.layout.mason_meet_status_cell, parent,false);
            mViewHolder = new MyViewHolder();
            convertView.setTag(mViewHolder);
        } else {
            mViewHolder = (MyViewHolder) convertView.getTag();
        }

        mViewHolder.serno=(TextView)convertView.findViewById(R.id.serial_no);
        mViewHolder.date1=(TextView)convertView.findViewById(R.id.dateTxt);
        mViewHolder.pstatus1=(TextView)convertView.findViewById(R.id.pstatus1);
        mViewHolder.pstatus2=(TextView)convertView.findViewById(R.id.pstatus2);
        mViewHolder.dealername=(TextView)convertView.findViewById(R.id.dealer_name);

        try{
            JSONObject jsonObject1= myList.getJSONObject(position);
            mViewHolder.serno.setText(jsonObject1.getString("voucher_no"));
            mViewHolder.date1.setText(jsonObject1.getString("meet_date"));
            mViewHolder.pstatus1.setText(jsonObject1.getString("asm_approve_status"));
            mViewHolder.pstatus2.setText(jsonObject1.getString("sh_approve_status"));
            mViewHolder.dealername.setText(jsonObject1.getString("party_name"));

            if (jsonObject1.getString("asm_approve_status").equalsIgnoreCase("pending")) {
                mViewHolder.pstatus1.setTextColor(ContextCompat.getColor(context, R.color.blue));
            }else  if (jsonObject1.getString("asm_approve_status").equalsIgnoreCase("approve")) {
                mViewHolder.pstatus1.setTextColor(ContextCompat.getColor(context, R.color.green));
            }else {
                mViewHolder.pstatus1.setTextColor(ContextCompat.getColor(context, R.color.red));
            }

            if (jsonObject1.getString("sh_approve_status").equalsIgnoreCase("pending")) {
                mViewHolder.pstatus2.setTextColor(ContextCompat.getColor(context, R.color.blue));
            }else  if (jsonObject1.getString("sh_approve_status").equalsIgnoreCase("approve")) {
                mViewHolder.pstatus2.setTextColor(ContextCompat.getColor(context, R.color.green));
            }else {
                mViewHolder.pstatus2.setTextColor(ContextCompat.getColor(context, R.color.red));
            }

        }catch (Exception e){}

        return convertView;
    }

    private class MyViewHolder {
        TextView serno,date1,pstatus1,pstatus2,dealername;
    }

}


