package com.maroondevelopment.networth.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.maroondevelopment.networth.domain.entity.Portfolio
import com.maroondevelopment.networth.domain.usecase.FetchPortfolioUseCase
import com.maroondevelopment.networth.domain.usecase.RefreshPortfolioUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed interface SnapshotUiState {
    class Success(val portfolio: Portfolio) : SnapshotUiState
    class Error(val message: String) : SnapshotUiState
    object Loading : SnapshotUiState
}

class SnapshotViewModel(
    private val fetchPortfolioUseCase: FetchPortfolioUseCase,
    private val refreshPortfolioUseCase: RefreshPortfolioUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow<SnapshotUiState>(SnapshotUiState.Loading)

    val uiState = _uiState.asStateFlow()

    fun fetch() {
        viewModelScope.launch {
            val portfolio = fetchPortfolioUseCase()
            _uiState.value = SnapshotUiState.Success(portfolio)
        }
    }

    fun refresh() {
        viewModelScope.launch {
            _uiState.value = SnapshotUiState.Loading

            val portfolio = refreshPortfolioUseCase()
            _uiState.value = SnapshotUiState.Success(portfolio)
        }
    }
}