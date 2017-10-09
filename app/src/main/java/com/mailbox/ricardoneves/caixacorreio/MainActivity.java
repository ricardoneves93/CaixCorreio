package com.mailbox.ricardoneves.caixacorreio;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

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

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class MainActivity extends AppCompatActivity {

    private static final String NEW_MAIL_MSG = "new_mail";
    private static final String OPEN_DOOR_MSG = "open_door";
    private static final String CLOSE_DOOR_MSG = "close_door";
    private static final String RESET_MSG = "reset_mail";

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

        // Get the weather every 60 minutes
        ScheduledExecutorService schedulerTime =
                Executors.newSingleThreadScheduledExecutor();

        // Get the weather every 60 minutes
        ScheduledExecutorService schedulerWeather =
                Executors.newSingleThreadScheduledExecutor();


        // Must have a "runOnUiThread" since every operation with UI must be handled by UI Thread
        schedulerTime.scheduleAtFixedRate
                (new Runnable() {
                    @Override
                    public void run() {
                        runOnUiThread(new Runnable() {
                            public void run() {
                                Time.updateTime(MainActivity.this);
                            }
                        });
                    }
                }, 0, 1, TimeUnit.SECONDS);


        // Must have a "runOnUiThread" since every operation with UI must be handled by UI Thread
        schedulerWeather.scheduleAtFixedRate
                (new Runnable() {
                    @Override
                    public void run() {
                        runOnUiThread(new Runnable() {
                            public void run() {
                                Weather.setWeather(MainActivity.this);
                            }
                        });
                    }
                }, 0, 60, TimeUnit.MINUTES);
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
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
                        ScreenResponses.newEmail(MainActivity.this, true);
                        break;
                    case OPEN_DOOR_MSG:
                        ScreenResponses.openDoor(MainActivity.this);
                        break;
                    case CLOSE_DOOR_MSG:
                        ScreenResponses.closeDoor(MainActivity.this);
                        break;
                    case RESET_MSG:
                        ScreenResponses.resetMail(MainActivity.this);
                        break;
                }
            }
        }
    };


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
                                ScreenResponses.openDoor(MainActivity.this);
                            if("has_mail".equals(mailState))
                                ScreenResponses.newEmail(MainActivity.this, false);


                        } catch (JSONException e) {
                            ScreenResponses.showAppDialog("Erro", "Não foi possível processar resposta", MainActivity.this);
                        }
                    }
        },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d("response", error.toString());
                        ScreenResponses.showAppDialog("Erro", "Não foi possível ir buscar o estado", MainActivity.this);
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
