package com.iodraw.beijingclock;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class MainActivity extends Activity {
    private final Handler handler = new Handler(Looper.getMainLooper());
    private TextView timeTextView;

    private final SimpleDateFormat formatter = new SimpleDateFormat("HH:mm:ss", Locale.CHINA);

    private final Runnable ticker = new Runnable() {
        @Override
        public void run() {
            timeTextView.setText(formatter.format(new Date()));
            handler.postDelayed(this, 1000);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        formatter.setTimeZone(TimeZone.getTimeZone("Asia/Shanghai"));
        setContentView(R.layout.activity_main);
        timeTextView = findViewById(R.id.timeText);
    }

    @Override
    protected void onStart() {
        super.onStart();
        ticker.run();
    }

    @Override
    protected void onStop() {
        super.onStop();
        handler.removeCallbacks(ticker);
    }
}
