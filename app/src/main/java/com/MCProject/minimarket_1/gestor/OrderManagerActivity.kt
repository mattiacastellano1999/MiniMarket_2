package com.MCProject.minimarket_1.gestor

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.MCProject.minimarket_1.MainActivity
import com.MCProject.minimarket_1.R
import com.MCProject.minimarket_1.MainActivity.Companion.auth
import com.MCProject.minimarket_1.MainActivity.Companion.frM
import com.MCProject.minimarket_1.MainActivity.Companion.frO
import com.MCProject.minimarket_1.rider.RiderActivity
import com.MCProject.minimarket_1.access.Login
import com.MCProject.minimarket_1.util.Order
import com.MCProject.minimarket_1.user.UserActivity


open class OrderManagerActivity : AppCompatActivity() {

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

        Log.i("HEY", "Order Manager Activity")
        setContentView(R.layout.gestor_order_activity)

        cliente = intent.extras!!["testo"].toString()
        orderN = intent.extras!!["nome_ordine"].toString()
        riderStatus = intent.extras!!["rStatus"].toString()


        Log.i("HEY", "Extra: " + cliente)

        spinner = findViewById(R.id.rider_ed)
        homeImgBtn = findViewById(R.id.home_imgBtn)
        logoutImgBtn = findViewById(R.id.exit_imgBtn)
        cancleBtn = findViewById(R.id.cancle_btn)
        confirmBtn = findViewById(R.id.confirm_btn)
        titleTV = findViewById(R.id.title_tv)
        statusTV = findViewById(R.id.status_tv)
    }

    @SuppressLint("SetTextI18n")
    override fun onStart() {
        super.onStart()

        buttonListener()
        //orderList = ArrayList<Order>()
        titleTV.text = "Rider Assignment \nFor Order: $orderN"

        statusTV.text = "Rider Status: $riderStatus"

        if(cliente.isNotEmpty()){
            Log.i("HEY", "Not Empty")
            //significa che e' stata chiamata in seguito ad aver premuto su un determinato ordine
            //in cliente ho la mail del cliente che ha effettuato l'ordine
            riderAviable = ArrayList()
            frM.getAllRiderAviable(this, riderAviable)
                    .addOnCompleteListener {
                        if(it.isSuccessful){
                            Log.i("HEY", "RIders Aviable:" + riderAviable)
                            populateSpinner()
                        }
                    }
        } else{
            //
        }
    }

    private fun populateSpinner() {
        val spinnerArrayAdapter: ArrayAdapter<String> = ArrayAdapter<String>(
                this, android.R.layout.simple_spinner_item, riderAviable
        )
        spinnerArrayAdapter
                .setDropDownViewResource(
                        android.R.layout.simple_spinner_dropdown_item
                )

        spinner.adapter = spinnerArrayAdapter
    }

    private fun buttonListener(){
        MainActivity.logoutListener(this, logoutImgBtn)

        MainActivity.homeListener(this, homeImgBtn)

        confirmBtn.setOnClickListener {
            sendDeliveryRequestToRider()
        }

        cancleBtn.setOnClickListener {
            val username = auth.currentUser.displayName
            when {
                username.equals("utenti") -> {
                    val intent = Intent(this, UserActivity::class.java)
                    this@OrderManagerActivity.startActivity(intent)
                }
                username.equals("gestori") -> {
                    val intent = Intent(this, GestorActivity::class.java)
                    this@OrderManagerActivity.startActivity(intent)
                }
                username.equals("riders") -> {
                    val intent = Intent(this, RiderActivity::class.java)
                    this@OrderManagerActivity.startActivity(intent)
                }
            }
        }
    }

    private fun sendDeliveryRequestToRider() {
        val rider = spinner.selectedItem.toString()
        val orderList = ArrayList<Order>()
        frO.getAllOrder(orderList, this)
                .addOnCompleteListener {@Synchronized

                if ( it.isSuccessful) {
                    if( !it.result.isEmpty) {@Synchronized
                        for (doc in it.result) {
                            Log.i("HEY", "Doc: "+doc.data)
                            orderList.add(frO.parseOrder(doc))
                        }

                        if (orderList.size < 1) {
                            Log.e("HEY", "Error: Order List Empty")
                            Toast.makeText(this, "Error: Order List Empty", Toast.LENGTH_SHORT).show()
                        } else {
                            Log.d("HEY", "Order List Rider: " + orderList[0].rider)
                            orderList.forEach { neworder ->
                                if (neworder.ordine == orderN) {
                                    if (neworder.riderStatus == getString(R.string.rider_status_NA)) {
                                        //rider non ancora assegnato -> mando richiesta al rider
                                        neworder.orderStatus =  getString(R.string.order_status_working)
                                        neworder.riderStatus = getString(R.string.requestSended)
                                        neworder.rider = rider
                                        frO.updateOrder(this, neworder).addOnCompleteListener {
                                            if(it.isSuccessful) {
                                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                                    val newMessage = mapOf<String, String>(
                                                        "gestore" to neworder.proprietario,
                                                        "numero_ordine" to neworder.ordine,
                                                        "cliente" to neworder.cliente,
                                                        "Testo" to "The Gestor: ${neworder.proprietario} require your services!"
                                                    )
                                                    frO.sendNotification(
                                                        this,
                                                        neworder.rider,
                                                        newMessage
                                                    ).addOnCompleteListener {
                                                        //reload activity
                                                        val intent = Intent(this, OrderList::class.java)
                                                        startActivity(intent)
                                                    }
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
                                    }
                                    if(neworder.riderStatus == getString(R.string.requestSended)){
                                        //la richiesta di delivery è stata mandata ad un rider, il quale deve rispondere
                                        Toast.makeText(this, "Delivery Request already sent!\n" +
                                                "Just Wait the Rider reply", Toast.LENGTH_SHORT).show()
                                    } else {
                                        //richiesta mandata e accettata dal rider. Il pacco è in consegna
                                        Toast.makeText(this, "Delivery Request Accepted!\n" +
                                                "Just Wait the Rider Delivery", Toast.LENGTH_SHORT).show()
                                    }
                                }
                            }
                        }
                    } else {
                        Log.e("HEY", "Error with Path")
                    }
                } else {
                    Log.e("HEY", "Error Firetore Marker Reading_0")
                }
        }
    }
}
