package com.aspl.steel.salesmanvisit;

import android.Manifest;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
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
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import com.aspl.steel.GlobalConfiguration;
import com.aspl.steel.LoginActivity;
import com.aspl.steel.R;
import com.aspl.steel.Services.LogoutService;
import com.aspl.steel.SteelHelper;
import com.aspl.steel.adapters.AutoDealerSearchAdp;
import com.aspl.steel.adapters.DealerSearchAutoComp;
import com.aspl.steel.fragments.GPSTracker;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import static android.app.Activity.RESULT_OK;

public class salesmanvisit_view extends Fragment {

    static int INTENT_REQUEST_GET_IMAGES=79,INTENT_REQUEST_GET_IMAGES2=78;

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

    String questionJsonImage="{\n" +
            "  \"dt\": null,\n" +
            "  \"descrptn\": null,\n" +
            "  \"isConverted\": null,\n" +
            "  \"visible\": null,\n" +
            "  \"xlXmlUploadStatus\": null,\n" +
            "  \"txnExist\": 0,\n" +
            "  \"refId\": 0,\n" +
            "  \"sourceId\": null,\n" +
            "  \"id\": null,\n" +
            "  \"name\": \"\",\n" +
            "  \"path\": \"\",\n" +
            "  \"segid\": 1,\n" +
            "  \"revid\": null,\n" +
            "  \"orderId\": null,\n" +
            "  \"refType\": \"\",\n" +
            "  \"tags\": \"\",\n" +
            "  \"fileType\": null,\n" +
            "  \"docName\": null,\n" +
            "  \"moduleId\": 0,\n" +
            "  \"text3\": \"\",\n" +
            "  \"txnid\": 0,\n" +
            "  \"status\": 1,\n" +
            "  \"text4\": null,\n" +
            "  \"arrayByte\": null,\n" +
            "  \"text1\": null,\n" +
            "  \"text2\": null,\n" +
            "  \"pid\": null,\n" +
            "  \"text5\": null,\n" +
            "  \"cid\": 1,\n" +
            "  \"xlXmlErrorMsg\": null,\n" +
            "  \"resizeFilepath\": null,\n" +
            "  \"source\": null,\n" +
            "  \"byteArray\": null,\n" +
            "  \"fileName\": null,\n" +
            "  \"docId\": null\n" +
            "}";


    View rootView;
    DrawerLayout mDrawerLayout;
    String[] metting_party = { "None", "Dealer", "Manager", "Both"};
    String[] unit_type = { "Kg", "Pic"};

    private static final int REQUEST_CAMERA_PERMISSION = 201;

    SimpleDateFormat dateFormatter=new SimpleDateFormat("dd-MM-yyyy");
    Spinner que_s1,que5_spinner;
    CheckBox que2_1,que2_2,que6_1,que6_2,que8_1,que8_2,que9_1,que9_2,que10_1,que10_2,que11_1,que11_2,que13_1,que13_2;
    EditText queA3,queA4,queA5,queA7,queA12_1,queA12_2,queA12_3,queA12_4,queA12_5,queA12_6,queA12_7,queA14;
    ImageView queA15_1,queA15_2;
    TextView queA16;
    TextView que1,que2,que3,que4,que5,que6,que7,que8,que9,que10,que11,que12,que13,que14,que15,que16;
    String  sLid,cid,segid,tempKeyCode,uid;
    TextView que12_1,que12_2,que12_3,que12_4,que12_5,que12_6,que12_7;
    CheckBox que12_1_1,que12_1_2,que12_2_1,que12_2_2,que12_3_1,que12_3_2,que12_4_1,que12_4_2,que12_5_1,que12_5_2,que12_6_1,que12_6_2,que12_7_1,que12_7_2;
    final String LOG_DBG=getClass().getSimpleName();
    FetchDealerName mFetchTask;
    ArrayList<HashMap> arrayList=new ArrayList<>();
    AutoCompleteTextView dealerSearchM;
    Spinner dealerSearchA;
    Switch m_a_swich;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.salesmanvisit_view, container, false);
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
        dealerSearchM=(AutoCompleteTextView)rootView.findViewById(R.id.dealerSearchM);
        dealerSearchA=(Spinner) rootView.findViewById(R.id.dealerSearchA);
        m_a_swich=(Switch) rootView.findViewById(R.id.m_a_swich);
        m_a_swich.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    // The toggle is enabled
                    dealerSearchM.setVisibility(View.VISIBLE);
                    dealerSearchA.setVisibility(View.GONE);
                }else{
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
                if(editable.length()>=1){
                    mFetchTask = new FetchDealerName(getContext(), 1);
                    mFetchTask.execute(cid, segid, sLid, "dealer", editable.toString(), tempKeyCode);
                }

            }
        });

        /*.......... View Refrence call.........*/
        spinnerviewQe1();
        spinnerviewQe5_spinner();
        holeViewAnswer();
        holeViewQuestion();
        questionAnswer2();
        questionAnswer6();
        questionAnswer8();
        questionAnswer9();
        questionAnswer10();
        questionAnswer11();
        questionAnswer13();

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
        queA3=(EditText)rootView.findViewById(R.id.queA3);
        queA4=(EditText)rootView.findViewById(R.id.queA4);
        queA5=(EditText)rootView.findViewById(R.id.queA5);
        queA7=(EditText)rootView.findViewById(R.id.queA7);
        queA12_1=(EditText)rootView.findViewById(R.id.queA12_1);
        queA12_2=(EditText)rootView.findViewById(R.id.queA12_2);
        queA12_3=(EditText)rootView.findViewById(R.id.queA12_3);
        queA12_4=(EditText)rootView.findViewById(R.id.queA12_4);
        queA12_5=(EditText)rootView.findViewById(R.id.queA12_5);
        queA12_6=(EditText)rootView.findViewById(R.id.queA12_6);
        queA12_7=(EditText)rootView.findViewById(R.id.queA12_7);
        queA14=(EditText)rootView.findViewById(R.id.queA14);
        queA15_1=(ImageView)rootView.findViewById(R.id.queA15_1);
        queA15_2=(ImageView)rootView.findViewById(R.id.queA15_2);
        queA16=(TextView)rootView.findViewById(R.id.queA16);
        queA16.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Date Selection For Next Visit
                    Calendar newCalender = Calendar.getInstance();
                    DatePickerDialog dialog = new DatePickerDialog(getActivity(), new DatePickerDialog.OnDateSetListener() {

                        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                            Calendar newDate = Calendar.getInstance();
                            newDate.set(year, monthOfYear, dayOfMonth);
                            String strFromDt = dateFormatter.format(newDate.getTime());
                            queA16.setText(strFromDt);
                            queA16.setTag(strFromDt);
                        }

                    }, newCalender.get(Calendar.YEAR), newCalender.get(Calendar.MONTH), newCalender.get(Calendar.DAY_OF_MONTH));
                    dialog.setTitle("Select Date:");
                    Calendar cal = Calendar.getInstance();
                    cal.setTime(new Date());
                    cal.add(Calendar.MONTH, -1);
                    dialog.show();
            }
        });
        queA15_1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Click and Upload First document
                takePhotoFromCamera(INTENT_REQUEST_GET_IMAGES);
            }
        });
        queA15_2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Click and Upload Second document
                takePhotoFromCamera(INTENT_REQUEST_GET_IMAGES2);
            }
        });

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
        que12=(TextView)rootView.findViewById(R.id.que12);
        que13=(TextView)rootView.findViewById(R.id.que13);
        que14=(TextView)rootView.findViewById(R.id.que14);
        que15=(TextView)rootView.findViewById(R.id.que15);
        que16=(TextView)rootView.findViewById(R.id.que16);
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

    void spinnerviewQe5_spinner(){
        que5_spinner = (Spinner) rootView.findViewById(R.id.que5_spinner);
        ArrayAdapter aa = new ArrayAdapter(getContext(),android.R.layout.simple_spinner_item,unit_type);
        aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //Setting the ArrayAdapter data on the Spinner
        que5_spinner.setAdapter(aa);
    }

    void questionAnswer2(){
        que2_1=(CheckBox)rootView.findViewById(R.id.que2_1);
        que2_2=(CheckBox)rootView.findViewById(R.id.que2_2);
        que2_1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (que2_2.isChecked()) {
                    que2_2.setChecked(false);
                }
            }
        });

        que2_2.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (que2_1.isChecked()) {
                    que2_1.setChecked(false);
                }
            }
        });

    }

    void questionAnswer6(){
        que6_1=(CheckBox)rootView.findViewById(R.id.que6_1);
        que6_2=(CheckBox)rootView.findViewById(R.id.que6_2);
        que6_1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                que6_2.setChecked(false);
            }
        });

        que6_2.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                que6_1.setChecked(false);
            }
        });

    }

    void questionAnswer8(){
        que8_1=(CheckBox)rootView.findViewById(R.id.que8_1);
        que8_2=(CheckBox)rootView.findViewById(R.id.que8_2);
        que8_1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                que8_2.setChecked(false);
            }
        });

        que8_2.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                que8_1.setChecked(false);
            }
        });

    }

    void questionAnswer9(){
        que9_1=(CheckBox)rootView.findViewById(R.id.que9_1);
        que9_2=(CheckBox)rootView.findViewById(R.id.que9_2);
        que9_1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                que9_2.setChecked(false);
            }
        });

        que9_2.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                que9_1.setChecked(false);
            }
        });

    }

    void questionAnswer10(){
        que10_1=(CheckBox)rootView.findViewById(R.id.que10_1);
        que10_2=(CheckBox)rootView.findViewById(R.id.que10_2);
        que10_1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                que10_2.setChecked(false);
            }
        });

        que10_2.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                que10_1.setChecked(false);
            }
        });

    }

    void questionAnswer11(){
        que11_1=(CheckBox)rootView.findViewById(R.id.que11_1);
        que11_2=(CheckBox)rootView.findViewById(R.id.que11_2);
        que11_1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                que11_2.setChecked(false);
            }
        });

        que11_2.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                que11_1.setChecked(false);
            }
        });

    }
    void questionAnswer13(){
        que13_1=(CheckBox)rootView.findViewById(R.id.que13_1);
        que13_2=(CheckBox)rootView.findViewById(R.id.que13_2);
        que13_1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                que13_2.setChecked(false);
            }
        });

        que13_2.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                que13_1.setChecked(false);
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

    String checkBoxValidation(CheckBox checkBox1,CheckBox checkBox2){
        if (checkBox1.isChecked()){
            return "Yes";
        }else if (checkBox2.isChecked()){
            return "No";
        }else if (!checkBox2.isChecked()){
            return "";
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
            JSONObject questObj1= new JSONObject(questionJson);
            questObj1.put("questionId",que1.getTag());
            questObj1.put("answer",que_s1.getSelectedItem());
            questObj1.put("rowNum",1);
            questionAr.put(questObj1);

            /* .......... Question 2 Answer.........*/
            String answer2=checkBoxValidation(que2_1,que2_2);
            if (answer2.equals("")){
                Toast.makeText(getContext(),"You can't left blank Question 2!",Toast.LENGTH_LONG).show();
                return;
            }
            JSONObject questObj2= new JSONObject(questionJson);
            questObj2.put("questionId",que2.getTag());
            questObj2.put("answer",answer2);
            questObj2.put("rowNum",2);
            questionAr.put(questObj2);

            /* .......... Question 3 Answer.........*/
            String answer3=editTextBlankCheck(queA3);
            if (answer3.equals("")){
                Toast.makeText(getContext(),"You can't left blank Question 3!",Toast.LENGTH_LONG).show();
                return;
            }
            JSONObject questObj3= new JSONObject(questionJson);
            questObj3.put("questionId",que3.getTag());
            questObj3.put("answer",answer3);
            questObj3.put("rowNum",3);
            questionAr.put(questObj3);

            /* .......... Question 4 Answer.........*/
            String answer4=editTextBlankCheck(queA4);
            if (answer4.equals("")){
                Toast.makeText(getContext(),"You can't left blank Question 4!",Toast.LENGTH_LONG).show();
                return;
            }
            JSONObject questObj4= new JSONObject(questionJson);
            questObj4.put("questionId",que4.getTag());
            questObj4.put("answer",answer4);
            questObj4.put("rowNum",4);
            questionAr.put(questObj4);

            /* .......... Question 5 Answer.........*/
            String answer5=editTextBlankCheck(queA5);
            if (answer5.equals("")){
                Toast.makeText(getContext(),"You can't left blank Question 5!",Toast.LENGTH_LONG).show();
                return;
            }
            JSONObject questObj5= new JSONObject(questionJson);
            questObj5.put("questionId",que5.getTag());
            questObj5.put("answer",answer5);
            questObj5.put("text3",que5_spinner.getSelectedItem());
            questObj5.put("rowNum",5);
            questionAr.put(questObj5);

            /* .......... Question 6 Answer.........*/

            String answer6=checkBoxValidation(que6_1,que6_2);
            if (answer6.equals("")){
                Toast.makeText(getContext(),"You can't left blank Question 6!",Toast.LENGTH_LONG).show();
                return;
            }
            JSONObject questObj6= new JSONObject(questionJson);
            questObj6.put("questionId",que6.getTag());
            questObj6.put("answer",answer6);
            questObj6.put("rowNum",6);
            questionAr.put(questObj6);

            /* .......... Question 7 Answer.........*/
            String answer7=editTextBlankCheck(queA7);
            if (answer7.equals("")){
                Toast.makeText(getContext(),"You can't left blank Question 7!",Toast.LENGTH_LONG).show();
                return;
            }
            JSONObject questObj7= new JSONObject(questionJson);
            questObj7.put("questionId",que7.getTag());
            questObj7.put("answer",answer7);
            questObj7.put("rowNum",7);
            questionAr.put(questObj7);

            /* .......... Question 8 Answer.........*/
            String answer8=checkBoxValidation(que8_1,que8_2);
            if (answer8.equals("")){
                Toast.makeText(getContext(),"You can't left blank Question 8!",Toast.LENGTH_LONG).show();
                return;
            }
            JSONObject questObj8= new JSONObject(questionJson);
            questObj8.put("questionId",que8.getTag());
            questObj8.put("answer",answer8);
            questObj8.put("rowNum",8);
            questionAr.put(questObj8);

            /* .......... Question 9 Answer.........*/
            String answer9=checkBoxValidation(que9_1,que9_2);
            if (answer9.equals("")){
                Toast.makeText(getContext(),"You can't left blank Question 9!",Toast.LENGTH_LONG).show();
                return;
            }
            JSONObject questObj9= new JSONObject(questionJson);
            questObj9.put("questionId",que9.getTag());
            questObj9.put("answer",answer9);
            questObj9.put("rowNum",9);
            questionAr.put(questObj9);

            /* .......... Question 10 Answer.........*/
            String answer10=checkBoxValidation(que10_1,que10_2);
            if (answer10.equals("")){
                Toast.makeText(getContext(),"You can't left blank Question 10!",Toast.LENGTH_LONG).show();
                return;
            }
            JSONObject questObj10= new JSONObject(questionJson);
            questObj10.put("questionId",que10.getTag());
            questObj10.put("answer",answer10);
            questObj10.put("rowNum",10);
            questionAr.put(questObj10);

            /* .......... Question 11 Answer.........*/
            String answer11=checkBoxValidation(que11_1,que11_2);
            if (answer11.equals("")){
                Toast.makeText(getContext(),"You can't left blank Question 11!",Toast.LENGTH_LONG).show();
                return;
            }
            JSONObject questObj11= new JSONObject(questionJson);
            questObj11.put("questionId",que11.getTag());
            questObj11.put("answer",answer11);
            questObj11.put("rowNum",11);
            questionAr.put(questObj11);

            /* .......... Question 12 Answer.........*/
            String answer12=checkBoxValidation(que12_1_1,que12_1_2);
            if (answer12.equals("")){
                Toast.makeText(getContext(),"You can't left blank Question 12 of (i)!",Toast.LENGTH_LONG).show();
                return;
            }
            JSONObject questObj12= new JSONObject(questionJson);
            questObj12.put("questionId",que12_1.getTag());
            questObj12.put("text1",answer12);
            try {
                if (answer12.equalsIgnoreCase("Yes")) {
                    String edit12_1=editTextBlankCheck(queA12_1);
                    if (edit12_1.equals("")){
                        Toast.makeText(getContext(),"You can't left blank Question 12 of (i)!",Toast.LENGTH_LONG).show();
                        return;
                    }else {
                        questObj12.put("answer", queA12_1.getText());
                    }
                }else{
                    questObj12.put("answer", "");
                }
            }catch (Exception e){}
            questObj12.put("rowNum",12);
            questionAr.put(questObj12);

            /* .......... Question 12    ----  13 Answer.........*/
            String answer13=checkBoxValidation(que12_2_1,que12_2_2);
            if (answer13.equals("")){
                Toast.makeText(getContext(),"You can't left blank Question 12 of (ii)!",Toast.LENGTH_LONG).show();
                return;
            }
            JSONObject questObj13= new JSONObject(questionJson);
            questObj13.put("questionId",que12_2.getTag());
            questObj13.put("text1",answer13);
            try {
                if (answer13.equalsIgnoreCase("Yes")) {
                    String edit12_2=editTextBlankCheck(queA12_2);
                    if (edit12_2.equals("")){
                        Toast.makeText(getContext(),"You can't left blank Question 12 of (ii)!",Toast.LENGTH_LONG).show();
                        return;
                    }else {
                        questObj13.put("answer", queA12_2.getText());
                    }
                }else{
                    questObj13.put("answer", "");
                }
            }catch (Exception e){}
            questObj13.put("rowNum",13);
            questionAr.put(questObj13);

            /* .......... Question 12    ----14 Answer.........*/
            String answer14=checkBoxValidation(que12_3_1,que12_3_2);
            if (answer14.equals("")){
                Toast.makeText(getContext(),"You can't left blank Question 12 of (iii)!",Toast.LENGTH_LONG).show();
                return;
            }
            JSONObject questObj14= new JSONObject(questionJson);
            questObj14.put("questionId",que12_3.getTag());
            questObj14.put("text1",answer14);
            try {
                if (answer14.equalsIgnoreCase("Yes")) {
                    String edit12_3=editTextBlankCheck(queA12_3);
                    if (edit12_3.equals("")){
                        Toast.makeText(getContext(),"You can't left blank Question 12 of (iii)!",Toast.LENGTH_LONG).show();
                        return;
                    }else {
                        questObj14.put("answer", queA12_3.getText());
                    }
                }else{
                    questObj14.put("answer", "");
                }
            }catch (Exception e){}
            questObj14.put("rowNum",14);
            questionAr.put(questObj14);

            /* .......... Question 12    ----15 Answer.........*/
            String answer15=checkBoxValidation(que12_4_1,que12_4_2);
            if (answer15.equals("")){
                Toast.makeText(getContext(),"You can't left blank Question 12 of (iv)!",Toast.LENGTH_LONG).show();
                return;
            }
            JSONObject questObj15= new JSONObject(questionJson);
            questObj15.put("questionId",que12_4.getTag());
            questObj15.put("text1",answer15);
            try {
                if (answer15.equalsIgnoreCase("Yes")) {
                    String edit12_4=editTextBlankCheck(queA12_4);
                    if (edit12_4.equals("")){
                        Toast.makeText(getContext(),"You can't left blank Question 12 of (iv)!",Toast.LENGTH_LONG).show();
                        return;
                    }else {
                        questObj15.put("answer", queA12_4.getText());
                    }
                }else{
                    questObj15.put("answer", "");
                }
            }catch (Exception e){}
            questObj15.put("rowNum",15);
            questionAr.put(questObj15);

            /* .......... Question 12    ----16 Answer.........*/
            String answer16=checkBoxValidation(que12_5_1,que12_5_2);
            if (answer16.equals("")){
                Toast.makeText(getContext(),"You can't left blank Question 12 of (v)!",Toast.LENGTH_LONG).show();
                return;
            }
            JSONObject questObj16= new JSONObject(questionJson);
            questObj16.put("questionId",que12_5.getTag());
            questObj16.put("text1",answer16);
            try {
                if (answer16.equalsIgnoreCase("Yes")) {
                    String edit12_5=editTextBlankCheck(queA12_5);
                    if (edit12_5.equals("")){
                        Toast.makeText(getContext(),"You can't left blank Question 12 of (v)!",Toast.LENGTH_LONG).show();
                        return;
                    }else {
                        questObj16.put("answer", queA12_5.getText());
                    }
                }else{
                    questObj16.put("answer", "");
                }
            }catch (Exception e){}
            questObj16.put("rowNum",16);
            questionAr.put(questObj16);

            /* .......... Question 12    ----17 Answer.........*/
            String answer17=checkBoxValidation(que12_6_1,que12_6_2);
            if (answer17.equals("")){
                Toast.makeText(getContext(),"You can't left blank Question 12 of (vi)!",Toast.LENGTH_LONG).show();
                return;
            }
            JSONObject questObj17= new JSONObject(questionJson);
            questObj17.put("questionId",que12_6.getTag());
            questObj17.put("text1",answer17);
            try {
                if (answer17.equalsIgnoreCase("Yes")) {
                    String edit12_6=editTextBlankCheck(queA12_6);
                    if (edit12_6.equals("")){
                        Toast.makeText(getContext(),"You can't left blank Question 12 of (vi)!",Toast.LENGTH_LONG).show();
                        return;
                    }else {
                        questObj17.put("answer", queA12_6.getText());
                    }
                }else{
                    questObj17.put("answer", "");
                }
            }catch (Exception e){}
            questObj17.put("rowNum",17);
            questionAr.put(questObj17);

            /* .......... Question 12    ----18 Answer.........*/
            String answer18=checkBoxValidation(que12_7_1,que12_7_2);
            if (answer18.equals("")){
                Toast.makeText(getContext(),"You can't left blank Question 12 of (vii)!",Toast.LENGTH_LONG).show();
                return;
            }
            JSONObject questObj18= new JSONObject(questionJson);
            questObj18.put("questionId",que12_7.getTag());
            questObj18.put("text1",answer18);
            try {
                if (answer18.equalsIgnoreCase("Yes")) {
                    String edit12_7=editTextBlankCheck(queA12_7);
                    if (edit12_7.equals("")){
                        Toast.makeText(getContext(),"You can't left blank Question 12 of (vii)!",Toast.LENGTH_LONG).show();
                        return;
                    }else {
                        questObj18.put("answer", queA12_7.getText());
                    }
                }else{
                    questObj18.put("answer", "");
                }
            }catch (Exception e){}
            questObj18.put("rowNum",18);
            questionAr.put(questObj18);

            /* .......... Question 12    ----19 Answer.........*/
            String answer19=checkBoxValidation(que13_1,que13_2);
            if (answer19.equals("")){
                Toast.makeText(getContext(),"You can't left blank Question 13!",Toast.LENGTH_LONG).show();
                return;
            }
            JSONObject questObj19= new JSONObject(questionJson);
            questObj19.put("questionId",que13.getTag());
            questObj19.put("answer",answer19);
            questObj19.put("rowNum",19);
            questionAr.put(questObj19);

            /* .......... Question 20 Answer.........*/
            String answer20=editTextBlankCheck(queA14);
            if (answer20.equals("")){
                Toast.makeText(getContext(),"You can't left blank Question 14!",Toast.LENGTH_LONG).show();
                return;
            }

            JSONObject questObj20= new JSONObject(questionJson);
            questObj20.put("questionId",que14.getTag());
            try {
                questObj20.put("answer", answer20);
            }catch (Exception e){}
            questObj20.put("rowNum",20);
            questionAr.put(questObj20);

            /* .......... Question 21 Answer.........*/
            JSONObject questObj21= new JSONObject(questionJson);
            JSONObject questObjquestionJsonImage= new JSONObject(questionJsonImage);
            questObj21.put("questionId",que15.getTag());
            questObj21.put("answer","");
            questObj21.put("rowNum",21);
            questObj21.put("fuObj",questObjquestionJsonImage);

            JSONArray imageArr=new JSONArray();
            if (!queA15_1.getTag().equals("0")){
                JSONObject questObjquestionJsonImage1= new JSONObject(questionJsonImage);
                Bitmap bitmap = (Bitmap) queA15_1.getTag();
                String imgString = Base64.encodeToString(getBytesFromBitmap(bitmap),
                        Base64.NO_WRAP);
                questObjquestionJsonImage1.put("arrayByte",imgString);
                questObjquestionJsonImage1.put("fileType","image/png");
                questObjquestionJsonImage1.put("fileName","image1.png");
                questObjquestionJsonImage1.put("resizeFilepath","");
                imageArr.put(questObjquestionJsonImage1) ;
            }
            if (!queA15_2.getTag().equals("0")){
                JSONObject questObjquestionJsonImage2= new JSONObject(questionJsonImage);
                Bitmap bitmap = (Bitmap) queA15_2.getTag();
                String imgString = Base64.encodeToString(getBytesFromBitmap(bitmap),
                        Base64.NO_WRAP);
                questObjquestionJsonImage2.put("arrayByte",imgString);
                questObjquestionJsonImage2.put("fileType","image/png");
                questObjquestionJsonImage2.put("fileName","image.png");
                questObjquestionJsonImage2.put("resizeFilepath","");
                imageArr.put(questObjquestionJsonImage2) ;
            }
            if (imageArr.length()>0) {
                questObj21.put("fuList", imageArr);
            }
            questionAr.put(questObj21);

            /* .......... Question 22 Answer.........*/
            if (queA16.getTag().equals("0")){
                Toast.makeText(getContext(),"You can't left blank Question 16!",Toast.LENGTH_LONG).show();
                return;
            }
            JSONObject questObj22= new JSONObject(questionJson);
            questObj22.put("questionId",que16.getTag());
            questObj22.put("answer",queA16.getText());
            questObj22.put("rowNum",22);
            questionAr.put(questObj22);

            /*............ Put All Data in Main JSON........*/
            JSONObject questObjMainJson= new JSONObject(mainJSon);
            questObjMainJson.put("qustnAnswerDtlsList",questionAr);

            if (m_a_swich.isChecked()){
                try {
                if (dealerSearchM.getText().equals("") && dealerSearchA.getTag().equals("0")){
                    Toast.makeText(getContext(),"Search a Dealer !",Toast.LENGTH_LONG).show();
                    return;
                }
                questObjMainJson.put("dealerSearchType","Manual");
                HashMap hashMap01= (HashMap) dealerSearchM.getTag();
                questObjMainJson.put("dealerId",hashMap01.get("lid"));
                questObjMainJson.put("salesmanId",sLid);
                }catch (Exception e){
                    Toast.makeText(getContext(),"Please Search a Dealer !",Toast.LENGTH_LONG).show();
                }
            }else {
                try {
                    if (dealerSearchA.getSelectedItem().equals("") || dealerSearchA.getSelectedItem() == null) {
                        Toast.makeText(getContext(), "Select a Dealer !", Toast.LENGTH_LONG).show();
                        return;
                    }
                    questObjMainJson.put("dealerSearchType", "GPS");
                    JSONObject partyObj1 = new JSONObject(String.valueOf(dealerSearchA.getSelectedItem()));
                    questObjMainJson.put("dealerId", partyObj1.getString("lid"));
                    questObjMainJson.put("salesmanId", sLid);
                }catch (Exception e){
                    Toast.makeText(getContext(), "Please Select a Dealer !", Toast.LENGTH_LONG).show();
                }
            }



            questObjMainJson.put("cid",cid);
            questObjMainJson.put("segid",segid);

            saveDatabyServlet saveDatabyServlet=new saveDatabyServlet(getContext(),questObjMainJson);
            saveDatabyServlet.execute();

        } catch (JSONException e) { e.printStackTrace(); }

    }

    // convert from bitmap to byte array
    public byte[] getBytesFromBitmap(Bitmap bitmap) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 90, stream);
        return stream.toByteArray();
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
            //Log.e("..........",s);
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
                String link="http://"+dnsport+"/SteelSales-war/Stl_SalesmanQuestionnaire_C";
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
                    }else if(i+1==2){
                        que2.setTag(questionS_id);
                        que2.setText(i+1+".  "+questionS);
                    }else if(i+1==3){
                        que3.setTag(questionS_id);
                        que3.setText(i+1+".  "+questionS);
                    }else if(i+1==4){
                        que4.setTag(questionS_id);
                        que4.setText(i+1+".  "+questionS);
                    }else if(i+1==5){
                        que5.setTag(questionS_id);
                        que5.setText(i+1+".  "+questionS);
                    }else if(i+1==6){
                        que6.setTag(questionS_id);
                        que6.setText(i+1+".  "+questionS);
                    }else if(i+1==7){
                        que7.setTag(questionS_id);
                        que7.setText(i+1+".  "+questionS);
                    }else if(i+1==8){
                        que8.setTag(questionS_id);
                        que8.setText(i+1+".  "+questionS);
                    }else if(i+1==9){
                        que9.setTag(questionS_id);
                        que9.setText(i+1+".  "+questionS);
                    }else if(i+1==10){
                        que10.setTag(questionS_id);
                        que10.setText(i+1+".  "+questionS);
                    }else if(i+1==11){
                        que11.setTag(questionS_id);
                        que11.setText(i+1+".  "+questionS);
                    }else if(i+1==12) {
                        que12.setTag(questionS_id);
                        que12.setText(i + 1 + ".  " + questionS);
                    }
                    /*.......... SUB Question of 12.........*/
                    else if(i+1==13){
                        que12_1.setTag(questionS_id);
                        que12_1.setText("     (i)"+"  "+questionS);
                    }else if(i+1==14){
                        que12_2.setTag(questionS_id);
                        que12_2.setText("     (ii)"+"  "+questionS);
                    }else if(i+1==15){
                        que12_3.setTag(questionS_id);
                        que12_3.setText("     (iii)"+"  "+questionS);
                    }else if(i+1==16){
                        que12_4.setTag(questionS_id);
                        que12_4.setText("     (iv)"+"  "+questionS);
                    }else if(i+1==17){
                        que12_5.setTag(questionS_id);
                        que12_5.setText("     (v)"+"  "+questionS);
                    }else if(i+1==18){
                        que12_6.setTag(questionS_id);
                        que12_6.setText("     (vi)"+"  "+questionS);
                    }else if(i+1==19){
                        que12_7.setTag(questionS_id);
                        que12_7.setText("     (vii)"+"  "+questionS);
                    }

                    /*.......... End of SUB Question of 12.........*/
                    else if(i+1==20){
                        que13.setTag(questionS_id);
                        que13.setText(13+".  "+questionS);
                    }else if(i+1==21){
                        que14.setTag(questionS_id);
                        que14.setText(14+".  "+questionS);
                    }else if(i+1==22){
                        que15.setTag(questionS_id);
                        que15.setText(15+".  "+questionS);
                    }else if(i+1==23){
                        que16.setTag(questionS_id);
                        que16.setText(16+".  "+questionS);
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
                String link="http://"+dnsport+"/SteelSales-war/Stl_Salesman_QstnAnswr_C_Api";
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

    private void takePhotoFromCamera(int imageType) {
        try {
            if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(intent, imageType);
            }else{
                ActivityCompat.requestPermissions(getActivity(), new
                        String[]{Manifest.permission.CAMERA}, REQUEST_CAMERA_PERMISSION);
            }
        }catch (Exception e){Log.e("Camera Error: ",e.toString());}

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode==INTENT_REQUEST_GET_IMAGES){
            if(resultCode==RESULT_OK) {
                Bitmap photo = (Bitmap) data.getExtras().get("data");
                queA15_1.setImageBitmap(photo);
                queA15_1.setTag(photo);
            }
        }else if(requestCode==INTENT_REQUEST_GET_IMAGES2){
            if(resultCode==RESULT_OK) {
                Bitmap photo = (Bitmap) data.getExtras().get("data");
                queA15_2.setImageBitmap(photo);
                queA15_2.setTag(photo);
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
                        dealerSearchM.setText(hashMap12.get("party_name"));
                        dealerSearchM.setTag(hashMap12);
                    }
                };

    }



}