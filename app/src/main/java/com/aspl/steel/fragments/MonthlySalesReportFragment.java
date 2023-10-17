package com.aspl.steel.fragments;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;

import com.aspl.steel.DealerNameAutocompleteActivity;
import com.aspl.steel.GlobalConfiguration;
import com.aspl.steel.MonthlySalesRptDetailActivity;
import com.aspl.steel.R;
import com.aspl.steel.Services.LogoutService;
import com.aspl.steel.SteelHelper;
import com.aspl.steel.adapters.gvMonthlySalesReportDateAdapter;
import com.aspl.steel.adapters.gvMonthlySalesReportDealerAdapter;

import org.json.JSONArray;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

/**
 *  Created by Arnab Kar on 5/3/16.
 */
public class MonthlySalesReportFragment extends Fragment {
    String LOG_DBG=this.getClass().getSimpleName();
    DrawerLayout mDrawerLayout;
    ProgressDialog progressDialog;
    String strType;
    private String cid,segid,tempKeyCode,sLid,uid,userType;
    static int REQUEST_SALESMAN_AUTOCOMPLETE=41;

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
        View rootView = inflater.inflate(R.layout.fragment_monthly_sales_report, container, false);
        initLogin(rootView);
        return rootView;
    }
    private void initLogin(final View rootView) {
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
        ((TextView) rootView.findViewById(R.id.screenTitle)).setText("Projected Dealer Point Monthly Sales Report");

        final Spinner spinnerType=(Spinner)rootView.findViewById(R.id.type);
        final String type[]={"Dealer","Date"};
        ArrayAdapter<String> adapter1=new ArrayAdapter<>(getActivity(), R.layout.spinner_layout_white, type);
        adapter1.setDropDownViewResource(
                android.R.layout.simple_spinner_dropdown_item);
        final View gvMonthlySalesReport_dealer,gvMonthlySalesReport_date;
        gvMonthlySalesReport_dealer=rootView.findViewById(R.id.gvMonthlySalesReport_dealer);
        gvMonthlySalesReport_date=rootView.findViewById(R.id.gvMonthlySalesReport_date);
        spinnerType.setOnItemSelectedListener(
                new AdapterView.OnItemSelectedListener() {
                    public void onItemSelected(
                            AdapterView<?> parent,
                            View view,
                            int position,
                            long id) {
                        //rootView.findViewById(R.id.ok).performClick();
                    }

                    public void onNothingSelected(AdapterView<?> parent) {
                    }
                }
        );
        spinnerType.setAdapter(adapter1);
        spinnerType.setSelection(0);

        if(userType.equals("Salesman")){
            rootView.findViewById(R.id.search_layout).setVisibility(View.GONE);
        }

        rootView.findViewById(R.id.search).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(getActivity(),DealerNameAutocompleteActivity.class);
                intent.putExtra("type", "Salesman");
                getActivity().startActivityForResult(intent, REQUEST_SALESMAN_AUTOCOMPLETE);
            }
        });
        final TextView fromDt=(TextView)rootView.findViewById(R.id.fromDt);
        final TextView toDt=(TextView)rootView.findViewById(R.id.toDt);
        final SimpleDateFormat dateFormat=new SimpleDateFormat("dd-MM-yyyy");
        fromDt.setText(dateFormat.format(SteelHelper.getFirstDateOfMonth(Calendar.getInstance())));
        toDt.setText(dateFormat.format(SteelHelper.getLastDateOfMonth(Calendar.getInstance())));

        fromDt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar newCalender = Calendar.getInstance();
                DatePickerDialog dialog = new DatePickerDialog(getActivity(), new DatePickerDialog.OnDateSetListener() {

                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        Calendar newDate = Calendar.getInstance();
                        newDate.set(year, monthOfYear, dayOfMonth);
                        fromDt.setText(dateFormat.format(newDate.getTime()));
                        if (!toDt.getText().toString().endsWith(fromDt.getText().toString().substring(3))) {
                            toDt.setText(dateFormat.format(SteelHelper.getLastDateOfMonth(newDate)));
                        }
                    }

                }, newCalender.get(Calendar.YEAR), newCalender.get(Calendar.MONTH), newCalender.get(Calendar.DAY_OF_MONTH));
                dialog.setTitle("Select Date:");
                dialog.show();
            }
        });
        toDt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar newCalender = Calendar.getInstance();
                DatePickerDialog dialog = new DatePickerDialog(getActivity(), new DatePickerDialog.OnDateSetListener() {

                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        Calendar newDate = Calendar.getInstance();
                        newDate.set(year, monthOfYear, dayOfMonth);
                        toDt.setText(dateFormat.format(newDate.getTime()));
                        if (!fromDt.getText().toString().endsWith(toDt.getText().toString().substring(3))) {
                            fromDt.setText(dateFormat.format(SteelHelper.getFirstDateOfMonth(newDate)));
                        }
                    }

                }, newCalender.get(Calendar.YEAR), newCalender.get(Calendar.MONTH), newCalender.get(Calendar.DAY_OF_MONTH));
                dialog.setTitle("Select Date:");
                dialog.show();
            }
        });
        rootView.findViewById(R.id.ok).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rootView.findViewById(R.id.footer).setVisibility(View.VISIBLE);
                rootView.findViewById(R.id.initScreen).setVisibility(View.GONE);
                Object id = rootView.findViewById(R.id.search).getTag();

                if (userType.equals("Salesman")) {

                } else if (id == null) {
                    sLid = "0";
                } else {
                    sLid = id.toString();
                }

                strType = spinnerType.getSelectedItem().toString();
                if (strType.equals("Dealer")) {
                    gvMonthlySalesReport_dealer.setVisibility(View.VISIBLE);
                    gvMonthlySalesReport_date.setVisibility(View.GONE);
                } else if (strType.equals("Date")) {
                    gvMonthlySalesReport_dealer.setVisibility(View.GONE);
                    gvMonthlySalesReport_date.setVisibility(View.VISIBLE);
                }

                String dt1 = ((TextView) rootView.findViewById(R.id.fromDt)).getText().toString();
                String dt2 = ((TextView) rootView.findViewById(R.id.toDt)).getText().toString();


                MonthlySalesReport monthlySalesReport = new MonthlySalesReport((AppCompatActivity) getActivity(), cid, segid, sLid, tempKeyCode, userType, strType, dt1, dt2);
                monthlySalesReport.execute();
            }
        });
        rootView.findViewById(R.id.clear_input).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rootView.findViewById(R.id.search).setTag(null);
                ((TextView) rootView.findViewById(R.id.search)).setText("All");
            }
        });

    }
    class MonthlySalesReport extends AsyncTask<String,Void,String>{
        AppCompatActivity context;
        String cid,segid,tempKeyCode,sLid,dt1,dt2,userType,rptType;
        public MonthlySalesReport(AppCompatActivity context, String cid, String segid, String sLid, String tempKeyCode, String userType, String rptType, String dt1, String dt2) {
            this.context = context;
            this.cid=cid;
            this.segid=segid;
            this.tempKeyCode=tempKeyCode;
            this.sLid=sLid;
            this.dt1=dt1;
            this.dt2=dt2;
            this.userType=userType;
            this.rptType=rptType;
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
                String link="http://"+dnsport+"/SteelSales-war/Stl_DailySalesRpt_C_Api";
                HashMap<String,String> hm=new HashMap<>();
                hm.put("cid",cid);
                hm.put("segid", segid);
                hm.put("sLid", sLid);
                hm.put("dt1", dt1);
                hm.put("dt2", dt2);
                hm.put("rptType", rptType);
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
            if (progressDialog != null && progressDialog.isShowing())
                progressDialog.dismiss();

            if (rptType.equalsIgnoreCase("Dealer")) {
                String msg = "";
                ArrayList<HashMap> arrList = new ArrayList<>();
                try {
                    JSONObject mainObj = new JSONObject(s);
                    JSONArray arrData = mainObj.getJSONArray("data");
                    msg = mainObj.getString("msg");
                    for (int i = 0; i < arrData.length(); i++) {
                        JSONObject jsonObject = arrData.getJSONObject(i);
                        String dealerName = jsonObject.getString("dealer");
                        String place = jsonObject.getString("place");
                        String sales_man = jsonObject.getString("sales_man");
                        BigDecimal d9 = new BigDecimal(jsonObject.getDouble("d9"));
                        BigDecimal d8 = new BigDecimal(jsonObject.getDouble("d8"));
                        BigDecimal d5 = new BigDecimal(jsonObject.getDouble("d5"));
                        BigDecimal d4 = new BigDecimal(jsonObject.getDouble("d4"));
                        BigDecimal d7 = new BigDecimal(jsonObject.getDouble("d7"));
                        BigDecimal d6 = new BigDecimal(jsonObject.getDouble("d6"));
                        BigDecimal d25 = new BigDecimal(jsonObject.getDouble("d25"));
                        BigDecimal d24 = new BigDecimal(jsonObject.getDouble("d24"));
                        BigDecimal d1 = new BigDecimal(jsonObject.getDouble("d1"));
                        BigDecimal d2 = new BigDecimal(jsonObject.getDouble("d2"));
                        BigDecimal d23 = new BigDecimal(jsonObject.getDouble("d23"));
                        BigDecimal d3 = new BigDecimal(jsonObject.getDouble("d3"));
                        BigDecimal d22 = new BigDecimal(jsonObject.getDouble("d22"));
                        BigDecimal d21 = new BigDecimal(jsonObject.getDouble("d21"));
                        BigDecimal d20 = new BigDecimal(jsonObject.getDouble("d20"));
                        BigDecimal d29 = new BigDecimal(jsonObject.getDouble("d29"));
                        BigDecimal d28 = new BigDecimal(jsonObject.getDouble("d28"));
                        BigDecimal d27 = new BigDecimal(jsonObject.getDouble("d27"));
                        BigDecimal d26 = new BigDecimal(jsonObject.getDouble("d26"));
                        BigDecimal d30 = new BigDecimal(jsonObject.getDouble("d30"));
                        BigDecimal d12 = new BigDecimal(jsonObject.getDouble("d12"));
                        BigDecimal d11 = new BigDecimal(jsonObject.getDouble("d11"));
                        BigDecimal d14 = new BigDecimal(jsonObject.getDouble("d14"));
                        BigDecimal d31 = new BigDecimal(jsonObject.getDouble("d31"));
                        BigDecimal d13 = new BigDecimal(jsonObject.getDouble("d13"));
                        BigDecimal d10 = new BigDecimal(jsonObject.getDouble("d10"));
                        BigDecimal d19 = new BigDecimal(jsonObject.getDouble("d19"));
                        BigDecimal d16 = new BigDecimal(jsonObject.getDouble("d16"));
                        BigDecimal d15 = new BigDecimal(jsonObject.getDouble("d15"));
                        BigDecimal d18 = new BigDecimal(jsonObject.getDouble("d18"));
                        BigDecimal d17 = new BigDecimal(jsonObject.getDouble("d17"));
                        BigDecimal totalVal = d9.add(d8).add(d5).add(d4).add(d7).add(d6)
                                .add(d25).add(d24).add(d1).add(d2).add(d23).add(d3)
                                .add(d22).add(d21).add(d20).add(d29).add(d28).add(d27)
                                .add(d26).add(d30).add(d12).add(d11).add(d14).add(d31)
                                .add(d13).add(d10).add(d19).add(d16).add(d15).add(d18).add(d17).setScale(3, BigDecimal.ROUND_HALF_UP).stripTrailingZeros();
                        String total = totalVal.toPlainString();

                        HashMap<String, String> hashMap = new HashMap<>();
                        hashMap.put("dealerName", dealerName);
                        hashMap.put("place", place);
                        hashMap.put("sales_man", sales_man);
                        hashMap.put("total", total);
                        hashMap.put("jsonData", jsonObject.toString());
                        arrList.add(hashMap);

                    }
                } catch (Exception e) {
                    Log.e(LOG_DBG, e.getLocalizedMessage());
                    Toast.makeText(context, "Unable to connect to server", Toast.LENGTH_SHORT).show();
                }
                if (msg.contains("Authorization Failure..!")) {
                    Toast.makeText(getActivity(), "Session Expired", Toast.LENGTH_SHORT).show();
                    Intent logout_intent=new Intent(getActivity(), LogoutService.class);
                    getActivity().startService(logout_intent);
                    getActivity().finish();
                    return;
                }

                ListView listView = (ListView) getActivity().findViewById(R.id.gvMonthlySalesReport_dealer);
                gvMonthlySalesReportDealerAdapter adapter = new gvMonthlySalesReportDealerAdapter(context, arrList, userType);
                listView.setEmptyView(getActivity().findViewById(R.id.emptyView));
                listView.setAdapter(adapter);

                ((TextView)getActivity().findViewById(R.id.footerLeftTitle)).setText("Total Dealer:");
                ((TextView)getActivity().findViewById(R.id.footerRightTitle)).setText("Total Qty:");
                ((TextView)getActivity().findViewById(R.id.txtLeft)).setText(adapter.getCount()+"");

                ArrayList<HashMap> myList=adapter.myList;
                Double total_value=0.0;
                for(int i=0;i<myList.size();i++){
                    total_value+=Double.parseDouble(myList.get(i).get("total").toString());
                }
                BigDecimal total_val=new BigDecimal(total_value);

                ((TextView) getActivity().findViewById(R.id.txtTotal)).setText(total_val.setScale(3,BigDecimal.ROUND_HALF_UP).toString());

                Date dateToSend;
                try {
                    dateToSend = new SimpleDateFormat("dd-MM-yyyy").parse(dt1);
                }catch (Exception e){
                    dateToSend=new Date();
                }
                final String strDateToSend=new SimpleDateFormat("MMMM").format(dateToSend)+" "+new SimpleDateFormat("yyyy").format(dateToSend);

                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        Intent intent = new Intent(getActivity(), MonthlySalesRptDetailActivity.class);
                        intent.putExtra("type","dealer");
                        intent.putExtra("jsonObject", view.findViewById(R.id.card_body).getTag().toString());
                        intent.putExtra("totalVal", ((TextView) view.findViewById(R.id.txtTotal)).getText().toString());
                        intent.putExtra("monthYear",strDateToSend);
                        startActivity(intent);
                    }
                });
            }
            else if(rptType.equalsIgnoreCase("date")){
                String msg = "";
                ArrayList<HashMap> arrList = new ArrayList<>();
                try{
                    JSONObject mainObj = new JSONObject(s);
                    JSONArray arrData = mainObj.getJSONArray("data");
                    msg = mainObj.getString("msg");
                    for (int i = 0; i < arrData.length(); i++) {
                        JSONObject jsonObject = arrData.getJSONObject(i);
                        String date_day=jsonObject.getString("date");
                        JSONArray dataArr=jsonObject.getJSONArray("data");
                        int dealer_count=dataArr.length();
                        BigDecimal quantity=new BigDecimal(0);
                        for(int j=0;j<dataArr.length();j++){
                            JSONObject dataObj=dataArr.getJSONObject(j);
                            BigDecimal qty=new BigDecimal(dataObj.getDouble("qty"));
                            quantity=quantity.add(qty);
                        }
                        String strQunatity=quantity.setScale(3,BigDecimal.ROUND_HALF_UP).stripTrailingZeros().toPlainString();
                        if(strQunatity.equals("0.000")){
                            strQunatity="0";
                        }
                        Date dateToSend;
                        try {
                            dateToSend = new SimpleDateFormat("dd-MM-yyyy").parse(dt1);
                        }catch (Exception e){
                            dateToSend=new Date();
                        }
                        final String strDateToSend=new SimpleDateFormat("MMMM").format(dateToSend)+" "+new SimpleDateFormat("yyyy").format(dateToSend);
                        HashMap<String, String> hashMap = new HashMap<>();
                        hashMap.put("date_day", date_day);
                        hashMap.put("month_year",strDateToSend);
                        hashMap.put("dealer_count", dealer_count+"");
                        hashMap.put("total", strQunatity);
                        hashMap.put("jsonData",dataArr.toString());
                        if(strQunatity.equals("0")||strQunatity.equals("0.000")){
                            continue;
                        }
                        arrList.add(hashMap);
                    }
                }catch (Exception e){
                    Log.e(LOG_DBG, e.getLocalizedMessage());
                    Toast.makeText(context, "Unable to connect to server", Toast.LENGTH_SHORT).show();
                }
                if (msg.contains("Authorization Failure..!")) {
                    Toast.makeText(getActivity(), "Session Expired", Toast.LENGTH_SHORT).show();
                    Intent logout_intent=new Intent(getActivity(), LogoutService.class);
                    getActivity().startService(logout_intent);
                    getActivity().finish();
                    return;
                }
                ListView listView = (ListView) getActivity().findViewById(R.id.gvMonthlySalesReport_date);
                Date dateToSend;
                try {
                    dateToSend = new SimpleDateFormat("dd-MM-yyyy").parse(dt1);
                }catch (Exception e){
                    dateToSend=new Date();
                }
                String monthYear=new SimpleDateFormat("MMM").format(dateToSend)+" "+new SimpleDateFormat("yyyy").format(dateToSend);
                gvMonthlySalesReportDateAdapter adapter = new gvMonthlySalesReportDateAdapter(context, arrList, userType,monthYear);
                listView.setEmptyView(getActivity().findViewById(R.id.emptyView));
                listView.setAdapter(adapter);

                ((TextView)getActivity().findViewById(R.id.footerLeftTitle)).setText("No. of days:");
                ((TextView)getActivity().findViewById(R.id.footerRightTitle)).setText("Total Qty:");
                ((TextView)getActivity().findViewById(R.id.txtLeft)).setText(adapter.getCount()+"");
                ArrayList<HashMap> myList=adapter.myList;
                Double total_value=0.0;
                for(int i=0;i<myList.size();i++){
                    total_value+=Double.parseDouble(myList.get(i).get("total").toString());
                }
                BigDecimal total_val=new BigDecimal(total_value);

                ((TextView) getActivity().findViewById(R.id.txtTotal)).setText(total_val.setScale(3,BigDecimal.ROUND_HALF_DOWN).toString());

                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        Intent intent = new Intent(getActivity(), MonthlySalesRptDetailActivity.class);
                        intent.putExtra("type","date");
                        intent.putExtra("jsonObject", view.findViewById(R.id.card_body).getTag().toString());
                        intent.putExtra("totalVal", ((TextView) view.findViewById(R.id.txtTotal)).getText().toString());
                        intent.putExtra("dayMonthYear", " ");
                        startActivity(intent);
                    }
                });

            }
            else {
                Toast.makeText(context,"Wrong option chosen",Toast.LENGTH_SHORT).show();
            }
        }
    }

}
