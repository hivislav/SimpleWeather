package ru.hivislav.simpleweather.view.history

import android.content.Context
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import ru.hivislav.simpleweather.R
import ru.hivislav.simpleweather.databinding.FragmentHistoryBinding
import ru.hivislav.simpleweather.model.entities.Weather
import ru.hivislav.simpleweather.model.entities.getDefaultCity
import ru.hivislav.simpleweather.view.details.DetailsFragment
import ru.hivislav.simpleweather.view.main.MainFragment
import ru.hivislav.simpleweather.view.main.MainFragmentAdapter
import ru.hivislav.simpleweather.viewmodel.AppStateMain
import ru.hivislav.simpleweather.viewmodel.MainViewModel

class HistoryFragment : Fragment() {

    private var _binding: FragmentHistoryBinding? = null
    private val binding: FragmentHistoryBinding
        get() {
            return _binding!!
        }

    //Инициализируем ViewModel (провайдер возвращает уже имеющуюся, а если ее нет, то создает)
    private val viewModel: MainViewModel by lazy {
        ViewModelProvider(this).get(MainViewModel::class.java)
    }

    private val adapter = HistoryFragmentAdapter()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentHistoryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setHasOptionsMenu(true)

        binding.historyFragmentRecyclerView.adapter = adapter
        binding.historyFragmentRecyclerView
            .addItemDecoration(DividerItemDecoration(context, LinearLayoutManager.HORIZONTAL))
        adapter.setHistoryWeather(listOf(Weather(getDefaultCity(), 10, 15, "", "")))
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    override fun onPrepareOptionsMenu(menu: Menu) {
        menu.findItem(R.id.menu_history).isVisible = false
        super.onPrepareOptionsMenu(menu)
    }

    companion object {
        fun newInstance() = HistoryFragment()
    }
}