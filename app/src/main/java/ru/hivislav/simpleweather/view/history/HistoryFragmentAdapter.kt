package ru.hivislav.simpleweather.view.history

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import ru.hivislav.simpleweather.R
import ru.hivislav.simpleweather.model.entities.Weather
import ru.hivislav.simpleweather.utils.YANDEX_ICON_URL
import ru.hivislav.simpleweather.utils.loadUrl

class HistoryFragmentAdapter: RecyclerView.Adapter<HistoryFragmentAdapter.MainViewHolder>() {

    private var weatherHistoryData: List<Weather> = listOf()

    fun setHistoryWeather(data: List<Weather>) {
        this.weatherHistoryData = data
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MainViewHolder {
        return MainViewHolder(LayoutInflater.from(parent.context).inflate
            (R.layout.recycler_history_city_item, parent, false))
    }

    override fun onBindViewHolder(holder: MainViewHolder, position: Int) {
        holder.bind(this.weatherHistoryData[position])
    }

    override fun getItemCount(): Int {
        return weatherHistoryData.size
    }

    inner class MainViewHolder(view: View) : RecyclerView.ViewHolder(view){
        fun bind(weather: Weather) {
            itemView.findViewById<TextView>(R.id.history_city_name).text = weather.city.name
            itemView.findViewById<TextView>(R.id.history_city_temperature).text = weather.temperature.toString()
            itemView.findViewById<TextView>(R.id.history_city_feels_like).text = weather.feelsLike.toString()
            itemView.findViewById<ImageView>(R.id.history_icon_weather_condition)
                .loadUrl(weather.icon)
        }
    }
}