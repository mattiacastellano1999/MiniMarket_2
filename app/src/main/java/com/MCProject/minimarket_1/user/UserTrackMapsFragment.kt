package com.MCProject.minimarket_1.user

import androidx.fragment.app.Fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.*
import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.util.Log
import android.widget.*
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.MCProject.minimarket_1.R
import com.google.android.gms.location.LocationServices
import java.io.IOException


@Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
class UserTrackMapsFragment : Fragment(), OnMapReadyCallback {

    val LOCATION_REQUEST_CODE = 101
    var gMap: GoogleMap? = null
    //var addrED: EditText? = null
    private var myLat = 0.0
    private var myLon = 0.0

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?,
    ): View? {
        return inflater.inflate(R.layout.fragment_user_maps, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment?.getMapAsync(this)
    }

    override fun onMapReady(googleMap: GoogleMap?) {
        gMap = googleMap

        myCheckPermission(gMap)

        //Aggiunta marker
        val myLocation = LatLng(myLat, myLon)
        if (googleMap != null) {
            googleMap.addMarker(MarkerOptions().position(myLocation).title("Your Position"))
            googleMap.moveCamera(CameraUpdateFactory.newLatLng(myLocation))
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        //addrED = view?.findViewById(R.id.addr_ed)
        val trackRiderBTN: Button? = view?.findViewById(R.id.showRider_btn)

        if (trackRiderBTN != null) {
            trackRiderBTN.setOnClickListener {
                //insert marker corresponding Rider Position
            }
        }

    }

    /**
     * controlla se l'utente ha dato i permessi necessari all'applicazione
     */
    private fun myCheckPermission(googleMap: GoogleMap?) {
        if(googleMap != null) {
            val permission = ContextCompat.checkSelfPermission(
                    view!!.context, Manifest.permission.ACCESS_FINE_LOCATION
            )

            if (permission == PackageManager.PERMISSION_GRANTED) {
                googleMap.isMyLocationEnabled = true
                //per ottenere latitudine e longitudine dal gps
                val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)

                fusedLocationClient.lastLocation
                        .addOnSuccessListener { location: Location? ->
                            // Got last known location. In some rare situations this can be null.
                            if (location != null) {
                                Toast.makeText(
                                        view!!.context,
                                        "Lat: " + location.latitude + " and Lon: " + location.longitude,
                                        Toast.LENGTH_LONG
                                ).show()
                                // do something, save it perhaps?
                                myLat = location.latitude
                                myLon = location.longitude

                                reverseGeocoding()
                            }
                        }
            } else {
                requestPermission(
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        LOCATION_REQUEST_CODE
                )

            }
        }
        else {
            Toast.makeText(view!!.context, "Please, Enable GPS", Toast.LENGTH_LONG).show()
        }
    }

    private fun requestPermission(
        accessFineLocation: String,
        locationRequestCode: Int,
    ) {
        /**
         * il problema è che se faccio dei cambiamenti a questa linea di codice non funziona poi più nulla
         *
         * Soluzione --> spostare l'override di onRequestPermissionResult nella Activity che invoca il fragment
         */
        ActivityCompat.requestPermissions(context as Activity, arrayOf(accessFineLocation), locationRequestCode)

    }



    /**
     * funzione che dalle coordinate risale alla località
     */
    private fun reverseGeocoding() {
        var geocodeMatches: List<Address>? = null

        try {
            geocodeMatches = Geocoder(context).getFromLocation(myLat, myLon, 1)
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }
}