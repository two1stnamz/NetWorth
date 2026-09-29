package com.maroondevelopment.networth.di

import com.maroondevelopment.networth.data.datasource.LocalQuoteDataSource
import com.maroondevelopment.networth.data.datasource.LocalQuoteDataSourceImpl
import com.maroondevelopment.networth.data.datasource.RemoteQuoteDataSource
import com.maroondevelopment.networth.data.datasource.RemoteQuoteDataSourceImpl
import com.maroondevelopment.networth.data.repository.AccountRepositoryImpl
import com.maroondevelopment.networth.data.repository.HoldingsRepositoryImpl
import com.maroondevelopment.networth.data.repository.PositionRepositoryImpl
import com.maroondevelopment.networth.data.repository.QuoteRepositoryImpl
import com.maroondevelopment.networth.domain.repository.AccountRepository
import com.maroondevelopment.networth.domain.repository.HoldingsRepository
import com.maroondevelopment.networth.domain.repository.PositionRepository
import com.maroondevelopment.networth.domain.repository.QuoteRepository
import com.maroondevelopment.networth.domain.usecase.AddPositionToAccountUseCase
import com.maroondevelopment.networth.domain.usecase.AddPositionToAccountUseCaseImpl
import com.maroondevelopment.networth.domain.usecase.CreateAccountUseCase
import com.maroondevelopment.networth.domain.usecase.CreateAccountUseCaseImpl
import com.maroondevelopment.networth.domain.usecase.FetchPortfolioUseCase
import com.maroondevelopment.networth.domain.usecase.LoadAccountSnapshotUseCase
import com.maroondevelopment.networth.domain.usecase.LoadAccountSnapshotUseCaseImpl
import com.maroondevelopment.networth.domain.usecase.LoadPortfolioUseCase
import com.maroondevelopment.networth.domain.usecase.LoadPortfolioUseCaseImpl
import com.maroondevelopment.networth.persistence.DriverFactory
import com.maroondevelopment.networth.persistence.createDatabase
import com.maroondevelopment.networth.presentation.SnapshotViewModel
import com.maroondevelopment.networth.presentation.portfolio.PortfolioViewModel
import org.koin.core.module.Module
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

/**
 * The single Koin module for the shared layer.
 *
 * [driverFactory] is supplied by each platform entry point because its constructor differs
 * per target (Android needs a [android.content.Context], iOS needs nothing).
 */
fun appModule(driverFactory: DriverFactory): Module = module {

    // Persistence
    single { createDatabase(driverFactory) }

    // Data sources
    single<LocalQuoteDataSource> { LocalQuoteDataSourceImpl(get()) }
    single<RemoteQuoteDataSource> { RemoteQuoteDataSourceImpl() }

    // Repositories
    single<QuoteRepository> { QuoteRepositoryImpl(remoteDataSource = get(), localDataSource = get()) }
    single<HoldingsRepository> { HoldingsRepositoryImpl() }
    single<AccountRepository> { AccountRepositoryImpl() }
    single<PositionRepository> { PositionRepositoryImpl(get()) }

    // Use cases
    factory { FetchPortfolioUseCase(get(), get()) }
    factory<LoadAccountSnapshotUseCase> { LoadAccountSnapshotUseCaseImpl(get(), get(), get()) }
    factory<LoadPortfolioUseCase> { LoadPortfolioUseCaseImpl(get(), get()) }
    factory<CreateAccountUseCase> { CreateAccountUseCaseImpl(get()) }
    factory<AddPositionToAccountUseCase> { AddPositionToAccountUseCaseImpl(get()) }

    // View models
    viewModelOf(::PortfolioViewModel)
    viewModelOf(::SnapshotViewModel)
}
