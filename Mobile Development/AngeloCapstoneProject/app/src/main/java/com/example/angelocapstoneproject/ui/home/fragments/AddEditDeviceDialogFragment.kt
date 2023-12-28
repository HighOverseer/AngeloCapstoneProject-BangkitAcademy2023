package com.example.angelocapstoneproject.ui.home.fragments

import android.app.Dialog
import com.example.angelocapstoneproject.data.local.model.Device
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.isVisible
import androidx.fragment.app.DialogFragment
import androidx.work.ListenableWorker.Result.Success
import com.example.angelocapstoneproject.CONNECTED
import com.example.angelocapstoneproject.R
import com.example.angelocapstoneproject.ViewModelFactory
import com.example.angelocapstoneproject.data.local.model.Contact
import com.example.angelocapstoneproject.databinding.FragmentDialogAddDeviceBinding
import com.example.angelocapstoneproject.domain.dummies.home.ContactDummy
import com.example.angelocapstoneproject.domain.helper.obtainViewModel
import com.example.angelocapstoneproject.ui.home.OnDialogDeviceListener
import com.example.angelocapstoneproject.ui.home.adapter.SpContactAdapter
import com.example.angelocapstoneproject.ui.home.viewmodels.AddEditDeviceDialogViewModel
import com.onesignal.OneSignal

class AddEditDeviceDialogFragment:DialogFragment(){

    private var binding:FragmentDialogAddDeviceBinding?=null
    private lateinit var viewModel:AddEditDeviceDialogViewModel
    private lateinit var listener:OnDialogDeviceListener

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
        if (mFragment is OnDialogDeviceListener){
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
        viewModel = obtainViewModel(requireActivity().application, this)

        binding?.apply {
            val deviceId = arguments?.getLong(DEVICE_ID_KEY)
            val deviceName = arguments?.getString(DEVICE_NAME_KEY)
            val deviceIp = arguments?.getString(DEVICE_IP_KEY)
            val contactId = arguments?.getLong(DEVICE_CONTACT_ID_KEY)

            var isInEditMode = false

            viewModel.contacts.observe(viewLifecycleOwner){ contacts ->
                spEmergencyContact.adapter = SpContactAdapter(requireActivity(), contacts)

                isInEditMode = deviceName != null && deviceIp != null && deviceId != null
                if (isInEditMode){
                    tvInfo.text = "Edit Device"
                    etCamName.setText(deviceName)
                    etCamIp.setText(deviceIp)

                    val selectedContactIndex = contacts.indexOfFirst { it.id == contactId }
                    spEmergencyContact.setSelection(selectedContactIndex)

                    btnDelete.isVisible = true
                }

                btnDelete.setOnClickListener {
                    val device = Device(
                        deviceId?:return@setOnClickListener,
                        deviceName?:return@setOnClickListener,
                        deviceIp?:return@setOnClickListener,
                        contactId?:return@setOnClickListener
                    )
                    listener.onDeleteDevice(device)
                    OneSignal.User.removeTag(deviceIp)
                    dialog?.dismiss()
                }

                btnSaveEdit.setOnClickListener {
                    val newDeviceName = etCamName.text.toString()
                    val newDeviceIp = etCamIp.text.toString()
                    val selectedPosition = spEmergencyContact.selectedItemPosition
                    if (newDeviceName.isBlank() || newDeviceIp.isBlank() || selectedPosition == -1) return@setOnClickListener

                    val newContactId = contacts[selectedPosition].id

                    if (isInEditMode){
                        val device = Device(
                            id = deviceId ?: -1L,
                            name = newDeviceName,
                            ipAdress = newDeviceIp,
                            newContactId
                        )
                        val isSuccess = listener.onEditDevice(device)
                        if (!isSuccess) return@setOnClickListener

                        OneSignal.User.removeTag(deviceIp as String)
                        OneSignal.User.addTag(newDeviceIp, OneSignal.CONNECTED)
                    }else{
                        val newDevice = Device(
                            name = newDeviceName,
                            ipAdress = newDeviceIp,
                            emergencyContactId = newContactId
                        )
                        val isSuccess = listener.onAddDevice(newDevice)
                        if (!isSuccess) return@setOnClickListener

                        OneSignal.User.addTag(newDeviceIp, OneSignal.CONNECTED)
                    }
                    dialog?.dismiss()
                }

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
        const val DEVICE_CONTACT_ID_KEY = "contact id"
    }
}