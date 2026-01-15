package max.ohm.quoteapp.data.repository

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.postgrest.query.Columns
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import max.ohm.quoteapp.data.local.dao.QuoteDao
import max.ohm.quoteapp.data.local.entity.QuoteEntity
import max.ohm.quoteapp.data.remote.dto.FavoriteDto
import max.ohm.quoteapp.data.remote.dto.InsertFavoriteDto
import max.ohm.quoteapp.data.remote.dto.QuoteDto
import max.ohm.quoteapp.domain.model.Quote
import max.ohm.quoteapp.domain.model.QuoteCategory
import max.ohm.quoteapp.domain.repository.QuoteRepository
import max.ohm.quoteapp.util.Constants
import max.ohm.quoteapp.util.DateUtils
import max.ohm.quoteapp.util.Resource
import javax.inject.Inject
import javax.inject.Singleton

private val Context.quoteDataStore: DataStore<Preferences> by preferencesDataStore(name = "quote_prefs")

@Singleton
class QuoteRepositoryImpl @Inject constructor(
    private val supabaseClient: SupabaseClient,
    private val quoteDao: QuoteDao,
    @ApplicationContext private val context: Context
) : QuoteRepository {

    private val dataStore = context.quoteDataStore
    private val dailyQuoteIdKey = stringPreferencesKey(Constants.KEY_DAILY_QUOTE_ID)
    private val dailyQuoteDateKey = stringPreferencesKey(Constants.KEY_DAILY_QUOTE_DATE)

    override fun getAllQuotes(): Flow<List<Quote>> {
        return quoteDao.getAllQuotes().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override fun getQuotesByCategory(category: QuoteCategory): Flow<List<Quote>> {
        return quoteDao.getQuotesByCategory(category.name).map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override fun getFavoriteQuotes(): Flow<List<Quote>> {
        return quoteDao.getFavoriteQuotes().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override fun searchQuotes(query: String): Flow<List<Quote>> {
        return quoteDao.searchQuotes(query).map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override fun getQuotesByAuthor(author: String): Flow<List<Quote>> {
        return quoteDao.getQuotesByAuthor(author).map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override suspend fun getQuoteById(id: String): Quote? {
        return quoteDao.getQuoteById(id)?.toDomain()
    }

    override suspend fun refreshQuotes(): Resource<Unit> {
        return try {
            // Fetch quotes from Supabase
            val quotes = supabaseClient.postgrest[Constants.TABLE_QUOTES]
                .select()
                .decodeList<QuoteDto>()

            // Get user's favorites if logged in
            val favoriteQuoteIds = mutableSetOf<String>()
            val currentUser = supabaseClient.auth.currentUserOrNull()
            if (currentUser != null) {
                try {
                    val favorites = supabaseClient.postgrest[Constants.TABLE_FAVORITES]
                        .select(columns = Columns.list("quote_id")) {
                            filter { eq("user_id", currentUser.id) }
                        }
                        .decodeList<FavoriteDto>()
                    favoriteQuoteIds.addAll(favorites.map { it.quoteId })
                } catch (e: Exception) {
                    // Continue without favorites
                }
            }

            // Convert to entities and save locally
            val entities = quotes.map { dto ->
                QuoteEntity(
                    id = dto.id,
                    text = dto.text,
                    author = dto.author,
                    category = dto.category,
                    createdAt = dto.createdAt,
                    isFavorite = dto.id in favoriteQuoteIds
                )
            }
            quoteDao.insertQuotes(entities)

            Resource.Success(Unit)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Failed to refresh quotes")
        }
    }

    override suspend fun toggleFavorite(quoteId: String, isFavorite: Boolean): Resource<Unit> {
        return try {
            val currentUser = supabaseClient.auth.currentUserOrNull()
                ?: return Resource.Error("Please sign in to save favorites")

            if (isFavorite) {
                // Add to favorites
                supabaseClient.postgrest[Constants.TABLE_FAVORITES].insert(
                    InsertFavoriteDto(
                        userId = currentUser.id,
                        quoteId = quoteId
                    )
                )
            } else {
                // Remove from favorites
                supabaseClient.postgrest[Constants.TABLE_FAVORITES].delete {
                    filter {
                        eq("user_id", currentUser.id)
                        eq("quote_id", quoteId)
                    }
                }
            }

            // Update local database
            quoteDao.updateFavoriteStatus(quoteId, isFavorite)

            Resource.Success(Unit)
        } catch (e: Exception) {
            // Still update locally even if remote fails
            quoteDao.updateFavoriteStatus(quoteId, isFavorite)
            Resource.Error(e.message ?: "Failed to update favorite")
        }
    }

    override suspend fun getDailyQuote(): Resource<Quote> {
        return try {
            val prefs = dataStore.data.first()
            val savedDate = prefs[dailyQuoteDateKey]
            val savedQuoteId = prefs[dailyQuoteIdKey]
            val today = DateUtils.getCurrentDate()

            // Check if we already have a quote for today
            if (savedDate == today && savedQuoteId != null) {
                val quote = getQuoteById(savedQuoteId)
                if (quote != null) {
                    return Resource.Success(quote)
                }
            }

            // Get a new random quote
            val randomQuote = quoteDao.getRandomQuote()?.toDomain()
                ?: return Resource.Error("No quotes available")

            // Save as today's quote
            dataStore.edit { prefs ->
                prefs[dailyQuoteDateKey] = today
                prefs[dailyQuoteIdKey] = randomQuote.id
            }

            Resource.Success(randomQuote)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Failed to get daily quote")
        }
    }

    override suspend fun getRandomQuote(): Quote? {
        return quoteDao.getRandomQuote()?.toDomain()
    }
}
