package com.MCProject.minimarket_1.access

import android.app.Activity
import androidx.appcompat.app.AlertDialog
import com.MCProject.minimarket_1.R

class Loading(val activity: Activity) {

    private lateinit var dialog: AlertDialog

    fun startLoading() {
        val builder = AlertDialog.Builder(activity)

        val inflater = activity.layoutInflater

        builder.setView(inflater.inflate(R.layout.loading_dialog, null))
        builder.setCancelable(true)//cliccando fuori dal loading lo annulla

        dialog = builder.create()
        dialog.show()
    }

    fun stopLoadingDialog(){
        dialog.dismiss()
    }
}