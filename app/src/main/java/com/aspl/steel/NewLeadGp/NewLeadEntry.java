package com.aspl.steel.NewLeadGp;

import android.Manifest;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.ClipData;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;

import com.android.volley.RequestQueue;
import com.aspl.steel.GlobalConfiguration;
import com.aspl.steel.LoginActivity;
import com.aspl.steel.R;
import com.aspl.steel.Services.LogoutService;
import com.aspl.steel.SteelHelper;
import com.aspl.steel.adapters.DealerSearchAutoComp;
import com.aspl.steel.fragments.GPSTracker;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;

import static android.app.Activity.RESULT_OK;

public class NewLeadEntry extends Fragment {

    String mainJson = "{\"giftRequired\":null,\"accept\":null,\"state\":null,\"leadType\":null,\"inititate_salesman_name\":null,\"remarks\":null,\"siteDetails\":null,\"brandName\":null,\"exeSup\":null,\"id\":null,\"leadingRustguard\":null,\"uidUpdt\":null,\"assignSlid\":null,\"segid\":1,\"longitude\":null,\"assign_salesman_name\":null,\"district\":null,\"influencerName\":null,\"leadManagementDetails\":{\"id\":null,\"dtCrt\":null,\"status\":1,\"updateSlid\":null,\"segid\":1,\"leadId\":null,\"remarks\":null,\"leadStatus\":null,\"cid\":1,\"dtUpdt\":null,\"convertQty\":null,\"convertFrom\":null},\"fuList\":null,\"status\":1,\"asm_name\":null,\"image_dtls\":null,\"party_name\":null,\"influencerContact\":null,\"ownersName\":null,\"leadingCaptain\":null,\"siteAddress\":null,\"cid\":1,\"leadManagementDetailsList\":[],\"districtName\":null,\"inititateSlid\":null,\"uidCrt\":null,\"partyId\":null,\"asmId\":null,\"lead_crt_dt\":null,\"leadStatus\":null,\"ownersContact\":null,\"latitude\":null,\"fuObj\":{\"dt\":null,\"descrptn\":null,\"isConverted\":null,\"visible\":null,\"xlXmlUploadStatus\":null,\"txnExist\":null,\"refId\":null,\"sourceId\":null,\"id\":null,\"name\":null,\"path\":null,\"segid\":1,\"revid\":null,\"orderId\":null,\"refType\":null,\"tags\":null,\"fileType\":null,\"docName\":null,\"moduleId\":null,\"text3\":null,\"txnid\":null,\"status\":1,\"text4\":null,\"arrayByte\":null,\"text1\":null,\"text2\":null,\"pid\":null,\"text5\":null,\"cid\":1,\"xlXmlErrorMsg\":null,\"resizeFilepath\":null,\"source\":null,\"byteArray\":null,\"fileName\":null,\"docId\":null},\"leadNum\":null}";
    String fugObject = "{\"dt\":null,\"descrptn\":null,\"isConverted\":null,\"visible\":null,\"xlXmlUploadStatus\":null,\"txnExist\":null,\"refId\":null,\"sourceId\":null,\"id\":null,\"name\":null,\"path\":null,\"segid\":1,\"revid\":null,\"orderId\":null,\"refType\":null,\"tags\":null,\"fileType\":null,\"docName\":null,\"moduleId\":null,\"text3\":null,\"txnid\":null,\"status\":1,\"text4\":null,\"arrayByte\":null,\"text1\":null,\"text2\":null,\"pid\":null,\"text5\":null,\"cid\":1,\"xlXmlErrorMsg\":null,\"resizeFilepath\":null,\"source\":null,\"byteArray\":null,\"fileName\":null,\"docId\":null}";
    String leadMangdtls = "{\"id\":null,\"dtCrt\":null,\"status\":1,\"updateSlid\":null,\"segid\":1,\"leadId\":null,\"remarks\":null,\"leadStatus\":null,\"cid\":1,\"dtUpdt\":null,\"convertQty\":null,\"convertFrom\":null}";

    String[] lead_tpe = { "RV", "NV", "OV"};
    String[] sate_ar = {"West Bengal","Andra Pradesh","Arunachal Pradesh","Assam","Bihar","Chhattisgarh","Goa","Gujarat","Haryana","Himachal Pradesh","Jammu and Kashmir","Jharkhand","Karnataka","Kerala","Madya Pradesh","Maharashtra","Manipur","Meghalaya","Mizoram","Nagaland","Orissa","Punjab","Rajasthan","Sikkim","Tamil Nadu","Tripura","Uttaranchal","Uttar Pradesh"};
    String[] brand = { "Captain","Tata", "SRMB", "Shyam","Elegant","JSW","Panther","Others","NA"};

    View rootView;
    private static final int REQUEST_CAMERA_PERMISSION = 201;
    DrawerLayout mDrawerLayout;
    SimpleDateFormat dateFormatter = new SimpleDateFormat("dd-MM-yyyy");
    String sLid, cid, segid, tempKeyCode, uid, salesman_name;
    final String LOG_DBG = getClass().getSimpleName();
    RequestQueue requestQueue = null;
    TextView saveBtn,lat_log1,lat_log2;
    EditText site_dtls, site_adrs,onerName,oner_cnt,infu_name,infu_cont,remarks,f_exe;
    Spinner spin1,spin2,spin3,spin4;
    ImageView img1, img2, img3;
    AutoCompleteTextView dealerSearchM;
    CheckBox que1_1,que1_2,que2_1,que2_2,que3_1,que3_2;
    int imageType = 100;
    FetchDealerName mFetchTask;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle
            savedInstanceState) {
        rootView = inflater.inflate(R.layout.new_lead_entryview, container, false);
        viewSet(rootView);
        SharedPreferences sharedPref = getActivity().getSharedPreferences("loginCredentials", Context.MODE_PRIVATE);
        cid = sharedPref.getString("cid", "");
        segid = sharedPref.getString("segid", "");
        tempKeyCode = sharedPref.getString("storedKeyCode", "");
        sLid = sharedPref.getString("storedSlid", "");
        uid = sharedPref.getString("storedUserId", "");
        salesman_name = sharedPref.getString("salesman_name", "");
        saveBtn = (TextView) rootView.findViewById(R.id.saveData);
        lat_log1 = (TextView) rootView.findViewById(R.id.lat_log1);
        lat_log2 = (TextView) rootView.findViewById(R.id.lat_log2);
        site_dtls = (EditText) rootView.findViewById(R.id.site_dtls);
        site_adrs = (EditText) rootView.findViewById(R.id.site_adrs);
        onerName = (EditText) rootView.findViewById(R.id.onerName);
        oner_cnt = (EditText) rootView.findViewById(R.id.oner_cnt);
        infu_name = (EditText) rootView.findViewById(R.id.infu_name);
        infu_cont = (EditText) rootView.findViewById(R.id.infu_cont);
        remarks = (EditText) rootView.findViewById(R.id.remarks);
        f_exe = (EditText) rootView.findViewById(R.id.f_exe);

        dealerSearchM = (AutoCompleteTextView) rootView.findViewById(R.id.dealerSearchM);
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
                    mFetchTask.execute(cid, segid, sLid, "dealer_sub_dealer", editable.toString(), tempKeyCode);
                }

            }
        });

        bill_imageRefrance1();
        bill_imageRefrance2();
        bill_imageRefrance3();

        spin1Method();
        spin2Method();
        spin3 = (Spinner) rootView.findViewById(R.id.spin3);
        spin4Method();
        checkBoxMethod1();
        checkBoxMethod2();
        checkBoxMethod3();
        locationTrackingMethod();

        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveDataJSONPrepare();
            }
        });

        return rootView;
    }

    void checkBoxMethod1(){
        que1_1=(CheckBox)rootView.findViewById(R.id.que1_1);
        que1_2=(CheckBox)rootView.findViewById(R.id.que1_2);
        que1_1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (que1_2.isChecked()) {
                    que1_2.setChecked(false);
                }
            }
        });

        que1_2.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (que1_1.isChecked()) {
                    que1_1.setChecked(false);
                }
            }
        });

    }

    void checkBoxMethod2(){
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

    void checkBoxMethod3(){
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

    void bill_imageRefrance1() {
        img1 = (ImageView) rootView.findViewById(R.id.img1);
        img1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectImage(1);
            }
        });
    }

    void bill_imageRefrance2() {
        img2 = (ImageView) rootView.findViewById(R.id.img2);
        img2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectImage(2);
            }
        });
    }

    void bill_imageRefrance3() {
        img3 = (ImageView) rootView.findViewById(R.id.img3);
        img3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectImage(3);
            }
        });
    }

    void spin1Method() {
        spin1 = (Spinner) rootView.findViewById(R.id.spin1);
        ArrayAdapter aa = new ArrayAdapter(getContext(),android.R.layout.simple_spinner_item,lead_tpe);
        aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //Setting the ArrayAdapter data on the Spinner
        spin1.setAdapter(aa);
    }

    void spin2Method() {
        spin2 = (Spinner) rootView.findViewById(R.id.spin2);
        ArrayAdapter aa = new ArrayAdapter(getContext(),android.R.layout.simple_spinner_item,sate_ar);
        aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //Setting the ArrayAdapter data on the Spinner
        spin2.setAdapter(aa);
        spin2.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> arg0, View view, int arg2, long arg3) {
                spinnerDataCallServlet dealerListbyLocation = new spinnerDataCallServlet(getContext());
                dealerListbyLocation.execute();
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
                // TODO Auto-generated method stub

            }
        });
    }

    void spin4Method() {
        spin4 = (Spinner) rootView.findViewById(R.id.spin4);
        ArrayAdapter aa = new ArrayAdapter(getContext(),android.R.layout.simple_spinner_item,brand);
        aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //Setting the ArrayAdapter data on the Spinner
        spin4.setAdapter(aa);
    }

    void locationTrackingMethod() {
        GPSTracker gpsTracker = new GPSTracker(getActivity());
        if (!gpsTracker.canGetLocation()) {
            Toast.makeText(getContext(), "GPS is disabled. You will be log out", Toast.LENGTH_SHORT).show();
            return;
        }else {
            lat_log1.setText(gpsTracker.latitude+"");
            lat_log1.setTag(gpsTracker.latitude+"");
            lat_log2.setText(",  "+gpsTracker.longitude);
            lat_log2.setTag(gpsTracker.longitude+"");


        }
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


    private void selectImage(final int imageNo) {
        final CharSequence[] items = {"Take Photo", "Choose from Gallery",
                "Cancel"};
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Add Photo!");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                //boolean result=Utilit.checkPermission(getActivity());
                if (items[item].equals("Take Photo")) {
                    cameraIntent(imageNo);
                } else if (items[item].equals("Choose from Gallery")) {
                    galleryIntent(imageNo);
                } else if (items[item].equals("Cancel")) {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }

    private void cameraIntent(int imageNo) {
        try {
            if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                imageType = 0;
                startActivityForResult(intent, imageNo);
            } else {
                ActivityCompat.requestPermissions(getActivity(), new
                        String[]{Manifest.permission.CAMERA}, REQUEST_CAMERA_PERMISSION);
            }
        } catch (Exception e) {
            Log.e("Camera Error: ", e.toString());
        }
    }

    private void galleryIntent(int imageNo) {
        imageType = 1;
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        startActivityForResult(intent, imageNo);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            if (imageType == 1) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                        if (data.getClipData() != null) {
                            ClipData mClipData = data.getClipData();
                            for (int i = 0; i < mClipData.getItemCount(); i++) {
                                try {
                                    ClipData.Item item = mClipData.getItemAt(i);
                                    Uri uri = item.getUri();
                                    // display your images
                                    if (i == 0) {
                                        Bitmap bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), uri);
                                        img1.setImageBitmap(bitmap);
                                        img1.setTag(bitmap);

                                    } else if (i == 1) {
                                        Bitmap bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), uri);
                                        img2.setImageBitmap(bitmap);
                                        img2.setTag(bitmap);

                                    } else if (i == 2) {
                                        Bitmap bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), uri);
                                        img3.setImageBitmap(bitmap);
                                        img3.setTag(bitmap);
                                    }
                                }catch (IOException e) {
                                    e.printStackTrace();}
                            }
                        } else if (data.getData() != null) {
                            Uri uri = data.getData();
                            try {
                                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), uri);
                                img1.setImageBitmap(bitmap);
                                img1.setTag(bitmap);

                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                }
            } else if (imageType == 0) {
                if (requestCode == 1) {
                    if (resultCode == RESULT_OK) {
                        Bitmap photo = (Bitmap) data.getExtras().get("data");
                        img1.setImageBitmap(photo);
                        img1.setTag(photo);
                    }
                } else if (requestCode == 2) {
                    if (resultCode == RESULT_OK) {
                        Bitmap photo = (Bitmap) data.getExtras().get("data");
                        img2.setImageBitmap(photo);
                        img2.setTag(photo);
                    }
                } else if (requestCode == 3) {
                    if (resultCode == RESULT_OK) {
                        Bitmap photo = (Bitmap) data.getExtras().get("data");
                        img3.setImageBitmap(photo);
                        img3.setTag(photo);
                    }
                }
            }
        }
    }


    /*.............. Dealer List by Location...........*/
    class spinnerDataCallServlet extends AsyncTask<String, Void, String> {
        private Context context;
        ProgressDialog progressDialog;

        public spinnerDataCallServlet(Context context) {
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
            try {
                String dnsport = GlobalConfiguration.getDomainport();
                String link = "http://" + dnsport + "/SteelSales-war/Stl_DistrictList_ByState_C_Api";
                HashMap<String, String> hm = new HashMap<>();
                hm.put("cid", cid);
                hm.put("segid", segid);
                hm.put("state", (String) spin2.getSelectedItem());    //or uid ask
                hm.put("keyCode", tempKeyCode);

                return SteelHelper.performPostCall(link, hm);
            } catch (Exception e) {
                return "Exception: " + e.toString();
            }
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            //Log.e("..........", s);
            // Dismiss the progress dialog
            if (progressDialog != null && progressDialog.isShowing())
                progressDialog.dismiss();

            try {
                JSONArray dealerList = new JSONArray(s);
                if (dealerList.length() > 0) {
                    DisticSpinerAdapter autoDealerSearchAdp = new DisticSpinerAdapter(getContext(), dealerList);
                    spin3.setAdapter(autoDealerSearchAdp);
                } else {
                    Toast.makeText(context, "Server Side Error!", Toast.LENGTH_SHORT).show();
                }

            } catch (Exception e) {
                Log.e("..........", e.toString());
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

//                    Log.e(".........",link);
//                    Log.e("........",hm.toString());

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
            //Log.e("........",result+"    Error");
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
                Log.e(LOG_DBG,e.toString());
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

                    }
                };

    }


    void saveDataJSONPrepare(){
        try {
            JSONObject mainJsonObj= new JSONObject(mainJson);
            mainJsonObj.put("leadType",spin1.getSelectedItem());
            mainJsonObj.put("state",spin2.getSelectedItem());
            mainJsonObj.put("brandName",spin4.getSelectedItem());
            try {
                if (spin3.getSelectedItem().equals("") || spin3.getSelectedItem() == null) {
                    Toast.makeText(getContext(), "Please Select District !", Toast.LENGTH_LONG).show();
                    return;
                }
                JSONObject partyObj1 = new JSONObject(String.valueOf(spin3.getSelectedItem()));
                mainJsonObj.put("district", partyObj1.getString("id"));
            }catch (Exception e){
                Toast.makeText(getContext(), "Please Select a Dealer !", Toast.LENGTH_LONG).show();
            }

            String answer1=editTextBlankCheck(site_dtls);
            if (answer1.equals("")){
                Toast.makeText(getContext(),"Please Enter Site Details!",Toast.LENGTH_LONG).show();
                return;
            }
            mainJsonObj.put("siteDetails",answer1);

            String answer2=editTextBlankCheck(site_adrs);
            if (answer2.equals("")){
                Toast.makeText(getContext(),"Please Enter Site Address!",Toast.LENGTH_LONG).show();
                return;
            }
            mainJsonObj.put("siteAddress",answer2);

            try {
                if (dealerSearchM.getText().equals("")){
                    Toast.makeText(getContext(),"Search a Dealer/Sub-dealer !",Toast.LENGTH_LONG).show();
                    return;
                }
                HashMap hashMap01= (HashMap) dealerSearchM.getTag();
                mainJsonObj.put("partyId",hashMap01.get("lid"));
            }catch (Exception e){
                Toast.makeText(getContext(),"Please Search a Dealer !",Toast.LENGTH_LONG).show();
            }

            String answer3=editTextBlankCheck(onerName);
            if (answer3.equals("")){
                Toast.makeText(getContext(),"Please Enter Owner's Name!",Toast.LENGTH_LONG).show();
                return;
            }
            mainJsonObj.put("ownersName",answer3);

            String answer4=editTextBlankCheck(oner_cnt);
            if (answer4.equals("")){
                Toast.makeText(getContext(),"Please Enter Owner's Contact Number!",Toast.LENGTH_LONG).show();
                return;
            }
            mainJsonObj.put("ownersContact",answer4);

            String answer5=editTextBlankCheck(infu_name);
            if (answer5.equals("")){
                Toast.makeText(getContext(),"Please Enter Influencer Name!",Toast.LENGTH_LONG).show();
                return;
            }
            mainJsonObj.put("influencerName",answer5);

            String answer6=editTextBlankCheck(infu_cont);
            if (answer6.equals("")){
                Toast.makeText(getContext(),"Please Enter Influencer Contact Number!",Toast.LENGTH_LONG).show();
                return;
            }
            mainJsonObj.put("influencerContact",answer6);

            String answer10=editTextBlankCheck(remarks);
            if (answer10.equals("")){
                Toast.makeText(getContext(),"Please Enter Remark!",Toast.LENGTH_LONG).show();
                return;
            }
            mainJsonObj.put("remarks",answer10);

            String answer11=editTextBlankCheck(f_exe);
            if (answer11.equals("")){
                Toast.makeText(getContext(),"Please Enter F.Exe/F.Sup!",Toast.LENGTH_LONG).show();
                return;
            }
            mainJsonObj.put("exeSup",answer11);

            String answer7=checkBoxValidation(que1_1,que1_2);
            if (answer7.equals("")){
                Toast.makeText(getContext(),"Please Select Lead For Captain!",Toast.LENGTH_LONG).show();
                return;
            }
            mainJsonObj.put("leadingCaptain",answer7);

            String answer8=checkBoxValidation(que2_1,que2_2);
            if (answer8.equals("")){
                Toast.makeText(getContext(),"Please Select Rustguard!",Toast.LENGTH_LONG).show();
                return;
            }
            mainJsonObj.put("leadingRustguard",answer8);

            String answer9=checkBoxValidation(que3_1,que3_2);
            if (answer9.equals("")){
                Toast.makeText(getContext(),"Please Select Gift Required!",Toast.LENGTH_LONG).show();
                return;
            }
            mainJsonObj.put("giftRequired",answer9);



            if (lat_log1.getTag().equals("0") || lat_log2.getTag().equals("0")){
                Toast.makeText(getContext(),"Your Location Error",Toast.LENGTH_LONG).show();
                return;
            }
            mainJsonObj.put("latitude",lat_log1.getTag());
            mainJsonObj.put("longitude",lat_log2.getTag());
            mainJsonObj.put("inititateSlid",sLid);

            JSONObject insitateDtls= new JSONObject(leadMangdtls);
            insitateDtls.put("updateSlid",sLid);
            insitateDtls.put("leadStatus","lead_create");
            JSONArray instDtlsAr = new JSONArray();
            instDtlsAr.put(insitateDtls);
            mainJsonObj.put("leadManagementDetailsList",instDtlsAr);


            JSONArray imageArr=new JSONArray();
            if (!img1.getTag().equals("0")){
                JSONObject questObjquestionJsonImage1= new JSONObject(fugObject);
                Bitmap bitmap = (Bitmap) img1.getTag();
                String imgString = Base64.encodeToString(getBytesFromBitmap(bitmap),
                        Base64.NO_WRAP);
                questObjquestionJsonImage1.put("arrayByte",imgString);
                questObjquestionJsonImage1.put("fileType","image/png");
                questObjquestionJsonImage1.put("fileName","image1.png");
                questObjquestionJsonImage1.put("refType","lead_management");
                questObjquestionJsonImage1.put("resizeFilepath","");
                imageArr.put(questObjquestionJsonImage1) ;
            }

            if (!img2.getTag().equals("0")){
                JSONObject questObjquestionJsonImage2= new JSONObject(fugObject);
                Bitmap bitmap = (Bitmap) img2.getTag();
                String imgString = Base64.encodeToString(getBytesFromBitmap(bitmap),
                        Base64.NO_WRAP);
                questObjquestionJsonImage2.put("arrayByte",imgString);
                questObjquestionJsonImage2.put("fileType","image/png");
                questObjquestionJsonImage2.put("fileName","image2.png");
                questObjquestionJsonImage2.put("refType","lead_management");
                questObjquestionJsonImage2.put("resizeFilepath","");
                imageArr.put(questObjquestionJsonImage2) ;
            }

            if (!img3.getTag().equals("0")){
                JSONObject questObjquestionJsonImage2= new JSONObject(fugObject);
                Bitmap bitmap = (Bitmap) img3.getTag();
                String imgString = Base64.encodeToString(getBytesFromBitmap(bitmap),
                        Base64.NO_WRAP);
                questObjquestionJsonImage2.put("arrayByte",imgString);
                questObjquestionJsonImage2.put("fileType","image/png");
                questObjquestionJsonImage2.put("fileName","image3.png");
                questObjquestionJsonImage2.put("refType","lead_management");
                questObjquestionJsonImage2.put("resizeFilepath","");
                imageArr.put(questObjquestionJsonImage2) ;
            }

            if (imageArr.length()>0) {
                mainJsonObj.put("fuList", imageArr);
            }

            saveDatabyServlet saveDatabyServlet=new saveDatabyServlet(getContext(),mainJsonObj);
            saveDatabyServlet.execute();

        } catch (JSONException e) { e.printStackTrace(); }

    }

    // convert from bitmap to byte array
    public byte[] getBytesFromBitmap(Bitmap bitmap) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 90, stream);
        return stream.toByteArray();
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
                String link="http://"+dnsport+"/SteelSales-war/Stl_LeadManagement_C_Api";
                HashMap<String,String> hm=new HashMap<>();
                hm.put("cid",cid);
                hm.put("segid", segid);
                hm.put("data", answerOnj1.toString());
                hm.put("keyCode",tempKeyCode);

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
           // Log.e("............",s);
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