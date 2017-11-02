package tk.lonamiwebs.QuickLauncher;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Vibrator;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

/**
 * Created by Lonami on 16/12/2014.
 */
public class HomeActivity extends Activity {

    //region Setup

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        S.sharedPref_showTitleBar = getSharedPreferences(S.pfk_showTitleBar, Context.MODE_PRIVATE);
        if (S.getShowTitleBar())
            setTheme(android.R.style.Theme_Wallpaper_NoTitleBar);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        S.context = getApplicationContext();
        S.manager = getPackageManager();

        S.sharedPref_users = getSharedPreferences(S.pfk_users, Context.MODE_PRIVATE);
        S.sharedPref_locked = getSharedPreferences(S.pfk_locked, Context.MODE_PRIVATE);
        S.sharedPref_timeInMs = getSharedPreferences(S.pfk_timeInMs, Context.MODE_PRIVATE);

        S.refreshApps();
        S.refreshUsers();

        S.resetPasswordService(!S.onlyRootNoPassword());

        if (S.getLoggedUser() == null)
            S.showLockScreen();
    }

    //endregion


    //region Buttons

    public void showApps(View view) {
        int ms = S.getTimeInMs();
        if (ms == 666) {
            int dot = 80;      // Length of a Morse Code "dot" in milliseconds
            int dash = 180;     // Length of a Morse Code "dash" in milliseconds
            int short_gap = 80;    // Length of Gap Between dots/dashes
            int medium_gap = 180;   // Length of Gap Between Letters
            int long_gap = 300;    // Length of Gap Between Words
            long[] morse = {
                    0,  // Start immediately
                    dot, short_gap, dot, short_gap, dot,    // s
                    medium_gap,
                    dash, short_gap, dash, short_gap, dash, // o
                    medium_gap,
                    dot, short_gap, dot, short_gap, dot,    // s
                    long_gap
            };

            ((Vibrator)getSystemService(Context.VIBRATOR_SERVICE)).vibrate(morse, -1);
        } else
            ((Vibrator)getSystemService(Context.VIBRATOR_SERVICE)).vibrate(ms);

        startActivity(new Intent(this, AppsListActivity.class));
        //todo wont work or what -> http://stackoverflow.com/questions/3389501/activity-transition-in-android
        //overridePendingTransition(R.anim.abc_slide_in_top, R.anim.abc_slide_out_top);
    }

    //endregion

    //region Forbidders

    @Override
    public void onBackPressed() {
        return;
    }

    //endregion

    //region Menu

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (!S.getIsLogged() || !S.getLoggedUser().username.equals("root"))
            return false;

        getMenuInflater().inflate(R.menu.menu_home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            startActivity(new Intent(this, SettingsActivity.class));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    //endregion

}