package com.mangaverse.reader.core.data.preferences

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit
import androidx.preference.PreferenceManager
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Manager for handling application preferences
 */
@Singleton
class PreferencesManager @Inject constructor(
    @ApplicationContext private val context: Context
) {

    companion object {
        // Auth preferences
        private const val KEY_AUTH_TOKEN = "auth_token"
        
        // Reader preferences
        private const val KEY_READING_DIRECTION = "reading_direction"
        private const val KEY_THEME_MODE = "theme_mode"
        private const val KEY_USE_DATA_SAVER = "use_data_saver"
        private const val KEY_AUTO_DOWNLOAD_WIFI = "auto_download_wifi"
        private const val KEY_SHOW_NSFW_CONTENT = "show_nsfw_content"
        private const val KEY_PREFERRED_LANGUAGES = "preferred_languages"
        private const val KEY_NOTIFICATIONS_ENABLED = "notifications_enabled"
        
        // Default values
        private const val DEFAULT_READING_DIRECTION = "LEFT_TO_RIGHT"
        private const val DEFAULT_THEME_MODE = "SYSTEM"
        private const val DEFAULT_USE_DATA_SAVER = false
        private const val DEFAULT_AUTO_DOWNLOAD_WIFI = false
        private const val DEFAULT_SHOW_NSFW_CONTENT = false
        private const val DEFAULT_PREFERRED_LANGUAGES = "en"
        private const val DEFAULT_NOTIFICATIONS_ENABLED = true
    }

    private val preferences: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)

    /**
     * Save authentication token
     */
    fun saveAuthToken(token: String) {
        preferences.edit {
            putString(KEY_AUTH_TOKEN, token)
        }
    }

    /**
     * Get authentication token
     */
    fun getAuthToken(): String {
        return preferences.getString(KEY_AUTH_TOKEN, "") ?: ""
    }

    /**
     * Clear authentication token
     */
    fun clearAuthToken() {
        preferences.edit {
            remove(KEY_AUTH_TOKEN)
        }
    }

    /**
     * Get reading direction preference
     */
    fun getReadingDirection(): String {
        return preferences.getString(KEY_READING_DIRECTION, DEFAULT_READING_DIRECTION) ?: DEFAULT_READING_DIRECTION
    }

    /**
     * Set reading direction preference
     */
    fun setReadingDirection(direction: String) {
        preferences.edit {
            putString(KEY_READING_DIRECTION, direction)
        }
    }

    /**
     * Get theme mode preference
     */
    fun getThemeMode(): String {
        return preferences.getString(KEY_THEME_MODE, DEFAULT_THEME_MODE) ?: DEFAULT_THEME_MODE
    }

    /**
     * Set theme mode preference
     */
    fun setThemeMode(mode: String) {
        preferences.edit {
            putString(KEY_THEME_MODE, mode)
        }
    }

    /**
     * Get data saver preference
     */
    fun getUseDataSaver(): Boolean {
        return preferences.getBoolean(KEY_USE_DATA_SAVER, DEFAULT_USE_DATA_SAVER)
    }

    /**
     * Set data saver preference
     */
    fun setUseDataSaver(enabled: Boolean) {
        preferences.edit {
            putBoolean(KEY_USE_DATA_SAVER, enabled)
        }
    }

    /**
     * Get auto download on WiFi preference
     */
    fun getAutoDownloadOnWifi(): Boolean {
        return preferences.getBoolean(KEY_AUTO_DOWNLOAD_WIFI, DEFAULT_AUTO_DOWNLOAD_WIFI)
    }

    /**
     * Set auto download on WiFi preference
     */
    fun setAutoDownloadOnWifi(enabled: Boolean) {
        preferences.edit {
            putBoolean(KEY_AUTO_DOWNLOAD_WIFI, enabled)
        }
    }

    /**
     * Get show NSFW content preference
     */
    fun getShowNsfwContent(): Boolean {
        return preferences.getBoolean(KEY_SHOW_NSFW_CONTENT, DEFAULT_SHOW_NSFW_CONTENT)
    }

    /**
     * Set show NSFW content preference
     */
    fun setShowNsfwContent(enabled: Boolean) {
        preferences.edit {
            putBoolean(KEY_SHOW_NSFW_CONTENT, enabled)
        }
    }

    /**
     * Get preferred languages
     */
    fun getPreferredLanguages(): String {
        return preferences.getString(KEY_PREFERRED_LANGUAGES, DEFAULT_PREFERRED_LANGUAGES) ?: DEFAULT_PREFERRED_LANGUAGES
    }

    /**
     * Set preferred languages
     */
    fun setPreferredLanguages(languages: String) {
        preferences.edit {
            putString(KEY_PREFERRED_LANGUAGES, languages)
        }
    }

    /**
     * Get notifications enabled preference
     */
    fun getNotificationsEnabled(): Boolean {
        return preferences.getBoolean(KEY_NOTIFICATIONS_ENABLED, DEFAULT_NOTIFICATIONS_ENABLED)
    }

    /**
     * Set notifications enabled preference
     */
    fun setNotificationsEnabled(enabled: Boolean) {
        preferences.edit {
            putBoolean(KEY_NOTIFICATIONS_ENABLED, enabled)
        }
    }

    /**
     * Clear all preferences
     */
    fun clearAll() {
        preferences.edit {
            clear()
        }
    }
}