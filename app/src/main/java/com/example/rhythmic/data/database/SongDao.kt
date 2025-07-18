package com.example.rhythmic.data.database

import androidx.room.*
import com.example.rhythmic.data.model.Song
import com.example.rhythmic.data.model.Playlist
import com.example.rhythmic.data.model.PlaylistSong
import kotlinx.coroutines.flow.Flow

@Dao
interface SongDao {
    @Query("SELECT * FROM songs ORDER BY title ASC")
    fun getAllSongs(): Flow<List<Song>>
    
    @Query("SELECT * FROM songs WHERE id = :id")
    suspend fun getSongById(id: String): Song?
    
    @Query("SELECT * FROM songs WHERE title LIKE :query OR artist LIKE :query OR album LIKE :query")
    fun searchSongs(query: String): Flow<List<Song>>
    
    @Query("SELECT * FROM songs WHERE isFavorite = 1")
    fun getFavoriteSongs(): Flow<List<Song>>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSong(song: Song)
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSongs(songs: List<Song>)
    
    @Update
    suspend fun updateSong(song: Song)
    
    @Delete
    suspend fun deleteSong(song: Song)
    
    @Query("UPDATE songs SET isFavorite = :isFavorite WHERE id = :songId")
    suspend fun updateFavoriteStatus(songId: String, isFavorite: Boolean)
}

@Dao
interface PlaylistDao {
    @Query("SELECT * FROM playlists ORDER BY createdAt DESC")
    fun getAllPlaylists(): Flow<List<Playlist>>
    
    @Query("SELECT * FROM playlists WHERE id = :id")
    suspend fun getPlaylistById(id: Long): Playlist?
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPlaylist(playlist: Playlist): Long
    
    @Update
    suspend fun updatePlaylist(playlist: Playlist)
    
    @Delete
    suspend fun deletePlaylist(playlist: Playlist)
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPlaylistSong(playlistSong: PlaylistSong)
    
    @Query("DELETE FROM playlist_songs WHERE playlistId = :playlistId AND songId = :songId")
    suspend fun removePlaylistSong(playlistId: Long, songId: String)
    
    @Query("SELECT s.* FROM songs s INNER JOIN playlist_songs ps ON s.id = ps.songId WHERE ps.playlistId = :playlistId ORDER BY ps.position")
    fun getPlaylistSongs(playlistId: Long): Flow<List<Song>>
    
    @Query("DELETE FROM playlist_songs WHERE playlistId = :playlistId")
    suspend fun clearPlaylist(playlistId: Long)
}

// ==================== Repository ====================