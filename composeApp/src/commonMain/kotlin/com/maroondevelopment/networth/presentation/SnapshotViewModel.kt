package com.maroondevelopment.networth.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.maroondevelopment.networth.domain.entity.CachePolicy
import com.maroondevelopment.networth.domain.entity.Portfolio
import com.maroondevelopment.networth.domain.usecase.FetchPortfolioUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed interface SnapshotUiState {
    class Success(val portfolio: Portfolio) : SnapshotUiState
    class Error(val message: String) : SnapshotUiState
    object Loading : SnapshotUiState
}

class SnapshotViewModel(
    private val fetchPortfolioUseCase: FetchPortfolioUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow<SnapshotUiState>(SnapshotUiState.Loading)

    val uiState = _uiState.asStateFlow()

    fun fetch() {
        viewModelScope.launch {
            val portfolio = fetchPortfolioUseCase(cachePolicy = CachePolicy.PREFER_CACHE)
            _uiState.value = SnapshotUiState.Success(portfolio)
        }
    }

    fun refresh() {
        viewModelScope.launch {
            _uiState.value = SnapshotUiState.Loading

            val portfolio = fetchPortfolioUseCase(cachePolicy = CachePolicy.REFRESH)
            _uiState.value = SnapshotUiState.Success(portfolio)
        }
    }
}