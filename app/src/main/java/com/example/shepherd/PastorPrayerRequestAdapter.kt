package com.example.shepherd

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class PastorPrayerRequestAdapter(
    private val prayerRequests: List<PrayerRequest>,
    private val onItemClick: (PrayerRequest) -> Unit
) : RecyclerView.Adapter<PastorPrayerRequestAdapter.PrayerRequestViewHolder>() {

    class PrayerRequestViewHolder(
        itemView: View
    ) : RecyclerView.ViewHolder(itemView) {

        val tvMemberName: TextView =
            itemView.findViewById(R.id.tvMemberName)

        val tvPrayerPreview: TextView =
            itemView.findViewById(R.id.tvPrayerPreview)

        val tvDate: TextView =
            itemView.findViewById(R.id.tvDate)

        val tvStatus: TextView =
            itemView.findViewById(R.id.tvStatus)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): PrayerRequestViewHolder {

        val view = LayoutInflater.from(parent.context).inflate(
            R.layout.item_pastor_prayer_request,
            parent,
            false
        )

        return PrayerRequestViewHolder(view)
    }

    override fun onBindViewHolder(
        holder: PrayerRequestViewHolder,
        position: Int
    ) {

        val prayerRequest = prayerRequests[position]

        holder.tvMemberName.text = "Prayer Request"

        holder.tvPrayerPreview.text =
            prayerRequest.requestText

        holder.tvDate.text =
            prayerRequest.category

        holder.tvStatus.text =
            prayerRequest.status

        holder.itemView.setOnClickListener {
            onItemClick(prayerRequest)
        }
    }

    override fun getItemCount(): Int {
        return prayerRequests.size
    }
}