package max.ohm.quoteapp.di

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.Auth
import io.github.jan.supabase.auth.FlowType
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.realtime.Realtime
import io.github.jan.supabase.storage.Storage
import max.ohm.quoteapp.BuildConfig
import max.ohm.quoteapp.data.local.QuoteDatabase
import max.ohm.quoteapp.data.local.dao.CollectionDao
import max.ohm.quoteapp.data.local.dao.QuoteDao
import max.ohm.quoteapp.data.repository.AuthRepositoryImpl
import max.ohm.quoteapp.data.repository.CollectionRepositoryImpl
import max.ohm.quoteapp.data.repository.QuoteRepositoryImpl
import max.ohm.quoteapp.data.repository.SettingsRepositoryImpl
import max.ohm.quoteapp.domain.repository.AuthRepository
import max.ohm.quoteapp.domain.repository.CollectionRepository
import max.ohm.quoteapp.domain.repository.QuoteRepository
import max.ohm.quoteapp.domain.repository.SettingsRepository
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideSupabaseClient(): SupabaseClient {
        return createSupabaseClient(
            supabaseUrl = BuildConfig.SUPABASE_URL,
            supabaseKey = BuildConfig.SUPABASE_ANON_KEY
        ) {
            install(Auth) {
                flowType = FlowType.PKCE
                scheme = "quoteapp"
                host = "auth-callback"
            }
            install(Postgrest)
            install(Storage)
            install(Realtime)
        }
    }

    @Provides
    @Singleton
    fun provideQuoteDatabase(
        @ApplicationContext context: Context
    ): QuoteDatabase {
        return Room.databaseBuilder(
            context,
            QuoteDatabase::class.java,
            QuoteDatabase.DATABASE_NAME
        ).build()
    }

    @Provides
    @Singleton
    fun provideQuoteDao(database: QuoteDatabase): QuoteDao {
        return database.quoteDao()
    }

    @Provides
    @Singleton
    fun provideCollectionDao(database: QuoteDatabase): CollectionDao {
        return database.collectionDao()
    }

    @Provides
    @Singleton
    fun provideAuthRepository(
        supabaseClient: SupabaseClient,
        @ApplicationContext context: Context
    ): AuthRepository {
        return AuthRepositoryImpl(supabaseClient, context)
    }

    @Provides
    @Singleton
    fun provideQuoteRepository(
        supabaseClient: SupabaseClient,
        quoteDao: QuoteDao,
        @ApplicationContext context: Context
    ): QuoteRepository {
        return QuoteRepositoryImpl(supabaseClient, quoteDao, context)
    }

    @Provides
    @Singleton
    fun provideCollectionRepository(
        supabaseClient: SupabaseClient,
        collectionDao: CollectionDao,
        quoteDao: QuoteDao
    ): CollectionRepository {
        return CollectionRepositoryImpl(supabaseClient, collectionDao, quoteDao)
    }

    @Provides
    @Singleton
    fun provideSettingsRepository(
        @ApplicationContext context: Context
    ): SettingsRepository {
        return SettingsRepositoryImpl(context)
    }

    @Provides
    @Singleton
    fun provideStorageRepository(
        supabaseClient: SupabaseClient
    ): max.ohm.quoteapp.domain.repository.StorageRepository {
        return max.ohm.quoteapp.data.repository.StorageRepositoryImpl(supabaseClient)
    }
}
