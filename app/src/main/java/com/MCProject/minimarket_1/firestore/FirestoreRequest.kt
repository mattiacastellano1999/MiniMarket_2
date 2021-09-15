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
import com.MCProject.minimarket_1.gestor.OrderList
import com.MCProject.minimarket_1.rider.RiderActivity.Companion.myOrder
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

        Log.i("HEY", "Path: $from")
        val ret = db.collection(from)
                .get()
                .addOnCompleteListener {@Synchronized
                    if ( it.isSuccessful) {

                        if( !it.result.isEmpty) {
                            for (doc in it.result) {
                                Log.i("HEY", "Doc: "+doc.data)
                                orderList.add(parseOrder(doc))
                            }
                        } else {
                            Log.e("HEY", "Error with Path")
                        }
                    } else {
                        Log.e("HEY", "Error Firetore Marker Reading_0")
                    }
                    return@addOnCompleteListener
                }
                .addOnFailureListener {
                    Log.e("HEY", "Error Firetore Marker Reading")
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
            Log.i("HEY", "While Doc: "+doc.data["prod_name_"+i])
            myOrder.products.set(
                doc.data["prod_name_" + i].toString(),
                doc.data["prod_qty_" + i].toString()
            )
            i++
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

                Log.i("HEY", "data adding_13 path:$pathToMyProduct")
                if ( it.isSuccessful) {
                    var i = 0
                    if( !it.result.isEmpty) {
                        for (doc in it.result) {
                            Log.i("HEY", "data adding_12:${doc.data["nome"]}")
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
                        Log.i("HEY", "data adding_14:${productList.size}")
                    } else {
                        Log.e("HEY", "Error with Path")
                        load.stopLoadingDialog()
                    }
                } else {
                    Log.e("HEY", "Error Firetore Marker Reading_0")
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
                Log.e("HEY", "Error Firetore Marker Reading")
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

        Log.i("HEY", "download")
        val pathReference = imgDb!!.reference.child("images/$prod.jpg")
        Log.i("HEY", "path: images/$prod")

        return pathReference
            .getBytes(9024 * 9024)
            .addOnSuccessListener{
                Log.i("HEY", "ok")
                val bytes = ByteArrayOutputStream()
                val bpm = BitmapFactory.decodeByteArray(it, 0, it.size)
                //bpm.compress(Bitmap.CompressFormat.JPEG, 100, bytes)
                val path = MediaStore.Images.Media.insertImage(context.contentResolver, bpm, "Title", null)
                if(productList.isNotEmpty())
                    productList[i].img = Uri.parse(path)
                else
                    Log.e("HEY", "Error: List Empty")
                Log.i("HEY", "data adding_1 end")

                return@addOnSuccessListener
                //Creazione Lista
                //listAdapter = ProductAdapter()
            }
            .addOnFailureListener {
                Log.i("HEY", "error:$it")
                Toast.makeText(
                    context,
                    "Filed to Download",
                    Toast.LENGTH_LONG
                ).show()
                return@addOnFailureListener
            }
            .addOnCanceledListener {
                Log.i("HEY", "error2")
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
            Log.i("HEY", "Removing : "+pathToMyProduct+"/"+elementToDelete)
            db.collection(pathToMyProduct)
                .document(elementToDelete)
                .delete()
                .addOnSuccessListener {
                    Log.i("HEY", "Removed")
                    Toast.makeText(
                        context,
                        "New Message !",
                        Toast.LENGTH_LONG
                    ).show()
                }
                .addOnFailureListener {
                    Log.i("HEY", "Failed")
                    Toast.makeText(
                        context,
                        "Error during Document deleting",
                        Toast.LENGTH_LONG
                    ).show()
                }
            Log.i("HEY", "End Delete")
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
                Log.i("HEY", "Removed")
                Toast.makeText(
                    context,
                    "Product Correctly Removed",
                    Toast.LENGTH_LONG
                ).show()
            }
            .addOnFailureListener {
                Log.i("HEY", "Failed")
                Toast.makeText(
                    context,
                    "Error during Document deleting",
                    Toast.LENGTH_LONG
                ).show()
            }
        Log.i("HEY", "End Delete")
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun sendNotificationToGestor(
        context: Activity,
        sender: String?,
        receiver: String,
        message: String,
        orderN: String
    ) {
        Log.i("HEY", "Invio: "+ message)
        val fm = FirebaseMessaging(MainActivity.mail, context)
        fm.sendMessageToGestor(context, sender!!, receiver, message, orderN)

        //reload activity
        val intent = Intent(context, CartProductListActivity::class.java)
        context.startActivity(intent)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun sendNotificationToRider(
            context: Activity,
            sender: String?,
            receiver: String,
            message: String,
            orderN: String
    ) {
        Log.i("HEY", "Invio: "+ message)

        val fm = FirebaseMessaging(MainActivity.mail, context)
        fm.sendMessageToRider(context, sender!!, receiver, message, orderN)

        //reload activity
        val intent = Intent(context, OrderList::class.java)
        context.startActivity(intent)
    }

    fun getAllElement(context: Activity, elements: ArrayList<String>): Task<QuerySnapshot> {
        Log.i("HEY", "Start Getting Riders")
        val load = Loading(context)
        load.startLoading()
        elements.clear()

        val ret = db.collection(pathToMyProduct)
                .get()
                .addOnCompleteListener {
                    if ( it.isSuccessful) {
                        if( !it.result.isEmpty) {
                            for (doc in it.result) {
                                Log.i("HEY", "data adding_12:${doc.data}")
                                if (doc.data["status"].toString() == "1") {
                                    elements.add(
                                        doc.data["email"].toString()
                                    )
                                }
                            }
                        } else {
                            Log.e("HEY", "Error with Path")
                            load.stopLoadingDialog()
                        }
                    } else {
                        Log.e("HEY", "Error Firetore Marker Reading_0")
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
                    Log.e("HEY", "Error Firetore Marker Reading")
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

