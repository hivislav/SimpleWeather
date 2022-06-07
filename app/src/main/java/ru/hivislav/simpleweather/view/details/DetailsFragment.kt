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
import ru.hivislav.simpleweather.viewmodel.AppStateDetails
import ru.hivislav.simpleweather.viewmodel.AppStateMain
import ru.hivislav.simpleweather.viewmodel.DetailsViewModel

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

    private lateinit var localWeather: Weather

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentDetailsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //Забираем погоду по ключу
        arguments?.getParcelable<Weather>(DETAIL_FRAGMENT_BUNDLE_KEY)?.let {
                localWeather = it

                setStaticWeatherData(it)

                viewModel.getLiveData().observe(viewLifecycleOwner,
                            Observer<AppStateDetails> { appStateDetails: AppStateDetails ->  setDynamicWeatherData(appStateDetails)})
                viewModel.getWeatherFromRemoteServer(it.city.lat, it.city.lon)
        }
    }

    private fun setStaticWeatherData(weather: Weather) {
        with(binding) {
        cityName.text = weather.city.name
        cityCoordinates.text = "${weather.city.lat} ${weather.city.lon}"
        }
    }

    private fun setDynamicWeatherData(appStateDetails: AppStateDetails) = with(binding) {
        when (appStateDetails) {
            is AppStateDetails.Error -> {
                mainView.visibility = View.INVISIBLE
                loadingLayout.visibility = View.GONE
                Snackbar.make(binding.root, appStateDetails.error.toString(), Snackbar.LENGTH_LONG)
                    .setAction("Попробовать еще раз") {
                        viewModel.getWeatherFromRemoteServer(localWeather.city.lat, localWeather.city.lon)
                    }.show()
            }
            is AppStateDetails.Loading -> {
                mainView.visibility = View.GONE
                loadingLayout.visibility = View.VISIBLE
            }
            is AppStateDetails.Success -> {
                loadingLayout.visibility = View.GONE
                mainView.visibility = View.VISIBLE
                temperatureValue.text = appStateDetails.weatherDTO.fact.temp.toString()
                feelsLikeValue.text = appStateDetails.weatherDTO.fact.feelsLike.toString()
                conditionValue.text =
                    getConditionOnRus(appStateDetails.weatherDTO.fact.condition.toString())
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