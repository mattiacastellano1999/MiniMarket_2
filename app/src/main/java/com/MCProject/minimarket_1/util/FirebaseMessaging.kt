package com.MCProject.minimarket_1.util

import android.app.Activity
import android.util.Log
import android.widget.Toast
import com.MCProject.minimarket_1.MainActivity
import com.MCProject.minimarket_1.MainActivity.Companion.frO
import com.MCProject.minimarket_1.rider.RiderActivity
import com.MCProject.minimarket_1.rider.RiderActivity.Companion.orderList
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.DocumentSnapshot
import java.util.*

class FirebaseMessaging(val path: String, context: Activity) {

    val NAME_FIELD: String
    val TEXT_FIELD: String
    var firestoreNotificationListener: DocumentReference
    lateinit var firestoreNotificationSender: DocumentReference

    init {
        //recupero da firestore la collezione creata
        firestoreNotificationListener = MainActivity.db
            .collection( "/notification")
            .document("message_for_$path")

        NAME_FIELD = "Nome"
        TEXT_FIELD = "Testo"
    }


    fun sendMesage(
        context: Activity,
        dest: String,
        newMessage: Map<String, String>
    ): Task<Void> {

        firestoreNotificationSender = MainActivity.db
            .collection( context.getString(com.MCProject.minimarket_1.R.string.notification_path))
            .document("message_for_$dest")

        //scrivo su firestore il nuovo messaggio
        return firestoreNotificationSender.set(newMessage)
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
    fun addRealtimeUpdate(context: Activity, userType: String) {
        firestoreNotificationListener.addSnapshotListener { documentSnapshot, e ->
            when {
                e != null -> {
                    Log.e("HEY", "Errors: " + e.message)
                }
                documentSnapshot != null -> {
                if(userType == "rider"){
                        riderReadFromFirebase(context, userType)
                    } else if(userType == "user") {
                        userReadFromFirebase(context, userType)
                    } else {
                        gestorReadFromFirebase(context, userType)
                    }
                }
            }
        }
    }

    private fun userReadFromFirebase(context: Activity, userType: String) {
            var notify = Notification(context)
            val channellid = Random().nextInt(100)

            initNotification(channellid, userType, notify).addOnCompleteListener { res ->
                if( res.result.exists()) {
                    //funziona solo con la notifica
                    frO.getAllOrder(
                        orderList,
                        context
                    ).addOnCompleteListener { @Synchronized
                        if ( it.isSuccessful) {
                            if( !it.result.isEmpty) {
                            for (doc in it.result) {
                                var order = frO.parseOrder(doc)
                                Log.i("HEY", "Doc: "+doc.data)
                                orderList.add(order)
                                if(order.ordine == res.result["numero_ordine"]){
                                    RiderActivity.myOrder = order
                                }
                            }
                            } else {
                                Log.e("HEY", "Error with Path")
                            }
                        } else {
                            Log.e("HEY", "Error Firetore Marker Reading_0")
                        }
                        notify.showUserNotification(
                            channellid.toString(),
                            res.result.get("numero_ordine").toString(),
                            res.result.get("gestore").toString(),
                            res.result.get("cliente").toString(),
                            res.result.get("Testo").toString()
                        )
                    }

                }
            }
    }

    private fun initNotification(channellid: Int, userType: String, notify: Notification): Task<DocumentSnapshot> {
        return firestoreNotificationListener
            .get()
            .addOnSuccessListener {
                Log.i("HEY", "Mesaggio Da:" + it.data)
                notify.createNotificationChannel(
                    channellid.toString(),
                    "Channel_$userType$channellid",
                    "Prova Channel1"
                )
            }
    }

    private fun gestorReadFromFirebase(context: Activity, type: String) {
        var notify = Notification(context)
        val channellid = Random().nextInt(100)

        initNotification(channellid, type, notify).addOnCompleteListener {
            notify.showGestorNotification(
                channellid.toString(),
                it.result.get("numero_ordine").toString(),
                it.result.get("gestore").toString(),
                it.result.get("cliente").toString(),
                it.result.get("Testo").toString()
            )
        }
    }

    private fun riderReadFromFirebase(context: Activity, type: String) {
        var notify = Notification(context)
        val channellid = Random().nextInt(100)

        initNotification(channellid, type, notify).addOnCompleteListener {
                if( it.result.exists()) {
                    //funziona solo con la notifica
                    frO.getAllOrder(
                        orderList,
                        context
                    ).addOnCompleteListener {docs -> @Synchronized
                        for (doc in docs.result) {
                            val order = frO.parseOrder(doc)
                            Log.i("HEY", "Doc: "+doc.data)
                            orderList.add(order)
                            if(order.ordine == it.result["numero_ordine"]){
                                RiderActivity.myOrder = order
                                Log.i("HEY", "Order: ${order.ordine}")
                            }
                        }
                        notify.showRiderNotification(
                            channellid.toString(),
                            it.result.get("numero_ordine").toString(),
                            it.result.get("gestore").toString(),
                            it.result.get("cliente").toString(),
                            it.result.get("Testo").toString()
                        )
                    }

                }
            }
    }

}