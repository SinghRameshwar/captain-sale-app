package com.aspl.steel.AttendanceRptGrp;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import com.aspl.steel.R;

import org.json.JSONArray;
import org.json.JSONObject;

public class AttendanceListAdapter extends BaseAdapter {
    String LOG_DBG=this.getClass().getSimpleName();
    public JSONArray myList ;
    LayoutInflater inflater;
    Context context;

    public AttendanceListAdapter(Context context, JSONArray myList) {
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
            convertView = inflater.inflate(R.layout.attendance_list_cell, parent,false);
            mViewHolder = new MyViewHolder();
            convertView.setTag(mViewHolder);
        } else {
            mViewHolder = (MyViewHolder) convertView.getTag();
        }

        mViewHolder.dateTxt=(TextView)convertView.findViewById(R.id.dateTxt);
        mViewHolder.inTimeTxt=(TextView)convertView.findViewById(R.id.inTimeTxt);
        mViewHolder.outTimeTxt=(TextView)convertView.findViewById(R.id.outTimeTxt);

        try{
            JSONObject jsonObject1= myList.getJSONObject(position);
            mViewHolder.dateTxt.setText(jsonObject1.getString("date"));
            mViewHolder.inTimeTxt.setText(jsonObject1.getString("inTime"));
            mViewHolder.outTimeTxt.setText(jsonObject1.getString("outTime"));

        }catch (Exception e){
            Log.e("Error Adapter: ",e.toString());}

        return convertView;
    }

    private class MyViewHolder {
        TextView dateTxt,inTimeTxt,outTimeTxt;
    }

}

