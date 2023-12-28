package com.example.angelocapstoneproject.domain

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.Uri


class CallEmergencyReceiver:BroadcastReceiver() {

    companion object{
        const val ACTION = "Call Emergency"
        const val CALL_NUMBER = "call_number"
    }

    override fun onReceive(p0: Context?, p1: Intent?) {
        if(p1?.action == ACTION){
            print("action")
            val callNumber = p1.getStringExtra(CALL_NUMBER) ?: return
            val intentEmergencyCall = Intent(Intent.ACTION_CALL)
            intentEmergencyCall.data = Uri.parse("tel:$callNumber")
            p0?.startActivity(intentEmergencyCall)
        }
    }

}