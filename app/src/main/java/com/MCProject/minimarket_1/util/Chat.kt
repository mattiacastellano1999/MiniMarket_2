package com.MCProject.minimarket_1.util

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.text.method.ScrollingMovementMethod
import android.util.Log
import android.widget.*
import com.MCProject.minimarket_1.MainActivity
import com.MCProject.minimarket_1.MainActivity.Companion.auth
import com.MCProject.minimarket_1.MainActivity.Companion.mail
import com.MCProject.minimarket_1.R
import com.MCProject.minimarket_1.access.Login
import com.MCProject.minimarket_1.gestor.GestorActivity
import com.MCProject.minimarket_1.gestor.OrderList
import com.MCProject.minimarket_1.rider.RiderActivity
import com.MCProject.minimarket_1.user.UserActivity
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.QuerySnapshot

open class Chat: Activity() {

    lateinit var title1: TextView
    lateinit var title2: TextView
    lateinit var textBox1: TextView
    lateinit var textBox2: TextView
    lateinit var messageBox1: EditText
    lateinit var messageBox2: EditText
    lateinit var button1: Button
    lateinit var button2: Button
    lateinit var homeBtn: ImageButton
    lateinit var logouBtn: ImageButton

    val PATH = "/chat"
    var MITT_FIELD = "mittente"
    var DEST_FIELD = "destinatario"
    var TEXT_FIELD = "testo_messaggio"

    var firestoreChatCollection = MainActivity.db.collection(PATH)
    var firestoreListener: ListenerRegistration? = null

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
        homeBtn = findViewById(R.id.home_imgBtn)
        logouBtn = findViewById(R.id.exit_imgBtn)
    }

    override fun onStart() {
        super.onStart()

        textBox1.movementMethod = ScrollingMovementMethod()
        textBox2.movementMethod = ScrollingMovementMethod()

        MainActivity.logoutListener(this, logouBtn)
        MainActivity.homeListener(this, homeBtn)
    }

    /**
     * Va in ascolto su /chat/{doc} e quando avviene una modifica assegan i valori letti a message
     */
    fun chatRealtimeUpdate(type: String, order: Order) {
        firestoreListener = firestoreChatCollection
            .addSnapshotListener{ documentSnapshot, e ->
                when {
                e != null -> {
                    Log.e("ERRORS",""+e.message)
                }
                documentSnapshot != null -> {
                    readFromFirebase(type, order)
                }
            }
        }
    }

    @SuppressLint("SetTextI18n")
    fun readFromFirebase(type: String, order: Order): Task<QuerySnapshot> {
        message.clear()
        return firestoreChatCollection
            .get()
            .addOnSuccessListener {
                if (!it.isEmpty) {
                    for (el in it.documents) {
                        val elLetto = el.id

                        if (el[MITT_FIELD] != null) {
                            message[MITT_FIELD] = el[MITT_FIELD].toString()
                            message[DEST_FIELD] = el[DEST_FIELD].toString()
                            message[TEXT_FIELD] = el[TEXT_FIELD].toString()

                            if (message[DEST_FIELD] == mail) {
                                //  GESTORE & USER  //
                                if (type == getString(R.string.gestori) || type == getString(R.string.utenti)) {
                                    if (message[MITT_FIELD] == order.rider) {
                                        textBox1.text =
                                            textBox1.text.toString() + "\n" + message[TEXT_FIELD].toString()
                                        MainActivity.frM.deleteFromDB(this, elLetto, "/chat")
                                    } else {
                                        Log.e("HEY", "mittente sconosciuto")
                                    }
                                }

                                //  RIDER   //
                                else if (type == getString(R.string.rider)) {
                                    if (message[MITT_FIELD] == order.proprietario) {
                                        textBox1.text =
                                            textBox1.text.toString() + "\n" + message[TEXT_FIELD].toString()
                                        MainActivity.frM.deleteFromDB(this, elLetto, "/chat")
                                    } else if (message[MITT_FIELD] == order.cliente) {
                                        textBox2.text =
                                            textBox2.text.toString() + "\n" + message[TEXT_FIELD].toString()
                                        MainActivity.frM.deleteFromDB(this, elLetto, "/chat")
                                    } else {
                                        Log.e("HEY", "mittente sconosciuto")
                                    }
                                }
                            }
                        }
                    }
                }
            }
    }

    fun sendMesage(mittente: String, destinatario: String, editText: EditText){

        val newMessage = mapOf<String, String>(
            MITT_FIELD to mittente,
            DEST_FIELD to destinatario,
            TEXT_FIELD to editText.text.toString()
        )

        //scrivo su firestore il nuovo messaggio
        firestoreChatCollection
            .add(newMessage)
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

    override fun onStop() {
        super.onStop()
        Log.i("HEY", "DETACHED")
        firestoreListener!!.remove()
    }
}
