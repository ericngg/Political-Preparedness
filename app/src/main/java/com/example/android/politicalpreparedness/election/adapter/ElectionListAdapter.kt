package com.example.android.politicalpreparedness.election.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.android.politicalpreparedness.databinding.ItemElectionBinding
import com.example.android.politicalpreparedness.network.models.Election

class ElectionListAdapter(private val clickListener: ElectionListener): ListAdapter<Election, ElectionListAdapter.ElectionViewHolder>(ElectionDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ElectionViewHolder {
        return ElectionViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: ElectionViewHolder, position: Int) {
        holder.bind(clickListener, getItem(position)!!)
    }

    class ElectionViewHolder(val binding: ItemElectionBinding): RecyclerView.ViewHolder(binding.root) {
        fun bind(clickListener: ElectionListener, election: Election) {
            binding.election = election
            binding.clickListener = clickListener
            binding.executePendingBindings()

            binding.tvElectionName.text = election.name
            binding.tvElectionDate.text = election.electionDay.toString()
        }

        companion object {
            fun from(parent: ViewGroup): ElectionViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = ItemElectionBinding.inflate(layoutInflater, parent, false)

                return ElectionViewHolder(binding)
            }
        }
    }

    /**
     *  Election DiffUtil
     *
     **/
    class ElectionDiffCallback : DiffUtil.ItemCallback<Election>() {
        override fun areItemsTheSame(oldItem: Election, newItem: Election): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Election, newItem: Election): Boolean {
            return oldItem == newItem
        }

    }

    /**
     *  Election onClick Listener
     *
     **/
    class ElectionListener(val clickListener: (election: Election) -> Unit) {
        fun onClick(election: Election) = clickListener(election)
    }
}