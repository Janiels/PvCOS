package hds.pvcos;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

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

        Log.w("PvCOS", "LORTET VIRKER");
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION);
        registerReceiver(broadcastReceiver, intentFilter);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        unregisterReceiver(broadcastReceiver);
    }

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
                    setSound(settings.getSoundOption());
                }
            }
        }
    };

    private void setSound(SoundOption sound) {
        if (sound == SoundOption.DoNothing)
            return;

        String text;
        if (sound == SoundOption.Mute) {
            text = "Muting";
        } else {
            text = "Unmuting";
        }

        Toast.makeText(getApplicationContext(), text, Toast.LENGTH_LONG).show();

        AudioManager audioMgr = (AudioManager)getSystemService(Context.AUDIO_SERVICE);

        if (sound == SoundOption.Mute)
            muteStream(audioMgr, AudioManager.STREAM_MUSIC);
        else
            unmuteStream(audioMgr, AudioManager.STREAM_MUSIC);
    }

    private void muteStream(AudioManager audioMgr, int stream) {
        audioMgr.setStreamVolume(stream, 0, 0);
    }

    private void unmuteStream(AudioManager audioMgr, int stream) {
        audioMgr.setStreamVolume(stream, audioMgr.getStreamMaxVolume(stream), 0);
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
