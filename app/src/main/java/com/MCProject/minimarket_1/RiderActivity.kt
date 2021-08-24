package com.MCProject.minimarket_1

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

class RiderActivity: AppCompatActivity() {

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.rider_activity_main)
    }


        override fun onBackPressed() {
        //super.onBackPressed()
    }
}