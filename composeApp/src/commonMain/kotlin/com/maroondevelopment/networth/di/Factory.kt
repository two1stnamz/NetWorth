package com.maroondevelopment.networth.di

import com.maroondevelopment.networth.Database
import com.maroondevelopment.networth.data.repository.HoldingsRepositoryImpl
import com.maroondevelopment.networth.data.repository.QuoteRepositoryImpl
import com.maroondevelopment.networth.domain.repository.HoldingsRepository
import com.maroondevelopment.networth.domain.repository.QuoteRepository
import com.maroondevelopment.networth.domain.usecase.FetchAssetsUseCase
import com.maroondevelopment.networth.persistence.DriverFactory
import com.maroondevelopment.networth.persistence.createDatabase
import com.maroondevelopment.networth.presentation.SnapshotViewModel

object Factory {

    private lateinit var database: Database

    fun initialize(driverFactory: DriverFactory) {
        this.database = createDatabase(driverFactory)
    }

    fun snapshotViewModel() = SnapshotViewModel(
        fetchAssetsUseCase = fetchAssetsUseCase()
    )

    private fun fetchAssetsUseCase(): FetchAssetsUseCase =
        FetchAssetsUseCase(holdingsRepository(), quoteRepository())

    private fun holdingsRepository(): HoldingsRepository = HoldingsRepositoryImpl()

    private fun quoteRepository(): QuoteRepository = QuoteRepositoryImpl()
}