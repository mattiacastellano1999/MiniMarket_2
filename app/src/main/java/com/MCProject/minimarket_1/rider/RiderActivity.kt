package com.MCProject.minimarket_1.rider

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageButton
import android.widget.Switch
import androidx.appcompat.app.AppCompatActivity
import com.MCProject.minimarket_1.MainActivity
import com.MCProject.minimarket_1.MainActivity.Companion.frR
import com.MCProject.minimarket_1.MainActivity.Companion.mail
import com.MCProject.minimarket_1.R
import com.MCProject.minimarket_1.RiderChatFragment
import com.MCProject.minimarket_1.access.Login
import com.MCProject.minimarket_1.util.FirebaseMessaging
import com.MCProject.minimarket_1.util.Order
import com.google.firebase.auth.FirebaseAuth

@SuppressLint("UseSwitchCompatOrMaterialCode")
class RiderActivity: AppCompatActivity() {


    lateinit var logoutImgBtn: ImageButton
    val auth = FirebaseAuth.getInstance()
    lateinit var switch: Switch

    companion object {
        var riderStatus = 0
        var riderName = ""
        var riderEmail = ""
        var riderSurname = ""
        var myOrder: Order? = null
        var orderList = ArrayList<Order>()
    }

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.i("HEY", "RDIER Activity")
        setContentView(R.layout.rider_activity_main)
        
        logoutImgBtn = findViewById(R.id.exit_imgBtn)

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

        switchListener()

        //Check some notification
        Log.i("HEY", "Check Notify")
        val fm = FirebaseMessaging(MainActivity.mail, this)
        fm.addRealtimeUpdate(this, "/profili/riders/dati/$mail")

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
        chat.setOnClickListener {
            val chatFragment = RiderChatFragment()
            supportFragmentManager.beginTransaction().apply {
                replace(R.id.main_fragment, chatFragment)
                commit()
            }
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