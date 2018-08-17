package com.mailbox.ricardoneves.caixacorreio;

import android.app.Activity;
import android.content.Context;
import android.util.Base64;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class Mail {

    private static final String URL_STRING = "http://ricardoneves.noip.me:8090/current_state";
    private static final String USERNAME = "admin";
    private static final String PASSWORD = "password";

    private static Boolean previousHasMail = false;

    public static void updateMailState(final Context context, final Activity activity) {
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
                                ScreenResponses.openDoor(activity);
                            else if("closed".equals(doorState)) {
                                ScreenResponses.closeDoor(activity);
                            }
                            if("has_mail".equals(mailState)) {
                                if(previousHasMail == false) {
                                    ScreenResponses.newEmail(activity, true);
                                    previousHasMail = true;
                                } else {
                                    ScreenResponses.newEmail(activity, false);
                                }

                            }
                            if("empty".equals(mailState)) {
                                ScreenResponses.resetMail(activity);
                                previousHasMail = false;
                            }


                        } catch (JSONException e) {
                            ScreenResponses.showAppDialog("Erro", "Não foi possível processar resposta", activity);
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d("response", error.toString());
                        ScreenResponses.showAppDialog("Erro", "Não foi possível ir buscar o estado", activity);
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
