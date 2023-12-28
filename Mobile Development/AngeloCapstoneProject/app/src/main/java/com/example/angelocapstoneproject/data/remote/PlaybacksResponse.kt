package com.example.angelocapstoneproject.data.remote

import com.example.angelocapstoneproject.data.local.model.PlaybackVideo
import com.google.gson.annotations.SerializedName

data class PlaybacksResponse(
	@field:SerializedName("files")
	val files: List<PlaybackVideoDto>
)

data class PlaybackVideoDto(

	@field:SerializedName("file_url")
	val fileUrl: String? = null,

	@field:SerializedName("upload_time")
	val uploadTime: String? = null,

	@field:SerializedName("file_name")
	val fileName: String? = null
){
	fun toPlaybackVideo():PlaybackVideo{
		println(this)
		return PlaybackVideo(
			fileUrl ?: throw Exception(),
			uploadTime ?: "",
			fileName ?: ""
		)
	}
}
