package com.mangaverse.reader.core.security

import android.content.Context
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import androidx.security.crypto.EncryptedFile
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import dagger.hilt.android.qualifiers.ApplicationContext
import java.io.File
import java.security.SecureRandom
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.IvParameterSpec
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Manager class for handling security-related operations
 */
@Singleton
class SecurityManager @Inject constructor(
    @ApplicationContext private val context: Context
) {

    companion object {
        private const val MASTER_KEY_ALIAS = "mangaverse_master_key"
        private const val ENCRYPTED_PREFS_FILE = "mangaverse_secure_prefs"
        private const val DATABASE_KEY_PREF = "database_encryption_key"
        private const val KEYSTORE_PROVIDER = "AndroidKeyStore"
        private const val TRANSFORMATION = "AES/CBC/PKCS7Padding"
    }

    /**
     * Create or get the master key for encryption
     */
    private fun getMasterKey(): MasterKey {
        return MasterKey.Builder(context, MASTER_KEY_ALIAS)
            .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
            .build()
    }

    /**
     * Get encrypted shared preferences
     */
    private fun getEncryptedSharedPreferences() = EncryptedSharedPreferences.create(
        context,
        ENCRYPTED_PREFS_FILE,
        getMasterKey(),
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
    )

    /**
     * Get the database encryption key
     * If it doesn't exist, generate a new one and store it
     */
    fun getDatabaseKey(): String {
        val prefs = getEncryptedSharedPreferences()
        var key = prefs.getString(DATABASE_KEY_PREF, null)

        if (key == null) {
            // Generate a new key if one doesn't exist
            key = generateRandomKey(32) // 256 bits
            prefs.edit().putString(DATABASE_KEY_PREF, key).apply()
        }

        return key
    }

    /**
     * Create an encrypted file
     */
    fun createEncryptedFile(file: File): EncryptedFile {
        return EncryptedFile.Builder(
            context,
            file,
            getMasterKey(),
            EncryptedFile.FileEncryptionScheme.AES256_GCM_HKDF_4KB
        ).build()
    }

    /**
     * Generate a random encryption key
     */
    fun generateRandomKey(length: Int): String {
        val random = SecureRandom()
        val bytes = ByteArray(length)
        random.nextBytes(bytes)
        return bytes.joinToString("") { "%02x".format(it) }
    }

    /**
     * Generate an AES key using Android Keystore
     */
    fun generateAesKey(keyAlias: String): SecretKey {
        val keyGenerator = KeyGenerator.getInstance(
            KeyProperties.KEY_ALGORITHM_AES,
            KEYSTORE_PROVIDER
        )

        val keyGenParameterSpec = KeyGenParameterSpec.Builder(
            keyAlias,
            KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT
        )
            .setBlockModes(KeyProperties.BLOCK_MODE_CBC)
            .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_PKCS7)
            .setUserAuthenticationRequired(false)
            .setRandomizedEncryptionRequired(true)
            .build()

        keyGenerator.init(keyGenParameterSpec)
        return keyGenerator.generateKey()
    }

    /**
     * Encrypt data
     */
    fun encrypt(data: ByteArray, key: SecretKey): Pair<ByteArray, ByteArray> {
        val cipher = Cipher.getInstance(TRANSFORMATION)
        cipher.init(Cipher.ENCRYPT_MODE, key)
        val iv = cipher.iv
        val encryptedData = cipher.doFinal(data)
        return Pair(encryptedData, iv)
    }

    /**
     * Decrypt data
     */
    fun decrypt(encryptedData: ByteArray, key: SecretKey, iv: ByteArray): ByteArray {
        val cipher = Cipher.getInstance(TRANSFORMATION)
        val ivSpec = IvParameterSpec(iv)
        cipher.init(Cipher.DECRYPT_MODE, key, ivSpec)
        return cipher.doFinal(encryptedData)
    }

    /**
     * Store a secure value in encrypted shared preferences
     */
    fun storeSecureValue(key: String, value: String) {
        getEncryptedSharedPreferences().edit().putString(key, value).apply()
    }

    /**
     * Retrieve a secure value from encrypted shared preferences
     */
    fun getSecureValue(key: String): String? {
        return getEncryptedSharedPreferences().getString(key, null)
    }

    /**
     * Remove a secure value from encrypted shared preferences
     */
    fun removeSecureValue(key: String) {
        getEncryptedSharedPreferences().edit().remove(key).apply()
    }
}