package com.MCProject.minimarket_0.gestor

import android.os.Bundle
import android.os.PersistableBundle
import android.widget.Spinner
import androidx.appcompat.app.AppCompatActivity
import com.MCProject.minimarket_1.R

class OrderManagerActivity: AppCompatActivity() {

    lateinit var spinner: Spinner

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.gestor_order_activity)

        spinner = findViewById(R.id.rider_ed)
    }

    override fun onStart() {
        super.onStart()


    }
}
