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

    private val _address = MutableLiveData<String>()
    val address: LiveData<String>
        get() = _address

    private val _navigateToRepresentativeDetail = MutableLiveData<Representative>()
    val navigateToRepresentativeDetail: LiveData<Representative>
        get() = _navigateToRepresentativeDetail

    // Test address
    private val testAddress = "621 129th PL NE Bellevue WA 98005"

    fun onRepresentativeClick(representative: Representative) {
        _navigateToRepresentativeDetail.value = representative
    }

    fun onNavigatedRepresentativeDetailNavigated() {
        _navigateToRepresentativeDetail.value = null
    }

    /**
     *  Calls the api to fetch representatives from the API with a provided address
     *
     *  The following code will prove helpful in constructing a representative from the API. This code combines the two nodes of the RepresentativeResponse into a single official :
     *  val (offices, officials) = getRepresentativesDeferred.await()
     *  _representatives.value = offices.flatMap { office -> office.getRepresentatives(officials) }
     *  Note: getRepresentatives in the above code represents the method used to fetch data from the API
     *  Note: _representatives in the above code represents the established mutable live data housing representatives
     */
    fun fetch() {
        viewModelScope.launch {
            try {
                //val repResponse = CivicsApi.retrofitService.getRepresentatives(BuildConfig.API_KEY, testAddress)

                val repResponse = CivicsApi.retrofitService.getRepresentatives(BuildConfig.API_KEY, _address.value!!)

                val (offices, officials) = repResponse
                _representatives.value = offices.flatMap {offices -> offices.getRepresentatives(officials)}
            } catch (e: Exception) {
                Log.e(TAG, "Representative fetch error $e")
            }
        }
    }

    /**
     *  Called after find my representative button was pressed
     *
     *  Creates a string with the whole address and sets the address value with the new address
     */
    fun setAddress(address1: String, address2: String, city: String, state: String, zip: String) {
        _address.value = "$address2 $address1 $city $state $zip"
        Log.i("test", _address.value!!)
    }
}
