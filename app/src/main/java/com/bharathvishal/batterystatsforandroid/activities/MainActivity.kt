/**
 *
 * Copyright 2018-2025 Bharath Vishal G.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 **/


package com.bharathvishal.batterystatsforandroid.activities

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.res.Configuration
import android.os.BatteryManager
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.WindowInsets
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import com.bharathvishal.batterystatsforandroid.R
import com.bharathvishal.batterystatsforandroid.constants.Constants
import com.bharathvishal.batterystatsforandroid.databinding.ActivityMainBinding
import com.google.android.material.color.DynamicColors

/*
 * Created by Bharath Vishal on 20-01-2018.
 */

class MainActivity : AppCompatActivity() {
    private lateinit var activityContext: Context
    private lateinit var binding: ActivityMainBinding


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
            val fastchargestatus = intent.getBooleanExtra("fastcharge_status", false)
            val temperature = intent.getIntExtra("temperature", 0)
            var level = 0

            if (isPresent) {
                if (rawLevel >= 0 && scale > 0) {
                    level = rawLevel * 100 / scale
                }

                binding.BatteryLevel.text = (+level).toString() + "%"
                binding.BatteryType.text = technology
                binding.PowerSource.text = getPlugTypeResultString(plugged)
                binding.BatteryVoltage.text = Constants.STRING_EMPTY + voltage / 1000.0f + " V"
                binding.BatteryStatus.text = getStatusResultString(status)
                binding.BatteryHealth.text = getHealthResultString(health)
                binding.FastCharging.text = Constants.STRING_EMPTY + fastchargestatus

                binding.txtProgressBattery.text = "$level%"

                val tempInCelsius = temperature / 10.0f
                val tempInFarheneit = 9 / 5 * tempInCelsius + 32.0f

                binding.BatteryTemp.text =
                    Constants.STRING_EMPTY + tempInCelsius + " C / " + tempInFarheneit + " F"
            } else {
                binding.txtProgressBattery.text = Constants.SYMBOL_HYPHEN

                binding.BatteryLevel.text = Constants.SYMBOL_HYPHEN
                binding.BatteryType.text = Constants.SYMBOL_HYPHEN
                binding.PowerSource.text = Constants.SYMBOL_HYPHEN
                binding.BatteryTemp.text = Constants.SYMBOL_HYPHEN
                binding.BatteryVoltage.text = Constants.SYMBOL_HYPHEN
                binding.BatteryStatus.text = Constants.SYMBOL_HYPHEN
                binding.BatteryHealth.text = Constants.SYMBOL_HYPHEN
                binding.FastCharging.text = Constants.SYMBOL_HYPHEN
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
        //1.2.6
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.VANILLA_ICE_CREAM) {
                enableEdgeToEdge()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        super.onCreate(savedInstanceState)
        try {
            DynamicColors.applyToActivityIfAvailable(this)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        //Get the context of the Activity
        activityContext = this


        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.VANILLA_ICE_CREAM) {
                val viewTempAppBar = findViewById<View>(R.id.appbarlayout1);
                viewTempAppBar.setOnApplyWindowInsetsListener { view, insets ->
                    val statusBarInsets = insets.getInsets(WindowInsets.Type.statusBars())

                    val nightModeFlags: Int = activityContext.resources
                        .getConfiguration().uiMode and Configuration.UI_MODE_NIGHT_MASK
                    val isDarkMode = nightModeFlags == Configuration.UI_MODE_NIGHT_YES
                    val isDynamicTheme = DynamicColors.isDynamicColorAvailable();
                    // Adjust padding to avoid overlap
                    view.setPadding(0, statusBarInsets.top, 0, 0)
                    insets
                }

                val tempL: View = findViewById<View>(R.id.card_view);
                ViewCompat.setOnApplyWindowInsetsListener(tempL) { view, windowInsets ->
                    val insets = windowInsets.getInsets(WindowInsetsCompat.Type.systemGestures())
                    // Apply the insets as padding to the view. Here, set all the dimensions
                    // as appropriate to your layout. You can also update the view's margin if
                    // more appropriate.
                    tempL.updatePadding(0, 0, 0, insets.bottom)

                    // Return CONSUMED if you don't want the window insets to keep passing down
                    // to descendant views.
                    WindowInsetsCompat.CONSUMED
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }


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