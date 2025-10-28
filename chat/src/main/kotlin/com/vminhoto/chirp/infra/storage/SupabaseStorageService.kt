package com.vminhoto.chirp.infra.storage

import com.vminhoto.chirp.domain.exception.InvalidProfilePictureException
import com.vminhoto.chirp.domain.exception.StorageException
import com.vminhoto.chirp.domain.models.ProfilePictureUploadCredentials
import com.vminhoto.chirp.domain.type.UserId
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import org.springframework.web.client.RestClient
import java.time.Instant
import java.util.UUID

/**
 * Service to handle Supabase calls
 * @param supabaseUrl the base URL for supabase calls
 * @param supabaseRestClient The REST client to make calls
 */
@Service
class SupabaseStorageService(
    @param:Value("\${supabase.url") private val supabaseUrl: String,
    private val supabaseRestClient: RestClient,
) {
    companion object {
        private val allowedMimeTypes = mapOf(
            "image/jpeg" to "jpg",
            "image/jpg" to "jpg",
            "image/png" to "png",
            "image/webp" to "webp"
        )
    }

    /**
     * Generates a Signed URL for picture upload.
     * @param userId The id of the user
     * @param mimeType the type of media
     * @return A [ProfilePictureUploadCredentials] containing the picture upload information.
     */
    fun generateSignedUploadUrl(userId: UserId, mimeType: String): ProfilePictureUploadCredentials {
        val extension = allowedMimeTypes[mimeType]
            ?: throw InvalidProfilePictureException("Invalid mime type $mimeType")

        val fileName = "user_${userId}_${UUID.randomUUID()}.$extension"
        val path = "profile-pictures/$fileName"

        val publicUrl = "$supabaseUrl/storage/v1/object/public/$path"

        return ProfilePictureUploadCredentials(
            uploadUrl = createSignedUrl(
                path = path,
                expiresInSeconds = 300
            ) ,
            publicUrl = publicUrl,
            headers = mapOf(
                "Content-Type" to mimeType
            ),
            expiresAt = Instant.now().plusSeconds(300)
        )
    }

    /**
     * Creates a signed URL from the Supabase endpoint
     * @param path the path of the picture we want.
     * @param expiresInSeconds the time for the URL to expire.
     * @return a String of the supabase URL to upload the picture.
     */
    private fun createSignedUrl(
        path: String,
        expiresInSeconds: Int
    ): String {
        val json = """
            { "expiresIn": $expiresInSeconds }
        """.trimIndent()

        val response = supabaseRestClient
            .post()
            .uri("/storage/v1/objects/upload/sign/$path")
            .body(json)
            .retrieve()
            .body(SignedUploadResponse::class.java)
            ?: throw StorageException("Failed to create signed URL")

        return "$supabaseUrl/storage/v1${response.url}"
    }

    /**
     * Function to delete a file. Builds the path from the base supabase URL and the picture URL
     * and makes the delete request.
     * @param url The url of the picture
     * @throws StorageException if the URL is invalid.
     */
    fun deleteFile(url: String) {
        val path = if(url.contains("/object/public/")) {
            url.substringAfter("/object/public/")
        } else throw StorageException("Invalid file URL format")

        val deleteUrl = "/storage/v1/object/$path"

        val response = supabaseRestClient
            .delete()
            .uri(deleteUrl)
            .retrieve()
            .toBodilessEntity()

        if(response.statusCode.isError) {
            throw StorageException("Unable to delete file: ${response.statusCode}")
        }
    }

    /**
     * Data class for body parsing
     * @param url the URL of the response.
     */
    private data class SignedUploadResponse(
        val url: String
    )
}