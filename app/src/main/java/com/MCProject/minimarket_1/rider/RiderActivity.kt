package com.MCProject.minimarket_1.rider

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.Switch
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.MCProject.minimarket_1.MainActivity
import com.MCProject.minimarket_1.MainActivity.Companion.frO
import com.MCProject.minimarket_1.MainActivity.Companion.frR
import com.MCProject.minimarket_1.MainActivity.Companion.mail
import com.MCProject.minimarket_1.R
import com.MCProject.minimarket_1.access.Login
import com.MCProject.minimarket_1.util.FirebaseMessaging
import com.MCProject.minimarket_1.util.Order
import com.google.firebase.auth.FirebaseAuth

@SuppressLint("UseSwitchCompatOrMaterialCode")
class RiderActivity: AppCompatActivity() {


    lateinit var logoutImgBtn: ImageButton
    lateinit var welcomeTV: TextView
    val auth = FirebaseAuth.getInstance()
    lateinit var switch: Switch
    lateinit var leaveMarketBTN: Button
    lateinit var endDelivBTN: Button

    companion object {
        var riderStatus = 0
        var riderName = ""
        var riderEmail = ""
        var riderSurname = ""
        var orderGestor = ""
        var orderName = ""
        var myOrder: Order? = null
        var orderList = ArrayList<Order>()
    }

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.i("HEY", "RDIER Activity")
        setContentView(R.layout.rider_activity_main)
        
        logoutImgBtn = findViewById(R.id.exit_imgBtn)
        welcomeTV = findViewById(R.id.welcome_tv)
        leaveMarketBTN = findViewById(R.id.picked_btn)
        endDelivBTN = findViewById(R.id.delivered_btn)

        welcomeTV.text = "Welcome\n$mail"

    }

    @SuppressLint("UseSwitchCompatOrMaterialCode")
    override fun onStart() {
        super.onStart()

        switch = findViewById<Switch>(R.id.switch1)

        frR.getRider(this, "/profili/riders/dati", mail).addOnCompleteListener {
            riderStatus = it.result["status"].toString().toInt()
            riderEmail = it.result["email"].toString()
            riderSurname = it.result["cognome rider"].toString()
            riderName = it.result["nome rider"].toString()

            if(riderStatus == 1) {
                //rider aviable for delivery
                if(!switch.isChecked)
                    switch.performClick()
            } else {
                //rider NOT aviable for delivery
                if(switch.isChecked)
                    switch.performClick()
            }
        }

        orderName = ""
        frO.getAllOrder(orderList, this)
            .addOnCompleteListener {
                for (doc in it.result) {
                    Log.i("HEY", "DOC::: "+doc.data)
                    if( doc["riderStatus"] == getString(R.string.rider_status_accepted) ) {
                        if( doc["orderStatus"] != getString(R.string.order_status_complete) ) {
                            if( doc["rider"] == mail ) {
                                orderName = doc["ordine"].toString()
                                myOrder = frO.parseOrder(doc)
                                btnListener()
                                break
                            }
                        }
                    }
                }
            }

        switchListener()

        //Check some notification
        Log.i("HEY", "Check Notify")
        val fm = FirebaseMessaging(mail, this)
        fm.addRealtimeUpdate(this, "rider")

        btnListener()
    }

    private fun btnListener() {
        logoutImgBtn.setOnClickListener {
            auth.signOut()
            if(auth.currentUser == null) {
                val intentLogout = Intent(this, Login::class.java)
                startActivity(intentLogout)
            }
        }

        val chat = findViewById<Button>(R.id.chat_btn)
        if(orderName.isNotEmpty()){
            endDelivBTN.visibility = View.VISIBLE
            leaveMarketBTN.visibility = View.VISIBLE
            chat.visibility = View.VISIBLE

            chat.setOnClickListener {
                val intent = Intent(this, ChatRider::class.java)
                startActivity(intent)
            }

            leaveMarketBTN.setOnClickListener {
                //abilito la possibilità di chattare con il rider
                myOrder!!.orderStatus = getString(R.string.order_status_delivering)
                frO.updateOrder(this, myOrder!!).addOnCompleteListener {
                    if(it.isSuccessful) {
                        //mando all'utente la notifica
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                            val fm = FirebaseMessaging(mail, this)
                            val newMessage = mapOf<String, String>(
                                "gestore" to myOrder!!.proprietario,
                                "numero_ordine" to myOrder!!.ordine,
                                "cliente" to myOrder!!.cliente,
                                "Testo" to "The Rider $mail Leave the Market"
                            )
                            fm.sendMesage(this, myOrder!!.cliente, newMessage)
                        }
                    }
                }
            }

        } else {
            chat.visibility = View.GONE
            endDelivBTN.visibility = View.GONE
            leaveMarketBTN.visibility = View.GONE
        }

        val order = findViewById<Button>(R.id.delivery_btn)
        order.setOnClickListener {
            val intent = Intent(this, DeliveryList::class.java)
            startActivity(intent)
        }
    }

    private fun switchListener() {
        switch.setOnClickListener {
            var entry: HashMap<String, Any>
            if (switch.isChecked) {
                entry = hashMapOf<String, Any>(
                    "status" to 1,
                    "nome rider" to riderName,
                    "email" to riderEmail,
                    "cognome rider" to riderSurname
                )
            } else {
                entry = hashMapOf<String, Any>(
                    "status" to 0,
                    "nome rider" to riderName,
                    "email" to riderEmail,
                    "cognome rider" to riderSurname
                )
            }
            frR.updateRider(this, "/profili/riders/dati", mail, entry)
        }
    }
}