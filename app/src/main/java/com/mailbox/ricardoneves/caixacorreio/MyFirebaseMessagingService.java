package com.mailbox.ricardoneves.caixacorreio;

import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

/**
 * Created by ricardoneves on 21/09/2017.
 */

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private static final String TAG = "FCMService";
    private LocalBroadcastManager broadcaster;

    @Override
    public void onCreate() {
        super.onCreate();
        broadcaster = LocalBroadcastManager.getInstance(this);
    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

//        Intent intent = new Intent("mail_data");
//        intent.putExtra("body", remoteMessage.getNotification().getBody());
//        intent.putExtra("title", remoteMessage.getNotification().getTitle());
//        broadcaster.sendBroadcast(intent);
    }



}
