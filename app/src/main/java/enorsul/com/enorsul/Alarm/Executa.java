package enorsul.com.enorsul.Alarm;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import enorsul.com.enorsul.services.ServiceSincronizar;

public class Executa extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        Log.d("SYNCLOG", "Executa_15_ChamadoBroadCastAlarme");

        Intent intentb = new Intent(context.getApplicationContext(), ServiceSincronizar.class);

        context.startService(intentb );


        Log.d("SYNCLOG", "Executa_22_FinalizadoBroadCastAlarme");



    }
}