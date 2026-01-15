package max.ohm.quoteapp.data.repository

import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.storage.storage
import max.ohm.quoteapp.domain.repository.StorageRepository
import max.ohm.quoteapp.util.Constants
import max.ohm.quoteapp.util.Resource
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.time.Duration.Companion.minutes

@Singleton
class StorageRepositoryImpl @Inject constructor(
    private val supabaseClient: SupabaseClient
) : StorageRepository {

    override suspend fun uploadProfilePicture(userId: String, byteArray: ByteArray): Resource<String> {
        return try {
            android.util.Log.d("StorageRepo", "Starting upload for user $userId, size: ${byteArray.size} bytes")
            val bucket = supabaseClient.storage.from(Constants.BUCKET_AVATARS)
            val fileName = "$userId.jpg" // We'll convert everything to JPEG or just use this extension
            
            bucket.upload(fileName, byteArray) {
                upsert = true
            }
            android.util.Log.d("StorageRepo", "Upload successful")
            
            // Get public URL
            val publicUrl = bucket.publicUrl(fileName)
            android.util.Log.d("StorageRepo", "Public URL: $publicUrl")
            
            // Add a timestamp query param to bust cache
            val timestamp = System.currentTimeMillis()
            Resource.Success("$publicUrl?t=$timestamp")
        } catch (e: Exception) {
            android.util.Log.e("StorageRepo", "Upload failed", e)
            Resource.Error(e.message ?: "Failed to upload image")
        }
    }
}
