package com.aspl.steel.adapters;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import androidx.core.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.aspl.steel.R;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Arnab Kar on 5 March 2016.
 * This is a custom adapter that is used to populate the grid view in monthly sales report as of now
 */
public class gvMonthlySalesReportDealerAdapter extends BaseAdapter{
    String LOG_DBG=this.getClass().getSimpleName();
    public ArrayList<HashMap> myList = new ArrayList<>();
    ArrayList<HashMap> myListTemp=new ArrayList<>();
    LayoutInflater inflater;
    Context context;
    String userType;

    public gvMonthlySalesReportDealerAdapter(Context context, ArrayList<HashMap> myList, String userType) {
        super();
        this.myList = myList;
        myListTemp=myList;
        this.context = context;
        inflater = LayoutInflater.from(this.context);        // only context can also be used
        this.userType=userType;
    }
    @Override
    public int getCount() {
        return myListTemp.size();
    }

    @Override
    public Object getItem(int position) {
        return myListTemp.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        MyViewHolder mViewHolder;

        if(convertView == null) {
            convertView = inflater.inflate(R.layout.monthly_report_grid_list, parent,false);
            mViewHolder = new MyViewHolder();
            convertView.setTag(mViewHolder);
        } else {
            mViewHolder = (MyViewHolder) convertView.getTag();
        }

        mViewHolder.dealerName=(TextView)convertView.findViewById(R.id.dealerName);
        mViewHolder.txtTotal=(TextView)convertView.findViewById(R.id.txtTotal);
        mViewHolder.txtLocation=(TextView)convertView.findViewById(R.id.txtLocation);
        mViewHolder.salesmanName=(TextView)convertView.findViewById(R.id.salesmanName);
        mViewHolder.cardBody=convertView.findViewById(R.id.card_body);
        mViewHolder.mainCard=convertView.findViewById(R.id.mainCard);

        int sdk = android.os.Build.VERSION.SDK_INT;

        if(position%2==0){
            if(sdk < android.os.Build.VERSION_CODES.JELLY_BEAN) {
                //noinspection deprecation
                mViewHolder.mainCard.setBackgroundDrawable(ContextCompat.getDrawable(context, R.drawable.dark_container_bg_round_color1));
            } else {
                mViewHolder.mainCard.setBackground(ContextCompat.getDrawable(context, R.drawable.dark_container_bg_round_color1));
            }
        }else {
            if(sdk < android.os.Build.VERSION_CODES.JELLY_BEAN) {
                //noinspection deprecation
                mViewHolder.mainCard.setBackgroundDrawable(ContextCompat.getDrawable(context, R.drawable.dark_container_bg_round_color2));
            } else {
                mViewHolder.mainCard.setBackground(ContextCompat.getDrawable(context, R.drawable.dark_container_bg_round_color2));
            }
        }

        if(userType.equals("Salesman")){
            mViewHolder.salesmanName.setVisibility(View.GONE);
        }

        HashMap<String,Object> data;
        data=((HashMap)myListTemp.get(position));
        mViewHolder.cardBody.setTag(data.get("jsonData"));
        mViewHolder.dealerName.setText(data.get("dealerName").toString());
        mViewHolder.txtLocation.setText(data.get("place").toString());
        mViewHolder.salesmanName.setText(data.get("sales_man").toString());
        mViewHolder.txtTotal.setText(data.get("total").toString());

        return convertView;
    }

    private class MyViewHolder {
        TextView dealerName,txtTotal,txtLocation,salesmanName;
        View cardBody,mainCard;
    }

}
