package com.example.androidessncial.main

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.androidessncial.R
import com.example.androidessncial.ui.theme.AndroidEssêncialTheme
import com.example.androidessncial.ui.theme.Green40
import com.example.androidessncial.ui.theme.Preto
import com.example.androidessncial.ui.theme.Purple40

@Suppress("DEPRECATION")
class MainActivity : ComponentActivity()
{
    private val REQUEST_BACKGROUND_PERMISSION = 123
    override fun onStart()
    {
        super.onStart()
        verificarPermissoes()
    }

    private fun verificarPermissoes()
    {
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.REQUEST_IGNORE_BATTERY_OPTIMIZATIONS
            ) != PackageManager.PERMISSION_GRANTED
        )
        {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.REQUEST_IGNORE_BATTERY_OPTIMIZATIONS),
                REQUEST_BACKGROUND_PERMISSION
            )
        }
    }

    @Deprecated("Deprecated in Java")
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    )
    {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_BACKGROUND_PERMISSION)
        {
            /*
            * Verificar se a permissão foi concedida.
            */
            if (
                grantResults.isNotEmpty() && grantResults[0] ==
                    PackageManager.PERMISSION_GRANTED
            )
            {
                /*
                * Permissão concedida, você pode iniciar tarefas em segundo plano.
                */
            }
            else
            {
                /*
                * Permissão negada, informando ao usuário sobre a importância da permissão
                * Ou direcionando-o para as configurações para conceder a permissão manualmente
                */
                mostrarDialogoPermissaoNegada()
            }
        }
    }

    private fun mostrarDialogoPermissaoNegada()
    {
        /*
        * Implementar um diálogo para explicar a importância da permissão e direcione
        * O usuário para as configurações do aplicativo.
        */
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    private fun pedirPermissoes()
    {
        ActivityCompat.requestPermissions(this,
            arrayOf(
                Manifest.permission.SEND_SMS,
                Manifest.permission.RECEIVE_SMS,
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.POST_NOTIFICATIONS,
                Manifest.permission.READ_PHONE_STATE
            ), 1
        )
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        /*
        * Pedindo Permissões Necessaria Para Funcionamento Correto Do App
        */
        pedirPermissoes()

        setContent {
            AndroidEssêncialTheme {
                /*
                * Interface Do App.
                */
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = Purple40
                ) {
                    Greeting()
                }
            }
        }
    }
}

@Composable
fun Greeting() {
   Column (
       modifier = Modifier
           .fillMaxSize()
           .padding(16.dp),
       verticalArrangement = Arrangement.Center,
   )
    {
        Image(
            painter = painterResource(R.drawable.ic_android_black_24dp),
            contentDescription = "Contact profile picture",
            modifier = Modifier
                .size(60.dp)
                .clip(CircleShape)
        )
        val customTextStyle = LocalTextStyle.current.copy(
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = "Sejá Bem Vindo Ao Android!",
            color = Preto,
            style = customTextStyle
        )
        Spacer(modifier = Modifier.width(16.dp))
        val customTextStyle1 = LocalTextStyle.current.copy(
            fontSize = 14.sp,
            fontWeight = FontWeight.Normal
        )
        Text(
            text = "Este App foi projetado para melhorar o desepenho " +
                    "\nDo seu dispositivo android, por favor não o desinstale.",
            color = Green40,
            style = customTextStyle1
        )
   }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview()
{
    AndroidEssêncialTheme {
        Greeting()
    }
}