package com.maroondevelopment.networth.presentation.history

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.maroondevelopment.networth.domain.entity.v2.LoadPortfolioHistoryOutcome
import com.maroondevelopment.networth.domain.usecase.LoadPortfolioHistoryUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class PortfolioHistoryViewModel(
    private val loadPortfolioHistory: LoadPortfolioHistoryUseCase
) : ViewModel() {

    private val _flow = MutableStateFlow<PortfolioHistoryUiState>(PortfolioHistoryUiState.Loading)

    val flow = _flow.asStateFlow()

    /** History is read straight from the local table; there is nothing to fetch remotely. */
    fun load() {
        viewModelScope.launch {
            _flow.value = when (val outcome = loadPortfolioHistory()) {
                LoadPortfolioHistoryOutcome.Error -> PortfolioHistoryUiState.Error
                is LoadPortfolioHistoryOutcome.Success ->
                    if (outcome.history.points.isEmpty()) {
                        PortfolioHistoryUiState.Empty
                    } else {
                        PortfolioHistoryUiState.Loaded(outcome.history.toUiModel())
                    }
            }
        }
    }
}
