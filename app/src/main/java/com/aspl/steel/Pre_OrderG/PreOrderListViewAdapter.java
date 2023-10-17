package com.aspl.steel.Pre_OrderG;

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

import java.text.DecimalFormat;

public class PreOrderListViewAdapter extends BaseAdapter {
    String LOG_DBG=this.getClass().getSimpleName();
    public JSONArray myList ;
    LayoutInflater inflater;
    Context context;
    DecimalFormat precision = new DecimalFormat("0.00");

    public PreOrderListViewAdapter(Context context, JSONArray myList) {
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
            convertView = inflater.inflate(R.layout.pre_order_list_view_cell, parent,false);
            mViewHolder = new MyViewHolder();
            convertView.setTag(mViewHolder);
        } else {
            mViewHolder = (MyViewHolder) convertView.getTag();
        }

        mViewHolder.dateTxt=(TextView)convertView.findViewById(R.id.dateTxt);
        mViewHolder.state=(TextView)convertView.findViewById(R.id.state);
        mViewHolder.owner_name=(TextView)convertView.findViewById(R.id.owner_name);
        mViewHolder.pre_qty=(TextView)convertView.findViewById(R.id.pre_qty);
        mViewHolder.so_qty=(TextView)convertView.findViewById(R.id.so_qty);

        try{
            JSONObject jsonObject1= myList.getJSONObject(position);

            mViewHolder.dateTxt.setText(jsonObject1.getString("preOrderDate"));
            mViewHolder.state.setText(jsonObject1.getString("state"));
            mViewHolder.owner_name.setText(jsonObject1.getString("partyName"));
            mViewHolder.pre_qty.setText(precision.format(jsonObject1.getDouble("preOrderQty")));
            mViewHolder.so_qty.setText(precision.format(jsonObject1.getDouble("soQty")));



        }catch (Exception e){
            Log.e("Error Adapter: ",e.toString());}

        return convertView;
    }

    private class MyViewHolder {
        TextView dateTxt,state,owner_name,pre_qty,so_qty;
    }

}

