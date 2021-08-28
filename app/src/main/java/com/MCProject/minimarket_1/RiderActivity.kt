package com.MCProject.minimarket_1

import android.annotation.SuppressLint
import android.app.NotificationManager
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.widget.Switch
import androidx.appcompat.app.AppCompatActivity
import com.MCProject.minimarket_1.access.util.FirebaseMessaging

class RiderActivity: AppCompatActivity() {

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.i("HEY", "RDIER Activity")
        setContentView(R.layout.rider_activity_main)
    }

    override fun onStart() {
        super.onStart()

        //Check some notification
        Log.i("HEY", "Check Notify")
        val fm = FirebaseMessaging(MainActivity.mail)
        fm.addRealtimeUpdate(this)

        val switch = findViewById<Switch>(R.id.switch1)

        switch.setOnClickListener {

        }
    }

    override fun onBackPressed() {
        //super.onBackPressed()
    }
}