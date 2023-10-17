package com.aspl.steel.fragments;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.DatePicker;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;

import com.aspl.steel.Controller;
import com.aspl.steel.DealerNameAutocompleteActivity;
import com.aspl.steel.GlobalConfiguration;
import com.aspl.steel.R;
import com.aspl.steel.RequisitionEntryActivity;
import com.aspl.steel.Services.LogoutService;
import com.aspl.steel.SteelHelper;
import com.aspl.steel.adapters.lvRequisitionAdapter;
import com.baoyz.swipemenulistview.SwipeMenu;
import com.baoyz.swipemenulistview.SwipeMenuCreator;
import com.baoyz.swipemenulistview.SwipeMenuItem;
import com.baoyz.swipemenulistview.SwipeMenuListView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

/**
 *  Created by Arnab Kar on 17 June 2016.
 */
public class RequisitionListingFragment extends Fragment {
    String LOG_DBG=this.getClass().getSimpleName();
    DrawerLayout mDrawerLayout;
    ProgressDialog progressDialog;
    Date date=new Date();
    SimpleDateFormat dateFormatter=new SimpleDateFormat("dd-MM-yyyy",Locale.getDefault());
    private String cid,segid,tempKeyCode,sLid,uid,userType;
    int REQUEST_SALESMAN_AUTOCOMPLETE=41,REQUEST_REQUISITION_ENTRY=23;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SharedPreferences sharedPref = getActivity().getSharedPreferences("loginCredentials", Context.MODE_PRIVATE);
        cid=sharedPref.getString("cid", "");
        segid=sharedPref.getString("segid", "");
        tempKeyCode=sharedPref.getString("storedKeyCode","");
        sLid=sharedPref.getString("storedSlid","");
        uid=sharedPref.getString("storedUserId","");
        userType=sharedPref.getString("storedUserType","");

    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_requisition_listing, container, false);
        initLogin(rootView);
        return rootView;
    }
    private void initLogin(final View rootView) {
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
        ((TextView) rootView.findViewById(R.id.screenTitle)).setText("Requisition");

        //hide the search bar if salesman else hide
        if(userType.equals("Salesman")){
            rootView.findViewById(R.id.search_salesman).setVisibility(View.GONE);
        }else {
            rootView.findViewById(R.id.search_salesman).setVisibility(View.VISIBLE);
        }
        if(getArguments()!=null && getArguments().getString("salesman_name")!=null && getArguments().getString("salesman_id")!=null){
            rootView.findViewById(R.id.search_salesman).setTag(getArguments().getString("salesman_id"));
            ((TextView)rootView.findViewById(R.id.search_salesman)).setText(getArguments().getString("salesman_name"));
            sLid=getArguments().getString("salesman_id");
        }else {
            rootView.findViewById(R.id.search_salesman).setTag(null);
            ((TextView)rootView.findViewById(R.id.search_salesman)).setText("");
        }
        if(getArguments()!=null && getArguments().getString("dt")!=null) {
            String dt = getArguments().getString("dt");
            ((TextView)rootView.findViewById(R.id.right_title)).setText(dt);
        }else {
            ((TextView)rootView.findViewById(R.id.right_title)).setText(dateFormatter.format(date));
        }
        final String dt=((TextView) rootView.findViewById(R.id.right_title)).getText().toString();
        if(getArguments()==null){
            FetchRequisitionTemplate fetchDailySaleEntryTemplate= new FetchRequisitionTemplate((AppCompatActivity) getActivity(),cid,segid,tempKeyCode,true,sLid);
            fetchDailySaleEntryTemplate.execute(dt);
        }
        else if(!getArguments().getBoolean("backDateEdit")){
            rootView.findViewById(R.id.fab).setEnabled(false);
            rootView.findViewById(R.id.status_bar).setVisibility(View.GONE);
            rootView.findViewById(R.id.right_title).setOnClickListener(null);
            FetchRequisitionTemplate fetchDailySaleEntryTemplate= new FetchRequisitionTemplate((AppCompatActivity) getActivity(),cid,segid,tempKeyCode,false,sLid);
            fetchDailySaleEntryTemplate.execute(dt);
        }else {
            FetchRequisitionTemplate fetchDailySaleEntryTemplate= new FetchRequisitionTemplate((AppCompatActivity) getActivity(),cid,segid,tempKeyCode,true,sLid);
            fetchDailySaleEntryTemplate.execute(dt);
        }
        rootView.findViewById(R.id.right_title).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                Calendar newCalender = Calendar.getInstance();
                DatePickerDialog dialog = new DatePickerDialog(getActivity(), new DatePickerDialog.OnDateSetListener() {

                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        Calendar newDate = Calendar.getInstance();
                        newDate.set(year, monthOfYear, dayOfMonth);
                        ((TextView) v).setText(dateFormatter.format(newDate.getTime()));
                        String dt=dateFormatter.format(newDate.getTime());
                        ((TextView)rootView.findViewById(R.id.txtNarration)).setText("");

                        new BackdatedEditCheck((AppCompatActivity) getActivity(),cid,segid,tempKeyCode,dt).execute();
                    }

                }, newCalender.get(Calendar.YEAR), newCalender.get(Calendar.MONTH), newCalender.get(Calendar.DAY_OF_MONTH));
                dialog.setTitle("Select Date:");
                Calendar cal = Calendar.getInstance();
                cal.setTime(new Date());
                cal.add(Calendar.MONTH, -1);
                dialog.show();
            }
        });
        rootView.findViewById(R.id.search_salesman).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(getActivity(), DealerNameAutocompleteActivity.class);
                intent.putExtra("type", "Salesman");
                getActivity().startActivityForResult(intent, REQUEST_SALESMAN_AUTOCOMPLETE);
            }
        });

        /*
        todo: Review this code before final delete
        ListView lvRequisition=(ListView)rootView.findViewById(R.id.lvRequisition);
        final lvRequisitionAdapter adapter=new lvRequisitionAdapter(getActivity(),new JSONArray());
        lvRequisition.setAdapter(adapter);
        rootView.findViewById(R.id.clear_input).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rootView.findViewById(R.id.search_salesman).setTag(null);
                ((TextView) rootView.findViewById(R.id.search_salesman)).setText("");
            }
        });*/

        rootView.findViewById(R.id.performSearch).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new BackdatedEditCheck((AppCompatActivity) getActivity(),cid,segid,tempKeyCode,dt).execute();
            }
        });
        rootView.findViewById(R.id.save).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                JSONObject requisitionMainTemplate = ((Controller) getActivity().getApplication()).requisitionMainTemplate;
                String dt = ((TextView) rootView.findViewById(R.id.right_title)).getText().toString();
                String narration=((TextView) rootView.findViewById(R.id.txtNarration)).getText().toString();

                if(rootView.findViewById(R.id.search_salesman).getTag()==null || rootView.findViewById(R.id.search_salesman).getTag().equals("0")){
                    Toast.makeText(getActivity(),"Only salesman data can be saved",Toast.LENGTH_SHORT).show();
                    return;
                }
                if(((ListView)rootView.findViewById(R.id.lvRequisition)).getAdapter().isEmpty()){
                    Toast.makeText(getActivity(),"Nothing to save",Toast.LENGTH_SHORT).show();
                    return;
                }
                postProcess(requisitionMainTemplate, dt,narration);
                SaveTask saveTask = new SaveTask(getActivity(), 1,dt);
                saveTask.execute(requisitionMainTemplate.toString());
            }
        });
        rootView.findViewById(R.id.newEntry).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TextView tvSearch=(TextView)rootView.findViewById(R.id.search_salesman);

                if(sLid.equals("0") && (tvSearch.getTag()==null||tvSearch.getText().toString().equals(""))){
                    Toast.makeText(getContext(),"Select salesman first",Toast.LENGTH_SHORT).show();
                }else {
                    JSONObject requisitionMainTemplate = ((Controller) getActivity().getApplication()).requisitionMainTemplate;
                    JSONObject templateToEdit;
                    if (requisitionMainTemplate == null) {
                        Toast.makeText(getContext(), "Error fetching data", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    try {
                        templateToEdit = requisitionMainTemplate.getJSONObject("invVoucherObj").getJSONObject("invVdetailObj");
                    } catch (Exception e) {
                        Toast.makeText(getContext(), "Error fetching data", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    Intent intent = new Intent(getContext(), RequisitionEntryActivity.class);
                    intent.putExtra("EditFlag", false);
                    intent.putExtra("position",((ListView)rootView.findViewById(R.id.lvRequisition)).getCount());
                    intent.putExtra("templateJsonString", templateToEdit.toString());
                    if(getActivity().findViewById(R.id.search_salesman).getVisibility()==View.VISIBLE){
                        intent.putExtra("sLid",getActivity().findViewById(R.id.search_salesman).getTag().toString());
                    }
                    getActivity().startActivityForResult(intent, REQUEST_REQUISITION_ENTRY);
                }
            }
        });
    }

    class SaveTask extends AsyncTask<String,Void,String> {
        private Context context;
        private int byGetOrPost=0;          //Flag 0 means get and 1 means post. (By default it is get).
        String dt;

        public SaveTask(Context context,int flag,String dt) {
            this.context = context;
            byGetOrPost = flag;
            this.dt=dt;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(context);
            progressDialog.setIndeterminate(true);
            progressDialog.setCancelable(false);
            progressDialog.setMessage(getResources().getString(R.string.Authenticating));
            progressDialog.show();
        }

        @Override
        protected String doInBackground(String... params) {
            if(byGetOrPost == 0){ //means by Get Method
                throw new UnsupportedOperationException(this.getClass().getSimpleName() + " -> Get disabled for security reasons");
            }
            else{
                try{
                    String data = params[0];
                    JSONObject dataJson=new JSONObject(data);
                    data=dataJson.getJSONObject("invVoucherObj").toString();
                    String dnsport= GlobalConfiguration.getDomainport();
                    String link="http://"+dnsport+"/SteelSales-war/Stl_Transaction_Entry_Edit_Delete_Api";
                    HashMap<String,String> hm=new HashMap<>();
                    hm.put("uid",uid);
                    hm.put("data", data);
                    hm.put("keyCode", tempKeyCode);
                    //Log.e(LOG_DBG,"........    "+data);

//                    int maxLogSize = 1000;
//                    for(int i = 0; i <= data.length() / maxLogSize; i++) {
//                        int start = i * maxLogSize;
//                        int end = (i+1) * maxLogSize;
//                        end = end > data.length() ? data.length() : end;
//                        Log.e("........", data.substring(start, end));
//                    }

                    return SteelHelper.performPostCall(link,hm);
                }catch (Exception e){
                    return "Exception: " + e.toString();
                }
            }
        }

        @Override
        protected void onPostExecute(String result) {
            JSONObject resultJson;
            String successFlag,msg;

            // Dismiss the progress dialog
            if (progressDialog!=null && progressDialog.isShowing())
                progressDialog.dismiss();

            try{
                resultJson=new JSONObject(result);
                successFlag=resultJson.getString("type");
                msg=resultJson.getString("msg");
            }catch (Exception e){
                successFlag="";
                msg="";
            }
            if(successFlag.equals("success")){
                Toast.makeText(context,"Data saved",Toast.LENGTH_SHORT).show();
                new BackdatedEditCheck((AppCompatActivity) getActivity(),cid,segid,tempKeyCode,dt).execute();

            }else if(msg.contains("Authorization Failure")){
                Intent logout_intent=new Intent(getActivity(), LogoutService.class);
                getActivity().startService(logout_intent);
                getActivity().finish();
            }
            else {
                Toast.makeText(context,"Failed to save data",Toast.LENGTH_LONG).show();
                new BackdatedEditCheck((AppCompatActivity) getActivity(),cid,segid,tempKeyCode,dt).execute();
            }
        }
    }
    class BackdatedEditCheck extends AsyncTask<String,Void,String>{
        private AppCompatActivity context;
        ProgressDialog progressDialog;
        String cid,segid,tempKeyCode,dt;
        public BackdatedEditCheck(AppCompatActivity context, String cid, String segid, String tempKeyCode, String dt) {
            this.context = context;
            this.cid=cid;
            this.segid=segid;
            this.tempKeyCode=tempKeyCode;
            this.dt=dt;
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
                String link="http://"+dnsport+"/SteelSales-war/Stl_BackDateCheck_Api";
                HashMap<String,String> hm=new HashMap<>();
                hm.put("cid",cid);
                hm.put("segid", segid);
                hm.put("uid", uid);
                hm.put("date",dt);
                hm.put("type","back_dt_edit");
                hm.put("keyCode",tempKeyCode);
                return SteelHelper.performPostCall(link, hm);
            }catch (Exception e){
                return "Exception: " + e.toString();
            }
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            //Log.e("backdate",s+" on date "+dt);
            // Dismiss the progress dialog
            if (progressDialog!=null && progressDialog.isShowing())
                progressDialog.dismiss();
            try{
                JSONObject jsonObject=new JSONObject(s);
                if(jsonObject.getString("type").equals("success")){
                    if(jsonObject.getString("msg").equals("true")){
                        //Backdated Entry allowed for this date
                        getActivity().findViewById(R.id.clickDisabled).setVisibility(View.GONE);

                        if(getActivity().findViewById(R.id.search_salesman).getTag()!=null){
                            sLid=getActivity().findViewById(R.id.search_salesman).getTag().toString();
                        }
                        FetchRequisitionTemplate fetchDailySaleEntryTemplate= new FetchRequisitionTemplate((AppCompatActivity) getActivity(),cid,segid,tempKeyCode,true,sLid);
                        fetchDailySaleEntryTemplate.execute(dt);
                    }
                    else {
                        getActivity().findViewById(R.id.clickDisabled).setVisibility(View.VISIBLE);
                        Toast.makeText(context,"Backdate Edit disabled",Toast.LENGTH_SHORT).show();
                    }
                }
                else {
                    throw new Exception("Type error");
                }
            }catch (Exception e){
                Log.e(LOG_DBG,e.getLocalizedMessage());
                Toast.makeText(context,"Error connecting to server",Toast.LENGTH_SHORT).show();
            }
        }
    }
    class FetchRequisitionTemplate extends AsyncTask<String,Void,String> {
        private AppCompatActivity context;
        ProgressDialog progressDialog;
        String cid,segid,tempKeyCode;
        boolean clickable;
        String sLidFromSearch;

        public FetchRequisitionTemplate(AppCompatActivity context, String cid, String segid, String tempKeyCode, boolean clickable, String sLidFromSearch) {
            this.context = context;
            this.cid=cid;
            this.segid=segid;
            this.tempKeyCode=tempKeyCode;
            this.clickable=clickable;
            this.sLidFromSearch=sLidFromSearch;
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
                String dt = arg0[0];
                String dnsport= GlobalConfiguration.getDomainport();
                String link="http://"+dnsport+"/SteelSales-war/Stl_RequisitionTxn_DuplicateChecking_Api";
                HashMap<String,String> hm=new HashMap<>();
                hm.put("cid",cid);
                hm.put("segid", segid);
                hm.put("sLid", sLidFromSearch);
                hm.put("dt",dt);
                hm.put("keyCode",tempKeyCode);
                String result=SteelHelper.performPostCall(link, hm);
                return result;
            }catch (Exception e){
                return "Exception: " + e.toString();
            }
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            // Dismiss the progress dialog
            if (progressDialog!=null && progressDialog.isShowing())
                progressDialog.dismiss();
            try{
                JSONObject main_template=new JSONObject(s);
                if(main_template.getString("msg").contains("Authorization Failure..!")){
                    Toast.makeText(context,"Session Expired.",Toast.LENGTH_SHORT).show();
                    Intent logout_intent=new Intent(getActivity(), LogoutService.class);
                    getActivity().startService(logout_intent);
                    getActivity().finish();
                }
                ((Controller) getActivity().getApplication()).requisitionMainTemplate= main_template;

                InvVtypeLoaderTask invVtypeLoaderTask=new InvVtypeLoaderTask(getActivity(),1,clickable);
                invVtypeLoaderTask.execute("requisition");
                getActivity().findViewById(R.id.newEntry).setEnabled(true);
                getActivity().findViewById(R.id.serverUnreachable).setVisibility(View.GONE);
                getActivity().findViewById(R.id.internetUnreachable).setVisibility(View.GONE);
            }catch (Exception e){
                //((Controller) getActivity().getApplication()).requisitionMainTemplate=null;
                try {
                    Log.e(LOG_DBG, e.getLocalizedMessage());
                    Toast.makeText(context, "Unable to connect to server", Toast.LENGTH_SHORT).show();
                    getActivity().findViewById(R.id.newEntry).setEnabled(false);
                    if(SteelHelper.isNetworkAvailable((AppCompatActivity) getActivity())){
                        getActivity().findViewById(R.id.serverUnreachable).setVisibility(View.VISIBLE);
                    } else {
                        getActivity().findViewById(R.id.internetUnreachable).setVisibility(View.VISIBLE);
                    }
                }catch (Exception innerExcep){
                }
            }
        }
    }
    private void postProcess(JSONObject mainJsonObj,String dt,String narration){
        String type;
        try {
            type = mainJsonObj.getString("type");
            JSONObject invVoucherObj=mainJsonObj.getJSONObject("invVoucherObj");
            invVoucherObj.put("dt",dt);
            invVoucherObj.put("ivnum","");
            invVoucherObj.put("vid",0);
            invVoucherObj.put("narration",narration);
            invVoucherObj.put("text4",sLid);
            invVoucherObj.put("text3","open");
            //invVoucherObj.put("text5",dealerId);
            JSONObject invVdetailObj=invVoucherObj.getJSONObject("invVdetailObj");
            invVdetailObj.put("batchId",0);
            invVdetailObj.put("amt",0);
            JSONObject invQdetailObj=invVdetailObj.getJSONObject("invQdetailObj");
            invQdetailObj.put("cunitId",0);
            invQdetailObj.put("rate",0);
            invQdetailObj.put("rateBasis",0);
            invQdetailObj.put("disc",0.0f);
            invQdetailObj.put("cqty",1.0f);
            invQdetailObj.put("sqty", new BigDecimal(0));

        }catch (Exception e){
            type="";
            Log.e(LOG_DBG,e.getLocalizedMessage());
        }
        if (!type.equals("success")) {
            mainJsonObj=null;
        }
        ((Controller) getActivity().getApplication()).requisitionMainTemplate=mainJsonObj;
    }
    class InvVtypeLoaderTask extends AsyncTask<String,Void,String>{
        ProgressDialog progressDialog1;
        private Context context;
        private int byGetOrPost=0;          //Flag 0 means get and 1 means post. (By default it is get).
        boolean clickable;

        public InvVtypeLoaderTask(Context context,int flag,boolean clickable) {
            this.context = context;
            byGetOrPost = flag;
            this.clickable=clickable;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog1 = new ProgressDialog(context);
            progressDialog1.setIndeterminate(true);
            progressDialog1.setCancelable(false);
            progressDialog1.setMessage(getResources().getString(R.string.Authenticating));
            progressDialog1.show();
        }
        @Override
        protected String doInBackground(String... params) {
            if(byGetOrPost == 0){ //means by Get Method
                throw new UnsupportedOperationException(this.getClass().getSimpleName() + " -> Get disabled for security reasons");
            }
            else{
                try{
                    String name = params[0];
                    String dnsport= GlobalConfiguration.getDomainport();
                    String link="http://"+dnsport+"/SteelSales-war/Stl_InvVtype_C_Api";
                    HashMap<String,String> hm=new HashMap<>();
                    hm.put("cid",cid);
                    hm.put("segid", segid);
                    hm.put("name",name);
                    hm.put("keyCode", tempKeyCode);
                    return SteelHelper.performPostCall(link,hm);
                }catch (Exception e){
                    return "Exception: " + e.toString();
                }
            }
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            //Log.e("...........",s);
            // Dismiss the progress dialog
            try{
                if (progressDialog1!=null && progressDialog1.isShowing())
                    progressDialog1.dismiss();
            }catch (Exception e){
                return;
            }

            JSONObject invVtypeObj;
            try{
                invVtypeObj=new JSONObject(s).getJSONArray("data").getJSONObject(0);
            }catch (Exception e){
                invVtypeObj=new JSONObject();
                Log.e(LOG_DBG,e.getLocalizedMessage());
            }
            JSONObject requisitionMainTemplate=((Controller) getActivity().getApplication()).requisitionMainTemplate;
            try {
                requisitionMainTemplate.getJSONObject("invVoucherObj").put("invVtype",invVtypeObj);
            }catch (Exception e){
                Log.e(LOG_DBG,e.getLocalizedMessage());
            }
            SwipeMenuListView lvRequisition=(SwipeMenuListView)getActivity().findViewById(R.id.lvRequisition);
            TextView tvNarration=(TextView)getActivity().findViewById(R.id.txtNarration);
            updateRequisitionList(lvRequisition,clickable,tvNarration);

        }
        private void updateRequisitionList(final SwipeMenuListView lvRequisition, final boolean clickable, final TextView tvNarration){
            final JSONObject requisitionMainTemplate=((Controller) getActivity().getApplication()).requisitionMainTemplate;
            //Log.e("..........",requisitionMainTemplate.toString());
            try {
                JSONObject invVoucherObj=requisitionMainTemplate.getJSONObject("invVoucherObj");
                JSONArray invVdetailList=invVoucherObj.getJSONArray("invVdetailList");
                final lvRequisitionAdapter adapter=new lvRequisitionAdapter((AppCompatActivity) getActivity(),invVdetailList);
                lvRequisition.setAdapter(adapter);
                SwipeMenuCreator creator = new SwipeMenuCreator() {

                    @Override
                    public void create(SwipeMenu menu) {
                        // create "delete" item
                        SwipeMenuItem deleteItem = new SwipeMenuItem(
                                getActivity().getApplicationContext());
                        // set item background
                        deleteItem.setBackground(new ColorDrawable(Color.parseColor("#969799")));
                        // set item width
                        deleteItem.setWidth(SteelHelper.dp2px(getContext(),70));
                        // set a icon
                        deleteItem.setIcon(R.drawable.ic_delete_24dp);
                        // add to menu
                        menu.addMenuItem(deleteItem);
                    }

                };
                lvRequisition.setOnMenuItemClickListener(new SwipeMenuListView.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(final int position, SwipeMenu menu, int index) {
                        switch (index) {
                            case 0:
                                // delete
                                if(lvRequisition.getAdapter().getCount()==1){
                                    Toast.makeText(context,"Last item canot be deleted",Toast.LENGTH_SHORT).show();
                                }
                                else {
                                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

                                    builder.setTitle("Confirm");
                                    builder.setMessage("Sure to delete?");

                                    builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {

                                        public void onClick(DialogInterface dialog, int which) {
                                            try {
                                                if(requisitionMainTemplate.isNull("invVoucherObj") && requisitionMainTemplate.getJSONObject("invVoucherObj").isNull("invVdetailList")){

                                                }
                                                else if(requisitionMainTemplate.getJSONObject("invVoucherObj").getJSONArray("invVdetailList").getJSONObject(position).isNull("id")){
                                                    //Log.e("New", "New Entry"); remove the entry
                                                    requisitionMainTemplate.getJSONObject("invVoucherObj").getJSONArray("invVdetailList").getJSONObject(position).put("status", 0);
                                                    JSONArray invVdetailList=requisitionMainTemplate.getJSONObject("invVoucherObj").getJSONArray("invVdetailList");
                                                    invVdetailList=SteelHelper.remove(position,invVdetailList);
                                                    requisitionMainTemplate.getJSONObject("invVoucherObj").put("invVdetailList", invVdetailList);
                                                }else {
                                                    //Old entry set status 0 only
                                                    requisitionMainTemplate.getJSONObject("invVoucherObj").getJSONArray("invVdetailList").getJSONObject(position).put("status", 0);
                                                }
                                                getActivity().findViewById(R.id.save).performClick();

                                                Toast.makeText(getActivity(),"Deleting",Toast.LENGTH_SHORT).show();
                                            }catch (Exception e){
                                                Log.e(LOG_DBG,e.getLocalizedMessage());
                                            }
                                            dialog.dismiss();
                                        }

                                    });

                                    builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            // Do nothing
                                            dialog.dismiss();
                                        }
                                    });

                                    AlertDialog alert = builder.create();
                                    alert.show();
                                }
                                break;
                        }
                        // false : close the menu; true : not close the menu
                        return false;
                    }
                });
                lvRequisition.setMenuCreator(creator);
                lvRequisition.setSwipeDirection(SwipeMenuListView.DIRECTION_LEFT);

                String narration =invVoucherObj.isNull("narration")?"":invVoucherObj.getString("narration");
                tvNarration.setText(narration);
                if(clickable){
                    lvRequisition.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            Intent intent = new Intent(getActivity(), RequisitionEntryActivity.class);
                            intent.putExtra("EditFlag", true);
                            intent.putExtra("position", position);
                            intent.putExtra("canDelete",lvRequisition.getAdapter().getCount()!=1);
                            if(getActivity().findViewById(R.id.search_salesman).getVisibility()==View.VISIBLE){
                                intent.putExtra("sLid",getActivity().findViewById(R.id.search_salesman).getTag().toString());
                            }
                            getActivity().startActivityForResult(intent, REQUEST_REQUISITION_ENTRY);
                        }
                    });
                }
            }catch (Exception e){
                Log.e(this.getClass().getSimpleName(),e.getLocalizedMessage());
            }
        }
    }
}
