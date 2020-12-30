package de.uriegel.firemusic

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.KeyEvent
import com.google.android.exoplayer2.ExoPlaybackException
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.SimpleExoPlayer
import kotlinx.android.synthetic.main.activity_audio.*
import java.net.URLEncoder

class AudioActivity : AppCompatActivity(), Player.EventListener {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_audio)

        val album = intent.getStringArrayExtra("album")!!
        val url = intent.getStringExtra("url")!!
        playlist = album.map { "${url}/${URLEncoder.encode(it, "utf-8")}" }.toTypedArray()
    }

    override fun onStart() {
        super.onStart()
        initializePlayer()
    }

    override fun onResume() {
        super.onResume()
        initializePlayer()
    }

    override fun onPause() {
        super.onPause()
        releasePlayer()
    }

    override fun onStop() {
        super.onStop()
        releasePlayer()
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        playerView?.showController()
        return super.onKeyDown(keyCode, event)
    }

    private fun initializePlayer() {
        if (player == null) {
            player = SimpleExoPlayer.Builder(this).build()
            playerView.player = player

            playlist.forEach { player!!.addMediaItem(MediaItem.fromUri(it)) }

            //exoplayerView.player = simpleExoplayer
            player!!.prepare();
            // Start the playback.
            player!!.playWhenReady = true
            player!!.addListener(this)
        }
    }

    private fun releasePlayer() {
        if (player != null) {
            //playbackPosition = simpleExoplayer.currentPosition
            player!!.release()
            player = null
        }
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

    private var player: SimpleExoPlayer? = null
    private lateinit var playlist: Array<String>
}