package com.example.androidessncial.service

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.telephony.TelephonyManager
import android.widget.Toast
import androidx.annotation.RequiresApi

class SimStateReceiver : BroadcastReceiver() {

    @RequiresApi(Build.VERSION_CODES.R)
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == TelephonyManager.ACTION_MULTI_SIM_CONFIG_CHANGED) {
            val simState = intent.getStringExtra(
                TelephonyManager.EXTRA_ACTIVE_SIM_SUPPORTED_COUNT
            )
            if (simState != null) {
                when (simState) {
                    TelephonyManager.SIM_STATE_ABSENT.toString() -> {
                        /*
                        * O cartão SIM foi removido
                        */
                        //Log.d("SimStateReceiver", "SIM card removed")
                        showToast("Sim Removido", context)
                    }
                    TelephonyManager.SIM_STATE_READY.toString() -> {
                        /*
                        * O cartão SIM está pronto para uso
                        */
                        //Log.d("SimStateReceiver", "SIM card ready")
                        showToast("Sim Pronto", context)
                    }
                    // Outros estados do cartão SIM podem ser tratados conforme necessário
                }
            }
        }
    }

    //Toast
    private fun showToast(message: String, context: Context) {
        Toast.makeText(
            context, message, Toast.LENGTH_SHORT
        ).show()
    }
}