package hds.pvcos;

import android.os.Bundle;
import android.app.Activity;
import android.view.View;
import android.widget.RadioButton;
import android.widget.TextView;

public class EditNotifications extends Activity {
    public static final String WIFINAMEIDENTIFIER = "hds.pvcos.wifiName";
    private String originalName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_notifications);

        originalName = getIntent().getStringExtra(WIFINAMEIDENTIFIER);
        ((TextView)findViewById(R.id.editText3)).setText(originalName);

        WifiSettings settings = ((PvcApp)getApplication()).getWifiSettings(originalName);
        if (settings == null) {
            return;
        }

        switch (settings.getSoundOption()) {
            case Mute:
                ((RadioButton)findViewById(R.id.radioButton)).setChecked(true);
                break;
            case Unmute:
                ((RadioButton)findViewById(R.id.radioButton2)).setChecked(true);
                break;
            case Vibrate:
                ((RadioButton)findViewById(R.id.radioButton4)).setChecked(true);
                break;
        }
    }

    public void buttonSaveNotificationChangeClick(View v) {
        // do stuff
        String newName = ((TextView)findViewById(R.id.editText3)).getText().toString();
        if (newName.equals("")) return;

        ((PvcApp)getApplication()).removeWifiByName(originalName);

        WifiSettings wifiSetting = new WifiSettings();
        wifiSetting.setWifiName(newName);

        if (((RadioButton)findViewById(R.id.radioButton)).isChecked()) {
            // mute
            wifiSetting.setSoundOption(SoundOption.Mute);
        } else if (((RadioButton)findViewById(R.id.radioButton2)).isChecked()) {
            // unmute
            wifiSetting.setSoundOption(SoundOption.Unmute);
        } else if (((RadioButton)findViewById(R.id.radioButton4)).isChecked()) {
            // vibrate
            wifiSetting.setSoundOption(SoundOption.Vibrate);
        } else {
            // do nothing
            wifiSetting.setSoundOption(SoundOption.DoNothing);
        }
        ((PvcApp)getApplication()).saveWifiSettings(wifiSetting);
        finish();
    }
}
