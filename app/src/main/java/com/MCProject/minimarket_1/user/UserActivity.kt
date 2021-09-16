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
import com.MCProject.minimarket_1.MainActivity
import com.MCProject.minimarket_1.R
import com.MCProject.minimarket_1.access.Login
import com.MCProject.minimarket_1.rider.ChatRider
import com.MCProject.minimarket_1.rider.RiderActivity
import com.MCProject.minimarket_1.rider.RiderActivity.Companion.myOrder
import com.MCProject.minimarket_1.rider.RiderActivity.Companion.orderName
import com.MCProject.minimarket_1.util.FirebaseMessaging
import com.MCProject.minimarket_1.util.MyLocation
import com.google.android.gms.maps.SupportMapFragment
import com.google.firebase.auth.FirebaseAuth

class UserActivity: AppCompatActivity() {

    val userTrackMapsFragment = UserTrackMapsFragment()
    lateinit var locationBTN: Button
    lateinit var oldOrderBTN: Button
    lateinit var marketListBTN: Button
    lateinit var chatUserBTN: Button
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
        chatUserBTN = findViewById(R.id.chatUser_btn)

        welcomeTV.text = "Welcome\n${firebaseAuth.currentUser.email}"
    }

    override fun onStart() {
        super.onStart()
        locationBTN.visibility = View.VISIBLE
        oldOrderBTN.visibility = View.VISIBLE
        marketListBTN.visibility = View.VISIBLE
        chatUserBTN.visibility = View.GONE

        MainActivity.frO.getAllOrder(RiderActivity.orderList, this)
            .addOnCompleteListener {
                for (doc in it.result) {
                    Log.i("HEY", "DOC::: "+doc.data)
                    if( doc["riderStatus"] == getString(R.string.rider_status_accepted) ) {
                        if( doc["orderStatus"] == getString(R.string.order_status_delivering) ) {
                            if( doc["cliente"] == MainActivity.mail) {
                                orderName = doc["ordine"].toString()
                                myOrder = MainActivity.frO.parseOrder(doc)
                                buttonListener()
                                break
                            }
                        }
                    }
                }
            }

        buttonListener()

        val loc = MyLocation(this.applicationContext, this)

        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(loc)


        //Check some notification
        Log.i("HEY", "Check Notify")
        val fm = FirebaseMessaging(MainActivity.mail, this)
        fm.addRealtimeUpdate(this, "user")

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
            if (firebaseAuth.currentUser == null) {
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

        if ( orderName.isNotEmpty() ) {
            if( myOrder!!.riderStatus == getString(R.string.rider_status_accepted) &&
                myOrder!!.orderStatus == getString(R.string.order_status_delivering)
            ) {
                chatUserBTN.visibility = View.VISIBLE
                chatUserBTN.setOnClickListener {
                    val intent = Intent(this, ChatUser::class.java)
                    startActivity(intent)
                }
            }
        } else {
            chatUserBTN.visibility = View.GONE
        }
    }

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
}