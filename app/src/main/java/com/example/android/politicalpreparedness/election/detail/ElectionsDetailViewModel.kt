package com.example.android.politicalpreparedness.election.detail

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
import com.example.android.politicalpreparedness.network.models.VoterInfoResponse
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ElectionsDetailViewModel(val dao: ElectionDao, val app: Application): ViewModel() {

    private lateinit var election: Election
    private val TAG = "ElectionsDetailViewModel"

    private var _following = MutableLiveData<Boolean>()
    val following: LiveData<Boolean>
        get() = _following

    private var _data = MutableLiveData<VoterInfoResponse>()
    val data: LiveData<VoterInfoResponse>
        get() = _data

    private var _votingUrl = MutableLiveData<String>()
    val votingUrl: LiveData<String>
        get() = _votingUrl

    private var _ballotUrl = MutableLiveData<String>()
    val ballotUrl: LiveData<String>
        get() = _ballotUrl

    fun setElection(election: Election) {
        this.election = election
    }

    fun fetch() {
        val division = election.division
        val address = "${division.state}, US"

        // Test data
        val testAddress = "Washington, US"
        val testElectionId = 2000

        viewModelScope.launch {
            try {
                // Test call
                _data.value = CivicsApi.retrofitService.getVoterInfo(BuildConfig.API_KEY, testAddress, testElectionId.toLong())

                //_data.value = CivicsApi.retrofitService.getVoterInfo(BuildConfig.API_KEY, address, election.id.toLong())

                Log.i(TAG, data.value?.state?.get(0)?.electionAdministrationBody?.ballotInfoUrl.toString())
                Log.i(TAG, data.value?.state?.get(0)?.electionAdministrationBody?.votingLocationFinderUrl.toString())

                _ballotUrl.value = data.value?.state?.get(0)?.electionAdministrationBody?.ballotInfoUrl.toString()
                _votingUrl.value = data.value?.state?.get(0)?.electionAdministrationBody?.votingLocationFinderUrl.toString()

            } catch (e: Exception) {
                Log.e(TAG, "fetch error $e")
            }
        }
    }

    fun follow() {
        CoroutineScope(Dispatchers.IO).launch {
            dao.insert(election)
            _following.postValue(true)
        }
    }

    fun unfollow() {
        CoroutineScope(Dispatchers.IO).launch {
            dao.delete(election)
            _following.postValue(false)
        }
    }

    fun isFollowing() {
        CoroutineScope(Dispatchers.IO).launch {
            val daoElection = dao.get(election.id.toLong())

            if (daoElection == null) {
                _following.postValue(false)
            } else {
                _following.postValue(true)
            }
        }
    }

}