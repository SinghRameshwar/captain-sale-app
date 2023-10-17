package com.aspl.steel.MasonMeetComplation;

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
import android.content.pm.ResolveInfo;
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
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
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
import com.aspl.steel.MasonMeetInitateGp.MasionMeetInitiate;
import com.aspl.steel.NewLeadGp.NewLeadEntry;
import com.aspl.steel.Pre_OrderG.itemListAdapter;
import com.aspl.steel.R;
import com.aspl.steel.Services.LogoutService;
import com.aspl.steel.SteelHelper;
import com.aspl.steel.adapters.AutoDealerSearchAdp;
import com.aspl.steel.adapters.DealerSearchAutoComp;
import com.aspl.steel.salesmanvisit.salesmanvisit_view;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static android.app.Activity.RESULT_OK;

public class MasionMeetCompletionEntry extends Fragment {

    String mainJson = "{\"budget\":null,\"masonMeetParticipantsObj\":{\"id\":null,\"dtCrt\":null,\"status\":1,\"name\":null,\"segid\":1,\"contactNo\":null,\"cid\":1,\"dtUpdt\":null},\"masonMeetGiftsList\":[],\"inititate_sname\":null,\"conductSlid\":null,\"masonMeetHistoryObj\":null,\"id\":null,\"sh_id\":null,\"dtCrt\":null,\"conduct_sname\":null,\"meetType\":null,\"sh_name\":null,\"shApprove\":null,\"segid\":1,\"asmApproveDt\":null,\"shApproveDt\":null,\"attendees\":null,\"fuList\":null,\"status\":1,\"meetDate\":null,\"asm_name\":null,\"asm_id\":null,\"party_name\":null,\"cid\":1,\"masonMeetGiftsObj\":{\"id\":null,\"dtCrt\":null,\"asmId\":null,\"status\":1,\"segid\":1,\"giftId\":null,\"gift_price\":null,\"cid\":1,\"dtUpdt\":null,\"gift_name\":null},\"inititateDt\":null,\"vtypeId\":null,\"flag\":null,\"inititateSlid\":null,\"partyId\":null,\"asmApprove\":null,\"masonMeetParticipantsList\":[],\"ref_id\":null,\"fuObj\":{\"descrptn\":null,\"dt\":null,\"isConverted\":null,\"xlXmlUploadStatus\":null,\"visible\":null,\"txnExist\":null,\"refId\":null,\"sourceId\":null,\"id\":null,\"name\":null,\"path\":null,\"segid\":1,\"revid\":null,\"orderId\":null,\"refType\":null,\"tags\":null,\"docName\":null,\"fileType\":null,\"moduleId\":null,\"text3\":null,\"txnid\":null,\"status\":1,\"text4\":null,\"arrayByte\":null,\"text1\":null,\"text2\":null,\"pid\":null,\"text5\":null,\"cid\":1,\"xlXmlErrorMsg\":null,\"resizeFilepath\":null,\"source\":null,\"byteArray\":null,\"fileName\":null,\"docId\":null},\"vnum\":null,\"dtUpdt\":null}";
    String fugObject = "{\"descrptn\":null,\"dt\":null,\"isConverted\":null,\"xlXmlUploadStatus\":null,\"txnExist\":null,\"visible\":null,\"refId\":null,\"sourceId\":null,\"id\":null,\"name\":null,\"path\":null,\"segid\":1,\"revid\":null,\"orderId\":null,\"refType\":null,\"tags\":null,\"docName\":null,\"fileType\":null,\"moduleId\":null,\"txnid\":null,\"text3\":null,\"status\":1,\"text4\":null,\"arrayByte\":null,\"text1\":null,\"text2\":null,\"pid\":null,\"text5\":null,\"cid\":1,\"xlXmlErrorMsg\":null,\"resizeFilepath\":null,\"source\":null,\"byteArray\":null,\"fileName\":null,\"docId\":null}";
    String masionMeetPObj = "{\"id\":null,\"dtCrt\":null,\"status\":null,\"name\":null,\"segid\":1,\"contactNo\":null,\"cid\":1,\"dtUpdt\":null}";

    View rootView;
    private static final int REQUEST_CAMERA_PERMISSION = 201;
    DrawerLayout mDrawerLayout;
    SimpleDateFormat dateFormatter=new SimpleDateFormat("dd-MM-yyyy");
    String  sLid,cid,segid,tempKeyCode,uid,salesman_name;
    final String LOG_DBG=getClass().getSimpleName();
    RequestQueue requestQueue = null;
    TextView saveBtn,dateTxt1,lable1;
    EditText actual_numPEdit,actualExpance;
    Spinner Spinnerview;
    LinearLayout actualPnum_lay;
    ImageView bill_img1,bill_img2,bill_img3,bill_img4;
    ImageView msonMeet_img1,msonMeet_img2,msonMeet_img3,msonMeet_img4;
    int imageType = 100;
    Spinner v_type;
    String[] v_typeAr = { "Mason", "Engineers", "Contracters","Dealers","Customers"};



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.masion_meet_compleation_entryview, container, false);
        viewSet(rootView);
        SharedPreferences sharedPref = getActivity().getSharedPreferences("loginCredentials", Context.MODE_PRIVATE);
        cid = sharedPref.getString("cid", "");
        segid = sharedPref.getString("segid", "");
        tempKeyCode = sharedPref.getString("storedKeyCode", "");
        sLid = sharedPref.getString("storedSlid", "");
        uid = sharedPref.getString("storedUserId", "");
        salesman_name = sharedPref.getString("salesman_name", "");
        saveBtn=(TextView)rootView.findViewById(R.id.saveData);
        dateTxt1=(TextView)rootView.findViewById(R.id.dateTxt1);
        lable1=(TextView)rootView.findViewById(R.id.lable1);
        actual_numPEdit=(EditText) rootView.findViewById(R.id.actual_numPEdit);
        Spinnerview = (Spinner) rootView.findViewById(R.id.Spinnerview);
        spin1Method();

        Spinnerview.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> arg0, View view, int arg2, long arg3) {
                TextView text1=view.findViewById(R.id.text1);
                try {
                    JSONObject obj1= new JSONObject(text1.getTag()+"");
                    lable1.setText(obj1.getString("vnum")+" - "+obj1.getString("meet_date")+" - "+obj1.getString("dealer_name")+"\n"+obj1.getString("work_state")+" - "+obj1.getString("district")+", Average sale- "+obj1.getString("avrg_sale")+", Last-Lifting- "+obj1.getString("last_lifting"));

                }catch (Exception e){}
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
                // TODO Auto-generated method stub

            }
        });

        actual_numPEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (actual_numPEdit.getText().length()>0) {
                    if ((Integer.parseInt(actual_numPEdit.getText()+"")<= 45)) {
                        addnumof_partasionLay(Integer.parseInt(actual_numPEdit.getText() + ""));
                    }else{
                        actual_numPEdit.getText().clear();
                        Toast.makeText(getContext(),"Max Participation Number of 45!",Toast.LENGTH_LONG).show();
                    }
                }else{
                    actualPnum_lay.removeAllViews();
                }
            }
        });
        actualExpance=(EditText) rootView.findViewById(R.id.actualExpance);
        actualPnum_lay = (LinearLayout) rootView.findViewById(R.id.actualPnum_lay);

        bill_imageRefrance1();
        bill_imageRefrance2();
        bill_imageRefrance3();
        bill_imageRefrance4();
        mason_imageRefrance1();
        mason_imageRefrance2();
        mason_imageRefrance3();
        mason_imageRefrance4();

        dateTxt1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
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
                cal.setTime(new Date());
                //cal.add(Calendar.MONTH, -1);
                dialog.getDatePicker().setMinDate(cal.getTimeInMillis());
                dialog.show();
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

    void spin1Method() {
        v_type = (Spinner) rootView.findViewById(R.id.v_type);
        ArrayAdapter aa = new ArrayAdapter(getContext(),android.R.layout.simple_spinner_item,v_typeAr);
        aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //Setting the ArrayAdapter data on the Spinner
        v_type.setAdapter(aa);
        v_type.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> arg0, View view, int arg2, long arg3) {
                spinnerDataCallServlet dealerListbyLocation=new spinnerDataCallServlet(getContext());
                dealerListbyLocation.execute();
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
                // TODO Auto-generated method stub

            }
        });
    }

    void bill_imageRefrance1(){
        bill_img1 = (ImageView) rootView.findViewById(R.id.bill_img1);
        bill_img1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectImage(1);
            }
        });
    }
    void bill_imageRefrance2(){
        bill_img2 = (ImageView) rootView.findViewById(R.id.bill_img2);
        bill_img2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectImage(2);
            }
        });
    }
    void bill_imageRefrance3(){
        bill_img3 = (ImageView) rootView.findViewById(R.id.bill_img3);
        bill_img3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectImage(3);
            }
        });
    }
    void bill_imageRefrance4(){
        bill_img4 = (ImageView) rootView.findViewById(R.id.bill_img4);
        bill_img4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectImage(4);
            }
        });
    }
    void mason_imageRefrance1(){
        msonMeet_img1 = (ImageView) rootView.findViewById(R.id.msonMeet_img1);
        msonMeet_img1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectImage(5);
            }
        });
    }
    void mason_imageRefrance2(){
        msonMeet_img2 = (ImageView) rootView.findViewById(R.id.msonMeet_img2);
        msonMeet_img2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectImage(6);
            }
        });
    }
    void mason_imageRefrance3(){
        msonMeet_img3 = (ImageView) rootView.findViewById(R.id.msonMeet_img3);
        msonMeet_img3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectImage(7);
            }
        });
    }
    void mason_imageRefrance4(){
        msonMeet_img4 = (ImageView) rootView.findViewById(R.id.msonMeet_img4);
        msonMeet_img4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectImage(8);
            }
        });
    }

    void addnumof_partasionLay(int numOfP){
        actualPnum_lay.removeAllViews();
        for (int i=0; i < numOfP; i++) {
            LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View view = inflater.inflate(R.layout.mason_participation_cell, null);
            TextView nametitle1 = (TextView)view.findViewById(R.id.nametitle1);
            EditText name1 = (EditText)view.findViewById(R.id.name1);
            EditText number1 = (EditText)view.findViewById(R.id.number1);
            nametitle1.setText(1+i+". Name");
            view.setTag(i);

            actualPnum_lay.addView(view);
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
        final CharSequence[] items = { "Take Photo", "Choose from Gallery",
                "Cancel" };
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
            }else{
                ActivityCompat.requestPermissions(getActivity(), new
                        String[]{Manifest.permission.CAMERA}, REQUEST_CAMERA_PERMISSION);
            }
        }catch (Exception e){Log.e("Camera Error: ",e.toString());}
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
                if (requestCode <= 4) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                        if (data.getClipData() != null) {
                            ClipData mClipData = data.getClipData();
                            for (int i = 0; i < mClipData.getItemCount(); i++) {
                                try {
                                ClipData.Item item = mClipData.getItemAt(i);
                                Uri uri = item.getUri();
                                    if (i == 0) {
                                        Bitmap bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), uri);
                                        bill_img1.setImageBitmap(bitmap);
                                        bill_img1.setTag(bitmap);

                                    }else if (i == 1) {
                                        Bitmap bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), uri);
                                        bill_img2.setImageBitmap(bitmap);
                                        bill_img2.setTag(bitmap);

                                    }else if (i == 2) {
                                        Bitmap bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), uri);
                                        bill_img3.setImageBitmap(bitmap);
                                        bill_img3.setTag(bitmap);

                                    }else if (i == 3) {
                                        Bitmap bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), uri);
                                        bill_img4.setImageBitmap(bitmap);
                                        bill_img4.setTag(bitmap);
                                    }
                                }catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                        } else if (data.getData() != null) {
                            try{
                                Uri uri = data.getData();
                                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), uri);
                                bill_img1.setImageBitmap(bitmap);
                                bill_img1.setTag(bitmap);
                            }catch (IOException e) {
                            e.printStackTrace();
                        }
                        }
                    }
                }else{
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
                                        msonMeet_img1.setImageBitmap(bitmap);
                                        msonMeet_img1.setTag(bitmap);

                                    }else if (i == 1) {
                                        Bitmap bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), uri);
                                        msonMeet_img2.setImageBitmap(bitmap);
                                        msonMeet_img2.setTag(bitmap);

                                    }else if (i == 2) {
                                        Bitmap bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), uri);
                                        msonMeet_img3.setImageBitmap(bitmap);
                                        msonMeet_img3.setTag(bitmap);

                                    }else if (i == 3) {
                                        Bitmap bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), uri);
                                        msonMeet_img4.setImageBitmap(bitmap);
                                        msonMeet_img4.setTag(bitmap);
                                    }

                                }catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                        } else if (data.getData() != null) {
                            try {
                                Uri uri = data.getData();
                                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), uri);
                                msonMeet_img1.setImageBitmap(bitmap);
                                msonMeet_img1.setTag(bitmap);

                            }catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
            }else if (imageType == 0){
                if (requestCode == 1) {
                    if (resultCode == RESULT_OK) {
                        Bitmap photo = (Bitmap) data.getExtras().get("data");
                        bill_img1.setImageBitmap(photo);
                        bill_img1.setTag(photo);
                    }
                } else if (requestCode == 2) {
                    if (resultCode == RESULT_OK) {
                        Bitmap photo = (Bitmap) data.getExtras().get("data");
                        bill_img2.setImageBitmap(photo);
                        bill_img2.setTag(photo);
                    }
                } else if (requestCode == 3) {
                    if (resultCode == RESULT_OK) {
                        Bitmap photo = (Bitmap) data.getExtras().get("data");
                        bill_img3.setImageBitmap(photo);
                        bill_img3.setTag(photo);
                    }
                } else if (requestCode == 4) {
                    if (resultCode == RESULT_OK) {
                        Bitmap photo = (Bitmap) data.getExtras().get("data");
                        bill_img4.setImageBitmap(photo);
                        bill_img4.setTag(photo);
                    }
                } else if (requestCode == 5) {
                    if (resultCode == RESULT_OK) {
                        Bitmap photo = (Bitmap) data.getExtras().get("data");
                        msonMeet_img1.setImageBitmap(photo);
                        msonMeet_img1.setTag(photo);
                    }
                } else if (requestCode == 6) {
                    if (resultCode == RESULT_OK) {
                        Bitmap photo = (Bitmap) data.getExtras().get("data");
                        msonMeet_img2.setImageBitmap(photo);
                        msonMeet_img2.setTag(photo);
                    }
                } else if (requestCode == 7) {
                    if (resultCode == RESULT_OK) {
                        Bitmap photo = (Bitmap) data.getExtras().get("data");
                        msonMeet_img3.setImageBitmap(photo);
                        msonMeet_img3.setTag(photo);
                    }
                } else if (requestCode == 8) {
                    if (resultCode == RESULT_OK) {
                        Bitmap photo = (Bitmap) data.getExtras().get("data");
                        msonMeet_img4.setImageBitmap(photo);
                        msonMeet_img4.setTag(photo);
                    }
                }
            }
        }
    }

    /*.............. Dealer List by Location...........*/
    class spinnerDataCallServlet extends AsyncTask<String,Void,String> {
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
            try{
                String dnsport= GlobalConfiguration.getDomainport();
                String link="http://"+dnsport+"/SteelSales-war/Stl_MasonMeet_ConductList_C_Api";
                HashMap<String,String> hm=new HashMap<>();
                hm.put("cid",cid);
                hm.put("segid", segid);
                hm.put("slid", sLid);    //or uid ask
                hm.put("keyCode",tempKeyCode);
                hm.put("keyCode",tempKeyCode);
                hm.put("meet_type", String.valueOf((v_type.getSelectedItemPosition()+1)));

//                Log.e("......",link);
//                Log.e("......",hm.toString());

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
                }if (main_template.getString("type").equalsIgnoreCase("success")){
                    JSONArray dealerList = new JSONArray();
                    dealerList=main_template.getJSONArray("dataobj");
                    if (dealerList.length()>0) {
                        MasonCompSpnAdapter autoDealerSearchAdp = new MasonCompSpnAdapter(getContext(), dealerList);
                        Spinnerview.setAdapter(autoDealerSearchAdp);
                    }else{
                        MasonCompSpnAdapter autoDealerSearchAdp = new MasonCompSpnAdapter(getContext(), dealerList);
                        Spinnerview.setAdapter(autoDealerSearchAdp);
                        lable1.setText("");

                       // Toast.makeText(context,main_template.getString("msg"),Toast.LENGTH_SHORT).show();
                    }
                }else{
                    JSONArray dealerList = new JSONArray();
                    lable1.setText("");

                    MasonCompSpnAdapter autoDealerSearchAdp = new MasonCompSpnAdapter(getContext(), dealerList);
                    Spinnerview.setAdapter(autoDealerSearchAdp);
                    Toast.makeText(context,main_template.getString("msg"),Toast.LENGTH_SHORT).show();
                }
            }catch (Exception e){
                Log.e("..........",e.toString());
                Toast.makeText(context,e.toString(),Toast.LENGTH_SHORT).show();
            }

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

    // convert from bitmap to byte array
    public byte[] getBytesFromBitmap(Bitmap bitmap) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 90, stream);
        return stream.toByteArray();
    }

    void mainJsonObj(){
        try {
            JSONObject mainJsonObj1 = new JSONObject(mainJson);
            String vnum = "";
            if (dateTxt1.getTag().equals("0")){
                Toast.makeText(getContext(),"Please Select a Date!",Toast.LENGTH_LONG).show();
                return;
            }
            mainJsonObj1.put("meetDate",dateTxt1.getText());

            String editBox1=editTextBlankCheck(actual_numPEdit);
            if (editBox1.equals("")){
                Toast.makeText(getContext(),"Please Enter Actual Number of participation!",Toast.LENGTH_LONG).show();
                return;
            }
            mainJsonObj1.put("attendees",editBox1);

            String editBox2=editTextBlankCheck(actualExpance);
            if (editBox2.equals("")){
                Toast.makeText(getContext(),"Please Enter Actual Expense!",Toast.LENGTH_LONG).show();
                return;
            }
            mainJsonObj1.put("budget",editBox2);
            mainJsonObj1.put("meetType",String.valueOf(v_type.getSelectedItemPosition()+1));

            try {
                if (Spinnerview.getSelectedItem().equals("") || Spinnerview.getSelectedItem() == null) {
                    Toast.makeText(getContext(), "Got Error From Server Side !", Toast.LENGTH_LONG).show();
                    return;
                }
                JSONObject partyObj1 = new JSONObject(String.valueOf(Spinnerview.getSelectedItem()));
                vnum = partyObj1.getString("vnum");
                mainJsonObj1.put("ref_id",partyObj1.getString("meet_id"));
                mainJsonObj1.put("vnum",partyObj1.getString("vnum"));
            }catch (Exception e){
                Toast.makeText(getContext(), "We Got From Server Error. !", Toast.LENGTH_LONG).show();
            }

            JSONArray arrayP = new JSONArray();
            for (int i=0; i<Integer.parseInt(editBox1);i++){
                try {
                    JSONObject masionMeetPObj1 = new JSONObject(masionMeetPObj);
                    View view = actualPnum_lay.getChildAt(i);
                    EditText name1 = (EditText)view.findViewById(R.id.name1);
                    EditText number1 = (EditText)view.findViewById(R.id.number1);
                    //Log.e("......",name1.getText()+"    "+number1.getText());
                    String nameStr1=editTextBlankCheck(name1);
                    if (nameStr1.equals("")){
                        Toast.makeText(getContext(),"Please Enter Participation Name's - "+(i+1),Toast.LENGTH_LONG).show();
                        return;
                    }
                    masionMeetPObj1.put("name",nameStr1);

                    String numStr1=editTextBlankCheck(number1);
                    if (numStr1.equals("")){
                        Toast.makeText(getContext(),"Please Enter Phone Number, Participation- "+(i+1),Toast.LENGTH_LONG).show();
                        return;
                    }else if (numStr1.length()<10){
                        Toast.makeText(getContext(),"Please Enter Currect Phone Number, Participation- "+(i+1),Toast.LENGTH_LONG).show();
                        return;
                    }
                    masionMeetPObj1.put("contactNo",numStr1);
                    arrayP.put(masionMeetPObj1);
                }catch (Exception e){}
            }
            mainJsonObj1.put("masonMeetParticipantsList",arrayP);

            JSONArray imageArr=new JSONArray();
            if (!bill_img1.getTag().equals("0")){
                JSONObject questObjquestionJsonImage1= new JSONObject(fugObject);
                Bitmap bitmap = (Bitmap) bill_img1.getTag();
                String imgString = Base64.encodeToString(getBytesFromBitmap(bitmap),
                        Base64.NO_WRAP);
                questObjquestionJsonImage1.put("arrayByte",imgString);
                questObjquestionJsonImage1.put("fileType","image/png");
                questObjquestionJsonImage1.put("fileName",vnum+"_bill_image1.png");
                questObjquestionJsonImage1.put("refType","mason_bill");
                questObjquestionJsonImage1.put("resizeFilepath","");
                imageArr.put(questObjquestionJsonImage1) ;
            }

            if (!bill_img2.getTag().equals("0")){
                JSONObject questObjquestionJsonImage2= new JSONObject(fugObject);
                Bitmap bitmap = (Bitmap) bill_img1.getTag();
                String imgString = Base64.encodeToString(getBytesFromBitmap(bitmap),
                        Base64.NO_WRAP);
                questObjquestionJsonImage2.put("arrayByte",imgString);
                questObjquestionJsonImage2.put("fileType","image/png");
                questObjquestionJsonImage2.put("fileName",vnum+"_bill_image2.png");
                questObjquestionJsonImage2.put("refType","mason_bill");
                questObjquestionJsonImage2.put("resizeFilepath","");
                imageArr.put(questObjquestionJsonImage2) ;
            }

            if (!bill_img3.getTag().equals("0")){
                JSONObject questObjquestionJsonImage2= new JSONObject(fugObject);
                Bitmap bitmap = (Bitmap) bill_img3.getTag();
                String imgString = Base64.encodeToString(getBytesFromBitmap(bitmap),
                        Base64.NO_WRAP);
                questObjquestionJsonImage2.put("arrayByte",imgString);
                questObjquestionJsonImage2.put("fileType","image/png");
                questObjquestionJsonImage2.put("fileName",vnum+"_bill_image3.png");
                questObjquestionJsonImage2.put("refType","mason_bill");
                questObjquestionJsonImage2.put("resizeFilepath","");
                imageArr.put(questObjquestionJsonImage2) ;
            }

            if (!bill_img4.getTag().equals("0")){
                JSONObject questObjquestionJsonImage2= new JSONObject(fugObject);
                Bitmap bitmap = (Bitmap) bill_img4.getTag();
                String imgString = Base64.encodeToString(getBytesFromBitmap(bitmap),
                        Base64.NO_WRAP);
                questObjquestionJsonImage2.put("arrayByte",imgString);
                questObjquestionJsonImage2.put("fileType","image/png");
                questObjquestionJsonImage2.put("fileName",vnum+"_bill_image4.png");
                questObjquestionJsonImage2.put("refType","mason_bill");
                questObjquestionJsonImage2.put("resizeFilepath","");
                imageArr.put(questObjquestionJsonImage2) ;
            }

            if (!msonMeet_img1.getTag().equals("0")){
                JSONObject questObjquestionJsonImage2= new JSONObject(fugObject);
                Bitmap bitmap = (Bitmap) msonMeet_img1.getTag();
                String imgString = Base64.encodeToString(getBytesFromBitmap(bitmap),
                        Base64.NO_WRAP);
                questObjquestionJsonImage2.put("arrayByte",imgString);
                questObjquestionJsonImage2.put("fileType","image/png");
                questObjquestionJsonImage2.put("fileName",vnum+"_meet_image1.png");
                questObjquestionJsonImage2.put("refType","mason_meet");
                questObjquestionJsonImage2.put("resizeFilepath","");
                imageArr.put(questObjquestionJsonImage2) ;
            }

            if (!msonMeet_img2.getTag().equals("0")){
                JSONObject questObjquestionJsonImage2= new JSONObject(fugObject);
                Bitmap bitmap = (Bitmap) msonMeet_img2.getTag();
                String imgString = Base64.encodeToString(getBytesFromBitmap(bitmap),
                        Base64.NO_WRAP);
                questObjquestionJsonImage2.put("arrayByte",imgString);
                questObjquestionJsonImage2.put("fileType","image/png");
                questObjquestionJsonImage2.put("fileName",vnum+"_meet_image2.png");
                questObjquestionJsonImage2.put("refType","mason_meet");
                questObjquestionJsonImage2.put("resizeFilepath","");
                imageArr.put(questObjquestionJsonImage2) ;
            }

            if (!msonMeet_img3.getTag().equals("0")){
                JSONObject questObjquestionJsonImage2= new JSONObject(fugObject);
                Bitmap bitmap = (Bitmap) msonMeet_img3.getTag();
                String imgString = Base64.encodeToString(getBytesFromBitmap(bitmap),
                        Base64.NO_WRAP);
                questObjquestionJsonImage2.put("arrayByte",imgString);
                questObjquestionJsonImage2.put("fileType","image/png");
                questObjquestionJsonImage2.put("fileName",vnum+"_meet_image3.png");
                questObjquestionJsonImage2.put("refType","mason_meet");
                questObjquestionJsonImage2.put("resizeFilepath","");
                imageArr.put(questObjquestionJsonImage2) ;
            }

            if (!msonMeet_img4.getTag().equals("0")){
                JSONObject questObjquestionJsonImage2= new JSONObject(fugObject);
                Bitmap bitmap = (Bitmap) msonMeet_img4.getTag();
                String imgString = Base64.encodeToString(getBytesFromBitmap(bitmap),
                        Base64.NO_WRAP);
                questObjquestionJsonImage2.put("arrayByte",imgString);
                questObjquestionJsonImage2.put("fileType","image/png");
                questObjquestionJsonImage2.put("fileName",vnum+"_meet_image4.png");
                questObjquestionJsonImage2.put("refType","mason_meet");
                questObjquestionJsonImage2.put("resizeFilepath","");
                imageArr.put(questObjquestionJsonImage2) ;
            }

            if (imageArr.length()>0) {
                mainJsonObj1.put("fuList", imageArr);
            }

            saveDatabyServlet saveDatabyServlet=new saveDatabyServlet(getContext(),mainJsonObj1);
            saveDatabyServlet.execute();

        }catch (Exception e){Log.e("Error: ",e.toString());}
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
                String link="http://"+dnsport+"/SteelSales-war/Stl_Masonmeet_Complete_C_Api";
                HashMap<String,String> hm=new HashMap<>();
                hm.put("cid",cid);
                hm.put("segid", segid);
                hm.put("data", answerOnj1.toString());
                hm.put("keyCode",tempKeyCode);
                hm.put("meet_type", String.valueOf(v_type.getSelectedItemPosition()+1));

                //Log.e("........",link);
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


}
