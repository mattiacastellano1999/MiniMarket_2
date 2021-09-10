package com.MCProject.minimarket_1.rider

import android.annotation.SuppressLint
import android.content.res.Configuration
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.os.Bundle
import android.util.Log
import com.MCProject.minimarket_1.gestor.OrderManagerActivity
import com.MCProject.minimarket_1.rider.RiderActivity.Companion.myOrder
import com.google.firebase.firestore.GeoPoint


class DeliveryManagerActivity: OrderManagerActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


    }

    @SuppressLint("SetTextI18n")
    override fun onStart() {
        super.onStart()

        val clientGeocode = getCoordinates(myOrder!!.addrClient)
        val gestorGeocode = getCoordinates(myOrder!!.addrGestor)


        var distance = FloatArray(1)

        Log.i("HEY", "QUI1: "+ myOrder!!.addrClient )
        Log.i("HEY", "QUI:2 "+ myOrder!!.addrGestor )
        Log.i("HEY", "QUI: 3"+ distance )
        Location.distanceBetween(clientGeocode!!.latitude, clientGeocode.longitude, gestorGeocode!!.latitude, gestorGeocode.longitude, distance)

        if(myOrder != null) {
            statusTV.text =
                "Order Name: ${myOrder!!.nome_ordine} \n" +
                "Pay is: ${myOrder!!.prezzo_tot} \n" +
                "Distance Between Market and Client: ${distance[0]}\n" +
                "Delivery Address: ${myOrder!!.addrClient}"
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
}
