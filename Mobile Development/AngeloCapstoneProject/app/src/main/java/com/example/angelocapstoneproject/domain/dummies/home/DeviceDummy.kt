package com.example.angelocapstoneproject.domain.dummies.home

import com.example.angelocapstoneproject.data.local.model.Device

object DeviceDummy {

    private val devices = mutableListOf<Device>()

    fun getDevices():List<Device>{
        return devices
    }

    fun addDevice(newDevice: Device){
        devices.add(newDevice)
    }

    fun setDevice(selectedDevice: Device, index:Int){
        devices[index] = selectedDevice
    }

    fun checkHasIpUsed(deviceId:Long, deviceIp:String):Boolean{
        devices.forEach {
            if(it.id != deviceId && it.ipAdress == deviceIp) return true
        }
        return false
    }

    fun deleteDevice(selectedDevice: Device):Int{
        val index = devices.indexOf(selectedDevice)
        devices.remove(selectedDevice)
        return index
    }

  /*  private fun generateDevices():MutableList<Device>{
        return MutableList(10){ i ->
            Device(
                name = "Cam ${i + 1}",
                ipAdress = "192.168.0.$i"
            )
        }
    }*/


}