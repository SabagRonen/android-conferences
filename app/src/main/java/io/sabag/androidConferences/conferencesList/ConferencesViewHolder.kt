package io.sabag.androidConferences.conferencesList

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import io.sabag.androidConferences.ConferenceState
import io.sabag.androidConferences.R

class ConferencesViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    val title: TextView by lazy {
        itemView.findViewById<TextView>(R.id.title)
    }

    val subtitle: TextView by lazy {
        itemView.findViewById<TextView>(R.id.subtitle)
    }

    val info: TextView by lazy {
        itemView.findViewById<TextView>(R.id.info)
    }

    companion object {
        fun create(parent: ViewGroup) : ConferencesViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.item_conferences_overview, parent, false)
            return ConferencesViewHolder(view)
        }
    }

    fun bind(conferenceState: ConferenceState) {
        title.text = conferenceState.title
        subtitle.text = conferenceState.location
        info.text = conferenceState.info
    }
}