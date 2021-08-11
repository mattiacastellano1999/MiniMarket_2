package com.MCProject.minimarket_1.access.util

import android.app.Activity
import android.app.ProgressDialog
import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import com.MCProject.minimarket_1.access.JoinUs
import com.MCProject.minimarket_1.access.Loading
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.storage.FirebaseStorage
import java.io.ByteArrayOutputStream


class FirestoreRequest(
    var db: FirebaseFirestore,
    var auth: FirebaseAuth,
    var imgDb: FirebaseStorage?,
    var collection: String,
    var mail: String?
) {

    lateinit var pathToMyProduct: String

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
        Log.i("HEY","BEfore Collection Group ... "+ profile)
        db.collection("/profili/$profile/dati")
            .get()
            .addOnCompleteListener {

                for (doc in it.result) {
                    Log.i("HEY",""+doc.id)
                    arrayProfile.add(doc.id)
                }
                load.stopLoadingDialog()
            }
            .addOnFailureListener {
                Log.e("HEY", "Error Firestore Marker Reading")
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
                Log.i("HEY", "Added")
                Toast.makeText(
                    activity,
                    "Document Added Correctly",
                    Toast.LENGTH_LONG
                ).show()
                return@addOnSuccessListener
            }
            .addOnFailureListener {
                Log.i("HEY", "Failed")
                Toast.makeText(
                    activity,
                    "Error during Document data upload",
                    Toast.LENGTH_LONG
                ).show()
                return@addOnFailureListener
            }
    }


    fun addData(
        from: String,
        context: Activity,
        productList: ArrayList<ProductListActivity.Product>
    ): Task<QuerySnapshot> {

        Log.e("HEY", "CALLING ADDDATA")
        val load = Loading(context)
        load.startLoading()
        productList.clear()

        //read data firestore
        if(from.equals("cart")) {
            pathToMyProduct =  "/profili/$collection/$from/$mail/prodotti"
        } else {
            pathToMyProduct =  "/profili/$collection/$from/$mail/miei_prodotti"
        }
        val ret = db.collection(pathToMyProduct)
            .get()
            .addOnCompleteListener {
                if ( it.isSuccessful) {
                    var i = 0
                    if( !it.result.isEmpty) {
                        for (doc in it.result) {
                            Log.i("HEY", "data adding_12:$doc")
                            productList.add(
                                ProductListActivity.Product(
                                    i,
                                    null,
                                    doc.data["nome"].toString(),
                                    doc.data["descrizione"].toString(),
                                    doc.data["prezzo"].toString().toDouble(),
                                    doc.data["quantita"].toString().toInt()
                                )
                            )
                            i++
                            Log.i("HEY", "data adding_11:" + doc.data.toString())
                        }
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

    fun deleteFromDB(context: Activity, prodName: String?) {
        if(collection.equals("utenti")) {
            pathToMyProduct =  "/profili/$collection/cart/$mail/prodotti"
        } else {
            pathToMyProduct =  "/profili/$collection/market/$mail/miei_prodotti"
        }
        if(prodName != null) {
            db.collection(pathToMyProduct)
                .document(prodName)
                .delete()
                .addOnSuccessListener {
                    Log.i("HEY", "Removed")
                    Toast.makeText(
                        context,
                        "Product Correctly Removed",
                        Toast.LENGTH_LONG
                    ).show()
                    val intent = Intent(context, ProductListActivity::class.java)
                    context.startActivity(intent)
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

    fun uploadProduct(context: Activity, prod: ProductListActivity.Product): Task<Void> {
        Log.i("HEY", "Start Upload Data")
        if(collection.equals("utenti")) {
            pathToMyProduct =  "/profili/$collection/cart/$mail/prodotti"
        } else {
            pathToMyProduct =  "/profili/$collection/market/$mail/miei_prodotti"
        }
        val entry: HashMap<String, Any?> = hashMapOf<String, Any?>(
            "nome" to prod.name,
            "prezzo" to prod.price,
            "quantita" to prod.quantity
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
        prod: ProductListActivity.Product
    ): Task<DocumentSnapshot> {
        val load = Loading(context)
        load.startLoading()

        var entry1: HashMap<String, Any?> = hashMapOf<String, Any?>(
            "nome" to prod.name,
            "prezzo" to prod.price,
            "quantita" to 1
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
            "quantita" to prod.quantity - 1
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

}

