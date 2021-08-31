package com.MCProject.minimarket_1.firestore

import android.app.Activity
import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import com.MCProject.minimarket_1.access.Loading
import com.MCProject.minimarket_1.access.util.Order
import com.MCProject.minimarket_1.access.util.ProductListActivity
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.storage.FirebaseStorage

class FirestoreRequest_Order (
        override var db: FirebaseFirestore,
        override var auth: FirebaseAuth,
        override var imgDb: FirebaseStorage?,
        override var collection: String,
        override var mail: String?
): FirestoreRequest(db, auth, imgDb, collection, mail) {

    fun getAllOrder(
            market: String,
            orderList: ArrayList<Order>,
            productList: ArrayList<ProductListActivity.Product>,
            context: Activity
    ): Task<QuerySnapshot> {
        pathToMyProduct = "profili/gestori/ordini/${market}/miei_ordini/"
        val ret = addData("", context, productList)
                .addOnCompleteListener { va ->
                    for (doc in va.result) {
                        orderList.add(
                                Order(
                                        doc.data["proprietario"].toString(),
                                        doc.data["cliente"].toString(),
                                        doc.data["rider"].toString(),
                                        productList
                                )
                        )
                    }
                    Log.i("HEY", "data adding_15:${orderList.size}")
                }
        return ret
    }

    @RequiresApi(Build.VERSION_CODES.O)
    @Synchronized
    fun uploadOrder(
            productList: ArrayList<ProductListActivity.Product>,
            context: Activity,
            client: String?,
            rider: String?
    ) {
        val load = Loading(context)
        load.startLoading()
        productList.forEachIndexed { i, prod ->
            var entry2: HashMap<String, Any?> = hashMapOf<String, Any?>(
                    "nome" to prod.name,
                    "prezzo" to prod.price,
                    "quantita" to prod.quantity,
                    "descrizione" to prod.description,
                    "proprietario" to prod.owner,
                    "cliente" to client,
                    "rider" to rider
            )
            Log.i("HEY", "CIclo:::")
            db.collection("/profili/gestori/ordini/${prod.owner}/miei_ordini")
                    .document(prod.name)
                    .set(entry2)
                    .addOnSuccessListener {
                        removeProductFromCart(prod, context)
                    }
                    .addOnCompleteListener {
                        if(i >= productList.size-1){
                            load.stopLoadingDialog()
                            sendNotification(context, client, prod.owner, "Has Been Added: ${productList.size.toString()} Product to the new Order!" )
                        }
                    }
                    .addOnFailureListener {
                        Log.i("HEY", "Failed")
                        load.stopLoadingDialog()
                        Toast.makeText(
                                context,
                                "Error during Product Order",
                                Toast.LENGTH_LONG
                        ).show()
                    }
        }
        Log.i("HEY", "FINE CIclo:::")
    }


}