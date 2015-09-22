package hds.pvcos;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
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
    }

    public void buttonLogoutClick(View v) {
        Firebase server = ((PvcApp)getApplication()).getServer();
        server.unauth();
        startActivity(new Intent(this, LoginActivity.class));
        finish();
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
