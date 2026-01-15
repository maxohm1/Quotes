package max.ohm.quoteapp.domain.repository

import kotlinx.coroutines.flow.Flow
import max.ohm.quoteapp.domain.model.Quote
import max.ohm.quoteapp.domain.model.QuoteCollection
import max.ohm.quoteapp.util.Resource

interface CollectionRepository {
    fun getUserCollections(userId: String): Flow<List<QuoteCollection>>
    fun getQuotesInCollection(collectionId: String): Flow<List<Quote>>
    suspend fun getCollectionById(id: String): QuoteCollection?
    suspend fun createCollection(name: String, description: String): Resource<QuoteCollection>
    suspend fun updateCollection(id: String, name: String, description: String): Resource<Unit>
    suspend fun deleteCollection(id: String): Resource<Unit>
    suspend fun addQuoteToCollection(collectionId: String, quoteId: String): Resource<Unit>
    suspend fun removeQuoteFromCollection(collectionId: String, quoteId: String): Resource<Unit>
    suspend fun syncCollections(): Resource<Unit>
}
