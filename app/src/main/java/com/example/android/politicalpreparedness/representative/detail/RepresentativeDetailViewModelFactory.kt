package com.example.android.politicalpreparedness.representative.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class RepresentativeDetailViewModelFactory(): ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(RepresentativeDetailViewModel::class.java)) {
            @Suppress("uncheck_cast")
            return RepresentativeDetailViewModel() as T
        }
        throw IllegalArgumentException("Error creating RepresentativeDetailViewModel")
    }
}