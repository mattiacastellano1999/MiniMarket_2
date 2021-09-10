package com.MCProject.minimarket_1.util

class Order(
        var nome_ordine: String,
        var prezzo_tot: Double,
        var proprietario: String,
        var cliente: String,
        var addrClient: String,
        var addrGestor: String,
        var rider: String,
        var riderStatus: String,
        var products: HashMap<String, String>
        ){

}