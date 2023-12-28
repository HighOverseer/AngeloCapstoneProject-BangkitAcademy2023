package com.example.angelocapstoneproject.ui.home

import com.example.angelocapstoneproject.data.local.model.Contact

interface OnDialogContactListener {

    fun onAddContact(newContact: Contact)

    fun onDeleteContact(selectedContact: Contact)

}