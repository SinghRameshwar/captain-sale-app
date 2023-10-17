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
 *  Created by Arnab on 29/2/16.
 *  This Adapter populates autocomplete lists by querying the server
 */
public class AutocompleteBaseAdapter extends BaseAdapter {

    String LOG_DBG=this.getClass().getSimpleName();
    public ArrayList<HashMap> myList = new ArrayList<>();
    ArrayList<HashMap> myListTemp=new ArrayList<>();
    LayoutInflater inflater;
    Context context;

    public AutocompleteBaseAdapter(Context context, ArrayList<HashMap> myList) {
        super();
        this.myList = myList;
        myListTemp=myList;
        this.context = context;
        inflater = LayoutInflater.from(this.context);        // only context can also be used
    }

    @Override
    public int getCount() {
        return myListTemp.size();
    }

    @Override
    public Object getItem(int position) {
        return myListTemp.get(position);
    }  //Storing values of each lines in a list. Use it to retrieve and show detail on other page

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        MyViewHolder mViewHolder;

        if(convertView == null) {
            convertView = inflater.inflate(R.layout.autocomplete_adapter_layout, parent,false);
            mViewHolder = new MyViewHolder();
            convertView.setTag(mViewHolder);
        } else {
            mViewHolder = (MyViewHolder) convertView.getTag();
        }

        mViewHolder.mainTitle=(TextView)convertView.findViewById(R.id.text1);
        mViewHolder.tvJsonObj=(TextView)convertView.findViewById(R.id.tvJsonObj);
        mViewHolder.unit=(TextView)convertView.findViewById(R.id.unit);

        HashMap<String,Object> data;
        data=((HashMap)myListTemp.get(position));
        mViewHolder.mainTitle.setText(data.get("title").toString());
        mViewHolder.mainTitle.setTag(data.get("id").toString());
        if(data.get("jsonObj")!=null){
            mViewHolder.tvJsonObj.setText(data.get("jsonObj").toString());
        }
        if(data.get("unitName")!=null){
            mViewHolder.unit.setText(data.get("unitName").toString());
            mViewHolder.unit.setTag(data.get("id").toString());
        }

        return convertView;
    }

    private class MyViewHolder {
        TextView mainTitle,tvJsonObj,unit;
    }

}