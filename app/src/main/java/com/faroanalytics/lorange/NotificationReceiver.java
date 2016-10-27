package com.faroanalytics.lorange;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.widget.Toast;

import static android.media.RingtoneManager.getDefaultUri;

public class NotificationReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        String name = intent.getStringExtra("name");
        String phone = intent.getStringExtra("phone");
        String picture = intent.getStringExtra("picture");

        Intent notificationIntent = new Intent(context, BirthdayActivity.class);
        notificationIntent.putExtra("name", name);
        notificationIntent.putExtra("phone", phone);
        notificationIntent.putExtra("picture", picture);

        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        int PENDING_NOTIFICATION_INTENT_REQUEST_CODE = (int) System.currentTimeMillis();
        PendingIntent pendingNotificationIntent = PendingIntent.getActivity(context, PENDING_NOTIFICATION_INTENT_REQUEST_CODE,
                notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context)
                .setSmallIcon(android.R.drawable.btn_star)
                .setContentTitle("Birthday Reminder")
                .setContentText("Click me to see who's birthday it is")
                .setTicker("IMD BD Notification")
                .setContentIntent(pendingNotificationIntent)
                .setAutoCancel(false);

        notificationManager.notify(PENDING_NOTIFICATION_INTENT_REQUEST_CODE, builder.build());

    }
}