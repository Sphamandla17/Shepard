package com.example.shepherd.api

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.shepherd.R

class AnnouncementAdapter(
    private var announcements: List<Announcement>
) : RecyclerView.Adapter<AnnouncementAdapter.AnnouncementViewHolder>() {

    class AnnouncementViewHolder(
        itemView: View
    ) : RecyclerView.ViewHolder(itemView) {

        val title: TextView =
            itemView.findViewById(
                R.id.tvAnnouncementTitle
            )

        val message: TextView =
            itemView.findViewById(
                R.id.tvAnnouncementMessage
            )

        val createdBy: TextView =
            itemView.findViewById(
                R.id.tvAnnouncementCreatedBy
            )
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): AnnouncementViewHolder {

        val view =
            LayoutInflater.from(parent.context)
                .inflate(
                    R.layout.announcement_item,
                    parent,
                    false
                )

        return AnnouncementViewHolder(view)
    }

    override fun onBindViewHolder(
        holder: AnnouncementViewHolder,
        position: Int
    ) {

        val announcement =
            announcements[position]

        holder.title.text =
            announcement.title

        holder.message.text =
            announcement.message

        holder.createdBy.text =
            "Posted by: ${announcement.createdBy}"
    }

    override fun getItemCount(): Int {
        return announcements.size
    }

    fun updateAnnouncements(
        newAnnouncements: List<Announcement>
    ) {

        announcements =
            newAnnouncements

        notifyDataSetChanged()
    }
}