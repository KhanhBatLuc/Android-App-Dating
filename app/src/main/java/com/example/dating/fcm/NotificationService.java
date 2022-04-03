package com.example.dating.fcm;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import com.example.dating.R;
import com.example.dating.activity.ChatBoxActivity;
import com.example.dating.application.Functions;
import com.example.dating.dataholder.SessionManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import org.jetbrains.annotations.Contract;

import java.util.Map;

public class NotificationService extends FirebaseMessagingService {

    public static final String TYPE_NOTIFICATION_ORDER = "App\\Order";
    public static final String TYPE_NOTIFICATION_ARTICLE = "App\\Article";
    public static final String TYPE_NOTIFICATION_RATE = "RATE";
    private String APPLICATION_CHANNEL_ID = "BanaanApplicationNotifications";
    String room = "";

    @Override
    public void onNewToken(@NonNull String token) {
        super.onNewToken(token);
        new SessionManager(this).setFcmToken(token);
        Functions.refreshUserFCMToken(new SessionManager(this).getAccessToken(), token);
    }

    @SuppressWarnings("ConstantConditions")
    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        Map<String, String> data = remoteMessage.getData();


//        for (Map.Entry<String, String> entry : remoteMessage.getData().entrySet()) {
//            String key = entry.getKey();
//            String value = entry.getValue();
//            Log.d("onMessageReceived ", "key, " + key + " value " + value);
//        }

        Log.e("data model_id", data.get("model_id") == null ? "null" : data.get("model_id"));
        String title = data.get("title");
        String body = data.get("body");
        String model = data.get("model");
        String type = data.get("type");
        room = data.get("room");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
            createApplicationChannel();

        showNotify(title, body, Integer.parseInt(data.get("model_id") == null ? "0" : data.get("model_id")), getNotificationIntent(model, type, data));
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void createApplicationChannel() {
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if (notificationManager != null) {
            NotificationChannel notificationChannel = notificationManager.getNotificationChannel(APPLICATION_CHANNEL_ID);
            if (notificationChannel == null) {
                notificationChannel = new NotificationChannel(APPLICATION_CHANNEL_ID, getString(R.string.app_name), NotificationManager.IMPORTANCE_DEFAULT);
                notificationChannel.setDescription("Notifications");
                notificationChannel.enableLights(true);
                notificationChannel.enableVibration(true);
                notificationManager.createNotificationChannel(notificationChannel);
            }
        }
    }

    @SuppressWarnings("ConstantConditions")
    @Contract(pure = true)
    private PendingIntent getNotificationIntent(@NonNull String model, String type, Map<String, String> data) {
        Intent intent;
        intent = new Intent(this, ChatBoxActivity.class);
        intent.putExtra("room", room);
//        intent.putExtra("activity", "com.example.dating.activity.ChatBoxActivity");
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        return PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    private void showNotify(String title, String body, int modelID, PendingIntent pendingIntent) {
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this, APPLICATION_CHANNEL_ID);
        mBuilder.setContentTitle(title)
                .setContentText(body)
                .setSmallIcon(R.drawable.logo)
                .setStyle(new NotificationCompat.BigTextStyle(mBuilder).bigText(body))
                .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.logo))
                .setAutoCancel(true)
                .setVibrate(new long[]{300, 300, 300, 300, 300})
                .setLights(Color.GREEN, 1000, 2000)
                .setContentIntent(pendingIntent);

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if (notificationManager != null) {
            notificationManager.notify(modelID, mBuilder.build());
        }
    }
}
