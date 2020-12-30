package de.uriegel.firemusic

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.google.android.exoplayer2.ExoPlaybackException
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.SimpleExoPlayer

class AudioActivity : AppCompatActivity(), Player.EventListener {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_audio)

        val album = intent.getStringArrayExtra("album")!!
        val url = intent.getStringExtra("url")!!
        playlist = album.map { "${url}/${it}" }.toTypedArray()
    }

    override fun onStart() {
        super.onStart()
        initializePlayer()
    }

    override fun onStop() {
        super.onStop()
        releasePlayer()
    }

    private fun initializePlayer() {
        player = SimpleExoPlayer.Builder(this).build()

        playlist.forEach { player.addMediaItem(MediaItem.fromUri(it)) }

        //exoplayerView.player = simpleExoplayer
        player.prepare();
        // Start the playback.
        player.play();
        player.addListener(this)
    }

    private fun releasePlayer() {
        //playbackPosition = simpleExoplayer.currentPosition
        player.release()
    }

    override fun onPlayerError(error: ExoPlaybackException) {
        // handle error
    }

    override fun onPlayerStateChanged(playWhenReady: Boolean, playbackState: Int) {
//        if (playbackState == Player.STATE_BUFFERING)
//            progressBar.visibility = View.VISIBLE
//        else if (playbackState == Player.STATE_READY || playbackState == Player.STATE_ENDED)
//            progressBar.visibility = View.INVISIBLE
    }

    private lateinit var player: SimpleExoPlayer
    private lateinit var playlist: Array<String>
}