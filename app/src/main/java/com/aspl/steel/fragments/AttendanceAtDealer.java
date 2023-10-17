package com.aspl.steel.fragments;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.CompoundButton;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import com.aspl.steel.GlobalConfiguration;
import com.aspl.steel.LoginActivity;
import com.aspl.steel.R;
import com.aspl.steel.Services.LogoutService;
import com.aspl.steel.SteelHelper;
import com.aspl.steel.adapters.AutoDealerSearchAdp;
import com.aspl.steel.adapters.DealerSearchAutoComp;
import org.json.JSONArray;
import org.json.JSONObject;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class AttendanceAtDealer extends Fragment {

    SimpleDateFormat dateFormatter=new SimpleDateFormat("dd-MM-yyyy");
    SimpleDateFormat dateFormatter1=new SimpleDateFormat("01-MM-yyyy");
    AutoCompleteTextView dealerSearchM;
    Spinner dealerSearchA;
    Switch m_a_swich;
    View rootView;
    DrawerLayout mDrawerLayout;
    final String LOG_DBG=getClass().getSimpleName();
    String  sLid,cid,segid,tempKeyCode,uid;
    FetchDealerName mFetchTask;
    TextView target_txt,target_date;
    //RequestQueue requestQueue = null;
    Date date=new Date();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.attendance_at_dealer, container, false);
        viewSet(rootView);
        SharedPreferences sharedPref = getActivity().getSharedPreferences("loginCredentials", Context.MODE_PRIVATE);
        cid = sharedPref.getString("cid", "");
        segid = sharedPref.getString("segid", "");
        tempKeyCode = sharedPref.getString("storedKeyCode", "");
        sLid = sharedPref.getString("storedSlid", "");
        uid = sharedPref.getString("storedUserId", "");
        TextView saveData = (TextView) rootView.findViewById(R.id.saveData);
        target_txt=(TextView)rootView.findViewById(R.id.target_txt);
        target_date=(TextView)rootView.findViewById(R.id.target_date);
        saveData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (m_a_swich.isChecked()){
                    try {
                        if (dealerSearchM.getText().equals("") && dealerSearchA.getTag().equals("0")){
                            Toast.makeText(getContext(),"Search a Dealer !",Toast.LENGTH_LONG).show();
                            return;
                        }
                        HashMap<String,String> hashMap01= (HashMap) dealerSearchM.getTag();
                        String d_lid=hashMap01.get("lid");
                        new dealerValitationQrAtten((AppCompatActivity) getActivity(),d_lid).execute();
                    }catch (Exception e){
                        Toast.makeText(getContext(),"Please Search a Dealer !",Toast.LENGTH_LONG).show();
                    }
                }else {
                    try {
                        if (dealerSearchA.getSelectedItem().equals("") || dealerSearchA.getSelectedItem() == null) {
                            Toast.makeText(getContext(), "Select a Dealer !", Toast.LENGTH_LONG).show();
                            return;
                        }
                        JSONObject partyObj1 = new JSONObject(String.valueOf(dealerSearchA.getSelectedItem()));
                        String d_lid=partyObj1.getString("lid");
                        new dealerValitationQrAtten((AppCompatActivity) getActivity(),d_lid).execute();
                    }catch (Exception e){
                        Toast.makeText(getContext(), "Please Select a Dealer !", Toast.LENGTH_LONG).show();
                    }
                }

            }
        });
        dealerSearchM = (AutoCompleteTextView) rootView.findViewById(R.id.dealerSearchM);
        dealerSearchA = (Spinner) rootView.findViewById(R.id.dealerSearchA);
        m_a_swich = (Switch) rootView.findViewById(R.id.m_a_swich);
        m_a_swich.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    // The toggle is enabled
                    dealerSearchM.setVisibility(View.VISIBLE);
                    dealerSearchA.setVisibility(View.GONE);
                } else {
                    // The toggle is disabled
                    dealerSearchM.setVisibility(View.GONE);
                    dealerSearchA.setVisibility(View.VISIBLE);
                }
            }
        });
        /*........... AutoSearch Dealer.........*/
        locationTrackingMethod();
        dealerSearchM.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (mFetchTask != null) {
                    mFetchTask.cancel(true);
                    mFetchTask = null;
                }
                if (editable.length() >= 1) {
                    mFetchTask = new FetchDealerName(getContext(), 1);
                    mFetchTask.execute(cid, segid, sLid, "dealer", editable.toString(), tempKeyCode);
                }

            }
        });
        dealerSearchA.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?>arg0, View view, int arg2, long arg3) {
                try {
                    JSONObject partyObj1 = new JSONObject(String.valueOf(dealerSearchA.getSelectedItem()));
                    String d_lid = partyObj1.getString("lid");
                    DealerTargetAPI dealerTargetAPI = new DealerTargetAPI(getContext(), 1);
                    dealerTargetAPI.execute(cid, segid, partyObj1.getString("party_id"), tempKeyCode);
                   // saleQTyAPI(partyObj1.getString("party_name"));

                }catch (Exception e){}
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
                // TODO Auto-generated method stub

            }
        });

        return rootView;
    }

        private void viewSet (View rootView){
            Toolbar mToolbar = (Toolbar) rootView.findViewById(R.id.my_actionbar_toolbar);
            if (mToolbar != null) {
                ((AppCompatActivity) getActivity()).setSupportActionBar(mToolbar);
                mDrawerLayout = (DrawerLayout) getActivity().findViewById(R.id.drawer_layout);
                //This draws the menu icon on the title that looks like 3 horizontal lines(=)
                ActionBarDrawerToggle mDrawerToggle = new ActionBarDrawerToggle(
                        getActivity(), mDrawerLayout, mToolbar,
                        R.string.navigation_drawer_open, R.string.navigation_drawer_close
                );
                mDrawerLayout.setDrawerListener(mDrawerToggle);
                mDrawerToggle.syncState();
            }

        }

    void locationTrackingMethod() {
        GPSTracker gpsTracker = new GPSTracker(getContext());
        if (!gpsTracker.canGetLocation()) {
            Toast.makeText(getContext(), "GPS is disabled. You will be log out", Toast.LENGTH_SHORT).show();
            return;

        }else {
            /*............ Dealer List Call by Location.........*/
            dealerListbyLocation dealerListbyLocation=new dealerListbyLocation(getContext(),gpsTracker.latitude, gpsTracker.longitude);
            dealerListbyLocation.execute();
        }
    }

    /*.............. Dealer List by Location...........*/
    class dealerListbyLocation extends AsyncTask<String,Void,String> {
        private Context context;
        ProgressDialog progressDialog;
        double latitude;
        double longitude;
        public dealerListbyLocation(Context context,double latitude,double longitude) {
            this.context = context;
            this.latitude=latitude;
            this.longitude=longitude;

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
                String link="http://"+dnsport+"/SteelSales-war/Stl_Party_SearchByGPS_C_Api";
                HashMap<String,String> hm=new HashMap<>();
                hm.put("cid",cid);
                hm.put("segid", segid);
                hm.put("sLid", sLid);    //or uid ask
                hm.put("type", "dealer");
                hm.put("keyCode",tempKeyCode);
                hm.put("q", "%");
                hm.put("currentLat", latitude+"");
                hm.put("currentlong", longitude+"");

                return SteelHelper.performPostCall(link, hm);
            }catch (Exception e){
                return "Exception: " + e.toString();
            }
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            //Log.e(".....11.....",s);
            // Dismiss the progress dialog
            if (progressDialog!=null && progressDialog.isShowing())
                progressDialog.dismiss();

            if(s.startsWith("Exception:")){
                Toast.makeText(context,"Unable to connect to server.",Toast.LENGTH_LONG).show();
                try{
                    if(SteelHelper.isNetworkAvailable((AppCompatActivity) getActivity())) {
                        getActivity().findViewById(R.id.serverUnreachable).setVisibility(View.VISIBLE);
                    }else {
                        getActivity().findViewById(R.id.internetUnreachable).setVisibility(View.VISIBLE);
                    }
                }catch (Exception e){}
            }else {
                try{
                    getActivity().findViewById(R.id.serverUnreachable).setVisibility(View.GONE);
                    getActivity().findViewById(R.id.internetUnreachable).setVisibility(View.GONE);
                }catch (Exception e){}
            }
            try{
                JSONObject main_template=new JSONObject(s);
                String msg=main_template.getString("msg");
                if(msg.contains("Authorization Failure..!")){
                    //Code for logout
                    Toast.makeText(context,"Session expired.",Toast.LENGTH_SHORT).show();
                    Intent logout_intent=new Intent(getActivity(), LogoutService.class);
                    getActivity().startService(logout_intent);
                    getActivity().finish();
                    return;
                }
                if (main_template.getString("type").equalsIgnoreCase("success")){
                    JSONArray dealerList=main_template.getJSONArray("data");
                    if (dealerList.length()>0) {
                        AutoDealerSearchAdp autoDealerSearchAdp = new AutoDealerSearchAdp(getContext(), dealerList);
                        dealerSearchA.setAdapter(autoDealerSearchAdp);
                    }else{
                        Toast.makeText(context,main_template.getString("msg"),Toast.LENGTH_SHORT).show();
                    }
                }else{
                    Toast.makeText(context,main_template.getString("msg"),Toast.LENGTH_SHORT).show();
                }
            }catch (Exception e){
                Log.e("..........",e.toString());
            }

        }
    }

    /*.......... Manual Dealer Search..........*/
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
            //Log.e("........",result);
            mFetchTask=null;
            try{
                JSONObject mainObj=new JSONObject(result);
                if(mainObj.getString("msg").contains("Authorization Failure")){
                    //Code for logout here
                    Toast.makeText(getContext(),"Session expired.",Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(getActivity(), LoginActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    intent.putExtra("EXIT", true);
                    startActivity(intent);
                    getActivity().finish();

                }else if(mainObj.getString("type").equals("success")){
                    JSONArray data=mainObj.getJSONArray("data");
                    ArrayList<HashMap<String,String>> searchArrayList= new ArrayList<>();
                    for (int i=0; i<data.length(); i++){
                        JSONObject object12=data.getJSONObject(i);
                        HashMap<String,String> hashMap=new HashMap<>();
                        hashMap.put("lid",object12.getString("lid"));
                        hashMap.put("party_id",object12.getString("party_id"));
                        hashMap.put("party_name",object12.getString("party_name"));
                        hashMap.put("party_type_name",object12.getString("party_type_name"));
                        searchArrayList.add(hashMap);
                    }
                    DealerSearchAutoComp adapter = new DealerSearchAutoComp(getContext(), android.R.layout.simple_dropdown_item_1line, searchArrayList);
                    dealerSearchM.setAdapter(adapter);
                    dealerSearchM.setOnItemClickListener(onItemClickListener);
                    adapter.notifyDataSetChanged();
                }else {
                    Log.e(LOG_DBG,result);
                    throw new Exception("Server error");
                }
            }catch (Exception e){
                Toast.makeText(context,"Unable to connect to server",Toast.LENGTH_SHORT).show();
            }

        }

        private AdapterView.OnItemClickListener onItemClickListener =
                new AdapterView.OnItemClickListener(){
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                        HashMap<String,String> hashMap12= (HashMap<String, String>) adapterView.getItemAtPosition(i);
                        dealerSearchM.setText(hashMap12.get("party_name"));
                        dealerSearchM.setTag(hashMap12);
                        DealerTargetAPI dealerTargetAPI = new DealerTargetAPI(getContext(), 1);
                        dealerTargetAPI.execute(cid, segid, hashMap12.get("party_id"), tempKeyCode);
                      //  saleQTyAPI(hashMap12.get("party_name"));
                    }
                };

    }

    class dealerValitationQrAtten extends AsyncTask<String, Void, String> {
        private AppCompatActivity context;
        String dealer_id;
        ProgressDialog progressDialog;

        public dealerValitationQrAtten(AppCompatActivity context,String dealer_id) {
            this.context = context;
            this.dealer_id = dealer_id;
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
            try {
                String dnsport = GlobalConfiguration.getDomainport();
                String link = "http://" + dnsport + "/SteelSales-war/Stl_Track_Salesman_Loc_Api";
                HashMap<String, String> postDataParams = new HashMap<>();

                postDataParams.put("latitude", "0.00");
                postDataParams.put("longitude", "0.00");
                postDataParams.put("location", "");
                postDataParams.put("distance", "0.00");
                postDataParams.put("dealer_id", dealer_id);
                postDataParams.put("status", "3");
                postDataParams.put("difference", "0.00");
                postDataParams.put("cid", cid);
                postDataParams.put("segid", segid);
                postDataParams.put("uid", uid);
                postDataParams.put("sLid", sLid);
                postDataParams.put("keyCode", tempKeyCode);

                return SteelHelper.performPostCall(link, postDataParams);
            } catch (Exception e) {
                return "Exception: " + e.toString();
            }
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            Log.e("Response:  ", s.toString());
            if (progressDialog != null && progressDialog.isShowing())
                progressDialog.dismiss();
            try {
                JSONObject jsonObject = new JSONObject(s);
                if (jsonObject.getString("type").equals("success")) {
                    AlertDialog.Builder alertDialog = new AlertDialog.Builder(getContext())//set icon
                            .setIcon(R.mipmap.ic_launcher)//set title
                            .setTitle("Alert")//set message
                            .setMessage("Successfully data Saved")//set positive button
                            .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    //set what would happen when positive button is clicked
                                    removeFragmentMethod();
                                }
                            });
                    alertDialog.show();
                } else {
                    throw new Exception("Type error");
                }
            } catch (Exception e) {
                Log.e("......", e.getLocalizedMessage());
                Toast.makeText(context, "Error connecting to server", Toast.LENGTH_SHORT).show();
            }
        }
    }

    void removeFragmentMethod() {
        // getFragmentManager().beginTransaction().remove(ScannedBarcodeActivity.this).commit();
        getFragmentManager().popBackStack();
    }

    /*.......... DealerTargetAPI Search..........*/
    public class DealerTargetAPI extends AsyncTask<String, Void, String> {
        private Context context;
        private int byGetOrPost=0;          //Flag 0 means get and 1 means post. (By default it is get).

        DealerTargetAPI(Context context,int flag) {
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
                    String dealerId = arg0[2];
                    String tempKeyCode = arg0[3];
                    String dnsport= GlobalConfiguration.getDomainport();
                    String link="http://"+dnsport+"/SteelSales-war/Stl_Dealer_Target_ByDt_Api";
                    HashMap<String,String> hm=new HashMap<>();
                    hm.put("cid",cid);
                    hm.put("segid", segid);
                    hm.put("dealer_id", dealerId);
                    hm.put("keyCode", tempKeyCode);

                    return SteelHelper.performPostCall(link,hm);
                }catch (Exception e){
                    return "Exception: " + e.toString();
                }
            }
        }

        @Override
        protected void onPostExecute(String result) {
            //Log.e(".........",result);
            super.onPostExecute(result);
            try{
                JSONObject mainObj=new JSONObject(result);
                if(mainObj.getString("msg").contains("Authorization Failure")){
                    //Code for logout here
                    Toast.makeText(getContext(),"Session expired.",Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(getActivity(), LoginActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    intent.putExtra("EXIT", true);
                    startActivity(intent);
                    getActivity().finish();

                }else if(mainObj.getString("type").equals("success")){
                    JSONObject data=mainObj.getJSONObject("data");
                    target_txt.setText(data.getString("target_qty")+""+data.getString("target_unit"));
                    //target_date.setText(data.getString("fetch_dt"));
                }else {
                    Log.e(LOG_DBG,result);
                    throw new Exception("Server error");
                }
            }catch (Exception e){
                Toast.makeText(context,"Unable to connect to server",Toast.LENGTH_SHORT).show();
            }

        }

    }

    ProgressDialog progressview(){
        ProgressDialog progressDialog;
        progressDialog = new ProgressDialog(getContext());
        progressDialog.setIndeterminate(true);
        progressDialog.setCancelable(false);
        progressDialog.setMessage(getContext().getString(R.string.Authenticating));
        progressDialog.show();
        return progressDialog;
    }


//    void saleQTyAPI(String partyName){
//        final ProgressDialog progressDialog=progressview();
//        String link = "http://api.realbooks.in/AMSSERVICE/api/invreport/psRegVoucherRegWise/1089/1089/"+dateFormatter1.format(date)+"/"+dateFormatter.format(date);
//        try {
//            if(requestQueue==null) {
//                requestQueue = Volley.newRequestQueue(getContext());
//            }
//
//            JSONObject jsonEntityData= new JSONObject();
//            jsonEntityData.put("secretKey", "M400LABCZXASW2FGLMBVCAX310OK");
//            jsonEntityData.put("accessKey", "M400LABCZXASW2FGLMBVCAX310OK");
//            jsonEntityData.put("vtype", "Sale");
//            jsonEntityData.put("partyName", partyName);
////            Log.e(".......",link);
////            Log.e("......111.....",jsonEntityData.toString());
//
//            JsonObjectRequest request=new JsonObjectRequest(Request.Method.POST, link, jsonEntityData, new Response.Listener<JSONObject>() {
//                @Override
//                public void onResponse(JSONObject response) {
//                    try{
//                        if (progressDialog!=null && progressDialog.isShowing())
//                            progressDialog.dismiss();
//
//                        //Log.e("Response=====> ",response.toString());
//                        if(response.getString("type").equalsIgnoreCase("success")){
//
//                            JSONArray data1=response.getJSONArray("data");
//                            double qty=0;
//                            for (int i=0; i<data1.length(); i++){
//                                JSONObject object1=data1.getJSONObject(i);
//                                JSONArray data2= object1.getJSONArray("data");
//                                for (int j=0; j<data2.length(); j++){
//                                    JSONObject object2= data2.getJSONObject(j);
//                                    qty+=object2.getDouble("qty");
//                                }
//                            }
//                            target_date.setText(qty+" M.T");
//
//                        }else if (response.getString("type").equalsIgnoreCase("error")) {
//                            String message=response.getString("msg");
//                            AlertDialog.Builder alertDialog = new AlertDialog.Builder(getContext())//set icon
//                                    .setIcon(R.mipmap.ic_launcher)//set title
//                                    .setTitle("Alert")//set message
//                                    .setMessage(message)//set positive button
//                                    .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
//                                        @Override
//                                        public void onClick(DialogInterface dialogInterface, int i) {
//                                            //set what would happen when positive button is clicked
//
//                                        }
//                                    });
//                            alertDialog.show();
//
//                        }else if(response.getString("type").equalsIgnoreCase("autherror")){
//
//                            Toast.makeText(getContext(),"Session expired.",Toast.LENGTH_SHORT).show();
//                            Intent intent = new Intent(getActivity(), LoginActivity.class);
//                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                            intent.putExtra("EXIT", true);
//                            startActivity(intent);
//                            getActivity().finish();
//
//                        }else{
//                            String message=response.getString("msg");
//                            //RealBooksHelper.ShowPopup(myDialog,"Error",message,"e");
//                            Toast.makeText(getContext(),message,Toast.LENGTH_SHORT).show();
//                        }
//                    }catch (Exception e){
//                        Log.e("....","heloooooooo"+e.getLocalizedMessage());
//                        String message="That's an error Exception";
//                        Toast.makeText(getContext(),message,Toast.LENGTH_SHORT).show();
//
//                    }
//                }
//            },new Response.ErrorListener() {
//                @Override
//                public void onErrorResponse(VolleyError error) {
//                    Log.e(".....","helloooo12...."+error);
//                    Toast.makeText(getContext(),"Unable to connect to server",Toast.LENGTH_SHORT).show();
//                }
//            }){
//                @Override
//                public Map<String, String> getHeaders() {
//                    Map<String, String> params = new HashMap<String, String>();
//                    params.put("accountName", "bma");
//                    return params;
//                }
//            };
//            request.setRetryPolicy(new DefaultRetryPolicy(300000,DefaultRetryPolicy.DEFAULT_MAX_RETRIES,DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
//            requestQueue.add(request);
//        }catch (Exception ignored){
//            Log.e(".....","helloooo11...."+ignored+"");
//            Toast.makeText(getContext(),"Unable to connect to server",Toast.LENGTH_SHORT).show();
//        }
//    }

}
