package com.maroondevelopment.networth.presentation.portfolio

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.maroondevelopment.networth.domain.entity.CachePolicy
import com.maroondevelopment.networth.domain.entity.LoadPortfolioOutcome
import com.maroondevelopment.networth.domain.entity.v2.CreateAccountOutcome
import com.maroondevelopment.networth.domain.usecase.CreateAccountUseCase
import com.maroondevelopment.networth.domain.usecase.LoadPortfolioUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class PortfolioViewModel(
    private val loadPortfolio: LoadPortfolioUseCase,
    private val createAccount: CreateAccountUseCase
) : ViewModel() {

    private val _flow = MutableStateFlow<PortfolioUiState>(PortfolioUiState.Loading)

    val flow = _flow.asStateFlow()

    fun refresh() {
        internalLoad(CachePolicy.REFRESH)
    }

    fun load() {
        internalLoad(CachePolicy.PREFER_CACHE)
    }

    fun createNewAccount(name: String) {
        viewModelScope.launch {
            when (createAccount(name)) {
                CreateAccountOutcome.Error -> {}
                // Reload so the new account appears without restarting the app.
                CreateAccountOutcome.Success -> internalLoad(CachePolicy.PREFER_CACHE)
            }
        }
    }

    private fun internalLoad(cachePolicy: CachePolicy) {
        viewModelScope.launch {
            when (val outcome = loadPortfolio(cachePolicy)) {
                LoadPortfolioOutcome.Error -> _flow.value = PortfolioUiState.Error
                is LoadPortfolioOutcome.Success ->
                    _flow.value = PortfolioUiState.Success(outcome.portfolio.toUiModel())
            }
        }
    }
}
