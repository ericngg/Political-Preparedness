package com.example.android.politicalpreparedness.election

import android.app.Application
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.android.politicalpreparedness.BuildConfig
import com.example.android.politicalpreparedness.database.ElectionDao
import com.example.android.politicalpreparedness.network.CivicsApi
import com.example.android.politicalpreparedness.network.models.Election
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ElectionsViewModel(val dao: ElectionDao, val app: Application): ViewModel() {

    private val TAG = "ElectionViewModel"

    private val _upcoming = MutableLiveData<List<Election>>()
    val upcoming: LiveData<List<Election>>
        get() = _upcoming

    private val _saved = MutableLiveData<List<Election>>()
    val saved: LiveData<List<Election>>
        get() = _saved

    private val _navigateToElectionDetail = MutableLiveData<Election>()
    val navigateToElectionDetail
        get() = _navigateToElectionDetail

    /**
     *  Calls the API for upcoming elections
     *
     **/
    fun fetchUpcoming() {
        viewModelScope.launch{
            try {
                _upcoming.value = CivicsApi.retrofitService.getElections(BuildConfig.API_KEY).elections
            } catch (e: Exception) {
                Log.e(TAG, "Upcoming Election fetch error $e")
            }
        }
    }

    /**
     *  Calls the database for saved elections
     *
     *  If an exception is catched due to NPE, it creates an empty ArrayList
     **/
    fun fetchSaved() {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                _saved.postValue(dao.getAllElection())
            } catch(e: Exception) {
                Log.e(TAG, "Saved Election fetch error $e")
                _saved.value = ArrayList()
            }
        }
    }

    /**
     *  Sets the election clicked value (Before navigation)
     *
     **/
    fun onElectionClick(election: Election) {
        _navigateToElectionDetail.value = election
    }

    /**
     *  Clears the election value (After navigation)
     *
     **/
    fun onElectionDetailNavigated() {
        _navigateToElectionDetail.value = null
    }
}