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

    private val testAddress = "621 129th PL NE Bellevue WA 98005"

    fun fetch() {
        viewModelScope.launch {
            try {
                val repResponse = CivicsApi.retrofitService.getRepresentatives(BuildConfig.API_KEY, testAddress)

                val offices = repResponse.offices
                val officials = repResponse.officials

                _representatives.value = offices.flatMap {offices -> offices.getRepresentatives(officials)}

                
            } catch (e: Exception) {
                Log.e(TAG, "Representative fetch error $e")
            }
        }
    }
    //TODO: Establish live data for representatives and address

    //TODO: Create function to fetch representatives from API from a provided address

    /**
     *  The following code will prove helpful in constructing a representative from the API. This code combines the two nodes of the RepresentativeResponse into a single official :

    val (offices, officials) = getRepresentativesDeferred.await()
    _representatives.value = offices.flatMap { office -> office.getRepresentatives(officials) }

    Note: getRepresentatives in the above code represents the method used to fetch data from the API
    Note: _representatives in the above code represents the established mutable live data housing representatives

     */

    //TODO: Create function get address from geo location

    //TODO: Create function to get address from individual fields

}
