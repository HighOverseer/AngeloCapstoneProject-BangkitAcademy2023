package com.example.angelocapstoneproject.ui.home.fragments

import android.app.Dialog
import android.content.Context
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.example.angelocapstoneproject.data.local.model.Device
import com.example.angelocapstoneproject.databinding.FragmentDialogChooseDeviceBinding
import com.example.angelocapstoneproject.domain.dummies.home.DeviceDummy
import com.example.angelocapstoneproject.domain.helper.obtainViewModel
import com.example.angelocapstoneproject.ui.home.adapter.DevicesAdapter
import com.example.angelocapstoneproject.ui.home.OnChooseDialogDeviceListener
import com.example.angelocapstoneproject.ui.home.viewmodels.ChooseDeviceDialogViewModel

class ChooseDeviceDialogFragment:DialogFragment() {

    private var binding:FragmentDialogChooseDeviceBinding?=null
    private lateinit var viewModel:ChooseDeviceDialogViewModel
    private lateinit var listener : OnChooseDialogDeviceListener

    override fun onAttach(context: Context) {
        super.onAttach(context)
        val mParentFragment = parentFragment
        if (mParentFragment is OnChooseDialogDeviceListener){
            listener = mParentFragment
        }else dialog?.cancel()

    }

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
            ColorDrawable(android.graphics.Color.TRANSPARENT)
        )
        return dialog
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentDialogChooseDeviceBinding.inflate(
            inflater,
            container,
            false
        )
        return binding?.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = obtainViewModel(requireActivity().application, this)

        binding?.apply {
            viewModel.devices.observe(viewLifecycleOwner){
                val adapter = DevicesAdapter(it, ::onChosenItemClicked, true)
                rvDevices.adapter = adapter
            }
        }
    }

    private fun onChosenItemClicked(selectedDevice: Device){
        listener.onChoose(selectedDevice)
        dialog?.dismiss()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }
}