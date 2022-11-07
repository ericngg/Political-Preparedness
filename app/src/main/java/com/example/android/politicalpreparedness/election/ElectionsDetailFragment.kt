package com.example.android.politicalpreparedness.election

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import com.example.android.politicalpreparedness.R
import com.example.android.politicalpreparedness.databinding.FragmentElectionDetailBinding

class ElectionsDetailFragment : Fragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val binding = FragmentElectionDetailBinding.inflate(inflater)
        binding.lifecycleOwner = this

        val election = ElectionsDetailFragmentArgs.fromBundle(requireArguments()).election
        binding.election = election

        binding.btnFollow.setOnClickListener {
            
        }

        return binding.root
    }
}