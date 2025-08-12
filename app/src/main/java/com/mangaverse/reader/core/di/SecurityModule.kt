package com.mangaverse.reader.core.di

import android.content.Context
import com.mangaverse.reader.core.security.FileEncryptionService
import com.mangaverse.reader.core.security.SecurityManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Dagger Hilt module for providing security-related dependencies
 */
@Module
@InstallIn(SingletonComponent::class)
object SecurityModule {

    @Provides
    @Singleton
    fun provideSecurityManager(@ApplicationContext context: Context): SecurityManager {
        return SecurityManager.getInstance(context)
    }

    @Provides
    @Singleton
    fun provideFileEncryptionService(
        @ApplicationContext context: Context,
        securityManager: SecurityManager
    ): FileEncryptionService {
        return FileEncryptionService(context, securityManager)
    }
}