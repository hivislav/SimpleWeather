package ru.hivislav.simpleweather.view.history

import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import ru.hivislav.simpleweather.R
import ru.hivislav.simpleweather.databinding.FragmentHistoryBinding
import ru.hivislav.simpleweather.model.entities.Weather
import ru.hivislav.simpleweather.model.entities.getDefaultCity
import ru.hivislav.simpleweather.viewmodel.AppStateMain
import ru.hivislav.simpleweather.viewmodel.HistoryViewModel
import ru.hivislav.simpleweather.viewmodel.MainViewModel

class HistoryFragment : Fragment() {

    private var _binding: FragmentHistoryBinding? = null
    private val binding: FragmentHistoryBinding
        get() {
            return _binding!!
        }

    //Инициализируем ViewModel (провайдер возвращает уже имеющуюся, а если ее нет, то создает)
    private val viewModel: HistoryViewModel by lazy {
        ViewModelProvider(this).get(HistoryViewModel::class.java)
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

        viewModel.getLiveData().observe(viewLifecycleOwner, Observer<AppStateMain> { renderData(it) })
        viewModel.getAllHistory()

        binding.historyFragmentRecyclerView.adapter = adapter
        binding.historyFragmentRecyclerView.layoutManager =
            LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        binding.historyFragmentRecyclerView
            .addItemDecoration(DividerItemDecoration(context, LinearLayoutManager.VERTICAL))
    }

    private fun renderData(appState: AppStateMain) {
        with(binding) {
            when (appState) {
                is AppStateMain.Error -> {
                    // TODO
                }
                is AppStateMain.Loading -> {}
                is AppStateMain.Success -> {
                    adapter.setHistoryWeather(appState.weatherData)
                }
            }
        }
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