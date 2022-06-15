package ru.hivislav.simpleweather.view.main

import android.content.Context
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.snackbar.Snackbar
import ru.hivislav.simpleweather.R
import ru.hivislav.simpleweather.databinding.FragmentMainBinding
import ru.hivislav.simpleweather.model.entities.Weather
import ru.hivislav.simpleweather.view.details.DetailsFragment
import ru.hivislav.simpleweather.view.history.HistoryFragment
import ru.hivislav.simpleweather.viewmodel.AppStateMain
import ru.hivislav.simpleweather.viewmodel.MainViewModel

class MainFragment : Fragment(), OnItemClickListener {

    private var _binding: FragmentMainBinding? = null
    private val binding: FragmentMainBinding
    get() {
        return _binding!!
    }

    private var isRussian = true

    //Инициализируем ViewModel (провайдер возвращает уже имеющуюся, а если ее нет, то создает)
    private val viewModel: MainViewModel by lazy {
        ViewModelProvider(this).get(MainViewModel::class.java)
    }

    private val mainAdapter = MainFragmentAdapter(this)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentMainBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initView()
        //Получаем LiveData и подписываемся на ее изменения
        viewModel.getLiveData().observe(viewLifecycleOwner, Observer<AppStateMain>{renderData(it)})

        binding.mainFragmentFAB.setOnClickListener {
            sendRequestForChangeCitiesList()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    private fun initView() {
        setHasOptionsMenu(true)
        binding.mainFragmentRecyclerView.adapter = mainAdapter
        loadChooseCitiesList()
    }

    private fun renderData(appStateMain: AppStateMain) {
        when (appStateMain) {
            is AppStateMain.Error -> {
                binding.mainFragmentLoadingLayout.visibility = View.GONE
                Snackbar.make(binding.root, "Error", Snackbar.LENGTH_LONG)
                    .setAction("Попробовать еще раз") {
                        sendRequestForChangeCitiesList()
                    }.show()
            }
            is AppStateMain.Loading -> {
                binding.mainFragmentLoadingLayout.visibility = View.VISIBLE
            }
            is AppStateMain.Success -> {
                binding.mainFragmentLoadingLayout.visibility = View.GONE
                mainAdapter.setWeather(appStateMain.weatherData)
                Snackbar.make(binding.root, "Success", Snackbar.LENGTH_SHORT).show()
            }
        }
    }

    private fun sendRequestForChangeCitiesList() {
        isRussian = !isRussian
        if (isRussian) {
            viewModel.getWeatherFromLocalSourceRus()
            binding.mainFragmentFAB.setImageResource(R.drawable.ic_russia)
        } else {
            viewModel.getWeatherFromLocalSourceWorld()
            binding.mainFragmentFAB.setImageResource(R.drawable.ic_earth)
        }
        saveChooseCitiesList()
    }

    private fun loadChooseCitiesList() {
        activity?.let {
            isRussian = activity
                ?.getPreferences(Context.MODE_PRIVATE)
                ?.getBoolean(IS_RUSSIAN_CITIES_LIST, true)
                ?: true
        }
        if (isRussian) {
            viewModel.getWeatherFromLocalSourceRus()
            binding.mainFragmentFAB.setImageResource(R.drawable.ic_russia)
        } else {
            viewModel.getWeatherFromLocalSourceWorld()
            binding.mainFragmentFAB.setImageResource(R.drawable.ic_earth)
        }
    }

    private fun saveChooseCitiesList() {
        val sharedPreferences = activity?.getPreferences(Context.MODE_PRIVATE)?.edit()
        sharedPreferences?.putBoolean(IS_RUSSIAN_CITIES_LIST, isRussian)
        sharedPreferences?.apply()
    }

    //по клику парсим погоду и показываем детальный экран
    override fun onItemClick(weather: Weather) {
        val bundle = Bundle()
        bundle.putParcelable(DetailsFragment.DETAIL_FRAGMENT_BUNDLE_KEY, weather)
        requireActivity().supportFragmentManager.beginTransaction()
            .add(R.id.container, DetailsFragment.newInstance(bundle))
            .addToBackStack("").commit()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.main_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when(item.itemId) {
            R.id.menu_history -> {
                requireActivity().supportFragmentManager.beginTransaction()
                    .add(R.id.container, HistoryFragment.newInstance())
                    .addToBackStack("").commit()
                true
            }
            else -> {false}
        }
    }

    companion object {
        const val IS_RUSSIAN_CITIES_LIST = "isRussianCitiesList"

        fun newInstance() = MainFragment()
    }
}
