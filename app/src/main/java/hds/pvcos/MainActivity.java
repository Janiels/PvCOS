package hds.pvcos;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.net.wifi.WifiManager;
import android.os.Bundle;
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
        }

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(WifiManager.SUPPLICANT_CONNECTION_CHANGE_ACTION);
        registerReceiver(broadcastReceiver, intentFilter);
    }

    private final BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            if (action.equals(WifiManager.SUPPLICANT_CONNECTION_CHANGE_ACTION)) {
                Log.w("PvCOS", "Connected to network " + ((WifiManager)getSystemService(WIFI_SERVICE)).getConnectionInfo().getSSID());
                WifiSettings settings = ((PvcApp)getApplication()).getCurrentWifiSettings(false);
                if (settings != null) {
                    setSound(settings.getSoundOption());
                }
            }
        }
    };

    private void setSound(SoundOption sound) {
        if (sound == SoundOption.DoNothing)
            return;

        int adjust = sound == SoundOption.Mute ? AudioManager.ADJUST_MUTE : AudioManager.ADJUST_UNMUTE;

        AudioManager audioMgr = (AudioManager)getSystemService(Context.AUDIO_SERVICE);
        audioMgr.adjustStreamVolume(AudioManager.STREAM_NOTIFICATION, adjust, 0);
        audioMgr.adjustStreamVolume(AudioManager.STREAM_ALARM, adjust, 0);
        audioMgr.adjustStreamVolume(AudioManager.STREAM_MUSIC, adjust, 0);
        audioMgr.adjustStreamVolume(AudioManager.STREAM_RING, adjust, 0);
        audioMgr.adjustStreamVolume(AudioManager.STREAM_SYSTEM, adjust, 0);
    }

    public void buttonWifiNetworksClick(View v) {
        startActivity(new Intent(this, WifiNetworksActivity.class));
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
