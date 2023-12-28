package com.example.angelocapstoneproject.ui.home.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.angelocapstoneproject.data.local.model.PlaybackThumbnail
import com.example.angelocapstoneproject.databinding.PlaybacksThumbnailItemLayoutBinding
import com.example.angelocapstoneproject.domain.helper.load

class PlaybackThumbnailsAdapter(
    private val items:List<PlaybackThumbnail>,
    /*spanCount:Int,
    columnCount:Int*/
):RecyclerView.Adapter<PlaybackThumbnailsAdapter.PlaybackThumbnailViewHolder>() {

    //private val orderedItems:List<PlaybackThumbnail?>

    init {
     /*   orderedItems = items.toMutableList().let {

            val fixImages = MutableList<PlaybackThumbnail?>(columnCount*spanCount){
                null
            }

            for (pos in it.indices){

                val startPos = (pos%columnCount)*spanCount
                val row = pos.div(columnCount)
                val finishPos = startPos+row
                fixImages[finishPos] = it[pos]
            }
            fixImages
        }*/
    }

    class PlaybackThumbnailViewHolder(
        val binding:PlaybacksThumbnailItemLayoutBinding
    ):RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlaybackThumbnailViewHolder {
        val binding = PlaybacksThumbnailItemLayoutBinding.inflate(
            LayoutInflater.from(
                parent.context
            ),
            parent,
            false
        )
        return PlaybackThumbnailViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PlaybackThumbnailViewHolder, position: Int) {
        val currItem = items[position]

        holder.apply {
            binding.apply {
                ivImage.load(
                    currItem.image
                )
                tvCamName.text = currItem.camName
            }
        }
    }

    override fun getItemCount() = items.size
}