package com.MCProject.minimarket_1.gestor

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.View
import com.MCProject.minimarket_1.MainActivity
import com.MCProject.minimarket_1.MainActivity.Companion.db
import com.MCProject.minimarket_1.MainActivity.Companion.mail
import com.MCProject.minimarket_1.R
import com.MCProject.minimarket_1.gestor.OrderList.Companion.orderSelected
import com.MCProject.minimarket_1.util.Chat
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.QuerySnapshot

class ChatGestor: Chat() {


    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        title1.text = "Chat With " + orderSelected.rider

        title2.visibility = View.GONE
        button2.visibility = View.GONE
        messageBox2.visibility = View.GONE
        textBox2.visibility = View.GONE

    }

    @SuppressLint("SetTextI18n")
    override fun onStart() {
        super.onStart()

        chatRealtimeUpdate(getString(R.string.gestori), orderSelected)

        button1.setOnClickListener {
            sendMesage(mail, orderSelected.rider , messageBox1)
            textBox1.text = textBox1.text.toString() + "\n" + messageBox1.text.toString()
            messageBox1.text.clear()
        }
    }
}
