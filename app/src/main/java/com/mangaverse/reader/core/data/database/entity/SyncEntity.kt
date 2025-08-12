package com.mangaverse.reader.core.data.database.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import java.util.Date

/**
 * Entity representing synchronization data with external services
 */
@Entity(
    tableName = "sync",
    indices = [
        Index("mangaId"),
        Index("service"),
        Index("lastSyncDate")
    ]
)
data class SyncEntity(
    @PrimaryKey
    val id: String,
    val mangaId: String,
    val service: SyncService,
    val serviceId: String, // ID of the manga in the external service
    val status: String, // reading, completed, on_hold, dropped, plan_to_read
    val score: Float?, // User rating
    val progress: Int, // Number of chapters read
    val lastSyncDate: Date,
    val dirty: Boolean // Needs to be synced to the service
)

enum class SyncService {
    MYANIMELIST,
    ANILIST
}