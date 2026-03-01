package com.maroondevelopment.networth.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.maroondevelopment.networth.domain.entity.Portfolio
import com.maroondevelopment.networth.domain.usecase.FetchPortfolioUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed interface SnapshotUiState {
    data class Success(val portfolio: Portfolio) : SnapshotUiState
    data class Error(val message: String) : SnapshotUiState
    object Loading : SnapshotUiState
}

class SnapshotViewModel(
    private val fetchPortfolioUseCase: FetchPortfolioUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow<SnapshotUiState>(SnapshotUiState.Loading)

    val uiState = _uiState.asStateFlow()

    fun fetch() {
        viewModelScope.launch {
            val portfolio = fetchPortfolioUseCase()
            _uiState.value = SnapshotUiState.Success(portfolio)
        }
    }
}