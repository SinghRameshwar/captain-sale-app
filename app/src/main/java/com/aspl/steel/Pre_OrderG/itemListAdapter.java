package com.aspl.steel.Pre_OrderG;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.aspl.steel.DealerVisitPRtp.VisitPListAdapter;
import com.aspl.steel.R;

import org.json.JSONArray;
import org.json.JSONObject;

public class itemListAdapter extends BaseAdapter {
    String LOG_DBG=this.getClass().getSimpleName();
    public JSONArray myList ;
    LayoutInflater inflater;
    Context context;

    public itemListAdapter(Context context, JSONArray myList) {
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
            convertView = inflater.inflate(R.layout.item_list_cell, parent,false);
            mViewHolder = new MyViewHolder();
            convertView.setTag(mViewHolder);
        } else {
            mViewHolder = (MyViewHolder) convertView.getTag();
        }

        mViewHolder.item_name=(TextView)convertView.findViewById(R.id.item_name);
        mViewHolder.item_amt=(TextView)convertView.findViewById(R.id.item_amt);

        try{
            JSONObject jsonObject1= myList.getJSONObject(position);
            mViewHolder.item_name.setText(jsonObject1.getString("itemName"));
            mViewHolder.item_amt.setText(jsonObject1.getString("itemRate"));

        }catch (Exception e){}

        return convertView;
    }

    private class MyViewHolder {
        TextView item_name,item_amt;
    }

}
