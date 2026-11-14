package com.racks.mydoctor;

import android.app.Application;
import android.util.Log;

import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.FirebaseStorage;

/**
 * Debug-only Application class. Initializes Firebase manually (FirebaseInitProvider is
 * removed in the debug manifest).
 *
 * When built with -PuseFirebaseEmulator=true, every Firebase call goes to the local
 * Firebase Emulator Suite (see /firebase-emulator) instead of the real project.
 * 10.0.2.2 is the host machine's localhost as seen from the Android emulator.
 *
 * The Realtime Database is redirected through the databaseUrl option rather than
 * FirebaseDatabase.useEmulator(): useEmulator() only affects the first instance, and
 * later FirebaseDatabase.getInstance() calls can return a new instance that talks to
 * the real database.
 */
public class DebugApp extends Application {

    private static final String HOST = "10.0.2.2";

    @Override
    public void onCreate() {
        super.onCreate();
        if (!BuildConfig.USE_FIREBASE_EMULATOR) {
            FirebaseApp.initializeApp(this);
            return;
        }
        FirebaseOptions base = FirebaseOptions.fromResource(this);
        if (base == null) throw new IllegalStateException("google-services.json missing");
        String ns = base.getProjectId() + "-default-rtdb";
        FirebaseOptions options = new FirebaseOptions.Builder(base)
                .setDatabaseUrl("http://" + HOST + ":9000?ns=" + ns)
                .build();
        FirebaseApp.initializeApp(this, options);
        FirebaseAuth.getInstance().useEmulator(HOST, 9099);
        FirebaseStorage.getInstance().useEmulator(HOST, 9199);
        Log.w("DebugApp", "Using local Firebase Emulator Suite (database ns=" + ns + ")");
    }
}
