package com.mailbox.ricardoneves.caixacorreio;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.Objects;

public class MainActivity extends AppCompatActivity {

    private final static String NEW_MAIL_MSG = "new_mail";
    private final static String OPEN_DOOR_MSG = "open_door";
    private final static String CLOSE_DOOR_MSG = "close_door";
    private final static String RESET_MSG = "reset_mail";

    private final static String RED_COLOR = "#FF0000";
    private final static String GREEN_COLOR = "#00FF00";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Register receiver to receive Notifications from firebaseMessagingService
        LocalBroadcastManager.getInstance(this).registerReceiver((mMessageReceiver), new IntentFilter("mail_data"));

        FirebaseMessaging.getInstance().subscribeToTopic("mail");

        // Hide the Title Bar
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        setContentView(R.layout.activity_main);

        // Hide the Status Bar
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
    }

    @Override
    protected void onResume() {
        super.onResume();
        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
    }

    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String messageBody = intent.getExtras().getString("body");
            if(!TextUtils.isEmpty(messageBody)) {
                LinearLayout mail_status_layout = (LinearLayout) findViewById(R.id.mail_status_layout);
                LinearLayout door_status_layout = (LinearLayout) findViewById(R.id.door_status_layout);
                ImageView mail_status_img = (ImageView) findViewById(R.id.mail_status_img);
                ImageView door_status_img = (ImageView) findViewById(R.id.door_status_img);
                switch (messageBody) {
                    case NEW_MAIL_MSG:
                        mail_status_layout.setBackgroundColor(Color.parseColor(GREEN_COLOR));
                        mail_status_img.setImageResource(R.drawable.open_email);
                        MediaPlayer mp = MediaPlayer.create(getApplicationContext(), R.raw.door_bell);
                        mp.start();
                        break;
                    case OPEN_DOOR_MSG:
                        door_status_layout.setBackgroundColor(Color.parseColor(RED_COLOR));
                        door_status_img.setImageResource(R.drawable.open_door);
                        break;
                    case CLOSE_DOOR_MSG:
                        door_status_layout.setBackgroundColor(Color.parseColor(GREEN_COLOR));
                        door_status_img.setImageResource(R.drawable.close_door);
                        break;
                    case RESET_MSG:
                        mail_status_layout.setBackgroundColor(Color.parseColor(RED_COLOR));
                        mail_status_img.setImageResource(R.drawable.close_email);
                        break;
                }
            }



        }
    };
}
