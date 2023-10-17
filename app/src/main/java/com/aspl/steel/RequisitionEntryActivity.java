package com.aspl.steel;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Parcelable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.aspl.steel.CrashReport.TopExceptionHandler;
import com.aspl.steel.NewLeadGp.NewLeadEntry;
import com.aspl.steel.adapters.DealerSearchAutoComp;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;

import nl.changer.polypicker.Config;
import nl.changer.polypicker.ImagePickerActivity;

/**
 *  Created by Arnab Kar on 18 June 2016.
 */
public class RequisitionEntryActivity extends AppCompatActivity{
    EditText dealerName,subDealerName,itemName,quantity,narration,district,address,phoneNo,itemWidth,itemHeight;
    TextView unit;
    LinearLayout imageHolder,dlerType_lay,dlr_serch_listlay,sub_dlr_serch_listlay;
    String sLid, cid, segid, tempKeyCode, uid, salesman_name;
    ListbyDealerName listbyDealerName;
    static int REQUEST_DEALER_AUTOCOMPLETE=21,REQUEST_ITEM_AUTOCOMPLETE=22,REQUEST_SUBDEALER_AUTOCOMPLETE=23,INTENT_REQUEST_GET_IMAGES=79,INTENT_REQUEST_BROWSE_IMAGE=78,INTENT_REQUEST_BROWSE_IMAGE_ONLINE=80;
    final int ASK_MULTIPLE_PERMISSION_REQUEST_CODE=2;
    String return_name="",return_id="",rt_contact="",rt_address="",return_JsonObj="",return_unit_name="",return_unit_id="",mReturning;
    String LOG_DBG=getClass().getSimpleName();
    ArrayList<Uri> mMedia=new ArrayList<>();
    ArrayList<Integer> markedOnlinePositionToDel=new ArrayList<>();
    int entryPageNo=0;
    CheckBox exis_Party,new_Party;

    public void onDealerNameReset(View view){
        try{
            TextView dealerName= (TextView) findViewById(R.id.dealerName);
            dealerName.setText("");
            dealerName.setTag(null);
            dlr_serch_listlay.removeAllViews();
            dlr_serch_listlay.setVisibility(View.GONE);
        }catch (Exception e){}
    }
    public void onSubDealerNameReset(View view){
        try{
            TextView subDealerName= (TextView) findViewById(R.id.subDealerName);
            subDealerName.setText("");
            subDealerName.setTag(null);
            sub_dlr_serch_listlay.removeAllViews();
            sub_dlr_serch_listlay.setVisibility(View.GONE);
        }catch (Exception e){}
    }
    private void getImages() {
        Intent intent = new Intent(this, ImagePickerActivity.class);
        Config config = new Config.Builder()
                .setTabBackgroundColor(R.color.white)    // set tab background color. Default white.
                .setTabSelectionIndicatorColor(R.color.blue)
                .setCameraButtonColor(R.color.green)
                .setSelectionLimit(10)    // set photo selection limit. Default unlimited selection.
                .build();
        ImagePickerActivity.setConfig(config);
        startActivityForResult(intent, INTENT_REQUEST_GET_IMAGES);
    }
    public void onFinishClick(View view){
        boolean editFlag = getIntent().getExtras().getBoolean("EditFlag");
        if (editFlag) {
            performEdit();
        }else {
            int flag=  checkValidEntry();
            if(flag==0){
                //No fields entered
                setResult(RESULT_OK);
                finish();
            }else if(flag==1){
                Toast.makeText(this,"Enter all the fields",Toast.LENGTH_SHORT).show();
            }else if(flag==3){
                //do nothing
            }else {
                onContinueClick(view);
                if(((TextView)findViewById(R.id.dealerName)).getError()==null){
                    setResult(RESULT_OK);
                    finish();
                }
                else if(((TextView)findViewById(R.id.dealerName)).getError().toString().equals("")){
                    setResult(RESULT_OK);
                    finish();
                }
            }
        }
    }
    public void onContinueClick(View view) {
        boolean editFlag = getIntent().getExtras().getBoolean("EditFlag");
        int position=getIntent().getExtras().getInt("position",0);
        if (editFlag) {
            performEdit();
        }
        else{
            //Log.e("valid check",checkValidEntry()+"");
            int flag=  checkValidEntry();
            if(flag==3){
                //do nothing
            }else if (flag==2) {
                String templateJsonString = getIntent().getExtras().getString("templateJsonString"); //copy of invVDetailObj
                JSONObject mainTemplateJSON;
                try {
                    mainTemplateJSON = new JSONObject(templateJsonString);
                    TextView quantity = (TextView) findViewById(R.id.quantity);
                    TextView unit = (TextView) findViewById(R.id.unit);
                    TextView narration = (TextView) findViewById(R.id.comment);
                    TextView districtName = (TextView) findViewById(R.id.districtName);
                    TextView address = (TextView) findViewById(R.id.address);
                    TextView phoneNo = (TextView) findViewById(R.id.phoneNo);
                    TextView itemWidth = (TextView) findViewById(R.id.itemWidth);
                    TextView itemHeight = (TextView) findViewById(R.id.itemHeight);
                    TextView dealerName=(TextView) findViewById(R.id.dealerName);
                    TextView subDealerName=(TextView) findViewById(R.id.subDealerName);
                    mainTemplateJSON.put("tqty", BigDecimal.valueOf(Double.parseDouble((quantity).getText().toString())));
                    mainTemplateJSON.put("dealerName", ((TextView) findViewById(R.id.dealerName)).getText()); //Transient field
                    mainTemplateJSON.put("text3", "open");
                    String dealerId="",subDealerId="";
                    if(dealerName.getTag()!=null){
                        dealerId=dealerName.getTag().toString();
                    }
                    mainTemplateJSON.put("text4", dealerId);

                    if(subDealerName.getTag()!=null){
                        subDealerId=subDealerName.getTag().toString();
                        mainTemplateJSON.put("text2", subDealerId);
                        mainTemplateJSON.put("subDealerName", subDealerName.getText().toString()); //Transient field
                    }else {
                        mainTemplateJSON.put("text2", "");
                        mainTemplateJSON.put("subDealerName", "");
                    }

                    mainTemplateJSON.put("text5", narration.getText().toString());
                    mainTemplateJSON.put("text6", districtName.getText().toString());
                    mainTemplateJSON.put("text7", address.getText().toString());
                    mainTemplateJSON.put("text8", phoneNo.getText().toString());
                    mainTemplateJSON.put("text9", itemWidth.getText().toString());
                    mainTemplateJSON.put("text10", itemHeight.getText().toString());
                    String partyType1 = checkBoxCheck(exis_Party,new_Party);
                    if (partyType1.equals("")){
                        Toast.makeText(this, "Please Check Party Type !", Toast.LENGTH_SHORT).show();
                        return;
                    }else{
                        mainTemplateJSON.put("text11", partyType1);
                    }
                    mainTemplateJSON.put("itemId", new JSONObject(findViewById(R.id.itemName).getTag().toString()));
                    mainTemplateJSON.put("sunitId", unit.getTag().toString());
                    mainTemplateJSON.put("amt", 0);
                    mainTemplateJSON.put("rowNum", entryPageNo+position+1);

                    mainTemplateJSON.getJSONObject("invQdetailObj").put("itemId", new JSONObject(findViewById(R.id.itemName).getTag().toString()));
                    mainTemplateJSON.getJSONObject("invQdetailObj").put("cunitId", 0);
                    mainTemplateJSON.getJSONObject("invQdetailObj").put("rate", 0);
                    mainTemplateJSON.getJSONObject("invQdetailObj").put("rateBasis", 0);
                    mainTemplateJSON.getJSONObject("invQdetailObj").put("disc", 0.0f);
                    mainTemplateJSON.getJSONObject("invQdetailObj").put("sqty", new BigDecimal(Double.parseDouble((quantity).getText().toString())));
                    mainTemplateJSON.getJSONObject("invQdetailObj").put("cqty", 1.0f);
                    mainTemplateJSON.getJSONObject("invQdetailObj").put("sunitId", unit.getTag().toString());

                    mainTemplateJSON.getJSONArray("invQdetailList").put(mainTemplateJSON.getJSONObject("invQdetailObj"));

                    String fuObjStr=mainTemplateJSON.getJSONObject("fuObj").toString();
                    JSONArray fuList=mainTemplateJSON.getJSONArray("fuList");
                    try{
                        for(Integer pos:markedOnlinePositionToDel){
                            fuList=SteelHelper.remove(pos,fuList);
                        }
                    }catch (Exception e){
                        Log.e(LOG_DBG,e.getLocalizedMessage());
                    }finally {
                        markedOnlinePositionToDel=new ArrayList<>();
                    }
                    for(Uri uri:mMedia){
                        try {
                            String arrayByte = SteelHelper.convertToBase64(uri.getPath());
                            String fileName = new File(uri.getPath()).getName();
                            String fileType = fileName.substring(fileName.lastIndexOf(".") + 1);
                            fileType="image/"+fileType;
                            JSONObject fuObj = new JSONObject(fuObjStr);
                            fuObj.put("arrayByte", arrayByte);
                            fuObj.put("fileType", fileType);
                            fuObj.put("fileName", fileName);
                            fuObj.put("resizeFilepath","");
                            Log.e(LOG_DBG,fuObj.toString());
                            fuList.put(fuObj);
                        }catch (Exception e){
                            Log.e(LOG_DBG,e.getLocalizedMessage());
                        }
                    }
                    mainTemplateJSON.put("fuList",fuList);

                } catch (Exception e) {
                    mainTemplateJSON = null;
                }
                if (mainTemplateJSON == null) {
                    Toast.makeText(this, "Error saving data", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    JSONObject requisitionMainTemplate = ((Controller) getApplication()).requisitionMainTemplate;
                    String dealerId="",subDealerId="";
                    if(dealerName.getTag()!=null){
                        dealerId=dealerName.getTag().toString();
                    }
                    if(subDealerName.getTag()!=null){
                        subDealerId=subDealerName.getTag().toString();
                    }
                    if(SteelHelper.dealerItemDuplicateEntryCheckRequisition(requisitionMainTemplate.toString(), dealerId,subDealerId, findViewById(R.id.itemName).getTag().toString())){
                        //true Means no duplicates till now
                        try {
                            requisitionMainTemplate.getJSONObject("invVoucherObj").getJSONArray("invVdetailList").put(mainTemplateJSON);
                        } catch (Exception e) {
                            Log.e(LOG_DBG, e.getLocalizedMessage());
                        }
                        dealerName.setText("");
                        dlr_serch_listlay.removeAllViews();
                        dlr_serch_listlay.setVisibility(View.GONE);
                        subDealerName.setText("");
                        sub_dlr_serch_listlay.removeAllViews();
                        sub_dlr_serch_listlay.setVisibility(View.GONE);
                        //itemName.setText("");
                        quantity.setText("");
                        narration.setText("");
                        district.setText("");
                        address.setText("");
                        phoneNo.setText("");
                        itemWidth.setText("");
                        itemHeight.setText("");
                        //unit.setText("");
                        dealerName.setError(null);
                        subDealerName.setError(null);
                        itemName.setError(null);
                        mMedia=new ArrayList<>();
                        imageHolder.removeAllViews();
                        entryPageNo++;
                    }else {
                        Toast.makeText(this,"Dealer with same item exists",Toast.LENGTH_SHORT).show();
                        dealerName.setError("Dealer with same item exists");
                    }
                }
            }else {
                Toast.makeText(this, "Enter all fields", Toast.LENGTH_SHORT).show();
            }
        }
    }
    private void performEdit(){
        int position=getIntent().getExtras().getInt("position",0);
        JSONObject requisitionMainTemplate = ((Controller) getApplication()).requisitionMainTemplate;
        int flag=checkValidEntry();
        if(flag==3){
            //
        }
        else if(flag==2){
            try{
                TextView dealerName = (TextView) findViewById(R.id.dealerName);
                TextView subDealerName = (TextView) findViewById(R.id.subDealerName);
                String dealerId="",subDealerId="";
                if(dealerName.getTag()!=null){
                    dealerId=dealerName.getTag().toString();
                }
                if(subDealerName.getTag()!=null) {
                    subDealerId = subDealerName.getTag().toString();
                }

                if(SteelHelper.dealerItemDuplicateEditCheckRequisition(requisitionMainTemplate.toString(), dealerId,subDealerId, findViewById(R.id.itemName).getTag().toString(), position)){
                    //true Means no duplicates till now
                    try {
                        JSONObject mainTemplateJSON=requisitionMainTemplate.getJSONObject("invVoucherObj").getJSONArray("invVdetailList").getJSONObject(position);

                        TextView quantity = (TextView) findViewById(R.id.quantity);
                        TextView unit = (TextView) findViewById(R.id.unit);
                        TextView narration = (TextView) findViewById(R.id.comment);
                        TextView districtName = (TextView) findViewById(R.id.districtName);
                        TextView address = (TextView) findViewById(R.id.address);
                        TextView phoneNo = (TextView) findViewById(R.id.phoneNo);
                        TextView itemWidth = (TextView) findViewById(R.id.itemWidth);
                        TextView itemHeight = (TextView) findViewById(R.id.itemHeight);


                        mainTemplateJSON.put("tqty", BigDecimal.valueOf(Double.parseDouble((quantity).getText().toString())));
                        mainTemplateJSON.put("dealerName", ((TextView) findViewById(R.id.dealerName)).getText());
                        mainTemplateJSON.put("subDealerName", ((TextView) findViewById(R.id.subDealerName)).getText());
                        mainTemplateJSON.put("text3", "open");


                        if(subDealerName.getTag()!=null){
                            subDealerId=subDealerName.getTag().toString();
                            mainTemplateJSON.put("text2", subDealerId);
                        }else {
                            mainTemplateJSON.put("text2", "");
                        }
                        mainTemplateJSON.put("text4", dealerId);
                        mainTemplateJSON.put("text5", narration.getText().toString());
                        mainTemplateJSON.put("text6", districtName.getText().toString());
                        mainTemplateJSON.put("text7", address.getText().toString());
                        mainTemplateJSON.put("text8", phoneNo.getText().toString());
                        mainTemplateJSON.put("text9", itemWidth.getText().toString());
                        mainTemplateJSON.put("text10", itemHeight.getText().toString());
                        mainTemplateJSON.put("itemId", new JSONObject(findViewById(R.id.itemName).getTag().toString()));
                        mainTemplateJSON.put("sunitId", unit.getTag().toString());
                        mainTemplateJSON.put("amt", 0);
                        //mainTemplateJSON.put("rowNum", position+entryPageNo+1);

                        mainTemplateJSON.getJSONArray("invQdetailList").getJSONObject(0).put("itemId", new JSONObject(findViewById(R.id.itemName).getTag().toString()));
                        mainTemplateJSON.getJSONArray("invQdetailList").getJSONObject(0).put("cunitId", 0);
                        mainTemplateJSON.getJSONArray("invQdetailList").getJSONObject(0).put("rate", 0);
                        mainTemplateJSON.getJSONArray("invQdetailList").getJSONObject(0).put("rateBasis", 0);
                        mainTemplateJSON.getJSONArray("invQdetailList").getJSONObject(0).put("disc", 0.0f);
                        mainTemplateJSON.getJSONArray("invQdetailList").getJSONObject(0).put("sqty", new BigDecimal(Double.parseDouble((quantity).getText().toString())));
                        mainTemplateJSON.getJSONArray("invQdetailList").getJSONObject(0).put("cqty", 1.0f);
                        mainTemplateJSON.getJSONArray("invQdetailList").getJSONObject(0).put("sunitId", unit.getTag().toString());

                        String fuObjStr=requisitionMainTemplate.getJSONObject("invVoucherObj").getJSONObject("invVdetailObj").getString("fuObj");
                        JSONArray fuList=mainTemplateJSON.getJSONArray("fuList");

                        /*//Todo new code check arnab
                        try{
                            for(Integer pos:markedOnlinePositionToDel){
                                fuList=SteelHelper.remove(pos,fuList);
                                Log.e("ok",fuList.length()+"");
                            }
                        }catch (Exception e){
                            Log.e(LOG_DBG,e.getLocalizedMessage());
                        }finally {
                            markedOnlinePositionToDel=new ArrayList<>();
                        }
                        Code ends here*/

                        for(Uri uri:mMedia){
                            try {
                                String arrayByte = SteelHelper.convertToBase64(uri.getPath());
                                String fileName = new File(uri.getPath()).getName();
                                String fileType = fileName.substring(fileName.lastIndexOf(".") + 1);
                                fileType="image/"+fileType;
                                JSONObject fuObj = new JSONObject(fuObjStr);
                                fuObj.put("arrayByte", arrayByte);
                                fuObj.put("fileType", fileType);
                                fuObj.put("fileName", fileName);
                                fuObj.put("resizeFilepath","");
                                Log.e(LOG_DBG,fuObj.toString());
                                fuList.put(fuObj);
                            }catch (Exception e){
                                //Log.e(LOG_DBG,e.getLocalizedMessage());
                            }
                        }
                        mainTemplateJSON.put("fuList",fuList);
                        Log.e("No Of Images",fuList.length()+"");

                    } catch (Exception e) {
                        Log.e(LOG_DBG, e.getLocalizedMessage());
                    }
                    dealerName.setText("");
                    dlr_serch_listlay.removeAllViews();
                    dlr_serch_listlay.setVisibility(View.GONE);
                    subDealerName.setText("");
                    sub_dlr_serch_listlay.removeAllViews();
                    sub_dlr_serch_listlay.setVisibility(View.GONE);
                    itemName.setText("");
                    quantity.setText("");
                    unit.setText("");
                    narration.setText("");
                    district.setText("");
                    address.setText("");
                    phoneNo.setText("");
                    itemWidth.setText("");
                    itemHeight.setText("");
                    setResult(RESULT_OK);
                    finish();
                }
                else {
                    dealerName.setError("Dealer with same item exists");
                    Toast.makeText(this,"Dealer with same item exists",Toast.LENGTH_SHORT).show();
                }

            }catch (Exception e){
                Log.e(LOG_DBG,e.getLocalizedMessage());
            }
        }else {
            Toast.makeText(this, "Enter all fields", Toast.LENGTH_SHORT).show();
        }
    }
    private int checkValidEntry(){
        try{
            if(!(quantity.getText().toString().equals("")))
                Double.parseDouble(quantity.getText().toString());
        }catch (Exception e){
            quantity.setError("Enter a valid quantity");
            quantity.setText("");
            return 3;
        }
        try{
            if(!(itemWidth.getText().toString().equals("")))
                Double.parseDouble(itemWidth.getText().toString());
        }catch (Exception e){
            itemWidth.setError("Enter a valid width");
            itemWidth.setText("");
            return 3;
        }
        try{
            if(!(itemHeight.getText().toString().equals("")))
                Double.parseDouble(itemHeight.getText().toString());
        }catch (Exception e){
            itemHeight.setError("Enter a valid height");
            itemHeight.setText("");
            return 3;
        }
        if(!(quantity.getText().toString().equals("")) && Double.parseDouble(quantity.getText().toString())==0){
            quantity.setError("Enter a valid quantity");
            quantity.setText("");
            return 3;
        }
        else if(dealerName.getText().toString().equals("") && subDealerName.getText().toString().equals("") && quantity.getText().toString().equals("")){
            return 0;       //No fields are entered
        }
        else if(!(dealerName.getText().toString().equals("")&&subDealerName.getText().toString().equals("")) && !(itemName.getText().toString().equals("")) && !(quantity.getText().toString().equals(""))){
            return 2;
        }
        else {
            return 1;       //Partial fill
        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Thread.setDefaultUncaughtExceptionHandler(new TopExceptionHandler(this));

        setContentView(R.layout.activity_requisition_entry);
        Toolbar toolbar= (Toolbar)findViewById(R.id.my_actionbar_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        View requisitionImgPreview=findViewById(R.id.requisitionImgPreview);
        View requisitionCommentLayout=findViewById(R.id.requisitionCommentLayout);
        View btnAttachDoc=null;
        if(requisitionImgPreview!=null) {
            requisitionImgPreview.setVisibility(View.VISIBLE);
            btnAttachDoc=requisitionImgPreview.findViewById(R.id.btnAttachDoc);
        }
        if(requisitionCommentLayout!=null){
            requisitionCommentLayout.setVisibility(View.VISIBLE);
        }
        SharedPreferences sharedPref = getSharedPreferences("loginCredentials", Context.MODE_PRIVATE);
        cid = sharedPref.getString("cid", "");
        segid = sharedPref.getString("segid", "");
        tempKeyCode = sharedPref.getString("storedKeyCode", "");
        sLid = sharedPref.getString("storedSlid", "");
        uid = sharedPref.getString("storedUserId", "");
        salesman_name = sharedPref.getString("salesman_name", "");

        dealerName=(EditText)findViewById(R.id.dealerName);
        subDealerName=(EditText)findViewById(R.id.subDealerName);
        itemName=(EditText)findViewById(R.id.itemName);
        quantity=(EditText)findViewById(R.id.quantity);
        unit=(TextView)findViewById(R.id.unit);
        narration=(EditText)findViewById(R.id.comment);
        district=(EditText)findViewById(R.id.districtName);
        address=(EditText)findViewById(R.id.address);
        phoneNo=(EditText)findViewById(R.id.phoneNo);
        itemWidth=(EditText)findViewById(R.id.itemWidth);
        itemHeight=(EditText)findViewById(R.id.itemHeight);
        imageHolder = (LinearLayout) findViewById(R.id.imageHolder);
        dlerType_lay = (LinearLayout) findViewById(R.id.dlerType_lay);
        dlr_serch_listlay = (LinearLayout) findViewById(R.id.dlr_serch_listlay);
        sub_dlr_serch_listlay = (LinearLayout) findViewById(R.id.sub_dlr_serch_listlay);

        exis_Party = (CheckBox) findViewById(R.id.exis_Party);
        new_Party = (CheckBox) findViewById(R.id.new_Party);

        exis_Party.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (new_Party.isChecked()) {
                    new_Party.setChecked(false);
                }
            }
        });
        new_Party.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (exis_Party.isChecked()) {
                    exis_Party.setChecked(false);
                }
            }
        });


        dealerName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(RequisitionEntryActivity.this,RequisitionDealerNameAutocompleteActivity.class);
                intent.putExtra("type","dealer");
                intent.putExtra("dealerId","0");
                intent.putExtra("sLid",getIntent().getExtras().getString("sLid"));
                startActivityForResult(intent, REQUEST_DEALER_AUTOCOMPLETE);
            }
        });
        subDealerName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RequisitionEntryActivity.this, RequisitionDealerNameAutocompleteActivity.class);
                intent.putExtra("type", "sub_dealer");
                String dealerId="0";
                if(dealerName.getTag()!=null){
                    dealerId=dealerName.getTag().toString();
                }
                intent.putExtra("dealerId", dealerId);
                intent.putExtra("sLid", getIntent().getExtras().getString("sLid"));
                startActivityForResult(intent, REQUEST_SUBDEALER_AUTOCOMPLETE);
            }
        });
        itemName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(RequisitionEntryActivity.this,ItemNameAutocompleteActivity.class);
                intent.putExtra("type","requisition");
                startActivityForResult(intent, REQUEST_ITEM_AUTOCOMPLETE);
            }
        });
        narration.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                TextView commentLimitIndicator = (TextView) findViewById(R.id.commentLimitIndicator);
                commentLimitIndicator.setText(s.length()+"/600");
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        if(btnAttachDoc!=null){
            btnAttachDoc.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //selectImage();
                    int ACCESS_CAMERA_PERMISSION = ContextCompat.checkSelfPermission(RequisitionEntryActivity.this, Manifest.permission.CAMERA);
                    int WRITE_EXTERNAL_STORAGE_PERMISSION = ContextCompat.checkSelfPermission(RequisitionEntryActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE);

                    if(ACCESS_CAMERA_PERMISSION== PackageManager.PERMISSION_GRANTED && WRITE_EXTERNAL_STORAGE_PERMISSION== PackageManager.PERMISSION_GRANTED) {
                        getImages();
                    } else {
                        ActivityCompat.requestPermissions(RequisitionEntryActivity.this,new String[]{
                                        Manifest.permission.CAMERA,
                                        Manifest.permission.WRITE_EXTERNAL_STORAGE},
                                ASK_MULTIPLE_PERMISSION_REQUEST_CODE);
                    }

                }
            });
        }
        boolean editFlag = getIntent().getExtras().getBoolean("EditFlag");
        final int position = getIntent().getExtras().getInt("position");
        final boolean canDelete=getIntent().getExtras().getBoolean("canDelete",false);
        if(editFlag){
            View discard=findViewById(R.id.discard);
            discard.setVisibility(View.GONE);
            ((TextView)findViewById(R.id.screenTitle)).setText("Edit");
            if(canDelete)
                findViewById(R.id.entryDelete).setVisibility(View.VISIBLE);
            final JSONObject requisitionMainTemplate = ((Controller) getApplication()).requisitionMainTemplate;
            findViewById(R.id.entryDelete).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try{
                        if(requisitionMainTemplate.getJSONObject("invVoucherObj").getJSONArray("invVdetailList").getJSONObject(position).isNull("id")){
                            //Log.e("New", "New Entry");
                            requisitionMainTemplate.getJSONObject("invVoucherObj").getJSONArray("invVdetailList").getJSONObject(position).put("status", 0);
                            JSONArray invVdetailList=requisitionMainTemplate.getJSONObject("invVoucherObj").getJSONArray("invVdetailList");
                            invVdetailList=SteelHelper.remove(position,invVdetailList);
                            requisitionMainTemplate.getJSONObject("invVoucherObj").put("invVdetailList",invVdetailList);
                            Toast.makeText(RequisitionEntryActivity.this,"Deleted",Toast.LENGTH_SHORT).show();
                            setResult(RESULT_OK);
                            finish();
                        }else {
                            requisitionMainTemplate.getJSONObject("invVoucherObj").getJSONArray("invVdetailList").getJSONObject(position).put("status",0);
                            Toast.makeText(RequisitionEntryActivity.this,"Deleted",Toast.LENGTH_SHORT).show();
                            setResult(RESULT_OK);
                            finish();
                        }

                    }catch (Exception e){
                        Toast.makeText(RequisitionEntryActivity.this,"Error in deletion",Toast.LENGTH_SHORT).show();
                    }
                }
            });
            findViewById(R.id.continue_next).setVisibility(View.GONE);
            try{
                JSONObject mainTemplateJSON=requisitionMainTemplate.getJSONObject("invVoucherObj").getJSONArray("invVdetailList").getJSONObject(position);
                String strDealerName=mainTemplateJSON.isNull("dealerName")?"":mainTemplateJSON.getString("dealerName");
                String strSubdealerName=mainTemplateJSON.isNull("subDealerName")?"":mainTemplateJSON.getString("subDealerName");
                String strItemName=mainTemplateJSON.getJSONObject("itemId").getString("itemName");
                BigDecimal strQuantity=BigDecimal.valueOf(mainTemplateJSON.getDouble("tqty"));
                String strDealerId=mainTemplateJSON.isNull("text4")?"":mainTemplateJSON.getString("text4");
                String strSubdealerId=mainTemplateJSON.isNull("text2")?"":mainTemplateJSON.getString("text2");
                String strComment=mainTemplateJSON.getString("text5");
                String strDistrict=mainTemplateJSON.getString("text6");
                String strAddress=mainTemplateJSON.getString("text7");
                String strPhoneNo=mainTemplateJSON.getString("text8");
                String strItemWidth=mainTemplateJSON.getString("text9");
                String strItemHeight=mainTemplateJSON.getString("text10");
                String text11=mainTemplateJSON.getString("text11");
                String strUnitId=mainTemplateJSON.getJSONObject("itemId").getJSONObject("unit").getString("unitId");
                String strUnitName=mainTemplateJSON.getJSONObject("itemId").getJSONObject("unit").getString("unitName");
                String strItemObj=mainTemplateJSON.getJSONObject("itemId").toString();

                JSONArray fuList=mainTemplateJSON.getJSONArray("fuList");

                ArrayList<String> mOnlineMedia=new ArrayList<>();
                for(int i=0;i<fuList.length();i++){
                    JSONObject fuObj=fuList.getJSONObject(i);
                    //Use arrayByte to put data and byteArray to fetch
                    String arrayByte=fuObj.getString("byteArray");
                    mOnlineMedia.add(arrayByte);
                }
                try {
                    showOnlineMedia(mOnlineMedia);
                }catch (Exception e){
                    Log.e(LOG_DBG,e.getLocalizedMessage());
                }
                dealerName.setText(strDealerName);
                dealerName.setTag(strDealerId);
                subDealerName.setText(strSubdealerName);
                if (text11.equals("Old Party")){
                    exis_Party.setChecked(true);
                }else {
                    new_Party.setChecked(true);
                }
                subDealerName.setTag(strSubdealerId);
                itemName.setText(strItemName);
                itemName.setTag(strItemObj);          //item obj
                unit.setText(strUnitName);
                unit.setTag(strUnitId);                       // unit Id
                quantity.setText(strQuantity.toPlainString());
                narration.setText(strComment);
                district.setText(strDistrict);
                address.setText(strAddress);
                phoneNo.setText(strPhoneNo);
                itemWidth.setText(strItemWidth);
                itemHeight.setText(strItemHeight);
            }catch (Exception e){
                //Log.e(LOG_DBG,e.getLocalizedMessage());
            }
        }else {
            //Entry initialization
            View discard=findViewById(R.id.discard);
            discard.setVisibility(View.VISIBLE);
            discard.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        JSONObject requisitionMainTemplate = ((Controller) getApplication()).requisitionMainTemplate;
                        JSONArray jsonArray= requisitionMainTemplate.getJSONObject("invVoucherObj").getJSONArray("invVdetailList");
                        if(entryPageNo<=0){
                            finish();
                        }else {
                            entryPageNo--;
                            JSONObject earlierObj=jsonArray.getJSONObject(jsonArray.length()-1);
                            {
                                String strDealerName=earlierObj.getString("dealerName");
                                String strSubdealerName=earlierObj.isNull("subDealerName")?"":earlierObj.getString("subDealerName");
                                String strItemName=earlierObj.getJSONObject("itemId").getString("itemName");
                                BigDecimal strQuantity=BigDecimal.valueOf(earlierObj.getDouble("tqty"));
                                String strDealerId=earlierObj.isNull("text4")?"":earlierObj.getString("text4");
                                String strSubdealerId=earlierObj.isNull("text2")?"":earlierObj.getString("text2");
                                String strComment=earlierObj.getString("text5");
                                String strDistrict=earlierObj.getString("text6");
                                String strAddress=earlierObj.getString("text7");
                                String strPhoneNo=earlierObj.getString("text8");
                                String strItemWidth=earlierObj.getString("text9");
                                String strItemHeight=earlierObj.getString("text10");
                                String strUnitId=earlierObj.getJSONObject("itemId").getJSONObject("unit").getString("unitId");
                                String strUnitName=earlierObj.getJSONObject("itemId").getJSONObject("unit").getString("unitName");
                                String strItemObj=earlierObj.getJSONObject("itemId").toString();

                                dealerName.setText(strDealerName);
                                dealerName.setTag(strDealerId);
                                subDealerName.setText(strSubdealerName);
                                subDealerName.setTag(strSubdealerId);
                                itemName.setText(strItemName);
                                itemName.setTag(strItemObj);          //item obj
                                unit.setText(strUnitName);
                                unit.setTag(strUnitId);                       // unit Id
                                quantity.setText(strQuantity.toPlainString());
                                narration.setText(strComment);
                                district.setText(strDistrict);
                                address.setText(strAddress);
                                phoneNo.setText(strPhoneNo);
                                itemWidth.setText(strItemWidth);
                                itemHeight.setText(strItemHeight);
                            }
                            jsonArray=SteelHelper.remove(jsonArray.length()-1,jsonArray);
                            ((Controller) getApplication()).requisitionMainTemplate.getJSONObject("invVoucherObj").put("invVdetailList",jsonArray);
                        }
                    }catch (Exception e){
                    }
                }
            });
            /*JSONObject requisitionMainTemplate = ((Controller) getApplication()).requisitionMainTemplate;
            try{
                JSONObject mainTemplateJSON=requisitionMainTemplate.getJSONObject("invVoucherObj").getJSONObject("invVdetailObj").getJSONObject("itemId");
                String defaultUnitName=mainTemplateJSON.getJSONObject("unit").getString("unitName");
                String defaultItemName=mainTemplateJSON.getString("itemName");
                String defaultUnitId=mainTemplateJSON.getJSONObject("unit").getString("unitId");
                //No default name for requisition activity
                *//*itemName.setText(defaultItemName);
                itemName.setTag(mainTemplateJSON.toString());
                unit.setText(defaultUnitName);
                unit.setTag(defaultUnitId);*//*
            }catch (Exception e){
                Log.e(LOG_DBG,e.getLocalizedMessage());
            }*/
        }
        dealerName.requestFocus();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==REQUEST_DEALER_AUTOCOMPLETE){
            if(resultCode==RESULT_OK){
                mReturning="DEALER_AUTOCOMPLETE";
                return_name=data.getExtras().getString("name");
                return_id=data.getExtras().getString("id");
                rt_address=data.getExtras().getString("address");
                rt_contact=data.getExtras().getString("contact");
                dlerType_lay.setVisibility(View.VISIBLE);
                listbyDealerName = new ListbyDealerName(RequisitionEntryActivity.this, 1,"d");
                listbyDealerName.execute(cid, segid, sLid,return_id,tempKeyCode);
                //Toast.makeText(RequisitionEntryActivity.this,"Helo"+rt_address+rt_contact,Toast.LENGTH_SHORT).show();
            }
        }else if(requestCode==REQUEST_SUBDEALER_AUTOCOMPLETE){
            if(resultCode==RESULT_OK){
                mReturning="SUBDEALER_AUTOCOMPLETE";
                return_name=data.getExtras().getString("name");
                return_id=data.getExtras().getString("id");
                listbyDealerName = new ListbyDealerName(RequisitionEntryActivity.this, 1,"s_d");
                listbyDealerName.execute(cid, segid, sLid,return_id,tempKeyCode);
            }

        } else if(requestCode==REQUEST_ITEM_AUTOCOMPLETE){
            if(resultCode==RESULT_OK){
                mReturning="ITEM_AUTOCOMPLETE";
                return_name=data.getExtras().getString("name");
                return_unit_name=data.getExtras().getString("unit_name");
                return_unit_id=data.getExtras().getString("unit_id");
                return_JsonObj=data.getExtras().getString("jsonObj");
            }
        }
        else if(requestCode==INTENT_REQUEST_GET_IMAGES){
            if(resultCode==RESULT_OK) {
                Parcelable[] parcelableUris = data.getParcelableArrayExtra(ImagePickerActivity.EXTRA_IMAGE_URIS);
                if (parcelableUris == null) {
                    return;
                }
                // Java doesn't allow array casting, this is a little hack
                Uri[] uris = new Uri[parcelableUris.length];
                System.arraycopy(parcelableUris, 0, uris, 0, parcelableUris.length);

                if (uris != null) {
                    for (Uri uri : uris) {
                        Log.i(LOG_DBG, " uri: " + uri);
                        mMedia.add(uri);
                    }
                    showMedia();
                }
            }
        }
        else if(requestCode==INTENT_REQUEST_BROWSE_IMAGE){
            if(resultCode==RESULT_OK){
                mMedia.remove(data.getExtras().getInt("positionToRemove",0));
                showMedia(mMedia);
            }
        }
        else if(requestCode==INTENT_REQUEST_BROWSE_IMAGE_ONLINE){
            if(resultCode==RESULT_OK){
                final JSONObject requisitionMainTemplate = ((Controller) getApplication()).requisitionMainTemplate;
                int positionToRemove = data.getExtras().getInt("positionToRemove", 0);
                ArrayList<String> mOnlineMedia = new ArrayList<>();
                try {
                    final int position = getIntent().getExtras().getInt("position");
                    JSONObject mainTemplateJSON = requisitionMainTemplate.getJSONObject("invVoucherObj").getJSONArray("invVdetailList").getJSONObject(position);
                    JSONArray fuList=mainTemplateJSON.getJSONArray("fuList");
                    fuList=SteelHelper.remove(positionToRemove,fuList);
                    mainTemplateJSON.put("fuList",fuList);
                    markedOnlinePositionToDel.add(positionToRemove);

                    for(int i=0;i<fuList.length();i++){
                        JSONObject fuObj=fuList.getJSONObject(i);
                        //Use arrayByte to put data and byteArray to fetch
                        String arrayByte = fuObj.getString("byteArray");
                        mOnlineMedia.add(arrayByte);
                    }
                }catch (Exception e){
                }
                showOnlineMedia(mOnlineMedia);
            }
        }
    }
    private void showMedia(final ArrayList<Uri> mMedia){
        LinearLayout imageHolder = (LinearLayout) findViewById(R.id.imageHolder);
        imageHolder.removeAllViews();
        for(int i=0;i<mMedia.size();i++){
            final ImageView mImageView = new ImageView(this);
            mImageView.setImageURI(mMedia.get(i));
            //Removes the padding from both sides
            mImageView.setAdjustViewBounds(true);
            imageHolder.addView(mImageView);
            final int pos=i;
            mImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        Intent intent = new Intent(RequisitionEntryActivity.this, MultipleURIImageBrowserActivity.class);
                        intent.putExtra("imageURIs", mMedia);
                        intent.putExtra("position", pos);
                        startActivityForResult(intent, INTENT_REQUEST_BROWSE_IMAGE);
                    }catch (Exception e){
                    }
                }
            });
        }
    }
    private void showOnlineMedia(final ArrayList<String> mOnlineMedia){
        LinearLayout imageHolder = (LinearLayout) findViewById(R.id.imageHolderOnline);
        if(imageHolder!=null)
            imageHolder.removeAllViews();
        for(int i=0;i<mOnlineMedia.size();i++){
            final ImageView mImageView = new ImageView(this);
            String base64Img=mOnlineMedia.get(i);
            mImageView.setImageBitmap(SteelHelper.convertToImage(RequisitionEntryActivity.this,SteelHelper.resizeBase64Image(base64Img,160,250)));
            //Removes the padding from both sides
            mImageView.setAdjustViewBounds(true);
            imageHolder.addView(mImageView);
            final int pos=i;
            mImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent=new Intent(RequisitionEntryActivity.this,MultipleURIImageBrowserActivity.class);
                    GlobalConfiguration.mOnlineMedia=mOnlineMedia;
                    intent.putExtra("position", pos);
                    startActivityForResult(intent, INTENT_REQUEST_BROWSE_IMAGE_ONLINE);
                }
            });
        }
    }
    private void showMedia() {
        LinearLayout imageHolder = (LinearLayout) findViewById(R.id.imageHolder);
        if (imageHolder != null){
            imageHolder.removeAllViews();
            for (int i = 0; i < mMedia.size(); i++) {
                final ImageView mImageView = new ImageView(this);
                Bitmap bitmap = SteelHelper.createScaledBitmap(mMedia.get(i).getPath(), 160, 250);
                mImageView.setImageBitmap(bitmap);
                //Removes the padding from both sides
                mImageView.setAdjustViewBounds(true);
                imageHolder.addView(mImageView);
                final int pos = i;
                mImageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(RequisitionEntryActivity.this, MultipleURIImageBrowserActivity.class);
                        intent.putExtra("imageURIs", mMedia);
                        intent.putExtra("position", pos);
                        startActivityForResult(intent, INTENT_REQUEST_BROWSE_IMAGE);
                    }
                });
            }
        }
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        if(mReturning==null){
            return;
        }
        if(mReturning.equals("DEALER_AUTOCOMPLETE")) {
            findViewById(R.id.dealerName).setTag(return_id);
            ((TextView)findViewById(R.id.dealerName)).setText(return_name);
            address.setText(rt_address);
            phoneNo.setText(rt_contact);
        }else if(mReturning.equals("SUBDEALER_AUTOCOMPLETE")){
            findViewById(R.id.subDealerName).setTag(return_id);
            ((TextView)findViewById(R.id.subDealerName)).setText(return_name);
        }else if(mReturning.equals("ITEM_AUTOCOMPLETE")){
            ((TextView)findViewById(R.id.itemName)).setText(return_name);
            findViewById(R.id.itemName).setTag(return_JsonObj);
            ((TextView)findViewById(R.id.unit)).setText(return_unit_name);
            findViewById(R.id.unit).setTag(return_unit_id);
        }
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    public boolean isInteger(double n, double tolerance) {
        double absN = Math.abs(n);
        return Math.abs(absN - Math.round(absN)) <= tolerance;
    }

    public class ListbyDealerName extends AsyncTask<String, Void, String> {
        private Context context;
        private int byGetOrPost=0;          //Flag 0 means get and 1 means post. (By default it is get).
        String dealerType;

        ListbyDealerName(Context context,int flag,String dealerType) {
            this.context = context;
            byGetOrPost = flag;
            this.dealerType = dealerType;
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
                    String partyId = arg0[3];
                    String tempKeyCode = arg0[4];
                    String dnsport= GlobalConfiguration.getDomainport();
                    String link="http://"+dnsport+"/SteelSales-war/Stl_RequisitionTxn_Last_Three_C_Api";
                    HashMap<String,String> hm=new HashMap<>();
                    hm.put("cid",cid);
                    hm.put("segid", segid);
                    hm.put("slid", sLid);
                    hm.put("partyId", partyId);
                    hm.put("keyCode", tempKeyCode);
                    if (dealerType.equals("d")) {
                        hm.put("type", "dealer");
                    }else{
                        hm.put("type", "subdealer");
                    }
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
            listbyDealerName=null;
            //Log.e("........",result);
            try{
                JSONObject mainObj=new JSONObject(result);
                if(mainObj.getString("msg").contains("Authorization Failure")){
                    //Code for logout here
                    Toast.makeText(RequisitionEntryActivity.this,"Session expired.",Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(RequisitionEntryActivity.this, LoginActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    intent.putExtra("EXIT", true);
                    startActivity(intent);
                    finish();

                }else if(mainObj.getString("type").equals("success")){
                    JSONArray data=mainObj.getJSONArray("data");
                    if (dealerType.equals("d")) {
                        viewRenderType(dlr_serch_listlay, data, context);
                    }else {
                        viewRenderType(sub_dlr_serch_listlay, data, context);
                    }
                }else {
                    Log.e(LOG_DBG,result);
                    throw new Exception("Server error");
                }
            }catch (Exception e){
                Log.e(LOG_DBG,e.toString());
                Toast.makeText(context,e.toString(),Toast.LENGTH_SHORT).show();
            }
        }
    }

    String checkBoxCheck(CheckBox box1, CheckBox box2){
        if (box1.isChecked()){
            return "Old Party";
        }else if(box2.isChecked()){
            return "New Party";
        }else{
            return "";
        }
    }

    void viewRenderType(LinearLayout linearLayout,JSONArray data,Context context){
        linearLayout.removeAllViews();
        if (data.length()>0){
            linearLayout.setVisibility(View.VISIBLE);
        }else{
            linearLayout.setVisibility(View.GONE);
        }
        try {
            for (int i = 0; i < data.length(); i++) {
                JSONObject object12 = data.getJSONObject(i);

                LayoutInflater inflater = LayoutInflater.from(context);
                View inflatedLayout = inflater.inflate(R.layout.requsion_list_cell, null, false);
                TextView itemName = (TextView) inflatedLayout.findViewById(R.id.itemName_d);
                TextView date_d = (TextView) inflatedLayout.findViewById(R.id.date_d);
                TextView qty_d = (TextView) inflatedLayout.findViewById(R.id.qty_d);
                LinearLayout devider_lay = inflatedLayout.findViewById(R.id.devider_lay);
                itemName.setText(object12.getString("item_name"));
                date_d.setText(object12.getString("vdate"));
                qty_d.setText(object12.getString("tqty"));
                linearLayout.addView(inflatedLayout);

            }
        }catch (Exception e){Log.e("Error",e.toString());}

    }

}
