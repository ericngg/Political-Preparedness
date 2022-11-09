package com.example.android.politicalpreparedness.representative

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.android.politicalpreparedness.database.ElectionDao
import com.example.android.politicalpreparedness.election.ElectionsViewModel

//TODO: Create Factory to generate ElectionViewModel with provided election datasource
class RepresentativeViewModelFactory(): ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(RepresentativeViewModel::class.java)) {
            @Suppress("uncheck_cast")
            return RepresentativeViewModel() as T
        }
        throw IllegalArgumentException("Error creating RepresentativeViewModel")
    }
}