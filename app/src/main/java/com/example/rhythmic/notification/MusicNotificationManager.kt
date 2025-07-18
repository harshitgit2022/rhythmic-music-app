package com.example.rhythmic.notification

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.browser.trusted.Token
import androidx.core.app.NotificationCompat
import androidx.media3.common.Player
import androidx.media3.session.MediaSession
import androidx.media3.session.MediaSessionService
import com.example.rhythmic.MainActivity
import com.example.rhythmic.R
import com.example.rhythmic.data.model.Song

class MusicNotificationManager(
    private val context: Context,
    private val sessionToken: MediaSession.Token
) {
    
    private val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    
    companion object {
        const val NOTIFICATION_ID = 1
        const val CHANNEL_ID = "music_playback"
    }
    
    init {
        createNotificationChannel()
    }
    
    private fun createNotificationChannel() {
        val channel = NotificationChannel(
            CHANNEL_ID,
            "Music Playback",
            NotificationManager.IMPORTANCE_LOW
        ).apply {
            description = "Shows currently playing music"
            setShowBadge(false)
            lockscreenVisibility = Notification.VISIBILITY_PUBLIC
        }
        notificationManager.createNotificationChannel(channel)
    }
    
    fun buildNotification(
        song: Song,
        isPlaying: Boolean,
        session: MediaSession
    ): Notification {
        val contentIntent = PendingIntent.getActivity(
            context,
            0,
            Intent(context, MainActivity::class.java),
            PendingIntent.FLAG_IMMUTABLE
        )
        
        return NotificationCompat.Builder(context, CHANNEL_ID)
            .setContentTitle(song.title)
            .setContentText(song.artist)
            .setSmallIcon(R.drawable.ic_music_note)
            .setContentIntent(contentIntent)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .setOnlyAlertOnce(true)
            .setShowWhen(false)
            .setStyle(
                androidx.media.app.NotificationCompat.MediaStyle()
                    .setMediaSession(session.sessionCompatToken)
                    .setShowActionsInCompactView(0, 1, 2)
                    .setShowCancelButton(true)
            )
            .addAction(
                NotificationCompat.Action(
                    R.drawable.ic_skip_previous,
                    "Previous",
                    MediaSessionService.buildMediaButtonPendingIntent(
                        context,
                        Player.COMMAND_SEEK_TO_PREVIOUS
                    )
                )
            )
            .addAction(
                NotificationCompat.Action(
                    if (isPlaying) R.drawable.ic_pause else R.drawable.ic_play,
                    if (isPlaying) "Pause" else "Play",
                    MediaSessionService.buildMediaButtonPendingIntent(
                        context,
                        Player.COMMAND_PLAY_PAUSE
                    )
                )
            )
            .addAction(
                NotificationCompat.Action(
                    R.drawable.ic_skip_next,
                    "Next",
                    MediaSessionService.buildMediaButtonPendingIntent(
                        context,
                        Player.COMMAND_SEEK_TO_NEXT
                    )
                )
            )
            .build()
    }
}

// ==================== Playlist Management ====================