package com.example.android.politicalpreparedness.election.detail

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.android.politicalpreparedness.R
import com.example.android.politicalpreparedness.database.ElectionDatabase
import com.example.android.politicalpreparedness.databinding.FragmentElectionDetailBinding
import com.example.android.politicalpreparedness.election.ElectionsViewModel
import com.example.android.politicalpreparedness.election.ElectionsViewModelFactory
import com.example.android.politicalpreparedness.network.models.Election
import com.google.android.material.snackbar.Snackbar

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
            // If the user was following, unfollow
            if (electionsDetailViewModel.following.value == true) {
                electionsDetailViewModel.unfollow()
            } else {
                // else follow
                electionsDetailViewModel.follow()
            }
        }

        // Onclick for voting locations, shows snackbar if there is no voting information
        binding.tvLocation.setOnClickListener{
            val url = electionsDetailViewModel.votingUrl.value

            if (url.isNullOrEmpty()) {
                Snackbar.make(binding.clLayout, "No Voting Location Information", Snackbar.LENGTH_LONG).show()
            } else {
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(electionsDetailViewModel.votingUrl.value))
                startActivity(intent)
            }
        }

        // Onclick for ballot information, shows snackbar if theres no voting information
        binding.tvBallot.setOnClickListener{
            val url = electionsDetailViewModel.ballotUrl.value

            if (url.isNullOrEmpty()) {
                Snackbar.make(binding.clLayout, "No Ballot Information", Snackbar.LENGTH_LONG).show()
            } else {
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(electionsDetailViewModel.ballotUrl.value))
                startActivity(intent)
            }
        }

        // Inits the button status whether the user is following or not
        electionsDetailViewModel.following.observe(viewLifecycleOwner, Observer { following ->
            if (following) {
                binding.btnFollow.text = "Unfollow Election"
                binding.btnFollow.setBackgroundColor(resources.getColor(R.color.colorPrimaryDark))
            } else {
                binding.btnFollow.text = "Follow Election"
                binding.btnFollow.setBackgroundColor(resources.getColor(R.color.colorPrimary))
            }
        })

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        electionsDetailViewModel.setElection(election)
        electionsDetailViewModel.isFollowing()
        electionsDetailViewModel.fetch()
    }
}