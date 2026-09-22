package com.example.shepherd

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class MemberPrayerRequestAdapter(
    private val prayerRequests: List<PrayerRequest>
) : RecyclerView.Adapter<MemberPrayerRequestAdapter.PrayerRequestViewHolder>() {

    class PrayerRequestViewHolder(
        itemView: View
    ) : RecyclerView.ViewHolder(itemView) {

        val tvPrayerPreview: TextView =
            itemView.findViewById(R.id.tvPrayerPreview)

        val tvDate: TextView =
            itemView.findViewById(R.id.tvDate)

        val tvStatus: TextView =
            itemView.findViewById(R.id.tvStatus)

        val tvFeedbackLabel: TextView =
            itemView.findViewById(R.id.tvFeedbackLabel)

        val tvPastorFeedback: TextView =
            itemView.findViewById(R.id.tvPastorFeedback)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): PrayerRequestViewHolder {

        val view =
            LayoutInflater.from(parent.context)
                .inflate(
                    R.layout.item_prayer_request,
                    parent,
                    false
                )

        return PrayerRequestViewHolder(view)
    }

    override fun onBindViewHolder(
        holder: PrayerRequestViewHolder,
        position: Int
    ) {

        val prayerRequest =
            prayerRequests[position]

        holder.tvPrayerPreview.text =
            prayerRequest.requestText

        holder.tvDate.text =
            prayerRequest.category

        holder.tvStatus.text =
            prayerRequest.status

        if (prayerRequest.pastorFeedback.isNotEmpty()) {

            holder.tvFeedbackLabel.visibility =
                View.VISIBLE

            holder.tvPastorFeedback.visibility =
                View.VISIBLE

            holder.tvPastorFeedback.text =
                prayerRequest.pastorFeedback

        } else {

            holder.tvFeedbackLabel.visibility =
                View.GONE

            holder.tvPastorFeedback.visibility =
                View.GONE
        }
    }

    override fun getItemCount(): Int {
        return prayerRequests.size
    }
}