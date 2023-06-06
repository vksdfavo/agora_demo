package com.example.newdemo;

import android.widget.Toast;

import io.agora.rtc.IRtcEngineEventHandler;

public class MyRtcEngineEventHandler extends IRtcEngineEventHandler {
    // Override the methods to handle specific events

    @Override
    public void onJoinChannelSuccess(String channel, int uid, int elapsed) {
        // Handle the event when the local user successfully joins a channel

    }

    @Override
    public void onUserJoined(int uid, int elapsed) {
        // Handle the event when a remote user joins the channel
    }

    @Override
    public void onUserOffline(int uid, int reason) {
        // Handle the event when a remote user goes offline or leaves the channel
    }

    // Other overridden methods for handling different events
}

