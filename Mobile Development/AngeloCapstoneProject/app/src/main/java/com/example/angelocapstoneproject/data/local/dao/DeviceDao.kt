package com.example.angelocapstoneproject.data.local.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.example.angelocapstoneproject.data.local.model.Device
import com.example.angelocapstoneproject.data.local.relation.DeviceAndContact

@Dao
interface DeviceDao {

    @Query("SELECT * FROM Device")
    fun getDevices():LiveData<List<Device>>

    @Transaction
    @Query("SELECT * FROM Device")
    fun getDevicesAndContacts():LiveData<List<DeviceAndContact>>

    @Query("SELECT * FROM Device WHERE ipAdress = :deviceIpAddress")
    suspend fun searchDeviceByIp(deviceIpAddress:String):Device?

    @Query("SELECT * FROM Device WHERE ipAdress = :deviceIpAddress")
    suspend fun searchDeviceAndContactByIp(deviceIpAddress:String):DeviceAndContact?

    @Insert
    suspend fun insertDevice(newDevice: Device)

    @Update
    suspend fun updateDevice(selectedDevice: Device)

    @Delete
    suspend fun deleteDevice(selectedDevice: Device)

    @Query("""
        SELECT EXISTS (
            SELECT * FROM Device 
            WHERE id != :deviceId 
            and ipAdress = :deviceIp
        )
    """)
    fun checkHasIpUsed(deviceId:Long, deviceIp:String):Boolean

}