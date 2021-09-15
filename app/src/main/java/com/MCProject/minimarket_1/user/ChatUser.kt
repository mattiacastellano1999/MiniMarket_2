package com.MCProject.minimarket_1.user

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.View
import com.MCProject.minimarket_1.MainActivity
import com.MCProject.minimarket_1.MainActivity.Companion.mail
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

        chatRealtimeUpdate(MainActivity.mail)

        button1.setOnClickListener {
            sendMesage( mail, RiderActivity.myOrder!!.rider , messageBox1)
            textBox1.text = textBox1.text.toString() + "\n" + messageBox1.text.toString()
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
                    if(message[MITT_FIELD] == RiderActivity.myOrder!!.rider){
                        textBox1.text = textBox1.text.toString() +"\n"+ message[TEXT_FIELD].toString()
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