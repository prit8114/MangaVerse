package com.mangaverse.reader.core.security

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.security.SecureRandom
import javax.crypto.Cipher
import javax.crypto.SecretKey
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Service for encrypting and decrypting files
 */
@Singleton
class FileEncryptionService @Inject constructor(
    @ApplicationContext private val context: Context,
    private val securityManager: SecurityManager
) {

    companion object {
        private const val TRANSFORMATION = "AES/CBC/PKCS7Padding"
        private const val ALGORITHM = "AES"
        private const val BUFFER_SIZE = 8192 // 8KB buffer
    }

    /**
     * Encrypt a file and save it to the specified location
     * @param sourceFile The file to encrypt
     * @param destFile The destination for the encrypted file
     * @return The encryption key used (hex string)
     */
    fun encryptFile(sourceFile: File, destFile: File): String {
        // Generate a random key for this file
        val keyBytes = ByteArray(32) // 256 bits
        SecureRandom().nextBytes(keyBytes)
        val secretKey = SecretKeySpec(keyBytes, ALGORITHM)
        
        // Initialize cipher
        val cipher = Cipher.getInstance(TRANSFORMATION)
        cipher.init(Cipher.ENCRYPT_MODE, secretKey)
        val iv = cipher.iv
        
        // Create output file if it doesn't exist
        if (!destFile.exists()) {
            destFile.parentFile?.mkdirs()
            destFile.createNewFile()
        }
        
        // Write IV to the beginning of the file
        FileOutputStream(destFile).use { fos ->
            // Write IV length and IV
            fos.write(iv.size)
            fos.write(iv)
            
            // Encrypt and write the file content
            FileInputStream(sourceFile).use { fis ->
                val buffer = ByteArray(BUFFER_SIZE)
                var bytesRead: Int
                
                while (fis.read(buffer).also { bytesRead = it } != -1) {
                    val encryptedBytes = if (bytesRead == buffer.size) {
                        cipher.update(buffer)
                    } else {
                        cipher.update(buffer, 0, bytesRead)
                    }
                    
                    if (encryptedBytes != null) {
                        fos.write(encryptedBytes)
                    }
                }
                
                // Write final block
                val finalBytes = cipher.doFinal()
                if (finalBytes != null) {
                    fos.write(finalBytes)
                }
            }
        }
        
        // Return the key as a hex string
        return keyBytes.joinToString("") { "%02x".format(it) }
    }

    /**
     * Decrypt a file and save it to the specified location
     * @param sourceFile The encrypted file
     * @param destFile The destination for the decrypted file
     * @param keyHex The encryption key as a hex string
     */
    fun decryptFile(sourceFile: File, destFile: File, keyHex: String) {
        // Convert hex key string to bytes
        val keyBytes = keyHex.chunked(2)
            .map { it.toInt(16).toByte() }
            .toByteArray()
        val secretKey = SecretKeySpec(keyBytes, ALGORITHM)
        
        // Create output file if it doesn't exist
        if (!destFile.exists()) {
            destFile.parentFile?.mkdirs()
            destFile.createNewFile()
        }
        
        FileInputStream(sourceFile).use { fis ->
            // Read IV length and IV
            val ivLength = fis.read()
            val iv = ByteArray(ivLength)
            fis.read(iv)
            
            // Initialize cipher for decryption
            val cipher = Cipher.getInstance(TRANSFORMATION)
            cipher.init(Cipher.DECRYPT_MODE, secretKey, IvParameterSpec(iv))
            
            // Decrypt and write the file content
            FileOutputStream(destFile).use { fos ->
                val buffer = ByteArray(BUFFER_SIZE)
                var bytesRead: Int
                
                while (fis.read(buffer).also { bytesRead = it } != -1) {
                    val decryptedBytes = if (bytesRead == buffer.size) {
                        cipher.update(buffer)
                    } else {
                        cipher.update(buffer, 0, bytesRead)
                    }
                    
                    if (decryptedBytes != null) {
                        fos.write(decryptedBytes)
                    }
                }
                
                // Write final block
                val finalBytes = cipher.doFinal()
                if (finalBytes != null) {
                    fos.write(finalBytes)
                }
            }
        }
    }

    /**
     * Get the app's private storage directory for manga
     */
    fun getMangaStorageDir(): File {
        val dir = File(context.filesDir, "manga")
        if (!dir.exists()) {
            dir.mkdirs()
        }
        return dir
    }

    /**
     * Get the storage directory for a specific manga
     */
    fun getMangaDir(mangaId: String): File {
        val dir = File(getMangaStorageDir(), mangaId)
        if (!dir.exists()) {
            dir.mkdirs()
        }
        return dir
    }

    /**
     * Get the storage directory for a specific chapter
     */
    fun getChapterDir(mangaId: String, chapterId: String): File {
        val dir = File(getMangaDir(mangaId), chapterId)
        if (!dir.exists()) {
            dir.mkdirs()
        }
        return dir
    }

    /**
     * Get the file for a specific page
     */
    fun getPageFile(mangaId: String, chapterId: String, pageNumber: Int): File {
        return File(getChapterDir(mangaId, chapterId), "$pageNumber.enc")
    }

    /**
     * Get a temporary decrypted file for a page
     */
    fun getTempPageFile(mangaId: String, chapterId: String, pageNumber: Int): File {
        val cacheDir = context.cacheDir
        val dir = File(cacheDir, "manga/$mangaId/$chapterId")
        if (!dir.exists()) {
            dir.mkdirs()
        }
        return File(dir, "$pageNumber.jpg")
    }

    /**
     * Clean up temporary files
     */
    fun cleanupTempFiles() {
        val cacheDir = File(context.cacheDir, "manga")
        if (cacheDir.exists()) {
            deleteRecursive(cacheDir)
        }
    }

    /**
     * Delete a directory and all its contents recursively
     */
    private fun deleteRecursive(fileOrDirectory: File) {
        if (fileOrDirectory.isDirectory) {
            fileOrDirectory.listFiles()?.forEach { child ->
                deleteRecursive(child)
            }
        }
        fileOrDirectory.delete()
    }
}