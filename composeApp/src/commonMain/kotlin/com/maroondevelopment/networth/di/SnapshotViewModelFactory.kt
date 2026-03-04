package com.maroondevelopment.networth.di

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.CreationExtras
import com.maroondevelopment.networth.presentation.SnapshotViewModel
import kotlin.reflect.KClass

class SnapshotViewModelFactory : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: KClass<T>, extras: CreationExtras): T {
        return SnapshotViewModel(Factory.fetchPortfolioUseCase()) as T
    }
}
