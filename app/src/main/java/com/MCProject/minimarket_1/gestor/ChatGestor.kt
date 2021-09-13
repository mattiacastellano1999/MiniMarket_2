package com.MCProject.minimarket_1.gestor

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.View
import com.MCProject.minimarket_1.MainActivity
import com.MCProject.minimarket_1.MainActivity.Companion.db
import com.MCProject.minimarket_1.gestor.OrderList.Companion.orderSelected
import com.MCProject.minimarket_1.util.Chat
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.DocumentSnapshot

class ChatGestor: Chat() {


    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        title1.text = "Chat With" + orderSelected.rider

        title2.visibility = View.GONE
        button2.visibility = View.GONE
        messageBox2.visibility = View.GONE
        textBox2.visibility = View.GONE

    }

    override fun onStart() {
        super.onStart()

        chatRealtimeUpdate(MainActivity.mail)

        button1.setOnClickListener {
            sendMesage(orderSelected.rider , messageBox1)
        }
    }

    override fun readFromFirebase(doc: String): Task<DocumentSnapshot> {
        return super.readFromFirebase(doc).addOnCompleteListener {
            Log.i("HEY", "ReadFromFirebaseOuter")
            if(it.isSuccessful) {
                if(message[NAME_FIELD] != null){
                    if(message[NAME_FIELD] == orderSelected.rider){
                        textBox1.text = message[TEXT_FIELD].toString()
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
