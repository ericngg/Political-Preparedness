package com.example.android.politicalpreparedness.representative

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.android.politicalpreparedness.BuildConfig
import com.example.android.politicalpreparedness.network.CivicsApi
import com.example.android.politicalpreparedness.representative.model.Representative
import kotlinx.coroutines.launch

class RepresentativeViewModel: ViewModel() {

    private val TAG = "RepresentativeViewModel"

    private val _representatives = MutableLiveData<List<Representative>>()
    val representative: LiveData<List<Representative>>
        get() = _representatives

    //TODO: Establish live data for representatives and address
    private val _address = MutableLiveData<String>()
    val address: LiveData<String>
        get() = _address

    private val testAddress = "621 129th PL NE Bellevue WA 98005"

    //TODO: Create function to fetch representatives from API from a provided address
    fun fetch() {
        viewModelScope.launch {
            try {
                val repResponse = CivicsApi.retrofitService.getRepresentatives(BuildConfig.API_KEY, _address.value!!)

                //val repResponse = CivicsApi.retrofitService.getRepresentatives(BuildConfig.API_KEY, testAddress)

                val (offices, officials) = repResponse
                _representatives.value = offices.flatMap {offices -> offices.getRepresentatives(officials)}
            } catch (e: Exception) {
                Log.e(TAG, "Representative fetch error $e")
            }
        }
    }


    /**
     *  The following code will prove helpful in constructing a representative from the API. This code combines the two nodes of the RepresentativeResponse into a single official :

    val (offices, officials) = getRepresentativesDeferred.await()
    _representatives.value = offices.flatMap { office -> office.getRepresentatives(officials) }

    Note: getRepresentatives in the above code represents the method used to fetch data from the API
    Note: _representatives in the above code represents the established mutable live data housing representatives

     */

    //TODO: Create function to get address from individual fields
    fun setAddress(address1: String, address2: String, city: String, state: String, zip: String) {
        _address.value = "$address2 $address1 $city $state $zip"
        Log.i("test", _address.value!!)
    }
}
