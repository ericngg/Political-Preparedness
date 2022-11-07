package com.example.android.politicalpreparedness.election

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.android.politicalpreparedness.database.ElectionDao

//TODO: Create Factory to generate ElectionViewModel with provided election datasource
class ElectionsViewModelFactory(val dao: ElectionDao, val app: Application): ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ElectionsViewModel::class.java)) {
            @Suppress("uncheck_cast")
            return ElectionsViewModel(dao, app) as T
        }
        throw IllegalArgumentException("Error creating ElectionsViewModel")
    }
}