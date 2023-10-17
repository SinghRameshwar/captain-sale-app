package com.aspl.steel.NewDealerVisit;

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
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
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
import com.aspl.steel.salesmanvisit.salesmanvisit_view;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

public class NewDealerVisitView extends Fragment {

    String mainJSon="{\n" +
            "  \"dealerId\": null,\n" +
            "  \"text3\": null,\n" +
            "  \"dealerSearchType\": null,\n" +
            "  \"text4\": null,\n" +
            "  \"status\": 1,\n" +
            "  \"text1\": null,\n" +
            "  \"text2\": null,\n" +
            "  \"text5\": null,\n" +
            "  \"cid\": 1,\n" +
            "  \"uidUpdate\": null,\n" +
            "  \"id\": null,\n" +
            "  \"dtCrt\": null,\n" +
            "  \"qustnAnswerDtlsList\": null,\n" +
            "  \"uidCrt\": null,\n" +
            "  \"salesmanId\": null,\n" +
            "  \"segid\": 1,\n" +
            "  \"dtUpdate\": null,\n" +
            "  \"blankQustnAnswerDtlsObj\": {\n" +
            "    \"questionId\": 1,\n" +
            "    \"text3\": null,\n" +
            "    \"fuList\": null,\n" +
            "    \"status\": 1,\n" +
            "    \"text1\": null,\n" +
            "    \"text2\": null,\n" +
            "    \"answer\": \"None\",\n" +
            "    \"cid\": 1,\n" +
            "    \"uidUpdate\": null,\n" +
            "    \"id\": null,\n" +
            "    \"dtCrt\": null,\n" +
            "    \"uidCrt\": null,\n" +
            "    \"segid\": 1,\n" +
            "    \"dtUpdate\": null,\n" +
            "    \"fuObj\": {\n" +
            "      \"dt\": null,\n" +
            "      \"descrptn\": null,\n" +
            "      \"isConverted\": null,\n" +
            "      \"visible\": null,\n" +
            "      \"xlXmlUploadStatus\": null,\n" +
            "      \"txnExist\": null,\n" +
            "      \"refId\": null,\n" +
            "      \"sourceId\": null,\n" +
            "      \"id\": null,\n" +
            "      \"name\": null,\n" +
            "      \"path\": null,\n" +
            "      \"segid\": 1,\n" +
            "      \"revid\": null,\n" +
            "      \"orderId\": null,\n" +
            "      \"refType\": null,\n" +
            "      \"tags\": null,\n" +
            "      \"fileType\": null,\n" +
            "      \"docName\": null,\n" +
            "      \"moduleId\": null,\n" +
            "      \"text3\": null,\n" +
            "      \"txnid\": null,\n" +
            "      \"status\": 1,\n" +
            "      \"text4\": null,\n" +
            "      \"arrayByte\": null,\n" +
            "      \"text1\": null,\n" +
            "      \"text2\": null,\n" +
            "      \"pid\": null,\n" +
            "      \"text5\": null,\n" +
            "      \"cid\": 1,\n" +
            "      \"xlXmlErrorMsg\": null,\n" +
            "      \"resizeFilepath\": null,\n" +
            "      \"source\": null,\n" +
            "      \"byteArray\": null,\n" +
            "      \"fileName\": null,\n" +
            "      \"docId\": null\n" +
            "    },\n" +
            "    \"rowNum\": null\n" +
            "  }\n" +
            "}";

    String questionJson="{\n" +
            "  \"questionId\": \"\",\n" +
            "  \"text3\": null,\n" +
            "  \"fuList\": null,\n" +
            "  \"status\": 1,\n" +
            "  \"text1\": null,\n" +
            "  \"text2\": null,\n" +
            "  \"answer\": \"\",\n" +
            "  \"cid\": 1,\n" +
            "  \"uidUpdate\": null,\n" +
            "  \"id\": null,\n" +
            "  \"dtCrt\": null,\n" +
            "  \"uidCrt\": null,\n" +
            "  \"segid\": 1,\n" +
            "  \"dtUpdate\": null,\n" +
            "  \"fuObj\": null,\n" +
            "  \"rowNum\": null\n" +
            "}";



    View rootView;
    DrawerLayout mDrawerLayout;
    String[] metting_party = { "Already selling competitor", "Captain Has Low brand pull", "Price is not lucrative", "Scheme not attractive", "Others(Please specify in Remarks)"};
    SimpleDateFormat dateFormatter=new SimpleDateFormat("dd-MM-yyyy");
    Spinner que_s1;
    CheckBox que3_1,que3_2,que4_1,que4_2,que7_1,que7_2;
    EditText queA1,queA2,queA2new,queA9,queA10,queA12_1,queA12_2,queA12_3,queA12_4,queA12_5,queA12_6,queA12_7;
    TextView que12_1,que12_2,que12_3,que12_4,que12_5,que12_6,que12_7;
    CheckBox que12_1_1,que12_1_2,que12_2_1,que12_2_2,que12_3_1,que12_3_2,que12_4_1,que12_4_2,que12_5_1,que12_5_2,que12_6_1,que12_6_2,que12_7_1,que12_7_2;
    TextView queA5,queA8;
    TextView que1,que2,que2new,que3,que4,que5,que6,que7,que8,que9,que10,que12;
    String  sLid,cid,segid,tempKeyCode,uid;
    final String LOG_DBG=getClass().getSimpleName();


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.new_dealer_visit_view, container, false);
        viewSet(rootView);
        SharedPreferences sharedPref = getActivity().getSharedPreferences("loginCredentials", Context.MODE_PRIVATE);
        cid=sharedPref.getString("cid", "");
        segid=sharedPref.getString("segid", "");
        tempKeyCode=sharedPref.getString("storedKeyCode","");
        sLid=sharedPref.getString("storedSlid","");
        uid=sharedPref.getString("storedUserId","");
        TextView saveData=(TextView)rootView.findViewById(R.id.saveData);
        saveData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveDataJSONPrepare();
            }
        });

        /*.......... View Refrence call.........*/
        spinnerviewQe1();
        holeViewAnswer();
        holeViewQuestion();
        questionAnswer3();
        questionAnswer4();
        questionAnswer7();

        /*............ Question Answare 12 of sub Answer .........*/
        questionAnswer12subQ_1();
        questionAnswer12subQ_2();
        questionAnswer12subQ_3();
        questionAnswer12subQ_4();
        questionAnswer12subQ_5();
        questionAnswer12subQ_6();
        questionAnswer12subQ_7();

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

    void holeViewAnswer(){
        queA1=(EditText)rootView.findViewById(R.id.queA1);
        queA2=(EditText)rootView.findViewById(R.id.queA2);
        queA2new=(EditText)rootView.findViewById(R.id.queA2new);
        queA9=(EditText)rootView.findViewById(R.id.queA9);
        queA10=(EditText)rootView.findViewById(R.id.queA10);
        queA5=(TextView)rootView.findViewById(R.id.queA5);
        queA8=(TextView)rootView.findViewById(R.id.queA8);
        queA12_1=(EditText)rootView.findViewById(R.id.queA12_1);
        queA12_2=(EditText)rootView.findViewById(R.id.queA12_2);
        queA12_3=(EditText)rootView.findViewById(R.id.queA12_3);
        queA12_4=(EditText)rootView.findViewById(R.id.queA12_4);
        queA12_5=(EditText)rootView.findViewById(R.id.queA12_5);
        queA12_6=(EditText)rootView.findViewById(R.id.queA12_6);
        queA12_7=(EditText)rootView.findViewById(R.id.queA12_7);

        queA8.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MonthYearPickerDialog pd = new MonthYearPickerDialog();
                pd.show(getFragmentManager(), "MonthYearPickerDialog");
            }
        });

        queA5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Date Selection For Next Visit
                Calendar newCalender = Calendar.getInstance();
                DatePickerDialog dialog = new DatePickerDialog(getActivity(), new DatePickerDialog.OnDateSetListener() {

                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        Calendar newDate = Calendar.getInstance();
                        newDate.set(year, monthOfYear, dayOfMonth);
                        String strFromDt = dateFormatter.format(newDate.getTime());
                        queA5.setText(strFromDt);
                        queA5.setTag(strFromDt);
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

    void holeViewQuestion(){
        que1=(TextView)rootView.findViewById(R.id.que1);
        que2=(TextView)rootView.findViewById(R.id.que2);
        que2new=(TextView)rootView.findViewById(R.id.que2new);

        que3=(TextView)rootView.findViewById(R.id.que3);
        que4=(TextView)rootView.findViewById(R.id.que4);
        que5=(TextView)rootView.findViewById(R.id.que5);
        que6=(TextView)rootView.findViewById(R.id.que6);
        que7=(TextView)rootView.findViewById(R.id.que7);
        que8=(TextView)rootView.findViewById(R.id.que8);
        que9=(TextView)rootView.findViewById(R.id.que9);
        que10=(TextView)rootView.findViewById(R.id.que10);
        que12=(TextView)rootView.findViewById(R.id.que12);

        /* ........ Question 12 SUB Question ........*/
        que12_1=(TextView)rootView.findViewById(R.id.que12_1);
        que12_2=(TextView)rootView.findViewById(R.id.que12_2);
        que12_3=(TextView)rootView.findViewById(R.id.que12_3);
        que12_4=(TextView)rootView.findViewById(R.id.que12_4);
        que12_5=(TextView)rootView.findViewById(R.id.que12_5);
        que12_6=(TextView)rootView.findViewById(R.id.que12_6);
        que12_7=(TextView)rootView.findViewById(R.id.que12_7);

    }

    void spinnerviewQe1(){
        que_s1 = (Spinner) rootView.findViewById(R.id.que_s1);
        ArrayAdapter aa = new ArrayAdapter(getContext(),android.R.layout.simple_spinner_item,metting_party);
        aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //Setting the ArrayAdapter data on the Spinner
        que_s1.setAdapter(aa);
    }

    void questionAnswer3(){
        que3_1=(CheckBox)rootView.findViewById(R.id.que3_1);
        que3_2=(CheckBox)rootView.findViewById(R.id.que3_2);
        que3_1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (que3_2.isChecked()) {
                    que3_2.setChecked(false);
                }
            }
        });

        que3_2.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (que3_1.isChecked()) {
                    que3_1.setChecked(false);
                }
            }
        });

    }

    void questionAnswer4(){
        que4_1=(CheckBox)rootView.findViewById(R.id.que4_1);
        que4_2=(CheckBox)rootView.findViewById(R.id.que4_2);
        que4_1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                que4_2.setChecked(false);
            }
        });

        que4_2.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                que4_1.setChecked(false);
            }
        });

    }

    void questionAnswer7(){
        que7_1=(CheckBox)rootView.findViewById(R.id.que7_1);
        que7_2=(CheckBox)rootView.findViewById(R.id.que7_2);
        que7_1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                que7_2.setChecked(false);
            }
        });

        que7_2.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                que7_1.setChecked(false);
            }
        });

    }

    void questionAnswer12subQ_1(){
        que12_1_1=(CheckBox)rootView.findViewById(R.id.que12_1_1);
        que12_1_2=(CheckBox)rootView.findViewById(R.id.que12_1_2);
        que12_1_1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                que12_1_2.setChecked(false);
                queA12_1.setEnabled(true);
                queA12_1.requestFocus();


            }
        });

        que12_1_2.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                que12_1_1.setChecked(false);
                queA12_1.setEnabled(false);
                queA12_1.setText("");

            }
        });
    }

    void questionAnswer12subQ_2(){
        que12_2_1=(CheckBox)rootView.findViewById(R.id.que12_2_1);
        que12_2_2=(CheckBox)rootView.findViewById(R.id.que12_2_2);
        que12_2_1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                que12_2_2.setChecked(false);
                queA12_2.setEnabled(true);
                queA12_2.requestFocus();
            }
        });

        que12_2_2.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                que12_2_1.setChecked(false);
                queA12_2.setEnabled(false);
                queA12_2.setText("");
            }
        });
    }

    void questionAnswer12subQ_3(){
        que12_3_1=(CheckBox)rootView.findViewById(R.id.que12_3_1);
        que12_3_2=(CheckBox)rootView.findViewById(R.id.que12_3_2);
        que12_3_1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                que12_3_2.setChecked(false);
                queA12_3.setEnabled(true);
                queA12_3.requestFocus();
            }
        });

        que12_3_2.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                que12_3_1.setChecked(false);
                queA12_3.setEnabled(false);
                queA12_3.setText("");
            }
        });
    }

    void questionAnswer12subQ_4(){
        que12_4_1=(CheckBox)rootView.findViewById(R.id.que12_4_1);
        que12_4_2=(CheckBox)rootView.findViewById(R.id.que12_4_2);
        que12_4_1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                que12_4_2.setChecked(false);
                queA12_4.setEnabled(true);
                queA12_4.requestFocus();
            }
        });

        que12_4_2.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                que12_4_1.setChecked(false);
                queA12_4.setEnabled(false);
                queA12_4.setText("");
            }
        });
    }

    void questionAnswer12subQ_5(){
        que12_5_1=(CheckBox)rootView.findViewById(R.id.que12_5_1);
        que12_5_2=(CheckBox)rootView.findViewById(R.id.que12_5_2);
        que12_5_1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                que12_5_2.setChecked(false);
                queA12_5.setEnabled(true);
                queA12_5.requestFocus();
            }
        });

        que12_5_2.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                que12_5_1.setChecked(false);
                queA12_5.setEnabled(false);
                queA12_5.setText("");
            }
        });
    }

    void questionAnswer12subQ_6(){
        que12_6_1=(CheckBox)rootView.findViewById(R.id.que12_6_1);
        que12_6_2=(CheckBox)rootView.findViewById(R.id.que12_6_2);
        que12_6_1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                que12_6_2.setChecked(false);
                queA12_6.setEnabled(true);
                queA12_6.requestFocus();
            }
        });

        que12_6_2.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                que12_6_1.setChecked(false);
                queA12_6.setEnabled(false);
                queA12_6.setText("");
            }
        });
    }

    void questionAnswer12subQ_7(){
        que12_7_1=(CheckBox)rootView.findViewById(R.id.que12_7_1);
        que12_7_2=(CheckBox)rootView.findViewById(R.id.que12_7_2);
        que12_7_1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                que12_7_2.setChecked(false);
                queA12_7.setEnabled(true);
                queA12_7.requestFocus();
            }
        });

        que12_7_2.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                que12_7_1.setChecked(false);
                queA12_7.setEnabled(false);
                queA12_7.setText("");
            }
        });
    }

    void saveDataJSONPrepare(){

        JSONArray questionAr=new JSONArray();
        try {
            /* .......... Question 1 Answer.........*/
            String answer1=editTextBlankCheck(queA1);
            if (answer1.equals("")){
                Toast.makeText(getContext(),"You can't left blank Question 1!",Toast.LENGTH_LONG).show();
                return;
            }
            JSONObject questObj1= new JSONObject(questionJson);
            questObj1.put("questionId",que1.getTag());
            questObj1.put("answer",answer1);
            questObj1.put("rowNum",1);
            questionAr.put(questObj1);

            /* .......... Question 2 Answer.........*/
            String answer2=editTextBlankCheck(queA2);
            if (answer2.equals("")){
                Toast.makeText(getContext(),"You can't left blank Question 3!",Toast.LENGTH_LONG).show();
                return;
            }
            JSONObject questObj2= new JSONObject(questionJson);
            questObj2.put("questionId",que2.getTag());
            questObj2.put("answer",answer2);
            questObj2.put("rowNum",3);
            questionAr.put(questObj2);

            /* .......... Question 2 new Answer.........*/
            String answer2new=editTextBlankCheck(queA2new);
            if (answer2new.equals("")){
                Toast.makeText(getContext(),"You can't left blank Question 2!",Toast.LENGTH_LONG).show();
                return;
            }
            JSONObject questObj2new= new JSONObject(questionJson);
            questObj2new.put("questionId",que2new.getTag());
            questObj2new.put("answer",answer2new);
            questObj2new.put("rowNum",2);
            questionAr.put(questObj2new);

            /* .......... Question 3 Answer.........*/
            String answer3=checkBoxValidation(que3_1,que3_2);
            if (answer3.equals("")){
                Toast.makeText(getContext(),"You can't left blank Question 4!",Toast.LENGTH_LONG).show();
                return;
            }
            JSONObject questObj3= new JSONObject(questionJson);
            questObj3.put("questionId",que3.getTag());
            questObj3.put("answer",answer3);
            questObj3.put("rowNum",4);
            questionAr.put(questObj3);

            /* .......... Question 4 Answer.........*/
            String answer4=checkBoxValidation(que4_1,que4_2);
            if (answer4.equals("")){
                Toast.makeText(getContext(),"You can't left blank Question 7!",Toast.LENGTH_LONG).show();
                return;
            }
            JSONObject questObj4= new JSONObject(questionJson);
            questObj4.put("questionId",que4.getTag());
            questObj4.put("answer",answer4);
            questObj4.put("rowNum",14);
            questionAr.put(questObj4);

            /* .......... Question 5 Answer.........*/
            if (queA5.getTag().equals("0") &&  answer4.equalsIgnoreCase("Yes")){
                Toast.makeText(getContext(),"You can't left blank Question 11!",Toast.LENGTH_LONG).show();
                return;
            }
            JSONObject questObj5= new JSONObject(questionJson);
            questObj5.put("questionId",que5.getTag());
            questObj5.put("answer",queA5.getText());
            questObj5.put("rowNum",18);
            questionAr.put(questObj5);

            /* .......... Question 6 Answer.........*/
            JSONObject questObj6= new JSONObject(questionJson);
            questObj6.put("questionId",que6.getTag());
            questObj6.put("answer",que_s1.getSelectedItem());
            questObj6.put("rowNum",15);
            questionAr.put(questObj6);

            /* .......... Question 12 Answer.........*/
            String answer12=checkBoxValidation(que12_1_1,que12_1_2);
            if (answer12.equals("")){
                Toast.makeText(getContext(),"You can't left blank Question 6 of (i)!",Toast.LENGTH_LONG).show();
                return;
            }
            JSONObject questObj12= new JSONObject(questionJson);
            questObj12.put("questionId",que12_1.getTag());
            questObj12.put("text1",answer12);
            try {
                if (answer12.equalsIgnoreCase("Yes")) {
                    String edit12_1=editTextBlankCheck(queA12_1);
                    if (edit12_1.equals("")){
                        Toast.makeText(getContext(),"You can't left blank Question 6 of (i)!",Toast.LENGTH_LONG).show();
                        return;
                    }else {
                        questObj12.put("answer", queA12_1.getText());
                    }
                }else{
                    questObj12.put("answer", "");
                }
            }catch (Exception e){}
            questObj12.put("rowNum",6);
            questionAr.put(questObj12);

            /* .......... Question 12    ----  13 Answer.........*/
            String answer13=checkBoxValidation(que12_2_1,que12_2_2);
            if (answer13.equals("")){
                Toast.makeText(getContext(),"You can't left blank Question 6 of (ii)!",Toast.LENGTH_LONG).show();
                return;
            }
            JSONObject questObj13= new JSONObject(questionJson);
            questObj13.put("questionId",que12_2.getTag());
            questObj13.put("text1",answer13);
            try {
                if (answer13.equalsIgnoreCase("Yes")) {
                    String edit12_2=editTextBlankCheck(queA12_2);
                    if (edit12_2.equals("")){
                        Toast.makeText(getContext(),"You can't left blank Question 6 of (ii)!",Toast.LENGTH_LONG).show();
                        return;
                    }else {
                        questObj13.put("answer", queA12_2.getText());
                    }
                }else{
                    questObj13.put("answer", "");
                }
            }catch (Exception e){}
            questObj13.put("rowNum",7);
            questionAr.put(questObj13);

            /* .......... Question 12    ----14 Answer.........*/
            String answer14=checkBoxValidation(que12_3_1,que12_3_2);
            if (answer14.equals("")){
                Toast.makeText(getContext(),"You can't left blank Question 6 of (iii)!",Toast.LENGTH_LONG).show();
                return;
            }
            JSONObject questObj14= new JSONObject(questionJson);
            questObj14.put("questionId",que12_3.getTag());
            questObj14.put("text1",answer14);
            try {
                if (answer14.equalsIgnoreCase("Yes")) {
                    String edit12_3=editTextBlankCheck(queA12_3);
                    if (edit12_3.equals("")){
                        Toast.makeText(getContext(),"You can't left blank Question 6 of (iii)!",Toast.LENGTH_LONG).show();
                        return;
                    }else {
                        questObj14.put("answer", queA12_3.getText());
                    }
                }else{
                    questObj14.put("answer", "");
                }
            }catch (Exception e){}
            questObj14.put("rowNum",8);
            questionAr.put(questObj14);

            /* .......... Question 12    ----15 Answer.........*/
            String answer15=checkBoxValidation(que12_4_1,que12_4_2);
            if (answer15.equals("")){
                Toast.makeText(getContext(),"You can't left blank Question 6 of (iv)!",Toast.LENGTH_LONG).show();
                return;
            }
            JSONObject questObj15= new JSONObject(questionJson);
            questObj15.put("questionId",que12_4.getTag());
            questObj15.put("text1",answer15);
            try {
                if (answer15.equalsIgnoreCase("Yes")) {
                    String edit12_4=editTextBlankCheck(queA12_4);
                    if (edit12_4.equals("")){
                        Toast.makeText(getContext(),"You can't left blank Question 6 of (iv)!",Toast.LENGTH_LONG).show();
                        return;
                    }else {
                        questObj15.put("answer", queA12_4.getText());
                    }
                }else{
                    questObj15.put("answer", "");
                }
            }catch (Exception e){}
            questObj15.put("rowNum",9);
            questionAr.put(questObj15);

            /* .......... Question 12    ----16 Answer.........*/
            String answer16=checkBoxValidation(que12_5_1,que12_5_2);
            if (answer16.equals("")){
                Toast.makeText(getContext(),"You can't left blank Question 6 of (v)!",Toast.LENGTH_LONG).show();
                return;
            }
            JSONObject questObj16= new JSONObject(questionJson);
            questObj16.put("questionId",que12_5.getTag());
            questObj16.put("text1",answer16);
            try {
                if (answer16.equalsIgnoreCase("Yes")) {
                    String edit12_5=editTextBlankCheck(queA12_5);
                    if (edit12_5.equals("")){
                        Toast.makeText(getContext(),"You can't left blank Question 6 of (v)!",Toast.LENGTH_LONG).show();
                        return;
                    }else {
                        questObj16.put("answer", queA12_5.getText());
                    }
                }else{
                    questObj16.put("answer", "");
                }
            }catch (Exception e){}
            questObj16.put("rowNum",10);
            questionAr.put(questObj16);

            /* .......... Question 12    ----17 Answer.........*/
            String answer17=checkBoxValidation(que12_6_1,que12_6_2);
            if (answer17.equals("")){
                Toast.makeText(getContext(),"You can't left blank Question 6 of (vi)!",Toast.LENGTH_LONG).show();
                return;
            }
            JSONObject questObj17= new JSONObject(questionJson);
            questObj17.put("questionId",que12_6.getTag());
            questObj17.put("text1",answer17);
            try {
                if (answer17.equalsIgnoreCase("Yes")) {
                    String edit12_6=editTextBlankCheck(queA12_6);
                    if (edit12_6.equals("")){
                        Toast.makeText(getContext(),"You can't left blank Question 6 of (vi)!",Toast.LENGTH_LONG).show();
                        return;
                    }else {
                        questObj17.put("answer", queA12_6.getText());
                    }
                }else{
                    questObj17.put("answer", "");
                }
            }catch (Exception e){}
            questObj17.put("rowNum",11);
            questionAr.put(questObj17);

            /* .......... Question 12    ----18 Answer.........*/
            String answer18=checkBoxValidation(que12_7_1,que12_7_2);
            if (answer18.equals("")){
                Toast.makeText(getContext(),"You can't left blank Question 6 of (vii)!",Toast.LENGTH_LONG).show();
                return;
            }
            JSONObject questObj18= new JSONObject(questionJson);
            questObj18.put("questionId",que12_7.getTag());
            questObj18.put("text1",answer18);
            try {
                if (answer18.equalsIgnoreCase("Yes")) {
                    String edit12_7=editTextBlankCheck(queA12_7);
                    if (edit12_7.equals("")){
                        Toast.makeText(getContext(),"You can't left blank Question 6 of (vii)!",Toast.LENGTH_LONG).show();
                        return;
                    }else {
                        questObj18.put("answer", queA12_7.getText());
                    }
                }else{
                    questObj18.put("answer", "");
                }
            }catch (Exception e){}
            questObj18.put("rowNum",12);
            questionAr.put(questObj18);

            /* .......... Question 8 .........*/
            String answer19=checkBoxValidation(que7_1,que7_2);
            if (answer19.equals("") &&  answer4.equalsIgnoreCase("Yes")){
                Toast.makeText(getContext(),"You can't left blank Question 9!",Toast.LENGTH_LONG).show();
                return;
            }
            JSONObject questObj19= new JSONObject(questionJson);
            questObj19.put("questionId",que7.getTag());
            questObj19.put("answer",answer19);
            questObj19.put("rowNum",16);
            questionAr.put(questObj19);

            /* .......... Question 9 .........*/

            if (queA8.getTag().equals("0") &&  answer4.equalsIgnoreCase("Yes")){
                Toast.makeText(getContext(),"You can't left blank Question 10!",Toast.LENGTH_LONG).show();
                return;
            }
            JSONObject questObj20= new JSONObject(questionJson);
            questObj20.put("questionId",que8.getTag());
            questObj20.put("answer",queA8.getText());
            questObj20.put("rowNum",17);
            questionAr.put(questObj20);

            /* .......... Question 10 Answer.........*/
            String answer21=editTextBlankCheck(queA9);
            if (answer21.equals("")){
                Toast.makeText(getContext(),"You can't left blank Question 5!",Toast.LENGTH_LONG).show();
                return;
            }

            JSONObject questObj21= new JSONObject(questionJson);
            questObj21.put("questionId",que9.getTag());
            try {
                questObj21.put("answer", answer21);
            }catch (Exception e){}
            questObj21.put("rowNum",5);
            questionAr.put(questObj21);

            /* .......... Question 11 Answer.........*/
            String answer22=editTextBlankCheck(queA10);
            if (answer22.equals("")){
                Toast.makeText(getContext(),"You can't left blank Question 12!",Toast.LENGTH_LONG).show();
                return;
            }

            JSONObject questObj22= new JSONObject(questionJson);
            questObj22.put("questionId",que10.getTag());
            try {
                questObj22.put("answer", answer22);
            }catch (Exception e){}
            questObj22.put("rowNum",19);
            questionAr.put(questObj22);

            /*............ Put All Data in Main JSON........*/
            JSONObject questObjMainJson= new JSONObject(mainJSon);
            questObjMainJson.put("qustnAnswerDtlsList",questionAr);

            questObjMainJson.put("dealerSearchType","");
            questObjMainJson.put("dealerId","0");
            questObjMainJson.put("salesmanId",sLid);

            questObjMainJson.put("cid",cid);
            questObjMainJson.put("segid",segid);

            saveDatabyServlet saveDatabyServlet=new saveDatabyServlet(getContext(),questObjMainJson);
            saveDatabyServlet.execute();

        } catch (JSONException e) { e.printStackTrace(); }

    }

    String checkBoxValidation(CheckBox checkBox1,CheckBox checkBox2){
        if (checkBox1.isChecked()){
            return checkBox1.getText().toString();
        }else if (checkBox2.isChecked()){
            return checkBox2.getText().toString();
        }else if (!checkBox2.isChecked()){
            return "";
        }else if (!checkBox2.isChecked()){
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
                String link="http://"+dnsport+"/SteelSales-war/Stl_SalesmanQuestionnaire_NewDealer_C";
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
                //Log.e(".........",s);
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

                    if (i+1==1){
                        que1.setTag(questionS_id);
                        que1.setText(i+1+".  "+questionS);
                    }else if(i+1==2) {
                        que2new.setTag(questionS_id);
                        que2new.setText(i + 1 + ".  " + questionS);
                    }else if(i+1==3){
                        que2.setTag(questionS_id);
                        que2.setText(i+1+".  "+questionS);
                    }else if(i+1==4){
                        que3.setTag(questionS_id);
                        que3.setText(i+1+".  "+questionS);
                    }else if(i+1==14){
                        que4.setTag(questionS_id);
                        que4.setText(7+".  "+questionS);
                    }else if(i+1==18){
                        que5.setTag(questionS_id);
                        que5.setText(11+".  "+questionS);
                    }else if(i+1==15){
                        que6.setTag(questionS_id);
                        que6.setText(8+".  "+questionS);
                    }
                    /*.......... SUB Question of 12.........*/

                    else if(i+1==6){
                       que12.setTag(questionS_id);
                       que12.setText(i+1+".  "+questionS);
                    }

                    else if(i+1==7){
                        que12_1.setTag(questionS_id);
                        que12_1.setText("     (i)"+"  "+questionS);
                    }else if(i+1==8){
                        que12_2.setTag(questionS_id);
                        que12_2.setText("     (ii)"+"  "+questionS);
                    }else if(i+1==9){
                        que12_3.setTag(questionS_id);
                        que12_3.setText("     (iii)"+"  "+questionS);
                    }else if(i+1==10){
                        que12_4.setTag(questionS_id);
                        que12_4.setText("     (iv)"+"  "+questionS);
                    }else if(i+1==11){
                        que12_5.setTag(questionS_id);
                        que12_5.setText("     (v)"+"  "+questionS);
                    }else if(i+1==12){
                        que12_6.setTag(questionS_id);
                        que12_6.setText("     (vi)"+"  "+questionS);
                    }else if(i+1==13){
                        que12_7.setTag(questionS_id);
                        que12_7.setText("     (vii)"+"  "+questionS);
                    }

                    /*.......... End of SUB Question of 12.........*/
                    else if(i+1==16){
                        que7.setTag(questionS_id);
                        que7.setText(9+".  "+questionS);
                    }else if(i+1==17){
                        que8.setTag(questionS_id);
                        que8.setText(10+".  "+questionS);
                    }else if(i+1==5){
                        que9.setTag(questionS_id);
                        que9.setText(i+1+".  "+questionS);
                    }else if(i+1==19){
                        que10.setTag(questionS_id);
                        que10.setText(12+".  "+questionS);
                    }
                }

            }catch (Exception e){
                Log.e("Question Error: ",e.toString());
            }
        }
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
                String link="http://"+dnsport+"/SteelSales-war/Stl_Salesman_QstnAnswr_nwDealer_C_Api";
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
