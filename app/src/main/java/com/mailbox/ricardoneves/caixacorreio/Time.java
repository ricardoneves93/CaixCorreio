package com.mailbox.ricardoneves.caixacorreio;

import android.app.Activity;
import android.widget.TextView;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Time {

    private Time() {}

    public static void updateTime(Activity activity) {
        Date date = new Date();
        setTime(activity, date);
        setDate(activity, date);
    }

    private static void setTime(Activity activity, Date currentDate) {
        TextView clockView = (TextView) activity.findViewById(R.id.clock_text_time);
        DateFormat dateFormat = new SimpleDateFormat("HH:mm");
        String currentTime = dateFormat.format(currentDate);
        clockView.setText(currentTime);

    }

    private static void setDate(Activity activity, Date currentDate) {
        TextView dateView = (TextView) activity.findViewById(R.id.clock_text_date);
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String currentTime = dateFormat.format(currentDate);
        String yearOrdinal = currentTime.split("-")[0];
        String monthOrdinal = currentTime.split("-")[1];
        String dayOrdinal = currentTime.split("-")[2];
        String monthName = "Not defined";
        switch (monthOrdinal) {
            case "01":
                monthName = "Janeiro";
                break;
            case "02":
                monthName = "Fevereiro";
                break;
            case "03":
                monthName = "Março";
                break;
            case "04":
                monthName = "Abril";
                break;
            case "05":
                monthName = "Maio";
                break;
            case "06":
                monthName = "Junho";
                break;
            case "07":
                monthName = "Julho";
                break;
            case "08":
                monthName = "Agosto";
                break;
            case "09":
                monthName = "Setembro";
                break;
            case "10":
                monthName = "Outubro";
                break;
            case "11":
                monthName = "Novembro";
                break;
            case "12":
                monthName = "Dezembro";
                break;
        }

        String spelledDate = Integer.parseInt(dayOrdinal) + " de " + monthName + " de " + yearOrdinal;

        dateView.setText(spelledDate);
    }

}
