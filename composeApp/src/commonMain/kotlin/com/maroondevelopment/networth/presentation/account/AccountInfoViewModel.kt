package com.maroondevelopment.networth.presentation.account

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.maroondevelopment.networth.domain.entity.AddPositionOutcome
import com.maroondevelopment.networth.domain.entity.CachePolicy
import com.maroondevelopment.networth.domain.entity.v2.DeletePositionOutcome
import com.maroondevelopment.networth.domain.entity.v2.LoadAccountSnapshotOutcome
import com.maroondevelopment.networth.domain.entity.v2.NewPosition
import com.maroondevelopment.networth.domain.entity.v2.UpdatePositionOutcome
import com.maroondevelopment.networth.domain.usecase.AddPositionToAccountUseCase
import com.maroondevelopment.networth.domain.usecase.DeletePositionFromAccountUseCase
import com.maroondevelopment.networth.domain.usecase.LoadAccountSnapshotUseCase
import com.maroondevelopment.networth.domain.usecase.UpdatePositionUnitsUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * Drives the account screen: the position list plus its add / edit / delete / refresh actions.
 *
 * Every mutation use case hands back the account's refreshed snapshot, so state only ever comes
 * from one place and the view model never patches its own list.
 */
class AccountInfoViewModel(
    private val accountId: Long,
    private val loadAccountSnapshot: LoadAccountSnapshotUseCase,
    private val addPositionToAccount: AddPositionToAccountUseCase,
    private val updatePositionUnits: UpdatePositionUnitsUseCase,
    private val deletePositionFromAccount: DeletePositionFromAccountUseCase
) : ViewModel() {

    private val _flow = MutableStateFlow<AccountInfoUiState>(AccountInfoUiState.Loading)

    val flow = _flow.asStateFlow()

    /** Loads from cache where possible — cheap enough to call on every entry to the screen. */
    fun load() = internalLoad(CachePolicy.PREFER_CACHE)

    /** Re-fetches every quote so each position is re-valued against the latest price. */
    fun refresh() = internalLoad(CachePolicy.REFRESH)

    fun addPosition(ticker: String, units: String) {
        val parsedUnits = units.toUnitsOrNull()

        if (parsedUnits == null) {
            showMessage(INVALID_UNITS_MESSAGE)
            return
        }

        viewModelScope.launch {
            val outcome = addPositionToAccount(
                accountId = accountId,
                position = NewPosition(ticker = ticker, units = parsedUnits)
            )

            when (outcome) {
                is AddPositionOutcome.Success ->
                    show(outcome.snapshot.toLoadedState(message = "Added ${ticker.trim().uppercase()}"))
                AddPositionOutcome.Failure.InvalidInput -> showMessage("Enter a ticker and a number of units greater than zero")
                AddPositionOutcome.Failure.DuplicateTicker -> showMessage("This account already holds ${ticker.trim().uppercase()} — edit it instead")
                AddPositionOutcome.Failure.NoQuote -> showMessage("No quote found for ${ticker.trim().uppercase()}")
                is AddPositionOutcome.Failure.Technical -> showMessage(COULD_NOT_SAVE_MESSAGE)
            }
        }
    }

    fun updateUnits(ticker: String, units: String) {
        val parsedUnits = units.toUnitsOrNull()

        if (parsedUnits == null) {
            showMessage(INVALID_UNITS_MESSAGE)
            return
        }

        viewModelScope.launch {
            when (val outcome = updatePositionUnits(accountId, ticker, parsedUnits)) {
                is UpdatePositionOutcome.Success ->
                    show(outcome.snapshot.toLoadedState(message = "Updated $ticker"))
                UpdatePositionOutcome.Failure.InvalidInput -> showMessage("Units must be greater than zero")
                UpdatePositionOutcome.Failure.NotFound -> showMessage("$ticker is no longer in this account")
                is UpdatePositionOutcome.Failure.Technical -> showMessage(COULD_NOT_SAVE_MESSAGE)
            }
        }
    }

    fun deletePosition(ticker: String) {
        viewModelScope.launch {
            when (val outcome = deletePositionFromAccount(accountId, ticker)) {
                is DeletePositionOutcome.Success ->
                    show(outcome.snapshot.toLoadedState(message = "Deleted $ticker"))
                DeletePositionOutcome.Failure.NotFound -> showMessage("$ticker is no longer in this account")
                is DeletePositionOutcome.Failure.Technical -> showMessage("Could not delete $ticker")
            }
        }
    }

    /** Clears feedback once the view has shown it, so it is not replayed on recomposition. */
    fun messageShown() {
        val loaded = _flow.value as? AccountInfoUiState.Loaded ?: return
        _flow.value = loaded.copy(message = null)
    }

    private fun internalLoad(policy: CachePolicy) {
        val loaded = _flow.value as? AccountInfoUiState.Loaded

        // Keep the list on screen while refreshing; only a cold load shows the spinner.
        _flow.value = loaded?.copy(isRefreshing = true, message = null) ?: AccountInfoUiState.Loading

        viewModelScope.launch {
            when (val outcome = loadAccountSnapshot(accountId, policy)) {
                is LoadAccountSnapshotOutcome.Success -> show(outcome.snapshot.toLoadedState())
                is LoadAccountSnapshotOutcome.Failure -> _flow.value = AccountInfoUiState.Error
            }
        }
    }

    private fun show(state: AccountInfoUiState.Loaded) {
        _flow.value = state
    }

    private fun showMessage(message: String) {
        val loaded = _flow.value as? AccountInfoUiState.Loaded ?: return
        _flow.value = loaded.copy(isRefreshing = false, message = message)
    }

    private fun String.toUnitsOrNull(): Double? = trim().toDoubleOrNull()

    private companion object {
        const val INVALID_UNITS_MESSAGE = "Enter a valid number of units"
        const val COULD_NOT_SAVE_MESSAGE = "Could not save the change"
    }
}
