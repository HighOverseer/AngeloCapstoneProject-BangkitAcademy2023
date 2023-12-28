package com.example.angelocapstoneproject.ui.home.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.angelocapstoneproject.data.local.model.Contact
import com.example.angelocapstoneproject.databinding.ContactItemLayoutBinding

class EmergencyContactAdapter(
    private val contacts:List<Contact>,
    private val onItemClick:(Contact) -> Unit
):RecyclerView.Adapter<EmergencyContactAdapter.EmergencyContactViewHolder>() {

    class EmergencyContactViewHolder(
        val binding:ContactItemLayoutBinding,
        clickedAtPostion : (Int) -> Unit
    ):RecyclerView.ViewHolder(binding.root){
        init {
            itemView.setOnClickListener {
                clickedAtPostion(absoluteAdapterPosition)
            }
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): EmergencyContactViewHolder {
        val binding = ContactItemLayoutBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return EmergencyContactViewHolder(binding){ position ->
            onItemClick(contacts[position])
        }
    }

    override fun onBindViewHolder(
        holder: EmergencyContactViewHolder,
        position: Int
    ) {
        val currItem = contacts[position]
        holder.apply {
            binding.apply {
                tvContactName.text = currItem.name
                tvContactNo.text = currItem.number
            }
        }
    }

    override fun getItemCount() = contacts.size
}