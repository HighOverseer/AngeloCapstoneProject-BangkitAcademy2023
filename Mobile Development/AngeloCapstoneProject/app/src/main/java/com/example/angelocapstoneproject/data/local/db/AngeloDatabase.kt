package com.example.angelocapstoneproject.data.local.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.angelocapstoneproject.data.local.dao.ContactDao
import com.example.angelocapstoneproject.data.local.dao.DeviceDao
import com.example.angelocapstoneproject.data.local.model.Contact
import com.example.angelocapstoneproject.data.local.model.Device

@Database(entities = [Device::class, Contact::class], version = 1)
abstract class AngeloDatabase:RoomDatabase() {

    abstract fun contactDao(): ContactDao
    abstract fun deviceDao(): DeviceDao

    companion object{
        @Volatile
        private var INSTANCE: AngeloDatabase? = null

        fun getInstance(context: Context): AngeloDatabase {
            return INSTANCE ?: synchronized(this){
                INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    AngeloDatabase::class.java,
                    "angelo.db"
                ).build()
            }.also { INSTANCE = it }
        }
    }
}