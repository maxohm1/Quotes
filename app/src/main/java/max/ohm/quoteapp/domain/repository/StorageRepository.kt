package max.ohm.quoteapp.domain.repository

import max.ohm.quoteapp.util.Resource

interface StorageRepository {
    suspend fun uploadProfilePicture(userId: String, byteArray: ByteArray): Resource<String>
}
