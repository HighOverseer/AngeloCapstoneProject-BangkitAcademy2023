package com.example.angelocapstoneproject.data

import android.content.Context
import com.example.angelocapstoneproject.R
import com.example.angelocapstoneproject.data.local.dao.ContactDao
import com.example.angelocapstoneproject.data.local.dao.DeviceDao
import com.example.angelocapstoneproject.data.local.db.AngeloDatabase
import com.example.angelocapstoneproject.data.local.model.Contact
import com.example.angelocapstoneproject.data.local.model.Device
import com.example.angelocapstoneproject.data.local.model.PlaybackVideo
import com.example.angelocapstoneproject.data.remote.ApiClient
import com.example.angelocapstoneproject.domain.DynamicString
import com.example.angelocapstoneproject.domain.Resource
import com.example.angelocapstoneproject.domain.StaticString
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import retrofit2.Call
import retrofit2.HttpException
import retrofit2.awaitResponse

class Repository private constructor(
    private val deviceDao: DeviceDao,
    private val contactDao: ContactDao,
){


    private suspend fun <T, S> fetchData(
        execute: () -> Call<T>,
        transformData: (T) -> S
    ): Resource<S> = withContext(Dispatchers.IO) {
        try {
            val response = execute().awaitResponse()
            val responseBody = response.body()
            if (response.isSuccessful && responseBody != null) {
                Resource.Success(
                    transformData(responseBody)
                )
            } else {
                val jsonObject = response.errorBody()?.string()?.let { JSONObject(it) }
                jsonObject?.let {
                    val message = jsonObject.getString("message")
                    Resource.Failure(
                        DynamicString(message)
                    )
                } ?: Resource.Failure(
                    StaticString(
                        R.string.response_not_success
                    )
                )
            }
        } catch (e: HttpException) {
            Resource.Error(e)
        } catch (e: Exception) {
            Resource.Error(e)
        }
    }

    suspend fun getPlaybacks(
        deviceIP:String,
        filterDate:String
    ):Resource<List<PlaybackVideo>> = withContext(Dispatchers.IO){
        val apiService = ApiClient.getApiService("http://$deviceIP:5000/")
        fetchData(
                execute = {
                    apiService.getPlaybacks(filterDate)
                },
                transformData = { response ->
                    response.files.map { it.toPlaybackVideo() }
                }
            )
    }

    fun getContacts() = contactDao.getContacts()

    suspend fun insertContact(contact: Contact){
        val isContactAlreadyAdded = contactDao.checkIsContactAlreadyAdded(
            contact.number
        )
        if (isContactAlreadyAdded) return

        contactDao.insertContact(contact)
    }

    suspend fun deleteContact(contact: Contact) = contactDao.deleteContact(contact)

    fun getDevices() = deviceDao.getDevices()

    suspend fun searchDeviceByIp(deviceIpAddress:String) = deviceDao.searchDeviceByIp(deviceIpAddress)

    suspend fun searchDeviceAndContactByIp(deviceIpAddress: String) = deviceDao.searchDeviceAndContactByIp(deviceIpAddress)

    suspend fun insertDevice(device: Device){
        val hasIpUsed = deviceDao.checkHasIpUsed(device.id, device.ipAdress)
        if(hasIpUsed) return

        deviceDao.insertDevice(device)
    }

    suspend fun updateDevice(device: Device){
        val hasIpUsed = deviceDao.checkHasIpUsed(device.id, device.ipAdress)
        if(hasIpUsed) return

        deviceDao.updateDevice(device)
    }

    suspend fun deleteDevice(device: Device) = deviceDao.deleteDevice(device)

    companion object{
        @Volatile
        private var INSTANCE:Repository?=null

        fun getInstance(context: Context):Repository{
            return INSTANCE ?: synchronized(this){
                if(INSTANCE != null) return INSTANCE as Repository

                val db = AngeloDatabase.getInstance(context.applicationContext)

                return Repository(db.deviceDao(), db.contactDao()).also {
                    INSTANCE = it
                }
            }
        }
    }
}