package com.maroondevelopment.networth.di

import com.maroondevelopment.networth.Database
import com.maroondevelopment.networth.data.datasource.LocalQuoteDataSourceImpl
import com.maroondevelopment.networth.data.datasource.RemoteQuoteDataSourceImpl
import com.maroondevelopment.networth.data.repository.HoldingsRepositoryImpl
import com.maroondevelopment.networth.data.repository.QuoteRepositoryImpl
import com.maroondevelopment.networth.domain.repository.HoldingsRepository
import com.maroondevelopment.networth.domain.repository.QuoteRepository
import com.maroondevelopment.networth.domain.usecase.FetchPortfolioUseCase
import com.maroondevelopment.networth.persistence.DriverFactory
import com.maroondevelopment.networth.persistence.createDatabase

object Factory {

    private lateinit var database: Database

    fun initialize(driverFactory: DriverFactory) {
        this.database = createDatabase(driverFactory)
    }

    fun fetchPortfolioUseCase(): FetchPortfolioUseCase =
        FetchPortfolioUseCase(holdingsRepository(), quoteRepository())

    private fun holdingsRepository(): HoldingsRepository = HoldingsRepositoryImpl()

    private fun quoteRepository(): QuoteRepository =
        QuoteRepositoryImpl(
            remoteDataSource = RemoteQuoteDataSourceImpl(),
            localDataSource = LocalQuoteDataSourceImpl(database)
        )
}