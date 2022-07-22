package edu.bd.ewu.stopwatch;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ActivityManager;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.service.notification.StatusBarNotification;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    TextView startime, countdown, error_msg, exit_btn;
    EditText seconds, minutes, hours;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        startime    = findViewById(R.id.start_time_btn);
        countdown   = findViewById(R.id.countdown_btn);
        seconds     = findViewById(R.id.seconds);
        minutes     = findViewById(R.id.minute);
        hours       = findViewById(R.id.hour);
        error_msg   = findViewById(R.id.error_msg);
        exit_btn   = findViewById(R.id.exit_button);

        startime.setOnClickListener(v -> {
            Intent ui = new Intent(getApplicationContext(),StopWatchAcitivity.class);
            startActivity(ui);
        });

        countdown.setOnClickListener(v -> {
            if(seconds.getText().toString().equals("")
                    || minutes.getText().toString().equals("")
                    || hours.getText().toString().equals("")){
                error_msg.setVisibility(View.VISIBLE);
                error_msg.setText("Field Cannot be Empty. Enter 0 Instead!!!");
            }
            else if(Integer.parseInt(seconds.getText().toString()) > 59){
                error_msg.setVisibility(View.VISIBLE);
                error_msg.setText("Second Should not be greater then 59");
            }
            else if(Integer.parseInt(minutes.getText().toString()) > 59){
                error_msg.setVisibility(View.VISIBLE);
                error_msg.setText("Minutes Should not be greater then 59");
            }
            else if(seconds.getText().toString().equals("0")
                    && minutes.getText().toString().equals("0")
                    && hours.getText().toString().equals("0")){
                error_msg.setVisibility(View.VISIBLE);
                error_msg.setText("All Field Cannot be Zero!!!");
            }
            else{
                int second = 0;

                second += Integer.parseInt(seconds.getText().toString());
                second += Integer.parseInt(minutes.getText().toString()) * 60;
                second += Integer.parseInt(hours.getText().toString()) * 3600;

                Intent countDown_intent = new Intent(getApplicationContext(), CountdownActivity.class);
                countDown_intent.putExtra("COUNTDOWN_SEC", String.valueOf(second));
                startActivity(countDown_intent);
            }
        });

        exit_btn.setOnClickListener(v -> {
            finish();
            System.exit(0);
        });

    }
}