package de.uriegel.firemusic

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.preference.PreferenceManager
import androidx.recyclerview.widget.GridLayoutManager
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json

class MainActivity : ActivityEx(), CoroutineScope {

    override val coroutineContext = Dispatchers.Main

    @Serializable
    data class Contents(val files: Array<String>)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        albums.layoutManager = GridLayoutManager(this, 6)
        albums.setHasFixedSize(true)

        launch {
            val preferences = PreferenceManager.getDefaultSharedPreferences(this@MainActivity)
            var url = preferences.getString("url", "")
            if (url!!.length < 6) {
                activityRequest(Intent(this@MainActivity, SettingsActivity::class.java))
                url = preferences.getString("url", "")
            }
            urlParts = arrayOf("${url}/music")
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

    private fun onItemClick(content: String) {
        urlParts += content
        listItems()
//                val intent = Intent(this@MainActivity, PlayerActivity::class.java)
//                intent.putExtra("film", film)
//                startActivity(intent)
    }

    private fun listItems() {
        launch {
            try {
                val addr = urlParts.joinToString(separator = "/")
                val result = httpGet(addr)
                val contents = Json.decodeFromString<Contents>(result).files

                val mp3s = contents.filter { it.toLowerCase().endsWith(".mp3") }
                if (mp3s.isNotEmpty()) {
                    val intent = Intent(this@MainActivity, AudioActivity::class.java)
                    intent.putExtra("album", mp3s.toTypedArray())
                    intent.putExtra("url", addr)
                    startActivity(intent)
                }
                else
                    albums.adapter = AlbumsAdapter(contents, ::onItemClick)
            } catch (e: Exception) { }
        }
    }

    private var urlParts = arrayOf<String>()
}