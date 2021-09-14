package com.MCProject.minimarket_1.rider

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.Fragment
import com.MCProject.minimarket_1.MainActivity
import com.MCProject.minimarket_1.MainActivity.Companion.mail
import com.MCProject.minimarket_1.gestor.OrderList
import com.MCProject.minimarket_1.util.Chat
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.QuerySnapshot

class ChatRider: Chat() {


    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        title2.visibility = View.VISIBLE
        button2.visibility = View.VISIBLE
        messageBox2.visibility = View.VISIBLE
        textBox2.visibility = View.VISIBLE

        title1.text = "Chat With " + RiderActivity.myOrder!!.proprietario
        title2.text = "Chat With " + RiderActivity.myOrder!!.cliente
    }

    @SuppressLint("SetTextI18n")
    override fun onStart() {
        super.onStart()

        chatRealtimeUpdate(MainActivity.mail)

        button1.setOnClickListener {
            sendMesage( mail, RiderActivity.myOrder!!.proprietario , messageBox1)
            textBox1.text = textBox1.text.toString() + "\n" + messageBox1.text.toString()
            messageBox1.text.clear()
        }

        button2.setOnClickListener {
            sendMesage(mail, RiderActivity.myOrder!!.cliente , messageBox2)
            textBox2.text = textBox2.text.toString() + "\n" + messageBox2.text.toString()
            messageBox1.text.clear()
        }
    }

    @SuppressLint("SetTextI18n")
    override fun readFromFirebase(doc: String): Task<QuerySnapshot> {
        return super.readFromFirebase(doc).addOnCompleteListener {
            Log.i("HEY", "ReadFromFirebaseOuter")
            if(it.isSuccessful && !it.result.isEmpty) {
                val elLetto = it.result.documents[0].id
                Log.i("HEY", "RES: "+it.result.documents[0].id)
                if(message[MITT_FIELD] != null){
                    Log.i("HEY", "MITT: "+message[MITT_FIELD])
                    if(message[MITT_FIELD] == RiderActivity.myOrder!!.proprietario){
                        textBox1.text = textBox1.text.toString() +"\n"+ message[TEXT_FIELD].toString()
                        MainActivity.frM.deleteFromDB(this, elLetto, "/chat")
                    } else if(message[MITT_FIELD] == RiderActivity.myOrder!!.cliente) {
                        textBox1.text = textBox2.text.toString() +"\n"+ message[TEXT_FIELD].toString()
                        MainActivity.frM.deleteFromDB(this, elLetto, "/chat")
                    } else {
                        Log.e("HEY", "mittente sconosciuto")
                    }
                } else {
                    Log.e("HEY", "message empty")
                }
            }
        }
    }
}