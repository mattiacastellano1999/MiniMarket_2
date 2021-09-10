package com.MCProject.minimarket_1.firestore

import android.app.Activity
import android.widget.Toast
import com.MCProject.minimarket_1.access.Loading
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.storage.FirebaseStorage
import java.util.*

class FirestoreRequest_Rider (
            override var db: FirebaseFirestore,
            override var auth: FirebaseAuth,
            override var imgDb: FirebaseStorage?,
            override var collection: String,
            override var mail: String?
    ): FirestoreRequest(db, auth, imgDb, collection, mail) {

    fun getRider(context: Activity, path: String, doc: String): Task<DocumentSnapshot> {
        val load = Loading(context)
        load.startLoading()

        val ret = db.collection(path).document(doc)
            .get()
            .addOnCompleteListener {
                load.stopLoadingDialog()
                return@addOnCompleteListener
            }
            .addOnFailureListener {
                Toast.makeText(context, it.message, Toast.LENGTH_SHORT).show()
            }
        return ret
    }

    fun updateRider(context: Activity, path: String, mail: String, entry: Any): Task<Void> {
        val load = Loading(context)
        load.startLoading()

        val ret = db.collection(path).document(mail)
            .set(entry)
            .addOnCompleteListener {
                load.stopLoadingDialog()
                return@addOnCompleteListener
            }
            .addOnFailureListener {
                Toast.makeText(context, it.message, Toast.LENGTH_SHORT).show()
            }
        return ret
    }


}