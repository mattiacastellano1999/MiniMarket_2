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
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.util.Log
import android.widget.*
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.MCProject.minimarket_1.R
import com.MCProject.minimarket_1.access.Loading
import com.MCProject.minimarket_1.access.util.FirestoreRequest
import com.MCProject.minimarket_1.access.util.MarketList
import com.google.android.gms.location.LocationServices
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
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

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        //addrED = view?.findViewById(R.id.addr_ed)
        val trackRiderBTN: Button? = view?.findViewById(R.id.showRider_btn)

        if (trackRiderBTN != null) {
            trackRiderBTN.setOnClickListener {
                //insert marker corresponding Rider Position
            }
        /*confirmBTN.setOnClickListener {
                val load = Loading(this.requireActivity())
                load.startLoading()

                val db = FirebaseFirestore.getInstance()
                val auth = FirebaseAuth.getInstance()
                val imgDb = FirebaseStorage.getInstance()
                val collection = auth.currentUser.displayName
                val mail = auth.currentUser.email
                MarketList
                    .getAllMarket(this.requireActivity(), myLat, myLon, FirestoreRequest(db, auth, imgDb, collection, mail))
                    .addOnCompleteListener {
                        load.stopLoadingDialog()
                        val i = Intent(context, MarketAviableActivity::class.java)
                        startActivity(i)
                    }
                    .addOnFailureListener {
                        load.stopLoadingDialog()
                        Toast.makeText(this.context, "Error during Markets ordering", Toast.LENGTH_SHORT).show()
                    }
            }*/
        }

    }

    /**
     * controlla se l'utente ha dato i permessi necessari all'applicazione
     */
    private fun myCheckPermission(googleMap: GoogleMap?) {
        if(googleMap != null) {
            Log.i("HEY", "Mid1 UDP")
            val permission = ContextCompat.checkSelfPermission(
                    view!!.context, Manifest.permission.ACCESS_FINE_LOCATION
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
                Log.i("HEY", "MidNot2 UDP")
                requestPermission(
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        LOCATION_REQUEST_CODE
                )

            }
        }
        else {
            Log.i("HEY", "gMap == null")
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
    var Address1: String? = null
    private fun reverseGeocoding() {
        var geocodeMatches: List<Address>? = null


        Log.i("HEY", "geocoding")

        try {
            geocodeMatches = Geocoder(context).getFromLocation(myLat, myLon, 1)
        } catch (e: IOException) {
            e.printStackTrace()
            Log.e("HEY", ""+e)
        }
        Log.i("HEY", "geocoding : $geocodeMatches")

        if (geocodeMatches != null /*&& addrED != null*/) {/*
            addrED!!.text.clear()
            Address1 = geocodeMatches[0].getAddressLine(0)
            Log.i("HEY", "Addr1: $Address1")
            addrED!!.append(Address1)*/
        }
    }
}