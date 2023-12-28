package com.example.angelocapstoneproject.ui.home.viewmodels

import androidx.lifecycle.ViewModel
import com.example.angelocapstoneproject.data.Repository

class AddEditDeviceDialogViewModel(
    private val repository: Repository
):ViewModel() {
    val contacts = repository.getContacts()
}