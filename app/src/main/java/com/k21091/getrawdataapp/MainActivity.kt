package com.k21091.getrawdataapp

import android.Manifest
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.WindowManager
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.k21091.getrawdataapp.ui.theme.GetRawDataAppTheme
import pub.devrel.easypermissions.EasyPermissions

var servicerunning = false

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        val permissions = arrayOf(
            Manifest.permission.ACCESS_WIFI_STATE,
            Manifest.permission.BLUETOOTH_ADMIN,
            Manifest.permission.BLUETOOTH,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.BLUETOOTH_SCAN,
            Manifest.permission.BLUETOOTH_ADVERTISE,
            Manifest.permission.NEARBY_WIFI_DEVICES,
            Manifest.permission.BLUETOOTH_CONNECT,
            Manifest.permission.INTERNET,
            Manifest.permission.ACCESS_NETWORK_STATE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.CHANGE_WIFI_STATE,
            Manifest.permission.POST_NOTIFICATIONS,
            Manifest.permission.FOREGROUND_SERVICE_LOCATION
        )

        //許可したいpermissionを許可できるように
        if (!EasyPermissions.hasPermissions(this, *permissions)) {
            EasyPermissions.requestPermissions(this, "パーミッションに関する説明", 1, *permissions)
        }
        setContent {
            GetRawDataAppTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = Color.White
                ) {
                    RawDataServiceControl(this)
                }
            }
        }

    }
}

@Composable
fun RawDataServiceControl(context: Context) {
    var count by remember { getCount }
    var ble by remember { ble_speed }
    fun showToast(context: Context, message: String) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }

    var isServiceRunning by remember { mutableStateOf(false) }
    var title by remember { mutableStateOf("生データ取る蔵") }
    val serviceIntent = Intent(context, GetRAwDataService::class.java)
    var isMenuOpen by remember { mutableStateOf(false) }
    Box(
        modifier = Modifier
            .fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        if (isMenuOpen) {
            Box(
                modifier = Modifier
                    .fillMaxHeight(0.6f)
                    .fillMaxWidth(0.8f)
                    .background(color = Color.LightGray, shape = RoundedCornerShape(15.dp))
            ) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally, // 横方向
                    verticalArrangement = Arrangement.Center // 縦方向
                ) {
                    Box(modifier = Modifier.weight(0.2f))
                    Text(text = "設定", fontSize = 32.sp, modifier = Modifier.weight(0.8f))
                    Text(text = "スキャン回数", fontSize = 25.sp, modifier = Modifier.weight(0.2f))

                    Box(modifier = Modifier.weight(0.2f))
                    SearchTextField(modifier = Modifier
                        .background(
                            color = Color.Gray.copy(alpha = 0.1f),
                            shape = RoundedCornerShape(10.dp)
                        )
                        .border(1.dp, Color.Gray, shape = RoundedCornerShape(10.dp))
                        .fillMaxWidth(0.8f)
                        .weight(0.4f), value = count, onValueChange = {
                        count = it
                        getCount.value = count
                    },
                        textStyle = TextStyle(fontSize = 20.sp)
                    )
                    Box(modifier = Modifier.weight(0.2f))
                    Text(text = "BLEスキャン時間(ms)", fontSize = 25.sp, modifier = Modifier.weight(0.2f))
                    Box(modifier = Modifier.weight(0.2f))

                    SearchTextField(modifier = Modifier
                        .background(
                            color = Color.Gray.copy(alpha = 0.1f),
                            shape = RoundedCornerShape(10.dp)
                        )
                        .border(1.dp, Color.Gray, shape = RoundedCornerShape(10.dp))
                        .fillMaxWidth(0.8f)
                        .weight(0.4f), value = ble, onValueChange = {
                        ble = it
                        ble_speed.value = ble
                    },
                        textStyle = TextStyle(fontSize = 20.sp))
                    Box(modifier = Modifier.weight(0.2f))
                }
            }
        } else {
            Column(
                modifier = Modifier,
                horizontalAlignment = Alignment.CenterHorizontally, // 横方向
                verticalArrangement = Arrangement.Center // 縦方向
            ) {
                Text(text = title, fontSize = 40.sp)
                Spacer(modifier = Modifier.height(16.dp))
                Button(
                    onClick = {
                        isServiceRunning = true
                        servicerunning = true
                        context.startService(serviceIntent)
                        title="生データ取ってる蔵"
                        showToast(context, "サービス開始") // トーストメッセージを表示
                    },
                    enabled = !isServiceRunning,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.Green, // Change the background color of the button when enabled
                        contentColor = Color.White, // Change the text color of the button
                        disabledContainerColor = Color.LightGray,
                        disabledContentColor = Color.White
                    )
                ) {
                    Text(text = "　開始　")
                }

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = {
                        isServiceRunning = false
                        servicerunning = false
                        context.stopService(serviceIntent)
                        title="生データ取る蔵"
                        showToast(context, "サービス終了")
                    },
                    enabled = isServiceRunning,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.Red, // Change the background color of the button when enabled
                        contentColor = Color.White, // Change the text color of the button
                        disabledContainerColor = Color.LightGray,
                        disabledContentColor = Color.White
                    )
                ) {
                    Text(text = "　停止　")
                }
            }
        }
        Button(
            onClick = { isMenuOpen = !isMenuOpen },
            modifier = Modifier
                .padding(top = 15.dp, end = 10.dp)
                .align(Alignment.TopEnd) // Align the button to the top end of the Box
        ) {
            Text(text = "設定")
        }
    }
}
