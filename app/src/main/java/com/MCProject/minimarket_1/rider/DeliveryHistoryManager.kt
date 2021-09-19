package com.MCProject.minimarket_1.rider

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import android.widget.TextView
import com.MCProject.minimarket_1.R
import com.MCProject.minimarket_1.gestor.OrderManagerActivity
import org.w3c.dom.Text

class DeliveryHistoryManager: OrderManagerActivity() {

    lateinit var dStatus : String
    lateinit var gestore : String
    lateinit var rider : String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        dStatus = intent.extras!!["dStatus"].toString()
        gestore = intent.extras!!["gestore"].toString()
        dStatus = intent.extras!!["dStatus"].toString()
        rider = intent.extras!!["rider"].toString()

    }

    @SuppressLint("SetTextI18n")
    override fun onStart() {
        super.onStart()

        val rider_tv = findViewById<TextView>(R.id.rider_tv)
        rider_tv.visibility = View.GONE
        spinner.visibility = View.GONE

        titleTV.text = "Information About \nDelivery: $orderN"

        statusTV.text = "The Delivery was: $dStatus\n" +
                "Client: $cliente\n" +
                "Gestor: $gestore\n" +
                "Rider: $rider\n"
        cancleBtn.visibility = View.GONE
        confirmBtn.visibility = View.GONE
    }
}
