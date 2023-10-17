package com.aspl.steel;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.aspl.steel.CrashReport.TopExceptionHandler;
import com.aspl.steel.adapters.AutocompleteBaseAdapter;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

/**
 *  Created by Arnab on 29/2/16.
 *  This is for dealer salesman autocomplete
 */
public class DealerNameAutocompleteActivity extends AppCompatActivity {
    final String LOG_DBG=getClass().getSimpleName();
    FetchDealerName mFetchTask;
    AutocompleteBaseAdapter adapter;
    ArrayList<HashMap> arrayList=new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Thread.setDefaultUncaughtExceptionHandler(new TopExceptionHandler(this));
        setContentView(R.layout.activity_dealer_name_autocomplete);
        EditText searchName=(EditText)findViewById(R.id.searchName);

        final View search_empty=findViewById(R.id.search_empty);
        SharedPreferences sharedPref = getSharedPreferences("loginCredentials", Context.MODE_PRIVATE);
        final String cid=sharedPref.getString("cid", "");
        final String segid=sharedPref.getString("segid", "");
        final String sLid;
        if(getIntent()!=null && getIntent().getExtras().getString("sLid")!=null){
            sLid=getIntent().getExtras().getString("sLid");
        }else {
            sLid=sharedPref.getString("storedSlid", "");
        }
        final String tempKeyCode=sharedPref.getString("storedKeyCode","");

        final String type=getIntent().getExtras().getString("type", "");
        ListView lvDealerName=(ListView)findViewById(R.id.lvDealerName);
        lvDealerName.setEmptyView(findViewById(R.id.emptyView));
        adapter=new AutocompleteBaseAdapter(this,arrayList);
        lvDealerName.setAdapter(adapter);
        lvDealerName.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                TextView title=(TextView)view.findViewById(R.id.text1);
                String strTitle=title.getText().toString();
                String idAsTag=title.getTag().toString();
                Intent intent=new Intent();
                intent.putExtra("id",idAsTag);
                intent.putExtra("name",strTitle);
                setResult(RESULT_OK, intent);
                finish();
            }
        });

        searchName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(final Editable s) {
                if (mFetchTask != null) {
                    mFetchTask.cancel(true);
                    mFetchTask = null;
                }
                if(s.length()>=1){
                    search_empty.setVisibility(View.GONE);
                    mFetchTask = new FetchDealerName(DealerNameAutocompleteActivity.this, 1);
                    mFetchTask.execute(cid, segid, sLid, type, s.toString(), tempKeyCode);
                }else {
                    search_empty.setVisibility(View.VISIBLE);
                }
            }
        });
        searchName.requestFocus();
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(searchName, InputMethodManager.SHOW_IMPLICIT);
    }
    public class FetchDealerName extends AsyncTask<String, Void, String> {
        private Context context;
        private int byGetOrPost=0;          //Flag 0 means get and 1 means post. (By default it is get).

        FetchDealerName(Context context,int flag) {
            this.context = context;
            byGetOrPost = flag;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            arrayList.clear();
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
                    String type = arg0[3];
                    String query = arg0[4];
                    String tempKeyCode = arg0[5];
                    String dnsport= GlobalConfiguration.getDomainport();
                    String link="http://"+dnsport+"/SteelSales-war/Stl_Party_SearchByType_C_Api";
                    HashMap<String,String> hm=new HashMap<>();
                    hm.put("cid",cid);
                    hm.put("segid", segid);
                    hm.put("sLid", sLid);
                    hm.put("type", type);
                    hm.put("q", query);
                    hm.put("keyCode", tempKeyCode);

                    return SteelHelper.performPostCall(link,hm);
                }catch (Exception e){
                    return "Exception: " + e.toString();
                }
            }
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            mFetchTask=null;
            try{
                JSONObject mainObj=new JSONObject(result);
                if(mainObj.getString("msg").contains("Authorization Failure")){
                    //Code for logout here
                    Toast.makeText(DealerNameAutocompleteActivity.this,"Session expired.",Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    intent.putExtra("EXIT", true);
                    startActivity(intent);
                    finish();
                }
                else if(mainObj.getString("type").equals("success")){
                    JSONArray data=mainObj.getJSONArray("data");
                    for(int i=0;i<data.length();i++){
                        JSONObject jsonObject=data.getJSONObject(i);
                        String lid=jsonObject.getString("lid");
                        String party_name= jsonObject.getString("party_name");
                        HashMap<String,String> hm=new HashMap<>();
                        hm.put("id",lid);
                        hm.put("title",party_name);
                        arrayList.add(hm);
                    }
                }
                else {
                    Log.e(LOG_DBG,result);
                    throw new Exception("Server error");
                }
            }catch (Exception e){
                Toast.makeText(context,"Unable to connect to server",Toast.LENGTH_SHORT).show();
            }
            adapter.notifyDataSetChanged();
        }
    }
}
