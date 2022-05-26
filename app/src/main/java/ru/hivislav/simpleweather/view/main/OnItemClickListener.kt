package ru.hivislav.simpleweather.view.main

import ru.hivislav.simpleweather.model.Weather

interface OnItemClickListener {
    fun onItemClick(weather: Weather)
}