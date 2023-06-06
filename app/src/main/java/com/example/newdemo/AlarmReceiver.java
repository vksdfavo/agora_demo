package com.example.newdemo;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

public class AlarmReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();

        if (ConstantsStep.ACCEPT.equals(action)) {
            Toast.makeText(context, "YES CALLED", Toast.LENGTH_LONG).show();
        } else if (ConstantsStep.REJECT.equals(action)) {
            Toast.makeText(context, "NO CALLED", Toast.LENGTH_LONG).show();
        }
    }
}
