package com.example.angelocapstoneproject.ui.home.fragments

import android.Manifest
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.graphics.Bitmap
import android.graphics.Rect
import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.PermissionRequest
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.view.isVisible
import com.example.angelocapstoneproject.R
import com.example.angelocapstoneproject.data.local.model.Device
import com.example.angelocapstoneproject.databinding.FragmentLiveViewBinding
import com.example.angelocapstoneproject.domain.helper.obtainViewModel
import com.example.angelocapstoneproject.ui.home.adapter.SpStringAdapter
import com.example.angelocapstoneproject.ui.home.OnChooseDialogDeviceListener
import com.example.angelocapstoneproject.ui.home.viewmodels.LiveViewViewModel
import com.longdo.mjpegviewer.MjpegView
import com.longdo.mjpegviewer.MjpegViewError
import com.longdo.mjpegviewer.MjpegViewStateChangeListener

class LiveViewFragment : Fragment(), OnChooseDialogDeviceListener{

    private var binding:FragmentLiveViewBinding?=null
    private lateinit var viewModel: LiveViewViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentLiveViewBinding.inflate(inflater, container, false)
        return binding?.root
    }

   /* private val requestPermission = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ){
        initViewDroneCam()
    }*/

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = obtainViewModel(requireActivity().application, this)

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
            btnStartTryAgain.setOnClickListener {
                tvInfo.isVisible = false
                btnStartTryAgain.isVisible = false
                
                streamView.stopStream()
                streamView.startStream()
            }
            //initViewDroneCam()
        }
    }

    private fun initViewDroneCam(deviceName:String, deviceIp:String){
        binding?.apply {
            streamView.apply {
                mode = if (isInPortraitMode()){
                    MjpegView.MODE_FIT_HEIGHT
                }else MjpegView.MODE_FIT_WIDTH
                stateChangeListener = onStreamStateChangedListener
                isAdjustHeight = true
                setUrl("http://$deviceIp:5000/video_feed?local_name=$deviceName")

                stopStream()
                startStream()

                /*if (ActivityCompat.checkSelfPermission(context, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED){
                    requestPermission.launch(Manifest.permission.CAMERA)
                }

                settings.javaScriptCanOpenWindowsAutomatically = true
                settings.allowContentAccess = true
                settings.javaScriptEnabled = true
                settings.mediaPlaybackRequiresUserGesture = false
                settings.allowFileAccessFromFileURLs = true
                settings.allowUniversalAccessFromFileURLs = true
                webViewClient = WebViewClient()
                settings.domStorageEnabled = true
                webChromeClient = object : WebChromeClient(){
                    override fun onPermissionRequest(request: PermissionRequest?) {

                        request?.grant(request.resources)
                        *//*request?.resources?.forEach {
                            if(it.equals(PermissionRequest.RESOURCE_VIDEO_CAPTURE)){
                                request.grant(arrayOf(PermissionRequest.RESOURCE_VIDEO_CAPTURE))
                                return@forEach
                            }
                        }*//*

                    }
                }
                loadUrl("http://10.0.2.2/video_feed")*/

            }
        }
    }

    private val onStreamStateChangedListener = object : MjpegViewStateChangeListener{
        override fun onStreamDownloadStart() {
            requireActivity().runOnUiThread{
                binding?.apply {
                    tvInfo.text = getString(R.string.loading)
                    tvInfo.isVisible = true
                    btnStartTryAgain.isVisible = false
                }
            }
        }

        override fun onStreamDownloadStop() {
            /*requireActivity().runOnUiThread{
                binding?.apply {
                    tvInfo.text = getString(R.string.stream_stopped)
                    tvInfo.isVisible = true
                    btnStartTryAgain.text = getString(R.string.start_again)
                    btnStartTryAgain.isVisible = true
                }
            }*/
        }
        override fun onServerConnected() {
            requireActivity().runOnUiThread {
                binding?.apply {
                    tvInfo.isVisible = false
                    btnStartTryAgain.isVisible = false
                }
            }
        }
        override fun onMeasurementChanged(rect: Rect?) {}
        override fun onNewFrame(image: Bitmap?) {}

        override fun onError(error: MjpegViewError?) {
            requireActivity().runOnUiThread {
                binding?.apply {
                    tvInfo.text = getString(R.string.failed_to_load_stream)
                    tvInfo.isVisible = true
                    btnStartTryAgain.text = getString(R.string.try_again)
                    btnStartTryAgain.isVisible = true
                }
            }
        }
    }

    private fun isInPortraitMode():Boolean{
        return resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT
    }

    override fun onChoose(selectedDevice: Device) {
        binding?.tvCamName?.text = selectedDevice.name
        initViewDroneCam(selectedDevice.name, selectedDevice.ipAdress)
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