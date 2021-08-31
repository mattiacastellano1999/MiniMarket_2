package com.MCProject.minimarket_1.access.util

import android.app.Activity
import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import com.MCProject.minimarket_1.MainActivity
import com.google.firebase.firestore.DocumentReference

class FirebaseMessaging constructor(val path: String) {

    val NAME_FIELD: String
    val TEXT_FIELD: String
    var firestoreChatListener: DocumentReference
    lateinit var firestoreChatSender: DocumentReference

    init {
        //recupero da firestore la collezione creata
        firestoreChatListener = MainActivity.db
            .collection("chat")
            .document("message_for_"+path)

        NAME_FIELD = "Nome"
        TEXT_FIELD = "Testo"
    }

    fun sendMesage(context: Activity, sender: String, receiver: String, message: String) {

        firestoreChatSender = MainActivity.db
            .collection("chat")
            .document("message_for_"+receiver)

        val newMessage = mapOf<String, String>(
            NAME_FIELD to sender,
            TEXT_FIELD to message
        )

        //scrivo su firestore il nuovo messaggio
        firestoreChatSender.set(newMessage)
            .addOnSuccessListener {
                Toast.makeText(
                    context,
                    "Message Sent",
                    Toast.LENGTH_LONG
                ).show()
            }
            .addOnFailureListener {
                Toast.makeText(
                    context,
                    "ERROR",
                    Toast.LENGTH_LONG
                ).show()
            }
    }

    /*  Listener sul nostro documento:
    *   quando rileva delle modifiche su Firebase
    *   le scrive sul textDisplay
    */
    fun addRealtimeUpdate(context: Activity) {
        firestoreChatListener.addSnapshotListener { documentSnapshot, e ->
            when {
                e != null -> {
                    Log.e("ERRORS", "" + e.message)
                }
                documentSnapshot != null -> {
                    readFromFirebase(context)
                }
            }
        }
    }

    private fun readFromFirebase(context: Activity) {
        //l.clear()
        firestoreChatListener
            .get()
            .addOnSuccessListener {
                Log.i("HEY", "Mesaggio Da:" + it.get("Nome").toString())
                val notify = Notification(context)
                notify.createNotificationChannel("0", "Channel1", "Prova Channel1")
                notify.showGestorNotification("0", it.get("Nome").toString(), it.get("Testo").toString())
            }
    }

}