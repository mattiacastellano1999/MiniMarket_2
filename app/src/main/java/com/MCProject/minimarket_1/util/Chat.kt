package com.MCProject.minimarket_1.util

import android.app.Activity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.MCProject.minimarket_1.MainActivity
import com.MCProject.minimarket_1.R
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.DocumentSnapshot

open class Chat: Activity() {

    lateinit var title1: TextView
    lateinit var title2: TextView

    lateinit var textBox1: TextView
    lateinit var textBox2: TextView

    lateinit var messageBox1: EditText
    lateinit var messageBox2: EditText

    lateinit var button1: Button
    lateinit var button2: Button

    val PATH = "/chat"
    var NAME_FIELD = "mittente"
    var TEXT_FIELD = "testo_messaggio"

    var firestoreChatCollection = MainActivity.db.collection(PATH)

    var message = HashMap<String, String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.i("HEY", "RDIER Activity")
        setContentView(R.layout.chat_activity)

        title1 = findViewById(R.id.chat_title_1)
        title2 = findViewById(R.id.chat_title_2)
        messageBox1 = findViewById(R.id.message_1)
        messageBox2 = findViewById(R.id.message_2)
        textBox1 = findViewById(R.id.text_box_1)
        textBox2 = findViewById(R.id.text_box_2)
        button1 = findViewById(R.id.button_1)
        button2 = findViewById(R.id.button_2)
    }

    /**
     * Va in ascolto su /chat/{doc} e quando avviene una modifica assegan i valori letti a message
     */
    fun chatRealtimeUpdate(doc: String) {
        var mydoc = doc
        firestoreChatCollection.document(mydoc)
            .addSnapshotListener{ documentSnapshot, e ->
                when {
                e != null -> {
                    Log.e("ERRORS",""+e.message)
                }
                documentSnapshot != null -> {
                    readFromFirebase(mydoc)
                }
            }
        }
    }

    open fun readFromFirebase(doc: String): Task<DocumentSnapshot> {
        message.clear()
        return firestoreChatCollection.document(doc)
            .get()
            .addOnSuccessListener {
                Log.i("HEY", ""+ it.get("Nome").toString())
                message[NAME_FIELD] = it.get(NAME_FIELD).toString()
                message[TEXT_FIELD] = it.get(TEXT_FIELD).toString()
            }
    }

    fun sendMesage(doc: String, editText: EditText){

        val newMessage = mapOf<String, String>(
            NAME_FIELD to editText.text.toString(),
            TEXT_FIELD to editText.text.toString()
        )

        //scrivo su firestore il nuovo messaggio
        firestoreChatCollection.document(doc)
            .set(newMessage)
            .addOnSuccessListener {
                Toast.makeText(
                    applicationContext,
                    "Message Sent",
                    Toast.LENGTH_LONG
                ).show()
            }
            .addOnFailureListener {
                Toast.makeText(
                    applicationContext,
                    "ERROR",
                    Toast.LENGTH_LONG
                ).show()
            }
    }
}
