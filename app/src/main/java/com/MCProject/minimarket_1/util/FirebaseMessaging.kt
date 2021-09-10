package com.MCProject.minimarket_1.util

import android.app.Activity
import android.util.Log
import android.widget.Toast
import com.MCProject.minimarket_1.MainActivity
import com.MCProject.minimarket_1.rider.RiderActivity
import com.google.firebase.firestore.DocumentReference
import java.util.*

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

    fun sendMessageToGestor(
        context: Activity,
        sender: String,
        receiver: String,
        message: String,
        orderN: String
    ) {
        val newMessage = mapOf<String, String>(
            "gestore" to receiver,
            "numero_ordine" to orderN,
            "cliente" to sender,
            TEXT_FIELD to message
        )

        sendMesage(context, receiver, newMessage)
    }

    fun sendMessageToRider(
        context: Activity,
        sender: String,
        receiver: String,
        message: String,
        orderN: String
    ) {
        val newMessage = mapOf<String, String>(
            "gestore" to receiver,
            "numero_ordine" to orderN,
            "cliente" to sender,
            TEXT_FIELD to message
        )

        sendMesage(context, receiver, newMessage)
    }

    fun sendMesage(
        context: Activity,
        receiver: String,
        newMessage: Map<String, String>
    ) {

        firestoreChatSender = MainActivity.db
            .collection("chat")
            .document("message_for_"+receiver)

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
    fun addRealtimeUpdate(context: Activity, s: String) {
        firestoreChatListener.addSnapshotListener { documentSnapshot, e ->
            when {
                e != null -> {
                    Log.e("HEY", "Errors: " + e.message)
                }
                documentSnapshot != null -> {
                    if(s.contains("rider")){
                        riderReadFromFirebase(context, s)
                    } else {
                        gestorReadFromFirebase(context, s)
                    }
                }
            }
        }
    }

    private fun gestorReadFromFirebase(context: Activity, type: String) {
        firestoreChatListener
            .get()
            .addOnSuccessListener {
                Log.i("HEY", "Mesaggio Da:" + it.data)
                val notify = Notification(context)
                val channellid = Random().nextInt(100)
                notify.createNotificationChannel(
                    channellid.toString(),
                    "Channel_$type$channellid",
                    "Prova Channel1")
                notify.showGestorNotification(
                    channellid.toString(),
                    it.get("numero_ordine").toString(),
                    it.get("gestore").toString(),
                    it.get("cliente").toString(),
                    it.get("Testo").toString())
                if(type.contains("rider") && it.exists()) {
                    //funziona solo con la notifica
                    MainActivity.frO.getAllOrder(
                        it.get("gestore").toString(),
                        RiderActivity.orderList,
                        context
                    ).addOnCompleteListener { docs ->
                        Log.i("HEY", "Arivato qui: " + docs.toString())
                        for( order in RiderActivity.orderList){
                            if(order.nome_ordine == it["nome_ordine"]){
                                RiderActivity.myOrder = order
                            }
                        }
                    }

                }
            }
    }

    private fun riderReadFromFirebase(context: Activity, type: String) {
        firestoreChatListener
            .get()
            .addOnSuccessListener {
                Log.i("HEY", "Mesaggio Da:" + it.data)
                val notify = Notification(context)
                val channellid = Random().nextInt(100)
                notify.createNotificationChannel(
                    channellid.toString(),
                    "Channel_$type$channellid",
                    "Prova Channel1")
                notify.showRiderNotification(
                    channellid.toString(),
                    it.get("numero_ordine").toString(),
                    it.get("gestore").toString(),
                    it.get("cliente").toString(),
                    it.get("Testo").toString()
                )
                if( it.exists()) {
                    //funziona solo con la notifica
                    MainActivity.frO.getAllOrder(
                        it.get("cliente").toString(),
                        RiderActivity.orderList,
                        context
                    ).addOnCompleteListener { docs ->
                        Log.i("HEY", "Arivato qui: " + RiderActivity.orderList)
                        for( order in RiderActivity.orderList){
                            if(order.nome_ordine == it["numero_ordine"]){
                                RiderActivity.myOrder = order/*Order(
                                    order.nome_ordine,
                                    order.prezzo_tot,
                                    order.proprietario,
                                    order.cliente,
                                    order.addrClient,
                                    order.addrGestor,
                                    order.rider,
                                    order.riderStatus,
                                    order.products
                                )*/
                                Log.i("HEY", "Order: ${order.nome_ordine}")
                            }
                        }
                    }

                }
            }
    }

}