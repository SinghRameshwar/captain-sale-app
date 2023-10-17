package com.aspl.steel.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.aspl.steel.Controller;
import com.aspl.steel.R;

import org.json.JSONArray;
import org.json.JSONObject;

import java.math.BigDecimal;

/**
 *  Created by Arnab Kar on 18 June 2016.
 */
public class lvRequisitionAdapter extends BaseAdapter{
    String LOG_DBG=this.getClass().getSimpleName();
    public JSONArray myList ;
    LayoutInflater inflater;
    AppCompatActivity context;

    public lvRequisitionAdapter(AppCompatActivity context, JSONArray myList) {
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
            convertView = inflater.inflate(R.layout.daily_sale_confirm_listitem, parent,false);
            mViewHolder = new MyViewHolder();
            convertView.setTag(mViewHolder);
        } else {
            mViewHolder = (MyViewHolder) convertView.getTag();
        }

        mViewHolder.dealerName=(TextView)convertView.findViewById(R.id.dealerName);
        mViewHolder.subDealerName=(TextView)convertView.findViewById(R.id.subDealerName);
        mViewHolder.itemName=(TextView)convertView.findViewById(R.id.itemName);
        mViewHolder.unit=(TextView)convertView.findViewById(R.id.unit);
        mViewHolder.quantity=(TextView)convertView.findViewById(R.id.quantity);
        mViewHolder.deleteView=convertView.findViewById(R.id.deleteLayout);
        mViewHolder.undo=convertView.findViewById(R.id.undo);

        String dealerName,subDealerName,itemName,unit;
        BigDecimal quantity;
        int decimalPlaces;
        try{
            final JSONObject data=myList.getJSONObject(position);
            if(data.getString("status").equals("0")){
                mViewHolder.deleteView.setVisibility(View.VISIBLE);
            }else {
                mViewHolder.deleteView.setVisibility(View.INVISIBLE);
            }
            mViewHolder.undo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final JSONObject dailySalesMainTemplate = ((Controller) context.getApplication()).dailySalesMainTemplate;
                    try{

                        dailySalesMainTemplate.getJSONObject("invVoucherObj").getJSONArray("invVdetailList").getJSONObject(position).put("status",1);
                        mViewHolder.deleteView.setVisibility(View.INVISIBLE);
                    }catch (Exception e){
                        Toast.makeText(context,"Error deleting",Toast.LENGTH_SHORT).show();
                    }
                }
            });
            itemName=data.getJSONObject("itemId").getString("itemName");
            unit=data.getJSONObject("itemId").getJSONObject("unit").getString("unitName");
            //decimalPlaces=data.getJSONObject("itemId").getJSONObject("unit").getInt("decimalPlaces");
            decimalPlaces=3;
            quantity=BigDecimal.valueOf(data.getDouble("tqty"));
            dealerName=data.isNull("dealerName")?"":data.getString("dealerName");
            subDealerName=data.isNull("subDealerName")?"":data.getString("subDealerName");

        }catch (Exception e){
            dealerName="";
            subDealerName="";
            itemName="";
            unit="";
            quantity=new BigDecimal(0);
            decimalPlaces=3;
        }

        mViewHolder.dealerName.setText(dealerName);
        mViewHolder.subDealerName.setText(subDealerName);
        mViewHolder.itemName.setText(itemName);
        mViewHolder.unit.setText(unit);
        mViewHolder.quantity.setText(quantity.setScale(decimalPlaces,BigDecimal.ROUND_HALF_EVEN).toPlainString());

        return convertView;
    }

    private class MyViewHolder {
        TextView dealerName,subDealerName,itemName,unit,quantity;
        View deleteView,undo;
    }

}
