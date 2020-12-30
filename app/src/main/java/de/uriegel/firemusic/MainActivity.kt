package de.uriegel.firemusic

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.preference.PreferenceManager
import androidx.recyclerview.widget.GridLayoutManager
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
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
            MainActivity.url = url!!

            fun onItemClick(film: String) {
//                val intent = Intent(this@MainActivity, PlayerActivity::class.java)
//                intent.putExtra("film", film)
//                startActivity(intent)
            }

            try {
                val result = httpGet("${MainActivity.url}/music")
                val files = Json.decodeFromString<Contents>(result).files
                albums.adapter = AlbumsAdapter(files, ::onItemClick)
            } catch (e: Exception) { }
        }
    }

    companion object {
        lateinit var url: String
    }
}