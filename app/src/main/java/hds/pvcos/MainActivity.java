package hds.pvcos;

import android.app.Activity;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.firebase.client.Firebase;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Firebase.setAndroidContext(this);

        setContentView(R.layout.activity_main);

        Firebase server = ((PvcApp)getApplication()).getServer();
        if (server.getAuth() == null || ((PvcApp)getApplication()).getEmail() == null) {
            if (server.getAuth() != null)
                server.unauth();

            // Not authed yet
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }

        Log.w("PvCOS", "LORTET VIRKER");
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION);
        registerReceiver(broadcastReceiver, intentFilter);
        registered = true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (registered) {
            unregisterReceiver(broadcastReceiver);
            registered = false;
        }
    }

    private boolean registered;
    private final BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.w("PvCOS", "BroadcastReceiver onReceive");
            final String action = intent.getAction();
            if (action.equals(WifiManager.NETWORK_STATE_CHANGED_ACTION)) {
                NetworkInfo netInfo = intent.getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO);
                Log.w("PvCOS", "Connected to network " + ((WifiManager)getSystemService(WIFI_SERVICE)).getConnectionInfo().getSSID() + " state: " + netInfo.getDetailedState().toString());
                WifiSettings settings = ((PvcApp)getApplication()).getCurrentWifiSettings(false);
                Log.w("PvCOS", "settings: " + (settings == null ? "null" : "not null"));
                if (settings != null) {
                    onLogonToWifi(settings);
                }
            }
        }
    };

    private long lastWifiChangeTime;
    private void onLogonToWifi(WifiSettings settings) {
        // Only allow notifications every 5 seconds
        long curTime = SystemClock.elapsedRealtime();
        if (lastWifiChangeTime != 0 && (curTime - lastWifiChangeTime) < 5000)
            return;

        lastWifiChangeTime = curTime;

        setSound(settings.getWifiName(), settings.getSoundOption());
    }

    private void setSound(String wifi, SoundOption sound) {
        if (sound == SoundOption.DoNothing)
            return;

        String title;
        String detail;
        int ringerMode;

        if (sound == SoundOption.Mute) {
            title = "Muted sound profile";
            detail = "Your sound was muted because you logged on to " + wifi;
            ringerMode = AudioManager.RINGER_MODE_SILENT;
        } else if (sound == SoundOption.Unmute) {
            title = "Unmuted sound profile";
            detail = "Your sound was unmuted because you logged on to " + wifi;
            ringerMode = AudioManager.RINGER_MODE_NORMAL;
        } else {
            title = "Vibrate sound profile";
            detail = "Your sound was changed to vibrate because you logged on to " + wifi;
            ringerMode = AudioManager.RINGER_MODE_VIBRATE;
        }

        AudioManager audioMgr = (AudioManager)getSystemService(Context.AUDIO_SERVICE);
        audioMgr.setRingerMode(ringerMode);

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.cast_ic_notification_1)
                .setContentTitle(title)
                .setContentText(detail)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(detail));

        NotificationManager notificationMgr =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationMgr.notify(0, notificationBuilder.build());
    }

    public void buttonTestLogonClick(View v) {
        WifiSettings settings = ((PvcApp)getApplication()).getWifiSettings("Test");
        if (settings != null)
            onLogonToWifi(settings);
    }

    public void buttonLogoutClick(View v) {
        Firebase server = ((PvcApp)getApplication()).getServer();
        server.unauth();
        startActivity(new Intent(this, LoginActivity.class));
        finish();
    }

    public void buttonBluetoothClick(View v) {
        startActivity(new Intent(this, BluetoothActivity.class));
    }

    public void buttonMyLocationClick(View v) {
        startActivity(new Intent(this, MapsActivity.class));
    }

    public void buttonEditNotificationClick(View v) {
        startActivity(new Intent(this, NotificationsActivity.class));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


}
