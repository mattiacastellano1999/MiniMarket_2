package com.MCProject.minimarket_1.util

/**
 * Classe Data degli ordini
 */
class Order(
        var ordine: String,
        var prezzo_tot: Double,
        var proprietario: String,
        var cliente: String,
        var addrClient: String,
        var addrGestor: String,
        var rider: String,
        var riderStatus: String,
        var orderStatus: String,
        var products: HashMap<String, String>
        ){

}