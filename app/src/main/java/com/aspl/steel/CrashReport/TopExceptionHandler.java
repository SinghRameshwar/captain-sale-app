package com.aspl.steel.CrashReport;

import android.content.Context;

import androidx.appcompat.app.AppCompatActivity;

import java.io.FileOutputStream;
import java.io.IOException;

/**
 *  Created by Arnab Kar on 3/11/15.
 */
public class TopExceptionHandler implements Thread.UncaughtExceptionHandler {

    private Thread.UncaughtExceptionHandler defaultUEH;

    private AppCompatActivity app = null;

    public TopExceptionHandler(AppCompatActivity app) {
        this.defaultUEH = Thread.getDefaultUncaughtExceptionHandler();
        this.app = app;
    }

    public void uncaughtException(Thread t, Throwable e)
    {
        StackTraceElement[] arr = e.getStackTrace();
        String report = e.toString()+"\n\n";
        report += "--------- Stack trace ---------\n\n";
        for (StackTraceElement anArr : arr) {
            report += "    " + anArr.toString() + "\n";
        }
        report += "-------------------------------\n\n";

// If the exception was thrown in a background thread inside
// AsyncTask, then the actual exception can be found with getCause
        report += "--------- Cause ---------\n\n";
        Throwable cause = e.getCause();
        if(cause != null) {
            report += cause.toString() + "\n\n";
            arr = cause.getStackTrace();
            for (StackTraceElement anArr : arr) {
                report += "    " + anArr.toString() + "\n";
            }
        }
        report += "-------------------------------\n\n";

        try {
            FileOutputStream trace = app.openFileOutput(
                    "stack.trace", Context.MODE_PRIVATE);
            trace.write(report.getBytes());
            trace.close();
            new CrashReportSender(app).sendReport("");
        } catch(IOException ioe) {
// ...
        }

        defaultUEH.uncaughtException(t, e);
    }
}