package com.example.angelocapstoneproject.domain.helper

import android.app.Application
import android.content.Context
import android.content.pm.PackageManager
import android.content.res.Resources
import android.os.Build
import android.util.DisplayMetrics
import android.util.TypedValue
import androidx.annotation.RequiresApi
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatSpinner
import androidx.appcompat.widget.ListPopupWindow
import androidx.core.app.ActivityCompat
import androidx.core.os.ConfigurationCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStoreOwner
import com.bumptech.glide.Glide
import com.example.angelocapstoneproject.ViewModelFactory
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date

fun AppCompatImageView.load(imageRes:Int) {
    Glide.with(context)
        .load(imageRes)
        .into(this)
}

fun Int.toDp(displayMetrics: DisplayMetrics):Int{
    return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, this.toFloat(), displayMetrics).toInt()
}


inline fun <reified T : ViewModel> obtainViewModel(application: Application, owner:ViewModelStoreOwner):T{
    val factory = ViewModelFactory.getInstance(application)
    return ViewModelProvider(owner, factory)[T::class.java]
}

/*fun AppCompatSpinner.setInit(context: Context, listItems:List<String>){
    val adapter = SpCamAdapter(context, listItems)
    setScrollable()
    this.adapter = adapter
}*/

const val DATE_PATTERN = "dd/MM/yyyy"
val simpleDateFormat = SimpleDateFormat(DATE_PATTERN, ConfigurationCompat.getLocales(Resources.getSystem().configuration)[0])

fun String.toDate(format: String): Date {
    val simpleDateFormat = SimpleDateFormat(format, ConfigurationCompat.getLocales(Resources.getSystem().configuration)[0])
    return try {
        simpleDateFormat.parse(this) as Date
    }catch (e:Exception){
        getCurrentDate()
    }
}

fun Date.toString(format: String):String{
    val simpleDateFormat = SimpleDateFormat(format, ConfigurationCompat.getLocales(Resources.getSystem().configuration)[0])
    return try {
        simpleDateFormat.format(this)
    }catch (e:Exception){
        e.printStackTrace()
        getCurrentDateInString("yyyy-MM-dd")!!
    }
}

fun getCurrentDateInString(format:String):String?{
    val simpleDateFormat = SimpleDateFormat(format, ConfigurationCompat.getLocales(Resources.getSystem().configuration)[0])
    val currentDate = getCurrentDate()
    return try {
        simpleDateFormat.format(currentDate)
    }catch (e:Exception){
        null
    }
}

fun getCurrentDate():Date = Calendar.getInstance().time



fun AppCompatSpinner.setScrollable(){
    val popupField = AppCompatSpinner::class.java.getDeclaredField("mPopup")
    popupField.isAccessible = true

    val popUpWindow = popupField.get(this) as ListPopupWindow
    val displayMetrics = this.context.resources.displayMetrics
    popUpWindow.height = 100.toDp(displayMetrics)

}


fun checkPermission(
    context: Context,
    permission:String
):Boolean{
    return ActivityCompat.checkSelfPermission(
        context,
        permission,
    ) == PackageManager.PERMISSION_GRANTED
}