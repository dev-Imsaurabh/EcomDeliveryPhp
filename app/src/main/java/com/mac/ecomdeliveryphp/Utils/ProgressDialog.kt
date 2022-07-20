package com.mac.ecomadminphp.Utils

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.TextureView
import android.widget.TextView
import com.mac.ecomdeliveryphp.R

class ProgressDialog {
    companion object {
        fun progressDialog(context: Context,msg:String): Dialog {
            val dialog = Dialog(context)
            val inflate = LayoutInflater.from(context).inflate(R.layout.progress_dialog, null)
            dialog.setContentView(inflate)
            dialog.setCancelable(false)
            val textView: TextView = dialog.findViewById(R.id.msgBox)
            textView.setText(msg)
            dialog.window!!.setBackgroundDrawable(
                ColorDrawable(Color.TRANSPARENT)
            )
            return dialog
        }
    }
}