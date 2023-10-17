package com.aspl.steel.adapters;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.PagerAdapter;

import com.aspl.steel.SteelHelper;

import java.util.ArrayList;

/**
 *  Created by Arnab on 02 July 2016.
 */
public class ImagePagerAdapter extends PagerAdapter {
    AppCompatActivity context;
    private ArrayList<Uri> mMedia;
    public ImagePagerAdapter(AppCompatActivity context, ArrayList<Uri> mMedia) {
        this.context=context;
        this.mMedia=mMedia;
    }
    @Override
    public int getCount() {
        return mMedia.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }
    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        ImageView imageView = new ImageView(context);
        DisplayMetrics displaymetrics = new DisplayMetrics();
        context.getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        int height = displaymetrics.heightPixels;
        int width = displaymetrics.widthPixels;
        Log.e("Path: ",mMedia.get(position).getPath()+imageView.getWidth());
        try{
            Bitmap bitmap = SteelHelper.createScaledBitmap(mMedia.get(position).getPath(), width, height);
            imageView.setImageBitmap(bitmap);
        }catch (Exception ignored){
        }
        //imageView.setImageURI(mMedia.get(position));
        container.addView(imageView);
        return imageView;
    }


    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        ImageView imageView = (ImageView) object;
        BitmapDrawable bd = (BitmapDrawable)imageView.getDrawable();
        bd.getBitmap().recycle();
        imageView.setImageBitmap(null);
        container.removeView((ImageView) object);
    }

}