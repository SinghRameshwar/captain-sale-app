package com.aspl.steel.Pre_OrderG;

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
import android.widget.AutoCompleteTextView;
import android.widget.DatePicker;
import android.widget.ListView;
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
import com.aspl.steel.MyLeadG.MyLeadAdapter;
import com.aspl.steel.MyLeadG.MyLeadDetails;
import com.aspl.steel.MyLeadG.MyLeadList;
import com.aspl.steel.R;
import com.aspl.steel.SteelHelper;
import com.aspl.steel.adapters.DealerSearchAutoComp;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class PreOrderListView extends Fragment {

    View rootView;
    DrawerLayout mDrawerLayout;
    SimpleDateFormat dateFormatter=new SimpleDateFormat("dd-MM-yyyy");
    SimpleDateFormat dateFormatter1=new SimpleDateFormat("01-MM-yyyy");
    String  sLid,cid,segid,tempKeyCode,uid;
    final String LOG_DBG=getClass().getSimpleName();
    ListView listView;
    Date date=new Date();
    PreOrderListViewAdapter myLeadAdapter;
    TextView right_title,right_title2;
    RequestQueue requestQueue = null;
    String asscesskey = "M400LABCZXASW2FGLMBVCAX310OK";
    AutoCompleteTextView dealerSearchM;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.pre_order_list_view, container, false);
        viewSet(rootView);
        SharedPreferences sharedPref = getActivity().getSharedPreferences("loginCredentials", Context.MODE_PRIVATE);
        cid = sharedPref.getString("cid", "");
        segid = sharedPref.getString("segid", "");
        tempKeyCode = sharedPref.getString("storedKeyCode", "");
        sLid = sharedPref.getString("storedSlid", "");
        uid = sharedPref.getString("storedUserId", "");
        listView=(ListView)rootView.findViewById(R.id.p_listView);
        dealerSearchM = (AutoCompleteTextView) rootView.findViewById(R.id.dealerSearchM);
        dealerSearchM.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                HashMap<String,String> hashMap12= (HashMap<String, String>) parent.getItemAtPosition(position);
                if(hashMap12.get("lid").equals("0")){
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

                }else {
                    dealerSearchM.setText(hashMap12.get("party_name"));
                    dealerSearchM.setTag(hashMap12.get("lid"));
                    dealerSearchM.clearFocus();
                    PreOrderListService(hashMap12.get("lid"));
                }
            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                try{
                    JSONObject object = (JSONObject) listView.getAdapter().getItem(position);
                    if (object.getString("lead_status").equalsIgnoreCase("Open")) {
                        Intent intent = new Intent(getContext(), MyLeadDetails.class);
                        intent.putExtra("lead_id", object.getString("id"));
                        getActivity().startActivityForResult(intent, 2);
                    }else{
                        Toast.makeText(getContext(),"Sorry! You can check details whom status is Open",Toast.LENGTH_LONG).show();
                    }

                }catch (Exception e){
                    Log.e("List Selection Error: ",e.toString());}

            }
        });
        right_title=(TextView)rootView.findViewById(R.id.right_title);
        right_title2=(TextView)rootView.findViewById(R.id.right_title2);
        right_title.setText(dateFormatter.format(date));
        right_title2.setText(dateFormatter.format(date));

        right_title.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar newCalender = Calendar.getInstance();
                DatePickerDialog dialog = new DatePickerDialog(getActivity(), new DatePickerDialog.OnDateSetListener() {

                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        Calendar newDate = Calendar.getInstance();
                        newDate.set(year, monthOfYear, dayOfMonth);
                        right_title.setText(dateFormatter.format(newDate.getTime()));
                        PreOrderListService(dealerSearchM.getTag().toString());
                    }
                }, newCalender.get(Calendar.YEAR), newCalender.get(Calendar.MONTH), newCalender.get(Calendar.DAY_OF_MONTH));
                dialog.setTitle("Select Date:");
                Calendar cal = Calendar.getInstance();
                cal.setTime(new Date());
                cal.add(Calendar.MONTH, -1);
                dialog.show();
            }
        });
        right_title2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar newCalender = Calendar.getInstance();
                DatePickerDialog dialog = new DatePickerDialog(getActivity(), new DatePickerDialog.OnDateSetListener() {

                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        Calendar newDate = Calendar.getInstance();
                        newDate.set(year, monthOfYear, dayOfMonth);
                        right_title2.setText(dateFormatter.format(newDate.getTime()));
                        PreOrderListService(dealerSearchM.getTag().toString());
                    }

                }, newCalender.get(Calendar.YEAR), newCalender.get(Calendar.MONTH), newCalender.get(Calendar.DAY_OF_MONTH));
                dialog.setTitle("Select Date:");
                Calendar cal = Calendar.getInstance();
                cal.setTime(new Date());
                cal.add(Calendar.MONTH, -1);
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
                if (editable.length() >= 5) {
                    FetchDealerName(editable.toString());
                }

            }
        });

        PreOrderListService(dealerSearchM.getTag().toString());

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

    ProgressDialog progressview() {
        ProgressDialog progressDialog;
        progressDialog = new ProgressDialog(getContext());
        progressDialog.setIndeterminate(true);
        progressDialog.setCancelable(false);
        progressDialog.setMessage(getContext().getString(R.string.Authenticating));
        progressDialog.show();
        return progressDialog;
    }

    /*.......... DealerTargetAPI Search..........*/
    void PreOrderListService(String lid){

        final ProgressDialog progressDialog = progressview();
        RequestQueue requestQueue = null;
       // String link = "http://ec2-3-7-170-219.ap-south-1.compute.amazonaws.com:9002/custom/preOrderParty/preOrderlist";  //Live
        String link = "https://app.realbooks.in/custom/preOrderParty/preOrderlist";

        try {
            if(requestQueue==null) {
                requestQueue = Volley.newRequestQueue(getContext());
            }

            JSONObject jsonEntityData= new JSONObject();
            jsonEntityData.put("cid", 1089);
            jsonEntityData.put("segid", 1089);
            jsonEntityData.put("lid", lid);
            jsonEntityData.put("status", "-1");
            jsonEntityData.put("type", "l");
            jsonEntityData.put("fromDt", right_title.getText());
            jsonEntityData.put("toDt", right_title2.getText());

//            Log.e(".......",link);
//            Log.e("......111.....",jsonEntityData.toString());

            JsonObjectRequest request=new JsonObjectRequest(Request.Method.POST, link, jsonEntityData, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    try{
                      //  Log.e("Response==> ",response.toString());
                        if (progressDialog != null && progressDialog.isShowing())
                            progressDialog.dismiss();

                        if(response.getString("type").equalsIgnoreCase("success")){
                            JSONArray jsonArray=response.getJSONArray("data");
                            myLeadAdapter=new PreOrderListViewAdapter(getContext(),jsonArray);
                            listView.setAdapter(myLeadAdapter);
                            myLeadAdapter.notifyDataSetChanged();

                        }else if (response.getString("type").equalsIgnoreCase("error")){
                            JSONArray jsonArray=new JSONArray();
                            myLeadAdapter=new PreOrderListViewAdapter(getContext(),jsonArray);
                            listView.setAdapter(myLeadAdapter);
                            myLeadAdapter.notifyDataSetChanged();

                            String message=response.getString("msg");
                            Toast.makeText(getContext(),message,Toast.LENGTH_SHORT).show();
                        }else{
                            String message=response.getString("msg");
                            Toast.makeText(getContext(),message,Toast.LENGTH_SHORT).show();
                        }
                    }catch (Exception e){
                        Log.e("....","heloooooooo"+e.getLocalizedMessage());
                        String message= "That's an Error Exception";
                        try {
                            message = response.getString("msg");
                        } catch (JSONException jsonException) {
                            jsonException.printStackTrace();
                        }
                        // String message="That's an error Exception";
                        Toast.makeText(getContext(),message,Toast.LENGTH_SHORT).show();

                    }
                }
            },new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.e(".....","helloooo12...."+error);
                    Toast.makeText(getContext(),"Unable to connect to server",Toast.LENGTH_SHORT).show();
                }
            }){
                @Override
                public Map<String, String> getHeaders() {
                    Map<String, String> params = new HashMap<String, String>();
                    params.put("Authorization", "Bearer eyJ0eXAiOiJKV1QiLCJhbGciOiJSUzI1NiJ9.eyJhdWQiOiIyIiwianRpIjoiZTE2N2JmYjZmYzUyOWI3NTkwZjEwNjA5NTNiOWQyMTUwNzk3YjBiNTBhM2Q1M2ZiZGNmYTllYWY3NTZiYzBlNzFmODljNDc4ZGY5MzgxNTYiLCJpYXQiOjE2NDg5MTY4MzguMjk2NDg3LCJuYmYiOjE2NDg5MTY4MzguMjk2NDkyLCJleHAiOjE2ODA0NTI4MzguMjkzMjU2LCJzdWIiOiIxMDI1Iiwic2NvcGVzIjpbXX0.kZgCjSpDcOF1EntOPVuQIX2EYMOpfnY9QjOMu_q4-UvWstHTXsQrztmBfCw7--TLRydXggEFpqXJP2VtfDMED2KT_MUcWQP-AjuqM6WMGXb4LygXHRUw6h6hymJzxEGy5Ndo7kxkUXeworuVn5uu__rwJlJgXsXrHoK2WrObyGWhWyUPadPiNmp_UXt3erJBcMjAKh2Yz09uVOGOHC4opJ0HYYDFa8wKOEUpuuXW4sk9Agebi-M3dzDY3rSa2HcbC7fkWRQZ4LEoPKfX9ssVBCw2kcfnUVOAeC4LwrIjusnW4lp1ADhcHt-iO5ZTNIyKvedZjxBJSPY723Jo54UgvPreo9sk1_GPCMPhKmomztW9h0C6ktHnV26lLIcgH3WTrui-1RHZLazqIyr7SSy3_j2zvi_I_-RIlo8qxQPK9htpa8ledwHCSHuuhory0GpgHNivvGkDxQcPv6qQAkVovDqFZb2cV9GeuM-DNtVEQlIOsuqi2pQMmF-Sp33oA4gGZXVtVaBnWoS4cfEKinj_97ENXbNMOwUsZT6ffNvdw07nHTb0lIQM5m6kSEs9q6Wm1TzfzO1qjz761f3YuMpDeb7kOQPb993xUKxXbDeUHVK5GHoGB-dL-B9MCTNgNCi_IhHJFlSVUrI7fjkKUSnsR_oMjDTnJ_5DK7psHD6D8sY");
                    return params;
                }
            };
            request.setRetryPolicy(new DefaultRetryPolicy(300000,DefaultRetryPolicy.DEFAULT_MAX_RETRIES,DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            requestQueue.add(request);
        }catch (Exception ignored){
            Log.e(".....","helloooo11...."+ignored+"");
            Toast.makeText(getContext(),"Unable to connect to server",Toast.LENGTH_SHORT).show();
        }
    }

    void FetchDealerName(String srchStr){

        //String link = "http://ec2-3-7-170-219.ap-south-1.compute.amazonaws.com:9002/custom/preOrderParty/searchByParty";  //Local

        String link = "https://app.realbooks.in/custom/preOrderParty/searchByParty";  //Live
        try {
            if(requestQueue==null) {
                requestQueue = Volley.newRequestQueue(getContext());
            }

            JSONObject jsonEntityData= new JSONObject();
            jsonEntityData.put("secretKey", asscesskey);
            jsonEntityData.put("accessKey", asscesskey);
            jsonEntityData.put("cid", 1089);
            jsonEntityData.put("branchId", 1089);
            jsonEntityData.put("type", "Sale Order");
            jsonEntityData.put("srchStr", srchStr);//
//            Log.e(".......",link);
//            Log.e("......111.....",jsonEntityData.toString());

            JsonObjectRequest request=new JsonObjectRequest(Request.Method.POST, link, jsonEntityData, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    try{
                        //Log.e("Response==> ",response.toString());
                        if(response.getString("type").equalsIgnoreCase("success")){
                            JSONArray data=response.getJSONArray("data");
                            ArrayList<HashMap<String,String>> searchArrayList= new ArrayList<>();
                            for (int i=0; i<data.length(); i++){
                                JSONObject object12=data.getJSONObject(i);
                                HashMap<String,String> hashMap=new HashMap<>();
                                hashMap.put("lid",object12.getString("lid"));
                                hashMap.put("gstInNo",object12.getString("gstInNo"));
                                hashMap.put("gstRegType",object12.getString("gstRegType"));
                                hashMap.put("ledgerGroup",object12.getString("ledgerGroup"));
                                hashMap.put("party_name",object12.getString("partyName"));
                                searchArrayList.add(hashMap);
                            }
                            DealerSearchAutoComp adapter = new DealerSearchAutoComp(getContext(), android.R.layout.simple_dropdown_item_1line, searchArrayList);
                            dealerSearchM.setAdapter(adapter);
                            adapter.notifyDataSetChanged();

                        }else if (response.getString("type").equalsIgnoreCase("error")) {
                            Log.e("Error:1 ",response.toString());

                        }else if(response.getString("type").equalsIgnoreCase("autherror")){
                            Toast.makeText(getContext(),"Session expired.",Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(getActivity(), LoginActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            intent.putExtra("EXIT", true);
                            startActivity(intent);
                            getActivity().finish();

                        }else{
                            Log.e("Error: ",response.toString());
                        }
                    }catch (Exception e){
                        Log.e("....","heloooooooo"+e.getLocalizedMessage());
                        String message="That's an error Exception";
                        Toast.makeText(getContext(),message,Toast.LENGTH_SHORT).show();

                    }
                }
            },new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.e(".....","helloooo12...."+error);
                    Toast.makeText(getContext(),"Unable to connect to server",Toast.LENGTH_SHORT).show();
                }
            }){
                @Override
                public Map<String, String> getHeaders() {
                    Map<String, String> params = new HashMap<String, String>();
                    params.put("accountName", "bma");
                    return params;
                }
            };
            request.setRetryPolicy(new DefaultRetryPolicy(300000,DefaultRetryPolicy.DEFAULT_MAX_RETRIES,DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            requestQueue.add(request);
        }catch (Exception ignored){
            Log.e(".....","helloooo11...."+ignored+"");
            Toast.makeText(getContext(),"Unable to connect to server",Toast.LENGTH_SHORT).show();
        }

    }

}
