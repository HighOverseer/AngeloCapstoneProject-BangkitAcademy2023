package com.example.angelocapstoneproject.domain

sealed class Resource<out T> private constructor() {
    data class Success<T>(val data: T) : Resource<T>()
    data class Failure(val message: StringRes) : Resource<Nothing>()
    data class Error(val e: Exception) : Resource<Nothing>()
}