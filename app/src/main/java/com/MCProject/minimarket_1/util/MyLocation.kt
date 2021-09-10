package com.MCProject.minimarket_1.util

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import java.io.IOException

class MyLocation(val context: Context, val activity: Activity): OnMapReadyCallback {

    companion object {
        var myLat = 0.0
        var myLon = 0.0
        var gMap: GoogleMap? = null
        val LOCATION_REQUEST_CODE = 101
    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onMapReady(googleMap: GoogleMap?) {
        gMap = googleMap

        Log.i("HEY", "UDP")
        myCheckPermission(gMap)
        Log.i("HEY", "Post UDP")

        //Aggiunta marker
        val myLocation = LatLng(myLat, myLon)
        if (googleMap != null) {
            googleMap.addMarker(MarkerOptions().position(myLocation).title("Your Position"))
            googleMap.moveCamera(CameraUpdateFactory.newLatLng(myLocation))
        }
    }

    /**
     * controlla se l'utente ha dato i permessi necessari all'applicazione
     */
    @RequiresApi(Build.VERSION_CODES.M)
    private fun myCheckPermission(googleMap: GoogleMap?) {
        if(googleMap != null) {
            Log.i("HEY", "Mid1 UDP")
            val permission = ContextCompat.checkSelfPermission(
                context, Manifest.permission.ACCESS_FINE_LOCATION
            )

            if (permission == PackageManager.PERMISSION_GRANTED) {
                Log.i("HEY", "Mid2 UDP")
                googleMap.isMyLocationEnabled = true
                //per ottenere latitudine e longitudine dal gps
                val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)

                fusedLocationClient.lastLocation
                    .addOnSuccessListener { location: Location? ->
                        // Got last known location. In some rare situations this can be null.
                        if (location != null) {
                            Log.i("HEY", "Mid3 UDP")
                            Toast.makeText(
                                context,
                                "Lat: " + location.latitude + " and Lon: " + location.longitude,
                                Toast.LENGTH_LONG
                            ).show()
                            // do something, save it perhaps?
                            myLat = location.latitude
                            myLon = location.longitude
                        }
                    }
            } else {
                Log.i("HEY", "MidNot2 UDP")
                myrequestPermission(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    LOCATION_REQUEST_CODE
                )

            }
        }
        else {
            Log.i("HEY", "gMap == null")
            Toast.makeText(context, "Please, Enable GPS", Toast.LENGTH_LONG).show()
        }
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun myrequestPermission(
        accessFineLocation: String,
        locationRequestCode: Int,
    ) {
        /**
         * il problema è che se faccio dei cambiamenti a questa linea di codice non funziona poi più nulla
         *
         * Soluzione --> spostare l'override di onRequestPermissionResult nella Activity che invoca il fragment
         */
        activity.requestPermissions(arrayOf(accessFineLocation), locationRequestCode)
    }

    fun reverseGeocoding(): String? {
        var geocodeMatches: List<Address>? = null

        try {
            geocodeMatches = Geocoder(context).getFromLocation(myLat, myLon, 1)
        } catch (e: IOException) {
            e.printStackTrace()
            Log.e("HEY", ""+e)
        }

        if (geocodeMatches != null) {
            return geocodeMatches[0].getAddressLine(0)
        }
        return null
    }

    fun geocoding(text: String?): Int{
        if(text != null) {
            var geocodeMatches: List<Address>? = null

            try {
                geocodeMatches = Geocoder(context).getFromLocationName("$text", 1)
            } catch (e: IOException) {
                e.printStackTrace()
                Log.e("HEY", ""+e)
                return -2
            }
            if (geocodeMatches != null) {
                myLat = geocodeMatches[0].latitude
                myLon = geocodeMatches[0].longitude
                return 1
            } else
                return 0
        }
        return -1
    }
}