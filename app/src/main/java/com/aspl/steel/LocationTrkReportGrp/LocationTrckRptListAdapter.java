package com.aspl.steel.LocationTrkReportGrp;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import com.aspl.steel.R;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class LocationTrckRptListAdapter extends BaseAdapter {
    String LOG_DBG=this.getClass().getSimpleName();
    public JSONArray myList ;
    LayoutInflater inflater;
    Context context;
    DateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd");
    DateFormat outputFormat = new SimpleDateFormat("dd-MM-yyyy");


    public LocationTrckRptListAdapter(Context context, JSONArray myList) {
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
            convertView = inflater.inflate(R.layout.location_trck_rpt_listcell, parent,false);
            mViewHolder = new MyViewHolder();
            convertView.setTag(mViewHolder);
        } else {
            mViewHolder = (MyViewHolder) convertView.getTag();
        }

        mViewHolder.dateTxt=(TextView)convertView.findViewById(R.id.dateTxt);
        mViewHolder.timeTxt=(TextView)convertView.findViewById(R.id.timeTxt);
        mViewHolder.distanceTxt=(TextView)convertView.findViewById(R.id.distanceTxt);
        mViewHolder.locationTxt=(TextView)convertView.findViewById(R.id.locationTxt);

        try{
            JSONObject jsonObject1= myList.getJSONObject(position);
            mViewHolder.timeTxt.setText(jsonObject1.getString("time"));
            mViewHolder.distanceTxt.setText(jsonObject1.getString("distance")+" Km");
            mViewHolder.locationTxt.setText(jsonObject1.getString("location"));

            String inputDateStr=jsonObject1.getString("date");
            Date date = inputFormat.parse(inputDateStr);
            String outputDateStr = outputFormat.format(date);
            mViewHolder.dateTxt.setText(outputDateStr);

        }catch (Exception e){
            Log.e("Error Adapter: ",e.toString());}

        return convertView;
    }

    private class MyViewHolder {
        TextView dateTxt,timeTxt,distanceTxt,locationTxt;
    }

}

