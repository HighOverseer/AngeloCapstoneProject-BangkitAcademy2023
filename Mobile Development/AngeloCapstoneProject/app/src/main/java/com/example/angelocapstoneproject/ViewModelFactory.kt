package com.example.angelocapstoneproject

import android.app.Application
import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.angelocapstoneproject.di.Injection
import com.example.angelocapstoneproject.ui.home.viewmodels.AddEditDeviceDialogViewModel
import com.example.angelocapstoneproject.ui.home.viewmodels.ChooseDeviceDialogViewModel
import com.example.angelocapstoneproject.ui.home.viewmodels.DeviceViewModel
import com.example.angelocapstoneproject.ui.home.viewmodels.EmergencyContactViewModel
import com.example.angelocapstoneproject.ui.home.viewmodels.LiveViewViewModel
import com.example.angelocapstoneproject.ui.home.viewmodels.PlaybackViewModel

class ViewModelFactory private constructor(
    private val application: Application,
): ViewModelProvider.NewInstanceFactory(){

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(AddEditDeviceDialogViewModel::class.java) -> {
                AddEditDeviceDialogViewModel(
                    Injection.provideRepository(
                        application.applicationContext
                    )
                ) as T
            }
            modelClass.isAssignableFrom(DeviceViewModel::class.java) -> {
                DeviceViewModel(
                    Injection.provideRepository(
                        application.applicationContext
                    )
                ) as T
            }
            modelClass.isAssignableFrom(ChooseDeviceDialogViewModel::class.java) -> {
                ChooseDeviceDialogViewModel(
                    Injection.provideRepository(
                        application.applicationContext
                    )
                ) as T
            }
            modelClass.isAssignableFrom(LiveViewViewModel::class.java) -> {
                LiveViewViewModel(
                    Injection.provideRepository(
                        application.applicationContext
                    )
                ) as T
            }
            modelClass.isAssignableFrom(PlaybackViewModel::class.java) -> {
                PlaybackViewModel(
                    Injection.provideRepository(
                        application.applicationContext
                    )
                ) as T
            }
            modelClass.isAssignableFrom(EmergencyContactViewModel::class.java) -> {
                EmergencyContactViewModel(
                    Injection.provideRepository(
                        application.applicationContext
                    )
                ) as T
            }
            else -> super.create(modelClass)
        }
    }

    companion object{
        @Volatile
        private var INSTANCE:ViewModelFactory?=null

        fun getInstance(
            application: Application,
        ):ViewModelFactory{
            return INSTANCE?: synchronized(this){
                INSTANCE?:ViewModelFactory(application)
            }.also { INSTANCE = it }
        }
    }
}