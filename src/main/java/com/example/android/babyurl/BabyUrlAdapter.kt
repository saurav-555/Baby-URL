package com.example.android.babyurl

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.android.babyurl.databinding.BabyUrlItemBinding
import com.example.android.babyurl.home.UrlHome


class BabyUrlAdapter(private val clickListener : BabyUrlListener) : ListAdapter<UrlHome, BabyUrlAdapter.ViewHolder>(BabyUrlDiffCallback()) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BabyUrlAdapter.ViewHolder {
        return ViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: BabyUrlAdapter.ViewHolder, position: Int) {
        holder.bind(getItem(position), clickListener)
    }

    class ViewHolder private constructor(val binding : BabyUrlItemBinding) : RecyclerView.ViewHolder(binding.root){
        // for BindViewHolder
        fun bind(item : UrlHome , clickListener : BabyUrlListener){
            binding.urlHome = item
            binding.clickListener = clickListener
            binding.executePendingBindings()
        }
        // for CreateViewHolder
        companion object{
            fun from(parent: ViewGroup) : ViewHolder{
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = BabyUrlItemBinding.inflate(layoutInflater, parent, false)
                return ViewHolder(binding)
            }
        }
    }

}

class BabyUrlDiffCallback : DiffUtil.ItemCallback<UrlHome>(){
    override fun areItemsTheSame(oldItem: UrlHome, newItem: UrlHome): Boolean {
        return oldItem.rowId == newItem.rowId
    }
    override fun areContentsTheSame(oldItem: UrlHome, newItem: UrlHome): Boolean {
        return oldItem == newItem
    }
}

class BabyUrlListener(val copyClick : (shortUrl: String) -> Unit , val deleteClick : (rowId : Long) -> Unit) {
    fun onCopyClick(urlHome: UrlHome) = copyClick(urlHome.shortUrl)
    fun onDeleteClick(urlHome: UrlHome) = deleteClick(urlHome.rowId)
}
