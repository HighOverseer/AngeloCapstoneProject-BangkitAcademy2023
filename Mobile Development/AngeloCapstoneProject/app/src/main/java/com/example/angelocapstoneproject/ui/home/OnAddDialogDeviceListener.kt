package com.example.angelocapstoneproject.ui.home

import com.example.angelocapstoneproject.data.model.Device

interface OnAddDialogDeviceListener {
    fun onAddDevice(newDevice: Device)

    fun onEditDevice(selectedDevice: Device)
}