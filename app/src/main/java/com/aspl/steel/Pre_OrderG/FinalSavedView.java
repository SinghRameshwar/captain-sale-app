package com.aspl.steel.Pre_OrderG;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import com.aspl.steel.CrashReport.TopExceptionHandler;
import com.aspl.steel.MyLeadG.MyLeadDetails;
import com.aspl.steel.R;

import org.json.JSONArray;
import org.json.JSONObject;

public class FinalSavedView extends Fragment {

    String LOG_DBG = getClass().getSimpleName();
    TextView save_btn;
    View rootView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.pre_order_final_view_saving, container, false);
        String Strobj = getArguments().getString("finalObj");
        String v_numAr = getArguments().getString("v_num");
        try {
            JSONArray savedObjAr = new JSONArray(Strobj);
            JSONArray v_numArObj = new JSONArray(v_numAr);
            finalViewAfterSave(savedObjAr,v_numArObj);

        }catch (Exception e){}

        return rootView;
    }

    void finalViewAfterSave(JSONArray savedObjAr,JSONArray v_numArObj) {
        TextView p_name = rootView.findViewById(R.id.p_name);
        TextView d_date = rootView.findViewById(R.id.d_date);
        TextView v_date = rootView.findViewById(R.id.v_date);
        TextView radio_txt = rootView.findViewById(R.id.radio_txt);
        LinearLayout load_holder = rootView.findViewById(R.id.load_holder);
        TextView okbtn = (TextView)rootView.findViewById(R.id.okbtn);
        okbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getFragmentManager().popBackStack();
            }
        });
        load_holder.removeAllViews();

        try {
            JSONObject savedOjb = savedObjAr.getJSONObject(0);
            JSONArray ledgerDetailsAr = savedOjb.getJSONArray("ledgerDetails");
            JSONObject ledgerDetails = ledgerDetailsAr.getJSONObject(0);

            p_name.setText(ledgerDetails.getString("ledgerName"));
            d_date.setText(savedOjb.getString("txnDt"));
            v_date.setText(savedOjb.getString("txnValidityDate"));
            radio_txt.setText(savedOjb.getString("text2"));


            for (int i = 0; i < savedObjAr.length(); i++) {
                LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                View view = inflater.inflate(R.layout.pre_order_final_view_cell, null);
                TextView sipping_adrs = view.findViewById(R.id.sipping_adrs);
                TextView loadCount = view.findViewById(R.id.load_count);
                LinearLayout itemListV = (LinearLayout)view.findViewById(R.id.itemListV);
                LinearLayout divider_lay = (LinearLayout)view.findViewById(R.id.divider_lay);
                loadCount.setText("Load # "+(i+1)+"            Order Number:- "+v_numArObj.getString(i));
                if (savedObjAr.length()==i+1){
                    divider_lay.setVisibility(View.INVISIBLE);
                }else{
                    divider_lay.setVisibility(View.VISIBLE);
                }

                JSONObject mainOb = savedObjAr.getJSONObject(i);
                sipping_adrs.setText(mainOb.getString("text1"));
                JSONArray itemListAr = mainOb.getJSONArray("itemDetails");

                for (int j=0; j<itemListAr.length(); j++){
                    JSONObject itemObj = itemListAr.getJSONObject(j);
                    if (!itemObj.getString("quantity").equals("0")){
                        LayoutInflater inflater1 = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                        View view1 = inflater1.inflate(R.layout.pre_order_final_view_item_cell, null);
                        TextView item_Name = view1.findViewById(R.id.item_Name);
                        TextView itmQty = view1.findViewById(R.id.itmQty);
                        item_Name.setText(itemObj.getString("itemName"));
                        itmQty.setText(itemObj.getString("quantity")+" "+"MTs");

                        itemListV.addView(view1);
                    }

                }
                load_holder.addView(view);
            }
        } catch (Exception e) {
            Log.e("Error After Save", e.toString()); }
    }
}
