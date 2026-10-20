package com.maroondevelopment.networth.presentation.portfolio

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.maroondevelopment.networth.domain.entity.CachePolicy
import com.maroondevelopment.networth.domain.entity.LoadPortfolioOutcome
import com.maroondevelopment.networth.domain.entity.v2.CreateInstitutionOutcome
import com.maroondevelopment.networth.domain.entity.v2.DeleteInstitutionOutcome
import com.maroondevelopment.networth.domain.entity.v2.RenameInstitutionOutcome
import com.maroondevelopment.networth.domain.usecase.CreateInstitutionUseCase
import com.maroondevelopment.networth.domain.usecase.DeleteInstitutionUseCase
import com.maroondevelopment.networth.domain.usecase.LoadPortfolioUseCase
import com.maroondevelopment.networth.domain.usecase.RefreshPortfolioUseCase
import com.maroondevelopment.networth.domain.usecase.RenameInstitutionUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * Drives the portfolio screen: the institution list plus its add / rename / delete / refresh
 * actions.
 *
 * The rename and delete use cases hand back the re-read portfolio, so the list only ever comes
 * from one place and this view model never patches it.
 */
class PortfolioViewModel(
    private val loadPortfolio: LoadPortfolioUseCase,
    private val refreshPortfolio: RefreshPortfolioUseCase,
    private val createInstitution: CreateInstitutionUseCase,
    private val renameInstitutionUseCase: RenameInstitutionUseCase,
    private val deleteInstitutionUseCase: DeleteInstitutionUseCase
) : ViewModel() {

    private val _flow = MutableStateFlow<PortfolioUiState>(PortfolioUiState.Loading)

    val flow = _flow.asStateFlow()

    /** Pulls live quotes across every institution and records the resulting total against today. */
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

    fun createNewInstitution(name: String) {
        viewModelScope.launch {
            when (createInstitution(name)) {
                CreateInstitutionOutcome.Error -> showMessage(COULD_NOT_SAVE_MESSAGE)
                // Reload so the new institution appears without restarting the app.
                CreateInstitutionOutcome.Success -> show(loadPortfolio(CachePolicy.PREFER_CACHE))
            }
        }
    }

    fun renameInstitution(institutionId: Long, name: String) {
        viewModelScope.launch {
            when (val outcome = renameInstitutionUseCase(institutionId, name)) {
                is RenameInstitutionOutcome.Success ->
                    show(outcome.portfolio.toSuccessState(message = "Renamed to ${name.trim()}"))
                RenameInstitutionOutcome.Failure.InvalidInput -> showMessage(BLANK_NAME_MESSAGE)
                RenameInstitutionOutcome.Failure.NotFound -> showMessage(GONE_MESSAGE)
                is RenameInstitutionOutcome.Failure.Technical -> showMessage(COULD_NOT_SAVE_MESSAGE)
            }
        }
    }

    fun deleteInstitution(institutionId: Long, name: String) {
        viewModelScope.launch {
            when (val outcome = deleteInstitutionUseCase(institutionId)) {
                is DeleteInstitutionOutcome.Success ->
                    show(outcome.portfolio.toSuccessState(message = "Deleted $name"))
                DeleteInstitutionOutcome.Failure.NotFound -> showMessage(GONE_MESSAGE)
                is DeleteInstitutionOutcome.Failure.Technical -> showMessage("Could not delete $name")
            }
        }
    }

    /** Clears feedback once the view has shown it, so it is not replayed on recomposition. */
    fun messageShown() {
        val success = _flow.value as? PortfolioUiState.Success ?: return
        _flow.value = success.copy(message = null)
    }

    private fun show(state: PortfolioUiState.Success) {
        _flow.value = state
    }

    private fun show(outcome: LoadPortfolioOutcome) {
        _flow.value = when (outcome) {
            LoadPortfolioOutcome.Error -> PortfolioUiState.Error
            is LoadPortfolioOutcome.Success -> outcome.portfolio.toSuccessState()
        }
    }

    private fun showMessage(message: String) {
        val success = _flow.value as? PortfolioUiState.Success ?: return
        _flow.value = success.copy(message = message)
    }

    private companion object {
        const val BLANK_NAME_MESSAGE = "Enter a name"
        const val GONE_MESSAGE = "That institution no longer exists"
        const val COULD_NOT_SAVE_MESSAGE = "Could not save the change"
    }
}
