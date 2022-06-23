package de.uriegel.firemusic

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.KeyEvent
import android.view.View.GONE
import androidx.preference.PreferenceManager
import com.google.android.exoplayer2.ExoPlaybackException
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.SimpleExoPlayer
import de.uriegel.activityextensions.http.post
import de.uriegel.firemusic.databinding.ActivityAudioBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.serialization.*
import kotlinx.serialization.json.Json
import java.net.URLEncoder

class AudioActivity : AppCompatActivity(), Player.EventListener, CoroutineScope {

    @Serializable
    data class SonyDataParam(val mode: String)

    @Serializable
    data class SonyData(val method: String, val version: String, val id: Int, val params: Array<SonyDataParam>)

    override val coroutineContext = Dispatchers.Main

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAudioBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val album = intent.getStringArrayExtra("album")!!
        val url = intent.getStringExtra("url")!!
        playlist = album.map { "${url}/${URLEncoder.encode(it, "utf-8")}" }.toTypedArray()

        val preferences = PreferenceManager.getDefaultSharedPreferences(this@AudioActivity)
        val sonyUrl = preferences.getString("sony_url", "")
        if (sonyUrl!!.length < 6)
            binding.powerSaving.visibility = GONE
        val sonyPsk = preferences.getString("sony_psk", "")

        binding.powerSaving.setOnClickListener {
            launch {
                val data = SonyData("setPowerSavingMode", "1.0", 111, arrayOf(SonyDataParam("pictureOff")))
                val content = Json.encodeToString(data)
                post(sonyUrl + "/system", content, sonyPsk)
            }
        }
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
        binding.playerView.showController()
        return super.onKeyDown(keyCode, event)
    }

    private fun initializePlayer() {
        if (player == null) {
            player = SimpleExoPlayer.Builder(this).build()
            binding.playerView.player = player
            playlist.forEach { player!!.addMediaItem(MediaItem.fromUri(it.replace("+", "%20"))) }
            player!!.prepare()
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

    fun onPlayerError(error: ExoPlaybackException) {
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
    private lateinit var binding: ActivityAudioBinding
}