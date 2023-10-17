package com.aspl.steel.adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import com.aspl.steel.R;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class AutoDealerSearchAdp extends BaseAdapter {

    JSONArray jsonArray;
    Context context;
    LayoutInflater inflater;

    public AutoDealerSearchAdp(Context context, JSONArray jsonArray) {
        super();
        this.jsonArray = jsonArray;
        this.context = context;
        inflater = LayoutInflater.from(this.context);
    }

    @Override
    public int getCount() {
        return jsonArray.length();
    }

    @Override
    public Object getItem(int i) {
        try {
            return jsonArray.get(i);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return 0;
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View convertView, ViewGroup parent) {

        if (convertView==null){
            LayoutInflater mInflater=(LayoutInflater)context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
            convertView=mInflater.inflate(R.layout.auto_search_dealer_spinner,null);
        }
        TextView mainTitle=(TextView)convertView.findViewById(R.id.text1);
        try {
            JSONObject object=jsonArray.getJSONObject(i);
            mainTitle.setText(object.getString("party_name"));
            mainTitle.setTag(object);

        }catch (Exception e){}

        return convertView;
    }

}
