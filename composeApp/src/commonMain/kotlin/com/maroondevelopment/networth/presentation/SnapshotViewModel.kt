package com.maroondevelopment.networth.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.maroondevelopment.networth.domain.usecase.FetchAssetsUseCase
import kotlinx.coroutines.launch

class SnapshotViewModel(
    private val fetchAssetsUseCase: FetchAssetsUseCase
) : ViewModel() {

    fun fetch() {
        viewModelScope.launch {
            val assets = fetchAssetsUseCase()
        }
    }
}