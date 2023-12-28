package com.example.angelocapstoneproject.ui.home.fragments

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.DialogFragment
import com.example.angelocapstoneproject.data.local.model.Contact
import com.example.angelocapstoneproject.databinding.FragmentDialogContactBinding
import com.example.angelocapstoneproject.ui.home.OnDialogContactListener

class DetailAddContactDialogFragment:DialogFragment() {

    private var binding:FragmentDialogContactBinding?=null
    private lateinit var listener: OnDialogContactListener

    override fun onAttach(context: Context) {
        super.onAttach(context)
        val mFragment = parentFragment
        if (mFragment is OnDialogContactListener){
            listener = mFragment
        }else dialog?.dismiss()
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
            ColorDrawable(Color.TRANSPARENT)
        )
        return dialog
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentDialogContactBinding.inflate(
            inflater,
            container,
            false
        )
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val contactId = arguments?.getLong(CONTACT_ID_KEY)
        val contactName = arguments?.getString(CONTACT_NAME_KEY)
        val contactNumber = arguments?.getString(CONTACT_NUMBER_KEY)

        val isInAddMode = contactId == null && contactName == null && contactNumber == null

        binding?.apply {

            if (isInAddMode){
                tvInfo.text = "Add Contact"
                etContactName.isEnabled = true
                etContactNumber.isEnabled = true

                btnDelete.isVisible = false
                btnAdd.isVisible = true
            }else{
                etContactName.setText(contactName)
                etContactNumber.setText(contactNumber)
            }

            btnAdd.setOnClickListener {
                val newContact = Contact(
                    name = etContactName.text.toString(),
                    number = etContactNumber.text.toString()
                )
                listener.onAddContact(newContact)
                dialog?.dismiss()
            }

            btnDelete.setOnClickListener {
                val contact = Contact(
                    contactId?:return@setOnClickListener,
                    contactName?:return@setOnClickListener,
                    contactNumber?:return@setOnClickListener
                )
                listener.onDeleteContact(contact)
                dialog?.dismiss()
            }
        }

    }


    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }

    companion object{
        const val CONTACT_ID_KEY = "id"
        const val CONTACT_NAME_KEY = "name"
        const val CONTACT_NUMBER_KEY = "number"
    }
}