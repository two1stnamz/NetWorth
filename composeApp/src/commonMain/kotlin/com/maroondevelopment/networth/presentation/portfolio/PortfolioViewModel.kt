package com.maroondevelopment.networth.presentation.portfolio

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.maroondevelopment.networth.domain.entity.CachePolicy
import com.maroondevelopment.networth.domain.usecase.LoadPortfolioUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class PortfolioViewModel(
    private val loadPortfolio: LoadPortfolioUseCase
) : ViewModel() {

    private val _flow = MutableStateFlow<PortfolioUiState>(PortfolioUiState.Loading)

    val flow = _flow.asStateFlow()

    fun refresh() {
        internalLoad(CachePolicy.REFRESH)
    }

    fun load() {
        internalLoad(CachePolicy.PREFER_CACHE)
    }

    private fun internalLoad(cachePolicy: CachePolicy) {
        viewModelScope.launch {
            val snapshots = loadPortfolio(cachePolicy)
        }
    }
}