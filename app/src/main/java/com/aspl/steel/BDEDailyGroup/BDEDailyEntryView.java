package com.aspl.steel.BDEDailyGroup;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
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
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;

import com.aspl.steel.GlobalConfiguration;
import com.aspl.steel.MasonMeetComplation.MasionMeetCompletionEntry;
import com.aspl.steel.R;
import com.aspl.steel.Services.LogoutService;
import com.aspl.steel.SteelHelper;
import com.aspl.steel.fragments.GPSTracker;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

public class BDEDailyEntryView extends Fragment {

    View rootView;
    DrawerLayout mDrawerLayout;
    SimpleDateFormat dateFormatter=new SimpleDateFormat("dd-MM-yyyy");
    SimpleDateFormat dateFormatter1=new SimpleDateFormat("yyyy-MM-dd");
    String sLid, cid, segid, tempKeyCode, uid;
    LinearLayout page1,page2;
    Spinner v_type,p_o_visit;
    EditText v_name,v_number,c_details,remark_txt;
    TextView visit_start,date_txt,end_visit_save;
    RadioGroup radioGroup;
    final String LOG_DBG = getClass().getSimpleName();
    Date date = new Date();
    JSONObject blankJson = new JSONObject();

    String[] p_o_v_optAr = { "Site Visit", "Arcade Information", "Courtesy Visit","Lifting Verification","Enlistment","FE /FS Meet","Others"};
    String[] v_typeAr = { "Engg", "Mason", "Customer","Site","Dealer","S.Dealer"};


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.bde_details_entry_view, container, false);
        viewSet(rootView);
        SharedPreferences sharedPref = getActivity().getSharedPreferences("loginCredentials", Context.MODE_PRIVATE);
        cid = sharedPref.getString("cid", "");
        segid = sharedPref.getString("segid", "");
        tempKeyCode = sharedPref.getString("storedKeyCode", "");
        sLid = sharedPref.getString("storedSlid", "");
        uid = sharedPref.getString("storedUserId", "");

        /*..... Page 1 is ......*/
        page1 = (LinearLayout)rootView.findViewById(R.id.page1);
        spinner1();
        v_name = (EditText) rootView.findViewById(R.id.v_name);
        v_number = (EditText) rootView.findViewById(R.id.v_number);
        visit_start = (TextView) rootView.findViewById(R.id.visit_start);
        visit_start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startVisitJsonPrepare();
            }
        });
        /*...... Page 2 is.....*/
        page2 = (LinearLayout)rootView.findViewById(R.id.page2);
        radioGroup = (RadioGroup)rootView.findViewById(R.id.radioGroup);
        spinner2();
        c_details = (EditText) rootView.findViewById(R.id.c_details);
        remark_txt = (EditText) rootView.findViewById(R.id.remark_txt);
        date_txt = (TextView) rootView.findViewById(R.id.date_txt);
        date_txt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar newCalender = Calendar.getInstance();
                DatePickerDialog dialog = new DatePickerDialog(getActivity(), new DatePickerDialog.OnDateSetListener() {
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        Calendar newDate = Calendar.getInstance();
                        newDate.set(year, monthOfYear, dayOfMonth);
                        date_txt.setText(dateFormatter.format(newDate.getTime()));
                        date_txt.setTag(dateFormatter1.format(newDate.getTime()));

                    }
                }, newCalender.get(Calendar.YEAR), newCalender.get(Calendar.MONTH), newCalender.get(Calendar.DAY_OF_MONTH));
                dialog.setTitle("Select Date:");
                Calendar cal = Calendar.getInstance();
                cal.setTime(new Date());
                //cal.add(Calendar.MONTH, -1);
                dialog.getDatePicker().setMinDate(cal.getTimeInMillis());
                dialog.show();
            }
        });
        end_visit_save = (TextView) rootView.findViewById(R.id.end_visit_save);
        end_visit_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EndVisitJsonPrepare();
            }
        });


        BlankJsonFetchAPI dealerTargetAPI = new BlankJsonFetchAPI(getContext(), 1);
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

    void spinner1() {
        v_type = (Spinner) rootView.findViewById(R.id.v_type);
        ArrayAdapter aa = new ArrayAdapter(getContext(),android.R.layout.simple_spinner_item,v_typeAr);
        aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //Setting the ArrayAdapter data on the Spinner
        v_type.setAdapter(aa);
    }

    void spinner2() {
        p_o_visit = (Spinner) rootView.findViewById(R.id.p_o_visit);
        ArrayAdapter aa = new ArrayAdapter(getContext(),android.R.layout.simple_spinner_item,p_o_v_optAr);
        aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //Setting the ArrayAdapter data on the Spinner
        p_o_visit.setAdapter(aa);
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

    void startVisitJsonPrepare(){
        try {
            blankJson.put("visitType", v_type.getSelectedItem());

            String editBox1=editTextBlankCheck(v_name);
            if (editBox1.equals("")){
                Toast.makeText(getContext(),"Please Enter Name",Toast.LENGTH_LONG).show();
                return;
            }
            blankJson.put("partyName", editBox1);

            String editBox2=editTextBlankCheck(v_number);
            if (editBox2.equals("")){
                Toast.makeText(getContext(),"Please Enter Phone Number",Toast.LENGTH_LONG).show();
                return;
            }
            blankJson.put("phoneNumber", editBox2);
            blankJson.put("visitStatus",0);
            blankJson.put("slid",sLid);

            GPSTracker gpsTracker = new GPSTracker(getActivity());
            if (!gpsTracker.canGetLocation()) {
                Toast.makeText(getContext(), "GPS is disabled. Please Check !", Toast.LENGTH_SHORT).show();
                return;
            }else {
                blankJson.put("check_in_latitude", gpsTracker.latitude);
                blankJson.put("check_in_longitude", gpsTracker.longitude);
            }

            saveDatabyServlet saveDatabyServlet=new saveDatabyServlet(getContext(),blankJson);
            saveDatabyServlet.execute();

        }catch (Exception e){Log.e("Error Page 1 Prepare",e.toString());}


    }

    void EndVisitJsonPrepare(){
        try {
            blankJson.put("visitPurpose", p_o_visit.getSelectedItem());

            int selectedId = radioGroup.getCheckedRadioButtonId();
            RadioButton radioButton = (RadioButton) rootView.findViewById(selectedId);
            blankJson.put("converted", radioButton.getText());

            String editBox1=editTextBlankCheck(c_details);
            if (editBox1.equals("") && radioButton.getText().equals("Yes")){
                Toast.makeText(getContext(),"Please Enter Conversion Details",Toast.LENGTH_LONG).show();
                return;
            }
            blankJson.put("conversionDetails", editBox1);

            String editBox2=editTextBlankCheck(remark_txt);
            if (editBox2.equals("")){
                Toast.makeText(getContext(),"Please Enter Remarks",Toast.LENGTH_LONG).show();
                return;
            }
            blankJson.put("remarks", editBox2);

            if (date_txt.getTag().equals("0")){
                Toast.makeText(getContext(),"Please Select Next Visit Date!",Toast.LENGTH_LONG).show();
                return;
            }
            blankJson.put("nextVisit",date_txt.getTag());
            blankJson.put("visitStatus",1);
            blankJson.put("slid",sLid);

            GPSTracker gpsTracker = new GPSTracker(getActivity());
            if (!gpsTracker.canGetLocation()) {
                Toast.makeText(getContext(), "GPS is disabled. Please Check !", Toast.LENGTH_SHORT).show();
                return;
            }else {
                blankJson.put("check_out_latitude", gpsTracker.latitude);
                blankJson.put("check_out_longitude", gpsTracker.longitude);
            }

            saveDatabyServlet saveDatabyServlet=new saveDatabyServlet(getContext(),blankJson);
            saveDatabyServlet.execute();

        }catch (Exception e){Log.e("Error Page 1 Prepare",e.toString());}


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
                String link="http://"+dnsport+"/SteelSales-war/Stl_BdeActivity_C_Api";
                HashMap<String,String> hm=new HashMap<>();
                hm.put("cid",cid);
                hm.put("segid", segid);
                hm.put("data", answerOnj1.toString());
                hm.put("keyCode",tempKeyCode);

                //Log.e("........",hm.toString());
//                String veryLongString=answerOnj1.toString();
//                int maxLogSize = 1000;
//                for(int i = 0; i <= veryLongString.length() / maxLogSize; i++) {
//                    int start = i * maxLogSize;
//                    int end = (i+1) * maxLogSize;
//                    end = end > veryLongString.length() ? veryLongString.length() : end;
//                    Log.e("..........     ", veryLongString.substring(start, end));
//                }

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
                if (main_template.getString("type").equalsIgnoreCase("success")) {
                    AlertDialog.Builder alertDialog = new AlertDialog.Builder(getContext())//set icon
                            .setIcon(R.mipmap.ic_launcher)//set title
                            .setTitle("Alert")//set message
                            .setMessage(msg)//set positive button
                            .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    //set what would happen when positive button is clicked
                                    getFragmentManager().popBackStack();
                                }
                            });
                    alertDialog.show();
                }else if (main_template.getString("type").equalsIgnoreCase("error")) {
                    AlertDialog.Builder alertDialog = new AlertDialog.Builder(getContext())//set icon
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
                    Intent logout_intent=new Intent(getActivity(), LogoutService.class);
                    getActivity().startService(logout_intent);
                    getActivity().finish();
                    return;
                }else{
                    Toast.makeText(context,msg,Toast.LENGTH_SHORT).show();
                }

            }catch (Exception e){
                Log.e("Error...: ",e.toString()+"    111");
                Toast.makeText(context,e.toString(),Toast.LENGTH_SHORT).show();
            }
        }
    }

    /*.......... Blank JSon Featch Search..........*/
    public class BlankJsonFetchAPI extends AsyncTask<String, Void, String> {
        private Context context;
        private int byGetOrPost=0;          //Flag 0 means get and 1 means post. (By default it is get).

        BlankJsonFetchAPI(Context context,int flag) {
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
                    String link="http://"+dnsport+"/SteelSales-war/Stl_BdeActivity_Fetch_Api";
                    HashMap<String,String> hm=new HashMap<>();
                    hm.put("cid",cid);
                    hm.put("segid", segid);
                    hm.put("slid", sLid);
                    hm.put("keyCode", tempKeyCode);

                    return SteelHelper.performPostCall(link,hm);
                }catch (Exception e){
                    return "Exception: " + e.toString();
                }
            }
        }

        @Override
        protected void onPostExecute(String result) {
           // Log.e(".....111....",result);
            super.onPostExecute(result);
            try{
                JSONObject mainObj=new JSONObject(result);
                if(mainObj.getString("type").equals("success")){
                   blankJson = mainObj.getJSONObject("bdeActivityObj");
                   if (!blankJson.isNull("id")){

                       page1.setVisibility(View.GONE);
                       page2.setVisibility(View.VISIBLE);
                   }else{
                       page1.setVisibility(View.VISIBLE);
                       page2.setVisibility(View.GONE);
                   }

                }else if (mainObj.getString("msg").equals("No Data Found")){
                    Toast.makeText(context,mainObj.getString("msg"),Toast.LENGTH_SHORT).show();

                }else if (mainObj.getString("type").equals("autherror")){

                    Toast.makeText(context,"Session expired.",Toast.LENGTH_SHORT).show();
                    Intent logout_intent=new Intent(getActivity(), LogoutService.class);
                    getActivity().startService(logout_intent);
                    getActivity().finish();
                    return;

                }else{
                    Log.e(LOG_DBG,result);
                    throw new Exception("Server error");
                }
            }catch (Exception e){
                Toast.makeText(context,"Unable to connect to server",Toast.LENGTH_SHORT).show();
            }

        }

    }


}
