package com.example.lumonote.ui.home

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
    : RecyclerView.Adapter<TagDisplayAdapter.TagDisplayViewHolder>()
{
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

    override fun onBindViewHolder(holder: TagDisplayViewHolder, position: Int) {
        // Find and store the equivalent tag object in the list meant to be same as in UI
        val tag = tagsList[position]

        if (position == 0) {
            holder.tagLayoutView.setBackgroundColor(getColor(context, R.color.gold))
            holder.tagName.setTextColor(getColor(context, R.color.dark_grey))
            holder.tagName.setTypeface(null, Typeface.BOLD);
        }

        // Populate the UI tag at that position with the data from the tag obj at same
        // index in the list
        holder.tagName.text = tag.tagName

        // change displayed notes by clicking on tag
        holder.tagCardView.setOnClickListener {
            // Logic here to change notes
            Log.d("TagsDisplayAdapter", "Clicked!")
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