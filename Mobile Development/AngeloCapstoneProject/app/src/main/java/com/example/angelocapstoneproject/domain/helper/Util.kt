package com.example.angelocapstoneproject.domain.helper

import android.content.Context
import android.util.DisplayMetrics
import android.util.TypedValue
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatSpinner
import androidx.appcompat.widget.ListPopupWindow
import com.bumptech.glide.Glide
import com.example.angelocapstoneproject.ui.home.adapter.SpCamAdapter

fun AppCompatImageView.load(imageRes:Int) {
    Glide.with(context)
        .load(imageRes)
        .into(this)
}

fun Int.toDp(displayMetrics: DisplayMetrics):Int{
    return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, this.toFloat(), displayMetrics).toInt()
}

/*fun AppCompatSpinner.setInit(context: Context, listItems:List<String>){
    val adapter = SpCamAdapter(context, listItems)
    setScrollable()
    this.adapter = adapter
}*/


fun AppCompatSpinner.setScrollable(){
    val popupField = AppCompatSpinner::class.java.getDeclaredField("mPopup")
    popupField.isAccessible = true

    val popUpWindow = popupField.get(this) as ListPopupWindow
    val displayMetrics = this.context.resources.displayMetrics
    popUpWindow.height = 100.toDp(displayMetrics)

}