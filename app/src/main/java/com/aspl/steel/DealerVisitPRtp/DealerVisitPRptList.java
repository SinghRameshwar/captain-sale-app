package com.aspl.steel.DealerVisitPRtp;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
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
import com.aspl.steel.LoginActivity;
import com.aspl.steel.R;
import com.aspl.steel.SteelHelper;
import com.aspl.steel.fragments.AttendanceAtDealer;
import com.aspl.steel.fragments.RequisitionListingFragment;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

public class DealerVisitPRptList extends Fragment {

    View rootView;
    DrawerLayout mDrawerLayout;
    SimpleDateFormat dateFormatter=new SimpleDateFormat("dd-MM-yyyy");
    SimpleDateFormat dateFormatter1=new SimpleDateFormat("01-MM-yyyy");
    String  sLid,cid,segid,tempKeyCode,uid;
    final String LOG_DBG=getClass().getSimpleName();
    ListView listView;
    Date date=new Date();
    VisitPListAdapter visitPListAdapter;
    TextView right_title,right_title2;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.dealer_visit_p_report_list, container, false);
        viewSet(rootView);
        SharedPreferences sharedPref = getActivity().getSharedPreferences("loginCredentials", Context.MODE_PRIVATE);
        cid = sharedPref.getString("cid", "");
        segid = sharedPref.getString("segid", "");
        tempKeyCode = sharedPref.getString("storedKeyCode", "");
        sLid = sharedPref.getString("storedSlid", "");
        uid = sharedPref.getString("storedUserId", "");
        listView=(ListView)rootView.findViewById(R.id.p_listView);
        right_title=(TextView)rootView.findViewById(R.id.right_title);
        right_title2=(TextView)rootView.findViewById(R.id.right_title2);
        right_title.setText(dateFormatter1.format(date));
        right_title2.setText(dateFormatter.format(date));

        right_title.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar newCalender = Calendar.getInstance();
                DatePickerDialog dialog = new DatePickerDialog(getActivity(), new DatePickerDialog.OnDateSetListener() {

                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        Calendar newDate = Calendar.getInstance();
                        newDate.set(year, monthOfYear, dayOfMonth);
                        right_title.setText(dateFormatter.format(newDate.getTime()));
                        DealerTargetAPI dealerTargetAPI = new DealerTargetAPI(getContext(), 1);
                        dealerTargetAPI.execute(cid, segid, sLid, tempKeyCode);
                    }
                    }, newCalender.get(Calendar.YEAR), newCalender.get(Calendar.MONTH), newCalender.get(Calendar.DAY_OF_MONTH));
                dialog.setTitle("Select Date:");
                Calendar cal = Calendar.getInstance();
                cal.setTime(new Date());
                cal.add(Calendar.MONTH, -1);
                dialog.show();
            }
        });
        right_title2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar newCalender = Calendar.getInstance();
                DatePickerDialog dialog = new DatePickerDialog(getActivity(), new DatePickerDialog.OnDateSetListener() {

                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        Calendar newDate = Calendar.getInstance();
                        newDate.set(year, monthOfYear, dayOfMonth);
                        right_title2.setText(dateFormatter.format(newDate.getTime()));
                        DealerTargetAPI dealerTargetAPI = new DealerTargetAPI(getContext(), 1);
                        dealerTargetAPI.execute(cid, segid, sLid, tempKeyCode);
                    }

                }, newCalender.get(Calendar.YEAR), newCalender.get(Calendar.MONTH), newCalender.get(Calendar.DAY_OF_MONTH));
                dialog.setTitle("Select Date:");
                Calendar cal = Calendar.getInstance();
                cal.setTime(new Date());
                cal.add(Calendar.MONTH, -1);
                dialog.show();
            }
        });

        DealerTargetAPI dealerTargetAPI = new DealerTargetAPI(getContext(), 1);
        dealerTargetAPI.execute(cid, segid, sLid, tempKeyCode);

        return rootView;
    }

    private void viewSet(View rootView) {
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
                    String sLid = arg0[2];
                    String tempKeyCode = arg0[3];
                    String dnsport= GlobalConfiguration.getDomainport();
                    String link="http://"+dnsport+"/SteelSales-war/Stl_Dealer_Visit_PlanList_C_Api";
                    HashMap<String,String> hm=new HashMap<>();
                    hm.put("cid",cid);
                    hm.put("segid", segid);
                    hm.put("saleman_id", sLid);
                    hm.put("from_date", right_title.getText().toString());
                    hm.put("to_date", right_title2.getText().toString());
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
                    JSONArray jsonArray=mainObj.getJSONArray("data");
                    visitPListAdapter=new VisitPListAdapter(getContext(),jsonArray);
                    listView.setAdapter(visitPListAdapter);
                    visitPListAdapter.notifyDataSetChanged();
                }else {
                    JSONArray jsonArray=new JSONArray();
                    visitPListAdapter=new VisitPListAdapter(getContext(),jsonArray);
                    listView.setAdapter(visitPListAdapter);
                    visitPListAdapter.notifyDataSetChanged();
                    Log.e(LOG_DBG,result);
                    throw new Exception("Server error");
                }
            }catch (Exception e){
                Toast.makeText(context,"Unable to connect to server",Toast.LENGTH_SHORT).show();
            }

        }

    }


}
