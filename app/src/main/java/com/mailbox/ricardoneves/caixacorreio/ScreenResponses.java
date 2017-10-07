package com.mailbox.ricardoneves.caixacorreio;


import android.app.Activity;
import android.content.DialogInterface;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.support.v7.app.AlertDialog;
import android.widget.ImageView;
import android.widget.LinearLayout;

public final class ScreenResponses {

    private static final String RED_COLOR = "#FF0000";
    private static final String GREEN_COLOR = "#00FF00";

    private ScreenResponses() {}

    public static void newEmail(Activity activity, boolean playSound) {
        LinearLayout mail_status_layout = (LinearLayout) activity.findViewById(R.id.mail_status_layout);
        ImageView mail_status_img = (ImageView) activity.findViewById(R.id.mail_status_img);

        mail_status_layout.setBackgroundColor(Color.parseColor(GREEN_COLOR));
        mail_status_img.setImageResource(R.drawable.open_email);
        if(playSound) {
            MediaPlayer mp = MediaPlayer.create(activity.getApplicationContext(), R.raw.door_bell);
            mp.start();
        }
    }

    public static void openDoor(Activity activity) {
        LinearLayout door_status_layout = (LinearLayout) activity.findViewById(R.id.door_status_layout);
        ImageView door_status_img = (ImageView) activity.findViewById(R.id.door_status_img);

        door_status_layout.setBackgroundColor(Color.parseColor(RED_COLOR));
        door_status_img.setImageResource(R.drawable.open_door);
    }

    public static void closeDoor(Activity activity) {
        LinearLayout door_status_layout = (LinearLayout) activity.findViewById(R.id.door_status_layout);
        ImageView door_status_img = (ImageView) activity.findViewById(R.id.door_status_img);

        door_status_layout.setBackgroundColor(Color.parseColor(GREEN_COLOR));
        door_status_img.setImageResource(R.drawable.close_door);
    }

    public static void resetMail(Activity activity) {
        LinearLayout mail_status_layout = (LinearLayout) activity.findViewById(R.id.mail_status_layout);
        ImageView mail_status_img = (ImageView) activity.findViewById(R.id.mail_status_img);

        mail_status_layout.setBackgroundColor(Color.parseColor(RED_COLOR));
        mail_status_img.setImageResource(R.drawable.close_email);
    }

    public static void showAppDialog(String title, String message, Activity activity) {
        AlertDialog alertDialog = new AlertDialog.Builder(activity).create();
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

}
