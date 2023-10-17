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
 * Created by Arnab Kar on 5/3/2016.
 * This is a custom adapter that is used to populate the grid view in monthly sales report as of now
 */
public class gvMonthlySalesReportDateAdapter extends BaseAdapter{
    String LOG_DBG=this.getClass().getSimpleName();
    public ArrayList<HashMap> myList = new ArrayList<>();
    ArrayList<HashMap> myListTemp=new ArrayList<>();
    LayoutInflater inflater;
    Context context;
    String userType,monthYear;

    public gvMonthlySalesReportDateAdapter(Context context, ArrayList<HashMap> myList, String userType,String monthYear) {
        super();
        this.myList = myList;
        myListTemp=myList;
        this.context = context;
        inflater = LayoutInflater.from(this.context);        // only context can also be used
        this.userType=userType;
        this.monthYear=monthYear;
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
            convertView = inflater.inflate(R.layout.monthly_report_grid_list_2, parent,false);
            mViewHolder = new MyViewHolder();
            convertView.setTag(mViewHolder);
        } else {
            mViewHolder = (MyViewHolder) convertView.getTag();
        }

        mViewHolder.dayOfMonth=(TextView)convertView.findViewById(R.id.day_of_month);
        mViewHolder.txtTotal=(TextView)convertView.findViewById(R.id.txtTotal);
        mViewHolder.monthYear=(TextView)convertView.findViewById(R.id.monthYear);
        mViewHolder.dealerCount=(TextView)convertView.findViewById(R.id.txtDealerCount);
        mViewHolder.cardBody=convertView.findViewById(R.id.card_body);
        mViewHolder.mainCard=convertView.findViewById(R.id.mainCard);

        int sdk = Build.VERSION.SDK_INT;

        if(position%2==0){
            if(sdk < Build.VERSION_CODES.JELLY_BEAN) {
                //noinspection deprecation
                mViewHolder.mainCard.setBackgroundDrawable(ContextCompat.getDrawable(context, R.drawable.dark_container_bg_round_color1));
            } else {
                mViewHolder.mainCard.setBackground(ContextCompat.getDrawable(context, R.drawable.dark_container_bg_round_color1));
            }
        }else {
            if(sdk < Build.VERSION_CODES.JELLY_BEAN) {
                //noinspection deprecation
                mViewHolder.mainCard.setBackgroundDrawable(ContextCompat.getDrawable(context, R.drawable.dark_container_bg_round_color2));
            } else {
                mViewHolder.mainCard.setBackground(ContextCompat.getDrawable(context, R.drawable.dark_container_bg_round_color2));
            }
        }

        HashMap<String,Object> data;
        data=((HashMap)myListTemp.get(position));
        mViewHolder.cardBody.setTag(data.get("jsonData".toString()));
        mViewHolder.dayOfMonth.setText(data.get("date_day").toString());
        mViewHolder.txtTotal.setText(data.get("total").toString());
        mViewHolder.dealerCount.setText(String.format("%s Dealer(s)", data.get("dealer_count").toString()));
        mViewHolder.monthYear.setText(monthYear);

        return convertView;

        /*hashMap.put("date_day", date_day);
        hashMap.put("month_year",strDateToSend);
        hashMap.put("dealer_count", dealer_count+"");
        hashMap.put("total", strQunatity);
        hashMap.put("jsonData",dataArr.toString());*/
    }

    private class MyViewHolder {
        TextView dayOfMonth,txtTotal,dealerCount,monthYear;
        View cardBody,mainCard;
    }

}
