package com.aspl.steel.EngineerVisitG;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;

import com.aspl.steel.DealerVisitPRtp.DealerVisitPRptList;
import com.aspl.steel.DealerVisitPRtp.VisitPListAdapter;
import com.aspl.steel.GlobalConfiguration;
import com.aspl.steel.LoginActivity;
import com.aspl.steel.R;
import com.aspl.steel.Services.LogoutService;
import com.aspl.steel.SteelHelper;
import com.aspl.steel.adapters.DealerSearchAutoComp;
import com.aspl.steel.salesmanvisit.salesmanvisit_view;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

public class EngineerVisitRtp extends Fragment {

    String mainJsonStr="{\"dprNo\":null,\"dealerId\":null,\"dealerSearchType\":null,\"text3\":null,\"status\":1,\"text4\":null,\"text1\":null,\"text2\":null,\"text5\":null,\"cid\":1,\"uidUpdate\":null,\"txnDate\":null,\"id\":null,\"dtCrt\":null,\"dprSeries\":null,\"engineerId\":null,\"qustnAnswerDtlsList\":null,\"uidCrt\":null,\"salesmanId\":null,\"segid\":1,\"dtUpdate\":null,\"blankQustnAnswerDtlsObj\":null}";
    String questionJson="{\"questionId\":null,\"text3\":null,\"fuList\":null,\"status\":1,\"text4\":null,\"text1\":null,\"text2\":null,\"answer\":null,\"text5\":null,\"text6\":null,\"uidUpdate\":null,\"cid\":1,\"id\":null,\"dtCrt\":null,\"uidCrt\":null,\"segid\":1,\"dtUpdate\":null,\"fuObj\":null,\"rowNum\":null}";

    View rootView;
    DrawerLayout mDrawerLayout;
    SimpleDateFormat dateFormatter = new SimpleDateFormat("dd-MM-yyyy");
    String sLid, cid, segid, tempKeyCode, uid;
    final String LOG_DBG = getClass().getSimpleName();
    Date date = new Date();
    TextView que1,que2,que3,que4,que5,que6,que7,que8,que9,que10,que11;
    CheckBox que7_1,que7_2,que7_3,que7_4,que7_5,que7_6,que7_7;
    CheckBox que6_1,que6_2,que8_1,que8_2,que9_1,que9_2,que10_1,que10_2;

    Spinner que_s5,que_s9;
    AutoCompleteTextView engineerSearchM;
    FetchEngineerName mFetchTask;
    EditText queA2,queA4,queA10,queA11;
    TextView queA3,que1A_details;
    LinearLayout qt5condasion_lay,opt_9_lay;

    String[] question5 = { "Dealer Issues", "Stocks Not Availablel", "Quality Issues", "Schemes", "Price"};
    String[] question9 = { "Costly", "Not Available", "Poor Quality"};



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.engineer_visit_rptview, container, false);
        viewSet(rootView);
        SharedPreferences sharedPref = getActivity().getSharedPreferences("loginCredentials", Context.MODE_PRIVATE);
        cid = sharedPref.getString("cid", "");
        segid = sharedPref.getString("segid", "");
        tempKeyCode = sharedPref.getString("storedKeyCode", "");
        sLid = sharedPref.getString("storedSlid", "");
        uid = sharedPref.getString("storedUserId", "");
        TextView saveData=(TextView)rootView.findViewById(R.id.saveData);
        opt_9_lay=(LinearLayout)rootView.findViewById(R.id.opt_9_lay);
        qt5condasion_lay=(LinearLayout)rootView.findViewById(R.id.qt5condasion_lay);
        que1A_details=(TextView)rootView.findViewById(R.id.que1A_details);

        saveData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveDataJSONPrepare();
            }
        });
        engineerSearchM=(AutoCompleteTextView)rootView.findViewById(R.id.engineerSearchM);
        engineerSearchM.addTextChangedListener(new TextWatcher() {
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
                if(editable.length()>=1){
                    mFetchTask = new FetchEngineerName(getContext(), 1);
                    mFetchTask.execute(cid, segid, sLid, "dealer", editable.toString(), tempKeyCode);
                }

            }
        });
        holeViewQuestion();
        questionAnswer7();
        questionAnswer6();
        questionAnswer8();
        questionAnswer9();
        questionAnswer10();

        /*........... Question Servlet Call..........*/
        questionList questionList=new questionList(getContext());
        questionList.execute();

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

    void holeViewQuestion(){
        que1=(TextView)rootView.findViewById(R.id.que1);
        que2=(TextView)rootView.findViewById(R.id.que2);
        que3=(TextView)rootView.findViewById(R.id.que3);
        que4=(TextView)rootView.findViewById(R.id.que4);
        que5=(TextView)rootView.findViewById(R.id.que5);
        que6=(TextView)rootView.findViewById(R.id.que6);
        que7=(TextView)rootView.findViewById(R.id.que7);
        que8=(TextView)rootView.findViewById(R.id.que8);
        que9=(TextView)rootView.findViewById(R.id.que9);
        que10=(TextView)rootView.findViewById(R.id.que10);
        que11=(TextView)rootView.findViewById(R.id.que11);

        /* ........ Question 12 SUB Question ........*/
        que7_1=(CheckBox) rootView.findViewById(R.id.que7_1);
        que7_2=(CheckBox)rootView.findViewById(R.id.que7_2);
        que7_3=(CheckBox)rootView.findViewById(R.id.que7_3);
        que7_4=(CheckBox)rootView.findViewById(R.id.que7_4);
        que7_5=(CheckBox)rootView.findViewById(R.id.que7_5);
        que7_6=(CheckBox)rootView.findViewById(R.id.que7_6);
        que7_7=(CheckBox)rootView.findViewById(R.id.que7_7);

        /*.......... Spinner ......*/
        que_s5=(Spinner)rootView.findViewById(R.id.que_s5);
        que_s9=(Spinner)rootView.findViewById(R.id.que_s9);

        ArrayAdapter aa = new ArrayAdapter(getContext(),android.R.layout.simple_spinner_item,question5);
        aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //Setting the ArrayAdapter data on the Spinner
        que_s5.setAdapter(aa);

        ArrayAdapter aa1 = new ArrayAdapter(getContext(),android.R.layout.simple_spinner_item,question9);
        aa1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //Setting the ArrayAdapter data on the Spinner
        que_s9.setAdapter(aa1);

    }

    void questionAnswer7(){
        que7_1=(CheckBox)rootView.findViewById(R.id.que7_1);
        que7_2=(CheckBox)rootView.findViewById(R.id.que7_2);
        que7_3=(CheckBox)rootView.findViewById(R.id.que7_3);
        que7_4=(CheckBox)rootView.findViewById(R.id.que7_4);
        que7_5=(CheckBox)rootView.findViewById(R.id.que7_5);
        que7_6=(CheckBox)rootView.findViewById(R.id.que7_6);
        que7_7=(CheckBox)rootView.findViewById(R.id.que7_7);

        queA2=(EditText) rootView.findViewById(R.id.queA2);
        queA4=(EditText) rootView.findViewById(R.id.queA4);
        queA10=(EditText) rootView.findViewById(R.id.queA10);
        queA11=(EditText) rootView.findViewById(R.id.queA11);
        queA3=(TextView) rootView.findViewById(R.id.queA3);
        queA3.setText(dateFormatter.format(date));
        queA3.setTag(dateFormatter.format(date));
        queA3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Date Selection For Next Visit
                Calendar newCalender = Calendar.getInstance();
                DatePickerDialog dialog = new DatePickerDialog(getActivity(), new DatePickerDialog.OnDateSetListener() {

                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        Calendar newDate = Calendar.getInstance();
                        newDate.set(year, monthOfYear, dayOfMonth);
                        String strFromDt = dateFormatter.format(newDate.getTime());
                        queA3.setText(strFromDt);
                        queA3.setTag(strFromDt);
                    }

                }, newCalender.get(Calendar.YEAR), newCalender.get(Calendar.MONTH), newCalender.get(Calendar.DAY_OF_MONTH));
                dialog.setTitle("Select Date:");
                Calendar cal = Calendar.getInstance();
                cal.setTime(new Date());
                cal.add(Calendar.MONTH, -1);
                dialog.show();
            }
        });

    }

    void questionAnswer6(){
        que6_1=(CheckBox)rootView.findViewById(R.id.que6_1);
        que6_2=(CheckBox)rootView.findViewById(R.id.que6_2);
        que6_1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (que6_2.isChecked()) {
                    que6_2.setChecked(false);
                }
                qt5condasion_lay.setVisibility(View.GONE);
            }
        });

        que6_2.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (que6_1.isChecked()) {
                    que6_1.setChecked(false);
                }
                qt5condasion_lay.setVisibility(View.VISIBLE);
            }
        });

    }

    void questionAnswer8(){
        que8_1=(CheckBox)rootView.findViewById(R.id.que8_1);
        que8_2=(CheckBox)rootView.findViewById(R.id.que8_2);
        que8_1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (que8_2.isChecked()) {
                    que8_2.setChecked(false);
                }
            }
        });

        que8_2.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (que8_1.isChecked()) {
                    que8_1.setChecked(false);
                }
            }
        });

    }

    void questionAnswer9(){
        que9_1=(CheckBox)rootView.findViewById(R.id.que9_1);
        que9_2=(CheckBox)rootView.findViewById(R.id.que9_2);
        que9_1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (que9_2.isChecked()) {
                    que9_2.setChecked(false);
                }
                opt_9_lay.setVisibility(View.GONE);
            }
        });

        que9_2.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (que9_1.isChecked()) {
                    que9_1.setChecked(false);
                }
                opt_9_lay.setVisibility(View.VISIBLE);
            }
        });

    }

    void questionAnswer10(){
        que10_1=(CheckBox)rootView.findViewById(R.id.que10_1);
        que10_2=(CheckBox)rootView.findViewById(R.id.que10_2);
        que10_1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (que10_2.isChecked()) {
                    que10_2.setChecked(false);
                }
            }
        });

        que10_2.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (que10_1.isChecked()) {
                    que10_1.setChecked(false);
                }
            }
        });

    }


    /*.............. Dealer List by Location...........*/
    class questionList extends AsyncTask<String,Void,String> {
        private Context context;
        ProgressDialog progressDialog;
        public questionList(Context context) {
            this.context = context;

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
                String link="http://"+dnsport+"/SteelSales-war/Stl_EngineerQuestionnaire_C";
                HashMap<String,String> hm=new HashMap<>();    //type,q,keyCode,currentLat,currentlong
                hm.put("cid",cid);
                hm.put("segid", segid);
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
            }
            try{
                JSONObject main_template=new JSONObject(s);
                //Log.e("......111...",s);
                String msg=main_template.getString("msg");
                if(msg.contains("Authorization Failure..!")){
                    //Code for logout
                    Toast.makeText(context,"Session expired.",Toast.LENGTH_SHORT).show();
                    Intent logout_intent=new Intent(getActivity(), LogoutService.class);
                    getActivity().startService(logout_intent);
                    getActivity().finish();
                    return;
                }

                JSONArray qesList=main_template.getJSONArray("data");
                for (int i=0; i< qesList.length(); i++) {
                    JSONObject q_obj = qesList.getJSONObject(i);
                    String questionS = q_obj.getString("question");
                    String questionS_id = q_obj.getString("id");


                    if (i + 1 == 1) {
                        que2.setTag(questionS_id);
                        que2.setText(i + 1 + ".  " + questionS);
                    } else if (i + 1 == 2) {
                        que3.setTag(questionS_id);
                        que3.setText(i + 1 + ".  " + questionS);
                    } else if (i + 1 == 3) {
                        que4.setTag(questionS_id);
                        que4.setText(i + 1 + ".  " + questionS);
                    } else if (i + 1 == 4) {
                        que5.setTag(questionS_id);
                        que5.setText(i + 1 + ".  " + questionS);
                    } else if (i + 1 == 5) {
                        que6.setTag(questionS_id);
                        que6.setText(i + 1 + ".  " + questionS);
                    }else if (i + 1 == 6) {
                        que7.setTag(questionS_id);
                        que7.setText(i + 1 + ".  " + questionS);
                    }else if (i + 1 == 7) {
                        que7_1.setTag(questionS_id);
                        que7_1.setText("a.  " + questionS);
                    }else if (i + 1 == 8) {
                        que7_2.setTag(questionS_id);
                        que7_2.setText("b.  " + questionS);
                    }else if (i + 1 == 9) {
                        que7_3.setTag(questionS_id);
                        que7_3.setText("c.  " + questionS);
                    }else if (i + 1 == 10) {
                        que7_4.setTag(questionS_id);
                        que7_4.setText("d.  " + questionS);
                    }else if (i + 1 == 11) {
                        que7_5.setTag(questionS_id);
                        que7_5.setText("e.  " + questionS);
                    }else if (i + 1 == 12) {
                        que7_6.setTag(questionS_id);
                        que7_6.setText("f.  " + questionS);
                    }else if (i + 1 == 13) {
                        que7_7.setTag(questionS_id);
                        que7_7.setText("g.  " + questionS);
                    }else if (i + 1 == 14) {
                        que8.setTag(questionS_id);
                        que8.setText(7 + ".  " + questionS);
                    }else if (i + 1 == 15) {
                        que9.setTag(questionS_id);
                        que9.setText(8 + ".  " + questionS);
                    }else if (i + 1 == 16) {
                        que10.setTag(questionS_id);
                        que10.setText(9 + ".  " + questionS);
                    }else if (i + 1 == 17) {
                        que11.setTag(questionS_id);
                        que11.setText(10 + ".  " + questionS);
                    }
                }


            }catch (Exception e){
                Log.e("Question Error: ",e.toString());
            }
        }
    }


    /*.......... Manual Dealer Search..........*/
    public class FetchEngineerName extends AsyncTask<String, Void, String> {
        private Context context;
        private int byGetOrPost=0;          //Flag 0 means get and 1 means post. (By default it is get).

        FetchEngineerName(Context context,int flag) {
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
                    hm.put("sLid", "0");
                    hm.put("type", "engineer");
                    hm.put("q", query);
                    hm.put("keyCode", tempKeyCode);

//                    Log.e(".........",link);
//                    Log.e(".........",hm.toString());


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
                        hashMap.put("phone",object12.getString("phone"));
                        hashMap.put("district",object12.getString("district"));
                        searchArrayList.add(hashMap);
                    }
                    DealerSearchAutoComp adapter = new DealerSearchAutoComp(getContext(), android.R.layout.simple_dropdown_item_1line, searchArrayList);
                    engineerSearchM.setAdapter(adapter);
                    engineerSearchM.setOnItemClickListener(onItemClickListener);
                    adapter.notifyDataSetChanged();
                }else {
                    Log.e(LOG_DBG,result);
                    Toast.makeText(context,mainObj.getString("msg"),Toast.LENGTH_SHORT).show();
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
                        engineerSearchM.setText(hashMap12.get("party_name"));
                        engineerSearchM.setTag(hashMap12);
                        que1A_details.setVisibility(View.VISIBLE);
                        que1A_details.setText("Dis: "+hashMap12.get("district")+", "+hashMap12.get("phone"));

                    }
                };


    }


    String checkBoxValidation(CheckBox checkBox1,CheckBox checkBox2){
        if (checkBox1.isChecked()){
            return "Yes";
        }else if (checkBox2.isChecked()){
            return "No";
        }else if (!checkBox2.isChecked() && !checkBox2.isChecked()){
            return "";
        }
        return "";
    }

    String singlecheckBoxValidation(CheckBox checkBox1){
        if (checkBox1.isChecked()){
            return "Yes";
        }else if (!checkBox1.isChecked()){
            return "";
        }
        return "";
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

    void saveDataJSONPrepare(){

        JSONArray questionAr=new JSONArray();
        try {
            /* .......... Question 1 Answer.........*/
            String answer2 = editTextBlankCheck(queA2);
            if (answer2.equals("")) {
                Toast.makeText(getContext(), "You can't left blank Question 1!", Toast.LENGTH_LONG).show();
                return;
            }
            JSONObject questObj2 = new JSONObject(questionJson);
            questObj2.put("questionId", que2.getTag());
            questObj2.put("answer", answer2);
            questObj2.put("rowNum", 1);
            questionAr.put(questObj2);

            /* .......... Question 2 Answer.........*/
            if (queA3.getTag().equals("0")) {
                Toast.makeText(getContext(), "You can't left blank Question 2!", Toast.LENGTH_LONG).show();
                return;
            }
            JSONObject questObj3 = new JSONObject(questionJson);
            questObj3.put("questionId", que3.getTag());
            questObj3.put("answer", queA3.getText());
            questObj3.put("rowNum", 2);
            questionAr.put(questObj3);

            /* .......... Question 3 Answer.........*/
            String answer4 = editTextBlankCheck(queA4);
            if (answer4.equals("")) {
                Toast.makeText(getContext(), "You can't left blank Question 3!", Toast.LENGTH_LONG).show();
                return;
            }
            JSONObject questObj4 = new JSONObject(questionJson);
            questObj4.put("questionId", que4.getTag());
            questObj4.put("answer", answer4);
            questObj4.put("rowNum", 3);
            questionAr.put(questObj4);

            /* .......... Question 4 Answer.........*/
            JSONObject questObj5 = new JSONObject(questionJson);
            questObj5.put("questionId", que5.getTag());
            questObj5.put("answer", que_s5.getSelectedItem());
            questObj5.put("rowNum", 4);
            questionAr.put(questObj5);

            /* .......... Question 5 Answer.........*/
            String answer6 = checkBoxValidation(que6_1, que6_2);
            if (answer6.equals("")) {
                Toast.makeText(getContext(), "You can't left blank Question 5!", Toast.LENGTH_LONG).show();
                return;
            }
            JSONObject questObj6 = new JSONObject(questionJson);
            questObj6.put("questionId", que6.getTag());
            questObj6.put("answer", answer6);
            questObj6.put("rowNum", 5);

            String answer6_10 = editTextBlankCheck(queA10);
            if (answer6.equalsIgnoreCase("No") && answer6_10.equals("")) {
                Toast.makeText(getContext(), "You can't left blank Question 5 of reasons!", Toast.LENGTH_LONG).show();
                return;
            } else if (answer6.equalsIgnoreCase("No") && !answer6_10.equals("")){
                questObj6.put("text1", answer6_10);
            }
            questionAr.put(questObj6);


            /* .......... Question 6  sub Que-1 Answer.........*/
            String answer7_1=singlecheckBoxValidation(que7_1);
            JSONObject questObj7_1= new JSONObject(questionJson);
            questObj7_1.put("questionId",que7_1.getTag());
            questObj7_1.put("answer",answer7_1);
            questObj7_1.put("rowNum",6);
            questionAr.put(questObj7_1);

            /* .......... Question 6  sub Que-2 Answer.........*/
            String answer7_2=singlecheckBoxValidation(que7_2);
            JSONObject questObj7_2= new JSONObject(questionJson);
            questObj7_2.put("questionId",que7_2.getTag());
            questObj7_2.put("answer",answer7_2);
            questObj7_2.put("rowNum",7);
            questionAr.put(questObj7_2);

            /* .......... Question 6  sub Que-3 Answer.........*/
            String answer7_3=singlecheckBoxValidation(que7_3);
            JSONObject questObj7_3= new JSONObject(questionJson);
            questObj7_3.put("questionId",que7_3.getTag());
            questObj7_3.put("answer",answer7_3);
            questObj7_3.put("rowNum",8);
            questionAr.put(questObj7_3);

            /* .......... Question 6  sub Que-4 Answer.........*/
            String answer7_4=singlecheckBoxValidation(que7_4);
            JSONObject questObj7_4= new JSONObject(questionJson);
            questObj7_4.put("questionId",que7_4.getTag());
            questObj7_4.put("answer",answer7_4);
            questObj7_4.put("rowNum",9);
            questionAr.put(questObj7_4);

            /* .......... Question 6  sub Que-5 Answer.........*/
            String answer7_5=singlecheckBoxValidation(que7_5);
            JSONObject questObj7_5= new JSONObject(questionJson);
            questObj7_5.put("questionId",que7_5.getTag());
            questObj7_5.put("answer",answer7_5);
            questObj7_5.put("rowNum",10);
            questionAr.put(questObj7_5);

            /* .......... Question 6  sub Que-6 Answer.........*/
            String answer7_6=singlecheckBoxValidation(que7_6);
            JSONObject questObj7_6= new JSONObject(questionJson);
            questObj7_6.put("questionId",que7_6.getTag());
            questObj7_6.put("answer",answer7_6);
            questObj7_6.put("rowNum",11);
            questionAr.put(questObj7_6);

            /* .......... Question 6  sub Que-7 Answer.........*/
            String answer7_7=singlecheckBoxValidation(que7_7);
            JSONObject questObj7_7= new JSONObject(questionJson);
            questObj7_7.put("questionId",que7_7.getTag());
            questObj7_7.put("answer",answer7_7);
            questObj7_7.put("rowNum",12);
            questionAr.put(questObj7_7);

            /* .......... Question 7 Answer.........*/
            String answer8=checkBoxValidation(que8_1,que8_2);
            if (answer8.equals("")){
                Toast.makeText(getContext(),"You can't left blank Question 7!",Toast.LENGTH_LONG).show();
                return;
            }
            JSONObject questObj8= new JSONObject(questionJson);
            questObj8.put("questionId",que8.getTag());
            questObj8.put("answer",answer8);
            questObj8.put("rowNum",13);
            questionAr.put(questObj8);

            /* .......... Question 8 Answer.........*/
            String answer9=checkBoxValidation(que9_1,que9_2);
            if (answer9.equals("")){
                Toast.makeText(getContext(),"You can't left blank Question 8!",Toast.LENGTH_LONG).show();
                return;
            }
            JSONObject questObj9= new JSONObject(questionJson);
            questObj9.put("questionId",que9.getTag());
            questObj9.put("answer",answer9);
            questObj9.put("rowNum",14);

            if (answer9.equalsIgnoreCase("No")) {
                questObj9.put("text1", que_s9.getSelectedItem());
            }
            questionAr.put(questObj9);

            /* .......... Question 9 Answer.........*/
            String answer10=checkBoxValidation(que10_1,que10_2);
            if (answer10.equals("")){
                Toast.makeText(getContext(),"You can't left blank Question 9!",Toast.LENGTH_LONG).show();
                return;
            }
            JSONObject questObj10= new JSONObject(questionJson);
            questObj10.put("questionId",que10.getTag());
            questObj10.put("answer",answer10);
            questObj10.put("rowNum",15);
            questionAr.put(questObj10);



            /* .......... Question 10 Answer.........*/
            String answer11=editTextBlankCheck(queA11);
            if (answer11.equals("")){
                Toast.makeText(getContext(),"You can't left blank Question 10!",Toast.LENGTH_LONG).show();
                return;
            }
            JSONObject questObj11= new JSONObject(questionJson);
            questObj11.put("questionId",que11.getTag());
            questObj11.put("answer", answer11);
            questObj11.put("rowNum",16);
            questionAr.put(questObj11);

            /*............ Put All Data in Main JSON........*/
            JSONObject questObjMainJson= new JSONObject(mainJsonStr);
            questObjMainJson.put("qustnAnswerDtlsList",questionAr);
            try {
                if (engineerSearchM.getText().equals("") || engineerSearchM.getTag().equals("0")){
                    Toast.makeText(getContext(),"Search a Engineer !",Toast.LENGTH_LONG).show();
                    return;
                }
                questObjMainJson.put("dealerSearchType","Manual");
                HashMap hashMap01= (HashMap) engineerSearchM.getTag();
                questObjMainJson.put("engineerId",hashMap01.get("lid"));
                questObjMainJson.put("dealerId",0);
                questObjMainJson.put("salesmanId",sLid);
            }catch (Exception e){
                Toast.makeText(getContext(),"Please Search a Dealer !",Toast.LENGTH_LONG).show();
            }
            questObjMainJson.put("cid",cid);
            questObjMainJson.put("segid",segid);

            saveDatabyServlet saveDatabyServlet=new saveDatabyServlet(getContext(),questObjMainJson);
            saveDatabyServlet.execute();

        } catch (JSONException e) { e.printStackTrace(); }

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
                String link="http://"+dnsport+"/SteelSales-war/Stl_Engineer_QstnAnswr_C_Api";
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
                Toast.makeText(context,"Unable to connect to server. Please check your Internet connection.",Toast.LENGTH_SHORT).show();
            }
        }
    }


}