package edu.bd.ewu.stopwatch;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    TextView startime, countdown;
    EditText seconds;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        startime    = findViewById(R.id.start_time_btn);
        countdown   = findViewById(R.id.countdown_btn);
        seconds     = findViewById(R.id.second_edittext);

        startime.setOnClickListener(v -> {
            Intent ui = new Intent(getApplicationContext(),StopWatchAcitivity.class);
            startActivity(ui);
        });

    }

    @Override
    protected void onStart() {
        super.onStart();
        if(foregroundServiceRunning()){
            Intent ui = new Intent(getApplicationContext(),StopWatchAcitivity.class);
            startActivity(ui);
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
}