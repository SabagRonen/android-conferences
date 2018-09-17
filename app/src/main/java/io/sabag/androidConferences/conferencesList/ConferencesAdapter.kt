package io.sabag.androidConferences.conferencesList

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import io.sabag.androidConferences.ConferenceState

class ConferencesAdapter(var conferencesList: List<ConferenceState>) : RecyclerView.Adapter<ConferencesViewHolder>(){
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ConferencesViewHolder {
        return ConferencesViewHolder.create(parent)
    }

    override fun onBindViewHolder(holder: ConferencesViewHolder, position: Int) {
        holder.bind(conferencesList[position])
    }

    override fun getItemCount(): Int {
        return conferencesList.size
    }

}