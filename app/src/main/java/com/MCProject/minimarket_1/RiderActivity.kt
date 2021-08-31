package com.MCProject.minimarket_1

import android.annotation.SuppressLint
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.ImageButton
import android.widget.Switch
import androidx.appcompat.app.AppCompatActivity
import com.MCProject.minimarket_1.access.Login
import com.MCProject.minimarket_1.access.util.FirebaseMessaging
import com.google.firebase.auth.FirebaseAuth

class RiderActivity: AppCompatActivity() {


    lateinit var logoutImgBtn: ImageButton
    val auth = FirebaseAuth.getInstance()

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.i("HEY", "RDIER Activity")
        setContentView(R.layout.rider_activity_main)
        
        logoutImgBtn = findViewById(R.id.exit_imgBtn)

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

        logoutImgBtn.setOnClickListener {
            auth.signOut()
            if(auth.currentUser == null) {
                val intentLogout = Intent(this, Login::class.java)
                startActivity(intentLogout)
            }
        }
    }

    override fun onBackPressed() {
        //super.onBackPressed()
    }
}