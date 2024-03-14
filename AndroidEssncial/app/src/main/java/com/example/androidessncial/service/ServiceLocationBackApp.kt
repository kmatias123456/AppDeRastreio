package com.example.androidessncial.service

import android.Manifest
import android.annotation.SuppressLint
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.IBinder
import android.widget.Toast
import androidx.core.app.ActivityCompat
import com.example.androidessncial.database.DatabaseHelper
import com.example.androidessncial.server.ApiResponseRetrofit
import com.example.androidessncial.server.ApiSendRetrofit
import com.example.androidessncial.server.InterfaceObjectRetrofit
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import retrofit2.Call
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Suppress("DEPRECATION")
class ServiceLocationBackApp: Service()
{
    override fun onBind(intent: Intent?): IBinder?
    {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int
    {
        abrirAppBack()
        return START_NOT_STICKY
    }

    private lateinit var fusedLocationClient: FusedLocationProviderClient

    private lateinit var locationRequest: LocationRequest

    /*
    * Chamada de atualização da localização
    */
    private val locationCallback = object : LocationCallback()
    {
        override fun onLocationResult(locationResult: LocationResult) {
            super.onLocationResult(locationResult)
            handleLocationResult(locationResult)
        }
    }

    /*
    * Lidar com resultado da localização
    */
    private fun handleLocationResult(locationResult: LocationResult)
    {
        val location = locationResult.lastLocation
        if (location != null)
        {
            val currentTime = location.time
            val datetimeFormat = SimpleDateFormat("HH:mm:ss", Locale.getDefault())
            val datetime = datetimeFormat.format(Date(currentTime))

            val currentDate = System.currentTimeMillis()
            val datedmyFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val datedmy = datedmyFormat.format(Date(currentDate))

            val latitude = location.latitude
            val longitude = location.longitude

            if (isConnectivityAvailable())
            {
                sendLocationToServer(latitude, longitude, datedmy, datetime)
            }
            else
            {
                val insertedData = saveLocationLocaly(
                    latitude, longitude, datedmy, datetime
                )
                if (insertedData != null)
                {
                    showToast("Inseridos Localmente Com Sucesso(1)")
                }
                else
                {
                    // Houve uma falha na inserção dos dados
                    showToast("Falha ao inserir os dados no banco local.")
                }
            }
        }
    }

    /*
    * Enviar os dados da localização para servidor PHP usando retrofit e metodo POST
    */
    private fun sendLocationToServer(
        latitude: Double,
        longitude: Double,
        datedmy: String,
        datetime: String

    )
    {
        val service = InterfaceObjectRetrofit.apiServiceRetrofit

        val request = ApiSendRetrofit(latitude, longitude, datedmy, datetime)

        val call = service.postLocation(request)

        call.enqueue(object : retrofit2.Callback<ApiResponseRetrofit>
        {
            @SuppressLint("SetTextI18n", "ResourceAsColor")
            override fun onResponse(
                call: Call<ApiResponseRetrofit>, response: Response<ApiResponseRetrofit>
            )
            {
                if (response.isSuccessful)
                {
                    showToast("Servidor Respondeu")

                }
                else
                {
                    val insertedData = saveLocationLocaly(
                        latitude, longitude, datedmy, datetime
                    )
                    if (insertedData != null)
                    {
                        showToast("Inseridos Localmente Com Sucesso(2)")
                    }
                    else
                    {
                        // Houve uma falha na inserção dos dados
                        showToast("Falha ao inserir os dados no banco local.")
                    }
                }
            }

            override fun onFailure(
                call: Call<ApiResponseRetrofit>, t: Throwable
            )
            {
                val insertedData = saveLocationLocaly(latitude, longitude, datedmy, datetime)
                if (insertedData != null)
                {
                    showToast("Inseridos Localmente Com Sucesso(3)")
                }
                else
                {
                    // Houve uma falha na inserção dos dados
                    showToast("Falha ao inserir os dados no banco local.")
                }
            }
        })
    }

    //Toast
    private fun showToast(message: String)
    {
        Toast.makeText(
            this, message, Toast.LENGTH_SHORT
        ).show()
    }

    /*
    * Inserir Os Dados Da Localização No Banco De Dados Local
    */
    private fun saveLocationLocaly(
        latitude: Double,
        longitude: Double,
        datedmy: String,
        datetime: String
    ): ApiSendRetrofit?
    {
        val dbHelper = DatabaseHelper(this)
        val apiSendRetrofit = ApiSendRetrofit(latitude, longitude, datedmy, datetime)

        return if (dbHelper.insertItem(apiSendRetrofit))
        {
            apiSendRetrofit
        }
        else
        {
            null
        }
    }

    /*
    * Checar Se As Permissões da localização Foram Concedidas
    */
    private fun checkLocationPermission(): Boolean
    {
        return (ActivityCompat.checkSelfPermission(this,
            Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this,
            Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
        )
    }

    /*
    * Verificar O Estado D Conectividade Dados Moveis e Wi-fi
    */
    private fun isConnectivityAvailable(): Boolean
    {
        val connectivityManager =
            getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        val network = connectivityManager.activeNetwork
        val networkCapabilities = connectivityManager.getNetworkCapabilities(network)

        return networkCapabilities != null &&
                (networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)
                        || networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)
                        || networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET)
                )
    }

    /*
    * Inicializar As Atualizações De Solicitações De Localização
    */
    private fun startLocationUpdates()
    {
        if (checkLocationPermission())
        {
            //Inicialização com base no tempo de cada chamada  2minutos / 10segundos
            fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, null)
        }
    }

    /*
    * App  Em Segundo Plano
    */
    private fun abrirAppBack()
    {
        //Inicializar Os Serviço de Solicitaçõs de Atualizações de localização
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        //Determinar O tempo Das Chamadas De Atualizações
        locationRequest = LocationRequest.create()
            .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
            .setInterval(20000)
            .setFastestInterval(10000)

        if (checkLocationPermission())
        {
            //Inicialização
            startLocationUpdates()
        }
    }
}