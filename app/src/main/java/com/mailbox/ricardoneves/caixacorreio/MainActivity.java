package com.mailbox.ricardoneves.caixacorreio;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.firebase.messaging.FirebaseMessaging;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private static final String NEW_MAIL_MSG = "new_mail";
    private static final String OPEN_DOOR_MSG = "open_door";
    private static final String CLOSE_DOOR_MSG = "close_door";
    private static final String RESET_MSG = "reset_mail";

    private static final String RED_COLOR = "#FF0000";
    private static final String GREEN_COLOR = "#00FF00";

    private static final String URL_STRING = "http://ricardoneves.noip.me:8090/current_state";
    private static final String USERNAME = "admin";
    private static final String PASSWORD = "password";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getState(getApplicationContext());

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
                switch (messageBody) {
                    case NEW_MAIL_MSG:
                        newMail();
                        break;
                    case OPEN_DOOR_MSG:
                        openDoor();
                        break;
                    case CLOSE_DOOR_MSG:
                        closeDoor();
                        break;
                    case RESET_MSG:
                        resetMail();
                        break;
                }
            }
        }
    };

    private void newMail() {
        LinearLayout mail_status_layout = (LinearLayout) findViewById(R.id.mail_status_layout);
        ImageView mail_status_img = (ImageView) findViewById(R.id.mail_status_img);

        mail_status_layout.setBackgroundColor(Color.parseColor(GREEN_COLOR));
        mail_status_img.setImageResource(R.drawable.open_email);
        MediaPlayer mp = MediaPlayer.create(getApplicationContext(), R.raw.door_bell);
        mp.start();
    }

    private void openDoor() {
        LinearLayout door_status_layout = (LinearLayout) findViewById(R.id.door_status_layout);
        ImageView door_status_img = (ImageView) findViewById(R.id.door_status_img);

        door_status_layout.setBackgroundColor(Color.parseColor(RED_COLOR));
        door_status_img.setImageResource(R.drawable.open_door);
    }

    private void closeDoor() {
        LinearLayout door_status_layout = (LinearLayout) findViewById(R.id.door_status_layout);
        ImageView door_status_img = (ImageView) findViewById(R.id.door_status_img);

        door_status_layout.setBackgroundColor(Color.parseColor(GREEN_COLOR));
        door_status_img.setImageResource(R.drawable.close_door);
    }

    private void resetMail() {
        LinearLayout mail_status_layout = (LinearLayout) findViewById(R.id.mail_status_layout);
        ImageView mail_status_img = (ImageView) findViewById(R.id.mail_status_img);

        mail_status_layout.setBackgroundColor(Color.parseColor(RED_COLOR));
        mail_status_img.setImageResource(R.drawable.close_email);
    }

    private void showAppDialog(String title, String message) {
        AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this).create();
        alertDialog.setTitle(title);
        alertDialog.setMessage(message);
        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        alertDialog.show();
    }


    private void getState(final Context context) {
        String baseAuthCredentials = USERNAME + ':' + PASSWORD;
        final String encodedCredentials = Base64.encodeToString(baseAuthCredentials.getBytes(), Base64.NO_WRAP);

        RequestQueue queue = Volley.newRequestQueue(context);

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, URL_STRING, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d("response", response.toString());
                        try {
                            String doorState = response.get("door").toString();
                            String mailState = response.get("mail").toString();

                            if("opened".equals(doorState))
                                openDoor();
                            if("has_mail".equals(mailState))
                                newMail();


                        } catch (JSONException e) {
                        }
                    }
        },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d("response", error.toString());
                        showAppDialog("Erro", "Não foi possível ir buscar o estado");
                    }
                }
        ){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<>();
                headers.put("Authorization", "Basic " + encodedCredentials);
                return headers;
            }
        };

        jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy());
        // Make the call for the HTTP request
        queue.add(jsonObjectRequest);
    }


}
