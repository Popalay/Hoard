package com.github.popalay.hoard.ui

import android.support.v7.recyclerview.extensions.ListAdapter
import android.support.v7.util.DiffUtil
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.github.popalay.hoard.R
import com.github.popalay.hoard.model.GithubUser
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.item_user.view.*

class UserAdapter : ListAdapter<GithubUser, UserAdapter.UserViewHolder>(DIFF_CALLBACK) {

    companion object {

        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<GithubUser>() {

            override fun areItemsTheSame(oldItem: GithubUser?, newItem: GithubUser?) = oldItem?.id == newItem?.id

            override fun areContentsTheSame(oldItem: GithubUser?, newItem: GithubUser?) = oldItem == newItem

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_user, parent, false)
        return UserViewHolder(view)
    }

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class UserViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun bind(item: GithubUser) {
            itemView.textUserName.text = item.login
            Picasso.get()
                .load(item.avatarUrl)
                .placeholder(R.mipmap.ic_launcher)
                .into(itemView.imageUser)
        }
    }
}