package com.MCProject.minimarket_1.firestore

import android.app.Activity
import android.app.ProgressDialog
import android.util.Log
import android.widget.Toast
import com.MCProject.minimarket_1.access.Loading
import com.MCProject.minimarket_1.util.ProductListActivity
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.storage.FirebaseStorage

class FirestoreRequest_Marketplace(
        override var db: FirebaseFirestore,
        override var auth: FirebaseAuth,
        override var imgDb: FirebaseStorage?,
        override var collection: String,
        override var mail: String?
): FirestoreRequest(db, auth, imgDb, collection, mail) {

    fun uploadProduct(context: Activity, prod: ProductListActivity.Product): Task<Void> {
        Log.i("HEY", "Start Upload Data")
        if(collection.equals("utenti")) {
            pathToMyProduct =  "/profili/$collection/cart/$mail/prodotti"
        } else {
            pathToMyProduct =  "/profili/$collection/market/$mail/miei_prodotti"
        }
        val entry: HashMap<String, Any?> = hashMapOf<String, Any?>(
                "nome" to prod.name,
                "descrizione" to prod.description,
                "prezzo" to prod.price,
                "quantita" to prod.quantity,
                "proprietario" to prod.owner
        )

        val pd = ProgressDialog(context)
        return db.collection(pathToMyProduct)
                .document(prod.name)
                .set(entry)
                .addOnSuccessListener {
                    pd.dismiss()
                    Log.i("HEY", "Added")
                    Toast.makeText(
                            context,
                            "Document Added Correctly",
                            Toast.LENGTH_LONG
                    ).show()
                    return@addOnSuccessListener
                }
                .addOnFailureListener {
                    pd.dismiss()
                    Log.i("HEY", "Failed")
                    Toast.makeText(
                            context,
                            "Error during Document data upload",
                            Toast.LENGTH_LONG
                    ).show()
                    return@addOnFailureListener
                }
    }

    fun addDataIntoCart(
        context: Activity,
        userEmail: String,
        gestorEmail: String,
        prod: ProductListActivity.Product,
        value: Int
    ): Task<DocumentSnapshot> {
        val load = Loading(context)
        load.startLoading()

        var entry1: HashMap<String, Any?> = hashMapOf<String, Any?>(
                "nome" to prod.name,
                "prezzo" to prod.price,
                "quantita" to value,
                "descrizione" to prod.description,
                "proprietario" to prod.owner
        )

        //aggiungo il prodotto in cart
        val fs = db.collection("/profili/utenti/cart/$userEmail/prodotti").document(prod.name)

        val added = fs.get()
                .addOnSuccessListener {
                    if( it.data.isNullOrEmpty()){
                        Log.i("HEY", "PreAdded")
                        fs.set(entry1)
                                .addOnSuccessListener {
                                    Log.i("HEY", "Added: "+it)
                                    Toast.makeText(
                                            context,
                                            "Document Added Correctly",
                                            Toast.LENGTH_LONG
                                    ).show()
                                    load.stopLoadingDialog()
                                }
                                .addOnFailureListener {
                                    Log.i("HEY", "Failed"+ it)
                                    Toast.makeText(
                                            context,
                                            "Error during Document data upload",
                                            Toast.LENGTH_LONG
                                    ).show()
                                    load.stopLoadingDialog()
                                }
                    } else {
                        Log.i("HEY", "prodotto in cart   " + it)
                        fs.update("quantita", FieldValue.increment(1))
                        load.stopLoadingDialog()
                    }
                }
                .addOnFailureListener {
                    Log.i("HEY", "prodotto in cart   " +it)
                    load.stopLoadingDialog()
                }



        //tolgo 1 prodotto dal market

        var entry2: HashMap<String, Any?> = hashMapOf<String, Any?>(
                "nome" to prod.name,
                "prezzo" to prod.price,
                "descrizione" to prod.description,
                "proprietario" to prod.owner,
                "quantita" to prod.quantity - value,
        )

        val ret = added.addOnSuccessListener {
            db.collection("/profili/gestori/market/$gestorEmail/miei_prodotti")
                    .document(prod.name)
                    .set(entry2)
                    .addOnSuccessListener {
                        load.stopLoadingDialog()
                        Log.i("HEY", "Added")
                        Toast.makeText(
                                context,
                                "Document Added Correctly",
                                Toast.LENGTH_LONG
                        ).show()
                    }
                    .addOnFailureListener {
                        load.stopLoadingDialog()
                        Log.i("HEY", "Failed")
                        Toast.makeText(
                                context,
                                "Error during Document data upload",
                                Toast.LENGTH_LONG
                        ).show()
                    }
        }
        return ret
    }


    override fun addData(
            path: String,
            context: Activity,
            productList: ArrayList<ProductListActivity.Product>
    ): Task<QuerySnapshot> {

        pathToMyProduct = path

        return super.addData(path, context, productList)
    }

    fun getAllRiderAviable(context: Activity, riderAviable: ArrayList<String>): Task<QuerySnapshot> {
        pathToMyProduct = "profili/riders/dati"

        return getAllElement(context, riderAviable)
    }

}