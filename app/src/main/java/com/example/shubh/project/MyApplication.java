package com.example.shubh.project;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.support.multidex.MultiDex;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

/**
 * Created by nitesh.behal on 11/19/2015.
 */
public class MyApplication extends Application {

    private static RequestQueue queue;


    void initiateVolley() {
        queue = Volley.newRequestQueue(this);
    }

    public static RequestQueue getQueue(Context context) {
        if (queue == null) {
            queue = Volley.newRequestQueue(context.getApplicationContext());

        }
        return queue;
    }

    /**
     * To catch all unCaught Exception.
     */
    private Thread.UncaughtExceptionHandler unCaughtExceptionHandler = new Thread.UncaughtExceptionHandler() {
        @Override
        public void uncaughtException(Thread thread, Throwable ex) {

            Intent intent = getPackageManager().getLaunchIntentForPackage("com.parchuni.buyerapp");
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);

            android.os.Process.killProcess(android.os.Process.myPid());


            System.exit(0); //Required to restart the application in case of any UncaughtException occurred
        }


    };

    @Override
    public void onCreate() {
        super.onCreate();
//        Thread.setDefaultUncaughtExceptionHandler(unCaughtExceptionHandler);
        initiateVolley();
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }


}
