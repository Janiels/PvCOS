package hds.pvcos;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.SupplicantState;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.util.Log;

import com.firebase.client.Firebase;

import java.util.ArrayList;

public class PvcApp extends Application {
    private Firebase server;

    private final Object serverLock = new Object();
    private String email;

    public void setEmail(String email) {
        this.email = email;
    }

    public String getEmail() {
        return email;
    }

    public Firebase getServer() {
        synchronized (serverLock) {
            if (server == null)
                server = new Firebase("https://boiling-torch-177.firebaseio.com/");

            return server;
        }
    }

    private static final String pvcosPrefs = "PvCOSPrefs";
    public WifiSettings getCurrentWifiSettings(boolean create) {
        WifiManager wifiManager = (WifiManager) getSystemService(WIFI_SERVICE);
        WifiInfo wifiInfo = wifiManager.getConnectionInfo();

        SupplicantState state = wifiInfo.getSupplicantState();
        String ssid = "<NOT CONNECTED>";
        if (state == SupplicantState.COMPLETED) {
            ssid = wifiInfo.getSSID();
        }

        WifiSettings settings = getWifiSettings(ssid);
        if (settings == null && create) {
            settings = new WifiSettings();
            settings.setWifiName(ssid);
        }

        return settings;
    }

    public WifiSettings getWifiSettings(String wifiName) {
        ArrayList<WifiSettings> allWifiSettings = getAllWifiSettings();

        for (WifiSettings settings : allWifiSettings) {
            if (settings.getWifiName().equals(wifiName))
                return settings;
        }

        return null;
    }

    public void saveWifiSettings(WifiSettings settings) {
        ArrayList<WifiSettings> allWifiSettings = getAllWifiSettings();

        boolean found = false;
        for (int i = 0; i < allWifiSettings.size(); i++) {
            WifiSettings otherSettings = allWifiSettings.get(i);
            if (otherSettings.getWifiName().equals(settings.getWifiName())) {
                allWifiSettings.set(i, settings);
                found = true;
                break;
            }
        }

        if (!found)
            allWifiSettings.add(settings);

        setAllWifiSettings(allWifiSettings);
    }

    private ArrayList<WifiSettings> getAllWifiSettings() {
        SharedPreferences prefs = getSharedPreferences(pvcosPrefs, 0);
        String wifis = prefs.getString("wifis", null);
        Log.w("PvCOS", "Loading " + wifis);
        if (wifis == null)
            return new ArrayList<>();

        ArrayList<WifiSettings> wifiSettings = new ArrayList<>();
        String[] wifisSplit = wifis.split("@@@@");
        for (String wifi : wifisSplit) {
            String[] wifiSplit = wifi.split("@@");

            WifiSettings settings = new WifiSettings();
            settings.setWifiName(wifiSplit[0]);

            SoundOption soundOption = SoundOption.valueOf(wifiSplit[1]);
            settings.setSoundOption(soundOption);
            wifiSettings.add(settings);
        }

        return wifiSettings;
    }

    private void setAllWifiSettings(ArrayList<WifiSettings> allWifiSettings) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < allWifiSettings.size(); i++) {
            WifiSettings wifiSettings = allWifiSettings.get(i);
            if (i > 0)
                sb.append("@@@@");

            sb.append(wifiSettings.getWifiName());
            sb.append("@@");
            sb.append(wifiSettings.getSoundOption().toString());
        }

        SharedPreferences prefs = getSharedPreferences(pvcosPrefs, 0);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("wifis", sb.toString());

        Log.w("PvCOS", "Saving " + sb.toString());
        editor.commit();
    }
}
