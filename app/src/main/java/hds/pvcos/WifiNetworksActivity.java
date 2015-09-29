package hds.pvcos;

import android.app.Activity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

public class WifiNetworksActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wifi_networks);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_wifi_networks, menu);
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

    public void buttonSelectSoundOptionClick(View view) {
        WifiSettings currentSetting = ((PvcApp)getApplication()).getCurrentWifiSettings(true);

        switch (view.getId()) {
            case R.id.radioButtonUnmute:
                currentSetting.setSoundOption(SoundOption.Unmute);
                break;
            case R.id.radioButtonMute:
                currentSetting.setSoundOption(SoundOption.Mute);
                break;
            case R.id.radioButtonNoSoundChange:
                currentSetting.setSoundOption(SoundOption.DoNothing);
                break;
        }

        ((PvcApp)getApplication()).saveWifiSettings(currentSetting);
    }
}
