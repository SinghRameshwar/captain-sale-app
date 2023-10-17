package com.aspl.steel;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.aspl.steel.CrashReport.CrashReportSender;
import com.aspl.steel.CrashReport.TopExceptionHandler;

import org.json.JSONArray;
import org.json.JSONObject;

import java.math.BigDecimal;


/**
 *  Created by arnab on 29/2/16.
 */
public class DailySaleEntryActivity extends AppCompatActivity{
    EditText dealerName,itemName,quantity;
    TextView unit;
    static int REQUEST_DEALER_AUTOCOMPLETE=21,REQUEST_ITEM_AUTOCOMPLETE=22;
    String return_name="",return_id="",return_JsonObj="",return_unit_name="",return_unit_id="",mReturning;
    String LOG_DBG=getClass().getSimpleName();
    int entryPageNo=0;

    public void onFinishClick(View view){
        //onContinueClick(view);
        boolean editFlag = getIntent().getExtras().getBoolean("EditFlag");
        if (editFlag) {
            performEdit();
        }else {
            int flag=  checkValidEntry();
            if(flag==0){
                //No fields entered
                setResult(RESULT_OK);
                finish();
            }
            else if(flag==1){
                Toast.makeText(this,"Enter all the fields",Toast.LENGTH_SHORT).show();
            }
            else if(flag==3){
                //do nothing
            }
            else {
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
        if (editFlag) {
            performEdit();
        }
        else{
            int flag=  checkValidEntry();
            if(flag==3){
                //do nothing
            }
            else if (flag==2) {
                String templateJsonString = getIntent().getExtras().getString("templateJsonString"); //copy of invVDetailObj
                JSONObject mainTemplateJSON;
                try {
                    mainTemplateJSON = new JSONObject(templateJsonString);
                    TextView quantity = (TextView) findViewById(R.id.quantity);
                    TextView unit = (TextView) findViewById(R.id.unit);
                    mainTemplateJSON.put("tqty", BigDecimal.valueOf(Double.parseDouble((quantity).getText().toString())));
                    mainTemplateJSON.put("dealerName", ((TextView) findViewById(R.id.dealerName)).getText());
                    mainTemplateJSON.put("text4", Integer.parseInt(findViewById(R.id.dealerName).getTag().toString()));
                    mainTemplateJSON.put("itemId", new JSONObject(findViewById(R.id.itemName).getTag().toString()));
                    mainTemplateJSON.put("sunitId", unit.getTag().toString());
                    mainTemplateJSON.put("amt", 0);

                    mainTemplateJSON.getJSONObject("invQdetailObj").put("itemId", new JSONObject(findViewById(R.id.itemName).getTag().toString()));
                    mainTemplateJSON.getJSONObject("invQdetailObj").put("cunitId", 0);
                    mainTemplateJSON.getJSONObject("invQdetailObj").put("rate", 0);
                    mainTemplateJSON.getJSONObject("invQdetailObj").put("rateBasis", 0);
                    mainTemplateJSON.getJSONObject("invQdetailObj").put("disc", 0.0f);
                    mainTemplateJSON.getJSONObject("invQdetailObj").put("sqty", new BigDecimal(Double.parseDouble((quantity).getText().toString())));
                    mainTemplateJSON.getJSONObject("invQdetailObj").put("cqty", 1.0f);
                    mainTemplateJSON.getJSONObject("invQdetailObj").put("sunitId", unit.getTag().toString());

                    mainTemplateJSON.getJSONArray("invQdetailList").put(mainTemplateJSON.getJSONObject("invQdetailObj"));

                } catch (Exception e) {
                    mainTemplateJSON = null;
                }
                if (mainTemplateJSON == null) {
                    Toast.makeText(this, "Error saving data", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    JSONObject dailySalesMainTemplate = ((Controller) getApplication()).dailySalesMainTemplate;
                    if(SteelHelper.dealerItemDuplicateEntryCheckDaliySales(dailySalesMainTemplate.toString(), findViewById(R.id.dealerName).getTag().toString(), findViewById(R.id.itemName).getTag().toString())){
                        //true Means no duplicates till now
                        try {
                            dailySalesMainTemplate.getJSONObject("invVoucherObj").getJSONArray("invVdetailList").put(mainTemplateJSON);
                        } catch (Exception e) {
                            //Log.e(LOG_DBG, e.getLocalizedMessage());
                        }
                        dealerName.setText("");
                        //itemName.setText("");
                        quantity.setText("");
                        //unit.setText("");
                        dealerName.setError(null);
                        itemName.setError(null);
                        entryPageNo++;
                    }
                    else {
                        Toast.makeText(this,"Dealer with same item exists",Toast.LENGTH_SHORT).show();
                        dealerName.setError("Dealer with same item exists");
                    }
                }
            }
            else {
                Toast.makeText(this, "Enter all fields", Toast.LENGTH_SHORT).show();
            }
        }
    }
    private void performEdit(){
        int position=getIntent().getExtras().getInt("position",0);
        JSONObject dailySalesMainTemplate = ((Controller) getApplication()).dailySalesMainTemplate;
        int flag=checkValidEntry();
        if(flag==3){
            //
        }
        else if(flag==2){
            try{
                if(SteelHelper.dealerItemDuplicateEditCheck(dailySalesMainTemplate.toString(), findViewById(R.id.dealerName).getTag().toString(), findViewById(R.id.itemName).getTag().toString(), position)){
                    //true Means no duplicates till now
                    try {
                        JSONObject mainTemplateJSON=dailySalesMainTemplate.getJSONObject("invVoucherObj").getJSONArray("invVdetailList").getJSONObject(position);

                        TextView quantity = (TextView) findViewById(R.id.quantity);
                        TextView unit = (TextView) findViewById(R.id.unit);
                        mainTemplateJSON.put("tqty", BigDecimal.valueOf(Double.parseDouble((quantity).getText().toString())));
                        mainTemplateJSON.put("dealerName", ((TextView) findViewById(R.id.dealerName)).getText());
                        mainTemplateJSON.put("text4", Integer.parseInt(findViewById(R.id.dealerName).getTag().toString()));
                        mainTemplateJSON.put("itemId", new JSONObject(findViewById(R.id.itemName).getTag().toString()));
                        mainTemplateJSON.put("sunitId", unit.getTag().toString());
                        mainTemplateJSON.put("amt", 0);

                        mainTemplateJSON.getJSONArray("invQdetailList").getJSONObject(0).put("itemId", new JSONObject(findViewById(R.id.itemName).getTag().toString()));
                        mainTemplateJSON.getJSONArray("invQdetailList").getJSONObject(0).put("cunitId", 0);
                        mainTemplateJSON.getJSONArray("invQdetailList").getJSONObject(0).put("rate", 0);
                        mainTemplateJSON.getJSONArray("invQdetailList").getJSONObject(0).put("rateBasis", 0);
                        mainTemplateJSON.getJSONArray("invQdetailList").getJSONObject(0).put("disc", 0.0f);
                        mainTemplateJSON.getJSONArray("invQdetailList").getJSONObject(0).put("sqty", new BigDecimal(Double.parseDouble((quantity).getText().toString())));
                        mainTemplateJSON.getJSONArray("invQdetailList").getJSONObject(0).put("cqty", 1.0f);
                        mainTemplateJSON.getJSONArray("invQdetailList").getJSONObject(0).put("sunitId", unit.getTag().toString());
                    } catch (Exception e) {
                        //Log.e(LOG_DBG, e.getLocalizedMessage());
                    }
                    dealerName.setText("");
                    itemName.setText("");
                    quantity.setText("");
                    unit.setText("");
                    setResult(RESULT_OK);
                    finish();
                }
                else {
                    dealerName.setError("Dealer with same item exists");
                    Toast.makeText(this,"Dealer with same item exists",Toast.LENGTH_SHORT).show();
                }

            }catch (Exception e){
                //Log.e(LOG_DBG,e.getLocalizedMessage());
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
        if(!(quantity.getText().toString().equals("")) && Double.parseDouble(quantity.getText().toString())==0){
            quantity.setError("Enter a valid quantity");
            quantity.setText("");
            return 3;
        }
        else if(dealerName.getText().toString().equals("") && quantity.getText().toString().equals("")){
            return 0;       //No fields are entered
        }
        else if(!(dealerName.getText().toString().equals("")) && !(itemName.getText().toString().equals("")) && !(quantity.getText().toString().equals(""))){
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
        setContentView(R.layout.activity_daily_sale_entry);
        Toolbar toolbar= (Toolbar)findViewById(R.id.my_actionbar_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        dealerName=(EditText)findViewById(R.id.dealerName);
        itemName=(EditText)findViewById(R.id.itemName);
        quantity=(EditText)findViewById(R.id.quantity);
        unit=(TextView)findViewById(R.id.unit);
        dealerName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(DailySaleEntryActivity.this,DealerNameAutocompleteActivity.class);
                intent.putExtra("type","Dealer");
                intent.putExtra("sLid",getIntent().getExtras().getString("sLid"));
                startActivityForResult(intent, REQUEST_DEALER_AUTOCOMPLETE);
            }
        });
        itemName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(DailySaleEntryActivity.this,ItemNameAutocompleteActivity.class);
                startActivityForResult(intent, REQUEST_ITEM_AUTOCOMPLETE);
            }
        });
        boolean editFlag = getIntent().getExtras().getBoolean("EditFlag");
        final int position = getIntent().getExtras().getInt("position");
        final boolean canDelete=getIntent().getExtras().getBoolean("canDelete",false);
        if(editFlag){
            View discard=findViewById(R.id.discard);
            discard.setVisibility(View.GONE);
            ((TextView)findViewById(R.id.screenTitle)).setText("Edit");
            if(canDelete)
                findViewById(R.id.entryDelete).setVisibility(View.VISIBLE);
            final JSONObject dailySalesMainTemplate = ((Controller) getApplication()).dailySalesMainTemplate;
            findViewById(R.id.entryDelete).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    try{
                        if(dailySalesMainTemplate.getJSONObject("invVoucherObj").getJSONArray("invVdetailList").getJSONObject(position).isNull("id")){
                            //Log.e("New", "New Entry");
                            dailySalesMainTemplate.getJSONObject("invVoucherObj").getJSONArray("invVdetailList").getJSONObject(position).put("status", 0);
                            JSONArray invVdetailList=dailySalesMainTemplate.getJSONObject("invVoucherObj").getJSONArray("invVdetailList");
                            invVdetailList=SteelHelper.remove(position,invVdetailList);
                            dailySalesMainTemplate.getJSONObject("invVoucherObj").put("invVdetailList",invVdetailList);
                            Toast.makeText(DailySaleEntryActivity.this,"Deleted",Toast.LENGTH_SHORT).show();
                            setResult(RESULT_OK);
                            finish();
                        }else {
                            dailySalesMainTemplate.getJSONObject("invVoucherObj").getJSONArray("invVdetailList").getJSONObject(position).put("status",0);
                            Toast.makeText(DailySaleEntryActivity.this,"Deleted",Toast.LENGTH_SHORT).show();
                            setResult(RESULT_OK);
                            finish();
                        }

                    }catch (Exception e){
                        Toast.makeText(DailySaleEntryActivity.this,"Error in deletion",Toast.LENGTH_SHORT).show();
                    }
                }
            });
            findViewById(R.id.continue_next).setVisibility(View.GONE);
            try{
                JSONObject mainTemplateJSON=dailySalesMainTemplate.getJSONObject("invVoucherObj").getJSONArray("invVdetailList").getJSONObject(position);
                String strDealerName=mainTemplateJSON.getString("dealerName");
                String strItemName=mainTemplateJSON.getJSONObject("itemId").getString("itemName");
                BigDecimal strQuantity=BigDecimal.valueOf(mainTemplateJSON.getDouble("tqty"));
                String strDealerId=mainTemplateJSON.getString("text4");
                String strUnitId=mainTemplateJSON.getJSONObject("itemId").getJSONObject("unit").getString("unitId");
                String strUnitName=mainTemplateJSON.getJSONObject("itemId").getJSONObject("unit").getString("unitName");
                String strItemObj=mainTemplateJSON.getJSONObject("itemId").toString();

                dealerName.setText(strDealerName);
                dealerName.setTag(strDealerId);
                itemName.setText(strItemName);
                itemName.setTag(strItemObj);          //item obj
                unit.setText(strUnitName);
                unit.setTag(strUnitId);                       // unit Id
                quantity.setText(strQuantity.toPlainString());
                Log.e(LOG_DBG,mainTemplateJSON.toString());
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
                        JSONObject dailySalesMainTemplate = ((Controller) getApplication()).dailySalesMainTemplate;
                        JSONArray jsonArray= dailySalesMainTemplate.getJSONObject("invVoucherObj").getJSONArray("invVdetailList");
                        if(entryPageNo<=0){
                            finish();
                        }else {
                            entryPageNo--;
                            JSONObject earlierObj=jsonArray.getJSONObject(jsonArray.length()-1);
                            {
                                String strDealerName=earlierObj.getString("dealerName");
                                String strItemName=earlierObj.getJSONObject("itemId").getString("itemName");
                                BigDecimal strQuantity=BigDecimal.valueOf(earlierObj.getDouble("tqty"));
                                String strDealerId=earlierObj.getString("text4");
                                String strUnitId=earlierObj.getJSONObject("itemId").getJSONObject("unit").getString("unitId");
                                String strUnitName=earlierObj.getJSONObject("itemId").getJSONObject("unit").getString("unitName");
                                String strItemObj=earlierObj.getJSONObject("itemId").toString();

                                dealerName.setText(strDealerName);
                                dealerName.setTag(strDealerId);
                                itemName.setText(strItemName);
                                itemName.setTag(strItemObj);          //item obj
                                unit.setText(strUnitName);
                                unit.setTag(strUnitId);                       // unit Id
                                quantity.setText(strQuantity.toPlainString());
                            }
                            jsonArray=SteelHelper.remove(jsonArray.length()-1,jsonArray);
                            ((Controller) getApplication()).dailySalesMainTemplate.getJSONObject("invVoucherObj").put("invVdetailList",jsonArray);
                        }
                    }catch (Exception e){
                    }
                }
            });
            JSONObject dailySalesMainTemplate = ((Controller) getApplication()).dailySalesMainTemplate;
            String errorReason;
            try{
                if(dailySalesMainTemplate==null){
                    errorReason="Error Code- 41901";
                }
                else if(dailySalesMainTemplate.isNull("invVoucherObj")){
                    errorReason="Error Code- 41902";
                }else if(dailySalesMainTemplate.getJSONObject("invVoucherObj").isNull("invVdetailObj")){
                    errorReason="Error Code- 41903";
                }else if(dailySalesMainTemplate.getJSONObject("invVoucherObj").getJSONObject("invVdetailObj").isNull("itemId")){
                    errorReason="Error Code- 41904";
                }else if(dailySalesMainTemplate.getJSONObject("invVoucherObj").getJSONObject("invVdetailObj").getJSONObject("itemId").isNull("unit")){
                    errorReason="Error Code- 41905";
                }else if(dailySalesMainTemplate.getJSONObject("invVoucherObj").getJSONObject("invVdetailObj").getJSONObject("itemId").getJSONObject("unit").isNull("unitName")){
                    errorReason="Error Code- 41906";
                }else if(dailySalesMainTemplate.getJSONObject("invVoucherObj").getJSONObject("invVdetailObj").getJSONObject("itemId").getJSONObject("unit").isNull("unitId")){
                    errorReason="Error Code- 41907";
                }else {
                    errorReason="";
                }
                if(!errorReason.equals("")){
                    new CrashReportSender(this).trackServerIssue("Report "+ errorReason +" using:",errorReason);
                }else {
                    JSONObject mainTemplateJSON=dailySalesMainTemplate.getJSONObject("invVoucherObj").getJSONObject("invVdetailObj").getJSONObject("itemId");
                    String defaultUnitName=mainTemplateJSON.getJSONObject("unit").getString("unitName");
                    String defaultItemName=mainTemplateJSON.getString("itemName");
                    String defaultUnitId=mainTemplateJSON.getJSONObject("unit").getString("unitId");
                    itemName.setText(defaultItemName);
                    itemName.setTag(mainTemplateJSON.toString());
                    unit.setText(defaultUnitName);
                    unit.setTag(defaultUnitId);
                }
            }catch (Exception e){
                //todo know the real msg
                //Log.e(LOG_DBG,e.getLocalizedMessage());
            }
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
            }
        }
        if(requestCode==REQUEST_ITEM_AUTOCOMPLETE){
            if(resultCode==RESULT_OK){
                mReturning="ITEM_AUTOCOMPLETE";
                return_name=data.getExtras().getString("name");
                return_unit_name=data.getExtras().getString("unit_name");
                return_unit_id=data.getExtras().getString("unit_id");
                return_JsonObj=data.getExtras().getString("jsonObj");
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
        }
        else if(mReturning.equals("ITEM_AUTOCOMPLETE")){
            ((TextView)findViewById(R.id.itemName)).setText(return_name);
            findViewById(R.id.itemName).setTag(return_JsonObj);
            ((TextView)findViewById(R.id.unit)).setText(return_unit_name);
            findViewById(R.id.unit).setTag(return_unit_id);
        }
    }

    /*@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.daily_sale_entry, menu);//Menu Resource, Menu
        return true;
    }*/
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

}
