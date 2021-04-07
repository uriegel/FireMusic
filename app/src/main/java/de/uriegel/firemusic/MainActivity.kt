package de.uriegel.firemusic

import android.content.Intent
import android.os.Bundle
import android.view.KeyEvent
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.preference.PreferenceManager
import androidx.recyclerview.widget.GridLayoutManager
import de.uriegel.activityextensions.ActivityRequest
import de.uriegel.activityextensions.http.*
import de.uriegel.firemusic.databinding.ActivityMainBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.serialization.*
import kotlinx.serialization.json.Json
import java.net.URLEncoder
import java.util.*


class MainActivity : AppCompatActivity(), CoroutineScope {

    override val coroutineContext = Dispatchers.Main

    @Serializable
    data class Contents(val files: Array<String>)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        fun isTV(): Boolean { return android.os.Build.MODEL.contains("AFT") }
        if (isTV())
            setTheme(R.style.Theme_FireMusic)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.albums.layoutManager = GridLayoutManager(this, 6)
        binding.albums.setHasFixedSize(true)

        launch {
            val preferences = PreferenceManager.getDefaultSharedPreferences(this@MainActivity)
            var url = preferences.getString("url", "")
            if (url!!.length < 6) {
                activityRequest.launch(Intent(this@MainActivity, SettingsActivity::class.java))
                url = preferences.getString("url", "")
            }
            urlParts = arrayOf("${url}/music")

            basicAuthentication(preferences.getString("name", "")!!, preferences.getString("auth_pw", "")!!)

            listItems()
        }
    }

    override fun onBackPressed() {
        if (urlParts.size > 1) {
            urlParts = urlParts.toList().dropLast(1).toTypedArray()
            listItems()
        }
        else
            super.onBackPressed()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id: Int = item.getItemId()
        if (id == R.id.menu_settings) {
            showSettings()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if (keyCode == KeyEvent.KEYCODE_MENU)
            showSettings()
        return super.onKeyDown(keyCode, event)
    }

    private fun showSettings() { startActivity(
        Intent(
            this@MainActivity,
            SettingsActivity::class.java
        )
    ) }

    private fun onItemClick(content: String) {
        urlParts += URLEncoder.encode(content, "utf-8")
        listItems()
    }

    private fun listItems() {
        launch {
            try {
                val addr = urlParts.joinToString(separator = "/")
                val result = getString(addr)
                val contents = Json.decodeFromString<Contents>(result).files

                val mp3s = contents.filter { it.toLowerCase(Locale.getDefault()).endsWith(".mp3") }
                if (mp3s.isNotEmpty()) {
                    urlParts = urlParts.toList().dropLast(1).toTypedArray()
                    val intent = Intent(this@MainActivity, AudioActivity::class.java)
                    intent.putExtra("album", mp3s.toTypedArray())
                    intent.putExtra("url", addr)
                    startActivity(intent)
                }
                else
                    binding.albums.adapter = AlbumsAdapter(contents, ::onItemClick)
            } catch (e: Exception) { }
        }
    }

    private val activityRequest = ActivityRequest(this)
    private var urlParts = arrayOf<String>()
    private lateinit var binding: ActivityMainBinding
}