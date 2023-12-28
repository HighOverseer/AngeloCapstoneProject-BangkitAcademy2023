package com.example.angelocapstoneproject.data.local.relation

import androidx.room.Embedded
import androidx.room.Relation
import com.example.angelocapstoneproject.data.local.model.Contact
import com.example.angelocapstoneproject.data.local.model.Device

data class DeviceAndContact(
    @Embedded
    val device: Device,

    @Relation(
        Contact::class,
        parentColumn = "emergencyContactId",
        entityColumn = "id"
    )
    val contact: Contact
)