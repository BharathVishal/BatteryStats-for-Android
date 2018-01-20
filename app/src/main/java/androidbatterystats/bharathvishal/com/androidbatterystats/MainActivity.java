package androidbatterystats.bharathvishal.com.androidbatterystats;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;


/**
 * Created by Bharath Vishal on 20-01-2018.
 */

public class MainActivity extends AppCompatActivity {
    private TextView battery_Level;
    private TextView battery_Type;
    private TextView battery_Source;
    private TextView battery_Temp;
    private TextView battery_Voltage;
    private TextView battery_Status;
    private TextView battery_Health;
    private TextView fast_Charging;

    private TextView txtProgress;
    Context activity_Context;


    private BroadcastReceiver battery_Stats_Receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            boolean isPresent = intent.getBooleanExtra("present", false);
            String technology = intent.getStringExtra("technology");
            int plugged = intent.getIntExtra("plugged", -1);
            int scale = intent.getIntExtra("scale", -1);
            int health = intent.getIntExtra("health", 0);
            int status = intent.getIntExtra("status", 0);
            int raw_Level = intent.getIntExtra("level", -1);
            int voltage = intent.getIntExtra("voltage", 0);
            boolean fastcharge_status = intent.getBooleanExtra("fastcharge_status", false);
            int temperature = intent.getIntExtra("temperature", 0);
            int level = 0;

            if (isPresent) {
                if (raw_Level >= 0 && scale > 0) {
                    level = (raw_Level * 100) / scale;
                }

                battery_Level.setText(+level + "%");
                battery_Type.setText(technology);
                battery_Source.setText(getPlugTypeResultString(plugged));
                battery_Voltage.setText(Constants.STRING_EMPTY + voltage / 1000.0f + " V");
                battery_Status.setText(getStatusResultString(status));
                battery_Health.setText(getHealthResultString(health));
                fast_Charging.setText(Constants.STRING_EMPTY + fastcharge_status);

                txtProgress.setText(level + "%");

                float temp_In_Celsius = temperature / 10.0f;
                float temp_In_Farheneit = (9 / 5) * temp_In_Celsius + 32.0f;

                battery_Temp.setText(Constants.STRING_EMPTY + temp_In_Celsius + " C / " + temp_In_Farheneit + " F");
            } else {
                txtProgress.setText(Constants.SYMBOL_HYPHEN);

                battery_Level.setText(Constants.SYMBOL_HYPHEN);
                battery_Type.setText(Constants.SYMBOL_HYPHEN);
                battery_Source.setText(Constants.SYMBOL_HYPHEN);
                battery_Temp.setText(Constants.SYMBOL_HYPHEN);
                battery_Voltage.setText(Constants.SYMBOL_HYPHEN);
                battery_Status.setText(Constants.SYMBOL_HYPHEN);
                battery_Health.setText(Constants.SYMBOL_HYPHEN);
                fast_Charging.setText(Constants.SYMBOL_HYPHEN);
            }
        }
    };


    private String getPlugTypeResultString(int plugged) {
        String plugType = Constants.UNKNOWN_VALUE;
        switch (plugged) {
            case BatteryManager.BATTERY_PLUGGED_AC:
                plugType = Constants.AC;
                break;
            case BatteryManager.BATTERY_PLUGGED_USB:
                plugType = Constants.USB;
                break;
        }
        return plugType;
    }


    private String getHealthResultString(int health) {
        String healthString = Constants.UNKNOWN_VALUE;

        switch (health) {
            case BatteryManager.BATTERY_HEALTH_DEAD:
                healthString = Constants.DEAD;
                break;
            case BatteryManager.BATTERY_HEALTH_GOOD:
                healthString = Constants.GOOD;
                break;
            case BatteryManager.BATTERY_HEALTH_OVER_VOLTAGE:
                healthString = Constants.OVERVOLTAGE;
                break;
            case BatteryManager.BATTERY_HEALTH_OVERHEAT:
                healthString = Constants.OVERHEAT;
                break;
            case BatteryManager.BATTERY_HEALTH_UNSPECIFIED_FAILURE:
                healthString = Constants.FAILURE;
                break;
        }
        return healthString;
    }


    private String getStatusResultString(int status) {
        String statusString = Constants.UNKNOWN_VALUE;

        switch (status) {
            case BatteryManager.BATTERY_STATUS_CHARGING:
                statusString = Constants.CHARGING;
                break;
            case BatteryManager.BATTERY_STATUS_DISCHARGING:
                statusString = Constants.DISCHARGING;
                break;
            case BatteryManager.BATTERY_STATUS_FULL:
                statusString = Constants.FULL;
                break;
            case BatteryManager.BATTERY_STATUS_NOT_CHARGING:
                statusString = Constants.NOT_CHARGING;
                break;
        }
        return statusString;
    }


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.activity_main);

        battery_Level = findViewById(R.id.Battery_Level);
        battery_Type = findViewById(R.id.Battery_Type);
        battery_Source = findViewById(R.id.Power_Source);
        battery_Temp = findViewById(R.id.Battery_Temp);
        battery_Voltage = findViewById(R.id.Battery_Voltage);
        battery_Status = findViewById(R.id.Battery_Status);
        battery_Health = findViewById(R.id.Battery_Health);
        fast_Charging = findViewById(R.id.Fast_Charging);
        txtProgress = findViewById(R.id.txtProgress_Battery);

        //Get the context of the Activity
        activity_Context = this;

        IntentFilter filter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        activity_Context.registerReceiver(battery_Stats_Receiver, filter);
    }


    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(battery_Stats_Receiver);
    }


    @Override
    public void onResume() {
        super.onResume();
        IntentFilter filter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        activity_Context.registerReceiver(battery_Stats_Receiver, filter);
    }
}