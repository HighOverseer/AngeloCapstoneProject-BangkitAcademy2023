package com.example.angelocapstoneproject.domain.dummies.home

import com.example.angelocapstoneproject.data.model.Device

object DeviceDummy {

    private val devices = generateDevices()

    fun getDevices():List<Device>{
        return devices
    }

    fun addDevice(newDevice: Device){
        devices.add(newDevice)
    }

    fun setDevice(selectedDevice: Device, index:Int){
        devices[index] = selectedDevice
    }

    private fun generateDevices():MutableList<Device>{
        return MutableList(10){ i ->
            Device(
                name = "Cam ${i + 1}",
                ipAdress = "192.168.0.$i"
            )
        }
    }


}