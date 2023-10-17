package com.aspl.steel;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Environment;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.BitmapCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;

/**
 *  Created by Arnab on 17 February 2016.
 */
//Tip: type show line number after pressing Ctrl+Shift+A to show the line number
public class SteelHelper {
    public static boolean isNetworkAvailable(AppCompatActivity activity) {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) activity.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
    static String sha1(String base) {
        try{
            MessageDigest digest = MessageDigest.getInstance("SHA-1");
            byte[] hash = digest.digest(base.getBytes("UTF-8"));
            StringBuilder hexString = new StringBuilder();

            for (byte aHash : hash) {
                String hex = Integer.toHexString(0xff & aHash);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }

            return hexString.toString();
        } catch(Exception ex){
            throw new RuntimeException(ex);
        }
    }

    public static void replaceFragment (AppCompatActivity activity,Fragment fragment,boolean history){
        String backStateName =  fragment.getClass().getName();
        String fragmentTag = backStateName;

        FragmentManager manager = activity.getSupportFragmentManager();
        boolean fragmentPopped = manager.popBackStackImmediate (backStateName, 0);
        while (manager.popBackStackImmediate());

        if (!fragmentPopped && manager.findFragmentByTag(fragmentTag) == null){ //fragment not in back stack, create it.
            FragmentTransaction ft = manager.beginTransaction();
            ft.setCustomAnimations(android.R.anim.slide_in_left, android.R.anim.slide_out_right,android.R.anim.slide_in_left, android.R.anim.slide_out_right);
            ft.replace(R.id.container, fragment, fragmentTag);
            //ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
            if(history)
                ft.addToBackStack(backStateName);
            ft.commit();
        }
        else {
            //Fragment in back stack, still create it, we really dont care it here
            FragmentTransaction ft = manager.beginTransaction();
            ft.replace(R.id.container, fragment, fragmentTag);
            ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
            ft.addToBackStack(backStateName);
            ft.commit();
        }
    }

    public static void addFragment (AppCompatActivity activity,Fragment fragment,boolean history){
        String backStateName =  fragment.getClass().getName();
        String fragmentTag = backStateName;

        FragmentManager manager = activity.getSupportFragmentManager();
        boolean fragmentPopped = manager.popBackStackImmediate (backStateName, 0);

        if (!fragmentPopped && manager.findFragmentByTag(fragmentTag) == null){ //fragment not in back stack, create it.
            FragmentTransaction ft = manager.beginTransaction();
            ft.setCustomAnimations(android.R.anim.slide_in_left, android.R.anim.slide_out_right,android.R.anim.slide_in_left, android.R.anim.slide_out_right);
            ft.add(R.id.container, fragment, fragmentTag);
            //ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
            if(history)
                ft.addToBackStack(backStateName);
            ft.commit();
        }
        else {
            //Fragment in back stack, still create it, we really dont care it here
            FragmentTransaction ft = manager.beginTransaction();
            ft.add(R.id.container, fragment, fragmentTag);
            ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
            ft.addToBackStack(backStateName);
            ft.commit();
        }
    }

    public static String getPostDataString(HashMap<String, String> params) throws UnsupportedEncodingException {
        StringBuilder result = new StringBuilder();
        boolean first = true;
        for(Map.Entry<String, String> entry : params.entrySet()){
            if (first)
                first = false;
            else
                result.append("&");

            result.append(URLEncoder.encode(entry.getKey(), "UTF-8"));
            result.append("=");
            result.append(URLEncoder.encode(entry.getValue(), "UTF-8"));
        }

        return result.toString();
    }

    public static String  performPostCall(String requestURL, HashMap<String, String> postDataParams) {
        URL url;
        String response = "";
        try {
            url = new URL(requestURL);

            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(15000);
            conn.setConnectTimeout(15000);
            conn.setRequestMethod("POST");
            conn.setDoInput(true);
            conn.setDoOutput(true);

            OutputStream os = conn.getOutputStream();
            BufferedWriter writer = new BufferedWriter(
                    new OutputStreamWriter(os, "UTF-8"));
            writer.write(SteelHelper.getPostDataString(postDataParams));

            writer.flush();
            writer.close();
            os.close();
            int responseCode=conn.getResponseCode();

            if (responseCode == HttpsURLConnection.HTTP_OK) {
                String line;
                BufferedReader br=new BufferedReader(new InputStreamReader(conn.getInputStream()));
                while ((line=br.readLine()) != null) {
                    response+=line;
                }
            }
            else {
                response="";

            }
            if (conn.getResponseCode()!=200){
                throw new Exception("Status code is "+conn.getResponseCode());
            }
        } catch (Exception e) {
            return "Exception: " + e.toString();
        }

        return response;
    }

    public static boolean dealerItemDuplicateEntryCheckRequisition(String mainObjToSearch,String dealerId,String subDealerId,String itemIdObj){
        try{
            String itemId=new JSONObject(itemIdObj).getString("id");
            JSONArray invVdetailList=new JSONObject(mainObjToSearch).getJSONObject("invVoucherObj").getJSONArray("invVdetailList");
            for(int i=0;i<invVdetailList.length();i++){
                JSONObject invVdetailListObj=invVdetailList.getJSONObject(i);
                if(invVdetailListObj.getInt("status")==0){
                    continue;
                }
                String ItemIdInList=invVdetailListObj.getJSONObject("itemId").getString("id");
                if(ItemIdInList.equals(itemId)){
                    String dealerIdInList=invVdetailListObj.getString("text4");
                    String subDealerIdInList=invVdetailListObj.getString("text2");
                    if((dealerId+":"+subDealerId).equals(dealerIdInList+":"+subDealerIdInList)){
                        return false;           //false means there is already a entry
                    }
                }

            }
        }catch (Exception e){
            Log.e("Steel Helper class", e.getLocalizedMessage());
            return false;
        }
        return true;
    }
    public static boolean dealerItemDuplicateEntryCheckDaliySales(String mainObjToSearch,String dealerId,String itemIdObj){
        try{
            String itemId=new JSONObject(itemIdObj).getString("id");
            JSONArray invVdetailList=new JSONObject(mainObjToSearch).getJSONObject("invVoucherObj").getJSONArray("invVdetailList");
            for(int i=0;i<invVdetailList.length();i++){
                JSONObject invVdetailListObj=invVdetailList.getJSONObject(i);
                if(invVdetailListObj.getInt("status")==0){
                    continue;
                }
                String ItemIdInList=invVdetailListObj.getJSONObject("itemId").getString("id");
                if(ItemIdInList.equals(itemId)){
                    String DealerIdInList=invVdetailListObj.getString("text4");
                    if(dealerId.equals(DealerIdInList)){
                        return false;           //false means there is already a entry
                    }
                }

            }
        }catch (Exception e){
            Log.e("Steel Helper class", e.getLocalizedMessage());
            return false;
        }
        return true;
    }

    public static boolean dealerItemDuplicateEditCheckRequisition(String mainObjToSearch,String dealerId,String subDealerId,String itemIdObj,int position){
        Boolean flagDuplicate=false;
        Log.e("Postion",position+"");
        Log.e("DealerId:SubDealerId",dealerId+":"+subDealerId);
        try{
            String itemId=new JSONObject(itemIdObj).getString("id");
            JSONArray invVdetailList=new JSONObject(mainObjToSearch).getJSONObject("invVoucherObj").getJSONArray("invVdetailList");
            for(int i=0;i<invVdetailList.length();i++){
                JSONObject invVdetailListObj=invVdetailList.getJSONObject(i);
                String ItemIdInList=invVdetailListObj.getJSONObject("itemId").getString("id");
                if(ItemIdInList.equals(itemId)){
                    String DealerIdInList=invVdetailListObj.getString("text4");
                    String subDealerIdInList=invVdetailListObj.getString("text2");
                    Log.e("item","same Item Found at"+i);//+(position!=i)+ "   DealerIdSubDealrId:"+DealerIdInList+"-"+subDealerIdInList);

                    if(((dealerId+":"+subDealerId).equalsIgnoreCase(DealerIdInList+":"+subDealerIdInList))&& position!=i){
                        Log.e("ok","bug");
                        return false;
                    }
                    else if(((dealerId+":"+subDealerId).equalsIgnoreCase(DealerIdInList+":"+subDealerIdInList))&& !flagDuplicate && position==i){
                        flagDuplicate=true;
                    }
                    else if(((dealerId+":"+subDealerId).equals(DealerIdInList+":"+subDealerIdInList))&& flagDuplicate){
                        return false;           //false means there is already a entry
                    }
                }
            }
        }catch (Exception e){
            Log.e("Steel Helper class", e.getLocalizedMessage());
            return false;
        }
        return true;
    }

    public static boolean dealerItemDuplicateEditCheck(String mainObjToSearch,String dealerId,String itemIdObj,int position){
        Log.e("Object",mainObjToSearch);
        Log.e("dealerId",dealerId);
        Log.e("itemId",itemIdObj);
        Boolean flagDuplicate=false;
        try{
            String itemId=new JSONObject(itemIdObj).getString("id");
            JSONArray invVdetailList=new JSONObject(mainObjToSearch).getJSONObject("invVoucherObj").getJSONArray("invVdetailList");
            for(int i=0;i<invVdetailList.length();i++){
                JSONObject invVdetailListObj=invVdetailList.getJSONObject(i);
                String ItemIdInList=invVdetailListObj.getJSONObject("itemId").getString("id");
                if(ItemIdInList.equals(itemId)){
                    String DealerIdInList=invVdetailListObj.getString("text4");
                    if(dealerId.equals(DealerIdInList)&& position!=i){
                        return false;
                    }
                    else if(dealerId.equals(DealerIdInList)&& !flagDuplicate && position==i){
                        flagDuplicate=true;
                    }
                    else if(dealerId.equals(DealerIdInList)&& flagDuplicate){
                        return false;           //false means there is already a entry
                    }
                }
            }
        }catch (Exception e){
            Log.e("Steel Helper class", e.getLocalizedMessage());
            return false;
        }
        return true;
    }

    public static JSONArray remove(final int idx, final JSONArray from) {
        final List<JSONObject> objs = asList(from);
        objs.remove(idx);

        final JSONArray ja = new JSONArray();
        for (final JSONObject obj : objs) {
            ja.put(obj);
        }

        return ja;
    }

    public static List<JSONObject> asList(final JSONArray ja) {
        final int len = ja.length();
        final ArrayList<JSONObject> result = new ArrayList<>(len);
        for (int i = 0; i < len; i++) {
            final JSONObject obj = ja.optJSONObject(i);
            if (obj != null) {
                result.add(obj);
            }
        }
        return result;
    }
    public static Date getFirstDateOfMonth(Calendar cal) {
        cal.set(Calendar.DAY_OF_MONTH, cal.getActualMinimum(Calendar.DAY_OF_MONTH));
        return cal.getTime();
    }
    public static Date getLastDateOfMonth(Calendar cal) {
        cal.set(Calendar.DAY_OF_MONTH, cal.getActualMaximum(Calendar.DAY_OF_MONTH));
        return cal.getTime();
    }
    public static int dp2px(Context context,int dp) {
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        return Math.round(dp * (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));
    }
    public static void writefile(Context context,String log,String fileName,boolean external) {
        File storageDir;
        if(external)
            storageDir = Environment.getExternalStorageDirectory();
        else
            storageDir = new File(context.getFilesDir()+"/RealBooks", fileName);

        File myFile = new File(storageDir, fileName+".txt");

        if(!myFile.exists()){
            try {
                myFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (myFile.exists()) {
            try {
                FileOutputStream fostream = new FileOutputStream(myFile,false);
                OutputStreamWriter oswriter = new OutputStreamWriter(fostream);
                BufferedWriter bwriter = new BufferedWriter(oswriter);
                bwriter.append(log);
                bwriter.newLine();
                bwriter.close();
                oswriter.close();
                fostream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    public static String convertToBase64(String imagePath)
    {
        Bitmap bm = BitmapFactory.decodeFile(imagePath);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        int bitmapByteCount= BitmapCompat.getAllocationByteCount(bm);
        if(bitmapByteCount>512576){
            bm.compress(Bitmap.CompressFormat.JPEG, 50, baos);
        }
        else {
            bm.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        }
        byte[] byteArrayImage = baos.toByteArray();
        String encodedImage = Base64.encodeToString(byteArrayImage, Base64.DEFAULT);
        return encodedImage;
    }
    public static Bitmap convertToImage(Context context,String strBase64){
        try {
            byte[] decodedString = Base64.decode(strBase64, Base64.DEFAULT);
            Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
            return decodedByte;
        }catch (Exception e){
            return BitmapFactory.decodeResource(context.getResources(),R.drawable.ic_action_cancel);
        }
    }
    public static String resizeBase64Image(String base64image,int IMG_WIDTH,int IMG_HEIGHT){
        byte [] encodeByte=Base64.decode(base64image.getBytes(),Base64.DEFAULT);
        BitmapFactory.Options options=new BitmapFactory.Options();
        options.inPurgeable = true;
        Bitmap image = BitmapFactory.decodeByteArray(encodeByte, 0, encodeByte.length,options);


        if(image.getHeight() <= 400 && image.getWidth() <= 400){
            return base64image;
        }
        image = Bitmap.createScaledBitmap(image, IMG_WIDTH, IMG_HEIGHT, false);

        ByteArrayOutputStream baos=new  ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.JPEG,100, baos);

        byte [] b=baos.toByteArray();
        System.gc();
        return Base64.encodeToString(b, Base64.NO_WRAP);
    }
    public static Bitmap createScaledBitmap(String pathName, int width, int height) {
        final BitmapFactory.Options opt = new BitmapFactory.Options();
        opt.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(pathName, opt);
        opt.inSampleSize = calculateBmpSampleSize(opt, width, height);
        opt.inJustDecodeBounds = false;
        return BitmapFactory.decodeFile(pathName, opt);
    }
    private static int calculateBmpSampleSize(BitmapFactory.Options opt, int width, int height) {
        final int outHeight = opt.outHeight;
        final int outWidth = opt.outWidth;
        int sampleSize = 1;
        if (outHeight > height || outWidth > width) {
            final int heightRatio = Math.round((float) outHeight / (float) height);
            final int widthRatio = Math.round((float) outWidth / (float) width);
            sampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
        }
        return sampleSize;
    }
}