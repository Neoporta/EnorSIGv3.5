package enorsul.com.enorsul.Alarm;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;

import enorsul.com.enorsul.LoginActivity;

public class Boot extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent i) {

        Intent intent = new Intent(context, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        context.startActivity(intent);





    }





}

