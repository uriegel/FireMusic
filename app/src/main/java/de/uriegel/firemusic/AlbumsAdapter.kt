package de.uriegel.firemusic

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class AlbumsAdapter(private val films: Array<String>, private val clickListener: ((track: String)->Unit)) : RecyclerView.Adapter<AlbumsAdapter.ViewHolder>() {

    override fun getItemCount(): Int {
        return films.count()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.album, parent, false)
        return ViewHolder(v, clickListener)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.album = films[position]
        holder.videoNameView.text = holder.album
        holder.videoNameView.setOnClickListener { holder.clickListener(holder.album) }
    }

    class ViewHolder(view: View, val clickListener: (album: String)->Unit) : RecyclerView.ViewHolder(view) {
        init {
            view.setOnClickListener {clickListener(album) }
        }
        var album = ""
        val videoNameView: TextView = view.findViewById(R.id.albumNameView)
    }
}