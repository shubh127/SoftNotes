package com.example.shubh.project.Firebase;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.example.shubh.project.Activity.Dashboard;
import com.example.shubh.project.Activity.SplashScreen;
import com.example.shubh.project.R;
import com.google.firebase.messaging.RemoteMessage;
import com.kosalgeek.android.photoutil.MainActivity;

import java.util.Random;

/**
 * Created by filipp on 5/23/2016.
 */
public class FirebaseMessagingService extends com.google.firebase.messaging.FirebaseMessagingService {
    String msg;

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
       // Log.d(">>>>>", "Message data payload: " + remoteMessage.getData().get("message"));
        msg = remoteMessage.getData().get("message");
        shwoNotification();
    }

    private void shwoNotification() {
        NotificationCompat.Builder notification;
        final int uniqueId = 45612;
        Uri soundURI = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        notification = new NotificationCompat.Builder(this);
        notification.setAutoCancel(true);
        notification.setSmallIcon(R.drawable.homeicon);
        notification.setTicker("New Upload From Your Friend");
        notification.setWhen(System.currentTimeMillis());
        notification.setContentTitle("New Upload");
        notification.setContentText(msg);
        notification.setSound(soundURI);


        Intent intent = new Intent(this, SplashScreen.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        notification.setContentIntent(pendingIntent);

        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        notificationManager.notify(uniqueId, notification.build());


    }
}