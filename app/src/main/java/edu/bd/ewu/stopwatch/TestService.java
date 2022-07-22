package edu.bd.ewu.stopwatch;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.IBinder;
import android.os.ResultReceiver;

import androidx.annotation.Nullable;

public class TestService extends Service {

    public static final String ACTION_GET_DATA = "ACTION_GET_DATA";
    public static final String EXTRA_RECEIVER = "EXTRA_RECEIVER";
    public static final String EXTRA_DATA = "EXTRA_DATA";

    public static final int RESULT_OK = 1;
    public static final int RESULT_FAILED = 0;

    private final BroadcastReceiver onGetDataReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // Send result data to the activity
            ResultReceiver receiver = intent.getParcelableExtra(EXTRA_RECEIVER);
            Bundle resultData = new Bundle();
            // The data of the Service that the Activity want to access.
            int myServiceVar = 100;
            resultData.putInt(EXTRA_DATA, myServiceVar);
            receiver.send(RESULT_OK, resultData);
        }
    };

    @Override
    public void onCreate() {
        super.onCreate();
        registerReceiver(onGetDataReceiver, new IntentFilter(ACTION_GET_DATA));
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // TODO: Your code logic goes here.
        return Service.START_STICKY;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
