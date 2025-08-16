package com.example.lumonote.ui.home

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Typeface
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat.getColor
import androidx.recyclerview.widget.RecyclerView
import com.example.lumonote.R
import com.example.lumonote.data.models.Tag

// Inherits from RecyclerView.Adapter to allow definition of recycler view behaviour
class TagDisplayAdapter(private var tagsList: List<Tag>, val context: Context)
    : RecyclerView.Adapter<TagDisplayAdapter.TagDisplayViewHolder>(){

    // Track the currently highlighted/selected item
    private var selectedPosition = 0 // default first item is highlighted

    // The layout from which this view data is accessed is passed into this later
    class TagDisplayViewHolder (tagDisplayView: View) : RecyclerView.ViewHolder(tagDisplayView) {
        val tagCardView: CardView = tagDisplayView.findViewById(R.id.tagItemCV)
        val tagLayoutView: LinearLayout = tagDisplayView.findViewById(R.id.tagItemLayoutLL)
        val tagName: TextView = tagDisplayView.findViewById(R.id.tagNameTV)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TagDisplayViewHolder {
        val tagDisplayView = LayoutInflater.from(parent.context).inflate(R.layout.item_tag,
            parent, false)

        return TagDisplayViewHolder(tagDisplayView)
    }

    // Returns the total number of items in the data set held by the adapter
    override fun getItemCount(): Int {
        return tagsList.size
    }

    override fun onBindViewHolder(holder: TagDisplayViewHolder, @SuppressLint("RecyclerView") position: Int) {
        // Find and store the equivalent tag object in the list meant to be same as in UI
        val tag = tagsList[position]
        // Populate the UI tag at that position
        holder.tagName.text = tag.tagName
        // Note: position of the tags can change dynamically at runtime


        // Apply selected/highlight style
        if (position == selectedPosition) {
            holder.tagLayoutView.setBackgroundColor(getColor(context, R.color.gold))
            holder.tagName.setTextColor(getColor(context, R.color.dark_grey))
            holder.tagName.setTypeface(null, Typeface.BOLD);
        } else {
            // Reset other items to default
            holder.tagLayoutView.setBackgroundColor(getColor(context, R.color.dark_grey))
            holder.tagName.setTextColor(getColor(context, R.color.light_grey_2))
            holder.tagName.setTypeface(null, Typeface.NORMAL);
        }

        // Handle click to change selection
        holder.tagCardView.setOnClickListener {
            val previousPosition = selectedPosition
            selectedPosition = position
            notifyItemChanged(previousPosition) // refresh old selected
            notifyItemChanged(selectedPosition) // refresh new selected
        }


    }

    // Ensure UI stays up-to-date with tagss list
    fun refreshData(newTags: List<Tag>) {
        tagsList = newTags

        notifyDataSetChanged()
    }

    fun toggleTagColor() {

    }
}