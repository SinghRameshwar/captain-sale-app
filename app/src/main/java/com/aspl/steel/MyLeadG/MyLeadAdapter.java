package com.aspl.steel.MyLeadG;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import com.aspl.steel.NewLeadGp.LeadStatusAdapter;
import com.aspl.steel.R;

import org.json.JSONArray;
import org.json.JSONObject;

public class MyLeadAdapter extends BaseAdapter {
    String LOG_DBG=this.getClass().getSimpleName();
    public JSONArray myList ;
    LayoutInflater inflater;
    Context context;

    public MyLeadAdapter(Context context, JSONArray myList) {
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
            convertView = inflater.inflate(R.layout.my_lead_list_cell, parent,false);
            mViewHolder = new MyViewHolder();
            convertView.setTag(mViewHolder);
        } else {
            mViewHolder = (MyViewHolder) convertView.getTag();
        }

        mViewHolder.lead_id=(TextView)convertView.findViewById(R.id.lead_id);
        mViewHolder.contact_txt=(TextView)convertView.findViewById(R.id.contact_txt);
        mViewHolder.owner_name=(TextView)convertView.findViewById(R.id.owner_name);
        mViewHolder.status_txt=(TextView)convertView.findViewById(R.id.status_txt);

        try{
            JSONObject jsonObject1= myList.getJSONObject(position);

            mViewHolder.lead_id.setText(jsonObject1.getString("lead_num"));
            mViewHolder.contact_txt.setText(jsonObject1.getString("owners_contact"));
            mViewHolder.owner_name.setText(jsonObject1.getString("owners_name"));
            mViewHolder.status_txt.setText(jsonObject1.getString("lead_status"));

            if (jsonObject1.getString("lead_status").equalsIgnoreCase("Open")) {
                mViewHolder.status_txt.setTextColor(ContextCompat.getColor(context, R.color.green));
            }else  if (jsonObject1.getString("lead_status").equalsIgnoreCase("Closed")) {
                mViewHolder.status_txt.setTextColor(ContextCompat.getColor(context, R.color.red));
            }else{
                mViewHolder.status_txt.setTextColor(ContextCompat.getColor(context, R.color.grey));
            }

        }catch (Exception e){
            Log.e("Error Adapter: ",e.toString());}

        return convertView;
    }

    private class MyViewHolder {
        TextView lead_id,contact_txt,owner_name,status_txt;
    }

}

