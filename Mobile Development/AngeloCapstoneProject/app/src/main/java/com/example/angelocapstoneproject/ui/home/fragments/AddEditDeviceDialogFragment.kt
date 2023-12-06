package com.example.angelocapstoneproject.ui.home.fragments

import android.app.Dialog
import com.example.angelocapstoneproject.data.model.Device
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.example.angelocapstoneproject.databinding.FragmentDialogAddDeviceBinding
import com.example.angelocapstoneproject.ui.home.OnAddDialogDeviceListener

class AddEditDeviceDialogFragment:DialogFragment(){

    private var binding:FragmentDialogAddDeviceBinding?=null

    private lateinit var listener:OnAddDialogDeviceListener

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(
            STYLE_NO_TITLE,
            android.R.style.Theme_Material_Dialog_NoActionBar_MinWidth
        )
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        dialog.window?.setBackgroundDrawable(
            ColorDrawable(Color.TRANSPARENT)
        )
        return dialog
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        val mFragment = parentFragment
        if (mFragment is OnAddDialogDeviceListener){
            listener = mFragment
        }

    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentDialogAddDeviceBinding.inflate(inflater, container, false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding?.apply {
            val deviceId = arguments?.getLong(DEVICE_ID_KEY)
            val deviceName = arguments?.getString(DEVICE_NAME_KEY)
            val deviceIp = arguments?.getString(DEVICE_IP_KEY)

            val isInEditMode = deviceName != null && deviceIp != null && deviceId != null
            if (isInEditMode){
                tvInfo.text = "Edit Device"
                etCamName.setText(deviceName)
                etCamIp.setText(deviceIp)
            }

            btnSaveEdit.setOnClickListener {
                val newDeviceName = etCamName.text.toString()
                val newDeviceIp = etCamIp.text.toString()

                if (isInEditMode){
                    val device = Device(
                        id = deviceId ?: -1L,
                        name = newDeviceName,
                        ipAdress = newDeviceIp
                    )
                    listener.onEditDevice(device)
                }else{
                    val newDevice = Device(
                        name = newDeviceName,
                        ipAdress = newDeviceIp
                    )
                    listener.onAddDevice(newDevice)
                }
                dialog?.dismiss()
            }
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }

    companion object{
        const val DEVICE_ID_KEY = "id"
        const val DEVICE_NAME_KEY = "name"
        const val DEVICE_IP_KEY = "ip"
    }
}