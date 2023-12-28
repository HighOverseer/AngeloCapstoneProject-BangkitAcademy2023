package com.example.angelocapstoneproject.di

import android.content.Context
import com.example.angelocapstoneproject.data.Repository

object Injection {

    fun provideRepository(context: Context):Repository{
        return Repository.getInstance(context)
    }
}