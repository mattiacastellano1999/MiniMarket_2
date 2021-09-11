package com.MCProject.minimarket_1.rider

import android.annotation.SuppressLint
import android.content.res.Configuration
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.RelativeLayout
import android.widget.TextView
import com.MCProject.minimarket_1.gestor.OrderManagerActivity
import com.MCProject.minimarket_1.rider.RiderActivity.Companion.myOrder
import com.google.firebase.firestore.GeoPoint
import android.R
import android.content.Intent
import android.widget.Toast
import com.MCProject.minimarket_1.MainActivity
import com.MCProject.minimarket_1.gestor.GestorActivity
import com.MCProject.minimarket_1.user.UserActivity
import com.MCProject.minimarket_1.util.Order


class DeliveryManagerActivity: OrderManagerActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


    }

    @SuppressLint("SetTextI18n")
    override fun onStart() {
        super.onStart()

        spinner.visibility = View.GONE
        val riderTv = findViewById<TextView>(com.MCProject.minimarket_1.R.id.rider_tv)
        riderTv.visibility = View.GONE

        val layoutParams = statusTV.layoutParams as RelativeLayout.LayoutParams
        layoutParams.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE)
        statusTV.layoutParams = layoutParams

        titleTV.text = "Delivery Request \nFor ${myOrder!!.nome_ordine}"

        val clientGeocode = getCoordinates(myOrder!!.addrClient)
        val gestorGeocode = getCoordinates(myOrder!!.addrGestor)


        var distance = FloatArray(1)

        Log.i("HEY", "QUI1: "+ myOrder!!.addrClient )
        Log.i("HEY", "QUI:2 "+ myOrder!!.addrGestor )
        Log.i("HEY", "QUI: 3"+ distance )
        Location.distanceBetween(clientGeocode!!.latitude, clientGeocode.longitude, gestorGeocode!!.latitude, gestorGeocode.longitude, distance)

        if(myOrder != null) {
            statusTV.text =
                "Order Name: ${myOrder!!.nome_ordine} \n\n" +
                "Pay is: ${myOrder!!.prezzo_tot} \n\n" +
                "Distance Between Market and Client: ${distance[0]/1000} Km\n\n" +
                "Delivery Address: ${myOrder!!.addrClient}"
        }

        confirmBtn.setOnClickListener {
            // set the rider status = accepted
            sendDeliveryResponseToGestor(getString(com.MCProject.minimarket_1.R.string.accepted))
        }

        cancleBtn.setOnClickListener {
            // set the rider status = refused
            myOrder!!.rider = "null"
            sendDeliveryResponseToGestor(getString(com.MCProject.minimarket_1.R.string.rider_status_NA))
            val username = MainActivity.auth.currentUser.displayName
            when {
                username.equals("utenti") -> {
                    val intent = Intent(this, UserActivity::class.java)
                    this@DeliveryManagerActivity.startActivity(intent)
                }
                username.equals("gestori") -> {
                    val intent = Intent(this, GestorActivity::class.java)
                    this@DeliveryManagerActivity.startActivity(intent)
                }
                username.equals("riders") -> {
                    val intent = Intent(this, RiderActivity::class.java)
                    this@DeliveryManagerActivity.startActivity(intent)
                }
            }
        }
    }



    private fun getCoordinates(addr: String): GeoPoint? {

        val coder = Geocoder(this)

        var p1: GeoPoint? = null

        try {
            val address = coder.getFromLocationName(addr, 1)
            if (address != null) {
                val location: Address = address[0]

                p1 = GeoPoint(
                    location.latitude ,
                    location.longitude
                )
            }
        } catch (e: Exception) {

        }
        return p1
    }

    private fun sendDeliveryResponseToGestor(value: String) {
        MainActivity.frO.updateOrder(this, myOrder!!, value, myOrder!!.rider)

        //elimino la notifica dal db
        MainActivity.frM.deleteFromDB(
            this,
            this.getString(com.MCProject.minimarket_1.R.string.antecedente_notification) + MainActivity.mail,
            this.getString(com.MCProject.minimarket_1.R.string.notification_path)
        )

        if(value == getString(com.MCProject.minimarket_1.R.string.accepted)) {
            //creo i canali di chat tra
            //rider e gestore
            //rider e cliente
        }
    }
}
