package com.maroondevelopment.networth.presentation.institution

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.maroondevelopment.networth.domain.entity.CachePolicy
import com.maroondevelopment.networth.domain.entity.v2.CreateAccountOutcome
import com.maroondevelopment.networth.domain.entity.v2.DeleteAccountOutcome
import com.maroondevelopment.networth.domain.entity.v2.LoadInstitutionSnapshotOutcome
import com.maroondevelopment.networth.domain.entity.v2.RenameAccountOutcome
import com.maroondevelopment.networth.domain.usecase.CreateAccountUseCase
import com.maroondevelopment.networth.domain.usecase.DeleteAccountUseCase
import com.maroondevelopment.networth.domain.usecase.LoadInstitutionSnapshotUseCase
import com.maroondevelopment.networth.domain.usecase.RenameAccountUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * Drives the institution screen: the account list plus its add / rename / delete / refresh
 * actions.
 *
 * The rename and delete use cases hand back the institution's re-read snapshot, so the list only
 * ever comes from one place and this view model never patches it.
 */
class InstitutionViewModel(
    private val institutionId: Long,
    private val loadInstitutionSnapshot: LoadInstitutionSnapshotUseCase,
    private val createAccount: CreateAccountUseCase,
    private val renameAccountUseCase: RenameAccountUseCase,
    private val deleteAccountUseCase: DeleteAccountUseCase
) : ViewModel() {

    private val _flow = MutableStateFlow<InstitutionUiState>(InstitutionUiState.Loading)

    val flow = _flow.asStateFlow()

    /** Reads from the local cache, falling back to the server for anything not cached yet. */
    fun load() = internalLoad(CachePolicy.PREFER_CACHE)

    /** Re-values every account at this institution against live quotes. */
    fun refresh() = internalLoad(CachePolicy.REFRESH)

    fun createNewAccount(name: String) {
        viewModelScope.launch {
            when (createAccount(institutionId, name)) {
                CreateAccountOutcome.Error -> showMessage(COULD_NOT_SAVE_MESSAGE)
                // Reload so the new account appears without restarting the app.
                CreateAccountOutcome.Success -> internalLoad(CachePolicy.PREFER_CACHE)
            }
        }
    }

    fun renameAccount(accountId: Long, name: String) {
        viewModelScope.launch {
            when (val outcome = renameAccountUseCase(accountId, name)) {
                is RenameAccountOutcome.Success ->
                    show(outcome.snapshot.toLoadedState(message = "Renamed to ${name.trim()}"))
                RenameAccountOutcome.Failure.InvalidInput -> showMessage(BLANK_NAME_MESSAGE)
                RenameAccountOutcome.Failure.NotFound -> showMessage(GONE_MESSAGE)
                is RenameAccountOutcome.Failure.Technical -> showMessage(COULD_NOT_SAVE_MESSAGE)
            }
        }
    }

    fun deleteAccount(accountId: Long, name: String) {
        viewModelScope.launch {
            when (val outcome = deleteAccountUseCase(accountId)) {
                is DeleteAccountOutcome.Success ->
                    show(outcome.snapshot.toLoadedState(message = "Deleted $name"))
                DeleteAccountOutcome.Failure.NotFound -> showMessage(GONE_MESSAGE)
                is DeleteAccountOutcome.Failure.Technical -> showMessage("Could not delete $name")
            }
        }
    }

    /** Clears feedback once the view has shown it, so it is not replayed on recomposition. */
    fun messageShown() {
        val loaded = _flow.value as? InstitutionUiState.Loaded ?: return
        _flow.value = loaded.copy(message = null)
    }

    private fun internalLoad(policy: CachePolicy) {
        val loaded = _flow.value as? InstitutionUiState.Loaded

        // Keep the list on screen while refreshing; only a cold load shows the spinner.
        _flow.value = loaded?.copy(isRefreshing = true, message = null) ?: InstitutionUiState.Loading

        viewModelScope.launch {
            _flow.value = when (val outcome = loadInstitutionSnapshot(institutionId, policy)) {
                is LoadInstitutionSnapshotOutcome.Success -> outcome.snapshot.toLoadedState()
                is LoadInstitutionSnapshotOutcome.Failure -> InstitutionUiState.Error
            }
        }
    }

    private fun show(state: InstitutionUiState.Loaded) {
        _flow.value = state
    }

    private fun showMessage(message: String) {
        val loaded = _flow.value as? InstitutionUiState.Loaded ?: return
        _flow.value = loaded.copy(isRefreshing = false, message = message)
    }

    private companion object {
        const val BLANK_NAME_MESSAGE = "Enter a name"
        const val GONE_MESSAGE = "That account no longer exists"
        const val COULD_NOT_SAVE_MESSAGE = "Could not save the change"
    }
}
