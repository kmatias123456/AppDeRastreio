package com.example.androidessncial.service

import android.annotation.SuppressLint
import android.app.Service
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import android.os.IBinder
import android.provider.Telephony
import android.telephony.SmsManager
import androidx.annotation.RequiresApi
import com.example.androidessncial.database.DatabaseHelper
import com.example.androidessncial.notifications.SmsServiceLocationBack

class SmsReceiverTeste : BroadcastReceiver()
{
    @RequiresApi(Build.VERSION_CODES.O)
    @SuppressLint("UnsafeProtectedBroadcastReceiver")
    override fun onReceive(context: Context, intent: Intent)
    {
        /*
        * A funcao que limpa o banco local.
        */
        fun deleteLocationDataBase()
        {
            val dbHelper = DatabaseHelper(context)
            dbHelper.deleteAllItems()
        }

        /*
        * A funcao que incializa a captura de localizacao em tempo determido.
        */
        fun startLocation()
        {
            Intent(context, SmsServiceLocationBack::class.java).also {
                it.action = SmsServiceLocationBack.Actions.START.toString()
                context.startForegroundService(it)
                //context.startService(it)
            }
        }

        /*
        * A funcao que interrompe a incializacao da captura de localizacao
        */
        fun stopLocation()
        {
            Intent(context, SmsServiceLocationBack::class.java).also {
                it.action = SmsServiceLocationBack.Actions.STOP.toString()
                context.startForegroundService(it)
                //context.startService(it)
            }
        }

        /*
        * A funcao que envia a ultima localizacao do dispositivo
        * Guardada no banco de dados local, com link para Google Maps.
        */
        fun senderPostion(address: String, smsBody: String)
        {
            val getDateLocation = DatabaseHelper(context)
            val getData = getDateLocation.getAllItems()
            val get = getData.last()

            val date = get.datedmy
            val time = get.datetime
            val latitude = get.latitude
            val longitude = get.longitude

            val googleMapsUrl =
                "http://maps.google.com/maps?q=$latitude,$longitude"
            val locationInfo =
                "Data:$date\nHora:$time"

            //val testAddress = "244944790744"
            @Suppress("DEPRECATION") val smsManager = SmsManager.getDefault()

            smsManager.sendTextMessage(
                address,
                null,
                "$locationInfo\n$googleMapsUrl",
                null,
                null
            )
        }

        val messages = Telephony.Sms.Intents.getMessagesFromIntent(intent)

        for (sms in messages)
        {
            val address = sms.displayOriginatingAddress
            val smsBody = sms.displayMessageBody

            when (smsBody)
            {
                "liga..." -> startLocation()
                "tudo bem..." -> stopLocation()
                "fatorylocatio..." -> deleteLocationDataBase()
                "liga só..." -> senderPostion(address, smsBody)
            }
        }
    }
}

/*
* A classe que incializa a promessa em segundo plano
*/
class SmsService : Service()
{
    override fun onBind(intent: Intent?): IBinder?
    {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int
    {
        val intentFilter = IntentFilter("android.provider.Telephony.SMS_RECEIVED")
        val receiver = SmsReceiverTeste()
        registerReceiver(receiver, intentFilter)
        return START_STICKY
    }
}