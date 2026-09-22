package com.example.shepherd

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import java.text.SimpleDateFormat
import java.util.Locale

class NotificationAdapter(
    private val notifications: List<NotificationItem>
) : RecyclerView.Adapter<NotificationAdapter.NotificationViewHolder>() {

    class NotificationViewHolder(
        itemView: View
    ) : RecyclerView.ViewHolder(itemView) {

        val tvNotificationTitle =
            itemView.findViewById<TextView>(
                R.id.tvNotificationTitle
            )

        val tvNotificationMessage =
            itemView.findViewById<TextView>(
                R.id.tvNotificationMessage
            )

        val tvNotificationDate =
            itemView.findViewById<TextView>(
                R.id.tvNotificationDate
            )
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): NotificationViewHolder {

        val view =
            LayoutInflater.from(parent.context)
                .inflate(
                    R.layout.item_notification,
                    parent,
                    false
                )

        return NotificationViewHolder(view)
    }

    override fun onBindViewHolder(
        holder: NotificationViewHolder,
        position: Int
    ) {

        val notification =
            notifications[position]

        holder.tvNotificationTitle.text =
            notification.title

        holder.tvNotificationMessage.text =
            notification.message

        if (notification.createdAt != null) {

            val dateFormat =
                SimpleDateFormat(
                    "dd MMM yyyy, HH:mm",
                    Locale.getDefault()
                )

            holder.tvNotificationDate.text =
                dateFormat.format(
                    notification.createdAt.toDate()
                )

        } else {

            holder.tvNotificationDate.text =
                ""
        }
    }

    override fun getItemCount(): Int {
        return notifications.size
    }
}