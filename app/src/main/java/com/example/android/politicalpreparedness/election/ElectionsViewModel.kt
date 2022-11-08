package com.example.android.politicalpreparedness.election

import android.app.Application
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.android.politicalpreparedness.BuildConfig
import com.example.android.politicalpreparedness.database.ElectionDao
import com.example.android.politicalpreparedness.database.ElectionDatabase
import com.example.android.politicalpreparedness.network.CivicsApi
import com.example.android.politicalpreparedness.network.CivicsApiService
import com.example.android.politicalpreparedness.network.models.Election
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

//TODO: Construct ViewModel and provide election datasource
class ElectionsViewModel(val dao: ElectionDao, val app: Application): ViewModel() {

    private val TAG = "ElectionViewModel"

    private val database = ElectionDatabase.getInstance(app).electionDao

    //TODO: Create live data val for upcoming elections
    private val _upcoming = MutableLiveData<List<Election>>()
    val upcoming: LiveData<List<Election>>
        get() = _upcoming

    //TODO: Create live data val for saved elections
    private val _saved = MutableLiveData<List<Election>>()
    val saved: LiveData<List<Election>>
        get() = _saved

    private val _navigateToElectionDetail = MutableLiveData<Election>()
    val navigateToElectionDetail
        get() = _navigateToElectionDetail



    //TODO: Create val and functions to populate live data for upcoming elections from the API and saved elections from local database
    fun fetchUpcoming() {
        viewModelScope.launch{
            try {
                _upcoming.value = CivicsApi.retrofitService.getElections(BuildConfig.API_KEY).elections
            } catch (e: Exception) {
                Log.e(TAG, "Upcoming Election fetch error $e")
            }
        }
    }

    fun fetchSaved() {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                _saved.postValue(database.getAllElection())
            } catch(e: Exception) {
                Log.e(TAG, "Saved Election fetch error $e")

                _saved.value = ArrayList()
            }
        }
    }

    //TODO: Create functions to navigate to saved or upcoming election voter info
    fun onElectionClick(election: Election) {
        _navigateToElectionDetail.value = election
    }

    fun onElectionDetailNavigated() {
        _navigateToElectionDetail.value = null
    }

}