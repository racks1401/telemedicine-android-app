package com.racks.mydoctor.activities;

import android.content.IntentFilter;
import android.net.ConnectivityManager;

import androidx.appcompat.app.AppCompatActivity;

import com.racks.mydoctor.helper.ConnectionReceiver;


public abstract class BaseActivity extends AppCompatActivity implements ConnectionReceiver.ReceiverListener {

    public boolean isConnectionLost = false;

    @Override
    protected void onResume() {
        super.onResume();
        setConnectionListener();
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver();
    }

    private void setConnectionListener() {
        IntentFilter intentFilter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(new ConnectionReceiver(), intentFilter);
        ConnectionReceiver.setNetworkChangeListener(this);
    }

    private void unregisterReceiver() {
        try {
            unregisterReceiver(new ConnectionReceiver());
        } catch (IllegalArgumentException e) {
            // Receiver not registered
        }
    }

    @Override
    public void onNetworkChange(boolean isConnected) {
        if (isConnected) {
            // Handle connected state
            handleConnectedState(isConnectionLost);
            if (isConnectionLost) {
                isConnectionLost = false;
            }
        } else {
            // Handle disconnected state
            handleDisconnectedState();
            isConnectionLost = true;
        }
    }

    // Method to handle connected state (to be implemented by subclasses)
    protected abstract void handleConnectedState(boolean b);

    // Method to handle disconnected state (to be implemented by subclasses)
    protected abstract void handleDisconnectedState();
}

