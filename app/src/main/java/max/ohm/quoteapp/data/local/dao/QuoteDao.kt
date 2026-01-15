package max.ohm.quoteapp.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow
import max.ohm.quoteapp.data.local.entity.QuoteEntity

@Dao
interface QuoteDao {
    @Query("SELECT * FROM quotes ORDER BY createdAt DESC")
    fun getAllQuotes(): Flow<List<QuoteEntity>>

    @Query("SELECT * FROM quotes WHERE category = :category ORDER BY createdAt DESC")
    fun getQuotesByCategory(category: String): Flow<List<QuoteEntity>>

    @Query("SELECT * FROM quotes WHERE isFavorite = 1 ORDER BY createdAt DESC")
    fun getFavoriteQuotes(): Flow<List<QuoteEntity>>

    @Query("SELECT * FROM quotes WHERE text LIKE '%' || :query || '%' OR author LIKE '%' || :query || '%' ORDER BY createdAt DESC")
    fun searchQuotes(query: String): Flow<List<QuoteEntity>>

    @Query("SELECT * FROM quotes WHERE author LIKE '%' || :author || '%' ORDER BY createdAt DESC")
    fun getQuotesByAuthor(author: String): Flow<List<QuoteEntity>>

    @Query("SELECT * FROM quotes WHERE id = :id")
    suspend fun getQuoteById(id: String): QuoteEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertQuotes(quotes: List<QuoteEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertQuote(quote: QuoteEntity)

    @Update
    suspend fun updateQuote(quote: QuoteEntity)

    @Query("UPDATE quotes SET isFavorite = :isFavorite WHERE id = :quoteId")
    suspend fun updateFavoriteStatus(quoteId: String, isFavorite: Boolean)

    @Query("DELETE FROM quotes")
    suspend fun deleteAllQuotes()

    @Query("SELECT * FROM quotes ORDER BY RANDOM() LIMIT 1")
    suspend fun getRandomQuote(): QuoteEntity?

    @Query("SELECT COUNT(*) FROM quotes")
    suspend fun getQuoteCount(): Int
}
