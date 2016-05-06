package com.example.popularmovies;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Created by vishal vyas on 5/6/16..
 * <p/>
 * Connectivity change receiver
 */
public class NetworkChangeReceiver extends BroadcastReceiver {

    NetworkConnectedListener networkConnectedListener;

    public NetworkChangeReceiver(NetworkConnectedListener networkConnectedListener) {
        this.networkConnectedListener = networkConnectedListener;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if (NetworkUtils.isConnected(context)) {
            if (this.networkConnectedListener != null) {
                networkConnectedListener.onConnected();
            }
        }
    }

    public interface NetworkConnectedListener {
        void onConnected();
    }
}
