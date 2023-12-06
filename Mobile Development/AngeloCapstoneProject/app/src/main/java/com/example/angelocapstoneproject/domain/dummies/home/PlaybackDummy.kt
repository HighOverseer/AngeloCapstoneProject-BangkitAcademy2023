package com.example.angelocapstoneproject.domain.dummies.home

import com.example.angelocapstoneproject.R
import com.example.angelocapstoneproject.data.model.PlaybackThumbnail

object PlaybackDummy {

    private val imageExamples = listOf(
        R.drawable.playback_ex1,
        R.drawable.playback_ex2,
        R.drawable.playback_ex3,
        R.drawable.playback_ex4,
        R.drawable.playback_ex5,
    )

    fun getPlaybackThumbnails():List<PlaybackThumbnail>{
        return List(10){ i ->
            PlaybackThumbnail(
                imageExamples[(0..4).shuffled()[0]],
                "Cam ${i + 1}"
            )
        }
    }
}