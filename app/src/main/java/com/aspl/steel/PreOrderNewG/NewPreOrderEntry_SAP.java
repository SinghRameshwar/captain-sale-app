package com.aspl.steel.PreOrderNewG;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
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
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.FrameLayout;
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

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.aspl.steel.GlobalConfiguration;
import com.aspl.steel.LoginActivity;
import com.aspl.steel.Pre_OrderG.AddressSpinnerList;
import com.aspl.steel.Pre_OrderG.FinalSavedView;
import com.aspl.steel.Pre_OrderG.FinalSavedViewSAP;
import com.aspl.steel.Pre_OrderG.PreOrderEntryView_SAP;
import com.aspl.steel.R;
import com.aspl.steel.Services.LogoutService;
import com.aspl.steel.SteelHelper;
import com.aspl.steel.adapters.DealerSearchAutoComp;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class NewPreOrderEntry_SAP extends Fragment {

    String itmGroupSAPID = "{\"1002\":[{\"itemId\":\"1300000009\",\"itemName\":\"M.S. Bars (TMT) 5.5MM LOOSE\"},{\"itemId\":\"1300000001\",\"itemName\":\"M.S. Bars (TMT) 8MM LOOSE\"},{\"itemId\":\"1300000002\",\"itemName\":\"M.S. Bars (TMT) 10MM LOOSE\"},{\"itemId\":\"1300000003\",\"itemName\":\"M.S. Bars (TMT) 12MM LOOSE\"},{\"itemId\":\"1300000004\",\"itemName\":\"M.S. Bars (TMT) 16MM LOOSE\"},{\"itemId\":\"1300000005\",\"itemName\":\"M.S. Bars (TMT) 20MM LOOSE\"},{\"itemId\":\"1300000006\",\"itemName\":\"M.S. Bars (TMT) 25MM LOOSE\"},{\"itemId\":\"1300000007\",\"itemName\":\"M.S. Bars (TMT) 28MM LOOSE\"},{\"itemId\":\"1300000008\",\"itemName\":\"M.S. Bars (TMT) 32MM LOOSE\"},{\"itemId\":\"1300000010\",\"itemName\":\"Trading M.S. Bars (TMT) 8MM LOOSE-RG\"},{\"itemId\":\"1300000011\",\"itemName\":\"Trading M.S. Bars (TMT) 10MM LOOSE-RG\"},{\"itemId\":\"1300000012\",\"itemName\":\"Trading M.S. Bars (TMT) 12MM LOOSE-RG\"},{\"itemId\":\"1300000013\",\"itemName\":\"Trading M.S. Bars (TMT) 16MM LOOSE-RG\"},{\"itemId\":\"1300000014\",\"itemName\":\"Trading M.S. Bars (TMT) 20MM LOOSE-RG\"},{\"itemId\":\"1300000015\",\"itemName\":\"Trading M.S. Bars (TMT) 25MM LOOSE-RG\"},{\"itemId\":\"1300000028\",\"itemName\":\"WIRE NAILS LOOSE 2 INCH\"},{\"itemId\":\"1300000029\",\"itemName\":\"WIRE NAILS LOOSE 2.5 INCH\"},{\"itemId\":\"1300000030\",\"itemName\":\"M.S WIRE LOOSE-16GAUGE\"},{\"itemId\":\"1300000031\",\"itemName\":\"M.S WIRE LOOSE-18GAUGE\"},{\"itemId\":\"1300000032\",\"itemName\":\"M.S WIRE LOOSE-20GAUGE\"},{\"itemId\":\"1300000033\",\"itemName\":\"M.S WIRE LOOSE-22GAUGE\"},{\"itemId\":\"1300000034\",\"itemName\":\"M.S WIRE LOOSE-23GAUGE\"},{\"itemId\":\"1300000036\",\"itemName\":\"M.S. Bars (TMT) 5.5MM RING LOOSE\"},{\"itemId\":\"1300000037\",\"itemName\":\"M.S. Bars (TMT) 8MM RING LOOSE\"}],\"1003\":[{\"itemId\":\"1300000009\",\"itemName\":\"M.S. Bars (TMT) 5.5MM LOOSE\"},{\"itemId\":\"1300000001\",\"itemName\":\"M.S. Bars (TMT) 8MM LOOSE\"},{\"itemId\":\"1300000002\",\"itemName\":\"M.S. Bars (TMT) 10MM LOOSE\"},{\"itemId\":\"1300000003\",\"itemName\":\"M.S. Bars (TMT) 12MM LOOSE\"},{\"itemId\":\"1300000004\",\"itemName\":\"M.S. Bars (TMT) 16MM LOOSE\"},{\"itemId\":\"1300000005\",\"itemName\":\"M.S. Bars (TMT) 20MM LOOSE\"},{\"itemId\":\"1300000006\",\"itemName\":\"M.S. Bars (TMT) 25MM LOOSE\"},{\"itemId\":\"1300000007\",\"itemName\":\"M.S. Bars (TMT) 28MM LOOSE\"},{\"itemId\":\"1300000008\",\"itemName\":\"M.S. Bars (TMT) 32MM LOOSE\"},{\"itemId\":\"1300000010\",\"itemName\":\"Trading M.S. Bars (TMT) 8MM LOOSE-RG\"},{\"itemId\":\"1300000011\",\"itemName\":\"Trading M.S. Bars (TMT) 10MM LOOSE-RG\"},{\"itemId\":\"1300000012\",\"itemName\":\"Trading M.S. Bars (TMT) 12MM LOOSE-RG\"},{\"itemId\":\"1300000013\",\"itemName\":\"Trading M.S. Bars (TMT) 16MM LOOSE-RG\"},{\"itemId\":\"1300000014\",\"itemName\":\"Trading M.S. Bars (TMT) 20MM LOOSE-RG\"},{\"itemId\":\"1300000015\",\"itemName\":\"Trading M.S. Bars (TMT) 25MM LOOSE-RG\"},{\"itemId\":\"1300000028\",\"itemName\":\"WIRE NAILS LOOSE 2 INCH\"},{\"itemId\":\"1300000029\",\"itemName\":\"WIRE NAILS LOOSE 2.5 INCH\"},{\"itemId\":\"1300000030\",\"itemName\":\"M.S WIRE LOOSE-16GAUGE\"},{\"itemId\":\"1300000031\",\"itemName\":\"M.S WIRE LOOSE-18GAUGE\"},{\"itemId\":\"1300000032\",\"itemName\":\"M.S WIRE LOOSE-20GAUGE\"},{\"itemId\":\"1300000033\",\"itemName\":\"M.S WIRE LOOSE-22GAUGE\"},{\"itemId\":\"1300000034\",\"itemName\":\"M.S WIRE LOOSE-23GAUGE\"},{\"itemId\":\"1300000036\",\"itemName\":\"M.S. Bars (TMT) 5.5MM RING LOOSE\"},{\"itemId\":\"1300000037\",\"itemName\":\"M.S. Bars (TMT) 8MM RING LOOSE\"}],\"1004\":[{\"itemId\":\"1300000009\",\"itemName\":\"Trading M.S. Bars (TMT) 5.5MM LOOSE\"},{\"itemId\":\"1300000001\",\"itemName\":\"Trading M.S. Bars (TMT) 8MM LOOSE\"},{\"itemId\":\"1300000002\",\"itemName\":\"Trading M.S. Bars (TMT) 10MM LOOSE\"},{\"itemId\":\"1300000003\",\"itemName\":\"Trading M.S. Bars (TMT) 12MM LOOSE\"},{\"itemId\":\"1300000004\",\"itemName\":\"Trading M.S. Bars (TMT) 16MM LOOSE\"},{\"itemId\":\"1300000005\",\"itemName\":\"Trading M.S. Bars (TMT) 20MM LOOSE\"},{\"itemId\":\"1300000006\",\"itemName\":\"Trading M.S. Bars (TMT) 25MM LOOSE\"},{\"itemId\":\"1300000007\",\"itemName\":\"Trading M.S. Bars (TMT) 28MM LOOSE\"},{\"itemId\":\"1300000008\",\"itemName\":\"Trading M.S. Bars (TMT) 32MM LOOSE\"},{\"itemId\":\"1300000010\",\"itemName\":\"Trading M.S. Bars (TMT) 8MM LOOSE-RG\"},{\"itemId\":\"1300000011\",\"itemName\":\"Trading M.S. Bars (TMT) 10MM LOOSE-RG\"},{\"itemId\":\"1300000012\",\"itemName\":\"Trading M.S. Bars (TMT) 12MM LOOSE-RG\"},{\"itemId\":\"1300000013\",\"itemName\":\"Trading M.S. Bars (TMT) 16MM LOOSE-RG\"},{\"itemId\":\"1300000014\",\"itemName\":\"Trading M.S. Bars (TMT) 20MM LOOSE-RG\"},{\"itemId\":\"1300000015\",\"itemName\":\"Trading M.S. Bars (TMT) 25MM LOOSE-RG\"},{\"itemId\":\"1300000028\",\"itemName\":\"WIRE NAILS LOOSE 2 INCH\"},{\"itemId\":\"1300000029\",\"itemName\":\"WIRE NAILS LOOSE 2.5 INCH\"},{\"itemId\":\"1300000030\",\"itemName\":\"M.S WIRE LOOSE-16GAUGE\"},{\"itemId\":\"1300000031\",\"itemName\":\"M.S WIRE LOOSE-18GAUGE\"},{\"itemId\":\"1300000032\",\"itemName\":\"M.S WIRE LOOSE-20GAUGE\"},{\"itemId\":\"1300000033\",\"itemName\":\"M.S WIRE LOOSE-22GAUGE\"},{\"itemId\":\"1300000034\",\"itemName\":\"M.S WIRE LOOSE-23GAUGE\"},{\"itemId\":\"1300000036\",\"itemName\":\"Trading M.S. Bars (TMT) 5.5MM RING LOOSE\"},{\"itemId\":\"1300000037\",\"itemName\":\"Trading M.S. Bars (TMT) 8M RING LOOSE\"}],\"1005\":[{\"itemId\":\"1300000009\",\"itemName\":\"Trading M.S. Bars (TMT) 5.5MM LOOSE\"},{\"itemId\":\"1300000001\",\"itemName\":\"Trading M.S. Bars (TMT) 8MM LOOSE\"},{\"itemId\":\"1300000002\",\"itemName\":\"Trading M.S. Bars (TMT) 10MM LOOSE\"},{\"itemId\":\"1300000003\",\"itemName\":\"Trading M.S. Bars (TMT) 12MM LOOSE\"},{\"itemId\":\"1300000004\",\"itemName\":\"Trading M.S. Bars (TMT) 16MM LOOSE\"},{\"itemId\":\"1300000005\",\"itemName\":\"Trading M.S. Bars (TMT) 20MM LOOSE\"},{\"itemId\":\"1300000006\",\"itemName\":\"Trading M.S. Bars (TMT) 25MM LOOSE\"},{\"itemId\":\"1300000007\",\"itemName\":\"Trading M.S. Bars (TMT) 28MM LOOSE\"},{\"itemId\":\"1300000008\",\"itemName\":\"Trading M.S. Bars (TMT) 32MM LOOSE\"},{\"itemId\":\"1300000010\",\"itemName\":\"Trading M.S. Bars (TMT) 8MM LOOSE-RG\"},{\"itemId\":\"1300000011\",\"itemName\":\"Trading M.S. Bars (TMT) 10MM LOOSE-RG\"},{\"itemId\":\"1300000012\",\"itemName\":\"Trading M.S. Bars (TMT) 12MM LOOSE-RG\"},{\"itemId\":\"1300000013\",\"itemName\":\"Trading M.S. Bars (TMT) 16MM LOOSE-RG\"},{\"itemId\":\"1300000014\",\"itemName\":\"Trading M.S. Bars (TMT) 20MM LOOSE-RG\"},{\"itemId\":\"1300000015\",\"itemName\":\"Trading M.S. Bars (TMT) 25MM LOOSE-RG\"},{\"itemId\":\"1300000028\",\"itemName\":\"WIRE NAILS LOOSE 2 INCH\"},{\"itemId\":\"1300000029\",\"itemName\":\"WIRE NAILS LOOSE 2.5 INCH\"},{\"itemId\":\"1300000030\",\"itemName\":\"M.S WIRE LOOSE-16GAUGE\"},{\"itemId\":\"1300000031\",\"itemName\":\"M.S WIRE LOOSE-18GAUGE\"},{\"itemId\":\"1300000032\",\"itemName\":\"M.S WIRE LOOSE-20GAUGE\"},{\"itemId\":\"1300000033\",\"itemName\":\"M.S WIRE LOOSE-22GAUGE\"},{\"itemId\":\"1300000034\",\"itemName\":\"M.S WIRE LOOSE-23GAUGE\"},{\"itemId\":\"1300000036\",\"itemName\":\"Trading M.S. Bars (TMT) 5.5MM RING LOOSE\"},{\"itemId\":\"1300000037\",\"itemName\":\"Trading M.S. Bars (TMT) 8M RING LOOSE\"}]}";

    String mainObjectStr = "{\"BUKRS\":\"1001\",\"DIVISION\":\"99\",\"CT_VALID_F\":\"18.08.2022\",\"CT_VALID_T\":\"28.08.2022\",\"SHIP_TO_PARTY\":\"0040000001\",\"SOLD_TO_PARTY\":\"0040000001\",\"ZZINCENTIVE\":\"Y\",\"LINE_ITEM\":[]}";
    String ItemObjStr = "{\"ITM_NUMBER\":\"10\",\"MATERIAL\":\"000000001300000012\",\"TARGET_QTY\":\"12\",\"PRICE\":\"39\"}";
    JSONArray headerAr = new JSONArray();

    String[] compListAr = {"Captain Steel India Ltd.", "JMD Alloys limited", "Steel Marketing Pvt. Ltd.","Avator Steel Private Ltd."};
    String[] compListArId = {"1001","2001","3001","4001"};

    String[] itemGrp = {"----Select----", "TMT", "RG"};
    String[] itemGrpId = {"0", "3502", "8749"};
    View rootView;
    Dialog myDialog;
    DrawerLayout mDrawerLayout;
    SimpleDateFormat dateFormatter = new SimpleDateFormat("dd.MM.yyyy");
    String sLid, cid, segid, tempKeyCode, uid, salesman_name;
    final String LOG_DBG = getClass().getSimpleName();
    RequestQueue requestQueue = null;
    Date date = new Date();
    JSONArray itemListAr;
    TextView saveBtn, dateTxt1, dateTxt2, add_more, TotalQty, addresh_dtls,screenTitle,entry_message, code_lid;
    EditText naration_txt;
    Spinner itemGrpSpinner, address_spn, comp_spinner;
    AutoCompleteTextView dealerSearchM;
    // itemListAdapter itemListAdapter1;
    // ListView item_listView;
    LinearLayout itemListLay;
    JSONArray voucherNumber = new JSONArray();
    boolean firstViewCall = true;
    RadioGroup radioGroup;
    dealerSearchApi searchApi;
    FrameLayout entrynotAllow;
    String itemNewGroupId = "1003";


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.new_preorder_entry_sap, container, false);
        myDialog = new Dialog(getContext());
        viewSet(rootView);
        SharedPreferences sharedPref = getActivity().getSharedPreferences("loginCredentials", Context.MODE_PRIVATE);
        cid = sharedPref.getString("cid", "");
        segid = sharedPref.getString("segid", "");
        tempKeyCode = sharedPref.getString("storedKeyCode", "");
        sLid = sharedPref.getString("storedSlid", "");
        uid = sharedPref.getString("storedUserId", "");
        salesman_name = sharedPref.getString("salesman_name", "");
        comp_spinnerView();
        itemGrpSpinner();
        addressSpinner();
        saveBtn = (TextView) rootView.findViewById(R.id.saveData);
        add_more = (TextView) rootView.findViewById(R.id.add_more);
        screenTitle = (TextView) rootView.findViewById(R.id.screenTitle);
        entrynotAllow = (FrameLayout) rootView.findViewById(R.id.entrynotAllow);
        entry_message = (TextView) rootView.findViewById(R.id.entry_message);
        addresh_dtls = (TextView) rootView.findViewById(R.id.addresh_dtls);
        itemListLay = (LinearLayout) rootView.findViewById(R.id.itemListLay);
        radioGroup = (RadioGroup) rootView.findViewById(R.id.radioGroup);
        code_lid = (TextView) rootView.findViewById(R.id.code_lid);

        dealerSearchM = (AutoCompleteTextView) rootView.findViewById(R.id.dealerSearchM);
        dealerSearchM.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                HashMap<String, String> hashMap12 = (HashMap<String, String>) parent.getItemAtPosition(position);
                code_lid.setText("SAP-Code:- "+hashMap12.get("lid"));
                itemNewGroupId = hashMap12.get("plant_code");
                dealerSearchM.setText(hashMap12.get("party_name"));
                dealerSearchM.setTag(hashMap12);
                dealerSearchM.clearFocus();
                itemGrpSpinner.setSelection(0);
                addressListApiSAP searchApi=new addressListApiSAP(getContext(),hashMap12.get("lid"));
                searchApi.execute();
            }
        });
        dateTxt1 = (TextView) rootView.findViewById(R.id.dateTxt1);
        dateTxt2 = (TextView) rootView.findViewById(R.id.dateTxt2);
        TotalQty = (TextView) rootView.findViewById(R.id.TotalQty);
        naration_txt = (EditText) rootView.findViewById(R.id.naration_txt);
        // item_listView = (ListView)rootView.findViewById(R.id.item_listView);
        dateTxt1.setText(dateFormatter.format(date));

        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.DATE, 7);
        dateTxt2.setText(dateFormatter.format(cal.getTime()));

        dateTxt1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar newCalender = Calendar.getInstance();
                DatePickerDialog dialog = new DatePickerDialog(getActivity(), new DatePickerDialog.OnDateSetListener() {
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        Calendar newDate = Calendar.getInstance();
                        newDate.set(year, monthOfYear, dayOfMonth);
                        dateTxt1.setText(dateFormatter.format(newDate.getTime()));
                        newDate.add(Calendar.DATE, 7);
                        dateTxt2.setText(dateFormatter.format(newDate.getTime()));
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

        dealerSearchM.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (editable.length() >= 3) {
                    searchApi=new dealerSearchApi(getContext(),editable.toString());
                    searchApi.execute();
                }

            }
        });


        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mainJsonObj("0");
            }
        });
        add_more.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mainJsonObj("1");
            }
        });
        firstViewCall = false;

        EntryAthourtheyCheck dealerSearchApi=new EntryAthourtheyCheck(getContext());
        dealerSearchApi.execute();

        return rootView;
    }

    void addressSpinner() {
        address_spn = (Spinner) rootView.findViewById(R.id.address_spn);
        address_spn.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> arg0, View view, int arg2, long arg3) {
                try {
                    JSONObject addrObj = new JSONObject(address_spn.getSelectedItem() + "");
                    String adrsStr = addrObj.getString("addressDtls");
                    addresh_dtls.setText(adrsStr);
                } catch (Exception e) {
                    Log.e("Address Set Error: ", e.toString());
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
                // TODO Auto-generated method stub

            }
        });
    }

    void itemGrpSpinner() {
        itemGrpSpinner = (Spinner) rootView.findViewById(R.id.itemGrpSpinner);
        ArrayAdapter aa = new ArrayAdapter(getContext(), android.R.layout.simple_spinner_item, itemGrp);
        aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //Setting the ArrayAdapter data on the Spinner
        itemGrpSpinner.setAdapter(aa);
        itemGrpSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> arg0, View view, int arg2, long arg3) {
                if (dealerSearchM.getTag().equals("0") || dealerSearchM.getText() == null || dealerSearchM.getText().equals("") && !firstViewCall) {
                    itemGrpSpinner.setSelection(0);
                    //Toast.makeText(getContext(),"Please Enter Dealer !",Toast.LENGTH_SHORT).show();
                } else {
                    HashMap<String, String> hashMap01 = (HashMap) dealerSearchM.getTag();
                    String d_lid = hashMap01.get("lid");
                    itemListbyItemGroupIdSAP searchApi=new itemListbyItemGroupIdSAP(getContext(),d_lid,itemGrp[arg2]);
                    searchApi.execute();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
                // TODO Auto-generated method stub

            }
        });
    }

    void comp_spinnerView() {
        comp_spinner = (Spinner) rootView.findViewById(R.id.comp_spinner);
        ArrayAdapter aa = new ArrayAdapter(getContext(), android.R.layout.simple_spinner_item, compListAr);
        aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //Setting the ArrayAdapter data on the Spinner
        comp_spinner.setAdapter(aa);
        comp_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> arg0, View view, int arg2, long arg3) {
                comp_spinner.setTag(compListArId[arg2]);
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
                // TODO Auto-generated method stub

            }
        });
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

    void addnumof_partasionLay(JSONArray jsonArray) {
        itemListLay.removeAllViews();
        try {
            for (int i = 0; i < jsonArray.length(); i++) {
                LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                View view = inflater.inflate(R.layout.item_list_cell, null);
                TextView item_name = (TextView) view.findViewById(R.id.item_name);
                TextView item_amt = (TextView) view.findViewById(R.id.item_amt);
                final EditText qty_field = (EditText) view.findViewById(R.id.qty_field);
                qty_field.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {

                    }

                    @Override
                    public void afterTextChanged(Editable s) {
                        try {
                            JSONObject objData = itemListAr.getJSONObject((Integer) qty_field.getTag());
                            objData.put("qty", qty_field.getText());
                            itemListAr.put((Integer) qty_field.getTag(), objData);
                            TotalQty.setText(totalQtyCalculate() + "");

                        } catch (Exception e) {
                            Log.e("Error Edit Qty: ", e.toString());
                        }
                    }
                });

                JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                item_name.setText(jsonObject1.getString("itemName") + " @" + jsonObject1.getString("itemRate") + "/MT ");
                item_amt.setText(jsonObject1.getString("itemRate"));
                qty_field.setText(jsonObject1.getString("qty"));

                qty_field.setTag(i);
                view.setTag(i);

                itemListLay.addView(view);
            }
        } catch (Exception e) {
            Log.e("Error: ", e.toString());
        }
    }

    double totalQtyCalculate() {
        try {
            double totalQtyD = 0.0;
            for (int i = 0; i < itemListAr.length(); i++) {
                JSONObject object = itemListAr.getJSONObject(i);
                totalQtyD += Double.parseDouble(object.getString("qty"));

            }
            return totalQtyD;
        } catch (Exception e) {
            Log.e("Total Qty Error: ", e.toString());
        }
        return 0;
    }

    ProgressDialog progressview() {
        ProgressDialog progressDialog;
        progressDialog = new ProgressDialog(getContext());
        progressDialog.setIndeterminate(true);
        progressDialog.setCancelable(false);
        progressDialog.setMessage(getContext().getString(R.string.Authenticating));
        progressDialog.show();
        return progressDialog;
    }



    JSONArray itemPrepareM() {
        JSONArray itemAr = new JSONArray();
        try {
            int ITM_NUMBER = 10;
            for (int i = 0; i < itemListAr.length(); i++) {
                JSONObject object1 = new JSONObject(ItemObjStr);
                JSONObject object2 = itemListAr.getJSONObject(i);
                object1.put("PRICE", object2.getString("itemRate"));
                object1.put("MATERIAL", object2.getString("itemId"));
                object1.put("TARGET_QTY", object2.getString("qty"));
                object1.put("ITM_NUMBER", ITM_NUMBER+"");
                object1.put("itemName", object2.getString("itemName"));

                ITM_NUMBER+=10;

                itemAr.put(object1);
            }
            return itemAr;
        } catch (Exception e) {
            Log.e("Item Error: ", e.toString());
        }
        return itemAr;
    }

    void mainJsonObj(String clickType) {
        try {
            JSONObject finalObject = new JSONObject();
            JSONArray jsonHEADERAR = new JSONArray();
            JSONObject objectMain = new JSONObject(mainObjectStr);
            if (Double.parseDouble(TotalQty.getText() + "") <= 0) {
                Toast.makeText(getContext(), "Please Enter Quantity !", Toast.LENGTH_SHORT).show();
                return;
            }
            objectMain.put("CT_VALID_T", dateTxt2.getText());
            objectMain.put("CT_VALID_F", dateTxt1.getText());
            objectMain.put("totalQuantity", TotalQty.getText());
            objectMain.put("USER_ID", salesman_name);

            JSONArray itemAr1 = itemPrepareM();
            if (itemAr1.length() <= 0) {
                Toast.makeText(getContext(), "Please Select Item Group !", Toast.LENGTH_SHORT).show();
                return;
            }
            objectMain.put("LINE_ITEM", itemAr1);
            objectMain.put("Narration", naration_txt.getText());

            if (address_spn.getSelectedItem().equals(null)) {
                Toast.makeText(getContext(), "Please Select Party Address !", Toast.LENGTH_SHORT).show();
                return;
            }
            try {
                JSONObject addrObj = new JSONObject(address_spn.getSelectedItem() + "");
                String adrsStr = addrObj.getString("addressDtls");
                objectMain.put("text1", adrsStr);
                int selectedId = radioGroup.getCheckedRadioButtonId();
                RadioButton radioButton = (RadioButton) rootView.findViewById(selectedId);
                objectMain.put("text2", radioButton.getText());
                objectMain.put("ZZINCENTIVE", radioButton.getTag());
                objectMain.put("SHIP_TO_PARTY", addrObj.getString("add_pm_id"));
                objectMain.put("shippingAddress", addrObj.getString("address"));

                HashMap<String, String> hashMap01 = (HashMap) dealerSearchM.getTag();
                String d_lid = hashMap01.get("lid");
                objectMain.put("ledgerName", hashMap01.get("party_name"));
                objectMain.put("SOLD_TO_PARTY", d_lid);

                jsonHEADERAR.put(objectMain);
                finalObject.put("HEADER",jsonHEADERAR);
            } catch (Exception e) {
                Log.e("Error: ", e.toString());
            }

//            JSONArray mainObjAr = (finalObject.getJSONArray("HEADER"));
//            voucherNumber.put("1234");
//            headerAr.put(mainObjAr.get(0));
//
//            if (clickType.equals("0")) {
//                Fragment fragment = new FinalSavedViewSAP();
//                Bundle bundle = new Bundle();
//                bundle.putString("finalObj", headerAr+"");
//                bundle.putString("v_num", voucherNumber.toString());
//                fragment.setArguments(bundle);
//                SteelHelper.replaceFragment((AppCompatActivity) getContext(), fragment, true);
//            }else{
//                addMoreMethodCall();
//            }

            saveDatabyServlet saveDatabyServlet=new saveDatabyServlet(getContext(),finalObject,clickType);
            saveDatabyServlet.execute();

        } catch (Exception e){Log.e("Error",e.toString());} {
        }
    }


    class addressListApiSAP extends AsyncTask<String, Void, String> {
        private Context context;
        String sold_party_code = "";

        public addressListApiSAP(Context context, String sold_party_code) {
            this.context = context;
            this.sold_party_code = sold_party_code;

        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... arg0) {
            try {
                String dnsport = GlobalConfiguration.getDomainport();
                String link = "http://" + dnsport + "/SteelSales-war/Stl_ShipToParty_Sap_Search";
                HashMap<String, String> hm = new HashMap<>();
                hm.put("keyCode", tempKeyCode);
                hm.put("sold_party_code", sold_party_code);
                hm.put("cid", cid);
                hm.put("segid", segid);

//                Log.e("........",hm.toString());
//                Log.e("........",link);

                return SteelHelper.performPostCall(link, hm);
            } catch (Exception e) {
                return "Exception: " + e.toString();
            }
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            //Log.e("............",s);
            // Dismiss the progress dialog
            try {
                JSONObject main_template = new JSONObject(s);
                String msg = main_template.getString("msg");
                if (main_template.getString("type").equalsIgnoreCase("success")) {
                    JSONArray data = main_template.getJSONArray("data");
                    AddressSpinnerList aa = new AddressSpinnerList(getContext(), data);
                    address_spn.setAdapter(aa);

                } else if (main_template.getString("type").equalsIgnoreCase("error")) {
                    Toast.makeText(getContext(), "Address API: " + msg, Toast.LENGTH_SHORT).show();

                } else if (msg.contains("Authorization Failure..!")) {
                    //Code for logout
                    Toast.makeText(context, "Session expired.", Toast.LENGTH_SHORT).show();
                    Intent logout_intent = new Intent(getActivity(), LogoutService.class);
                    getActivity().startService(logout_intent);
                    getActivity().finish();
                    return;
                } else {
                    Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
                }

            } catch (Exception e) {
                Log.e("Error...: ", e.toString() + "    111");
                //Toast.makeText(context, e.toString(), Toast.LENGTH_SHORT).show();
            }
        }
    }

    void addMoreMethodCall() {
        //dealerSearchM.setEnabled(false);
        dealerSearchM.setFocusable(false);
        itemListLay.removeAllViews();
        itemGrpSpinner.setSelection(0);
        TotalQty.setText("00.00");
        itemListAr = new JSONArray();
        naration_txt.setText("");

    }

    /*......... Curently it's not required .........*/
    class saveDatabyServlet extends AsyncTask<String, Void, String> {
        private Context context;
        JSONObject answerOnj1 = null;
        ProgressDialog progressDialog;
        String clickType;
        HashMap<String, String> hashMap01 = (HashMap) dealerSearchM.getTag();

        public saveDatabyServlet(Context context, JSONObject answerOnj1, String clickType) {
            this.context = context;
            this.answerOnj1 = answerOnj1;
            this.clickType = clickType;

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
                String link = "http://" + dnsport + "/SteelSales-war/Stl_PreOrder_Save";
                HashMap<String, String> hm = new HashMap<>();
                hm.put("data", answerOnj1.toString());
                hm.put("deviceKey", tempKeyCode);
                hm.put("slid", sLid);
                hm.put("rlbLid", "0");

                // Log.e("........",hm.toString());

//                String veryLongString=answerOnj1.toString();
//                int maxLogSize = 1000;
//                for(int i = 0; i <= veryLongString.length() / maxLogSize; i++) {
//                    int start = i * maxLogSize;
//                    int end = (i+1) * maxLogSize;
//                    end = end > veryLongString.length() ? veryLongString.length() : end;
//                    Log.e("..........     ", veryLongString.substring(start, end));
//                }

                return SteelHelper.performPostCall(link, hm);
            } catch (Exception e) {
                return "Exception: " + e.toString();
            }
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            //Log.e("............",s);
            // Dismiss the progress dialog
            if (progressDialog != null && progressDialog.isShowing())
                progressDialog.dismiss();

            try {
                final JSONObject main_template = new JSONObject(s);
                String msg = main_template.getString("msg");
                if (main_template.getString("type").equalsIgnoreCase("success")) {

                    JSONArray HEADER_Ar = answerOnj1.getJSONArray("HEADER");
                    JSONObject data = main_template.getJSONObject("data");
                    headerAr.put(HEADER_Ar.get(0));
                    voucherNumber.put(data.getString("vnum"));

                    if (clickType.equals("0")) {
                        Fragment fragment = new FinalSavedViewSAP();
                        Bundle bundle = new Bundle();
                        bundle.putString("finalObj", headerAr.toString());
                        bundle.putString("v_num", voucherNumber.toString());
                        fragment.setArguments(bundle);
                        SteelHelper.replaceFragment((AppCompatActivity) getContext(), fragment, true);

                    } else {
                        AlertDialog.Builder alertDialog = new AlertDialog.Builder(getContext())//set icon
                                .setIcon(R.mipmap.ic_launcher)//set title
                                .setTitle("Alert")//set message
                                .setMessage(msg)//set positive button
                                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        //set what would happen when positive button is clicked
                                        addMoreMethodCall();
                                        try {
                                            JSONObject dataObj = main_template.getJSONObject("data");
                                            naration_txt.setText("Previous Pre order vnum - " + dataObj.getString("vnum"));
                                        } catch (Exception e) {
                                            Log.e("Error After Saveed", e.toString());
                                        }

                                    }
                                });
                        alertDialog.show();
                    }

                } else if (main_template.getString("type").equalsIgnoreCase("error")) {
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
                } else if (msg.contains("Authorization Failure..!")) {
                    //Code for logout
                    Toast.makeText(context, "Session expired.", Toast.LENGTH_SHORT).show();
                    Intent logout_intent = new Intent(getActivity(), LogoutService.class);
                    getActivity().startService(logout_intent);
                    getActivity().finish();
                    return;
                } else {
                    Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
                }

            } catch (Exception e) {
                Log.e("Error...: ", e.toString() + "    111");
                Toast.makeText(context, e.toString(), Toast.LENGTH_SHORT).show();
            }
        }
    }

    class dealerSearchApi extends AsyncTask<String, Void, String> {
        private Context context;
        String searchStr = "";

        public dealerSearchApi(Context context, String searchStr) {
            this.context = context;
            this.searchStr = searchStr;

        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... arg0) {
            try {
                String dnsport = GlobalConfiguration.getDomainport();
                String link = "http://" + dnsport + "/SteelSales-war/Stl_Dealer_ByDistributor_C_Api";
                HashMap<String, String> hm = new HashMap<>();
                hm.put("q", searchStr);
                hm.put("keyCode", tempKeyCode);
                hm.put("distributorId", sLid);
                hm.put("cid", cid);

                //Log.e("........",hm.toString());
                //Log.e("........",link);

                return SteelHelper.performPostCall(link, hm);
            } catch (Exception e) {
                return "Exception: " + e.toString();
            }
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            //Log.e("............",s);

            //[{"lid":0,"party_id":0,"party_name":"All","party_type_name":""}]
            // Dismiss the progress dialog
            try {
                final JSONObject main_template = new JSONObject(s);
                String msg = main_template.getString("msg");
                if (main_template.getString("type").equalsIgnoreCase("success")) {
                    JSONArray data = main_template.getJSONArray("data");
                    ArrayList<HashMap<String, String>> searchArrayList = new ArrayList<>();
                    for (int i = 0; i < data.length(); i++) {
                        JSONObject object12 = data.getJSONObject(i);
                        HashMap<String, String> hashMap = new HashMap<>();
                        hashMap.put("lid", object12.getString("lid"));
                        hashMap.put("party_id", object12.getString("party_id"));
                        hashMap.put("party_name", object12.getString("party_name"));
                        hashMap.put("party_type_name", object12.getString("party_type_name"));
                        hashMap.put("plant_code", object12.getString("plant_code"));
                        searchArrayList.add(hashMap);
                    }
                    DealerSearchAutoComp adapter = new DealerSearchAutoComp(getContext(), android.R.layout.simple_dropdown_item_1line, searchArrayList);
                    dealerSearchM.setAdapter(adapter);
                    adapter.notifyDataSetChanged();

                } else if (main_template.getString("type").equalsIgnoreCase("error")) {
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
                } else if (msg.contains("Authorization Failure..!")) {
                    //Code for logout
                    Toast.makeText(context, "Session expired.", Toast.LENGTH_SHORT).show();
                    Intent logout_intent = new Intent(getActivity(), LogoutService.class);
                    getActivity().startService(logout_intent);
                    getActivity().finish();
                    return;
                } else {
                    Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
                }

            } catch (Exception e) {
                Log.e("Error...: ", e.toString() + "    111");
                Toast.makeText(context, e.toString(), Toast.LENGTH_SHORT).show();
            }
        }
    }

    class EntryAthourtheyCheck extends AsyncTask<String, Void, String> {
        private Context context;
        String searchStr = "";

        public EntryAthourtheyCheck(Context context) {
            this.context = context;
            this.searchStr = searchStr;

        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... arg0) {
            try {
                String dnsport = GlobalConfiguration.getDomainport();
                String link = "http://" + dnsport + "/SteelSales-war/Stl_Order_Booking_Status_Api";
                HashMap<String, String> hm = new HashMap<>();
                // hm.put("tempKeyCode", tempKeyCode);
                // hm.put("distributorId", sLid);
                hm.put("cid", cid);
                hm.put("segid", segid);
                hm.put("devicekey", cid);

                //Log.e("........",hm.toString());
                //Log.e("........",link);

                return SteelHelper.performPostCall(link, hm);
            } catch (Exception e) {
                return "Exception: " + e.toString();
            }
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            // Log.e("............",s);

            //[{"lid":0,"party_id":0,"party_name":"All","party_type_name":""}]
            // Dismiss the progress dialog
            try {
                final JSONObject main_template = new JSONObject(s);
                String msg = main_template.getString("msg");
                if (main_template.getString("type").equalsIgnoreCase("success")) {
                    entrynotAllow.setVisibility(View.GONE);

                } else if (main_template.getString("type").equalsIgnoreCase("error")) {
                    entrynotAllow.setVisibility(View.VISIBLE);
                    entry_message.setText(main_template.getString("msg"));

                } else if (msg.contains("Authorization Failure..!")) {
                    //Code for logout
                    Toast.makeText(context, "Session expired.", Toast.LENGTH_SHORT).show();
                    Intent logout_intent = new Intent(getActivity(), LogoutService.class);
                    getActivity().startService(logout_intent);
                    getActivity().finish();
                    return;
                } else {
                    Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
                }

            } catch (Exception e) {
                Log.e("Error...: ", e.toString() + "    111");
                Toast.makeText(context, e.toString(), Toast.LENGTH_SHORT).show();
            }
        }
    }

    class itemListbyItemGroupIdSAP_OldAPI extends AsyncTask<String, Void, String> {
        private Context context;
        String sold_party_code = "";
        String item_grp;

        public itemListbyItemGroupIdSAP_OldAPI(Context context, String sold_party_code, String item_grp) {
            this.context = context;
            this.sold_party_code = sold_party_code;
            this.item_grp = item_grp;

        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... arg0) {
            try {
                String dnsport = GlobalConfiguration.getDomainport();
                String link = "http://" + dnsport + "/SteelSales-war/Stl_Material_Fetch_PriceList";
                HashMap<String, String> hm = new HashMap<>();
                hm.put("keyCode", tempKeyCode);
                hm.put("sold_party_code", sold_party_code);
                hm.put("company_code", comp_spinner.getTag()+"");
                hm.put("item_grp", item_grp);
                hm.put("cid", cid);
                hm.put("segid", segid);

//                Log.e("........",hm.toString());
//                Log.e("........",link);

                return SteelHelper.performPostCall(link, hm);
            } catch (Exception e) {
                return "Exception: " + e.toString();
            }
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            //Log.e("............",s);
            // Dismiss the progress dialog
            try {
                JSONObject main_template = new JSONObject(s);
                String msg = main_template.getString("msg");
                if (main_template.getString("type").equalsIgnoreCase("success")) {
                    itemListAr = new JSONArray();
                    JSONArray arrarData = main_template.getJSONArray("dataArray");
                    for (int i = 0; i < arrarData.length(); i++) {
                        JSONObject object1 = arrarData.getJSONObject(i);
                        object1.put("qty", "0");
                        itemListAr.put(object1);
                    }
                    addnumof_partasionLay(itemListAr);
//                            itemListAdapter1= new itemListAdapter(getContext(),itemListAr);
//                            item_listView.setAdapter(itemListAdapter1);
//                            itemListAdapter1.notifyDataSetChanged();

                } else if (main_template.getString("type").equalsIgnoreCase("error")) {
                    Toast.makeText(getContext(), "Address API: " + msg, Toast.LENGTH_SHORT).show();

                } else if (msg.contains("Authorization Failure..!")) {
                    //Code for logout
                    Toast.makeText(context, "Session expired.", Toast.LENGTH_SHORT).show();
                    Intent logout_intent = new Intent(getActivity(), LogoutService.class);
                    getActivity().startService(logout_intent);
                    getActivity().finish();
                    return;
                } else {
                    Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
                }

            } catch (Exception e) {
                Log.e("Error...: ", e.toString() + "    111");
                //Toast.makeText(context, e.toString(), Toast.LENGTH_SHORT).show();
            }
        }
    }

    class itemListbyItemGroupIdSAP extends AsyncTask<String, Void, String> {
        final ProgressDialog progressDialog = progressview();
        private Context context;
        String sold_party_code = "";
        String item_grp;

        public itemListbyItemGroupIdSAP(Context context, String sold_party_code, String item_grp) {
            this.context = context;
            this.sold_party_code = sold_party_code;
            this.item_grp = item_grp;
        }
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... arg0) {
            try {
                String dnsport = GlobalConfiguration.getDomainport();
                String link = "http://" + dnsport + "/SteelSales-war/Stl_Material_Fetchhm_PriceList";
                HashMap<String, String> hm = new HashMap<>();
                hm.put("keyCode", tempKeyCode);
                hm.put("sold_party_code", sold_party_code);
                hm.put("company_code", comp_spinner.getTag()+"");
                hm.put("item_grp", item_grp);
                hm.put("cid", cid);
                hm.put("segid", segid);

//                Log.e("........",hm.toString());
//                Log.e("........",link);

                return SteelHelper.performPostCall(link, hm);
            } catch (Exception e) {
                return "Exception: " + e.toString();
            }
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            //Log.e("............",s);
            // Dismiss the progress dialog
            progressDialog.dismiss();
            try {
                JSONObject main_template = new JSONObject(s);
                String msg = main_template.getString("msg");
                if (main_template.getString("type").equalsIgnoreCase("success")) {
                    // Logic Change For SAP Item Group 17-09-2022
                    JSONObject dataObj = main_template.getJSONObject("dataObj");
                    itemMapingTolocalItemnew(dataObj);

                } else if (main_template.getString("type").equalsIgnoreCase("error")) {
                    Toast.makeText(getContext(), "Address API: " + msg, Toast.LENGTH_SHORT).show();

                } else if (msg.contains("Authorization Failure..!")) {
                    //Code for logout
                    Toast.makeText(context, "Session expired.", Toast.LENGTH_SHORT).show();
                    Intent logout_intent = new Intent(getActivity(), LogoutService.class);
                    getActivity().startService(logout_intent);
                    getActivity().finish();
                    return;
                } else {
                    Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
                }

            } catch (Exception e) {
                Log.e("Error...: ", e.toString() + "    111");
                //Toast.makeText(context, e.toString(), Toast.LENGTH_SHORT).show();
            }
        }
    }

    void itemMapingTolocalItemnew(JSONObject m_object){
        try {
            itemListAr = new JSONArray();
            JSONObject obj1 = new JSONObject(itmGroupSAPID);
            JSONArray staticItmList = obj1.getJSONArray(itemNewGroupId);
            for (int i = 0; i < staticItmList.length(); i++) {
                JSONObject object1 = staticItmList.getJSONObject(i);
                String itemId1 = "00000000"+object1.getString("itemId");
                try {
                    JSONObject object2 = m_object.getJSONObject(itemId1);
                    object2.put("qty", "0");
                    object2.put("itemName", object1.getString("itemName"));
                    object2.put("itemId", itemId1);
                    itemListAr.put(object2);
                }catch (Exception e){Log.e("Error: ",e.toString());}

            }
            addnumof_partasionLay(itemListAr);
        }catch (Exception e){Log.e("Error item New Logic:",e.toString());}
    }



}