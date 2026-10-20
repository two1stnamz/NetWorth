package com.maroondevelopment.networth.presentation.portfolio

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.maroondevelopment.networth.domain.entity.CachePolicy
import com.maroondevelopment.networth.domain.entity.LoadPortfolioOutcome
import com.maroondevelopment.networth.domain.entity.v2.CreateAccountOutcome
import com.maroondevelopment.networth.domain.usecase.CreateAccountUseCase
import com.maroondevelopment.networth.domain.usecase.LoadPortfolioUseCase
import com.maroondevelopment.networth.domain.usecase.RefreshPortfolioUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class PortfolioViewModel(
    private val loadPortfolio: LoadPortfolioUseCase,
    private val refreshPortfolio: RefreshPortfolioUseCase,
    private val createAccount: CreateAccountUseCase
) : ViewModel() {

    private val _flow = MutableStateFlow<PortfolioUiState>(PortfolioUiState.Loading)

    val flow = _flow.asStateFlow()

    /** Pulls live quotes and records the resulting total against today. */
    fun refresh() {
        viewModelScope.launch {
            show(refreshPortfolio())
        }
    }

    /** Reads from the local cache, falling back to the server for anything not cached yet. */
    fun load() {
        viewModelScope.launch {
            show(loadPortfolio(CachePolicy.PREFER_CACHE))
        }
    }

    fun createNewAccount(name: String) {
        viewModelScope.launch {
            when (createAccount(name)) {
                CreateAccountOutcome.Error -> {}
                // Reload so the new account appears without restarting the app.
                CreateAccountOutcome.Success -> show(loadPortfolio(CachePolicy.PREFER_CACHE))
            }
        }
    }

    private fun show(outcome: LoadPortfolioOutcome) {
        _flow.value = when (outcome) {
            LoadPortfolioOutcome.Error -> PortfolioUiState.Error
            is LoadPortfolioOutcome.Success -> PortfolioUiState.Success(outcome.portfolio.toUiModel())
        }
    }
}
