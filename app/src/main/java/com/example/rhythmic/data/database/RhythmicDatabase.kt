package com.example.rhythmic.data.database

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import android.content.Context
import com.example.rhythmic.data.model.Song
import com.example.rhythmic.data.model.Playlist
import com.example.rhythmic.data.model.PlaylistSong

@Database(
    entities = [Song::class, Playlist::class, PlaylistSong::class],
    version = 1,
    exportSchema = false
)
abstract class RhythmicDatabase : RoomDatabase() {
    abstract fun songDao(): SongDao
    abstract fun playlistDao(): PlaylistDao
    
    companion object {
        @Volatile
        private var INSTANCE: RhythmicDatabase? = null
        
        fun getDatabase(context: Context): RhythmicDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    RhythmicDatabase::class.java,
                    "rhythmic_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}

// ==================== DAOs ====================