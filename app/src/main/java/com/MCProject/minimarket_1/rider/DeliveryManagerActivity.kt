package com.MCProject.minimarket_1.rider

import android.annotation.SuppressLint
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.view.View
import com.MCProject.minimarket_1.rider.RiderActivity.Companion.myOrder
import com.google.firebase.firestore.GeoPoint
import android.content.Intent
import android.os.Build
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.MCProject.minimarket_1.MainActivity
import com.MCProject.minimarket_1.MainActivity.Companion.frM
import com.MCProject.minimarket_1.MainActivity.Companion.frO
import com.MCProject.minimarket_1.MainActivity.Companion.frR
import com.MCProject.minimarket_1.MainActivity.Companion.homeListener
import com.MCProject.minimarket_1.R
import com.MCProject.minimarket_1.access.Loading
import com.MCProject.minimarket_1.gestor.GestorActivity
import com.MCProject.minimarket_1.user.UserActivity


class DeliveryManagerActivity: AppCompatActivity() {

    lateinit var logoutImgBtn: ImageButton
    lateinit var homeImgBtn: ImageButton
    lateinit var cancleBtn: Button
    lateinit var confirmBtn: Button
    lateinit var titleTV: TextView
    lateinit var statusTV: TextView

    lateinit var cliente : String
    lateinit var orderN : String
    lateinit var riderAviable: ArrayList<String>
    lateinit var riderStatus: String
    lateinit var spinner: Spinner

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Log.i("HEY", "Delivery Manager Activity")
        setContentView(com.MCProject.minimarket_1.R.layout.gestor_order_activity)

        cliente = intent.extras!!["testo"].toString()
        orderN = intent.extras!!["nome_ordine"].toString()
        riderStatus = intent.extras!!["rStatus"].toString()


        Log.i("HEY", "Extra: " + cliente)

        spinner = findViewById(com.MCProject.minimarket_1.R.id.rider_ed)
        homeImgBtn = findViewById(com.MCProject.minimarket_1.R.id.home_imgBtn)
        logoutImgBtn = findViewById(com.MCProject.minimarket_1.R.id.exit_imgBtn)
        cancleBtn = findViewById(com.MCProject.minimarket_1.R.id.cancle_btn)
        confirmBtn = findViewById(com.MCProject.minimarket_1.R.id.confirm_btn)
        titleTV = findViewById(com.MCProject.minimarket_1.R.id.title_tv)
        statusTV = findViewById(com.MCProject.minimarket_1.R.id.status_tv)
    }

    @SuppressLint("SetTextI18n")
    @Synchronized
    override fun onStart() {
        super.onStart()

        spinner.visibility = View.GONE
        val riderTv = findViewById<TextView>(com.MCProject.minimarket_1.R.id.rider_tv)
        riderTv.visibility = View.GONE

        val layoutParams = statusTV.layoutParams as RelativeLayout.LayoutParams
        layoutParams.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE)
        statusTV.layoutParams = layoutParams

        titleTV.text = "Delivery Request \nFor ${myOrder!!.ordine}"

        val clientGeocode = getCoordinates(myOrder!!.addrClient)
        val gestorGeocode = getCoordinates(myOrder!!.addrGestor)

        var distance = FloatArray(3)

        Log.i("HEY", "QUI1: "+ myOrder!!.addrClient )
        Log.i("HEY", "QUI:2 "+ myOrder!!.addrGestor )
        Log.i("HEY", "QUI: 3"+ distance )
        Location.distanceBetween(
                clientGeocode!!.latitude,
                clientGeocode.longitude,
                gestorGeocode!!.latitude,
                gestorGeocode.longitude,
                distance
        )

        if(myOrder != null) {
            statusTV.text =
                "Order Name: ${myOrder!!.ordine} \n\n" +
                        "Pay is: ${myOrder!!.prezzo_tot} \n\n" +
                        "Distance Between Market and Client: ${distance[0] / 1000} km\n\n" +
                        "Delivery Address: ${myOrder!!.addrClient}"


            confirmBtn.setOnClickListener {
                // set the rider status = accepted
                updateOrder(getString(R.string.rider_status_accepted))
                val entry = hashMapOf<String, Any>(
                    "status" to 0,
                    "nome rider" to RiderActivity.riderName,
                    "email" to RiderActivity.riderEmail,
                    "cognome rider" to RiderActivity.riderSurname
                )
                frR.updateRider(this, "/profili/riders/dati", MainActivity.mail, entry)
                goHome()
            }

            cancleBtn.setOnClickListener {
                // set the rider status = refused
                myOrder!!.rider = "null"
                updateOrder(getString(R.string.rider_status_NA))
                goHome()
            }
        }
        homeListener(this, homeImgBtn)
    }

    fun goHome(){
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

    private fun getCoordinates(addr: String): GeoPoint? {
        val loading = Loading(this)
        loading.startLoading()

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
            Log.e("HEY", "Exception: ${e.message}")
        }
        loading.stopLoadingDialog()
        return p1
    }

    private fun updateOrder(value: String) {
        myOrder!!.riderStatus = value
        frO.updateOrder(this, myOrder!!).addOnCompleteListener {
            if(it.isSuccessful) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    frM.deleteFromDB(
                        this,
                        getString(R.string.antecedente_notification) + MainActivity.mail,
                        getString(R.string.notification_path)
                    )
                } else {
                    Log.i("HEY", "Error Order Sending to RIder")
                    Toast.makeText(
                        this,
                        getString(R.string.AndroidVersionOld),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }

        //elimino la notifica dal db
        frM.deleteFromDB(
            this,
            getString(R.string.antecedente_notification)+MainActivity.mail,
            getString(R.string.notification_path)
        )
    }

    override fun onBackPressed() {
        goHome()
    }
}
