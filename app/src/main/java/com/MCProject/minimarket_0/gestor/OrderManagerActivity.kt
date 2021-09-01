package com.MCProject.minimarket_0.gestor

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Spinner
import androidx.appcompat.app.AppCompatActivity
import com.MCProject.minimarket_1.MainActivity
import com.MCProject.minimarket_1.R
import android.widget.ArrayAdapter
import android.widget.ImageButton
import com.MCProject.minimarket_1.MainActivity.Companion.auth
import com.MCProject.minimarket_1.RiderActivity
import com.MCProject.minimarket_1.access.Login
import com.MCProject.minimarket_1.gestor.GestorActivity
import com.MCProject.minimarket_1.user.UserActivity
import kotlin.math.log


class OrderManagerActivity : AppCompatActivity() {
    lateinit var logoutImgBtn: ImageButton
    lateinit var homeImgBtn: ImageButton

    lateinit var cliente : String
    lateinit var riderAviable: ArrayList<String>
    lateinit var spinner: Spinner

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Log.i("HEY", "Order Manager Activity")
        setContentView(R.layout.gestor_order_activity)
        cliente = intent.extras!!["testo"].toString()

        spinner = findViewById(R.id.rider_ed)
        homeImgBtn = findViewById(R.id.home_imgBtn)
        logoutImgBtn = findViewById(R.id.exit_imgBtn)
    }

    override fun onStart() {
        super.onStart()

        buttonListener()

        if(cliente.isNotEmpty()){
            Log.i("HEY", "Not Empty")
            //significa che e' stata chiamata in seguito ad aver premuto su un determinato ordine
            //in cliente ho la mail del cliente che ha effettuato l'ordine
            riderAviable = ArrayList()
            MainActivity.frM
                    .getAllRiderAviable(this, riderAviable)
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
    }
}
