package com.aspl.steel.QrScannerGroup;

import android.Manifest;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
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
import com.aspl.steel.R;
import com.aspl.steel.SteelHelper;
import com.aspl.steel.fragments.GPSTracker;
import com.aspl.steel.fragments.ScannedBarcodeActivity;
import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class QrScaneToDetailsView extends Fragment {

    View rootView;
    DrawerLayout mDrawerLayout;
    String sLid,cid,segid,tempKeyCode,uid,storedName;
    final String LOG_DBG = getClass().getSimpleName();
    Date date = new Date();

    ObjectAnimator animator;
    View scannerLayout;
    View scannerBar;
    SurfaceView surfaceView;
    TextView txtBarcodeValue;

    private BarcodeDetector barcodeDetector;
    private CameraSource cameraSource;
    private static final int REQUEST_CAMERA_PERMISSION = 201;
    String intentData = "";
    int scannerCount = 0;
    DecimalFormat precision = new DecimalFormat("0.00");
    SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy");

    LinearLayout detalis_lay;
    TextView order_num,date_txt,party_name,item_size,remarks_txt;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.qr_scanner_details_view, container, false);
        viewSet(rootView);
        SharedPreferences sharedPref = getActivity().getSharedPreferences("loginCredentials", Context.MODE_PRIVATE);
        cid = sharedPref.getString("cid", "");
        segid = sharedPref.getString("segid", "");
        tempKeyCode = sharedPref.getString("storedKeyCode", "");
        sLid = sharedPref.getString("storedSlid", "");
        uid = sharedPref.getString("storedUserId", "");
        storedName = sharedPref.getString("storedName","");
        TextView scan_btn = (TextView)rootView.findViewById(R.id.scan_btn);
        initViews();
        scannerLayout = rootView.findViewById(R.id.scannerLayout);
        scannerBar = rootView.findViewById(R.id.scannerBar);
        animationView();

        scan_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                barcodeDetector.setProcessor(new Detector.Processor<Barcode>() {
                    @Override
                    public void release() {
                        // Toast.makeText(getContext(), "To prevent memory leaks barcode scanner has been stopped", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void receiveDetections(Detector.Detections<Barcode> detections) {
                        final SparseArray<Barcode> barcodes = detections.getDetectedItems();
                        if (barcodes.size() != 0) {
                            txtBarcodeValue.post(new Runnable() {
                                @Override
                                public void run() {
                                    if (scannerCount <= 0) {
                                        scannerCount = 1;
                                        if (barcodes.valueAt(0).email != null) {
                                            txtBarcodeValue.removeCallbacks(null);
                                            intentData = barcodes.valueAt(0).email.address;
                                            txtBarcodeValue.setText(intentData);
                                            Log.e("ADD CONTENT TO THE MAIL", "  ..............");
                                            cameraSource.stop();
                                        } else {
                                            intentData = barcodes.valueAt(0).displayValue;
                                            //  dlr_dtlpopup.setVisibility(View.VISIBLE);
                                            txtBarcodeValue.setText("Successfully Scanned Please Wait for Tract Location");
                                            cameraSource.stop();
                                            animator.setDuration(0);
                                            locationTrackingMethod(intentData);
                                            //Log.e("LAUNCH URL", "  .............." + "      AAAA" + intentData);
                                        }
                                    }
                                }
                            });

                        }
                    }
                });
            }
        });

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


    private void initViews() {
        surfaceView = rootView.findViewById(R.id.surfaceView);
        txtBarcodeValue = rootView.findViewById(R.id.txtBarcodeValue);
        detalis_lay =(LinearLayout) rootView.findViewById(R.id.detalis_lay);
        order_num =(TextView) rootView.findViewById(R.id.order_num);
        date_txt =(TextView) rootView.findViewById(R.id.date_txt);
        party_name =(TextView) rootView.findViewById(R.id.party_name);
        item_size =(TextView) rootView.findViewById(R.id.item_size);
        remarks_txt =(TextView) rootView.findViewById(R.id.remarks_txt);

    }

    private void initialiseDetectorsAndSources() {
        Toast.makeText(getContext(), "Barcode scanner started", Toast.LENGTH_SHORT).show();
        barcodeDetector = new BarcodeDetector.Builder(getContext())
                .setBarcodeFormats(Barcode.ALL_FORMATS)
                .build();

        cameraSource = new CameraSource.Builder(getContext(), barcodeDetector)
                .setRequestedPreviewSize(1920, 1080)
                .setAutoFocusEnabled(true) //you should add this feature
                .build();

        surfaceView.getHolder().addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder holder) {
                try {
                    if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                        cameraSource.start(surfaceView.getHolder());
                    } else {
                        ActivityCompat.requestPermissions(getActivity(), new
                                String[]{Manifest.permission.CAMERA}, REQUEST_CAMERA_PERMISSION);
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
            }

            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {
                cameraSource.stop();
            }
        });


//        barcodeDetector.setProcessor(new Detector.Processor<Barcode>() {
//            @Override
//            public void release() {
//                // Toast.makeText(getContext(), "To prevent memory leaks barcode scanner has been stopped", Toast.LENGTH_SHORT).show();
//            }
//
//            @Override
//            public void receiveDetections(Detector.Detections<Barcode> detections) {
//                final SparseArray<Barcode> barcodes = detections.getDetectedItems();
//                if (barcodes.size() != 0) {
//                    txtBarcodeValue.post(new Runnable() {
//                        @Override
//                        public void run() {
//                            if (scannerCount <= 0) {
//                                scannerCount = 1;
//                                if (barcodes.valueAt(0).email != null) {
//                                    txtBarcodeValue.removeCallbacks(null);
//                                    intentData = barcodes.valueAt(0).email.address;
//                                    txtBarcodeValue.setText(intentData);
//                                    Log.e("ADD CONTENT TO THE MAIL", "  ..............");
//                                    cameraSource.stop();
//                                } else {
//                                    intentData = barcodes.valueAt(0).displayValue;
//                                  //  dlr_dtlpopup.setVisibility(View.VISIBLE);
//                                    txtBarcodeValue.setText("Successfully Scanned Please Wait for Tract Location");
//                                    cameraSource.stop();
//                                    animator.setDuration(0);
//                                    locationTrackingMethod(intentData);
//                                     //Log.e("LAUNCH URL", "  .............." + "      AAAA" + intentData);
//                                }
//                            }
//                        }
//                    });
//
//                }
//            }
//        });
    }


    @Override
    public void onPause() {
        super.onPause();
        cameraSource.release();
    }

    @Override
    public void onResume() {
        super.onResume();
        initialiseDetectorsAndSources();
    }

    void animationView() {
        //Scanner overlay
        animator = null;

        ViewTreeObserver vto = scannerLayout.getViewTreeObserver();
        vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {

                scannerLayout.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
                    scannerLayout.getViewTreeObserver().
                            removeGlobalOnLayoutListener(this);

                } else {
                    scannerLayout.getViewTreeObserver().
                            removeOnGlobalLayoutListener(this);
                }

                float destination = (float) (scannerLayout.getY() +
                        scannerLayout.getHeight());

                animator = ObjectAnimator.ofFloat(scannerBar, "translationY",
                        scannerLayout.getY(),
                        destination);

                animator.setRepeatMode(ValueAnimator.REVERSE);
                animator.setRepeatCount(ValueAnimator.INFINITE);
                animator.setInterpolator(new AccelerateDecelerateInterpolator());
                animator.setDuration(3000);
                animator.start();

            }
        });

    }

    void locationTrackingMethod(String qrCode) {
        GPSTracker gpsTracker = new GPSTracker(getContext());
        if (!gpsTracker.canGetLocation()) {
            Toast.makeText(getContext(), "GPS is disabled. You will be log out", Toast.LENGTH_SHORT).show();
            return;

        }else {
            addressListApi(gpsTracker.latitude, gpsTracker.longitude,qrCode);
        }
    }


    void addressListApi(Double latitude, Double longitude,String qrcode_data){

        RequestQueue requestQueue = null;
        String link = "https://bandhan.captainsteel.com/saathi/index.php/api/scan-qr-code";  //Live
        try {
            if(requestQueue==null) {
                requestQueue = Volley.newRequestQueue(getContext());
            }

            JSONObject jsonEntityData= new JSONObject();
            jsonEntityData.put("username", storedName);
            jsonEntityData.put("lat", latitude.toString());
            jsonEntityData.put("long", longitude.toString());
            jsonEntityData.put("qr_code", qrcode_data.toString());
//            Log.e(".......",link);
//            Log.e("......111.....",jsonEntityData.toString());

            JsonObjectRequest request=new JsonObjectRequest(Request.Method.POST, link, jsonEntityData, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    try{
                        //Log.e("Response==> ",response.toString());
                        if(response.getBoolean("success")){
                            txtBarcodeValue.setVisibility(View.INVISIBLE);
                            JSONObject data = response.getJSONObject("data");
                            if (data.length()>0) {
                                detalis_lay.setVisibility(View.VISIBLE);
                                order_num.setText(data.getString("order_number"));
                                date_txt.setText(data.getString("created_at"));
                                party_name.setText(data.getString("party_name"));
                                item_size.setText(data.getString("size"));
                                remarks_txt.setText(data.getString("remarks"));
                            }
                        }else{
                            String message=response.getString("message");
                            txtBarcodeValue.setText(message);
                            Toast.makeText(getContext(),message,Toast.LENGTH_SHORT).show();
                        }
                    }catch (Exception e){
                        Log.e("....","heloooooooo"+e.getLocalizedMessage());
                        String message= "That's an Error Exception";
                        try {
                            message = response.getString("message");
                            txtBarcodeValue.setText(message);
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

}
