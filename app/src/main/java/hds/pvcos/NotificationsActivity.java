package hds.pvcos;

import android.content.Intent;
import android.os.Bundle;
import android.app.Activity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;

import java.util.ArrayList;

public class NotificationsActivity extends Activity {

    private ListView view;
    private String selectedStringInView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notifications);
        view = (ListView) findViewById(R.id.listView2);
        view.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                selectedStringInView = (String)view.getItemAtPosition(position);
            }
        });

        setDefaultWifiName();
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateListView();
    }

    private void updateListView() {
        ArrayList<WifiSettings> wifiSettings =
                ((PvcApp)getApplication()).getAllWifiSettings();
        ArrayList<String> wifiNames = new ArrayList<>();
        for(WifiSettings wifiSetting : wifiSettings) {
            wifiNames.add(wifiSetting.getWifiName());
        }
        view.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, wifiNames));
    }

    public void buttonEditNotificationClick(View v) {
        // do stuff
        if (selectedStringInView == null) return;

        Intent intent = new Intent(this, EditNotifications.class);
        intent.putExtra(EditNotifications.WIFINAMEIDENTIFIER, selectedStringInView);
        startActivity(intent);

        setDefaultWifiName();
    }

    public void buttonDeleteSelectedNotificationClick(View v) {
        // do stuff
        if (selectedStringInView == null) return;
        ((PvcApp)getApplication()).removeWifiByName(selectedStringInView);
        updateListView();
    }

    public void buttonAddNewNotificationClick(View v) {
        // do stuff
        String newName = ((EditText)findViewById(R.id.editText2)).getText().toString();
        if (newName.equals("")) return;

        Intent intent = new Intent(this, EditNotifications.class);
        intent.putExtra(EditNotifications.WIFINAMEIDENTIFIER, newName);
        startActivity(intent);

        setDefaultWifiName();
    }

    private void setDefaultWifiName() {
        String currentWifiName = ((PvcApp)getApplication()).getCurrentWifiSSID();
        ((EditText)findViewById(R.id.editText2)).setText(currentWifiName);
    }


}
