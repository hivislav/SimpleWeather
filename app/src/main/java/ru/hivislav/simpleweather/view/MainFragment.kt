package ru.hivislav.simpleweather.view

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.snackbar.Snackbar
import ru.hivislav.simpleweather.databinding.FragmentMainBinding
import ru.hivislav.simpleweather.viewmodel.AppState
import ru.hivislav.simpleweather.viewmodel.MainViewModel

class MainFragment : Fragment() {

    private var _binding: FragmentMainBinding? = null
    private val binding: FragmentMainBinding
    get() {
        return _binding!!
    }

    private lateinit var viewModel: MainViewModel

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //Инициализируем ViewModel (провайдер возвращает уже имеющуюся, а если ее нет, то создает)
        viewModel = ViewModelProvider(this).get(MainViewModel::class.java)
        //Получаем LiveData и подписываемся на ее изменения
        viewModel.getLiveData().observe(viewLifecycleOwner, Observer<AppState>{renderData(it)})

        viewModel.getWeather()
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = FragmentMainBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    private fun renderData(appState: AppState) {
        when (appState) {
            is AppState.Error -> {
                binding.loadingLayout.visibility = View.GONE
                Snackbar.make(binding.mainView, "Error", Snackbar.LENGTH_LONG)
                    .setAction("Попробовать еще раз") {
                        viewModel.getWeatherFromServer()
                    }.show()
            }
            is AppState.Loading -> {
                binding.loadingLayout.visibility = View.VISIBLE
            }
            is AppState.Success -> {
                binding.loadingLayout.visibility = View.GONE
                binding.cityName.text = appState.weatherData.city.name
                binding.cityCoordinates.text = "${appState.weatherData.city.lat} ${appState.weatherData.city.lon}"
                binding.temperatureValue.text =  "${appState.weatherData.temperature}"
                binding.feelsLikeValue.text =  "${appState.weatherData.feelsLike}"

                Snackbar.make(binding.mainView, "Success", Snackbar.LENGTH_LONG).show()
            }
        }
    }

    companion object {
        fun newInstance() = MainFragment()
        }
}
