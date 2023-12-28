package com.example.angelocapstoneproject.ui.home.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import com.example.angelocapstoneproject.R
import com.example.angelocapstoneproject.data.local.model.Device
import com.example.angelocapstoneproject.databinding.SpinnerDropdownItemBinding
import com.example.angelocapstoneproject.databinding.SpinnerItemBinding

class SpStringAdapter(
    context: Context,
    private val listItem:List<String>
):ArrayAdapter<String>(context, 0, listItem) {



    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
        val currItem = getItem(position)
        val textView = if (convertView != null){
            convertView.findViewById(android.R.id.text1)
        }else{
            val binding = SpinnerDropdownItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            binding.text1
        }
        return textView.apply {
            textView.text = currItem
            if (currItem == getItem(position)){
                setBackgroundResource(R.drawable.spinner_dropdown_bg)
            }
        }

    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getItem(position: Int): String{
        return listItem[position]
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
        return textView
    }

}