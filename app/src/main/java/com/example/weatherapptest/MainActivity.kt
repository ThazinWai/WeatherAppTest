package com.example.weatherapptest

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.graphics.drawable.Drawable
import android.location.LocationManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.karumi.dexter.Dexter
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionDeniedResponse
import com.karumi.dexter.listener.PermissionGrantedResponse
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.single.PermissionListener
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.*

class MainActivity : AppCompatActivity() {


    companion object{
        private const val API_KEY = "12531e9aa00b58deb30acb5d04bcfbda"
    }


    private val txtCity by lazy {
        findViewById<TextView>(R.id.txtCity)
    }

    private val txtTemp by lazy {
        findViewById<TextView>(R.id.txtTemp)
    }

    private val txtMaxTemp by lazy {
        findViewById<TextView>(R.id.txtMaxTemp)
    }

    private val txtMinTemp by lazy {
        findViewById<TextView>(R.id.txtMinTemp)
    }

    private val txtDes by lazy {
        findViewById<TextView>(R.id.txtDes)
    }

    private val ivIcon by lazy {
        findViewById<ImageView>(R.id.ivIcon)
    }

    private val editCityName by lazy {
        findViewById<EditText>(R.id.editCityName)
    }

    private val btnSearch by lazy {
        findViewById<Button>(R.id.btnSearch)
    }



    private val retrofit = Retrofit.Builder()
        .baseUrl("https://api.openweathermap.org/data/2.5/")
        .addConverterFactory(GsonConverterFactory.create())
        .client(OkHttpClient())
        .build()



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        //...
        val cal: Calendar = Calendar.getInstance()
        val hour: Int = cal.get(Calendar.HOUR_OF_DAY)
        val isNight = hour < 5 || hour > 18

        val currentDrawable: Int = if (isNight) R.drawable.bg else R.drawable.cbg
        val decorView: View = window.decorView
        val drawable: Drawable? = ContextCompat.getDrawable(this, currentDrawable)
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) decorView.setBackgroundDrawable(
            drawable
        ) else decorView.background = drawable




        //permission using Dexter
        Dexter.withActivity(this)
            .withPermission(Manifest.permission.ACCESS_FINE_LOCATION)
            .withListener(object : PermissionListener {
                override fun onPermissionGranted(p0: PermissionGrantedResponse?) {
                    Log.i("MainActivity.onCreate","Permission Granted!")

                    getLocation()
                }

                override fun onPermissionRationaleShouldBeShown(
                    p0: PermissionRequest?,
                    p1: PermissionToken?
                ) {
                    Log.i("MainActivity.onCreate","Permission Shown!")
                }

                override fun onPermissionDenied(p0: PermissionDeniedResponse?) {
                    Log.i("MainActivity.onCreate","Permission Denied!")
                }

            }).check()

        btnSearch.setOnClickListener{
            val cityName = editCityName.text.toString()
            executeNetworkCall(cityName=cityName)

        }

    }

    @SuppressLint("MissingPermission")
    private fun getLocation(){
        //access user location by GPS provider
        val locationManager = this@MainActivity.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        val location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)
        Log.i("MainActivity.onCreate",location?.latitude.toString())


        executeNetworkCall(latitude = location?.latitude.toString(),longitude = location?.longitude.toString())

//        executeNetworkCall(
//            latitude = location?.latitude.toString(),
//            longitude = location?.longitude.toString()
//        )
    }


    private fun executeNetworkCall(latitude:String, longitude:String){
        val openWeatherMapApi = retrofit.create(OpenWeatherMapApi::class.java)

        openWeatherMapApi.geoCoordinate(latitude=latitude, longitude = longitude, appId = API_KEY,units = "metric")
            .enqueue(object :retrofit2.Callback<OpenWeatherMapResponse>{
                override fun onFailure(call: Call<OpenWeatherMapResponse>, t: Throwable) {

                    t.printStackTrace()

                }

                override fun onResponse(
                    call: Call<OpenWeatherMapResponse>,
                    response: Response<OpenWeatherMapResponse>
                ) {
                    if(response.isSuccessful){
                        response.body()?.
                        let { OpenWeatherMapResponse->
                            Log.i("response",OpenWeatherMapResponse.toString())

//                            val iconUrl=OpenWeatherMapResponse.weather.getOrNull(1)?.icon ?: ""
//                            val fullUrl: String = "https://openweathermap.org/img/wn/$iconUrl@2x.png"

                            showData(
                                cityName = OpenWeatherMapResponse.name,
                                temp = OpenWeatherMapResponse.main.temp,
                                maxTemp = OpenWeatherMapResponse.main.temp_max,
                                minTemp = OpenWeatherMapResponse.main.temp_min,
                                des = OpenWeatherMapResponse.weather.getOrNull(0)?.description ?: ""
                               // weatherIcon = fullUrl
                            )
                        }
                    }
                }

            })
    }

    private fun executeNetworkCall(cityName: String){
        val openWeatherMapApi = retrofit.create(OpenWeatherMapApi::class.java)

        openWeatherMapApi.getByCityName(cityName=cityName, appId = API_KEY,units = "metric")
            .enqueue(object :retrofit2.Callback<OpenWeatherMapResponse>{
                override fun onFailure(call: Call<OpenWeatherMapResponse>, t: Throwable) {

                    t.printStackTrace()

                }

                override fun onResponse(
                    call: Call<OpenWeatherMapResponse>,
                    response: Response<OpenWeatherMapResponse>
                ) {
                    if(response.isSuccessful){
                        response.body()?.
                        let { OpenWeatherMapResponse->
                            Log.i("response",OpenWeatherMapResponse.toString())

                            //val iconUrl=OpenWeatherMapResponse.weather.getOrNull(1)?.icon ?: ""
                          //  var fullUrl = "https://openweathermap.org/img/wn/$iconUrl@2x.png"

                            showData(
                                cityName = OpenWeatherMapResponse.name,
                                temp = OpenWeatherMapResponse.main.temp,
                                maxTemp = OpenWeatherMapResponse.main.temp_max,
                                minTemp = OpenWeatherMapResponse.main.temp_min,
                                des = OpenWeatherMapResponse.weather.getOrNull(0)?.description ?: ""
                               // weatherIcon =fullUrl
                            )
                        }
                    }
                }

            })
    }



    private fun showData(
        cityName:String,
        temp:String,
        maxTemp:String,
        minTemp:String,
        des:String
    ) {
        txtCity.text=cityName
        txtTemp.text="$temp"
        txtMaxTemp.text="$maxTemp°"
        txtMinTemp.text= "$minTemp°"
        txtDes.text=des

        editCityName.setText(cityName)

//        Glide.with(this)
//            .load(weatherIcon)
//            .into(ivIcon)

        //Picasso.get().load(weatherIcon).into(ivIcon)
    }


}