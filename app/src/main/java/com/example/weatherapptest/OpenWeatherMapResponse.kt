package com.example.weatherapptest


import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass


@JsonClass(generateAdapter = true)
data class OpenWeatherMapResponse(
    @Json(name="weather")val weather:List<OpenWeatherMapResponseWeather>,
    @Json(name="main")val main:OpenWeatherMapResponseMain,
    @Json(name="name")val name:String

)


data class OpenWeatherMapResponseWeather(

    @Json(name="description")val description:String
)


data class OpenWeatherMapResponseMain(

    @Json(name="temp")val temp:String,
    @Json(name="temp_max")val temp_max:String,
    @Json(name="temp_min")val temp_min:String

)
