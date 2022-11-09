package com.example.android.politicalpreparedness.representative

import android.Manifest
import android.annotation.TargetApi
import android.app.Activity
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
        //TODO: Add Constant for Location request
        private val REQUEST_FOREGROUND_PERMISSIONS_REQUEST_CODE = 34
    }

    private val TAG = "RepresentativeFragment"

    //TODO: Declare ViewModel
    private lateinit var representativeViewModel: RepresentativeViewModel
    private lateinit var binding: FragmentRepresentativeBinding

    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        //TODO: Establish bindings
        binding = FragmentRepresentativeBinding.inflate(inflater)

        val viewModelFactory = RepresentativeViewModelFactory()
        representativeViewModel = ViewModelProvider(this, viewModelFactory)[RepresentativeViewModel::class.java]

        binding.lifecycleOwner = this
        binding.viewModel = representativeViewModel

        //TODO: Define and assign Representative adapter
        val adapterRepresentative = RepresentativeListAdapter(RepresentativeListAdapter.RepresentativeListener { representative ->

        })
        binding.rvRepresentatives.layoutManager = LinearLayoutManager(context)
        binding.rvRepresentatives.adapter = adapterRepresentative

        //TODO: Populate Representative adapter

        //TODO: Establish button listeners for field and location search
        binding.btnSearch.setOnClickListener {
            hideKeyboard()

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

        binding.btnLocation.setOnClickListener {
            if (checkLocationPermissions()) {
                getLocation()
            }
        }

        representativeViewModel.representative.observe(viewLifecycleOwner) {
            adapterRepresentative.submitList(it)
        }



        return binding.root
    }

    private fun checkLocationPermissions(): Boolean {
        return if (isPermissionGranted()) {
            true
        } else {
            //TODO: Request Location permissions
            requestLocationPermissions()
            false
        }
    }

    private fun requestLocationPermissions() {
        if (foregroundLocationPermissionApproved())
            return

        var permissionsArray = arrayOf(Manifest.permission.ACCESS_FINE_LOCATION)

        requestPermissions(permissionsArray, REQUEST_FOREGROUND_PERMISSIONS_REQUEST_CODE)

    }

    private fun isPermissionGranted() : Boolean {
        return ContextCompat.checkSelfPermission(
            requireContext(),
            Manifest.permission.ACCESS_FINE_LOCATION) === PackageManager.PERMISSION_GRANTED
    }

    @TargetApi(29)
    private fun foregroundLocationPermissionApproved(): Boolean {
        return (PackageManager.PERMISSION_GRANTED ==
                        ActivityCompat.checkSelfPermission(
                            requireContext(),
                            Manifest.permission.ACCESS_FINE_LOCATION))
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        //TODO: Handle location permission result to get location on permission granted

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

    private fun getLocation() {
        //TODO: Get location from LocationServices
        //TODO: The geoCodeLocation method is a helper function to change the lat/long location to a human readable street address

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

    private fun geoCodeLocation(location: Location): Address {
        val geocoder = Geocoder(context, Locale.getDefault())
        return geocoder.getFromLocation(location.latitude, location.longitude, 1)
                .map { address ->
                    Address(address.thoroughfare, address.subThoroughfare, address.locality, address.adminArea, address.postalCode)
                }
                .first()
    }

    private fun hideKeyboard() {
        val imm = activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(requireView().windowToken, 0)
    }
}