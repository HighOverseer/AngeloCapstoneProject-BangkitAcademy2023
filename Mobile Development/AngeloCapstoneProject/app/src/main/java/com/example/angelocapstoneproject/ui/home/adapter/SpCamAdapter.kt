package com.example.angelocapstoneproject.ui.home.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.core.content.res.ResourcesCompat
import com.example.angelocapstoneproject.R
import com.example.angelocapstoneproject.data.local.model.Contact
import com.example.angelocapstoneproject.data.local.model.Device
import com.example.angelocapstoneproject.databinding.SpinnerDropdownItemBinding
import com.example.angelocapstoneproject.databinding.SpinnerItemBinding

class SpContactAdapter(
    context: Context,
    private val listItem:List<Contact>
):ArrayAdapter<String>(context, 0, listItem.map { it.name }) {

    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
        val currItem = getItem(position)
        val textView = if (convertView != null){
            convertView.findViewById(android.R.id.text1)
        }else{
            val binding = SpinnerDropdownItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            binding.text1
        }
        textView.text = listItem[position].name
        return textView.apply {
            if (currItem == getItem(position)){
                setBackgroundResource(R.drawable.spinner_dropdown_bg)
            }
        }

    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getItem(position: Int): String{
        return listItem[position].name
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val currSelectedItem = getItem(position)
        val textView = if (convertView != null){
            convertView.findViewById(android.R.id.text1)
        }else{
            val binding = SpinnerItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            binding.text1
        }
        textView.text = currSelectedItem
        textView.setTextColor(
            ResourcesCompat.getColor(textView.resources, R.color.grey_10, null)
        )
        return textView
    }

}