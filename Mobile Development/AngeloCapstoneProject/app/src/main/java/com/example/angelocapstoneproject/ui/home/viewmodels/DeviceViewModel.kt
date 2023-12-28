package com.example.angelocapstoneproject.ui.home.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.angelocapstoneproject.data.Repository
import com.example.angelocapstoneproject.data.local.model.Device
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class DeviceViewModel(
    private val repository: Repository
):ViewModel() {

    val devices = repository.getDevices()

    fun insertDevice(
        newDevice: Device
    ){
        viewModelScope.launch(Dispatchers.IO) {
            repository.insertDevice(device = newDevice)
        }
    }

    fun updateDevice(
        selectedDevice: Device
    ){
        viewModelScope.launch(Dispatchers.IO) {
            repository.updateDevice(selectedDevice)
        }
    }

    fun deleteDevice(
        device: Device
    ){
        viewModelScope.launch(Dispatchers.IO) {
            repository.deleteDevice(device)
        }
    }
}