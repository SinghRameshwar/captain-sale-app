package com.aspl.steel.DealerVisitPRtp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import com.aspl.steel.R;
import org.json.JSONArray;
import org.json.JSONObject;


public class VisitPListAdapter extends BaseAdapter {
    String LOG_DBG=this.getClass().getSimpleName();
    public JSONArray myList ;
    LayoutInflater inflater;
    Context context;

    public VisitPListAdapter(Context context, JSONArray myList) {
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
            convertView = inflater.inflate(R.layout.p_listview_cell, parent,false);
            mViewHolder = new MyViewHolder();
            convertView.setTag(mViewHolder);
        } else {
            mViewHolder = (MyViewHolder) convertView.getTag();
        }

        mViewHolder.visit_p_txt=(TextView)convertView.findViewById(R.id.visit_p_txt);
        mViewHolder.visit_p_date=(TextView)convertView.findViewById(R.id.visit_p_date);

        try{
            JSONObject jsonObject1= myList.getJSONObject(position);
            mViewHolder.visit_p_txt.setText(jsonObject1.getString("party_name"));
            mViewHolder.visit_p_date.setText(jsonObject1.getString("date"));

        }catch (Exception e){}

        return convertView;
    }

    private class MyViewHolder {
        TextView visit_p_txt,visit_p_date;
    }

}


