package com.MCProject.minimarket_1.user

import android.app.Activity
import android.app.AlertDialog
import android.view.View
import android.widget.*
import com.MCProject.minimarket_1.R
import com.MCProject.minimarket_1.access.util.ProductListActivity

/**
 * classe dedicata lla creazione e gestione del popup che permette
 * all'utente di leggere la descrizione del prodotto e selezionarne la quantità
 */
class UserPopup (
    val context: Activity,
    val prod: ProductListActivity.Product,
    ): Activity() {

    var popupView: View
    var popupNameTV : TextView
    var popupQuantityNP: NumberPicker
    var popupImgIB: ImageButton
    var popupCancelBTN: Button
    var popupConfirmBTN: Button
    var popupDescriptionED: TextView

    companion object {
        lateinit var dialogBuilder: AlertDialog.Builder
        lateinit var dialog: AlertDialog
    }

    init {
        dialogBuilder = AlertDialog.Builder(context)

        popupView = context.layoutInflater.inflate(R.layout.form_user_product, null)

        popupNameTV = popupView.findViewById(R.id.nome_tv)
        popupQuantityNP = popupView.findViewById(R.id.number_picker)
        popupDescriptionED = popupView.findViewById(R.id.descr_tv)
        popupImgIB = popupView.findViewById(R.id.icon_prod_iv)

        popupCancelBTN = popupView.findViewById(R.id.cancle_btn)
        popupConfirmBTN = popupView.findViewById(R.id.confirm_btn)

        dialogBuilder.setView(popupView)
        dialog = dialogBuilder.create()
    }

    fun listenerInit () {
        dialog.show()
        popupQuantityNP.maxValue = prod.quantity
        popupQuantityNP.minValue = 0
        popupQuantityNP.value = 0

        popupNameTV.text = prod.name
        popupDescriptionED.text = prod.description
        popupImgIB.setImageURI(prod.img)
    }
}
