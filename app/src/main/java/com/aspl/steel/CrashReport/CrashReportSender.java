package com.aspl.steel.CrashReport;

import android.content.Intent;

import androidx.appcompat.app.AppCompatActivity;

import com.aspl.steel.R;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 *  Created by Arnab Kar on 3/11/15.
 */
public class CrashReportSender {
    private AppCompatActivity activity;
    public CrashReportSender(AppCompatActivity activity){
        this.activity=activity;
    }
    public void sendReport(String s){

        String line,trace="";
        try {
            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(activity
                            .openFileInput("stack.trace")));
            while((line = reader.readLine()) != null) {
                trace += line+"\n";
            }
        }  catch(IOException ioe) {
            trace="";
        }
        if(!s.equals("")){
            trace=trace+"\n"+s;
        }
        if(!trace.equals("")){
            Intent sendIntent = new Intent(Intent.ACTION_SEND);
            String subject = activity.getResources().getString(R.string.ErrorReport);
            String body =
                    "Mail this to developer of Steel Sales App: "+
                            "\n"+
                            trace+
                            "\n";

            sendIntent.putExtra(Intent.EXTRA_EMAIL,
                    new String[] {"rameshwariise49@gmail.com"});
            sendIntent.putExtra(Intent.EXTRA_TEXT, body);
            sendIntent.putExtra(Intent.EXTRA_SUBJECT, subject);
            sendIntent.setType("message/rfc822");

            activity.startActivity(
                    Intent.createChooser(sendIntent, "Send crash report using:"));

            activity.deleteFile("stack.trace");
        }
        else {
            Intent sendIntent = new Intent(Intent.ACTION_SEND);
            String subject = activity.getResources().getString(R.string.ErrorReport);
            String body =
                    "Mail this to developer of Steel Sales App: "+
                            "\n"+
                            trace+
                            "\n";

            sendIntent.putExtra(Intent.EXTRA_EMAIL,
                    new String[] {"rameshwariise49@gmail.com"});
            sendIntent.putExtra(Intent.EXTRA_TEXT, body);
            sendIntent.putExtra(Intent.EXTRA_SUBJECT, subject);
            sendIntent.setType("message/rfc822");

            activity.startActivity(
                    Intent.createChooser(sendIntent, "Send crash report using:"));
        }
    }
    public void trackServerIssue(String title,String msg){
        String line,trace="";
        try {
            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(activity
                            .openFileInput("stack.trace")));
            while((line = reader.readLine()) != null) {
                trace += line+"\n";
            }
        }  catch(IOException ioe) {
            trace="";
        }
        if(!msg.equals("")){
            trace=trace+"\n"+msg;
        }
        if(!trace.equals("")){
            Intent sendIntent = new Intent(Intent.ACTION_SEND);
            String subject = activity.getResources().getString(R.string.ErrorReport)+" with "+msg;
            String body =
                    "Mail this to developer of Steel Sales App: "+
                            "\n"+
                            trace+
                            "\n";

            sendIntent.putExtra(Intent.EXTRA_EMAIL,
                    new String[] {"rameshwariise49@gmail.con"});
            sendIntent.putExtra(Intent.EXTRA_TEXT, body);
            sendIntent.putExtra(Intent.EXTRA_SUBJECT, subject);
            sendIntent.setType("message/rfc822");

            activity.startActivity(
                    Intent.createChooser(sendIntent, title));

            activity.deleteFile("stack.trace");
        }
    }
}
