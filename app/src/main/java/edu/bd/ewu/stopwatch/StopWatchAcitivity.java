package edu.bd.ewu.stopwatch;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

public class StopWatchAcitivity extends AppCompatActivity {

    TextView hour_view, minute_view, seconds_view, reset_btn, pauseStart_btn, stop_btn, hidden_text;
    int reset =0, pause = 0, stop =0;
    int broadCast_sec;
    public static final String SECOND = "second";
    private MyReceiver mReceiver;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stop_watch_acitivity);

        hour_view = findViewById(R.id.hour);
        minute_view = findViewById(R.id.minute);
        seconds_view = findViewById(R.id.seconds);
        stop_btn    = findViewById(R.id.stop_btn);
        pauseStart_btn = findViewById(R.id.pauseStart_btn);
        pauseStart_btn.setText("PAUSE");
        reset_btn   = findViewById(R.id.reset_btn);
        hidden_text  = findViewById(R.id.hidden_text);

        Intent i = new Intent(getApplicationContext(), MyService.class);
        pauseStart_btn.setOnClickListener(v -> {
            if(pauseStart_btn.getText().toString().equalsIgnoreCase("PAUSE")){
                pauseStart_btn.setText("START");
                pauseStart_btn.setBackgroundColor(Color.parseColor("#35965f"));
                pause =1;
                stopService(new Intent(this, MyService.class));
                LocalBroadcastManager.getInstance(getApplicationContext())
                        .unregisterReceiver(mReceiver);
            }
            else{
                pauseStart_btn.setText("PAUSE");
                pauseStart_btn.setBackgroundColor(Color.parseColor("#cc12bd"));
                if(pause == 1){
                    pause = 0;
                    i.putExtra("SECOND", hidden_text.getText().toString());
                    i.putExtra("SERVICE_NAME", "stopwatch");
                    startService(i);
                    LocalBroadcastManager.getInstance(getApplicationContext())
                            .registerReceiver(mReceiver, new IntentFilter(MyService.SERVICE_MESSAGE));
                }
                else if(stop == 1 || reset == 1){
                    stop = 0;
                    i.putExtra("SECOND", "0");
                    i.putExtra("SERVICE_NAME", "stopwatch");
                    startService(i);
                    LocalBroadcastManager.getInstance(getApplicationContext())
                            .registerReceiver(mReceiver, new IntentFilter(MyService.SERVICE_MESSAGE));
                }
            }
        });

        reset_btn.setOnClickListener(v -> {
            pauseStart_btn.setText("START");
            pauseStart_btn.setBackgroundColor(Color.parseColor("#35965f"));
            reset = 1;
            stopService(new Intent(this, MyService.class));
            LocalBroadcastManager.getInstance(getApplicationContext())
                    .unregisterReceiver(mReceiver);
            setTime(seconds_view, minute_view, hour_view, 0);
        });

        stop_btn.setOnClickListener(v -> {
            pauseStart_btn.setText("START");
            pauseStart_btn.setBackgroundColor(Color.parseColor("#35965f"));
            stop = 1;
            stopService(new Intent(this, MyService.class));
            LocalBroadcastManager.getInstance(getApplicationContext())
                    .unregisterReceiver(mReceiver);
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onStart() {
        super.onStart();

        mReceiver = new MyReceiver();
        if(!foregroundServiceRunning()){
            Intent i = new Intent(getApplicationContext(), MyService.class);
            i.putExtra("SECOND", seconds_view.getText().toString());
            i.putExtra("SERVICE_NAME", "stopwatch");
            startService(i);
            LocalBroadcastManager.getInstance(getApplicationContext())
                    .registerReceiver(mReceiver, new IntentFilter(MyService.SERVICE_MESSAGE));
        }
    }

    class MyReceiver extends BroadcastReceiver{
        @Override
        public void onReceive(Context context, Intent intent) {
            broadCast_sec = Integer.parseInt(intent.getStringExtra(SECOND));
            //Log.d("second", "onReceive: "+ broadCast_sec);
            hidden_text.setText(intent.getStringExtra(SECOND));
            setTime(seconds_view, minute_view, hour_view, broadCast_sec);
        }
    }

    public boolean foregroundServiceRunning(){
        ActivityManager activityManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo serviceInfo: activityManager.getRunningServices(Integer.MAX_VALUE)){
            if(MyService.class.getName().equals(serviceInfo.service.getClassName())){
                return true;
            }
        }
        return false;
    }

    @SuppressLint("SetTextI18n")
    public void setTime(TextView seconds_view, TextView minute_view, TextView hour_view, int second){
        Log.d("second", "setTime: "+second);
        if(second%60 < 10) seconds_view.setText("0"+ second % 60);
        else seconds_view.setText(String.valueOf(second % 60));
        if((second % 3600) / 60 < 10) minute_view.setText("0"+ (second % 3600) / 60);
        else minute_view.setText(String.valueOf((second % 3600) / 60));
        if(second / 3600 < 10) hour_view.setText("0"+ second / 3600);
        else hour_view.setText(String.valueOf(second / 3600));
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        stopService(new Intent(this, MyService.class));
        LocalBroadcastManager.getInstance(getApplicationContext())
                .unregisterReceiver(mReceiver);
        startActivity(new Intent(this, MainActivity.class));
    }
}