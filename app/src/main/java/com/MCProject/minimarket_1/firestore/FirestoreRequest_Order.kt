package com.MCProject.minimarket_1.firestore

import android.app.Activity
import android.content.Intent
import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import com.MCProject.minimarket_1.R
import com.MCProject.minimarket_1.access.Loading
import com.MCProject.minimarket_1.rider.RiderActivity
import com.MCProject.minimarket_1.user.CartProductListActivity
import com.MCProject.minimarket_1.util.MyLocation
import com.MCProject.minimarket_1.util.Order
import com.MCProject.minimarket_1.util.ProductListActivity
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.storage.FirebaseStorage
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

class FirestoreRequest_Order (
        override var db: FirebaseFirestore,
        override var auth: FirebaseAuth,
        override var imgDb: FirebaseStorage?,
        override var collection: String,
        override var mail: String?
): FirestoreRequest(db, auth, imgDb, collection, mail) {

    /**
     * Get di tutti gli ordini
     */
    fun getAllOrder(
        orderList: ArrayList<Order>,
        context: Activity
    ): Task<QuerySnapshot> {
        pathToMyProduct = "/ordini"
        val ret = addOrderData(pathToMyProduct, context, orderList)
                .addOnCompleteListener { va ->
                }
        return ret
    }

    /**
     * Crea un nuovo ordine
     */
    @RequiresApi(Build.VERSION_CODES.O)
    fun uploadOrder(
        productList: ArrayList<ProductListActivity.Product>,
        context: Activity,
        client: String?,
        rider: String?,
        addrGestor: String
    ) {
        val load = Loading(context)
        load.startLoading()

        var doc = db.collection("/ordini")
        doUploadOrder(productList, context, client, rider, load, doc, addrGestor)
    }

    /**
     * Crea un nuovo ordine
     */
    @RequiresApi(Build.VERSION_CODES.O)
    @Synchronized
    private fun doUploadOrder(
        productList: ArrayList<ProductListActivity.Product>,
        context: Activity,
        client: String?,
        rider: String?,
        load: Loading,
        doc: CollectionReference,
        addrGestor: String
    ) {
        var entry2 = makeFsOrder(productList, context, client, rider, addrGestor)

        doc.add(entry2)
                .addOnSuccessListener {
                    productList.forEach { prod ->
                        removeProductFromCart(prod, context)
                    }
                }
                .addOnCompleteListener {
                    entry2["ordine"] = it.result.id
                    doc.document(it.result.id).set(entry2)

                    load.stopLoadingDialog()
                    val newMessage = mapOf<String, String>(
                        "gestore" to entry2["proprietario"].toString(),
                        "numero_ordine" to it.result.id,
                        "cliente" to client!!,
                        "Testo" to "Has Been Added: ${productList.size.toString()} Product to the new Order!"
                    )
                    sendNotification(
                        context,
                        entry2["proprietario"].toString(),
                        newMessage
                    ).addOnCompleteListener {
                        val intent = Intent(context, CartProductListActivity::class.java)
                        context.startActivity(intent)
                    }
                }
                .addOnFailureListener {
                    load.stopLoadingDialog()
                    Toast.makeText(
                            context,
                            "Error during Product Order",
                            Toast.LENGTH_LONG
                    ).show()
                }
    }

    private fun makeFsOrder(
        productList: ArrayList<ProductListActivity.Product>,
        context: Activity,
        client: String?,
        rider: String?,
        addrGestor: String
    ): HashMap<String, Any?> {
        var entry2: HashMap<String, Any?> = hashMapOf()
        entry2["cliente"] = client
        entry2["rider"] = rider
        entry2["proprietario"] = productList[0].owner
        entry2["riderStatus"] = context.getString(R.string.rider_status_NA)
        entry2["addrGestore"] = addrGestor
        entry2["addrCliente"] = MyLocation.address
        entry2["orderStatus"] = context.getString(R.string.order_status_working)
        var priceTot = 0.0
        var owner = ""

        productList.forEachIndexed { i, prod ->
            entry2["prod_name_$i"] = prod.name
            entry2["prod_qty_$i"] = prod.quantity
            owner = prod.owner
            priceTot += (prod.price * prod.quantity)
        }
        entry2["prezzo_tot"] = priceTot

        return entry2
    }

    /**
     * Aggiorna un ordine esistente
     */
    fun updateOrder(context: Activity, order: Order): Task<Void> {
        val load = Loading(context)
        load.startLoading()

        var entry: HashMap<String, Any?> = hashMapOf()
        entry["ordine"] = order.ordine
        entry["cliente"] = order.cliente
        entry["rider"] = order.rider
        entry["proprietario"] = order.proprietario
        entry["prezzo_tot"] = order.prezzo_tot
        entry["riderStatus"] = order.riderStatus
        entry["addrGestore"] = order.addrGestor
        entry["addrCliente"] = order.addrClient
        entry["orderStatus"] = order.orderStatus

        if(order.deliveryStatus != null) {
            entry["deliveryStatus"] = order.deliveryStatus
            entry["clientRatingCourtesy"] = order.clientRatingCourtesy
            entry["clientRatingPresence"] = order.clientRatingPresence
        }

        var i=0

        order.products.forEach { (key, value) ->
            entry["prod_name_"+i] = key
            entry["prod_qty_"+i] = value
            i++
        }

        return db.collection("/ordini")
                .document(order.ordine)
                .set(entry)
                .addOnCompleteListener {
                    load.stopLoadingDialog()
                }
                .addOnFailureListener {
                    load.stopLoadingDialog()
                    Toast.makeText(
                            context,
                            "Error during Product Order",
                            Toast.LENGTH_LONG
                    ).show()
                }
    }

}