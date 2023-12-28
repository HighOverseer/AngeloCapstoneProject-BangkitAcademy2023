package com.example.angelocapstoneproject.ui.home.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.angelocapstoneproject.data.local.model.Device
import com.example.angelocapstoneproject.databinding.FragmentDevicesBinding
import com.example.angelocapstoneproject.domain.dummies.home.DeviceDummy
import com.example.angelocapstoneproject.domain.helper.obtainViewModel
import com.example.angelocapstoneproject.ui.home.OnDialogDeviceListener
import com.example.angelocapstoneproject.ui.home.adapter.DevicesAdapter
import com.example.angelocapstoneproject.ui.home.viewmodels.DeviceViewModel


class DevicesFragment : Fragment(), OnDialogDeviceListener {

    private var binding:FragmentDevicesBinding?=null
    private lateinit var viewModel: DeviceViewModel

    private lateinit var adapter:DevicesAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentDevicesBinding.inflate(inflater, container, false)
        return binding?.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = obtainViewModel(requireActivity().application, this)



        binding?.apply {
            viewModel.devices.observe(viewLifecycleOwner){
                adapter = DevicesAdapter(
                    it,
                    onItemClicked = ::onDeviceItemClicked,
                    false
                )
                rvDevices.adapter = adapter
            }

            fab.setOnClickListener {
                val dialogFragment = AddEditDeviceDialogFragment()
                dialogFragment.show(childFragmentManager, null)
            }
        }



    }

    private fun onDeviceItemClicked(device: Device){
        val dialogFragment = AddEditDeviceDialogFragment()
        val args = Bundle().apply {
            putLong(AddEditDeviceDialogFragment.DEVICE_ID_KEY, device.id)
            putString(AddEditDeviceDialogFragment.DEVICE_NAME_KEY, device.name)
            putString(AddEditDeviceDialogFragment.DEVICE_IP_KEY, device.ipAdress)
            putLong(AddEditDeviceDialogFragment.DEVICE_CONTACT_ID_KEY, device.emergencyContactId)
        }
        dialogFragment.arguments = args
        dialogFragment.show(childFragmentManager, null)
    }

    override fun onAddDevice(newDevice: Device) : Boolean{
        viewModel.insertDevice(newDevice)
        return true
    }

    override fun onEditDevice(selectedDevice: Device) : Boolean{
        viewModel.updateDevice(selectedDevice)
        return true
    }

    override fun onDeleteDevice(selectedDevice: Device) {
        viewModel.deleteDevice(selectedDevice)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }

}