package com.aspl.steel.fragments;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.core.content.FileProvider;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.aspl.steel.R;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

/**
 * Created by arnab on 15 October 2019.
 */

public class ShareCurrentLoc extends Fragment {

    View rootView;
    DrawerLayout mDrawerLayout;
    TextView latitudeTxt,longitudeTxt;
    EditText dlrNameEdt;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.current_location, container, false);
        dlrNameEdt=(EditText)rootView.findViewById(R.id.dlrName);
        latitudeTxt=(TextView)rootView.findViewById(R.id.dlrltd);
        longitudeTxt=(TextView)rootView.findViewById(R.id.dlrlogtitude);
        ImageView locationShare=(ImageView)rootView.findViewById(R.id.locationShare);
        locationShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                shareReciptView();
            }
        });
        viewSet(rootView);
        locationTrackingMethod();
        return rootView;
    }

    private void viewSet(View rootView) {
        ((TextView) rootView.findViewById(R.id.screenTitle)).setText("Current Location");
        Toolbar mToolbar = (Toolbar) rootView.findViewById(R.id.my_actionbar_toolbar);
        if(mToolbar!=null) {
            ((AppCompatActivity)getActivity()).setSupportActionBar(mToolbar);
            mDrawerLayout=(DrawerLayout)getActivity().findViewById(R.id.drawer_layout);
            //This draws the menu icon on the title that looks like 3 horizontal lines(=)
            ActionBarDrawerToggle mDrawerToggle = new ActionBarDrawerToggle(
                    getActivity(),  mDrawerLayout, mToolbar,
                    R.string.navigation_drawer_open, R.string.navigation_drawer_close
            );
            mDrawerLayout.setDrawerListener(mDrawerToggle);
            mDrawerToggle.syncState();
        }
    }

    void locationTrackingMethod() {
        GPSTracker gpsTracker = new GPSTracker(getActivity());
        if (!gpsTracker.canGetLocation()) {
            Toast.makeText(getContext(), "GPS is disabled. You will be log out", Toast.LENGTH_SHORT).show();
            return;
        }else {
            HashMap hashMap=locationCalculate(gpsTracker.latitude, gpsTracker.longitude);
            latitudeTxt.setText("Latitude: "+hashMap.get("latitude"));
            longitudeTxt.setText("Longitude: "+hashMap.get("longitude")+"\n\n"+hashMap.get("location"));
        }
    }

    void shareReciptView(){
        ScrollView loc_contentView=(ScrollView)rootView.findViewById(R.id.loc_contentView);
        Bitmap bitmap = getBitmapFromView(loc_contentView);
        if (bitmap != null ) {
            //Save the image inside the APPLICTION folder
            File mediaStorageDir = new File(getActivity().getExternalCacheDir() + "Image.png");
            try {
                FileOutputStream outputStream = new FileOutputStream(String.valueOf(mediaStorageDir));
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
                outputStream.close();

                Uri imageUri = FileProvider.getUriForFile(getActivity(), getActivity().getApplicationContext().getPackageName() + ".provider", mediaStorageDir);
                Intent waIntent = new Intent(Intent.ACTION_SEND);
                waIntent.putExtra(Intent.EXTRA_TEXT, dlrNameEdt.getText()+"\n\n"+latitudeTxt.getText()+"\n\n"+longitudeTxt.getText());
                waIntent.putExtra(Intent.EXTRA_STREAM, imageUri);
                waIntent.setType("*/*");
                newGrantPermsionExplicty(waIntent,imageUri);
                startActivity(Intent.createChooser(waIntent, "Share with"));

            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public Bitmap getBitmapFromView(ScrollView view) {
      /*  Log.e("width", String.valueOf(view.getChildAt(0).getWidth()));
        Log.e("getHeight", String.valueOf(view.getChildAt(0).getHeight()));*/
        Bitmap bitmap = Bitmap.createBitmap(
                view.getChildAt(0).getWidth(),
                view.getChildAt(0).getHeight(), Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas(bitmap);
        view.getChildAt(0).draw(c);

        return bitmap;
    }

    HashMap locationCalculate(Double latitude, Double longitude) {
        final HashMap<String, String> postDataParams = new HashMap<>();
        postDataParams.put("latitude", latitude + "");
        postDataParams.put("longitude", longitude + "");
        postDataParams.put("currentTime", new Date().getTime() + "");

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

    void newGrantPermsionExplicty(Intent shareIntent, Uri urisShare) {
        List<ResolveInfo> resolvedInfoActivities =
                getActivity().getPackageManager().queryIntentActivities(shareIntent, PackageManager.MATCH_DEFAULT_ONLY);

        for (ResolveInfo ri : resolvedInfoActivities) {
            Log.d("", "Granting permission to - " + ri.activityInfo.packageName);
            getActivity().grantUriPermission(ri.activityInfo.packageName, urisShare, Intent.FLAG_GRANT_READ_URI_PERMISSION);
        }
    }

}