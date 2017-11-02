package tk.lonamiwebs.QuickLauncher;

/**
 * Created by Lonami on 18/12/2014.
 */

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;

public class ScreenOffService extends Service {


    static BroadcastReceiver receiver = null;

    @Override
    public IBinder onBind(Intent arg) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flag, int startId) {
        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_SCREEN_OFF);
        filter.addAction(Intent.ACTION_SCREEN_ON);

        receiver = new ScreenReceiver();
        registerReceiver(receiver, filter);

        return super.onStartCommand(intent, flag, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        stopSelf();

        unregisterReceiver(receiver);
    }


}
