package com.example.shepherd

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class AdminMemberAdapter(
    private val members: List<Member>
) : RecyclerView.Adapter<AdminMemberAdapter.MemberViewHolder>() {

    class MemberViewHolder(itemView: View) :
        RecyclerView.ViewHolder(itemView) {

        val tvMemberName =
            itemView.findViewById<TextView>(R.id.tvMemberName)

        val tvMemberEmail =
            itemView.findViewById<TextView>(R.id.tvMemberEmail)

        val tvMemberPhone =
            itemView.findViewById<TextView>(R.id.tvMemberPhone)

        val tvMemberRole =
            itemView.findViewById<TextView>(R.id.tvMemberRole)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): MemberViewHolder {

        val view = LayoutInflater.from(parent.context)
            .inflate(
                R.layout.item_admin_member,
                parent,
                false
            )

        return MemberViewHolder(view)
    }

    override fun onBindViewHolder(
        holder: MemberViewHolder,
        position: Int
    ) {

        val member = members[position]

        holder.tvMemberName.text =
            "${member.firstName} ${member.lastName}"

        holder.tvMemberEmail.text =
            member.email

        holder.tvMemberPhone.text =
            member.phone

        holder.tvMemberRole.text =
            member.role
    }

    override fun getItemCount(): Int {
        return members.size
    }
}