package io.sabag.androidConferences.conferencesList

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import dagger.android.support.AndroidSupportInjection
import io.sabag.androidConferences.R
import kotlinx.android.synthetic.main.fragment_conferences_list.*
import javax.inject.Inject


class ConferencesListFragment : Fragment() {

    @Inject lateinit var presenter: IConferencesListPresenter

    override fun onAttach(context: Context?) {
        AndroidSupportInjection.inject(this)
        super.onAttach(context)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_conferences_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val conferencesAdapter = ConferencesAdapter(emptyList())
        conferencesList.adapter = conferencesAdapter
        conferencesList.layoutManager = LinearLayoutManager(context)
        presenter.observeConferencesState(lifecycle) {
            conferencesAdapter.conferencesList = it
            conferencesAdapter.notifyDataSetChanged()
        }
        bottomNav.setOnNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.pastButton -> presenter.pastButtonClicked()
                R.id.upcomingButton -> presenter.upcomingButtonClicked()
                R.id.moreButton -> presenter.moreButtonClicked()
            }
            true
        }
        presenter.loadData()
    }
}

