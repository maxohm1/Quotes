package max.ohm.quoteapp.domain.repository

import kotlinx.coroutines.flow.Flow
import max.ohm.quoteapp.domain.model.Quote
import max.ohm.quoteapp.domain.model.QuoteCategory
import max.ohm.quoteapp.util.Resource

interface QuoteRepository {
    fun getAllQuotes(): Flow<List<Quote>>
    fun getQuotesByCategory(category: QuoteCategory): Flow<List<Quote>>
    fun getFavoriteQuotes(): Flow<List<Quote>>
    fun searchQuotes(query: String): Flow<List<Quote>>
    fun getQuotesByAuthor(author: String): Flow<List<Quote>>
    suspend fun getQuoteById(id: String): Quote?
    suspend fun refreshQuotes(): Resource<Unit>
    suspend fun toggleFavorite(quoteId: String, isFavorite: Boolean): Resource<Unit>
    suspend fun getDailyQuote(): Resource<Quote>
    suspend fun getRandomQuote(): Quote?
}
