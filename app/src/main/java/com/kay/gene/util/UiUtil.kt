package com.kay.gene.util


import android.content.Context
import android.widget.Toast

object UiUtil {

    fun showToast(context : Context,message : String){
        Toast.makeText(context,message,Toast.LENGTH_LONG).show()
    }
}
//Toast.makeText(this@YOUR_ACTIVITY_NAME, "Clicked on  " + users.get(position).name, Toast.LENGTH_LONG).show()