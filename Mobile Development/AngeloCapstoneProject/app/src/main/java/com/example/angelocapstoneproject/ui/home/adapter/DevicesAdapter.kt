package com.example.angelocapstoneproject.ui.home.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.angelocapstoneproject.data.local.model.Device
import com.example.angelocapstoneproject.databinding.DevicesDialogItemLayoutBinding
import com.example.angelocapstoneproject.databinding.DevicesItemLayoutBinding

class DevicesAdapter(
    private val items:List<Device>,
    private val onItemClicked:(Device) -> Unit,
    private val isForDialog:Boolean
):RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    class DevicesViewHolder(
        val binding:DevicesItemLayoutBinding,
        clickedAtPosition:(Int) -> Unit
    ):RecyclerView.ViewHolder(binding.root){
        init {
            itemView.setOnClickListener {
                clickedAtPosition(absoluteAdapterPosition)
            }
        }
    }

    class DevicesDialogViewHolder(
        val binding:DevicesDialogItemLayoutBinding,
        clickedAtPosition: (Int) -> Unit
    ):RecyclerView.ViewHolder(binding.root){
        init {
            itemView.setOnClickListener {
                clickedAtPosition(absoluteAdapterPosition)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        if (!isForDialog){
            val binding = DevicesItemLayoutBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
            return DevicesViewHolder(binding){ position ->
                onItemClicked(items[position])
            }
        }else{
            val binding = DevicesDialogItemLayoutBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
            return DevicesDialogViewHolder(binding){ position ->
                onItemClicked(items[position])
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        holder.apply {
            when(this){
                is DevicesViewHolder -> {
                    binding.apply {
                        tvCamName.text = items[position].name
                    }

                }
                is DevicesDialogViewHolder -> {
                    binding.apply {
                        tvCamName.text = items[position].name
                    }
                }
            }
        }
    }

    override fun getItemCount() = items.size
}