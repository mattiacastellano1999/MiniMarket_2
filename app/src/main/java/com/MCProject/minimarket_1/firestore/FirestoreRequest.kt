package com.MCProject.minimarket_1.firestore

import android.app.Activity
import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import com.MCProject.minimarket_1.MainActivity
import com.MCProject.minimarket_1.access.Loading
import com.MCProject.minimarket_1.util.FirebaseMessaging
import com.MCProject.minimarket_1.util.Order
import com.MCProject.minimarket_1.util.ProductListActivity
import com.MCProject.minimarket_1.user.CartProductListActivity
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QueryDocumentSnapshot
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.storage.FirebaseStorage
import java.io.ByteArrayOutputStream


open class FirestoreRequest(
        open var db: FirebaseFirestore,
        open var auth: FirebaseAuth,
        open var imgDb: FirebaseStorage?,
        open var collection: String,
        open var mail: String?
) {

    lateinit var pathToMyProduct: String

    /**
     * esgue la GET di tutti gli ordini memorizzati su FB
     */
    fun addOrderData(
            from: String,
            context: Activity,
            orderList: ArrayList<Order>
    ): Task<QuerySnapshot> {
        orderList.clear()

        val ret = db.collection(from)
                .get()
                .addOnCompleteListener {
                    return@addOnCompleteListener
                }
                .addOnFailureListener {
                    return@addOnFailureListener
                }
        return ret
    }

    @Synchronized
    fun parseOrder(doc: QueryDocumentSnapshot): Order {
        val myOrder = Order(
            doc.data["ordine"].toString(),
            doc.data["prezzo_tot"].toString().toDouble(),
            doc.data["proprietario"].toString(),
            doc.data["cliente"].toString(),
            doc.data["addrCliente"].toString(),
            doc.data["addrGestore"].toString(),
            doc.data["rider"].toString(),
            doc.data["riderStatus"].toString(),
            doc.data["orderStatus"].toString(),
            HashMap<String, String>()
        )
        var i = 0
        while(doc.data.containsKey("prod_name_$i")) {
            myOrder.products.set(
                doc.data["prod_name_" + i].toString(),
                doc.data["prod_qty_" + i].toString()
            )
            i++
        }

        if(doc.data.containsKey("deliveryStatus")){
            myOrder.deliveryStatus = doc.data["deliveryStatus"].toString()
            myOrder.clientRatingCourtesy = doc.data["clientRatingCourtesy"].toString().toInt()
            myOrder.clientRatingPresence = doc.data["clientRatingPresence"].toString().toInt()
        }

        return myOrder
    }

    /**
     * Esegue la GET di tutti i prodotti di un determinato utente contenuti in Firestore
     */
    open fun addData(
        from: String,
        context: Activity,
        productList: ArrayList<ProductListActivity.Product>
    ): Task<QuerySnapshot> {

        val load = Loading(context)
        load.startLoading()
        productList.clear()

        val ret = db.collection(pathToMyProduct)
            .get()
            .addOnCompleteListener {

                if ( it.isSuccessful) {
                    var i = 0
                    if( !it.result.isEmpty) {
                        for (doc in it.result) {
                            productList.add(
                                    ProductListActivity.Product(
                                            i,
                                            null,
                                            doc.data["nome"].toString(),
                                            doc.data["descrizione"].toString(),
                                            doc.data["prezzo"].toString().toDouble(),
                                            doc.data["quantita"].toString().toInt(),
                                            doc.data["proprietario"].toString()
                                    )
                            )
                            i++
                        }
                    } else {
                        load.stopLoadingDialog()
                    }
                } else {
                    Toast.makeText(
                        context,
                        "Database Reding Error_0",
                        Toast.LENGTH_LONG
                    ).show()
                }
                load.stopLoadingDialog()
                return@addOnCompleteListener
            }
            .addOnFailureListener {
                Toast.makeText(
                    context,
                    "Database Reding Error",
                    Toast.LENGTH_LONG
                ).show()
                load.stopLoadingDialog()
                return@addOnFailureListener
            }
        return ret
    }

    fun addDataImg(
        context: Activity,
        prod: String,
        i: Int,
        productList: ArrayList<ProductListActivity.Product>
    ): Task<ByteArray> {

        val pathReference = imgDb!!.reference.child("images/$prod.jpg")

        return pathReference
            .getBytes(9024 * 9024)
            .addOnSuccessListener{
                val bytes = ByteArrayOutputStream()
                val bpm = BitmapFactory.decodeByteArray(it, 0, it.size)
                //bpm.compress(Bitmap.CompressFormat.JPEG, 100, bytes)
                val path = MediaStore.Images.Media.insertImage(context.contentResolver, bpm, "Title", null)
                if(productList.isNotEmpty())
                    productList[i].img = Uri.parse(path)
                else

                return@addOnSuccessListener
            }
            .addOnFailureListener {
                Toast.makeText(
                    context,
                    "Filed to Download",
                    Toast.LENGTH_LONG
                ).show()
                return@addOnFailureListener
            }
            .addOnCanceledListener {
                Toast.makeText(
                    context,
                    "Filed to Download",
                    Toast.LENGTH_LONG
                ).show()
                return@addOnCanceledListener
            }
    }

    fun deleteFromDB(context: Activity, elementToDelete: String?, path: String?) {
        if( path == null) {
            if (collection.equals("utenti")) {
                pathToMyProduct = "/profili/$collection/cart/$mail/prodotti"
            } else {
                pathToMyProduct = "/profili/$collection/market/$mail/miei_prodotti"
            }
        } else {
            pathToMyProduct = path
        }
        if(elementToDelete != null) {
            db.collection(pathToMyProduct)
                .document(elementToDelete)
                .delete()
                .addOnSuccessListener {
                    Toast.makeText(
                        context,
                        "New Message !",
                        Toast.LENGTH_LONG
                    ).show()
                }
                .addOnFailureListener {
                    Toast.makeText(
                        context,
                        "Error during Document deleting",
                        Toast.LENGTH_LONG
                    ).show()
                }
        }
    }

    fun deleteFromDB(context: Activity, elementToDelete: String?) {
        deleteFromDB(context, elementToDelete, null)
    }

    fun removeProductFromCart(prod: ProductListActivity.Product, context: Activity) {
        pathToMyProduct =  "/profili/utenti/cart/$mail/prodotti"
        db.collection(pathToMyProduct)
            .document(prod.name)
            .delete()
            .addOnSuccessListener {
                Toast.makeText(
                    context,
                    "Product Correctly Removed",
                    Toast.LENGTH_LONG
                ).show()
            }
            .addOnFailureListener {
                Toast.makeText(
                    context,
                    "Error during Document deleting",
                    Toast.LENGTH_LONG
                ).show()
            }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun sendNotification(
        context: Activity,
        dest: String,
        message: Map<String, String>
    ): Task<Void> {

        val fm = FirebaseMessaging(MainActivity.mail, context)
        return fm.sendMesage(context, dest, message)
    }

    fun getAllElement(context: Activity, elements: ArrayList<String>): Task<QuerySnapshot> {
        val load = Loading(context)
        load.startLoading()
        elements.clear()

        val ret = db.collection(pathToMyProduct)
                .get()
                .addOnCompleteListener {
                    if ( it.isSuccessful) {
                        if( !it.result.isEmpty) {
                            for (doc in it.result) {
                                if (doc.data["status"].toString() == "1") {
                                    elements.add(
                                        doc.data["email"].toString()
                                    )
                                }
                            }
                        } else {
                            load.stopLoadingDialog()
                        }
                    } else {
                        Toast.makeText(
                                context,
                                "Database Reding Error_0",
                                Toast.LENGTH_LONG
                        ).show()
                    }
                    load.stopLoadingDialog()
                    return@addOnCompleteListener
                }
                .addOnFailureListener {
                    Toast.makeText(
                            context,
                            "Database Reding Error",
                            Toast.LENGTH_LONG
                    ).show()
                    load.stopLoadingDialog()
                    return@addOnFailureListener
                }
        return ret
    }

}

