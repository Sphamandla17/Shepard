package com.example.shepherd

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class AdminEventsAdapter(
    private val events: List<EventItem>,
    private val onEventSelected: (EventItem) -> Unit
) : RecyclerView.Adapter<AdminEventsAdapter.EventViewHolder>() {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): EventViewHolder {

        val view =
            LayoutInflater.from(parent.context)
                .inflate(
                    R.layout.item_admin_event,
                    parent,
                    false
                )

        return EventViewHolder(view)
    }

    override fun onBindViewHolder(
        holder: EventViewHolder,
        position: Int
    ) {

        val event =
            events[position]

        holder.tvTitle.text =
            event.title

        holder.tvDate.text =
            event.date

        holder.tvTime.text =
            event.time

        holder.tvLocation.text =
            event.location

        holder.btnViewRegistrations.setOnClickListener {

            onEventSelected(event)
        }
    }

    override fun getItemCount(): Int {
        return events.size
    }

    class EventViewHolder(
        itemView: View
    ) : RecyclerView.ViewHolder(itemView) {

        val tvTitle: TextView =
            itemView.findViewById(
                R.id.tvAdminEventTitle
            )

        val tvDate: TextView =
            itemView.findViewById(
                R.id.tvAdminEventDate
            )

        val tvTime: TextView =
            itemView.findViewById(
                R.id.tvAdminEventTime
            )

        val tvLocation: TextView =
            itemView.findViewById(
                R.id.tvAdminEventLocation
            )

        val btnViewRegistrations: Button =
            itemView.findViewById(
                R.id.btnViewRegistrations
            )
    }
}