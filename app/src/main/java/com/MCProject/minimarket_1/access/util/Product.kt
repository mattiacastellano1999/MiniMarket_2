package com.MCProject.minimarket_1.access.util

import android.net.Uri

/**
 * classe format per definire i prodotti e l'array di prodotti
 */

class Product {
    companion object {
        data class Product(var id: Int, var img: Uri?, var name: String, var price: Double, var quantity: Int)
        var productList = ArrayList<Product>()

        /*fun loadDataFromFS(context: Activity) {
            FirestoreRequest.addData(context, productList)
        }*/
    }
}