package com.MCProject.minimarket_1.firestore

import android.app.Activity
import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import com.MCProject.minimarket_1.R
import com.MCProject.minimarket_1.access.Loading
import com.MCProject.minimarket_1.access.util.Order
import com.MCProject.minimarket_1.access.util.ProductListActivity
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.storage.FirebaseStorage
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

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
            context: Activity
    ): Task<QuerySnapshot> {
        pathToMyProduct = "/profili/gestori/ordini/$market/miei_ordini"
        val ret = addOrderData(pathToMyProduct, context, orderList)
                .addOnCompleteListener { va ->
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

        findCorrectOrderName(productList[0].owner, load, productList, context, client, rider)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun findCorrectOrderName(
            e_mail: String, load: Loading,
            productList: ArrayList<ProductListActivity.Product>,
            context: Activity, client: String?,
            rider: String?
    ): DocumentReference {
        var orderName = "Order N_" + Random().nextInt(100)
        var doc: DocumentReference =
                db.collection("/profili/gestori/ordini/$e_mail/miei_ordini")
                        .document(orderName)
        Log.i("HEY" , "Name: "+ orderName)
        doc
                .get()
                .addOnSuccessListener {
                    if(it.exists()) {
                        Log.i("HEY", "Success: " + it.data)
                        findCorrectOrderName(e_mail, load, productList, context, client, rider)
                    } else {
                        doUploadData(productList, context, client, rider, orderName, load, doc)
                    }
                }
        return doc
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun doUploadData(
            productList: ArrayList<ProductListActivity.Product>,
            context: Activity,
            client: String?,
            rider: String?,
            orderName: String,
            load: Loading,
            doc: DocumentReference
    ) {
        var entry2: HashMap<String, Any?> = hashMapOf()
        entry2["nome"] = orderName
        entry2["cliente"] = client
        entry2["rider"] = rider
        entry2["proprietario"] = mail
        entry2["riderStatus"] = context.getString(R.string.rider_status_NA)
        var priceTot = 0.0
        var owner = ""


        productList.forEachIndexed { i, prod ->
            entry2["prod_name_$i"] = prod.name
            entry2["prod_qty_$i"] = prod.quantity
            owner = prod.owner
            priceTot += (prod.price * prod.quantity)
        }
        entry2["prezzo"] = priceTot

        doc.set(entry2)
                .addOnSuccessListener {
                    productList.forEach { prod ->
                        removeProductFromCart(prod, context)
                    }
                }
                .addOnCompleteListener {
                    load.stopLoadingDialog()
                    sendNotification(
                            context,
                            client,
                            owner,
                            "Has Been Added: ${productList.size.toString()} Product to the new Order!"
                    )
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

    /*@RequiresApi(Build.VERSION_CODES.O)
    @Synchronized
    fun uploadDeliveryRequest(
            context: Activity,
            orders: ArrayList<Order>
    ) {
        val order = gestor
        val load = Loading(context)
        load.startLoading()
        db.collection("/profili/riders/ordini/${rider}/ordini_da_accettare")
                .document(gestor + "_" + Random(100).nextInt(100).toString())
                .set(order)
                .addOnSuccessListener {
                    deleteFromDB(context, order, "profili/gestori/ordini/$gestor/miei_ordini/")
                }
                .addOnCompleteListener {
                    if(it.isSuccessful)
                        sendNotification(context, gestor, rider, "The Gestor: $gestor require your services!" )
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

    }*/

}