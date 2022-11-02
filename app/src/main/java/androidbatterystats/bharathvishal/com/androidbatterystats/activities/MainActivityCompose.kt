package androidbatterystats.bharathvishal.com.androidbatterystats.activities

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.BatteryManager
import android.os.Build
import android.os.Bundle
import androidbatterystats.bharathvishal.com.androidbatterystats.R
import androidbatterystats.bharathvishal.com.androidbatterystats.constants.Constants
import androidbatterystats.bharathvishal.com.androidbatterystats.theme.Material3AppTheme
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.google.android.material.color.DynamicColors

class MainActivityCompose : ComponentActivity() {
    private lateinit var activityContext: Context

    private var batteryLevelIntVal = mutableStateOf(100)
    private var batteryLevelStringVal = mutableStateOf("100%")
    private var batteryTypeVal = mutableStateOf("-")
    private var batteryTempVal = mutableStateOf("-")
    private var batteryPowerSourceVal = mutableStateOf("Unknown")
    private var batteryVoltageVal = mutableStateOf("-")
    private var batteryStatusVal = mutableStateOf("-")
    private var batteryHealthVal = mutableStateOf("-")
    private var batteryFastChargingVal = mutableStateOf("-")

    private val batteryStatsReceiver = object : BroadcastReceiver() {
        @SuppressLint("SetTextI18n")
        override fun onReceive(context: Context, intent: Intent) {

            if (Build.VERSION.SDK_INT >= 28) {
                val mBatteryManager =
                    context.getSystemService(Context.BATTERY_SERVICE) as BatteryManager
                mBatteryManager.computeChargeTimeRemaining()
            }


            val isPresent = intent.getBooleanExtra("present", false)
            val technology = intent.getStringExtra("technology")
            val plugged = intent.getIntExtra("plugged", -1)
            val scale = intent.getIntExtra("scale", -1)
            val health = intent.getIntExtra("health", 0)
            val status = intent.getIntExtra("status", 0)
            val rawLevel = intent.getIntExtra("level", -1)
            val voltage = intent.getIntExtra("voltage", 0)
            val fastChargeStatus = intent.getBooleanExtra("fastcharge_status", false)
            val temperature = intent.getIntExtra("temperature", 0)
            var level = 0

            if (isPresent) {
                if (rawLevel >= 0 && scale > 0) {
                    level = rawLevel * 100 / scale
                }

                batteryLevelIntVal.value = level
                batteryLevelStringVal.value = (+level).toString() + "%"
                if (technology != null)
                    batteryTypeVal.value = technology
                else
                    batteryTypeVal.value = "Unknown"

                batteryPowerSourceVal.value = getPlugTypeResultString(plugged)
                batteryVoltageVal.value = Constants.STRING_EMPTY + voltage / 1000.0f + " V"
                batteryStatusVal.value = getStatusResultString(status)
                batteryHealthVal.value = getHealthResultString(health)
                batteryFastChargingVal.value = Constants.STRING_EMPTY + fastChargeStatus

                val tempInCelsius = temperature / 10.0f
                val tempInFahrenheit = 9 / 5 * tempInCelsius + 32.0f

                batteryTempVal.value =
                    Constants.STRING_EMPTY + tempInCelsius + " C / " + tempInFahrenheit + " F"
            } else {
                batteryLevelStringVal.value = Constants.SYMBOL_HYPHEN
                batteryTypeVal.value = Constants.SYMBOL_HYPHEN
                batteryPowerSourceVal.value = Constants.SYMBOL_HYPHEN
                batteryTempVal.value = Constants.SYMBOL_HYPHEN
                batteryVoltageVal.value = Constants.SYMBOL_HYPHEN
                batteryStatusVal.value = Constants.SYMBOL_HYPHEN
                batteryHealthVal.value = Constants.SYMBOL_HYPHEN
                batteryFastChargingVal.value = Constants.SYMBOL_HYPHEN
            }
        }
    }


    private fun getPlugTypeResultString(plugged: Int): String {
        var plugType = Constants.UNKNOWN_VALUE
        when (plugged) {
            BatteryManager.BATTERY_PLUGGED_AC -> plugType = Constants.AC
            BatteryManager.BATTERY_PLUGGED_USB -> plugType = Constants.USB
        }
        return plugType
    }


    private fun getHealthResultString(health: Int): String {
        var healthString = Constants.UNKNOWN_VALUE

        when (health) {
            BatteryManager.BATTERY_HEALTH_DEAD -> healthString = Constants.DEAD
            BatteryManager.BATTERY_HEALTH_GOOD -> healthString = Constants.GOOD
            BatteryManager.BATTERY_HEALTH_OVER_VOLTAGE -> healthString = Constants.OVERVOLTAGE
            BatteryManager.BATTERY_HEALTH_OVERHEAT -> healthString = Constants.OVERHEAT
            BatteryManager.BATTERY_HEALTH_UNSPECIFIED_FAILURE -> healthString =
                Constants.FAILURE
        }
        return healthString
    }


    private fun getStatusResultString(status: Int): String {
        var statusString = Constants.UNKNOWN_VALUE

        when (status) {
            BatteryManager.BATTERY_STATUS_CHARGING -> statusString = Constants.CHARGING
            BatteryManager.BATTERY_STATUS_DISCHARGING -> statusString = Constants.DISCHARGING
            BatteryManager.BATTERY_STATUS_FULL -> statusString = Constants.FULL
            BatteryManager.BATTERY_STATUS_NOT_CHARGING -> statusString = Constants.NOT_CHARGING
        }
        return statusString
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //Applies Material dynamic theming
        try {
            DynamicColors.applyToActivityIfAvailable(this)
        } catch (e: Exception) {
            e.printStackTrace()
        }

        activityContext = this

        setContent {
            Material3AppTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.surface
                ) {
                    MainViewImplementation()
                }
            }
        }

        val filter = IntentFilter(Intent.ACTION_BATTERY_CHANGED)
        activityContext.registerReceiver(batteryStatsReceiver, filter)
    }

    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
    @Composable
    fun MainViewImplementation() {
        Column {
            TopAppBarMain()
            CardViewMain()
        }
    }

    //Top App bar composable function
    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun TopAppBarMain() {
        TopAppBar(
            title = { Text("Android Battery Stats  - Compose Activity") },
            colors = TopAppBarDefaults.smallTopAppBarColors(
                titleContentColor = MaterialTheme.colorScheme.onSurfaceVariant,
                containerColor = MaterialTheme.colorScheme.surface
            )
        )
    }

    //CardView composable function
    @Suppress("UNNECESSARY_SAFE_CALL")
    @Composable
    fun CardViewMain() {
        Column {
            Spacer(modifier = Modifier.padding(top = 6.dp))
            Card(
                modifier = Modifier
                    .padding(10.dp)
                    .fillMaxWidth()
                    .wrapContentHeight(),
                colors = CardDefaults.cardColors(),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(
                    modifier = Modifier
                        .padding(16.dp)
                        .fillMaxWidth()
                        .wrapContentHeight(),
                    verticalArrangement = Arrangement.Top,
                    horizontalAlignment = Alignment.CenterHorizontally,
                )
                {
                    //Battery Info Image logo composable function
                    ImageLogo(batteryLevelStringVal.value)
                    TextHeader()

                    RowComponentInCard("Battery Level", batteryLevelStringVal.value)
                    Divider(thickness = 0.5.dp)
                    RowComponentInCard("Battery Type", batteryTypeVal.value)
                    Divider(thickness = 0.5.dp)
                    RowComponentInCard("Battery Temp", batteryTempVal.value)
                    Divider(thickness = 0.5.dp)
                    RowComponentInCard("Power Source", batteryPowerSourceVal.value)
                    Divider(thickness = 0.5.dp)
                    RowComponentInCard("Battery Status", batteryStatusVal.value)
                    Divider(thickness = 0.5.dp)
                    RowComponentInCard("Battery Voltage", batteryVoltageVal.value)
                    Divider(thickness = 0.5.dp)
                    RowComponentInCard("Battery Health", batteryHealthVal.value)
                    Divider(thickness = 0.5.dp)
                    RowComponentInCard("Fast Charging", batteryFastChargingVal.value)
                }//end of column
            }//end of card
        }//end of outer column
    }//end of card view main


    //Battery Image Logo composable function
    @Composable
    fun ImageLogo(valueOfBatteryTemp: String) {
        Box(
            modifier = Modifier.padding(5.dp),
            contentAlignment = Alignment.Center
        )
        {
            Image(
                painter = painterResource(R.drawable.battery),
                contentDescription = "Image Logo",
                modifier = Modifier
                    .requiredHeight(125.dp)
                    .requiredWidth(125.dp)
                    .padding(5.dp)
            )

            Text(
                text = valueOfBatteryTemp,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .padding(5.dp)
                    .wrapContentHeight()
                    .wrapContentWidth(),
                style = MaterialTheme.typography.labelLarge
            )
        }
    }

    //Battery Info app name Text
    @Composable
    fun TextHeader() {
        Text(
            text = "BATTERY INFO",
            textAlign = TextAlign.Center,
            modifier = Modifier
                .padding(5.dp)
                .fillMaxWidth(),
            style = MaterialTheme.typography.labelLarge
        )
    }


    //Row component composable function for battery related info
    @Composable
    fun RowComponentInCard(strDesc: String, mutableVal: String) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(5.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
        ) {
            Column(
                modifier = Modifier.weight(0.5f),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = strDesc,
                    textAlign = TextAlign.Left,
                    modifier = Modifier
                        .padding(5.dp),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.primary
                )
            }
            Column(
                modifier = Modifier.weight(0.5f),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = mutableVal,
                    textAlign = TextAlign.Right,
                    modifier = Modifier
                        .padding(5.dp),
                    style = MaterialTheme.typography.bodyMedium,
                )
            }
        }
    }

    override fun onPause() {
        super.onPause()
        unregisterReceiver(batteryStatsReceiver)
    }

    public override fun onResume() {
        super.onResume()
        val filter = IntentFilter(Intent.ACTION_BATTERY_CHANGED)
        activityContext.registerReceiver(batteryStatsReceiver, filter)
    }


    //Preview for jetpack composable view
    @Preview(showBackground = true)
    @Composable
    fun DefaultPreview() {
        Material3AppTheme {
            MainViewImplementation()
        }
    }
}