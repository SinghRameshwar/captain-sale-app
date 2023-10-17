package com.aspl.steel.MasonMeetInitateGp;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
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
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.aspl.steel.GlobalConfiguration;
import com.aspl.steel.LoginActivity;
import com.aspl.steel.NewLeadGp.NewLeadEntry;
import com.aspl.steel.Pre_OrderG.PreOrderEntryView;
import com.aspl.steel.Pre_OrderG.itemListAdapter;
import com.aspl.steel.R;
import com.aspl.steel.Services.LogoutService;
import com.aspl.steel.SteelHelper;
import com.aspl.steel.adapters.DealerSearchAutoComp;
import com.aspl.steel.salesmanvisit.salesmanvisit_view;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class MasionMeetInitiate extends Fragment {

    String manJson = "{\"budget\":null,\"masonMeetParticipantsObj\":{\"id\":null,\"dtCrt\":null,\"status\":null,\"name\":null,\"segid\":1,\"contactNo\":null,\"cid\":1,\"dtUpdt\":null},\"masonMeetGiftsList\":[],\"conductSlid\":null,\"masonMeetHistoryObj\":null,\"id\":null,\"sh_id\":null,\"dtCrt\":null,\"shApprove\":null,\"segid\":1,\"asmApproveDt\":null,\"shApproveDt\":null,\"attendees\":null,\"fuList\":null,\"meetDate\":null,\"status\":1,\"asm_id\":null,\"cid\":1,\"masonMeetGiftsObj\":{\"id\":null,\"dtCrt\":null,\"status\":null,\"asmId\":null,\"segid\":1,\"giftId\":null,\"cid\":1,\"dtUpdt\":null},\"inititateDt\":null,\"vtypeId\":null,\"flag\":null,\"inititateSlid\":null,\"asmApprove\":null,\"partyId\":null,\"ref_id\":null,\"masonMeetParticipantsList\":[],\"fuObj\":null,\"vnum\":null,\"dtUpdt\":null}";

    View rootView;
    DrawerLayout mDrawerLayout;
    SimpleDateFormat dateFormatter=new SimpleDateFormat("dd-MM-yyyy");
    SimpleDateFormat dateFormatter1=new SimpleDateFormat("dd");
    String  sLid,cid,segid,tempKeyCode,uid,salesman_name;
    final String LOG_DBG=getClass().getSimpleName();
    RequestQueue requestQueue = null;
    TextView dateTxt1,dateTxt2,lable1;
    Button saveBtn;
    EditText qtyTxt,attendanceEdit;
    AutoCompleteTextView dealerSearchM;
    FetchDealerName mFetchTask;
    Spinner v_type;
    String[] v_typeAr = { "Mason", "Engineers", "Contracters","Dealers","Customers"};

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.masion_meet_initiate_view, container, false);
        viewSet(rootView);
        SharedPreferences sharedPref = getActivity().getSharedPreferences("loginCredentials", Context.MODE_PRIVATE);
        cid = sharedPref.getString("cid", "");
        segid = sharedPref.getString("segid", "");
        tempKeyCode = sharedPref.getString("storedKeyCode", "");
        sLid = sharedPref.getString("storedSlid", "");
        uid = sharedPref.getString("storedUserId", "");
        salesman_name = sharedPref.getString("salesman_name", "");
        saveBtn=(Button) rootView.findViewById(R.id.saveBtn);
        dealerSearchM = (AutoCompleteTextView) rootView.findViewById(R.id.dealerSearchM);
        dateTxt1=(TextView)rootView.findViewById(R.id.dateTxt1);
        lable1=(TextView)rootView.findViewById(R.id.lable1);
       // dateTxt2=(TextView)rootView.findViewById(R.id.dateTxt2);
        qtyTxt=(EditText) rootView.findViewById(R.id.qtyTxt);
        spin1Method();
        attendanceEdit=(EditText) rootView.findViewById(R.id.attendanceEdit);
        attendanceEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (attendanceEdit.getText().length()>0) {
                    if ((Integer.parseInt(attendanceEdit.getText()+"")<= 45)) {

                    }else{
                        attendanceEdit.getText().clear();
                        Toast.makeText(getContext(),"Max Number of Planned Attendees 45!",Toast.LENGTH_LONG).show();
                    }
                }
            }
        });

//        Calendar cal = Calendar.getInstance();
//        cal.setTime(date);
//        cal.add(Calendar.DATE, 7);
//        dateTxt2.setText(dateFormatter1.format(cal.getTime()));


        dateTxt1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String dateStr1 = new SimpleDateFormat("dd", Locale.getDefault()).format(new Date());
                Calendar newCalender = Calendar.getInstance();
                DatePickerDialog dialog = new DatePickerDialog(getActivity(), new DatePickerDialog.OnDateSetListener() {
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        Calendar newDate = Calendar.getInstance();
                        newDate.set(year, monthOfYear, dayOfMonth);
                        dateTxt1.setText(dateFormatter.format(newDate.getTime()));
                        dateTxt1.setTag(dateFormatter.format(newDate.getTime()));
                    }
                }, newCalender.get(Calendar.YEAR), newCalender.get(Calendar.MONTH), newCalender.get(Calendar.DAY_OF_MONTH));
                dialog.setTitle("Select Date:");
                Calendar cal = Calendar.getInstance();
                cal.add(Calendar.DATE, -Integer.parseInt(dateFormatter1.format(cal.getTime()))+1);
                if (Integer.parseInt(dateStr1) < 16){
                    cal.add(Calendar.MONTH, 1);
                }else{
                    cal.add(Calendar.MONTH, 2);
                }
                dialog.getDatePicker().setMinDate(cal.getTimeInMillis());
                cal.add(Calendar.DATE, cal.getActualMaximum(Calendar.DAY_OF_MONTH)-1);
                dialog.getDatePicker().setMaxDate(cal.getTimeInMillis());
                dialog.show();
            }
        });

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
        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mainJsonObj();
            }
        });

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

    void spin1Method() {
        v_type = (Spinner) rootView.findViewById(R.id.v_type);
        ArrayAdapter aa = new ArrayAdapter(getContext(),android.R.layout.simple_spinner_item,v_typeAr);
        aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //Setting the ArrayAdapter data on the Spinner
        v_type.setAdapter(aa);
        v_type.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> arg0, View view, int arg2, long arg3) {

            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
                // TODO Auto-generated method stub

            }
        });
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

    void mainJsonObj(){
        try {
            JSONObject manJsonObj = new JSONObject(manJson);
            if (dealerSearchM.getText().equals("") || dealerSearchM.getTag().equals("0")){
                Toast.makeText(getContext(),"Search a Dealer/Sub-Dealer !",Toast.LENGTH_LONG).show();
                return;
            }
            HashMap hashMap01= (HashMap) dealerSearchM.getTag();
            manJsonObj.put("partyId",hashMap01.get("lid"));
            manJsonObj.put("meetType",String.valueOf(v_type.getSelectedItemPosition()+1));


            if (dateTxt1.getTag().equals("0")){
                Toast.makeText(getContext(),"Please Select Meet Date ! ",Toast.LENGTH_LONG).show();
                return;
            }
            manJsonObj.put("meetDate",dateTxt1.getText());

            if (editTextBlankCheck(attendanceEdit).equals("")){
                Toast.makeText(getContext(),"Fill Number of planned attendees ! ",Toast.LENGTH_LONG).show();
                return;
            }
            manJsonObj.put("attendees",attendanceEdit.getText());

            if (editTextBlankCheck(qtyTxt).equals("")){
                Toast.makeText(getContext(),"Please Enter budget ! ",Toast.LENGTH_LONG).show();
                return;
            }
            manJsonObj.put("budget",qtyTxt.getText());
            manJsonObj.put("inititateSlid",sLid);

            saveMasionMeetInsitate saveDatabyServlet=new saveMasionMeetInsitate(getContext(),manJsonObj);
            saveDatabyServlet.execute();
        }catch (Exception e){
            Log.e("Error: ",e.toString());
        }
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

    class saveMasionMeetInsitate extends AsyncTask<String,Void,String> {
        private Context context;
        JSONObject answerOnj1=null;
        ProgressDialog progressDialog;
        public saveMasionMeetInsitate(Context context, JSONObject answerOnj1) {
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
                String link="http://"+dnsport+"/SteelSales-war/Stl_Masonmeet_C_Api";
                HashMap<String,String> hm=new HashMap<>();
                hm.put("cid",cid);
                hm.put("segid", segid);
                hm.put("uid", uid);    //or uid ask
                hm.put("data", answerOnj1.toString());
                hm.put("keyCode",tempKeyCode);

//                String veryLongString=answerOnj1.toString();
//                int maxLogSize = 1000;
//                for(int i = 0; i <= veryLongString.length() / maxLogSize; i++) {
//                    int start = i * maxLogSize;
//                    int end = (i+1) * maxLogSize;
//                    end = end > veryLongString.length() ? veryLongString.length() : end;
//                    Log.v("..........     ", veryLongString.substring(start, end));
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
            mFetchTask=null;
            //Log.e("........",result);
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
                        hashMap.put("rlb_slid",object12.getString("rlb_slid"));
                        hashMap.put("party_type_name",object12.getString("party_type_name"));
                        hashMap.put("last_lifting",object12.getString("last_lifting"));
                        hashMap.put("avrg_sale",object12.getString("avrg_sale"));
                        hashMap.put("district",object12.getString("district"));
                        hashMap.put("state",object12.getString("state"));
                        searchArrayList.add(hashMap);
                    }
                    DealerSearchAutoComp adapter = new DealerSearchAutoComp(getContext(), android.R.layout.simple_dropdown_item_1line, searchArrayList);
                    dealerSearchM.setAdapter(adapter);
                    dealerSearchM.setOnItemClickListener(onItemClickListener);
                    adapter.notifyDataSetChanged();
                }else {
                    Log.e(LOG_DBG,result);
                    Toast.makeText(context,mainObj.getString("msg"),Toast.LENGTH_SHORT).show();
                }
            }catch (Exception e){
                Toast.makeText(context,e.toString(),Toast.LENGTH_SHORT).show();
            }

        }

        private AdapterView.OnItemClickListener onItemClickListener =
                new AdapterView.OnItemClickListener(){
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                        HashMap<String,String> hashMap12= (HashMap<String, String>) adapterView.getItemAtPosition(i);
                        dealerSearchM.setText(hashMap12.get("party_name"));
                        dealerSearchM.setTag(hashMap12);
                        lable1.setText("State- "+hashMap12.get("state")+", District- "+hashMap12.get("district")+", Average sale- "+hashMap12.get("avrg_sale")+", Last-Lifting- "+hashMap12.get("last_lifting"));


                    }
                };

    }



}
