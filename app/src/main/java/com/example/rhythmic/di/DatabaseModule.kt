package com.example.rhythmic.di

import android.content.Context
import androidx.media3.exoplayer.ExoPlayer
import com.example.rhythmic.data.database.RhythmicDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {
    
    @Provides
    @Singleton
    fun provideRhythmicDatabase(@ApplicationContext context: Context): RhythmicDatabase {
        return RhythmicDatabase.getDatabase(context)
    }
    
    @Provides
    @Singleton
    fun provideExoPlayer(@ApplicationContext context: Context): ExoPlayer {
        return ExoPlayer.Builder(context)
            .build()
    }
}

// ==================== ViewModels ====================