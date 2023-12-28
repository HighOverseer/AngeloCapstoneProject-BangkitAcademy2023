package com.example.angelocapstoneproject.ui.home

import com.example.angelocapstoneproject.data.local.model.Device

interface OnChooseDialogDeviceListener{
    fun onChoose(selectedDevice: Device)
}