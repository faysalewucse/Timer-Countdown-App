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
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

public class CountdownActivity extends AppCompatActivity {

    TextView hour_view, minute_view, seconds_view, hidden_text;
    int broadCast_sec = 0;
    private MyReceiver mReceiver;
    public static final String SECOND = "second";
    private MediaPlayer alarm;
    private boolean media_running = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_countdown);

        hour_view = findViewById(R.id.hour);
        minute_view = findViewById(R.id.minute);
        seconds_view = findViewById(R.id.seconds);
        hidden_text  = findViewById(R.id.hidden_text);

    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onStart() {
        super.onStart();

        Intent countDown_intent = getIntent();
        String second = countDown_intent.getStringExtra("COUNTDOWN_SEC");

        mReceiver = new MyReceiver();
        if(!foregroundServiceRunning()){
            Intent i = new Intent(getApplicationContext(), MyService.class);
            i.putExtra("SECOND", second);
            i.putExtra("SERVICE_NAME", "countdown");
            startService(i);
            LocalBroadcastManager.getInstance(getApplicationContext())
                    .registerReceiver(mReceiver, new IntentFilter(MyService.SERVICE_MESSAGE));
        }
    }

    class MyReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            broadCast_sec = Integer.parseInt(intent.getStringExtra(SECOND));
            Log.d("second", "onReceive: "+ broadCast_sec);
            hidden_text.setText(intent.getStringExtra(SECOND));
            if(intent.getStringExtra(SECOND).equals("0")){
                alarm = MediaPlayer.create(getApplicationContext(), R.raw.alarm);
                alarm.setLooping(true);
                media_running = true;
                alarm.start();
            }
            setTime(seconds_view, minute_view, hour_view, broadCast_sec);
        }
    }

    @SuppressLint("SetTextI18n")
    public void setTime(TextView seconds_view, TextView minute_view, TextView hour_view, int second){
        if(second%60 < 10) seconds_view.setText("0"+ second % 60);
        else seconds_view.setText(String.valueOf(second % 60));
        if((second % 3600) / 60 < 10) minute_view.setText("0"+ (second % 3600) / 60);
        else minute_view.setText(String.valueOf((second % 3600) / 60));
        if(second / 3600 < 10) hour_view.setText("0"+ second / 3600);
        else hour_view.setText(String.valueOf(second / 3600));
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

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if(media_running) {
            media_running = false;
            alarm.stop();
        }
        stopService(new Intent(this, MyService.class));
        LocalBroadcastManager.getInstance(getApplicationContext())
                .unregisterReceiver(mReceiver);
        startActivity(new Intent(this, MainActivity.class));
    }
}