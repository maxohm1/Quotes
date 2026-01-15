package max.ohm.quoteapp.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import max.ohm.quoteapp.data.local.dao.CollectionDao
import max.ohm.quoteapp.data.local.dao.QuoteDao
import max.ohm.quoteapp.data.local.entity.CollectionEntity
import max.ohm.quoteapp.data.local.entity.CollectionQuoteEntity
import max.ohm.quoteapp.data.local.entity.QuoteEntity

@Database(
    entities = [
        QuoteEntity::class,
        CollectionEntity::class,
        CollectionQuoteEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class QuoteDatabase : RoomDatabase() {
    abstract fun quoteDao(): QuoteDao
    abstract fun collectionDao(): CollectionDao

    companion object {
        const val DATABASE_NAME = "quote_vault_db"
    }
}
