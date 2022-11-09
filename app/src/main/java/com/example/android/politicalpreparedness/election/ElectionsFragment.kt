package com.example.android.politicalpreparedness.election

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.android.politicalpreparedness.database.ElectionDatabase
import com.example.android.politicalpreparedness.databinding.FragmentElectionBinding
import com.example.android.politicalpreparedness.election.adapter.ElectionListAdapter

class ElectionsFragment: Fragment() {

    private val TAG = "electionsViewModel"

    private lateinit var electionsViewModel: ElectionsViewModel

    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        val binding = FragmentElectionBinding.inflate(inflater)
        val application = requireNotNull(this.activity).application

        val dao = ElectionDatabase.getInstance(application).electionDao
        val viewModelFactory = ElectionsViewModelFactory(dao, application)
        electionsViewModel = ViewModelProvider(this, viewModelFactory)[ElectionsViewModel::class.java]

        binding.lifecycleOwner = this
        binding.viewModel = electionsViewModel

        val adapterUpcoming = ElectionListAdapter(ElectionListAdapter.ElectionListener { election ->
            electionsViewModel.onElectionClick(election)
        })

        val adapterSaved = ElectionListAdapter(ElectionListAdapter.ElectionListener { election ->
            electionsViewModel.onElectionClick(election)
        })

        // upcoming recyclerview
        binding.rvUpcoming.layoutManager = LinearLayoutManager(context)
        binding.rvUpcoming.adapter = adapterUpcoming

        // saved recyclerview
        binding.rvSaved.layoutManager = LinearLayoutManager(context)
        binding.rvSaved.adapter = adapterSaved

        electionsViewModel.upcoming.observe(viewLifecycleOwner, Observer {
            adapterUpcoming.submitList(it)
            Log.i(TAG, "upcoming list updated")
        })

        electionsViewModel.saved.observe(viewLifecycleOwner, Observer {
            adapterSaved.submitList(it)
            Log.i(TAG, "saved list updated")
        })

        // navigate from the election fragment to election detail fragment
        electionsViewModel.navigateToElectionDetail.observe(viewLifecycleOwner, Observer { election ->
            election?.let {
                this.findNavController().navigate(ElectionsFragmentDirections.actionElectionsFragmentToElectionsDetailFragment(election))
                electionsViewModel.onElectionDetailNavigated()
            }
        })

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // init data
        electionsViewModel.fetchUpcoming()
        electionsViewModel.fetchSaved()
    }

}