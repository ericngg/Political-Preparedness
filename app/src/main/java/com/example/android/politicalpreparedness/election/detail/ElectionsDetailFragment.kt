package com.example.android.politicalpreparedness.election.detail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.android.politicalpreparedness.database.ElectionDatabase
import com.example.android.politicalpreparedness.databinding.FragmentElectionDetailBinding
import com.example.android.politicalpreparedness.election.ElectionsViewModel
import com.example.android.politicalpreparedness.election.ElectionsViewModelFactory
import com.example.android.politicalpreparedness.network.models.Election

class ElectionsDetailFragment : Fragment() {

    private lateinit var electionsDetailViewModel: ElectionsDetailViewModel

    private lateinit var election: Election

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val binding = FragmentElectionDetailBinding.inflate(inflater)
        binding.lifecycleOwner = this

        election = ElectionsDetailFragmentArgs.fromBundle(requireArguments()).election

        binding.election = election

        val application = requireNotNull(this.activity).application
        val dao = ElectionDatabase.getInstance(application).electionDao

        val viewModelFactory = ElectionsDetailViewModelFactory(dao, application)
        electionsDetailViewModel = ViewModelProvider(this, viewModelFactory)[ElectionsDetailViewModel::class.java]

        election.let {
            activity?.actionBar?.title = it.name
        }

        binding.btnFollow.setOnClickListener {

        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        electionsDetailViewModel.setElection(election)
        electionsDetailViewModel.fetch()
    }
}