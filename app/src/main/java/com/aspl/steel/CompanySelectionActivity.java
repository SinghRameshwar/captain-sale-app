package com.aspl.steel;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import com.aspl.steel.CrashReport.TopExceptionHandler;
import com.aspl.steel.Services.LogoutService;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;

/**
 *  Created by Arnab Kar on 17 February 2016.
 */
public class CompanySelectionActivity extends AppCompatActivity {

    public void onCompanySelect(View view){
        Spinner company_select=(Spinner)findViewById(R.id.company_select);
        if(company_select.getAdapter().isEmpty()){
            Toast.makeText(CompanySelectionActivity.this,"Select a company",Toast.LENGTH_SHORT).show();
            return;
        }
        String companyName= company_select.getSelectedItem().toString();
        String loginJson=getIntent().getExtras().getString("loginJson", "{}");
        String cid="0";
        String segid="0";
        try{
            JSONObject mainObj=new JSONObject(loginJson);
            JSONArray compArr=mainObj.getJSONArray("compArr");
            for(int i=0;i<compArr.length();i++){
                JSONObject companyListItem=compArr.getJSONObject(i);
                if(companyName.equals(companyListItem.getString("compname"))){
                cid=companyListItem.getString("cid");
                segid=companyListItem.getString("segid");
                break;
                }
            }
        }catch (Exception e){
            cid="0";
            segid="0";
        }
        SharedPreferences sharedPref = getSharedPreferences("loginCredentials", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString("cid",cid);
        editor.putString("segid",segid);
        editor.apply();
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Thread.setDefaultUncaughtExceptionHandler(new TopExceptionHandler(this));
        setContentView(R.layout.activity_company_select);
        findViewById(R.id.btn_login).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.setEnabled(false);
                onCompanySelect(v);
                SharedPreferences sharedPref = getSharedPreferences("loginCredentials", Context.MODE_PRIVATE);
                String cid=sharedPref.getString("cid", "");
                String segid=sharedPref.getString("segid", "");
                String tempKeyCode=sharedPref.getString("storedKeyCode","");
                String uid=sharedPref.getString("storedUserId","");
                new menuFetcher(cid,segid,uid,tempKeyCode).execute();
                /*Intent intent = new Intent(CompanySelectionActivity.this, MainActivity.class);
                startActivity(intent);
                finish();*/
            }
        });
        String companyList[];
        String loginJson=getIntent().getExtras().getString("loginJson","{}");
        try{
            JSONObject mainObj=new JSONObject(loginJson);
            JSONArray compArr=mainObj.getJSONArray("compArr");
            companyList=new String[compArr.length()];
            for(int i=0;i<compArr.length();i++){
                JSONObject companyListItem=compArr.getJSONObject(i);
                String companyName=companyListItem.getString("compname");
                companyList[i]=companyName;
                String cid=companyListItem.getString("cid");
                String segid=companyListItem.getString("segid");
            }
        }catch (Exception e){
            companyList=new String[0];
        }
        Spinner company_select=(Spinner)findViewById(R.id.company_select);
        ArrayAdapter adapter = new ArrayAdapter(this,R.layout.spinner_layout,companyList);
        adapter.setDropDownViewResource(R.layout.spinner_layout);
        company_select.setAdapter(adapter);

    }
    class menuFetcher extends AsyncTask<String,Void,String>{
        String cid,segId,uid,tempKeyCode;
        public menuFetcher(String cid,String segId,String uid,String tempKeyCode) {
            this.cid=cid;
            this.uid=uid;
            this.segId=segId;
            this.tempKeyCode=tempKeyCode;
        }

        @Override
        protected String doInBackground(String... arg0) {
            try{
                String dnsport= GlobalConfiguration.getDomainport();
                String link="http://"+dnsport+"/SteelSales-war/Stl_Menu_Api";
                HashMap<String,String> hm=new HashMap<>();
                hm.put("cid",cid);
                hm.put("segid", segId);
                hm.put("uid", uid);
                hm.put("keyCode",tempKeyCode);
                return SteelHelper.performPostCall(link, hm);
            }catch (Exception e){
                return "Exception: " + e.toString();
            }
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            //Log.e(".......",s);
            try {
                findViewById(R.id.btn_login).setEnabled(true);
            }catch (Exception e){}
            try{
                JSONObject menuObj=new JSONObject(s);
                if(menuObj.getString("type").equals("success")){
                    SharedPreferences sharedPreferences=getSharedPreferences("loginCredentials", Context.MODE_PRIVATE);
                    sharedPreferences.edit().putString("storedMenu",menuObj.getJSONArray("menu").toString()).apply();
                    Intent intent = new Intent(CompanySelectionActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                }else {
                    Toast.makeText(CompanySelectionActivity.this,"Session expired.",Toast.LENGTH_SHORT).show();
                    Intent logout_intent=new Intent(CompanySelectionActivity.this, LogoutService.class);
                    startService(logout_intent);
                }
            }catch (Exception e){
                Toast.makeText(CompanySelectionActivity.this,"Unable to connect to server.",Toast.LENGTH_SHORT).show();
            }
        }
    }
}
