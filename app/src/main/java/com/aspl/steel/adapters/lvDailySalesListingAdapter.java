package com.aspl.steel.adapters;

import android.annotation.TargetApi;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.aspl.steel.GlobalConfiguration;
import com.aspl.steel.R;
import com.aspl.steel.SteelHelper;
import com.aspl.steel.fragments.DailySaleConfirmationFragment;

import org.json.JSONArray;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.util.HashMap;

/**
 *  Created by Arnab on 3/3/16.
 */
public class lvDailySalesListingAdapter extends BaseAdapter{
    String LOG_DBG=this.getClass().getSimpleName();
    public JSONArray myList ;
    LayoutInflater inflater;
    AppCompatActivity context;
    String cid,segId,tempKeyCode,uid;

    public lvDailySalesListingAdapter(AppCompatActivity context, JSONArray myList,String cid,String segId,String tempKeyCode,String uid) {
        super();
        this.myList = myList;
        this.context = context;
        inflater = LayoutInflater.from(this.context);        // only context can also be used
        this.cid=cid;
        this.segId=segId;
        this.tempKeyCode=tempKeyCode;
        this.uid=uid;
    }

    @Override
    public int getCount() {
        return myList.length();
    }

    @Override
    public Object getItem(int position) {
        try {
            return myList.get(position);
        }catch (Exception e){
            return new JSONObject();
        }
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final MyViewHolder mViewHolder;

        if(convertView == null) {
            convertView = inflater.inflate(R.layout.report_daily_sale_confirm_listitem, parent,false);
            mViewHolder = new MyViewHolder();
            convertView.setTag(mViewHolder);
        } else {
            mViewHolder = (MyViewHolder) convertView.getTag();
        }

        mViewHolder.salesmanName=(TextView)convertView.findViewById(R.id.salesmanName);
        mViewHolder.date=(TextView)convertView.findViewById(R.id.date);
        mViewHolder.narration=(TextView)convertView.findViewById(R.id.narration);
        mViewHolder.quantity=(TextView)convertView.findViewById(R.id.quantity);
        mViewHolder.mainCard=convertView.findViewById(R.id.mainCard);

        int sdk = android.os.Build.VERSION.SDK_INT;
        if(position%2==0){
            if(sdk < android.os.Build.VERSION_CODES.JELLY_BEAN) {
                mViewHolder.mainCard.setBackgroundDrawable(ContextCompat.getDrawable(context, R.drawable.dark_container_bg_round_color1));
            } else {
                mViewHolder.mainCard.setBackground(ContextCompat.getDrawable(context, R.drawable.dark_container_bg_round_color1));
            }
        }else {
            if(sdk < android.os.Build.VERSION_CODES.JELLY_BEAN) {
                mViewHolder.mainCard.setBackgroundDrawable(ContextCompat.getDrawable(context, R.drawable.dark_container_bg_round_color2));
            } else {
                mViewHolder.mainCard.setBackground(ContextCompat.getDrawable(context, R.drawable.dark_container_bg_round_color2));
            }
        }

        try{
            final String idForEdit=myList.getJSONObject(position).getString("sLid");
            final String nameForEdit=myList.getJSONObject(position).getString("ledgerName");
            mViewHolder.salesmanName.setText(myList.getJSONObject(position).getString("ledgerName"));
            mViewHolder.date.setText(myList.getJSONObject(position).getString("dt"));
            final String dtForBackDateCheck=myList.getJSONObject(position).getString("dt");
            mViewHolder.quantity.setText(new BigDecimal(myList.getJSONObject(position).getDouble("qty")).setScale(3,BigDecimal.ROUND_HALF_EVEN).stripTrailingZeros().toPlainString());
            mViewHolder.narration.setText(myList.getJSONObject(position).isNull("narration")?"":myList.getJSONObject(position).getString("narration"));
            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    new BackdatedEditCheck(context, cid, segId, tempKeyCode, uid, dtForBackDateCheck,idForEdit,nameForEdit).execute();
                }
            });
        }catch (Exception e){

        }

        return convertView;
    }

    private class MyViewHolder {
        TextView date,salesmanName,quantity,narration;
        View mainCard;
    }
    class BackdatedEditCheck extends AsyncTask<String,Void,String> {
        private AppCompatActivity context;
        ProgressDialog progressDialog;
        String cid,segid,tempKeyCode,uid,Dt,idForEdit,nameForEdit;
        public BackdatedEditCheck(AppCompatActivity context,String cid,String segid,String tempKeyCode,String uid,String Dt,String idForEdit,String nameForEdit) {
            this.context = context;
            this.cid=cid;
            this.segid=segid;
            this.uid=uid;
            this.tempKeyCode=tempKeyCode;
            this.Dt=Dt;
            this.idForEdit=idForEdit;
            this.nameForEdit=nameForEdit;
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
                String link="http://"+dnsport+"/SteelSales-war/Stl_BackDateCheck_Api";
                HashMap<String,String> hm=new HashMap<>();
                hm.put("cid",cid);
                hm.put("segid", segid);
                hm.put("uid", uid);
                hm.put("date",Dt);
                hm.put("type","back_dt_edit");
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
            try{
                JSONObject jsonObject=new JSONObject(s);
                if(jsonObject.getString("type").equals("success")){
                    if(jsonObject.getString("msg").equals("true")){
                        //Backdated Entry allowed for this date

                        Fragment fragment= context.getSupportFragmentManager().findFragmentByTag(DailySaleConfirmationFragment.class.getName());
                        if(fragment!=null&& fragment.isVisible()){
                            context.getSupportFragmentManager().beginTransaction().detach(fragment).attach(fragment).commit();
                            Toast.makeText(context, context.getResources().getString(R.string.alreadyOnThatScreen), Toast.LENGTH_SHORT).show();
                        }
                        else {
                            Bundle bundle = new Bundle();
                            bundle.putString("dt", Dt);
                            bundle.putBoolean("backDateEdit",true);
                            bundle.putString("salesman_name",nameForEdit);
                            bundle.putString("salesman_id",idForEdit);
                            fragment=new DailySaleConfirmationFragment();
                            fragment.setArguments(bundle);
                            SteelHelper.addFragment(context,fragment,true);
                            //SteelHelper.replaceFragment(this,fragment, true)

                        }
                        /*FetchDailySaleListingTemplate fetchDailySaleEntryTemplate= new FetchDailySaleListingTemplate(getActivity(),cid,segid,tempKeyCode,fromDt,toDt);
                        fetchDailySaleEntryTemplate.execute();*/
                    }
                    else {
                        Toast.makeText(context, "Backdate Edit disabled", Toast.LENGTH_SHORT).show();
                    }
                }
                else {
                    throw new Exception("Type error");
                }
            }catch (Exception e){
                Log.e(LOG_DBG,e.getLocalizedMessage());
            }
        }
    }
}
