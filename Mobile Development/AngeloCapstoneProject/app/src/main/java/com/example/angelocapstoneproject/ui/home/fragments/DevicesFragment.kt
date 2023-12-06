package com.example.angelocapstoneproject.ui.home.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.angelocapstoneproject.data.model.Device
import com.example.angelocapstoneproject.databinding.FragmentDevicesBinding
import com.example.angelocapstoneproject.domain.dummies.home.DeviceDummy
import com.example.angelocapstoneproject.ui.home.OnAddDialogDeviceListener
import com.example.angelocapstoneproject.ui.home.adapter.DevicesAdapter


class DevicesFragment : Fragment(), OnAddDialogDeviceListener {

    private var binding:FragmentDevicesBinding?=null

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

        binding?.apply {
            adapter = DevicesAdapter(
                DeviceDummy.getDevices(),
                onItemClicked = ::onDeviceItemClicked,
                false
            )
            rvDevices.adapter = adapter
            rvDevices.layoutManager = LinearLayoutManager(requireActivity())

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
        }
        dialogFragment.arguments = args
        dialogFragment.show(childFragmentManager, null)
    }

    override fun onAddDevice(newDevice: Device) {
        DeviceDummy.addDevice(newDevice)
        adapter.notifyItemInserted(DeviceDummy.getDevices().lastIndex)
    }

    override fun onEditDevice(selectedDevice: Device) {
        val devices = DeviceDummy.getDevices()
        val index = devices.indexOfFirst { it.id == selectedDevice.id }
        DeviceDummy.setDevice(selectedDevice, index)
        adapter.notifyItemChanged(index)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }

}