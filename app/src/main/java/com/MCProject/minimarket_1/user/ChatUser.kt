package com.MCProject.minimarket_1.user

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.View
import com.MCProject.minimarket_1.MainActivity
import com.MCProject.minimarket_1.MainActivity.Companion.mail
import com.MCProject.minimarket_1.R
import com.MCProject.minimarket_1.rider.RiderActivity
import com.MCProject.minimarket_1.util.Chat
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.QuerySnapshot

class ChatUser: Chat() {

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        title2.visibility = View.INVISIBLE
        button2.visibility = View.INVISIBLE
        messageBox2.visibility = View.INVISIBLE
        textBox2.visibility = View.INVISIBLE

        title1.text = "Chat With " + RiderActivity.myOrder!!.rider
    }

    @SuppressLint("SetTextI18n")
    override fun onStart() {
        super.onStart()

        chatRealtimeUpdate(getString(R.string.utenti), RiderActivity.myOrder!!)

        button1.setOnClickListener {
            sendMesage( mail, RiderActivity.myOrder!!.rider , messageBox1)
            textBox1.text = textBox1.text.toString() + "\n" + messageBox1.text.toString()
            messageBox1.text.clear()
        }
    }

}