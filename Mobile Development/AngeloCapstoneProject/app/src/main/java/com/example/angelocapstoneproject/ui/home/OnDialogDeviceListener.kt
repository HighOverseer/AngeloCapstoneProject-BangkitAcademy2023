package com.example.angelocapstoneproject.ui.home

import com.example.angelocapstoneproject.data.local.model.Device

interface OnDialogDeviceListener {
    fun onAddDevice(newDevice: Device):Boolean

    fun onEditDevice(selectedDevice: Device):Boolean

    fun onDeleteDevice(selectedDevice: Device)
}