package com.example.angelocapstoneproject.domain.dummies.home

import com.example.angelocapstoneproject.data.local.model.Contact

object ContactDummy {

    private val _emergencyContacts = mutableListOf<Contact>()
    val emergencyContacts:List<Contact> = _emergencyContacts

    fun addContact(contact: Contact){
        emergencyContacts.forEach {
            if (it.name == contact.name && it.number == contact.number){
                return
            }
        }
        _emergencyContacts.add(contact)
    }

    fun deleteContact(contact: Contact):Int{
        val index = emergencyContacts.indexOf(contact)
        _emergencyContacts.remove(contact)
        return index
    }
}