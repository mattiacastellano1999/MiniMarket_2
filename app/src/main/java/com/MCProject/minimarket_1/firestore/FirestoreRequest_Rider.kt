package com.MCProject.minimarket_1.firestore

import android.app.Activity
import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import com.MCProject.minimarket_1.access.Loading
import com.MCProject.minimarket_1.access.util.ProductListActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage

class FirestoreRequest_Rider (
            override var db: FirebaseFirestore,
            override var auth: FirebaseAuth,
            override var imgDb: FirebaseStorage?,
            override var collection: String,
            override var mail: String?
    ): FirestoreRequest(db, auth, imgDb, collection, mail) {


}