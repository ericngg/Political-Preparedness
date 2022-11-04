package com.example.android.politicalpreparedness.election

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.android.politicalpreparedness.database.ElectionDatabase
import com.example.android.politicalpreparedness.databinding.FragmentElectionBinding
import com.example.android.politicalpreparedness.election.adapter.ElectionListAdapter

class ElectionsFragment: Fragment() {

    //TODO: Declare ViewModel
    private lateinit var electionsViewModel: ElectionsViewModel

    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        val binding = FragmentElectionBinding.inflate(inflater)
        val application = requireNotNull(this.activity).application

        val dataSource = ElectionDatabase.getInstance(application).electionDao

        //TODO: Add ViewModel values and create ViewModel
        val viewModelFactory = ElectionsViewModelFactory(dataSource, application)
        electionsViewModel = ViewModelProvider(this, viewModelFactory)[ElectionsViewModel::class.java]

        binding.lifecycleOwner = this
        binding.viewModel = electionsViewModel

        val adapterUpcoming = ElectionListAdapter(ElectionListAdapter.ElectionListener { election ->

        })

        val adapterSaved = ElectionListAdapter(ElectionListAdapter.ElectionListener { election ->

        })

        binding.rvUpcoming.layoutManager = LinearLayoutManager(context)
        binding.rvUpcoming.adapter = adapterUpcoming

        binding.rvSaved.layoutManager = LinearLayoutManager(context)
        binding.rvSaved.adapter = adapterSaved

        // Create navigation to other fragments for onClick

        //TODO: Add binding values
        //TODO: Link elections to voter info
        //TODO: Initiate recycler adapters
        //TODO: Populate recycler adapters

        return binding.root
    }

    //TODO: Refresh adapters when fragment loads

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        electionsViewModel.fetchUpcoming()
        electionsViewModel.fetchSaved()
    }

}