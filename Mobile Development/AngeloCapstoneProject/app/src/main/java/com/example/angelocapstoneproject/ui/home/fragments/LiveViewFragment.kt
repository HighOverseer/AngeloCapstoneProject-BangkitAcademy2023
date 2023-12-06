package com.example.angelocapstoneproject.ui.home.fragments

import android.content.res.Configuration
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.angelocapstoneproject.data.model.Device
import com.example.angelocapstoneproject.databinding.FragmentLiveViewBinding
import com.example.angelocapstoneproject.ui.home.adapter.SpStringAdapter
import com.example.angelocapstoneproject.ui.home.OnChooseDialogDeviceListener
import com.longdo.mjpegviewer.MjpegView

class LiveViewFragment : Fragment(), OnChooseDialogDeviceListener{

    private var binding:FragmentLiveViewBinding?=null

    private lateinit var spCamAdapter:SpStringAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentLiveViewBinding.inflate(inflater, container, false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding?.apply {
            /*spCam.apply {
                spCamAdapter = SpStringAdapter(requireActivity(), DeviceDummy.getDevices().map { it.name })
                spCam.setScrollable()
                this.adapter = spCamAdapter
            }*/
            mcvCamName.setOnClickListener {
                val chooseDeviceDialogFragment = ChooseDeviceDialogFragment()
                chooseDeviceDialogFragment.show(childFragmentManager, null)
            }
            initViewDroneCam()
        }
    }

    private fun initViewDroneCam(){
        binding?.apply {
            streamView.apply {
                mode = if (isInPortraitMode()){
                    MjpegView.MODE_FIT_HEIGHT
                }else MjpegView.MODE_FIT_WIDTH

                isAdjustHeight = true
                setUrl("http://10.0.2.2:5000/video_feed")
            }
        }
    }
    private fun isInPortraitMode():Boolean{
        return resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT
    }

    override fun onChoose(selectedDevice: Device) {
        binding?.tvCamName?.text = selectedDevice.name
    }

    override fun onResume() {
        binding?.streamView?.startStream()
        super.onResume()
    }

    override fun onPause() {
        binding?.streamView?.stopStream()
        super.onPause()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }


}