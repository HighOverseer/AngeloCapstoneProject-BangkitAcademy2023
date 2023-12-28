package com.example.angelocapstoneproject.data.local.model

import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity
data class Device(
    @PrimaryKey(autoGenerate = true)
    val id:Long = 0,
    val name:String,
    val ipAdress:String,
    val emergencyContactId: Long
)