package hds.pvcos;

import android.app.Application;

import com.firebase.client.Firebase;

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
}
