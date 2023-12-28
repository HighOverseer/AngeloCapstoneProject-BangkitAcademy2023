package com.example.angelocapstoneproject.data.local.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Contact(
    @PrimaryKey(autoGenerate = true)
    val id:Long = 0,
    val name:String,
    val number:String
){
    override fun toString(): String {
        return name
    }
}