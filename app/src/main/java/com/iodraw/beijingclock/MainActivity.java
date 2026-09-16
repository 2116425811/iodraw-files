package com.iodraw.beijingclock;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.TypedValue;
import android.view.Gravity;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class MainActivity extends Activity {
    private final Handler handler = new Handler(Looper.getMainLooper());
    private final SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss", Locale.CHINA);
    private TextView clockView;

    private final Runnable tick = new Runnable() {
        @Override
        public void run() {
            clockView.setText(timeFormat.format(new Date()));
            handler.postDelayed(this, 1000);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        timeFormat.setTimeZone(TimeZone.getTimeZone("Asia/Shanghai"));

        clockView = new TextView(this);
        clockView.setBackgroundColor(Color.parseColor("#111111"));
        clockView.setTextColor(Color.parseColor("#F5F5F5"));
        clockView.setGravity(Gravity.CENTER);
        clockView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 56);
        clockView.setLetterSpacing(0.08f);

        setContentView(clockView);
    }

    @Override
    protected void onStart() {
        super.onStart();
        tick.run();
    }

    @Override
    protected void onStop() {
        super.onStop();
        handler.removeCallbacks(tick);
    }
}
