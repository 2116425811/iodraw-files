package com.iodraw.beijingclock

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone

class MainActivity : AppCompatActivity() {
    private val handler = Handler(Looper.getMainLooper())
    private lateinit var timeTextView: TextView

    private val formatter = SimpleDateFormat("HH:mm:ss", Locale.CHINA).apply {
        timeZone = TimeZone.getTimeZone("Asia/Shanghai")
    }

    private val ticker = object : Runnable {
        override fun run() {
            timeTextView.text = formatter.format(Date())
            handler.postDelayed(this, 1000)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        timeTextView = findViewById(R.id.timeText)
    }

    override fun onStart() {
        super.onStart()
        ticker.run()
    }

    override fun onStop() {
        super.onStop()
        handler.removeCallbacks(ticker)
    }
}
