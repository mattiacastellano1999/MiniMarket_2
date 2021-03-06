package com.MCProject.minimarket_1.gestor

import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.provider.MediaStore.Images
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.MCProject.minimarket_1.MainActivity.Companion.collection
import com.MCProject.minimarket_1.MainActivity.Companion.db
import com.MCProject.minimarket_1.MainActivity.Companion.mail
import com.MCProject.minimarket_1.R
import com.MCProject.minimarket_1.util.ProductListActivity
import com.MCProject.minimarket_1.firestore.FirestoreRequest_Marketplace
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.StorageTask
import com.google.firebase.storage.UploadTask
import java.io.ByteArrayOutputStream

/**
 * classe dedicata alla creazione e gestione del popup che permette
 * ai gestori gestire i propri prodotti
 */
@Suppress("DEPRECATION")
class GestorPopup(
    val context: Activity,
    val prodList: ArrayList<ProductListActivity.Product>,
): Activity() {

    companion object {
        lateinit var dialogBuilder: AlertDialog.Builder
        lateinit var dialog: AlertDialog
    }
    var popupNameED :EditText
    var popupQuantityED: EditText
    var popupPriceED: EditText
    var popupImgIB: ImageButton
    var popupCancelBTN: Button
    var popupConfirmBTN: Button
    var popupDescriptionED: EditText
    var myOldProd: ProductListActivity.Product? = null

    var imageUri: Uri? = null
    var oldProd: String? = null
    private var storage: FirebaseStorage = FirebaseStorage.getInstance()
    private var storageReference: StorageReference = storage.reference
    var popupView: View
    var oldImg: Uri? = null
    val PERMISSION_CODE = 100;
    val IMAGE_CAPTURE_CODE = 101
    val IMAGE_LOCAL_CODE = 102


    val REQUEST_FOR_CREATING = 11
    val REQUEST_FOR_EDITING = 22
    var REQUEST = 0


    var data: Intent? = null

    init {
        dialogBuilder = AlertDialog.Builder(context)

        popupView = context.layoutInflater.inflate(R.layout.form_add_product, null)

        popupNameED = popupView.findViewById(R.id.nome_ed)
        popupQuantityED = popupView.findViewById(R.id.qty_ed)
        popupDescriptionED = popupView.findViewById(R.id.descr_ed)
        popupPriceED = popupView.findViewById(R.id.price_ed)
        popupImgIB = popupView.findViewById(R.id.icon_prod_iv)

        popupCancelBTN = popupView.findViewById(R.id.cancle_btn)
        popupConfirmBTN = popupView.findViewById(R.id.confirm_btn)

        dialogBuilder.setView(popupView)
        dialog = dialogBuilder.create()
    }

    @SuppressLint("InflateParams")
    fun makePopup(context: Activity) {
        REQUEST = REQUEST_FOR_CREATING
        listenerInit(context)
    }

    fun listenerInit(context: Activity) {
        dialog.show()

        popupImgIB.setOnClickListener {
            choosePicture(context)
        }

        popupConfirmBTN.setOnClickListener {
            if(
                    !popupNameED.text.isNullOrEmpty() &&
                    !popupPriceED.text.isNullOrEmpty() &&
                    !popupQuantityED.text.isNullOrEmpty() &&
                    !popupDescriptionED.text.isNullOrEmpty() &&
                    imageUri != null
            ) {
                val newprod = ProductListActivity.Product(
                    prodList.size + 1,
                    imageUri,
                    popupNameED.text.toString(),
                    popupDescriptionED.text.toString(),
                    popupPriceED.text.toString().toDouble(),
                    popupQuantityED.text.toString().toInt(),
                    mail
                )
                prodList.add(newprod)
                if(myOldProd != null) {
                    ProductListActivity.productList.remove(myOldProd!!)
                }
                val fr = FirestoreRequest_Marketplace(
                    db,
                    FirebaseAuth.getInstance(),
                    FirebaseStorage.getInstance(),
                    collection,
                    mail)
                fr.uploadProduct(context, newprod)
                    .addOnCompleteListener {
                        uploadProductPic(newprod).addOnCompleteListener {
                            dialog.dismiss()
                        }
                    }
                if(!oldProd.equals(newprod.name)) {
                        if(myOldProd != null)
                        fr.deleteFromDB(context, oldProd)
                }
            } else {
                Toast.makeText(
                    this.context,
                    "Please Fill the form",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

        popupCancelBTN.setOnClickListener {
            dialog.dismiss()
        }
    }

    private fun choosePicture(context: Activity) {
        //Show popup camera or local data
        dialog.dismiss()
        AlertDialog.Builder(this.context)
            .setTitle("Take a Picture")
            .setMessage("Choose if take your picture from device Memory or Camera")
            .setNegativeButton("Camera") {dialog: DialogInterface, _: Int ->
                //open camera
                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                    if(
                        ContextCompat.checkSelfPermission(this.context, android.Manifest.permission.CAMERA) == PackageManager.PERMISSION_DENIED  ||
                        this.context.checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED
                    ){
                        ActivityCompat.requestPermissions(
                            this.context,
                            arrayOf(android.Manifest.permission.CAMERA),
                            PERMISSION_CODE
                        )
                    } else {
                        openCamera(this.context)
                    }
                } else {
                    //versione di OS < Marshmallow
                    openCamera(this.context)
                }
                dialog.dismiss()
            }
            .setPositiveButton("InternalStorage") {dialog: DialogInterface, _: Int ->
                //Local Data
                val intent = Intent()
                intent.type = "image/*"
                intent.action = Intent.ACTION_GET_CONTENT
                this.context.startActivityForResult(intent, IMAGE_LOCAL_CODE)
                dialog.dismiss()
            }
            .show()
    }

    fun openCamera(context: Activity) {
        val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        if (cameraIntent.resolveActivity(context.packageManager) != null)
            context.startActivityForResult(cameraIntent, IMAGE_CAPTURE_CODE)
    }


    fun start(context: Activity, data: Intent?, requestCode: Int) {
        this.data = data

        if( requestCode == IMAGE_CAPTURE_CODE ) {
            val photo = data!!.extras!!["data"] as Bitmap
            imageUri = getImageUri(context.applicationContext, photo)
            popupImgIB.setImageURI(imageUri)
            listenerInit(context)
        }
        if(requestCode == IMAGE_LOCAL_CODE ) {
            if ( data!!.data != null ) {
                imageUri = data.data
                popupImgIB.setImageURI(imageUri)
                listenerInit(context)
            } else {
                //error
            }
        }
    }

    private fun getImageUri(inContext: Context, inImage: Bitmap): Uri {
        val bytes = ByteArrayOutputStream()
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes)
        val path = Images.Media.insertImage(inContext.contentResolver, inImage, "Title", null)
        return Uri.parse(path)
    }

    private fun uploadProductPic(newprod: ProductListActivity.Product): StorageTask<UploadTask.TaskSnapshot> {
        val pd = ProgressDialog(context)
        pd.setTitle("Uploading Image ...")
        pd.show()

        val imageRef: StorageReference = storageReference.child("images/${popupNameED.text.toString()}.jpg")

        return imageRef.putFile(imageUri!!)
            .addOnSuccessListener {
                pd.dismiss()
                Toast.makeText(
                    context,
                    "Image Uploaded !",
                    Toast.LENGTH_LONG
                ).show()
            }
            .addOnFailureListener {
                pd.dismiss()
                Toast.makeText(
                    context,
                    "Filed to Upload",
                    Toast.LENGTH_LONG
                ).show()
            }
            .addOnProgressListener {
                val progressPerc = 100.00 * it.bytesTransferred / it.totalByteCount
                pd.setMessage("Progress: $progressPerc %")
            }
            .addOnCompleteListener{
                if (it.isSuccessful) {
                    pd.dismiss()
                    if (REQUEST == REQUEST_FOR_EDITING)
                        if (oldProd != newprod.name)
                            deleteImageFromFS()
                }
            }
    }

    fun deleteImageFromFS(): Task<Void> {
        val imageRef: StorageReference = storageReference.child("images/${oldProd.toString()}.jpg")

        return imageRef.delete()
            .addOnFailureListener {
                Toast.makeText(
                    context,
                    "Problem With Image Replacement",
                    Toast.LENGTH_SHORT
                ).show()
            }
    }


    fun editProduct(prod: ProductListActivity.Product) {
        myOldProd = prod
        oldProd = prod.name
        popupImgIB.setImageURI(prod.img)
        imageUri = prod.img
        popupNameED.setText(prod.name)
        popupDescriptionED.setText(prod.description)
        popupPriceED.setText(prod.price.toString())
        popupQuantityED.setText(prod.quantity.toString())


        REQUEST = REQUEST_FOR_EDITING

        return listenerInit(context)
    }

    fun clearData() {
        oldProd = null
        popupImgIB.setImageURI(null)
        imageUri = null
        popupNameED.setText("")
        popupDescriptionED.setText("")
        popupPriceED.setText("")
        popupQuantityED.setText("")
    }

}

