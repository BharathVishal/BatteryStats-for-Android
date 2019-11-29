package androidbatterystats.bharathvishal.com.androidbatterystats

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.BatteryManager
import android.os.Build
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*


/**
 * Created by Bharath Vishal on 20-01-2018.
 */

class MainActivity : AppCompatActivity() {
    private lateinit var activityContext: Context


    private val batteryStatsReceiver = object : BroadcastReceiver() {
        @SuppressLint("SetTextI18n")
        override fun onReceive(context: Context, intent: Intent) {

            if (Build.VERSION.SDK_INT >= 28) {
                val mBatteryManager = context.getSystemService(Context.BATTERY_SERVICE) as BatteryManager
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
            val fastchargestatus = intent.getBooleanExtra("fastcharge_status", false)
            val temperature = intent.getIntExtra("temperature", 0)
            var level = 0

            if (isPresent) {
                if (rawLevel >= 0 && scale > 0) {
                    level = rawLevel * 100 / scale
                }

                Battery_Level.text = (+level).toString() + "%"
                Battery_Type.text = technology
                Power_Source.text = getPlugTypeResultString(plugged)
                Battery_Voltage.text = Constants.STRING_EMPTY + voltage / 1000.0f + " V"
                Battery_Status.text = getStatusResultString(status)
                Battery_Health.text = getHealthResultString(health)
                Fast_Charging.text = Constants.STRING_EMPTY + fastchargestatus

                txtProgress_Battery.text = level.toString() + "%"

                val tempInCelsius = temperature / 10.0f
                val tempInFarheneit = 9 / 5 * tempInCelsius + 32.0f

                Battery_Temp.text = Constants.STRING_EMPTY + tempInCelsius + " C / " + tempInFarheneit + " F"
            } else {
                txtProgress_Battery.text = Constants.SYMBOL_HYPHEN

                Battery_Level.text = Constants.SYMBOL_HYPHEN
                Battery_Type.text = Constants.SYMBOL_HYPHEN
                Power_Source.text = Constants.SYMBOL_HYPHEN
                Battery_Temp.text = Constants.SYMBOL_HYPHEN
                Battery_Voltage.text = Constants.SYMBOL_HYPHEN
                Battery_Status.text = Constants.SYMBOL_HYPHEN
                Battery_Health.text = Constants.SYMBOL_HYPHEN
                Fast_Charging.text = Constants.SYMBOL_HYPHEN
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
            BatteryManager.BATTERY_HEALTH_UNSPECIFIED_FAILURE -> healthString = Constants.FAILURE
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
        this.setContentView(R.layout.activity_main)

        //Get the context of the Activity
        activityContext = this

        val filter = IntentFilter(Intent.ACTION_BATTERY_CHANGED)
        activityContext.registerReceiver(batteryStatsReceiver, filter)
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
}