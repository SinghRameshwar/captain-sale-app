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
import com.aspl.steel.DealerNameAutocompleteActivity;
import com.aspl.steel.EngineerVisitG.EngineerVisitRtp;
import com.aspl.steel.GlobalConfiguration;
import com.aspl.steel.LoginActivity;
import com.aspl.steel.Pre_OrderG.AddressSpinnerList;
import com.aspl.steel.Pre_OrderG.FinalSavedView;
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

public class NewPreOrderEntry extends Fragment {

    String mainObjectStr = "{\"txnDt\":null,\"itemDetails\":[],\"txnDescription\":null,\"freightType\":null,\"ledgerDetails\":[],\"shippingPartyName\":null,\"txnTypeName\":null,\"totalQuantity\":null,\"paymentDays\":null,\"poDate\":null,\"shippingAddress\":null,\"apiReference\":null,\"txnValidityDate\":null,\"txnNum\":null,\"brokerName\":null,\"poNumber\":null,\"paymentTerm\":null,\"txnId\":0}";
    String ItemObjStr = "{\"itemName\":null,\"amount\":null,\"quantity\":null,\"rate\":null,\"itemDescription\":null}";
    String ledgerObjStr = "{\"amountType\":null,\"amount\":null,\"ledgerName\":null,\"ledgerType\":null}";

    String[] itemGrp = {"----Select----", "TMT", "TMT Rust Guard"};
    String[] itemGrpId = {"0", "3502", "8749"};
    View rootView;
    Dialog myDialog;
    DrawerLayout mDrawerLayout;
    SimpleDateFormat dateFormatter = new SimpleDateFormat("dd-MM-yyyy");
    String sLid, cid, segid, tempKeyCode, uid, salesman_name;
    final String LOG_DBG = getClass().getSimpleName();
    RequestQueue requestQueue = null;
    Date date = new Date();
    JSONArray itemListAr;
    TextView saveBtn, dateTxt1, dateTxt2, add_more, TotalQty, addresh_dtls,screenTitle,entry_message;
    EditText naration_txt;
    Spinner itemGrpSpinner, address_spn;
    AutoCompleteTextView dealerSearchM;
    // itemListAdapter itemListAdapter1;
    // ListView item_listView;
    LinearLayout itemListLay;
    JSONArray savedObjAr = new JSONArray();
    JSONArray voucherNumber = new JSONArray();
    boolean firstViewCall = true;
    String asscesskey = "M400LABCZXASW2FGLMBVCAX310OK"; // local  UG67906TTRM845OPYT   //  Live  M400LABCZXASW2FGLMBVCAX310OK
    String emalId = "mobile.apps@captainsteel.com"; // local manoj.chouhan@bmaind.com  //live  mobile.apps@captainsteel.com
    int isDealer = 0; //local 1  // Live 0
    RadioGroup radioGroup;
    dealerSearchApi searchApi;
    FrameLayout entrynotAllow;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.new_preorder_entry_view, container, false);
        myDialog = new Dialog(getContext());
        viewSet(rootView);
        SharedPreferences sharedPref = getActivity().getSharedPreferences("loginCredentials", Context.MODE_PRIVATE);
        cid = sharedPref.getString("cid", "");
        segid = sharedPref.getString("segid", "");
        tempKeyCode = sharedPref.getString("storedKeyCode", "");
        sLid = sharedPref.getString("storedSlid", "");
        uid = sharedPref.getString("storedUserId", "");
        salesman_name = sharedPref.getString("salesman_name", "");
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

        dealerSearchM = (AutoCompleteTextView) rootView.findViewById(R.id.dealerSearchM);
        dealerSearchM.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                HashMap<String, String> hashMap12 = (HashMap<String, String>) parent.getItemAtPosition(position);
                if (hashMap12.get("lid").equals("0")) {
                    itemGrpSpinner.setSelection(0);
                    dealerSearchM.getText().clear();
                    dealerSearchM.setTag("0");

                    AlertDialog.Builder alertDialog = new AlertDialog.Builder(getContext())//set icon
                            .setIcon(R.mipmap.ic_launcher)//set title
                            .setTitle("Alert")//set message
                            .setMessage("Ledger Not Registered in Realbooks!")//set positive button
                            .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    //set what would happen when positive button is clicked

                                }
                            });
                    alertDialog.show();

                } else {
                    dealerSearchM.setText(hashMap12.get("party_name"));
                    dealerSearchM.setTag(hashMap12);
                    dealerSearchM.clearFocus();
                    itemGrpSpinner.setSelection(0);
                    addressListApi(hashMap12.get("party_name"));
                }
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
                    itemListbyItemGroupId(d_lid, itemGrpId[arg2]);
                }
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

    void itemListbyItemGroupId(String partyId, String itemGid) {
        final ProgressDialog progressDialog = progressview();
        //String link = "http://ec2-3-7-170-219.ap-south-1.compute.amazonaws.com:85/AMSSERVICE/api/custom/fetch/itemmastergroup";// Local

        String link = "https://app.realbooks.in/AMSSERVICE/api/custom/fetch/itemmastergroup"; // live
        try {
            if (requestQueue == null) {
                requestQueue = Volley.newRequestQueue(getContext());
            }

            JSONObject jsonEntityData = new JSONObject();
            jsonEntityData.put("emailid", emalId);
            jsonEntityData.put("secretKey", asscesskey);
            jsonEntityData.put("deviceId", asscesskey);
            jsonEntityData.put("deviceOs", "Android");
            jsonEntityData.put("cid", 1089);
            jsonEntityData.put("segid", 1089);
            jsonEntityData.put("partyId", Integer.parseInt(partyId));//162686
            jsonEntityData.put("itemGroupId", Integer.parseInt(itemGid));//3502
            jsonEntityData.put("isDealer", isDealer);
            jsonEntityData.put("dtStr", dateFormatter.format(date));
            jsonEntityData.put("accessKey", asscesskey);
            //Log.e(".......",link);
            //Log.e("......111.....",jsonEntityData.toString());

            JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, link, jsonEntityData, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    try {
                        if (progressDialog != null && progressDialog.isShowing())
                            progressDialog.dismiss();

                        //Log.e("***********",response.toString());
                        itemListLay.removeAllViews();
                        if (response.getString("type").equalsIgnoreCase("success")) {
                            itemListAr = new JSONArray();
                            JSONArray arrarData = response.getJSONArray("dataArray");
                            for (int i = 0; i < arrarData.length(); i++) {
                                JSONObject object1 = arrarData.getJSONObject(i);
                                object1.put("qty", "0");
                                itemListAr.put(object1);
                            }
                            addnumof_partasionLay(itemListAr);
//                            itemListAdapter1= new itemListAdapter(getContext(),itemListAr);
//                            item_listView.setAdapter(itemListAdapter1);
//                            itemListAdapter1.notifyDataSetChanged();

                        } else if (response.getString("type").equalsIgnoreCase("autherror")) {

                            String message = response.getString("msg");
                            AlertDialog.Builder alertDialog = new AlertDialog.Builder(getContext())//set icon
                                    .setIcon(R.mipmap.ic_launcher)//set title
                                    .setTitle("Alert")//set message
                                    .setMessage(message)//set positive button
                                    .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            //set what would happen when positive button is clicked

                                        }
                                    });
                            alertDialog.show();

//                            String message=response.getString("msg");
//                            Toast.makeText(getContext(),message,Toast.LENGTH_SHORT).show();
//                            Intent intent = new Intent(getActivity(), LoginActivity.class);
//                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                            intent.putExtra("EXIT", true);
//                            startActivity(intent);
//                            getActivity().finish();
                        } else {
                            String message = response.getString("msg");
                            AlertDialog.Builder alertDialog = new AlertDialog.Builder(getContext())//set icon
                                    .setIcon(R.mipmap.ic_launcher)//set title
                                    .setTitle("Alert")//set message
                                    .setMessage(message)//set positive button
                                    .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            //set what would happen when positive button is clicked

                                        }
                                    });
                            alertDialog.show();
                        }
                    } catch (Exception e) {
                        Log.e("....", "heloooooooo" + e.getLocalizedMessage());
                        String message = "That's an error Exception";
                        Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();

                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.e(".....", "helloooo...." + error);
                    Toast.makeText(getContext(), "Unable to connect to server", Toast.LENGTH_SHORT).show();
                }
            }) {
                @Override
                public Map<String, String> getHeaders() {
                    Map<String, String> params = new HashMap<String, String>();
                    params.put("accountName", "bma");
                    return params;
                }
            };
            request.setRetryPolicy(new DefaultRetryPolicy(300000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            requestQueue.add(request);
        } catch (Exception ignored) {
            Log.e(".....", "helloooo...." + ignored + "");
            Toast.makeText(getContext(), "Unable to connect to server", Toast.LENGTH_SHORT).show();
        }
    }

    JSONArray itemPrepareM() {
        JSONArray itemAr = new JSONArray();
        try {
            for (int i = 0; i < itemListAr.length(); i++) {
                JSONObject object1 = new JSONObject(ItemObjStr);
                JSONObject object2 = itemListAr.getJSONObject(i);
                object1.put("rate", object2.getString("itemRate"));
                object1.put("itemName", object2.getString("itemName"));
                object1.put("quantity", object2.getString("qty"));

                itemAr.put(object1);
            }
            return itemAr;
        } catch (Exception e) {
            Log.e("Item Error: ", e.toString());
        }
        return itemAr;
    }

    JSONArray ledgerDtlsP() {
        JSONArray ledgerAr = new JSONArray();
        try {
            JSONObject jsonObject = new JSONObject(ledgerObjStr);
            HashMap<String, String> hashMap01 = (HashMap) dealerSearchM.getTag();
            String party_name = hashMap01.get("party_name");
            jsonObject.put("ledgerName", party_name);
            jsonObject.put("amountType", "DR");
            jsonObject.put("ledgerType", "PARTY");
            ledgerAr.put(jsonObject);

        } catch (Exception e) {
        }
        return ledgerAr;
    }

    void mainJsonObj(String clickType) {
        try {
            JSONObject objectMain = new JSONObject(mainObjectStr);
            objectMain.put("txnDt", dateFormatter.format(date));
            objectMain.put("salesMan", salesman_name);
            if (Double.parseDouble(TotalQty.getText() + "") <= 0) {
                Toast.makeText(getContext(), "Please Enter Quantity !", Toast.LENGTH_SHORT).show();
                return;
            }
            objectMain.put("totalQuantity", TotalQty.getText());
            objectMain.put("txnValidityDate", dateTxt2.getText());
            objectMain.put("dispatchDate", dateTxt1.getText());


            objectMain.put("ledgerDetails", ledgerDtlsP());
            JSONArray itemAr1 = itemPrepareM();
            if (itemAr1.length() <= 0) {
                Toast.makeText(getContext(), "Please Select Item Group !", Toast.LENGTH_SHORT).show();
                return;
            }
            objectMain.put("itemDetails", itemAr1);
            objectMain.put("txnDescription", naration_txt.getText());

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
                objectMain.put("billIncentive", radioButton.getTag());

                if (!addrObj.getString("address").equalsIgnoreCase("Primay Address")) {
                    HashMap<String, String> hashMap01 = (HashMap) dealerSearchM.getTag();
                    String party_name = hashMap01.get("party_name");
                    objectMain.put("shippingPartyName", party_name);
                    objectMain.put("shippingAddress", addrObj.getString("address"));
                }
            } catch (Exception e) {
                Log.e("Error: ", e.toString());
            }

            JSONObject dataObj = new JSONObject();
            dataObj.put("cid", "1089");
            dataObj.put("branchId", "1089");
            dataObj.put("data", objectMain);
            dataObj.put("accessKey", asscesskey);
            dataObj.put("secretKey", asscesskey);

            saveDatabyServlet saveDatabyServlet=new saveDatabyServlet(getContext(),dataObj,clickType);
            saveDatabyServlet.execute();


        } catch (Exception e) {
        }
    }

    void addressListApi(String srchStr) {
        final ProgressDialog progressDialog = progressview();
        //String link = "http://ec2-3-7-170-219.ap-south-1.compute.amazonaws.com:9002/custom/preOrderParty/shiAddByParty";  //Local

        String link = "https://app.realbooks.in/custom/preOrderParty/shiAddByParty";  //Live

        try {
            if (requestQueue == null) {
                requestQueue = Volley.newRequestQueue(getContext());
            }

            JSONObject jsonEntityData = new JSONObject();
            jsonEntityData.put("secretKey", asscesskey);
            jsonEntityData.put("accessKey", asscesskey);
            jsonEntityData.put("cid", 1089);
            jsonEntityData.put("branchId", 1089);
            jsonEntityData.put("type", "invoice_ship_party");
            jsonEntityData.put("partyName", srchStr);
            // Log.e(".......",link);
            // Log.e("......111.....",jsonEntityData.toString());

            JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, link, jsonEntityData, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    try {
                        if (progressDialog != null && progressDialog.isShowing())
                            progressDialog.dismiss();

                        //Log.e("Response==> ",response.toString());
                        if (response.getString("type").equalsIgnoreCase("success")) {
                            JSONArray data = response.getJSONArray("data");
                            AddressSpinnerList aa = new AddressSpinnerList(getContext(), data);
                            address_spn.setAdapter(aa);

                        } else if (response.getString("type").equalsIgnoreCase("error")) {
                            String message = response.getString("msg");
                            Toast.makeText(getContext(), "Address API: " + message, Toast.LENGTH_SHORT).show();

                        } else if (response.getString("type").equalsIgnoreCase("autherror")) {

                            Toast.makeText(getContext(), "Session expired.", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(getActivity(), LoginActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            intent.putExtra("EXIT", true);
                            startActivity(intent);
                            getActivity().finish();

                        } else {
                            String message = response.getString("msg");
                            //RealBooksHelper.ShowPopup(myDialog,"Error",message,"e");
                            Toast.makeText(getContext(), "Address API: 1" + message, Toast.LENGTH_SHORT).show();
                        }
                    } catch (Exception e) {
                        Log.e("....", "heloooooooo" + e.getLocalizedMessage());
                        String message = "That's an error Exception";
                        Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();

                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.e(".....", "helloooo12...." + error);
                    Toast.makeText(getContext(), "Unable to connect to server", Toast.LENGTH_SHORT).show();
                }
            }) {
                @Override
                public Map<String, String> getHeaders() {
                    Map<String, String> params = new HashMap<String, String>();
                    params.put("accountName", "bma");
                    return params;
                }
            };
            request.setRetryPolicy(new DefaultRetryPolicy(300000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            requestQueue.add(request);
        } catch (Exception ignored) {
            Log.e(".....", "helloooo11...." + ignored + "");
            Toast.makeText(getContext(), "Unable to connect to server", Toast.LENGTH_SHORT).show();
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
                hm.put("rlbLid", hashMap01.get("lid"));

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

                    String message = main_template.getString("msg");
                    JSONObject data = main_template.getJSONObject("data");
                    savedObjAr.put(answerOnj1.getJSONObject("data"));
                    voucherNumber.put(data.getString("vnum"));

                    if (clickType.equals("0")) {
                        Fragment fragment = new FinalSavedView();
                        Bundle bundle = new Bundle();
                        bundle.putString("finalObj", savedObjAr.toString());
                        bundle.putString("v_num", voucherNumber.toString());
                        fragment.setArguments(bundle);
                        SteelHelper.replaceFragment((AppCompatActivity) getContext(), fragment, true);

                    } else {
                        AlertDialog.Builder alertDialog = new AlertDialog.Builder(getContext())//set icon
                                .setIcon(R.mipmap.ic_launcher)//set title
                                .setTitle("Alert")//set message
                                .setMessage(message)//set positive button
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
}


