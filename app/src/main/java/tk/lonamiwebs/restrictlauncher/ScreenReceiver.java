package tk.lonamiwebs.QuickLauncher;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Created by Lonami on 17/12/2014.
 */
public class ScreenReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(Intent.ACTION_SCREEN_OFF)) {
            S.lock();
        } else if (intent.getAction().equals(Intent.ACTION_SCREEN_ON)) {
            S.showLockScreen();
        }
    }
}