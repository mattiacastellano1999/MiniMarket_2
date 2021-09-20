package com.MCProject.minimarket_1.firestore

import android.app.Activity
import android.util.Log
import android.widget.Toast
import com.MCProject.minimarket_1.access.JoinUs
import com.MCProject.minimarket_1.access.Loading
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage

class FirestoreRequest_User(
        override var db: FirebaseFirestore,
        override var auth: FirebaseAuth,
        override var imgDb: FirebaseStorage?,
        override var collection: String,
        override var mail: String?
): FirestoreRequest(db, auth, imgDb, collection, mail) {

    /**
     * Funzione che ritorna tutti gli utenti che sono o gestori, o riders, o utenti
     * @param activity (this, context)
     * @param profile una stringa tra "gestori", "riders", "utenti"
     * @return ArrayList contenent tutti gli username
     */
    fun getAllCategoryProfile(activity: Activity, profile: String): ArrayList<String>{

        val arrayProfile = ArrayList<String>()
        val load = Loading(activity)
        load.startLoading()

        //read data firestore
        db.collection("/profili/$profile/dati")
                .get()
                .addOnCompleteListener {

                    for (doc in it.result) {
                        arrayProfile.add(doc.id)
                    }
                    load.stopLoadingDialog()
                }
                .addOnFailureListener {
                    Toast.makeText(
                            activity,
                            "Database Reding Error",
                            Toast.LENGTH_LONG
                    ).show()
                    load.stopLoadingDialog()
                }
        return arrayProfile
    }

    /**
     * Usata dalla JoinUs per aggiungere un nuovo utente
     */
    fun newUser(
            activity: Activity,
            entry: HashMap<String, Any?>,
            collection: String
    ): Task<Void> {

        val joinus = JoinUs()
        return db.collection("/profili/$collection/dati")
                .document(entry["email"].toString())
                .set(entry)
                .addOnSuccessListener {
                    Toast.makeText(
                            activity,
                            "Document Added Correctly",
                            Toast.LENGTH_LONG
                    ).show()
                    return@addOnSuccessListener
                }
                .addOnFailureListener {
                    Toast.makeText(
                            activity,
                            "Error during Document data upload",
                            Toast.LENGTH_LONG
                    ).show()
                    return@addOnFailureListener
                }
    }
}