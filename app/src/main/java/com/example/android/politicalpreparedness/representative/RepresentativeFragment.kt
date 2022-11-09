package com.example.android.politicalpreparedness.representative

import android.Manifest
import android.annotation.TargetApi
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.*
import android.view.inputmethod.InputMethodManager
import android.widget.ArrayAdapter
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.android.politicalpreparedness.BuildConfig
import com.example.android.politicalpreparedness.R
import com.example.android.politicalpreparedness.databinding.FragmentRepresentativeBinding
import com.example.android.politicalpreparedness.network.models.Address
import com.example.android.politicalpreparedness.representative.adapter.RepresentativeListAdapter
import com.google.android.gms.location.LocationServices
import com.google.android.material.snackbar.Snackbar
import java.util.Locale

class DetailFragment : Fragment() {

    companion object {
        private val REQUEST_FOREGROUND_PERMISSIONS_REQUEST_CODE = 34
    }

    private val TAG = "RepresentativeFragment"

    private lateinit var representativeViewModel: RepresentativeViewModel
    private lateinit var binding: FragmentRepresentativeBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        binding = FragmentRepresentativeBinding.inflate(inflater)

        val viewModelFactory = RepresentativeViewModelFactory()
        representativeViewModel = ViewModelProvider(this, viewModelFactory)[RepresentativeViewModel::class.java]

        binding.lifecycleOwner = this
        binding.viewModel = representativeViewModel

        val adapterRepresentative = RepresentativeListAdapter(RepresentativeListAdapter.RepresentativeListener { representative ->
            representativeViewModel.onRepresentativeClick(representative)
        })
        binding.rvRepresentatives.layoutManager = LinearLayoutManager(context)
        binding.rvRepresentatives.adapter = adapterRepresentative

        // Find my representatives button onClick
        binding.btnSearch.setOnClickListener {
            hideKeyboard()

            // Makes sure none of the text fields are empty
            when {
                binding.tvAddress1.text.isNullOrEmpty() -> {
                    Snackbar.make(binding.mlLayout, getString(R.string.empty_address1), Snackbar.LENGTH_LONG).show()
                }
                binding.tvAddress2.text.isNullOrEmpty() -> {
                    Snackbar.make(binding.mlLayout, getString(R.string.empty_address2), Snackbar.LENGTH_LONG).show()
                }
                binding.tvCity.text.isNullOrEmpty() -> {
                    Snackbar.make(binding.mlLayout, getString(R.string.empty_city), Snackbar.LENGTH_LONG).show()
                }
                binding.tvZip.text.isNullOrEmpty() -> {
                    Snackbar.make(binding.mlLayout, getString(R.string.empty_zip), Snackbar.LENGTH_LONG).show()
                }
                else -> {
                    representativeViewModel.setAddress(
                        binding.tvAddress1.text.toString(),
                        binding.tvAddress2.text.toString(),
                        binding.tvCity.text.toString(),
                        binding.sState.selectedItem.toString(),
                        binding.tvZip.text.toString()
                    )

                    representativeViewModel.fetch()
                }
            }
        }

        // Find my location button onClick
        binding.btnLocation.setOnClickListener {
            if (checkLocationPermissions()) {
                getLocation()
            }
        }

        representativeViewModel.representative.observe(viewLifecycleOwner) {
            adapterRepresentative.submitList(it)
        }

        representativeViewModel.navigateToRepresentativeDetail.observe(viewLifecycleOwner) { representative ->
            representative?.let {
                var photoUrl = ""

                if (representative.official.photoUrl != null) {
                    photoUrl = representative.official.photoUrl
                }

                this.findNavController().navigate(DetailFragmentDirections.actionRepresentativeFragmentToRepresentativeDetailFragment(
                    photoUrl,
                    representative.official.name!!,
                    representative.office.name!!
                ))
                representativeViewModel.onNavigatedRepresentativeDetailNavigated()
            }
        }
        return binding.root
    }

    /**
     *  Checks if permission is granted
     *  If false, request for permissions
     *
     */
    private fun checkLocationPermissions(): Boolean {
        return if (isPermissionGranted()) {
            true
        } else {
            requestLocationPermissions()
            false
        }
    }

    /**
     *  Requests for location permissions
     *
     */
    private fun requestLocationPermissions() {
        if (foregroundLocationPermissionApproved())
            return

        var permissionsArray = arrayOf(Manifest.permission.ACCESS_FINE_LOCATION)
        requestPermissions(permissionsArray, REQUEST_FOREGROUND_PERMISSIONS_REQUEST_CODE)

    }

    /**
     *  Checks if location permission was granted
     *
     */
    private fun isPermissionGranted() : Boolean {
        return ContextCompat.checkSelfPermission(
            requireContext(),
            Manifest.permission.ACCESS_FINE_LOCATION) === PackageManager.PERMISSION_GRANTED
    }

    /**
     *  Checks if location permissions were approved
     *
     */
    @TargetApi(29)
    private fun foregroundLocationPermissionApproved(): Boolean {
        return (PackageManager.PERMISSION_GRANTED ==
                        ActivityCompat.checkSelfPermission(
                            requireContext(),
                            Manifest.permission.ACCESS_FINE_LOCATION))
    }

    /**
     *  Handles location permission results to get location on permission granted
     *
     *  If permission not granted, a snackbar appears with settings intent
     */
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == REQUEST_FOREGROUND_PERMISSIONS_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && (grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                getLocation()
            } else if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_DENIED){
                Snackbar.make(binding.mlLayout, getString(R.string.location_denied), Snackbar.LENGTH_INDEFINITE).setAction(
                    R.string.settings) {
                    startActivity(
                        Intent().apply {
                            action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
                            data = Uri.fromParts("package", BuildConfig.APPLICATION_ID, null)
                            flags = Intent.FLAG_ACTIVITY_NEW_TASK
                        }
                    )
                }.show()
            }
        }
    }

    /**
     *  Gets location of user and  fills the text boxes with the address
     *
     */
    private fun getLocation() {
        var fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())

        if (ActivityCompat.checkSelfPermission(
                requireActivity(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                requireActivity(),
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }

        fusedLocationClient.lastLocation
            .addOnSuccessListener { location: Location? ->
                Log.i(TAG, location.toString())

                if (location != null) {
                    val address = geoCodeLocation(location)

                    binding.tvAddress1.setText(address.line1)
                    binding.tvAddress2.setText(address.line2)
                    binding.tvCity.setText(address.city)
                    var pos = (binding.sState.adapter as ArrayAdapter<String?>).getPosition(address.state)
                    binding.sState.setSelection(pos)
                    binding.tvZip.setText(address.zip)

                    representativeViewModel.setAddress(
                        binding.tvAddress1.text.toString(),
                        binding.tvAddress2.text.toString(),
                        binding.tvCity.text.toString(),
                        binding.sState.selectedItem.toString(),
                        binding.tvZip.text.toString()
                    )

                    representativeViewModel.fetch()
                }
            }

    }

    /**
     *  Converts a geo Location object with latitude and longitude and returns an address object
     *
     */
    private fun geoCodeLocation(location: Location): Address {
        val geocoder = Geocoder(context, Locale.getDefault())
        return geocoder.getFromLocation(location.latitude, location.longitude, 1)
                .map { address ->
                    Address(address.thoroughfare, address.subThoroughfare, address.locality, address.adminArea, address.postalCode)
                }
                .first()
    }

    /**
     *  Hides the keyboard
     *
     */
    private fun hideKeyboard() {
        val imm = activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(requireView().windowToken, 0)
    }
}