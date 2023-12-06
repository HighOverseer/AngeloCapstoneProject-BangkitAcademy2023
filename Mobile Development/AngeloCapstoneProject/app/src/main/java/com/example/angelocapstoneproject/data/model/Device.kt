package com.example.angelocapstoneproject.data.model

private var increId = 0L
    get(){
        field += 1
        return field
    }

data class Device(
    val id:Long = increId,
    val name:String,
    val ipAdress:String
)