package com.MCProject.minimarket_1.user

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.MCProject.minimarket_1.R
import com.MCProject.minimarket_1.access.Login
import com.MCProject.minimarket_1.access.util.MyLocation
import com.google.android.gms.maps.SupportMapFragment
import com.google.firebase.auth.FirebaseAuth

class UserActivity: AppCompatActivity() {

    val userTrackMapsFragment = UserTrackMapsFragment()
    lateinit var locationBTN: Button
    lateinit var oldOrderBTN: Button
    lateinit var marketListBTN: Button
    lateinit var logoutIMGBTN: ImageButton
    lateinit var cartIMGBTN: ImageButton
    lateinit var welcomeTV: TextView
    val firebaseAuth = FirebaseAuth.getInstance()

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.user_activity_main)


        Log.i("HEY", "MA QUI")
        locationBTN = findViewById(R.id.location_btn)
        logoutIMGBTN = findViewById(R.id.exit_imgBtn)
        cartIMGBTN = findViewById(R.id.cart_imgBtn)
        welcomeTV = findViewById(R.id.welcome_tv)
        oldOrderBTN = findViewById(R.id.oldOrder_btn)
        marketListBTN = findViewById(R.id.marketList_btn)

        welcomeTV.text = "Welcome ${firebaseAuth.currentUser.email}"
        buttonListener()

        val loc = MyLocation(this.applicationContext, this)

        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(loc)
    }

    private fun buttonListener() {

        locationBTN.setOnClickListener {
            locationBTN.visibility = View.GONE
            oldOrderBTN.visibility = View.GONE
            marketListBTN.visibility = View.GONE
             //FindMyLocationButton
            supportFragmentManager.beginTransaction().apply {
                replace(R.id.main_fragment, userTrackMapsFragment)
                commit()
            }
        }

        logoutIMGBTN.setOnClickListener {
            firebaseAuth.signOut()
            if(firebaseAuth.currentUser == null) {
                val intentLogout = Intent(this, Login::class.java)
                startActivity(intentLogout)
            }
        }

        cartIMGBTN.setOnClickListener {
            val intent = Intent(this, CartProductListActivity::class.java)
            startActivity(intent)
        }

        marketListBTN.setOnClickListener {
            locationBTN.visibility = View.GONE
            marketListBTN.visibility = View.GONE
            oldOrderBTN.visibility = View.GONE
            val i = Intent(this, MarketAviableActivity::class.java)
            startActivity(i)
        }

        //when the rider leave the market
        /*leave.setOnClickListener {
            locationBTN.visibility = View.GONE
            //FindMyLocationButton
            supportFragmentManager.beginTransaction().apply {
                replace(R.id.chat_fragment, chatFragment)
                commit()
            }
        }*/

    }

    /*override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == 101){
            if (grantResults.isEmpty() || grantResults[0] != PackageManager.PERMISSION_GRANTED){
                Toast.makeText(
                    this,
                    "Unable to show location - permission required" ,
                    Toast.LENGTH_LONG
                ).show()
            } else {
                val frag = supportFragmentManager.findFragmentById(R.id.main_fragment)
                supportFragmentManager.beginTransaction()
                    .detach(frag!!)
                    .attach(frag)
                    .commit()
            }
        }
    }*/

    /**
     * for API level 5 and greater
     * Azioni da fare quando viene premuto il tasto di ritorno alla schermata precedente
     */
    override fun onBackPressed() {
        backButtonPressed()
            //super.onBackPressed()
    }

    private fun backButtonPressed(): Boolean {
        val frag = supportFragmentManager.findFragmentById(R.id.main_fragment)
        return if( frag != null ){
            supportFragmentManager.beginTransaction()
                .remove(frag)
                .commit()
            locationBTN.visibility = View.VISIBLE
            marketListBTN.visibility = View.VISIBLE
            oldOrderBTN.visibility = View.VISIBLE
            true
        } else
            false
    }

    /*@RequiresApi(Build.VERSION_CODES.M)
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
                this, Manifest.permission.ACCESS_FINE_LOCATION
            )

            if (permission == PackageManager.PERMISSION_GRANTED) {
                Log.i("HEY", "Mid2 UDP")
                googleMap.isMyLocationEnabled = true
                //per ottenere latitudine e longitudine dal gps
                val fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

                fusedLocationClient.lastLocation
                    .addOnSuccessListener { location: Location? ->
                        // Got last known location. In some rare situations this can be null.
                        if (location != null) {
                            Log.i("HEY", "Mid3 UDP")
                            Toast.makeText(
                                this,
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
            Toast.makeText(this, "Please, Enable GPS", Toast.LENGTH_LONG).show()
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
        requestPermissions(arrayOf(accessFineLocation), locationRequestCode)

    }*/
}