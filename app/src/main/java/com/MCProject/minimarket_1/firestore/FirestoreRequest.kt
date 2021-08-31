package com.MCProject.minimarket_1.firestore

import android.app.Activity
import android.app.ProgressDialog
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
import com.MCProject.minimarket_1.access.util.FirebaseMessaging
import com.MCProject.minimarket_1.access.util.ProductListActivity
import com.MCProject.minimarket_1.user.CartProductListActivity
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
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
            db.collection(pathToMyProduct)
                .document(elementToDelete)
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
    fun sendNotification(
        context: Activity,
        sender: String?,
        receiver: String,
        message: String
    ) {
        Log.i("HEY", "Invio: "+ message)
        val fm = FirebaseMessaging(MainActivity.mail)
        fm.sendMesage(context, sender!!, receiver, message)

        //reload activity
        val intent = Intent(context, CartProductListActivity::class.java)
        context.startActivity(intent)
    }

}

