package com.MCProject.minimarket_1.rider

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.Fragment
import com.MCProject.minimarket_1.MainActivity
import com.MCProject.minimarket_1.MainActivity.Companion.mail
import com.MCProject.minimarket_1.R
import com.MCProject.minimarket_1.gestor.OrderList
import com.MCProject.minimarket_1.rider.RiderActivity.Companion.myOrder
import com.MCProject.minimarket_1.util.Chat
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.QuerySnapshot

class ChatRider: Chat() {


    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if(myOrder!!.orderStatus == getString(R.string.order_status_delivering)) {
            title2.visibility = View.VISIBLE
            button2.visibility = View.VISIBLE
            messageBox2.visibility = View.VISIBLE
            textBox2.visibility = View.VISIBLE
            title2.text = "Chat With " + RiderActivity.myOrder!!.cliente
        } else {
            title2.visibility = View.GONE
            button2.visibility = View.GONE
            messageBox2.visibility = View.GONE
            textBox2.visibility = View.GONE
        }

        title1.text = "Chat With " + RiderActivity.myOrder!!.proprietario
    }

    @SuppressLint("SetTextI18n")
    override fun onStart() {
        super.onStart()

        chatRealtimeUpdate(getString(R.string.rider), RiderActivity.myOrder!!)

        button1.setOnClickListener {
            sendMesage( mail, RiderActivity.myOrder!!.proprietario , messageBox1)
            textBox1.text = textBox1.text.toString() + "\n" + messageBox1.text.toString()
            messageBox1.text.clear()
        }

        button2.setOnClickListener {
            sendMesage(mail, RiderActivity.myOrder!!.cliente , messageBox2)
            textBox2.text = textBox2.text.toString() + "\n" + messageBox2.text.toString()
            messageBox2.text.clear()
        }
    }

}