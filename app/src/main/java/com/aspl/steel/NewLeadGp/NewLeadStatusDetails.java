package com.aspl.steel.NewLeadGp;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.aspl.steel.CrashReport.TopExceptionHandler;
import com.aspl.steel.GlobalConfiguration;
import com.aspl.steel.MyLeadG.MyLeadDetails;
import com.aspl.steel.R;
import com.aspl.steel.Services.LogoutService;
import com.aspl.steel.SteelHelper;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;

public class NewLeadStatusDetails extends AppCompatActivity {

    String LOG_DBG=getClass().getSimpleName();
    String  sLid,cid,segid,tempKeyCode,uid,lead_id;
    TextView d_sub_dlr_txt,site_dtls_txt,site_adrs_txt,infu_name_txt,infu_cont_txt,brnd_usd_txt,led_fr_caotn,led_fr_rstgrd;
    TextView date_txt,oner_name_txt,oner_cont_txt,lead_num,lead_type,gift_required,f_exe;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Thread.setDefaultUncaughtExceptionHandler(new TopExceptionHandler(this));
        setContentView(R.layout.new_lead_status_detais);
        Toolbar toolbar= (Toolbar)findViewById(R.id.my_actionbar_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        SharedPreferences sharedPref = getSharedPreferences("loginCredentials", Context.MODE_PRIVATE);
        cid = sharedPref.getString("cid", "");
        segid = sharedPref.getString("segid", "");
        tempKeyCode = sharedPref.getString("storedKeyCode", "");
        sLid = sharedPref.getString("storedSlid", "");
        uid = sharedPref.getString("storedUserId", "");
        lead_id = getIntent().getStringExtra("lead_id");

        viewRefrance();

        MyLeadDetailsService dealerTargetAPI = new MyLeadDetailsService(NewLeadStatusDetails.this, 1);
        dealerTargetAPI.execute(cid, segid, sLid, tempKeyCode);

    }

    void viewRefrance(){
        d_sub_dlr_txt = (TextView) findViewById(R.id.d_sub_dlr_txt);
        site_dtls_txt = (TextView) findViewById(R.id.site_dtls_txt);
        site_adrs_txt = (TextView) findViewById(R.id.site_adrs_txt);
        infu_name_txt = (TextView) findViewById(R.id.infu_name_txt);
        infu_cont_txt = (TextView) findViewById(R.id.infu_cont_txt);
        brnd_usd_txt = (TextView) findViewById(R.id.brnd_usd_txt);
        led_fr_caotn = (TextView) findViewById(R.id.led_fr_caotn);
        led_fr_rstgrd = (TextView) findViewById(R.id.led_fr_rstgrd);
        date_txt = (TextView) findViewById(R.id.date_txt);
        oner_name_txt = (TextView) findViewById(R.id.oner_name_txt);
        oner_cont_txt = (TextView) findViewById(R.id.oner_cont_txt);
        lead_num = (TextView) findViewById(R.id.lead_num);
        lead_type = (TextView) findViewById(R.id.lead_type);
        gift_required = (TextView) findViewById(R.id.gift_required);
        f_exe = (TextView) findViewById(R.id.f_exe);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
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

    /*.......... MyLeadDetailsService API ..........*/
    public class MyLeadDetailsService extends AsyncTask<String, Void, String> {
        private Context context;
        private int byGetOrPost=0;          //Flag 0 means get and 1 means post. (By default it is get).

        MyLeadDetailsService(Context context,int flag) {
            this.context = context;
            byGetOrPost = flag;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }

        @Override
        protected String doInBackground(String... arg0 ) {
            if(byGetOrPost == 0){ //means by Get Method
                throw new UnsupportedOperationException(this.getClass().getSimpleName() + " -> Get disabled for security reasons");
            }else{
                try{
                    String cid = arg0[0];
                    String segid = arg0[1];
                    String sLid = arg0[2];
                    String tempKeyCode = arg0[3];
                    String dnsport= GlobalConfiguration.getDomainport();
                    String link="http://"+dnsport+"/SteelSales-war/Stl_LeadManagement_GetDetails_ById_C_Api";
                    HashMap<String,String> hm=new HashMap<>();
                    hm.put("cid",cid);
                    hm.put("segid", segid);
                    hm.put("lead_id", lead_id);
                    hm.put("keyCode", tempKeyCode);

                    return SteelHelper.performPostCall(link,hm);
                }catch (Exception e){
                    return "Exception: " + e.toString();
                }
            }
        }

        @Override
        protected void onPostExecute(String result) {
            //Log.e(".....111....",result);
            super.onPostExecute(result);

//            String veryLongString=result;
//            int maxLogSize = 1000;
//            for(int i = 0; i <= veryLongString.length() / maxLogSize; i++) {
//                int start = i * maxLogSize;
//                int end = (i+1) * maxLogSize;
//                end = end > veryLongString.length() ? veryLongString.length() : end;
//                Log.e("..........     ", veryLongString.substring(start, end));
//            }
            try{
                JSONObject mainObj=new JSONObject(result);
                if(mainObj.getString("type").equalsIgnoreCase("success")){
                    JSONObject objectData = mainObj.getJSONObject("data");
                    setDetailsData(objectData);

                }else{
                    Log.e(LOG_DBG,result);
                    throw new Exception("Server error");
                }
            }catch (Exception e){
                Log.e(LOG_DBG,e.toString());
                Toast.makeText(context,"Unable to connect to server",Toast.LENGTH_SHORT).show();
            }
        }
    }

    void setDetailsData(JSONObject object){
        try{
            d_sub_dlr_txt.setText(object.getString("party"));
            site_dtls_txt.setText(object.getString("site_details"));
            site_adrs_txt.setText(object.getString("site_address"));
            infu_name_txt.setText(object.getString("influencer_name"));
            infu_cont_txt.setText(object.getString("influencer_contact"));
            brnd_usd_txt.setText(object.getString("brand_name"));
            led_fr_caotn.setText(object.getString("leading_captain"));
            led_fr_rstgrd.setText(object.getString("leading_rustguard"));

            date_txt.setText(object.getString("lead_crt_dt"));
            oner_name_txt.setText(object.getString("owners_name"));
            oner_cont_txt.setText(object.getString("owners_contact"));
            lead_num.setText(object.getString("lead_num"));
            lead_type.setText(object.getString("lead_type"));
            gift_required.setText(object.getString("gift_required"));
            f_exe.setText(object.getString("exe_sup"));

        }catch (Exception e){Log.e(LOG_DBG,e.toString());}

    }

}
