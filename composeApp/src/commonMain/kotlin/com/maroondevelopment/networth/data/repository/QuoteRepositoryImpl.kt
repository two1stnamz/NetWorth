package com.maroondevelopment.networth.data.repository

import com.maroondevelopment.networth.domain.entity.Quote
import com.maroondevelopment.networth.domain.repository.QuoteRepository
import com.maroondevelopment.networth.network.HttpFactory
import io.ktor.client.request.get
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
import io.ktor.http.isSuccess
import kotlinx.serialization.json.Json

class QuoteRepositoryImpl : QuoteRepository {

    override suspend fun getQuote(symbol: String): Quote {
        val url = "https://financialmodelingprep.com/stable/quote-short?symbol=$symbol&apikey=9k3snnJt033yf5LsxiclwhxK5TKNhwZz"
        val response: HttpResponse = HttpFactory.httpClient.get(url)

        // 2. Check for success status (200-299)
        if (response.status.isSuccess()) {
            return Json.decodeFromString<Quote>(response.bodyAsText())
        } else {
            return Quote(
                symbol = symbol,
                price = 0.0,
                change = 0.0,
                volume = 0
            )
        }
    }
}