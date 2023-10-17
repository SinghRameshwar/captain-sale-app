package com.aspl.steel.fragments;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;

import com.aspl.steel.GlobalConfiguration;
import com.aspl.steel.R;
import com.aspl.steel.Services.LogoutService;
import com.aspl.steel.SteelHelper;
import com.aspl.steel.adapters.lvDailySalesListingAdapter;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

/**
 *  Created by Arnab on 4/3/16.
 */
public class DailySalesConfirmListingFragment extends Fragment {

    String LOG_DBG=this.getClass().getSimpleName();
    DrawerLayout mDrawerLayout;
    Date date=new Date();
    SimpleDateFormat dateFormatter=new SimpleDateFormat("dd-MM-yyyy");
    private String cid,segid,tempKeyCode,sLid,uid;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SharedPreferences sharedPref = getActivity().getSharedPreferences("loginCredentials", Context.MODE_PRIVATE);
        cid=sharedPref.getString("cid", "");
        segid=sharedPref.getString("segid", "");
        tempKeyCode=sharedPref.getString("storedKeyCode","");
        sLid=sharedPref.getString("storedSlid","");
        uid=sharedPref.getString("storedUserId","");
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_daily_sale_confirmation_listing, container, false);
        initLogin(rootView);
        return rootView;
    }
    private void initLogin(final View rootView) {
        ((TextView)rootView.findViewById(R.id.screenTitle)).setText("Daily Sales Confirmation Listing");
        ((TextView)rootView.findViewById(R.id.right_title1)).setText(dateFormatter.format(date));
        ((TextView)rootView.findViewById(R.id.right_title2)).setText(dateFormatter.format(date));
        final TextView fromDt=((TextView) rootView.findViewById(R.id.right_title1));
        final TextView toDt=((TextView) rootView.findViewById(R.id.right_title2));
        rootView.findViewById(R.id.right_title1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                Calendar newCalender = Calendar.getInstance();
                DatePickerDialog dialog = new DatePickerDialog(getActivity(), new DatePickerDialog.OnDateSetListener() {

                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        Calendar newDate = Calendar.getInstance();
                        newDate.set(year, monthOfYear, dayOfMonth);
                        String strToDt = ((TextView) rootView.findViewById(R.id.right_title2)).getText().toString();
                        String strFromDt = dateFormatter.format(newDate.getTime());
                        try {
                            Date toDt = new SimpleDateFormat("dd-MM-yyyy").parse(strToDt);
                            Date fromDt=new SimpleDateFormat("dd-MM-yyyy").parse(strFromDt);
                            if (toDt.getTime()>=fromDt.getTime()) {
                                ((TextView) v).setText(dateFormatter.format(newDate.getTime()));
                                FetchDailySaleListingTemplate fetchDailySaleEntryTemplate= new FetchDailySaleListingTemplate((AppCompatActivity) getContext(),cid,segid,tempKeyCode,((TextView)v).getText().toString(),strToDt);
                                fetchDailySaleEntryTemplate.execute();
                            } else {
                                Toast.makeText(getActivity(), "From date should be earlier than To date", Toast.LENGTH_SHORT).show();
                            }
                        } catch (Exception e) {
                            Toast.makeText(getActivity(), "Error occured while setting date", Toast.LENGTH_SHORT).show();
                        }
                    }

                }, newCalender.get(Calendar.YEAR), newCalender.get(Calendar.MONTH), newCalender.get(Calendar.DAY_OF_MONTH));
                dialog.setTitle("Select Date:");
                Calendar cal = Calendar.getInstance();
                cal.setTime(new Date());
                cal.add(Calendar.MONTH, -1);
                dialog.show();
            }
        });
        rootView.findViewById(R.id.right_title2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                Calendar newCalender = Calendar.getInstance();
                DatePickerDialog dialog = new DatePickerDialog(getActivity(), new DatePickerDialog.OnDateSetListener() {

                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        Calendar newDate = Calendar.getInstance();
                        newDate.set(year, monthOfYear, dayOfMonth);
                        String strToDt = dateFormatter.format(newDate.getTime());
                        String strFromDt = ((TextView) rootView.findViewById(R.id.right_title1)).getText().toString();
                        try {
                            Date toDt = new SimpleDateFormat("dd-MM-yyyy").parse(strToDt);
                            Date fromDt=new SimpleDateFormat("dd-MM-yyyy").parse(strFromDt);
                            if (fromDt.getTime()<=toDt.getTime()) {
                                ((TextView) v).setText(dateFormatter.format(newDate.getTime()));
                                FetchDailySaleListingTemplate fetchDailySaleEntryTemplate= new FetchDailySaleListingTemplate((AppCompatActivity) getActivity(),cid,segid,tempKeyCode,strFromDt,((TextView)v).getText().toString());
                                fetchDailySaleEntryTemplate.execute();
                            } else {
                                Toast.makeText(getActivity(), "To date should be after From date", Toast.LENGTH_SHORT).show();
                            }
                        } catch (Exception e) {
                            Toast.makeText(getActivity(), "Error occured while setting date", Toast.LENGTH_SHORT).show();
                        }

                    }

                }, newCalender.get(Calendar.YEAR), newCalender.get(Calendar.MONTH), newCalender.get(Calendar.DAY_OF_MONTH));
                dialog.setTitle("Select Date:");
                Calendar cal = Calendar.getInstance();
                cal.setTime(new Date());
                cal.add(Calendar.MONTH, -1);
                dialog.show();
            }
        });

        Toolbar mToolbar=(Toolbar)rootView.findViewById(R.id.my_actionbar_toolbar);
        if(mToolbar!=null) {
            ((AppCompatActivity)getActivity()).setSupportActionBar(mToolbar);
            mDrawerLayout=(DrawerLayout)getActivity().findViewById(R.id.drawer_layout);
            //This draws the menu icon on the title that looks like 3 horizontal lines(=)
            ActionBarDrawerToggle mDrawerToggle = new ActionBarDrawerToggle(
                    getActivity(),  mDrawerLayout, mToolbar,
                    R.string.navigation_drawer_open, R.string.navigation_drawer_close
            );
            mDrawerLayout.setDrawerListener(mDrawerToggle);
            mDrawerToggle.syncState();
        }
        FetchDailySaleListingTemplate fetchDailySaleEntryTemplate= new FetchDailySaleListingTemplate((AppCompatActivity) getActivity(),cid,segid,tempKeyCode,fromDt.getText().toString(),toDt.getText().toString());
        fetchDailySaleEntryTemplate.execute();
    }
    class FetchDailySaleListingTemplate extends AsyncTask<String,Void,String> {
        private AppCompatActivity context;
        ProgressDialog progressDialog;
        String cid,segid,tempKeyCode,fromDt,toDt;

        public FetchDailySaleListingTemplate(AppCompatActivity context, String cid, String segid, String tempKeyCode, String fromDt, String toDt) {
            this.context = context;
            this.cid=cid;
            this.segid=segid;
            this.tempKeyCode=tempKeyCode;
            this.fromDt=fromDt;
            this.toDt=toDt;
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
                String link="http://"+dnsport+"/SteelSales-war/Stl_TransactionList_C_Api";
                HashMap<String,String> hm=new HashMap<>();
                hm.put("cid",cid);
                hm.put("segid", segid);
                hm.put("fromDt", fromDt);
                hm.put("toDt", toDt);
                hm.put("nature","sale_confirmation" );
                hm.put("uid", sLid);    //or uid ask
                hm.put("partyId", "0");
                hm.put("keyCode",tempKeyCode);
                return SteelHelper.performPostCall(link, hm);
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
                JSONArray listItem=main_template.getJSONArray("data");
                lvDailySalesListingAdapter adapter=new lvDailySalesListingAdapter((AppCompatActivity)getActivity(),listItem,cid,segid,tempKeyCode,uid);
                ListView listView=(ListView)getActivity().findViewById(R.id.lvDailySalesReport);
                listView.setEmptyView(getActivity().findViewById(R.id.emptyView));
                if(listView!=null){
                    listView.setAdapter(adapter);
                }
            }catch (Exception e){
            }
        }
    }
}