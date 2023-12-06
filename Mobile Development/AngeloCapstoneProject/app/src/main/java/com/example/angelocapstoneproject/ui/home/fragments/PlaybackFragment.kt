package com.example.angelocapstoneproject.ui.home.fragments

import android.app.DatePickerDialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatTextView
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import com.example.angelocapstoneproject.R
import com.example.angelocapstoneproject.data.model.Device
import com.example.angelocapstoneproject.databinding.FragmentPlaybackBinding
import com.example.angelocapstoneproject.ui.home.OnChooseDialogDeviceListener
import java.util.Calendar


class PlaybackFragment : Fragment(), OnChooseDialogDeviceListener{

    private var binding:FragmentPlaybackBinding?=null

    private lateinit var videoPlayer:ExoPlayer

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentPlaybackBinding.inflate(inflater, container, false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)



        binding?.apply {
            val videoItem = MediaItem.fromUri("https://github.com/dicodingacademy/assets/releases/download/release-video/VideoDicoding.mp4")
            videoPlayer = ExoPlayer.Builder(requireActivity()).build().also { exoPlayer ->
                exoPlayer.setMediaItem(videoItem)
                exoPlayer.prepare()
            }
            videoView.player = videoPlayer

            mcvCamName.setOnClickListener {
                videoPlayer.pause()
                val chooseDeviceDialogFragment = ChooseDeviceDialogFragment()
                chooseDeviceDialogFragment.show(childFragmentManager, null)
            }
            mcvDate.setOnClickListener {
                videoPlayer.pause()
                showDatePicker(tvDate)
            }

        }

   /*     binding?.apply {
            val adapter = PlaybackThumbnailsAdapter(PlaybackDummy.getPlaybackThumbnails())
            rvPlaybacks.adapter = adapter
            rvPlaybacks.addItemDecoration(
                PlaybackThumbnailsDecoration(
                    resources.displayMetrics,
                    2.toDp(resources.displayMetrics),
                )
            )
            rvPlaybacks.layoutManager = GridLayoutManager(
                requireActivity(),
                3,
                GridLayoutManager.VERTICAL,
                false
            )
        }*/
    }

    private fun showDatePicker(tvDate: AppCompatTextView){
        val currSelectionDate = tvDate.text

        val year:Int
        val month:Int
        val day:Int

        if (currSelectionDate.isBlank()){
            val c = Calendar.getInstance()
            year = c.get(Calendar.YEAR)
            month = c.get(Calendar.MONTH)
            day = c.get(Calendar.DAY_OF_MONTH)
        }else{
            val splittedDate = currSelectionDate.split("-")
            year = splittedDate[0].toInt()
            month = splittedDate[1].toInt() - 1
            day = splittedDate[2].toInt()
        }

        DatePickerDialog(
            requireActivity(),
            { _, yearr, monthOfTheYear, dayOfTheMonth ->
                tvDate.text = getString(
                    R.string.date,
                    yearr.toString(),
                    monthOfTheYear.plus(1).toString(),
                    dayOfTheMonth.toString()
                )
            },
            year,
            month,
            day
        ).show()
    }

    override fun onChoose(selectedDevice: Device) {
        binding?.apply {
            tvCamName.text = selectedDevice.name
        }
    }

    override fun onPause() {
        super.onPause()
        videoPlayer.stop()
        videoPlayer.clearMediaItems()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }



}