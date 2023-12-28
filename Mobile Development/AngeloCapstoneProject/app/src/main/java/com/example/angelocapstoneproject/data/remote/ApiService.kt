package com.example.angelocapstoneproject.data.remote

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiService {

    @GET("files")
    fun getPlaybacks(
        @Query("filter_date")
        filterDate:String
    ): Call<PlaybacksResponse>
}