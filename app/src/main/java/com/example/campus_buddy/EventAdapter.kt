package com.example.campus_buddy

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class EventAdapter(
    private var events: List<UnifiedEvent>,
    private val onItemClick: (UnifiedEvent) -> Unit
) : RecyclerView.Adapter<EventAdapter.EventViewHolder>() {

    inner class EventViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val title: TextView = itemView.findViewById(R.id.tvEventTitle)
        val time: TextView = itemView.findViewById(R.id.tvEventTime)
        val description: TextView = itemView.findViewById(R.id.tvEventDescription)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EventViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_event, parent, false)
        return EventViewHolder(view)
    }

    override fun onBindViewHolder(holder: EventViewHolder, position: Int) {
        val event = events[position]
        holder.title.text = event.title
        holder.time.text = event.time ?: "No time"
        holder.description.text = event.description ?: "No description"

        // Highlight Google events (optional)
        if (event.isGoogleEvent) {
            holder.title.setTextColor(holder.itemView.context.getColor(android.R.color.holo_blue_dark))
        }

        holder.itemView.setOnClickListener { onItemClick(event) }
    }

    override fun getItemCount(): Int = events.size

    fun updateEvents(newEvents: List<UnifiedEvent>) {
        events = newEvents
        notifyDataSetChanged()
    }
}
