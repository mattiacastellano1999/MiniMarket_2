package com.MCProject.minimarket_0.gestor

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.MCProject.minimarket_1.R
import com.MCProject.minimarket_1.MainActivity.Companion.auth
import com.MCProject.minimarket_1.MainActivity.Companion.frM
import com.MCProject.minimarket_1.MainActivity.Companion.frO
import com.MCProject.minimarket_1.MainActivity.Companion.mail
import com.MCProject.minimarket_1.RiderActivity
import com.MCProject.minimarket_1.access.Login
import com.MCProject.minimarket_1.access.util.Order
import com.MCProject.minimarket_1.gestor.GestorActivity
import com.MCProject.minimarket_1.user.UserActivity


class OrderManagerActivity(var order: Order?) : AppCompatActivity() {

    constructor(): this(null)

    lateinit var logoutImgBtn: ImageButton
    lateinit var homeImgBtn: ImageButton
    lateinit var cancleBtn: Button
    lateinit var confirmBtn: Button
    lateinit var titleTV: TextView
    lateinit var statusTV: TextView

    lateinit var cliente : String
    lateinit var orderN : String
    lateinit var riderAviable: ArrayList<String>
    //lateinit var orderList: ArrayList<Order>
    lateinit var spinner: Spinner

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Log.i("HEY", "Order Manager Activity")
        setContentView(R.layout.gestor_order_activity)

        cliente = intent.extras!!["testo"].toString()
        orderN = intent.extras!!["nome_ordine"].toString()
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
        if(order != null) {
            //vuol dire che la activity è stata chiamata da UnusedOrderManager
            statusTV.text = "Rider Status: " + order!!.riderStatus
        } else {
            //la activity è stata chiamata da Notification
            statusTV.text = "Rider Status: not assigned"
            order = Order(orderN, 0.0, "", "", "", "", HashMap())
        }

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
        logoutImgBtn.setOnClickListener {
            auth.signOut()
            if(auth.currentUser == null) {
                val intentLogout = Intent(this, Login::class.java)
                startActivity(intentLogout)
            }
        }

        homeImgBtn.setOnClickListener {
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
        if(order!!.prezzo_tot == 0.0) {
            val orderList = ArrayList<Order>()
            frO.getAllOrder(mail, orderList, this)
                    .addOnCompleteListener {
                        if (orderList.size < 1) {
                            Log.e("HEY", "Error: Order List Empty")
                            Toast.makeText(this, "Error: Order List Empty", Toast.LENGTH_SHORT).show()
                        } else {
                            Log.d("HEY", "Order List Rider: " + orderList[0].rider)
                            orderList.forEach { order ->
                                if (order.nome_ordine == orderN) {
                                    if (order.riderStatus == "not assigned") {
                                        //rider non ancora assegnato -> mando richiesta al rider
                                        frO.updateOrder(this, order, "request sended", rider)
                                    }
                                }
                            }
                        }
                    }
        } else {
            if (order!!.riderStatus == "not assigned") {
                //rider non ancora assegnato -> mando richiesta al rider
                frO.updateOrder(this, order!!, "request sended", rider)
            }
            if(order!!.riderStatus == "request sended"){
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
