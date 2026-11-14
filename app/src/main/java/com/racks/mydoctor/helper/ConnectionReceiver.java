package com.racks.mydoctor.helper;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.racks.mydoctor.activities.BaseActivity;


public class ConnectionReceiver extends BroadcastReceiver {
    public static ReceiverListener receiverListener;


    public static void setNetworkChangeListener(BaseActivity baseActivity) {
        receiverListener = baseActivity;
    }

    @Override
    public void onReceive(Context context, Intent intent) {

        if (receiverListener != null) {

            boolean isConnected = CheckConnection.isConnected(context.getApplicationContext());

            receiverListener.onNetworkChange(isConnected);
        }
    }

    public interface ReceiverListener {
        void onNetworkChange(boolean isConnected);
    }

}
