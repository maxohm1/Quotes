package max.ohm.quoteapp.data.repository

import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.postgrest.postgrest
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import max.ohm.quoteapp.data.local.dao.CollectionDao
import max.ohm.quoteapp.data.local.dao.QuoteDao
import max.ohm.quoteapp.data.local.entity.CollectionEntity
import max.ohm.quoteapp.data.local.entity.CollectionQuoteEntity
import max.ohm.quoteapp.data.remote.dto.CollectionDto
import max.ohm.quoteapp.data.remote.dto.CollectionQuoteDto
import max.ohm.quoteapp.data.remote.dto.InsertCollectionDto
import max.ohm.quoteapp.data.remote.dto.InsertCollectionQuoteDto
import max.ohm.quoteapp.domain.model.Quote
import max.ohm.quoteapp.domain.model.QuoteCollection
import max.ohm.quoteapp.domain.repository.CollectionRepository
import max.ohm.quoteapp.util.Constants
import max.ohm.quoteapp.util.DateUtils
import max.ohm.quoteapp.util.Resource
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CollectionRepositoryImpl @Inject constructor(
    private val supabaseClient: SupabaseClient,
    private val collectionDao: CollectionDao,
    private val quoteDao: QuoteDao
) : CollectionRepository {

    override fun getUserCollections(userId: String): Flow<List<QuoteCollection>> {
        return collectionDao.getCollectionsByUser(userId).map { entities ->
            entities.map { entity ->
                val quoteIds = collectionDao.getQuoteIdsInCollection(entity.id).first()
                entity.toDomain(quoteIds)
            }
        }
    }

    override fun getQuotesInCollection(collectionId: String): Flow<List<Quote>> {
        return collectionDao.getQuoteIdsInCollection(collectionId).map { quoteIds ->
            quoteIds.mapNotNull { quoteId ->
                quoteDao.getQuoteById(quoteId)?.toDomain()
            }
        }
    }

    override suspend fun getCollectionById(id: String): QuoteCollection? {
        val entity = collectionDao.getCollectionById(id) ?: return null
        val quoteIds = collectionDao.getQuoteIdsInCollection(id).first()
        return entity.toDomain(quoteIds)
    }

    override suspend fun createCollection(name: String, description: String): Resource<QuoteCollection> {
        return try {
            val currentUser = supabaseClient.auth.currentUserOrNull()
                ?: return Resource.Error("Please sign in to create collections")

            val timestamp = DateUtils.getCurrentIsoTimestamp()
            val collectionId = UUID.randomUUID().toString()

            // Insert to Supabase
            val insertDto = InsertCollectionDto(
                name = name,
                description = description,
                userId = currentUser.id
            )
            
            val result = supabaseClient.postgrest[Constants.TABLE_COLLECTIONS]
                .insert(insertDto) { select() }
                .decodeSingle<CollectionDto>()

            // Save locally
            val entity = CollectionEntity(
                id = result.id,
                name = result.name,
                description = result.description,
                userId = result.userId,
                createdAt = result.createdAt,
                updatedAt = result.updatedAt
            )
            collectionDao.insertCollection(entity)

            Resource.Success(result.toDomain())
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Failed to create collection")
        }
    }

    override suspend fun updateCollection(id: String, name: String, description: String): Resource<Unit> {
        return try {
            val timestamp = DateUtils.getCurrentIsoTimestamp()

            // Update in Supabase
            supabaseClient.postgrest[Constants.TABLE_COLLECTIONS].update({
                set("name", name)
                set("description", description)
                set("updated_at", timestamp)
            }) {
                filter { eq("id", id) }
            }

            // Update locally
            collectionDao.updateCollection(id, name, description, timestamp)

            Resource.Success(Unit)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Failed to update collection")
        }
    }

    override suspend fun deleteCollection(id: String): Resource<Unit> {
        return try {
            // Delete from Supabase
            supabaseClient.postgrest[Constants.TABLE_COLLECTION_QUOTES].delete {
                filter { eq("collection_id", id) }
            }
            supabaseClient.postgrest[Constants.TABLE_COLLECTIONS].delete {
                filter { eq("id", id) }
            }

            // Delete locally
            collectionDao.deleteCollectionWithQuotes(id)

            Resource.Success(Unit)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Failed to delete collection")
        }
    }

    override suspend fun addQuoteToCollection(collectionId: String, quoteId: String): Resource<Unit> {
        return try {
            android.util.Log.d("CollectionRepo", "Adding quote $quoteId to collection $collectionId")
            
            // Try to add to Supabase (may fail due to RLS)
            try {
                supabaseClient.postgrest[Constants.TABLE_COLLECTION_QUOTES].insert(
                    InsertCollectionQuoteDto(
                        collectionId = collectionId,
                        quoteId = quoteId
                    )
                )
                android.util.Log.d("CollectionRepo", "Added to Supabase successfully")
            } catch (e: Exception) {
                // Log but continue - will save locally
                android.util.Log.w("CollectionRepo", "Supabase insert failed (RLS?), saving locally only: ${e.message}")
            }

            // Always add locally
            collectionDao.addQuoteToCollection(
                CollectionQuoteEntity(
                    collectionId = collectionId,
                    quoteId = quoteId
                )
            )
            android.util.Log.d("CollectionRepo", "Added to local DB successfully")

            Resource.Success(Unit)
        } catch (e: Exception) {
            android.util.Log.e("CollectionRepo", "Error adding quote to collection", e)
            Resource.Error(e.message ?: "Failed to add quote to collection")
        }
    }

    override suspend fun removeQuoteFromCollection(collectionId: String, quoteId: String): Resource<Unit> {
        return try {
            // Remove from Supabase
            supabaseClient.postgrest[Constants.TABLE_COLLECTION_QUOTES].delete {
                filter {
                    eq("collection_id", collectionId)
                    eq("quote_id", quoteId)
                }
            }

            // Remove locally
            collectionDao.removeQuoteFromCollection(collectionId, quoteId)

            Resource.Success(Unit)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Failed to remove quote from collection")
        }
    }

    override suspend fun syncCollections(): Resource<Unit> {
        return try {
            val currentUser = supabaseClient.auth.currentUserOrNull()
                ?: return Resource.Error("Not authenticated")

            // Fetch collections from Supabase
            val collections = supabaseClient.postgrest[Constants.TABLE_COLLECTIONS]
                .select {
                    filter { eq("user_id", currentUser.id) }
                }
                .decodeList<CollectionDto>()

            // Fetch collection quotes
            val collectionQuotes = supabaseClient.postgrest[Constants.TABLE_COLLECTION_QUOTES]
                .select()
                .decodeList<CollectionQuoteDto>()

            // Save locally
            val entities = collections.map { dto ->
                CollectionEntity(
                    id = dto.id,
                    name = dto.name,
                    description = dto.description,
                    userId = dto.userId,
                    createdAt = dto.createdAt,
                    updatedAt = dto.updatedAt
                )
            }
            collectionDao.insertCollections(entities)

            // Save collection quotes
            collectionQuotes.forEach { cq ->
                collectionDao.addQuoteToCollection(
                    CollectionQuoteEntity(
                        collectionId = cq.collectionId,
                        quoteId = cq.quoteId
                    )
                )
            }

            Resource.Success(Unit)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Failed to sync collections")
        }
    }
}
