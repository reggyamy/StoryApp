package com.reggya.stroryapp.ui

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import com.reggya.stroryapp.R

class ProgressDialog(context: Context) {

    private var dialog : Dialog? = null
    init {
        dialog = Dialog(context)
        init()
    }

    private fun init(){
        dialog?.setContentView(R.layout.progressbar)
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
    }

    fun showProgressBar(){
        dialog?.isShowing
        dialog?.show()
    }

    fun dismissProgressBar(){
        dialog?.dismiss()
    }
}