package com.example.newdemo;


import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;


public class ForegroundService extends Service {
    public static final String CHANNEL_ID = "ForegroundServiceChannel";
    private boolean isActive = false;

    private SharedPreferences user;
    private Handler handler = new Handler();

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        Intent resetIntent = new Intent(this, ForegroundService.class);
        resetIntent.setAction(ConstantsStep.RESET_COUNT);
        PendingIntent resetPendingIntent = PendingIntent.getService(this, 0, resetIntent, PendingIntent.FLAG_IMMUTABLE);

        resetIntent.setAction(ConstantsStep.STOP_SAVE_COUNT);
        PendingIntent stopPendingIntent = PendingIntent.getService(this, 0, resetIntent, PendingIntent.FLAG_IMMUTABLE);

        String input = intent.getStringExtra("inputExtra");
        createNotificationChannel();

        Intent notificationIntent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this,
                0, notificationIntent, PendingIntent.FLAG_IMMUTABLE);
        Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("Vikas Kumar")
                .setContentText(input)
                .setSmallIcon(R.drawable.baseline_call_24)
                .setLargeIcon(Bitmap.createScaledBitmap(BitmapFactory.decodeResource(this.getResources(), R.drawable.profile), 97, 128, false))
                .setContentIntent(pendingIntent)
                .addAction(R.drawable.reset, "Reject", resetPendingIntent)
                .addAction(R.drawable.stop, "Accept", stopPendingIntent)
                .setOngoing(true)
                .build();
        startForeground(1, notification);

        //do heavy work on a background thread
        //stopSelf();
        return START_NOT_STICKY;

    }


    public void stopService() {
        Intent serviceIntent = new Intent(this, ForegroundService.class);
        stopService(serviceIntent);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel serviceChannel = new NotificationChannel(
                    CHANNEL_ID,
                    "Foreground Service Channel",
                    NotificationManager.IMPORTANCE_DEFAULT
            );
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(serviceChannel);
        }
    }
}
