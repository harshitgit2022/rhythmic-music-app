package com.example.rhythmic.data.repository

import android.content.Context
import android.provider.MediaStore
import com.example.rhythmic.data.database.RhythmicDatabase
import com.example.rhythmic.data.model.Song
import com.example.rhythmic.data.model.Playlist
import com.example.rhythmic.data.model.PlaylistSong
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MusicRepository @Inject constructor(
    private val database: RhythmicDatabase,
    private val context: Context
) {
    
    fun getAllSongs(): Flow<List<Song>> = database.songDao().getAllSongs()
    
    fun searchSongs(query: String): Flow<List<Song>> =
        database.songDao().searchSongs("%$query%")
    
    fun getFavoriteSongs(): Flow<List<Song>> = database.songDao().getFavoriteSongs()
    
    suspend fun updateFavoriteStatus(songId: String, isFavorite: Boolean) {
        database.songDao().updateFavoriteStatus(songId, isFavorite)
    }
    
    fun getAllPlaylists(): Flow<List<Playlist>> = database.playlistDao().getAllPlaylists()
    
    suspend fun createPlaylist(name: String, description: String): Long {
        val playlist = Playlist(name = name, description = description)
        return database.playlistDao().insertPlaylist(playlist)
    }
    
    suspend fun addSongToPlaylist(playlistId: Long, songId: String) {
        val position = database.playlistDao().getPlaylistSongs(playlistId).hashCode()
        val playlistSong = PlaylistSong(playlistId = playlistId, songId = songId, position = position)
        database.playlistDao().insertPlaylistSong(playlistSong)
    }
    
    fun getPlaylistSongs(playlistId: Long): Flow<List<Song>> =
        database.playlistDao().getPlaylistSongs(playlistId)
    
    suspend fun loadSongsFromDevice() {
        withContext(Dispatchers.IO) {
            val songs = mutableListOf<Song>()
            val projection = arrayOf(
                MediaStore.Audio.Media._ID,
                MediaStore.Audio.Media.TITLE,
                MediaStore.Audio.Media.ARTIST,
                MediaStore.Audio.Media.ALBUM,
                MediaStore.Audio.Media.DURATION,
                MediaStore.Audio.Media.DATA
            )
            
            val cursor = context.contentResolver.query(
                MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                projection,
                "${MediaStore.Audio.Media.IS_MUSIC} = 1",
                null,
                "${MediaStore.Audio.Media.TITLE} ASC"
            )
            
            cursor?.use {
                val idColumn = it.getColumnIndexOrThrow(MediaStore.Audio.Media._ID)
                val titleColumn = it.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE)
                val artistColumn = it.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST)
                val albumColumn = it.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM)
                val durationColumn = it.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION)
                val pathColumn = it.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA)
                
                while (it.moveToNext()) {
                    val id = it.getString(idColumn)
                    val title = it.getString(titleColumn) ?: "Unknown"
                    val artist = it.getString(artistColumn) ?: "Unknown Artist"
                    val album = it.getString(albumColumn) ?: "Unknown Album"
                    val duration = it.getLong(durationColumn)
                    val path = it.getString(pathColumn)
                    
                    songs.add(
                        Song(
                            id = id,
                            title = title,
                            artist = artist,
                            album = album,
                            duration = duration,
                            path = path
                        )
                    )
                }
            }
            
            database.songDao().insertSongs(songs)
        }
    }
}

// ==================== Music Service ====================