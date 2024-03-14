@file:Suppress("DEPRECATION")

package com.example.androidessncial.service

import android.app.Service
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import android.os.IBinder
import android.telephony.TelephonyManager
import android.widget.Toast
import com.example.androidessncial.notifications.SmsServiceLocationBack

@Suppress("DEPRECATION")
class PhoneState : BroadcastReceiver()
{
    override fun onReceive(context: Context?, intent: Intent?)
    {
        if (intent?.action == TelephonyManager.ACTION_PHONE_STATE_CHANGED)
        {
            when (intent.getStringExtra(TelephonyManager.EXTRA_STATE))
            {
                /*
                * O telefone está tocando (chamada recebida)
                */
                TelephonyManager.EXTRA_STATE_RINGING -> {
                    val incomingNumber = intent.getStringExtra(
                        TelephonyManager.EXTRA_INCOMING_NUMBER
                    )
                    Toast.makeText(
                        context,
                        "Chamada Recebida de: $incomingNumber",
                        Toast.LENGTH_SHORT
                    ).show()

                }

                /*
                * A chamada está ativa
                */
                TelephonyManager.EXTRA_STATE_OFFHOOK -> {
                    Toast.makeText(
                        context,
                        "Chamada Ativa",
                        Toast.LENGTH_SHORT
                    ).show()
                }

                /*
                * A chamada está encerrada
                */
                TelephonyManager.EXTRA_STATE_IDLE -> {
                    Toast.makeText(
                        context,
                        "Caleb Chamada Encerrada",
                        Toast.LENGTH_SHORT
                    ).show()
                    Intent(context, SmsServiceLocationBack::class.java).also {

                        it.action = SmsServiceLocationBack.Actions.START.toString()
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
                        {
                            context?.startForegroundService(it)
                        }
                    }
                }
            }
        }
    }

    class PhoneService : Service()
    {
        private val receiver = PhoneState()

        override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int
        {
            val intentFilter = IntentFilter(
                TelephonyManager.ACTION_PHONE_STATE_CHANGED
            )

            val receiver = PhoneState()
            registerReceiver(receiver, intentFilter)
            return START_STICKY
        }

        override fun onDestroy()
        {
            super.onDestroy()
            unregisterReceiver(receiver)
        }

        override fun onBind(intent: Intent?): IBinder?
        {
            return null
        }
    }
}