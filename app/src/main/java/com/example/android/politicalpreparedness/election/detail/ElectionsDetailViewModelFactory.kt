package com.example.android.politicalpreparedness.election.detail

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.android.politicalpreparedness.database.ElectionDao

class ElectionsDetailViewModelFactory(val dao: ElectionDao, val app: Application): ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ElectionsDetailViewModel::class.java)) {
            @Suppress("uncheck_cast")
            return ElectionsDetailViewModel(dao, app) as T
        }
        throw IllegalArgumentException("Error creating ElectionsDetailViewModel")
    }
}