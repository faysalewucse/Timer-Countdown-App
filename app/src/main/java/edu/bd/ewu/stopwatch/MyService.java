package edu.bd.ewu.stopwatch;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.provider.AlarmClock;
import android.provider.Settings;
import android.util.Log;

import androidx.annotation.RequiresApi;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

public class MyService extends Service {
    public static final String SERVICE_MESSAGE = "ServiceMessage";
    int second;
    String check;
    final Handler handler = new Handler();
    private Runnable myRunnable;
    Intent ui = new Intent(SERVICE_MESSAGE);
    public MyService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        second = Integer.parseInt(intent.getStringExtra("SECOND"));
        check = intent.getStringExtra("SERVICE_NAME");
        Log.d("second", "onStartCommand: Service Start. Second: "+second);
        //For Showing Notification
        final String CHANNELID = "Foreground Service Id";
        NotificationChannel channel = new NotificationChannel(
                CHANNELID, CHANNELID, NotificationManager.IMPORTANCE_LOW
        );
        getSystemService(NotificationManager.class).createNotificationChannel(channel);
        myRunnable = new Runnable() {
            public void run() {
                boolean running = true;
                if(check.equalsIgnoreCase("stopwatch")) second++;
                else second--;
                Log.d("second", "run: second: "+second);
                Notification.Builder notification = new Notification.Builder(getApplicationContext(), CHANNELID)
                        .setContentText((second / 3600)+":"+((second % 3600)) / 60+":"+(second % 60))
                        .setContentTitle("Stopwatch is Running")
                        .setSmallIcon(R.drawable.ic_launcher_background);
                startForeground(1001, notification.build());

                if(check.equalsIgnoreCase("stopwatch"))
                    ui.putExtra(StopWatchAcitivity.SECOND, String.valueOf(second-1));
                else ui.putExtra(StopWatchAcitivity.SECOND, String.valueOf(second+1));

                if(second+2 == 0) {
                    running = false;
                    stopSelf();
                }

                if(running) LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(ui);
                handler.postDelayed(this, 1000);
            }
        };
        handler.post(myRunnable);

        return START_STICKY;
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        super.onTaskRemoved(rootIntent);
        handler.removeCallbacks(myRunnable);
        stopSelf();
        stopForeground(true);
        Log.d("second", "onDestroy: Service Destroyed");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        handler.removeCallbacks(myRunnable);
        Log.d("second", "onDestroy: Service Destroyed");
    }
}