package com.aspl.steel.fragments;

import android.Manifest;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.ProgressDialog;
import android.content.Context;
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

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import com.aspl.steel.GlobalConfiguration;
import com.aspl.steel.R;
import com.aspl.steel.SteelHelper;
import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;

import org.json.JSONObject;

import java.io.IOException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

/**
 * Created by arnab on 01 July 2019.
 */

public class ScannedBarcodeActivity extends Fragment {

    // Animation view
    ObjectAnimator animator;
    View scannerLayout;
    View scannerBar;
    View rootView;

    SurfaceView surfaceView;
    TextView txtBarcodeValue, dler_id, scnDate, loc_adrText, locnMisMas,dlr_subdlr_loc;
    LinearLayout mis_lay;
    private BarcodeDetector barcodeDetector;
    private CameraSource cameraSource;
    private static final int REQUEST_CAMERA_PERMISSION = 201;
    String intentData = "";
    int scannerCount = 0;
    FrameLayout dlr_dtlpopup;
    HashMap<String, String> postHashMapData = new HashMap();
    JSONObject qrcodeDtls;
    DecimalFormat precision = new DecimalFormat("0.00");
    SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy");

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.activity_scan_barcode, container, false);
        dlr_dtlpopup = (FrameLayout) rootView.findViewById(R.id.dlr_dtlpopup);
        dlr_dtlpopup.setVisibility(View.GONE);

        TextView popuo_donebtn = (TextView) rootView.findViewById(R.id.popuo_donebtn);
        popuo_donebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (postHashMapData.size() > 0) {
                    new dealerValitationQrAtten((AppCompatActivity) getActivity(), postHashMapData).execute();
                } else {
                    Toast.makeText(getContext(), "Sorry, No dealer is registered in this QR code !", Toast.LENGTH_SHORT).show();
                }
            }
        });

        TextView popuo_cancelbtn = (TextView) rootView.findViewById(R.id.popuo_Cancelbtn);
        popuo_cancelbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                removeFragmentMethod();
            }
        });

        try {
            initViews();
            animationView();
        }catch (Exception e){Log.e("Scann View Error",e.toString());}
        return rootView;
    }


    private void initViews() {
        txtBarcodeValue = rootView.findViewById(R.id.txtBarcodeValue);
        surfaceView = rootView.findViewById(R.id.surfaceView);
        dler_id = rootView.findViewById(R.id.dler_id);
        scnDate = rootView.findViewById(R.id.scnDate);
        loc_adrText = rootView.findViewById(R.id.loc_adrText);
        locnMisMas = rootView.findViewById(R.id.locnMisMas);
        dlr_subdlr_loc=rootView.findViewById(R.id.dlr_subdlr_loc);
        mis_lay=rootView.findViewById(R.id.mis_lay);

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
                                    //Log.e("ADD CONTENT TO THE MAIL", "  ..............");
                                    cameraSource.stop();
                                } else {
                                    intentData = barcodes.valueAt(0).displayValue;
                                    dlr_dtlpopup.setVisibility(View.VISIBLE);
                                    valuesSetOnUI(intentData);
                                    // Log.e("LAUNCH URL", "  .............." + "      AAAA" + intentData);
                                    cameraSource.stop();

                                }
                            }
                        }
                    });

                }
            }
        });
    }

    void valuesSetOnUI(String values) {
        try {
            qrcodeDtls = new JSONObject(values);
            txtBarcodeValue.setText(qrcodeDtls.getString("dealerName"));
            dler_id.setText(qrcodeDtls.getString("dl_id"));

            String formattedDate = df.format(new Date());
            scnDate.setText(formattedDate);//qrcodeDtls.getString("date"));//latitude_dec:22.71, longitude_dec:88.48,   longitude: 88-28-38,   latitude 22-42-41
            locationTrackingMethod();
            if (postHashMapData != null) {
                // 100 meaters destiance check to dealer locations
                loc_adrText.setText(postHashMapData.get("location"));
                double latitudeDl=Double.parseDouble(postHashMapData.get("latitude"));
                double longitudeDl=Double.parseDouble(postHashMapData.get("longitude"));
                double qrlatitude=Double.parseDouble(qrcodeDtls.getString("latitude_dec"));
                double qrlongitude=Double.parseDouble(qrcodeDtls.getString("longitude_dec"));
                double distanceflt=distance(latitudeDl,longitudeDl,qrlatitude,qrlongitude);
                postHashMapData.put("status","2");
                SharedPreferences sharedPref = getActivity().getSharedPreferences("loginCredentials", Context.MODE_PRIVATE);
                String locationDiff = sharedPref.getString("locationDiff", "");
                if (distanceflt>Double.parseDouble(locationDiff)) {
                    mis_lay.setVisibility(View.VISIBLE);
                    postHashMapData.put("difference",distanceflt+"");
                    String dealerLoc=bySouradDaLocation(qrlatitude,qrlongitude);
                    locnMisMas.setText("Your Location is "+precision.format(distanceflt / 1000) + " Km Mismatch !");
                    dlr_subdlr_loc.setText(dealerLoc);
                }

            }
        } catch (Exception e) {
            Log.e("Formate Error: ", e.toString());
        }
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
        scannerLayout = rootView.findViewById(R.id.scannerLayout);
        scannerBar = rootView.findViewById(R.id.scannerBar);

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


    class dealerValitationQrAtten extends AsyncTask<String, Void, String> {
        private AppCompatActivity context;
        ProgressDialog progressDialog;
        HashMap<String, String> hashMap;

        public dealerValitationQrAtten(AppCompatActivity context, HashMap<String, String> hashMap) {
            this.context = context;
            this.hashMap = hashMap;
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
                String link = "http://" + dnsport + "/SteelSales-war/Stl_Track_Salesman_Loc_Api";
                HashMap<String, String> postDataParams = new HashMap<>();

                SharedPreferences sharedPref = getActivity().getSharedPreferences("loginCredentials", Context.MODE_PRIVATE);
                String cid = sharedPref.getString("cid", "");
                String segid = sharedPref.getString("segid", "");
                String keyCode = sharedPref.getString("storedKeyCode", "");

                postDataParams = hashMap;
                postDataParams.put("cid", cid);
                postDataParams.put("segid", segid);
                postDataParams.put("uid", sharedPref.getString("storedUserId", ""));
                postDataParams.put("keyCode", keyCode);

                return SteelHelper.performPostCall(link, postDataParams);
            } catch (Exception e) {
                return "Exception: " + e.toString();
            }
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            Log.e("Response:  ", s.toString());
            if (progressDialog != null && progressDialog.isShowing())
                progressDialog.dismiss();
            try {
                JSONObject jsonObject = new JSONObject(s);
                if (jsonObject.getString("type").equals("success")) {
                    removeFragmentMethod();
                } else {
                    throw new Exception("Type error");
                }
            } catch (Exception e) {
                Log.e("......", e.getLocalizedMessage());
                Toast.makeText(context, "Error connecting to server", Toast.LENGTH_SHORT).show();
            }
        }
    }

    HashMap locationCalculate(Double latitude, Double longitude) {
        SharedPreferences sharedPref = getActivity().getSharedPreferences("loginCredentials", Context.MODE_PRIVATE);
        final HashMap<String, String> postDataParams = new HashMap<>();
        double distance_km = 0.0;
        postDataParams.put("sLid", sharedPref.getString("storedSlid", ""));
        postDataParams.put("longitude", longitude + "");
        postDataParams.put("latitude", latitude + "");
        postDataParams.put("timeOfUpdate", new Date().getTime() + "");
        postDataParams.put("distance", distance_km + "");
        postDataParams.put("dealer_id", dler_id.getText().toString());

        String locationAddress = "";
        if (Geocoder.isPresent()) {
            Geocoder geocoder = new Geocoder(getActivity(), Locale.getDefault());
            try {
                List<Address> addressList = geocoder.getFromLocation(latitude, longitude, 1);
                String addressLine0 = addressList.get(0).getAddressLine(0);
                String addressLine1 = addressList.get(0).getAddressLine(1);
                String addressLine2 = addressList.get(0).getAddressLine(2);
                if (addressLine0 != null)
                    locationAddress += addressLine0;
                if (addressLine1 != null)
                    locationAddress += " " + addressLine1;
                if (addressLine2 != null)
                    locationAddress += " " + addressLine2;

            } catch (Exception ignored) {
            }
            //Log.e("Location",locationAddress);
        }
        postDataParams.put("location", locationAddress + "");
        return postDataParams;
    }


    void removeFragmentMethod() {
        // getFragmentManager().beginTransaction().remove(ScannedBarcodeActivity.this).commit();
        getFragmentManager().popBackStack();
    }


    void locationTrackingMethod() {
        GPSTracker gpsTracker = new GPSTracker(getContext());
        postHashMapData = new HashMap<>();
        if (!gpsTracker.canGetLocation()) {
            Toast.makeText(getContext(), "GPS is disabled. You will be log out", Toast.LENGTH_SHORT).show();
            return;

        }else {
            postHashMapData = locationCalculate(gpsTracker.latitude, gpsTracker.longitude);
        }
    }

    //Here getting distance in kilometers (km)
    private double distance(double old_latitude, double old_longitude, double latitude, double longitude) {
        Location locationA = new Location("point A");
        locationA.setLatitude(old_latitude);
        locationA.setLongitude(old_longitude);
        Location locationB = new Location("point B");
        locationB.setLatitude(latitude);
        locationB.setLongitude(longitude);

        return (locationA.distanceTo(locationB));
    }



    String bySouradDaLocation(Double latitude, Double longitude) {
        String locationAddress = "";
        if (Geocoder.isPresent()) {
            Geocoder geocoder = new Geocoder(getActivity(), Locale.getDefault());
            try {
                List<Address> addressList = geocoder.getFromLocation(latitude, longitude, 1);
                String addressLine0 = addressList.get(0).getAddressLine(0);
                String addressLine1 = addressList.get(0).getAddressLine(1);
                String addressLine2 = addressList.get(0).getAddressLine(2);
                if (addressLine0 != null)
                    locationAddress += addressLine0;
                if (addressLine1 != null)
                    locationAddress += " " + addressLine1;
                if (addressLine2 != null)
                    locationAddress += " " + addressLine2;

            } catch (Exception ignored) {
            }
            //Log.e("Location",locationAddress);
        }
        return locationAddress;
    }




}