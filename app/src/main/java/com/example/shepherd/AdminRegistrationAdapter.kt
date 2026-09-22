package com.example.shepherd

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class AdminRegistrationAdapter(
    private val members: List<RegistrationMember>
) : RecyclerView.Adapter<AdminRegistrationAdapter.RegistrationViewHolder>() {

    class RegistrationViewHolder(itemView: View) :
        RecyclerView.ViewHolder(itemView) {

        val name: TextView =
            itemView.findViewById(R.id.tvRegistrationName)

        val email: TextView =
            itemView.findViewById(R.id.tvRegistrationEmail)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): RegistrationViewHolder {

        val view = LayoutInflater.from(parent.context)
            .inflate(
                R.layout.item_admin_registration,
                parent,
                false
            )

        return RegistrationViewHolder(view)
    }

    override fun onBindViewHolder(
        holder: RegistrationViewHolder,
        position: Int
    ) {

        val member = members[position]

        holder.name.text = member.name
        holder.email.text = member.email
    }

    override fun getItemCount(): Int {
        return members.size
    }
}