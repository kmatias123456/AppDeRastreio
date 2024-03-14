package com.example.androidessncial.service

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.telephony.SmsManager
import com.example.androidessncial.database.DatabaseHelper
import android.util.Log
import android.widget.Toast

@Suppress("DEPRECATION")
class BootReceiver : BroadcastReceiver()
{
    override fun onReceive(context: Context?, intent: Intent?)
    {
        if (intent?.action == "android.intent.action.BOOT_COMPLETED")
        {
            Log.d("BootReceiver", "onReceive: BOOT_COMPLETED recebido")
            senderSmsBoot(context)
        }
    }

    /*
    * A funcao que enviar os dados capturados no banco local
    * Para Numeros especificados no boot complet
    */
    private fun senderSmsBoot(context: Context?)
    {
        val getDateLocation = context?.let { DatabaseHelper(it) }
        val getData = getDateLocation?.getAllItems()
        val get = getData?.last()

        val date = get?.datedmy
        val time = get?.datetime
        val latitude = get?.latitude
        val longitude = get?.longitude

        val googleMapsUrl =
            "http://maps.google.com/maps?q=$latitude,$longitude"
        val locationInfo =
            "Data:$date\nHora:$time"

        val mensagemLinks = listOf("935189465", "950466103", "954078332")
        val smsManager = SmsManager.getDefault()

        for (mensagemLink in mensagemLinks)
        {
            smsManager.sendTextMessage(
                mensagemLink,
                null,
                "$locationInfo\n$googleMapsUrl",
                null,
                null
            )
        }
        Toast.makeText(context, "Test Boot", Toast.LENGTH_SHORT).show()
    }
}