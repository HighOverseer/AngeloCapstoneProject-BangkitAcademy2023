package com.example.angelocapstoneproject.ui.home.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.map
import androidx.lifecycle.viewModelScope
import com.example.angelocapstoneproject.data.Repository
import com.example.angelocapstoneproject.data.local.model.Device
import com.example.angelocapstoneproject.data.local.model.PlaybackVideo
import com.example.angelocapstoneproject.domain.Resource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class PlaybackViewModel(
    private val repository: Repository
):ViewModel() {


    private val _filterState = MutableLiveData(FilterState())
    val filterState:LiveData<FilterState> = _filterState

    private val _playbackVideos = MutableLiveData<List<PlaybackVideo>>()
    val playbackVideos:LiveData<List<PlaybackVideo>> = _playbackVideos

    fun findDeviceNameByIp(deviceIpAddress:String, date:String, playbackUrl:String){
        viewModelScope.launch(Dispatchers.IO) {
            println("val device")
            val device = repository.searchDeviceByIp(deviceIpAddress) ?: return@launch
            println("val _filter")
            _filterState.postValue(FilterState(date, device))
            getPlaybacks(device.ipAdress, date, playbackUrl)
        }
    }

    suspend fun findDeviceAndContactByIp(deviceIpAddress: String) = repository.searchDeviceAndContactByIp(deviceIpAddress)

    fun updateFilterState(filterDate: String){
        _filterState.value = filterState.value?.copy(filterDate = filterDate)

        filterState.value?.run {
            if (filterDevice != null){
                getPlaybacks(filterDevice.ipAdress, filterDate)
            }
        }
    }

    fun updateFilterState(filterDevice: Device){
        _filterState.value = filterState.value?.copy(filterDevice = filterDevice)

        filterState.value?.run {
            if (filterDate != null){
                getPlaybacks(filterDevice.ipAdress, filterDate)
            }
        }
    }

    private fun getPlaybacks(deviceIP:String, filterDate:String, firstPriorityUrl:String? = null){
        viewModelScope.launch {
            when(val resource = repository.getPlaybacks(deviceIP, filterDate)){
                is Resource.Success -> {
                    if(firstPriorityUrl == null){
                        resource.data.let { _playbackVideos.value = it }
                        return@launch
                    }
                    println(resource.data)
                    val sortedData = withContext(Dispatchers.Default){
                        val mutableData = resource.data.toMutableList()
                        val index = mutableData.indexOfFirst { it.fileUrl == firstPriorityUrl }

                        //swap
                        val oldObj = mutableData[0]
                        mutableData[0] = mutableData[index]
                        mutableData[index] = oldObj
                        mutableData
                    }
                    _playbackVideos.value = sortedData
                }
                else -> {
                    _playbackVideos.value = emptyList()
                }
            }
        }
    }

    data class FilterState(
        val filterDate:String?=null,
        val filterDevice:Device?=null
    )
}