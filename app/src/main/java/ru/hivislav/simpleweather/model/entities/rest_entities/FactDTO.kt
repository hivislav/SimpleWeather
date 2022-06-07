package ru.hivislav.simpleweather.model.entities.rest_entities

import com.google.gson.annotations.SerializedName

data class FactDTO (
    val temp: Int?,
    @SerializedName("feels_like")
    val feelsLike: Int?,
    val condition: String?,
    val icon: String? = "https://yastatic.net/weather/i/icons/funky/dark/ovc_+ra.svg"
)
