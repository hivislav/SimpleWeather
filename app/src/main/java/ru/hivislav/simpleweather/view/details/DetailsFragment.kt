package ru.hivislav.simpleweather.view.details

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.snackbar.Snackbar
import ru.hivislav.simpleweather.databinding.FragmentDetailsBinding
import ru.hivislav.simpleweather.model.entities.Weather
import ru.hivislav.simpleweather.model.entities.getConditionOnRus
import ru.hivislav.simpleweather.viewmodel.AppState
import ru.hivislav.simpleweather.viewmodel.DetailsViewModel
import java.util.*

class DetailsFragment : Fragment() {

    private var _binding: FragmentDetailsBinding? = null
    private val binding: FragmentDetailsBinding
        get() {
            return _binding!!
        }

    //Инициализируем ViewModel (провайдер возвращает уже имеющуюся, а если ее нет, то создает)
    private val viewModel: DetailsViewModel by lazy {
        ViewModelProvider(this).get(DetailsViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentDetailsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //Забираем погоду по ключу
        arguments?.getParcelable<Weather>(DETAIL_FRAGMENT_BUNDLE_KEY)?.let {
                setStaticWeatherData(it)

                viewModel.getLiveData().observe(viewLifecycleOwner,
                            Observer<AppState> {appState: AppState ->  setDynamicWeatherData(appState)})
                viewModel.loadData(it.city.lat, it.city.lon)
        }
    }

    private fun setStaticWeatherData(weather: Weather) {
        with(binding) {
        cityName.text = weather.city.name
        cityCoordinates.text = "${weather.city.lat} ${weather.city.lon}"
        }
    }

    private fun setDynamicWeatherData(appState: AppState) = with(binding) {
        when (appState) {
            is AppState.Error -> {
                mainView.visibility = View.INVISIBLE
                loadingLayout.visibility = View.GONE
                Snackbar.make(binding.root, "Error", Snackbar.LENGTH_LONG)
                    .setAction("Попробовать еще раз") {
                        viewModel.loadData(appState.weatherData[0].city.lat,
                                            appState.weatherData[0].city.lon)
                    }.show()
            }
            is AppState.Loading -> {
                mainView.visibility = View.GONE
                loadingLayout.visibility = View.VISIBLE
            }
            is AppState.Success -> {
                loadingLayout.visibility = View.GONE
                mainView.visibility = View.VISIBLE
                temperatureValue.text = appState.weatherData[0].temperature.toString()
                feelsLikeValue.text = appState.weatherData[0].feelsLike.toString()
                conditionValue.text =
                    getConditionOnRus(appState.weatherData[0].condition.toString())
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    companion object {
        const val DETAIL_FRAGMENT_BUNDLE_KEY = "DETAIL_FRAGMENT_BUNDLE_KEY"

        fun newInstance(bundle: Bundle) = DetailsFragment().apply { arguments = bundle }
    }
}