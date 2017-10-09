package com.mailbox.ricardoneves.caixacorreio;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class Weather {

    private static String API_URL = "http://api.apixu.com/v1/forecast.json?key=9142c24cb9d94061869225804170710&q=Maia&lang=pt&days=4";

    private Weather() {}

    public static void setWeather(final Activity activity) {

        RequestQueue queue = Volley.newRequestQueue(activity.getApplicationContext());

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, API_URL, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d("response_weather", response.toString());
                        try {
                            JSONArray forecasts = response.getJSONObject("forecast").getJSONArray("forecastday");
                            for(int i = 1; i < forecasts.length(); i++) {
                                JSONObject dayForecast = forecasts.getJSONObject(i).getJSONObject("day");
                                JSONObject condition = dayForecast.getJSONObject("condition");
                                String weekDay = getWeekDay(forecasts.getJSONObject(i).getString("date"));
                                String maxTemp = String.valueOf(
                                        Math.round(
                                                Double.parseDouble(dayForecast.getString("maxtemp_c"))
                                        )
                                );

                                String minTemp = String.valueOf(
                                        Math.round(
                                                Double.parseDouble(dayForecast.getString("mintemp_c"))
                                        )
                                );
                                String weatherStatus = condition.getString("text");
                                String iconUrl = condition.getString("icon");
                                setWeatherData(activity, weekDay, maxTemp, minTemp, weatherStatus, iconUrl, i);

                            }
                        } catch (JSONException e) {

                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d("response_weather", error.toString());
                    }
                }
        );

        jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy());
        // Make the call for the HTTP request
        queue.add(jsonObjectRequest);
    }

    private static void setWeatherData(Activity activity, String weekDay, String maxTemp, String minTemp, String weatherStatus, String iconUrl, int index) {
        switch(index) {
            case 1:
                ((TextView) activity.findViewById(R.id.week_day_day1)).setText(weekDay);
                ((TextView) activity.findViewById(R.id.weather_status_day1)).setText(weatherStatus);
                ((TextView) activity.findViewById(R.id.max_temp_day1)).setText(maxTemp + "ºC");
                ((TextView) activity.findViewById(R.id.min_temp_day1)).setText(minTemp + "ºC");
                new Weather.DownloadImageTask((ImageView) activity.findViewById(R.id.weather_icon_day1))
                        .execute("http:" + iconUrl);
                break;
            case 2:
                ((TextView) activity.findViewById(R.id.week_day_day2)).setText(weekDay);
                ((TextView) activity.findViewById(R.id.weather_status_day2)).setText(weatherStatus);
                ((TextView) activity.findViewById(R.id.max_temp_day2)).setText(maxTemp + "ºC");
                ((TextView) activity.findViewById(R.id.min_temp_day2)).setText(minTemp + "ºC");
                new Weather.DownloadImageTask((ImageView) activity.findViewById(R.id.weather_icon_day2))
                        .execute("http:" + iconUrl);
                break;
            case 3:
                ((TextView) activity.findViewById(R.id.week_day_day3)).setText(weekDay);
                ((TextView) activity.findViewById(R.id.weather_status_day3)).setText(weatherStatus);
                ((TextView) activity.findViewById(R.id.max_temp_day3)).setText(maxTemp + "ºC");
                ((TextView) activity.findViewById(R.id.min_temp_day3)).setText(minTemp + "ºC");
                new Weather.DownloadImageTask((ImageView) activity.findViewById(R.id.weather_icon_day3))
                        .execute("http:" + iconUrl);
                break;
        }
    }

    // date format should be "yyyy-MM-dd"
    private static String getWeekDay(String dateString) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        String weekDay = "Indeterminado";
        try {
            Date date = format.parse(dateString);
            Calendar c = Calendar.getInstance();
            c.setTime(date);
            int dayOfWeek = c.get(Calendar.DAY_OF_WEEK);
            switch(dayOfWeek) {
                case Calendar.SUNDAY:
                    weekDay = "Domingo";
                    break;
                case Calendar.MONDAY:
                    weekDay = "Segunda-Feira";
                    break;
                case Calendar.TUESDAY:
                    weekDay = "Terça-Feira";
                    break;
                case Calendar.WEDNESDAY:
                    weekDay = "Quarta-feira";
                    break;
                case Calendar.THURSDAY:
                    weekDay = "Quinta-feira";
                    break;
                case Calendar.FRIDAY:
                    weekDay = "Sexta-feira";
                    break;
                case Calendar.SATURDAY:
                    weekDay = "Sábado";
                    break;
            }
        } catch (ParseException e) {
        }

        return weekDay;
    }


    private static class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        ImageView bmImage;

        public DownloadImageTask(ImageView bmImage) {
            this.bmImage = bmImage;
        }

        protected Bitmap doInBackground(String... urls) {
            String urldisplay = urls[0];
            Bitmap mIcon11 = null;
            try {
                InputStream in = new java.net.URL(urldisplay).openStream();
                mIcon11 = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return mIcon11;
        }

        protected void onPostExecute(Bitmap result) {
            bmImage.setImageBitmap(result);
        }
    }
}
