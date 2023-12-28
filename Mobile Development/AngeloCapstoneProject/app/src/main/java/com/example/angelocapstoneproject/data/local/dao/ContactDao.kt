package com.example.angelocapstoneproject.data.local.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.example.angelocapstoneproject.data.local.model.Contact

@Dao
interface ContactDao {
    @Query("SELECT * FROM Contact")
    fun getContacts():LiveData<List<Contact>>

    @Query("""
        SELECT EXISTS (
            SELECT * FROM Contact 
            WHERE number = :number
        )
    """)
    suspend fun checkIsContactAlreadyAdded(number:String):Boolean

    @Insert
    suspend fun insertContact(contact: Contact)

    @Delete
    suspend fun deleteContact(contact: Contact)
}