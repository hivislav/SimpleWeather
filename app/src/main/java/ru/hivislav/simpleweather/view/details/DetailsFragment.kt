package ru.hivislav.simpleweather.view.details

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import ru.hivislav.simpleweather.R
import ru.hivislav.simpleweather.databinding.FragmentDetailsBinding
import ru.hivislav.simpleweather.model.Weather

class DetailsFragment : Fragment() {

    private var _binding: FragmentDetailsBinding? = null
    private val binding: FragmentDetailsBinding
        get() {
            return _binding!!
        }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //Забираем погоду по ключу
        val weather = arguments?.getParcelable<Weather>(DETAIL_FRAGMENT_BUNDLE_KEY)
        if(weather != null){
            setWeatherData(weather)
        }
    }

    private fun setWeatherData(weather: Weather) {
        binding.cityName.text = weather.city.name
        binding.cityCoordinates.text = "${weather.city.lat} ${weather.city.lon}"
        binding.temperatureValue.text =  "${weather.temperature}"
        binding.feelsLikeValue.text =  "${weather.feelsLike}"
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentDetailsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    companion object {
        const val DETAIL_FRAGMENT_BUNDLE_KEY = "DETAIL_FRAGMENT_BUNDLE_KEY"

        fun newInstance(bundle: Bundle): DetailsFragment{
            val fragment = DetailsFragment()
            fragment.arguments = bundle
            return fragment
        }
    }
}