package com.maroondevelopment.networth.data.datasource

import com.maroondevelopment.networth.Database
import com.maroondevelopment.networth.data.QuoteDto
import com.maroondevelopment.networth.network.HttpFactory
import io.ktor.client.request.get
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
import io.ktor.http.isSuccess
import kotlinx.serialization.json.Json

interface LocalQuoteDataSource {

    suspend fun getQuote(symbol: String): QuoteDto?

    suspend fun storeQuote(quote: QuoteDto)
}

interface RemoteQuoteDataSource {

    suspend fun getQuote(symbol: String): QuoteDto?
}

class LocalQuoteDataSourceImpl(
    private val database: Database
) : LocalQuoteDataSource {

    override suspend fun getQuote(symbol: String): QuoteDto? {
        var quote: QuoteDto? = null

        try {
            database.quoteQueries.selectBySymbol(symbol).executeAsOne().apply {
                quote = QuoteDto(
                    ticker = this.symbol,
                    price = this.price
                )
            }
        } catch (t: Throwable) {
            println("[BENG][LocalQuoteDataSource] getQuote() - EXCEPTION!!! ${t.message}")
        }

        return quote
    }

    override suspend fun storeQuote(quote: QuoteDto) {
        try {
            database.quoteQueries.deleteBySymbol(quote.ticker)
            database.quoteQueries.insert(quote.ticker, quote.price)
        } catch (t: Throwable) {
            println("[BENG][LocalQuoteDataSource] storeQuote() - EXCEPTION!!! ${t.message}")
        }
    }
}

class RemoteQuoteDataSourceImpl : RemoteQuoteDataSource {

    private val json = Json {
        ignoreUnknownKeys = true
    }

    override suspend fun getQuote(symbol: String): QuoteDto? {
        try {
            val url = "https://stockinvest.us/api/v1/predictions/$symbol"
            val response: HttpResponse = HttpFactory.httpClient.get(url)

            // 2. Check for success status (200-299)
            if (response.status.isSuccess()) {
                return json.decodeFromString<QuoteDto>(response.bodyAsText())
            } else {
                throw Exception("Failed to fetch quote: ${response.status}. Status code: ${response.status.value}, Response: ${response.bodyAsText()}")
            }
        } catch (t: Throwable) {
            println("[BENG][RemoteQuoteDataSource] getQuote() - EXCEPTION!!! ${t.message}")
            return null
        }
    }
}