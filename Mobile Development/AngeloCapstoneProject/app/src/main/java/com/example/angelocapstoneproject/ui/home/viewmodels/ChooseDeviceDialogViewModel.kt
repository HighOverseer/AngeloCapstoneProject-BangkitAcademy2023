package com.example.angelocapstoneproject.ui.home.viewmodels

import androidx.lifecycle.ViewModel
import com.example.angelocapstoneproject.data.Repository

class ChooseDeviceDialogViewModel(
    private val repository: Repository
): ViewModel() {

    val devices = repository.getDevices()
}