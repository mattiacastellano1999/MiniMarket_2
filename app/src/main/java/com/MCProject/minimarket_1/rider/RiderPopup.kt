package com.MCProject.minimarket_1.rider

import android.app.Activity
import android.app.AlertDialog
import android.view.View
import android.widget.*
import com.MCProject.minimarket_1.R
import com.MCProject.minimarket_1.util.Order

class RiderPopup (
    val context: Activity,
    val order: Order,
    ): Activity() {

    var popupView: View

    var rb_delivered: RadioButton
    var rb_notdelivered: RadioButton
    var rb_refused: RadioButton

    var raiting_cortesia: RatingBar
    var raiting_casa: RatingBar


    var popupCancelBTN: Button
    var popupConfirmBTN: Button

    companion object {
        lateinit var dialogBuilder: AlertDialog.Builder
        lateinit var dialog: AlertDialog
    }

    init {
        dialogBuilder = AlertDialog.Builder(context)

        popupView = context.layoutInflater.inflate(R.layout.form_rider_delivend, null)

        rb_delivered = popupView.findViewById(R.id.rb_delivered)
        rb_notdelivered = popupView.findViewById(R.id.rb_notdelivered)
        rb_refused = popupView.findViewById(R.id.rb_refused)

        raiting_cortesia = popupView.findViewById(R.id.raiting_cortesia)
        raiting_casa = popupView.findViewById(R.id.raiting_casa)
        popupCancelBTN = popupView.findViewById(R.id.cancle_btn)
        popupConfirmBTN = popupView.findViewById(R.id.confirm_btn)

        dialogBuilder.setView(popupView)
        dialog = dialogBuilder.create()
    }

    fun listenerInit () {
        dialog.show()

        popupCancelBTN.setOnClickListener {
            dialog.dismiss()
        }
    }
}
