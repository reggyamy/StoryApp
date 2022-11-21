package com.reggya.stroryapp.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.reggya.stroryapp.data.ListStoryItem
import com.reggya.stroryapp.databinding.StoryItemBinding

class StoriesAdapter : RecyclerView.Adapter<StoriesAdapter.ViewHolder>() {

    private var stories = ArrayList<ListStoryItem?>()
    var itemOnClick : ((ListStoryItem?) -> Unit)?  = null

    fun setNewData(newData: List<ListStoryItem?>?){
        if (newData == null) return
        stories.clear()
        stories.addAll(newData)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(StoryItemBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val story = stories[position]
        story?.let { holder.bind(it) }
    }

    override fun getItemCount(): Int = stories.size


    inner class ViewHolder(private val binding: StoryItemBinding): RecyclerView.ViewHolder(binding.root) {
        fun bind(story: ListStoryItem) {
            Glide.with(itemView).load(story.photoUrl).into(binding.image)
            binding.title.text = story.name

            itemView.setOnClickListener {
                itemOnClick?.invoke(story)
            }
        }

    }

}