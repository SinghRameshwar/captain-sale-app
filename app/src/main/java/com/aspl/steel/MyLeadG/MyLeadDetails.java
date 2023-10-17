package com.aspl.steel.MyLeadG;

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
import com.aspl.steel.R;
import com.aspl.steel.Services.LogoutService;
import com.aspl.steel.SteelHelper;
import org.json.JSONArray;
import org.json.JSONObject;
import java.util.HashMap;


public class MyLeadDetails extends AppCompatActivity {

    String blankJson = "{\"id\":null,\"dtCrt\":null,\"status\":1,\"updateSlid\":null,\"leadId\":null,\"segid\":1,\"remarks\":null,\"leadStatus\":null,\"cid\":1,\"dtUpdt\":null,\"convertQty\":null,\"convertFrom\":null}";
    String LOG_DBG=getClass().getSimpleName();
    String  sLid,cid,segid,tempKeyCode,uid,lead_id;
    TextView d_sub_dlr_txt,site_dtls_txt,site_adrs_txt,infu_name_txt,infu_cont_txt,brnd_usd_txt,led_fr_caotn,led_fr_rstgrd;
    TextView date_txt,oner_name_txt,oner_cont_txt,lead_num,lead_type,gift_required,f_exe,lead_f_remark;
    EditText remark_edt,convrt_qty;
    Spinner spin1,spin2;
    LinearLayout histry_lay;
    TextView save_btn;
    String[] statusAr = { "Open", "Convert", "Partial Convert","Close"};
    String[] cnvrtfmAr = { "Adhunik",
            "Balaji", "Balmukund", "Concast", "Dytron", "Elegant", "Gagan", "JSW", "Kamdhenu", "Magadh", "Panther", "Prestige", "Rashmi", "Sail", "Sel", "Shyam", "SRMB",
            "Super Shakti", "Tata", "Toptech", "Ultramax", "Others"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Thread.setDefaultUncaughtExceptionHandler(new TopExceptionHandler(this));
        setContentView(R.layout.my_lead_details);
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
        save_btn = findViewById(R.id.save_btn);
        save_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                JsonPrepareMethod();
            }
        });
        viewRefrance();
        spin1Method();
        spin2Method();

        MyLeadDetailsService dealerTargetAPI = new MyLeadDetailsService(MyLeadDetails.this, 1);
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
        lead_f_remark = (TextView) findViewById(R.id.lead_f_remark);
        histry_lay = (LinearLayout) findViewById(R.id.histry_lay);
        remark_edt = (EditText) findViewById(R.id.remark_edt);
        convrt_qty = (EditText) findViewById(R.id.convrt_qty);

        date_txt = (TextView) findViewById(R.id.date_txt);
        oner_name_txt = (TextView) findViewById(R.id.oner_name_txt);
        oner_cont_txt = (TextView) findViewById(R.id.oner_cont_txt);
        lead_num = (TextView) findViewById(R.id.lead_num);
        lead_type = (TextView) findViewById(R.id.lead_type);
        gift_required = (TextView) findViewById(R.id.gift_required);
        f_exe = (TextView) findViewById(R.id.f_exe);

    }

    void spin1Method() {
        spin1 = (Spinner) findViewById(R.id.spin1);
        ArrayAdapter aa = new ArrayAdapter(MyLeadDetails.this,android.R.layout.simple_spinner_item,statusAr);
        aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //Setting the ArrayAdapter data on the Spinner
        spin1.setAdapter(aa);
    }

    void spin2Method() {
        spin2 = (Spinner) findViewById(R.id.spin2);
        ArrayAdapter aa = new ArrayAdapter(MyLeadDetails.this,android.R.layout.simple_spinner_item,cnvrtfmAr);
        aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //Setting the ArrayAdapter data on the Spinner
        spin2.setAdapter(aa);
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
                    JSONArray jsonArray = objectData.getJSONArray("leadDetailsList");
                    historyViewList(jsonArray);

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
            lead_f_remark.setText(object.getString("remarks"));

            date_txt.setText(object.getString("lead_crt_dt"));
            oner_name_txt.setText(object.getString("owners_name"));
            oner_cont_txt.setText(object.getString("owners_contact"));
            lead_num.setText(object.getString("lead_num"));
            lead_type.setText(object.getString("lead_type"));
            gift_required.setText(object.getString("gift_required"));
            f_exe.setText(object.getString("exe_sup"));

        }catch (Exception e){Log.e(LOG_DBG,e.toString());}

    }

    void historyViewList(JSONArray hisAr) {
        try {
            histry_lay.removeAllViews();
            for (int i = 0; i < hisAr.length(); i++) {
                LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                View view = inflater.inflate(R.layout.my_lead_dtls_history_cell, null);

                TextView date_txt = (TextView) view.findViewById(R.id.date_txt);
                TextView status_txt = (TextView) view.findViewById(R.id.status_txt);
                TextView cnrt_qty = (TextView) view.findViewById(R.id.cnrt_qty);
                TextView cnrt_frm = (TextView) view.findViewById(R.id.cnrt_frm);
                TextView remark_txt = (TextView) view.findViewById(R.id.remark_txt);

                JSONObject object1 = hisAr.getJSONObject(i);
                date_txt.setText(object1.getString("dt_crt"));
                status_txt.setText(object1.getString("lead_status"));
                cnrt_qty.setText(object1.getString("convert_qty"));
                cnrt_frm.setText(object1.getString("convert_from"));
                remark_txt.setText(object1.getString("remarks"));

                view.setTag(i);
                histry_lay.addView(view);
            }
        }catch (Exception e){Log.e(LOG_DBG,e.toString());}
    }

    String editTextBlankCheck(EditText text){
        if (text.getText().length()<=0){
            return "";
        }else if (text.getText()==null){
            return "";
        }else if (text.getText().equals("")){
            return "";
        }
        return text.getText()+"";
    }

    void JsonPrepareMethod(){
        try {
            JSONObject mainJsonObj = new JSONObject(blankJson);
            String answer1=editTextBlankCheck(remark_edt);
            if (answer1.equals("")){
                Toast.makeText(MyLeadDetails.this,"Please Fill Remark!",Toast.LENGTH_LONG).show();
                return;
            }
            mainJsonObj.put("remarks",answer1);

            String answer2=editTextBlankCheck(convrt_qty);
            if (answer2.equals("")){
                Toast.makeText(MyLeadDetails.this,"Please Enter convert qty!",Toast.LENGTH_LONG).show();
                return;
            }
            mainJsonObj.put("convertQty",answer2);
            mainJsonObj.put("leadStatus",spin1.getSelectedItem());
            mainJsonObj.put("convertFrom",spin2.getSelectedItem());
            mainJsonObj.put("leadId",lead_id);

            saveDatabyServlet saveDatabyServlet=new saveDatabyServlet(MyLeadDetails.this,mainJsonObj);
            saveDatabyServlet.execute();

        }catch (Exception e){Log.e(LOG_DBG,e.toString());}
    }

    /*........... Data Saved on Server by Servlet........*/
    class saveDatabyServlet extends AsyncTask<String,Void,String> {
        private Context context;
        JSONObject answerOnj1=null;
        ProgressDialog progressDialog;
        public saveDatabyServlet(Context context, JSONObject answerOnj1) {
            this.context = context;
            this.answerOnj1=answerOnj1;

        }
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(context);
            progressDialog.setIndeterminate(true);
            progressDialog.setCancelable(false);
            progressDialog.setMessage(context.getString(R.string.Authenticating));
            progressDialog.show();
        }

        @Override
        protected String doInBackground(String... arg0) {
            try{
                String dnsport= GlobalConfiguration.getDomainport();
                String link="http://"+dnsport+"/SteelSales-war/Stl_LeadManagementDtls_C_Api";
                HashMap<String,String> hm=new HashMap<>();
                hm.put("cid",cid);
                hm.put("segid", segid);
                hm.put("data", answerOnj1.toString());
                hm.put("keyCode",tempKeyCode);

                return SteelHelper.performPostCall(link, hm);
            }catch (Exception e){
                return "Exception: " + e.toString();
            }
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            //Log.e("............",s);
            // Dismiss the progress dialog
            if (progressDialog!=null && progressDialog.isShowing())
                progressDialog.dismiss();

            try{
                JSONObject main_template=new JSONObject(s);
                String msg=main_template.getString("msg");
                if (main_template.getString("type").equalsIgnoreCase("Create")) {
                    AlertDialog.Builder alertDialog = new AlertDialog.Builder(MyLeadDetails.this)//set icon
                            .setIcon(R.mipmap.ic_launcher)//set title
                            .setTitle("Alert")//set message
                            .setMessage(msg)//set positive button
                            .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    //set what would happen when positive button is clicked
                                    finish();
                                }
                            });
                    alertDialog.show();
                }else if (main_template.getString("type").equalsIgnoreCase("error")) {
                    AlertDialog.Builder alertDialog = new AlertDialog.Builder(MyLeadDetails.this)//set icon
                            .setIcon(R.mipmap.ic_launcher)//set title
                            .setTitle("Alert")//set message
                            .setMessage(msg)//set positive button
                            .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    //set what would happen when positive button is clicked

                                }
                            });
                    alertDialog.show();
                }else if(msg.contains("Authorization Failure..!")){
                    //Code for logout
                    Toast.makeText(context,"Session expired.",Toast.LENGTH_SHORT).show();
                    Intent logout_intent=new Intent(MyLeadDetails.this, LogoutService.class);
                    startService(logout_intent);
                    finish();
                    return;
                }else{
                    Toast.makeText(context,msg,Toast.LENGTH_SHORT).show();
                }

            }catch (Exception e){
                Log.e("Error...: ",e.toString()+"    111");
                Toast.makeText(context,"Unable to connect to server. Please check your Internet connection.",Toast.LENGTH_SHORT).show();
            }
        }
    }
}
