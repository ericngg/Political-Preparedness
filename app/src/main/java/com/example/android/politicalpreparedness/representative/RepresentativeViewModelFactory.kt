package com.example.android.politicalpreparedness.representative

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class RepresentativeViewModelFactory: ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(RepresentativeViewModel::class.java)) {
            @Suppress("uncheck_cast")
            return RepresentativeViewModel() as T
        }
        throw IllegalArgumentException("Error creating RepresentativeViewModel")
    }
}