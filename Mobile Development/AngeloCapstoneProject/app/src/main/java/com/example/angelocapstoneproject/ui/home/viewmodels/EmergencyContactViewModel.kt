package com.example.angelocapstoneproject.ui.home.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.angelocapstoneproject.data.Repository
import com.example.angelocapstoneproject.data.local.model.Contact
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class EmergencyContactViewModel(
    private val repository: Repository
) : ViewModel(){

    val contacts = repository.getContacts()

    fun insertContact(newContact: Contact) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.insertContact(newContact)
        }
    }

    fun deleteContact(contact: Contact){
        viewModelScope.launch(Dispatchers.IO) {
            repository.deleteContact(contact)
        }
    }


}