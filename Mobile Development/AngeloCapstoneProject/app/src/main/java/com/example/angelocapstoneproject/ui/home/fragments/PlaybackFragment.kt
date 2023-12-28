package com.example.angelocapstoneproject.ui.home.fragments

import android.app.DatePickerDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import com.example.angelocapstoneproject.R
import com.example.angelocapstoneproject.data.Repository
import com.example.angelocapstoneproject.data.local.model.Device
import com.example.angelocapstoneproject.databinding.FragmentPlaybackBinding
import com.example.angelocapstoneproject.domain.CallEmergencyReceiver
import com.example.angelocapstoneproject.domain.dummies.home.DeviceDummy
import com.example.angelocapstoneproject.domain.helper.obtainViewModel
import com.example.angelocapstoneproject.domain.helper.simpleDateFormat
import com.example.angelocapstoneproject.ui.home.OnChooseDialogDeviceListener
import com.example.angelocapstoneproject.ui.home.viewmodels.PlaybackViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Calendar


class PlaybackFragment : Fragment(), OnChooseDialogDeviceListener{

    private var binding:FragmentPlaybackBinding?=null
    private lateinit var viewModel: PlaybackViewModel

    private lateinit var videoPlayer:ExoPlayer

    private var callJob: Job?=null


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentPlaybackBinding.inflate(inflater, container, false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = obtainViewModel(requireActivity().application, this)

        videoPlayer = ExoPlayer.Builder(requireActivity()).build()

        binding?.apply {
            viewModel.playbackVideos.observe(viewLifecycleOwner){
               val videoItems = List(it.size){ position ->
                   MediaItem.fromUri(it[position].fileUrl)
               }

               videoPlayer.also { exoPlayer ->
                   exoPlayer.stop()
                   exoPlayer.clearMediaItems()
                   exoPlayer.setMediaItems(videoItems)
                   exoPlayer.prepare()
               }

               videoView.player = videoPlayer
            }

            viewModel.filterState.observe(viewLifecycleOwner){
                tvCamName.text = it.filterDevice?.name ?: "[ Select Device ]"
                tvDate.text = it.filterDate ?: "[ Select Date ]"

                if(it.filterDate == null || it.filterDevice == null){
                    btnCallEmergency.visibility = View.INVISIBLE
                }else{
                    btnCallEmergency.visibility = View.VISIBLE
                }
            }

            btnCallEmergency.setOnClickListener {
                if (it.visibility == View.INVISIBLE) return@setOnClickListener

                viewModel.filterState.value?.filterDevice?.let { device ->
                    callJob?.cancel()
                    callJob = lifecycleScope.launch{
                        callEmergency(device)
                    }
                }
            }

            mcvCamName.setOnClickListener {
                videoPlayer.pause()
                val chooseDeviceDialogFragment = ChooseDeviceDialogFragment()
                chooseDeviceDialogFragment.show(childFragmentManager, null)
            }
            mcvDate.setOnClickListener {
                videoPlayer.pause()
                showDatePicker(tvDate)
            }
            arguments?.let {

                val deviceIp = arguments?.getString(DEVICE_IP) ?: return
                val playbackUrl = arguments?.getString(PLAYBACK_URL) ?: return
                val date = arguments?.getString(DATE) ?: return

                viewModel.findDeviceNameByIp(deviceIp, date, playbackUrl)

                val callNumber = arguments?.getString(CALL_NUMBER) ?: return
                val intentEmergencyCall = Intent(Intent.ACTION_CALL)
                intentEmergencyCall.data = Uri.parse("tel:$callNumber")
                startActivity(intentEmergencyCall)

                arguments?.clear()
                return
            }
            val videoItem = MediaItem.fromUri("https://storage.googleapis.com/angelo-bucket-storage/fall_video_stream_975.avi")
            videoPlayer.also { exoPlayer ->
                exoPlayer.setMediaItem(videoItem)
                exoPlayer.prepare()
            }
            videoView.player = videoPlayer
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

    private suspend fun callEmergency(device: Device) {
        val deviceAndContact = viewModel.findDeviceAndContactByIp(device.ipAdress) ?: return
        withContext(Dispatchers.Main){
            val intentEmergencyCall = Intent(Intent.ACTION_CALL)
            intentEmergencyCall.data = Uri.parse("tel:${deviceAndContact.contact.number}")
            startActivity(intentEmergencyCall)
        }
    }

    private fun showDatePicker(tvDate: AppCompatTextView){

        val currSelectionDate = try {
            simpleDateFormat.parse(tvDate.text.toString())
            tvDate.text
        }catch (e:Exception){
            e.printStackTrace()
            ""
        }

        val year:Int
        val month:Int
        val day:Int

        if (currSelectionDate.isBlank()){
            val c = Calendar.getInstance()
            year = c.get(Calendar.YEAR)
            month = c.get(Calendar.MONTH)
            day = c.get(Calendar.DAY_OF_MONTH)
        }else{
            val splittedDate = currSelectionDate.split("/")
            day = splittedDate[0].toInt()
            month = splittedDate[1].toInt() - 1
            year = splittedDate[2].toInt()
        }

        DatePickerDialog(
            requireActivity(),
            { _, yearr, monthOfTheYear, dayOfTheMonth ->

                tvDate.text = getString(
                    R.string.date,
                    dayOfTheMonth.toString(),
                    monthOfTheYear.plus(1).toString(),
                    yearr.toString()
                )

                val filterDate = "$dayOfTheMonth/${monthOfTheYear.plus(1)}/$yearr"
                viewModel.updateFilterState(filterDate)
            },
            year,
            month,
            day
        ).show()
    }

    override fun onChoose(selectedDevice: Device) {
        binding?.apply {
            tvCamName.text = selectedDevice.name
            viewModel.updateFilterState(selectedDevice)
        }
    }

    override fun onStop() {
        super.onStop()
        videoPlayer.stop()
        videoPlayer.clearMediaItems()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }

    companion object{
        const val DEVICE_IP = "device_ip"
        const val PLAYBACK_URL = "playback_url"
        const val DATE = "date"


        const val CALL_NUMBER = "call number"
    }


}