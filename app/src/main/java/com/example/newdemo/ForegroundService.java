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
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;


public class ForegroundService extends Service {
    public static final String CHANNEL_ID = "ForegroundServiceChannel";
    private boolean isActive = false;
    int notification_id = 1711101;

    private SharedPreferences user;
    private Handler handler = new Handler();

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        if (ConstantsStep.REJECT.equals(intent.getAction())) {

            Toast.makeText(this, "reject", Toast.LENGTH_SHORT).show();
        }


        Notification notification = buildNotification(intent);
        startForeground(notification_id, notification);
        // Perform your service logic here
        return START_STICKY;


    }

    private void stopForegroundService(boolean b) {
        Toast.makeText(this, "accept", Toast.LENGTH_SHORT).show();


    }

    private void resetCount() {

        Toast.makeText(this, "reject", Toast.LENGTH_SHORT).show();

    }

    private Notification buildNotification(Intent intent) {

        Intent reject = new Intent(this, MainActivity.class);
        reject.setAction(ConstantsStep.REJECT);
        reject.putExtra("vikas", "kumar");
        PendingIntent rejectPendingIntent = PendingIntent.getBroadcast(this, 0, reject, PendingIntent.FLAG_IMMUTABLE);


        Intent accept = new Intent(this, MainActivity.class);
        accept.setAction(ConstantsStep.ACCEPT);
        PendingIntent acceptPendingIntent = PendingIntent.getBroadcast(this, 0, accept, PendingIntent.FLAG_IMMUTABLE);

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
                .addAction(R.drawable.reset, "Reject", rejectPendingIntent)
                .addAction(R.drawable.stop, "Accept", acceptPendingIntent)
                .setOngoing(true)
                .build();
        startForeground(notification_id, notification);

        //do heavy work on a background thread
        //stopSelf();

        return notification;
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
            NotificationChannel serviceChannel = new NotificationChannel(CHANNEL_ID, "Foreground Service Channel", NotificationManager.IMPORTANCE_DEFAULT);
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(serviceChannel);
        }
    }
}
