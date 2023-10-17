package com.aspl.steel;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

import com.aspl.steel.CrashReport.TopExceptionHandler;
import com.aspl.steel.adapters.gvMonthlySalesReportDateDetailAdapter;
import com.aspl.steel.adapters.gvMonthlySalesReportDealerDetailAdapter;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

/**
 *  Created by arnab on 5/3/16.
 */
public class MonthlySalesRptDetailActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Thread.setDefaultUncaughtExceptionHandler(new TopExceptionHandler(this));
        setContentView(R.layout.activity_monthly_salesrpt_detail);
        Toolbar toolbar= (Toolbar)findViewById(R.id.my_actionbar_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        if(getIntent().getExtras().getString("type", "").equals("dealer")) {
            String strJsonObject = getIntent().getExtras().getString("jsonObject", "");
            String strTotal = getIntent().getExtras().getString("totalVal", "");
            String monthYear = getIntent().getExtras().getString("monthYear", "");

            ((TextView) findViewById(R.id.monthYear)).setText(monthYear);
            try {
                JSONObject mainObj = new JSONObject(strJsonObject);
                ((TextView) findViewById(R.id.txtDealer)).setText(mainObj.getString("dealer"));
                ((TextView) findViewById(R.id.txtLocation)).setText(mainObj.getString("place"));
                ((TextView) findViewById(R.id.txtSalesman)).setText(mainObj.getString("sales_man"));
                ((TextView) findViewById(R.id.txtTotal)).setText(strTotal);
                ((TextView) findViewById(R.id.txtTotal1)).setText(strTotal);

                GridView gridView = (GridView) findViewById(R.id.gvMonthlySalesReportDetail);
                ArrayList<HashMap> arrayList = new ArrayList<>();
                for (int i = 1; i <= 31; i++) {
                    HashMap<String, String> hashMap = new HashMap<>();
                    String d = "d" + i;
                    hashMap.put("quantity", mainObj.getString(d));
                    arrayList.add(hashMap);
                }
                gvMonthlySalesReportDealerDetailAdapter adapter = new gvMonthlySalesReportDealerDetailAdapter(this, arrayList, monthYear);
                gridView.setAdapter(adapter);
            } catch (Exception e) {
            }
        }
        else if(getIntent().getExtras().getString("type", "").equals("date")){
            String strTotal = getIntent().getExtras().getString("totalVal", "");
            findViewById(R.id.locationLayout).setVisibility(View.GONE);
            findViewById(R.id.dealerLayout).setVisibility(View.GONE);
            ((TextView)findViewById(R.id.title2)).setText("Total Dealer:");
            //((TextView)findViewById(R.id.txtDealer)).setText(getIntent().getExtras().getString("dayMonthYear", ""));
            ((TextView)findViewById(R.id.txtTotal)).setText(strTotal);
            ((TextView)findViewById(R.id.txtTotal1)).setText(strTotal);
            String strJsonObject = getIntent().getExtras().getString("jsonObject", "");
            try {
                JSONArray data = new JSONArray(strJsonObject);
                GridView gridView = (GridView) findViewById(R.id.gvMonthlySalesReportDetail);
                ArrayList<HashMap> arrayList = new ArrayList<>();
                for (int i=0;i<data.length();i++) {
                    HashMap<String, String> hashMap = new HashMap<>();
                    hashMap.put("quantity", data.getJSONObject(i).getString("qty"));
                    hashMap.put("location",data.getJSONObject(i).getString("place"));
                    hashMap.put("dealerName", data.getJSONObject(i).getString("dealer"));
                    arrayList.add(hashMap);
                }
                gvMonthlySalesReportDateDetailAdapter adapter = new gvMonthlySalesReportDateDetailAdapter(this, arrayList, getIntent().getExtras().getString("dayMonthYear", ""));
                gridView.setAdapter(adapter);
                final Toast t=Toast.makeText(this,"",Toast.LENGTH_SHORT);
                gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        t.setText(((TextView) view.findViewById(R.id.day_of_month)).getText().toString());
                        t.show();
                        //Toast.makeText(MonthlySalesRptDetailActivity.this,((TextView) view.findViewById(R.id.day_of_month)).getText().toString(),Toast.LENGTH_SHORT).show();
                    }
                });
            }
            catch (Exception e){
                Log.e(this.getClass().getSimpleName(),e.getLocalizedMessage());
            }
            GridView gridView=(GridView)findViewById(R.id.gvMonthlySalesReportDetail);
            ((TextView)findViewById(R.id.txtSalesman)).setText(gridView.getAdapter().getCount()+"");

        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
