package com.example.android.politicalpreparedness.representative.detail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.example.android.politicalpreparedness.R
import com.example.android.politicalpreparedness.databinding.FragmentRepresentativeDetailBinding

class RepresentativeDetailFragment : Fragment() {

    private lateinit var representativeDetailViewModel: RepresentativeDetailViewModel
    private lateinit var binding: FragmentRepresentativeDetailBinding

    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        binding = FragmentRepresentativeDetailBinding.inflate(inflater)

        val viewModelFactory = RepresentativeDetailViewModelFactory()
        representativeDetailViewModel = ViewModelProvider(this, viewModelFactory)[RepresentativeDetailViewModel::class.java]

        binding.lifecycleOwner = this
        binding.viewModel = representativeDetailViewModel

        init()

        return binding.root
    }

    private fun init() {
        val photoUrl = RepresentativeDetailFragmentArgs.fromBundle(requireArguments()).photoUrl
        val uri = photoUrl.toUri().buildUpon().scheme("https").build()

        Glide.with(requireContext())
            .load(uri)
            .transform(CircleCrop())
            .placeholder(R.drawable.ic_profile)
            .error(R.drawable.ic_profile)
            .into(binding.ivProfile)


        binding.tvName.text = RepresentativeDetailFragmentArgs.fromBundle(requireArguments()).name
        binding.tvRole.text = RepresentativeDetailFragmentArgs.fromBundle(requireArguments()).role
    }
}