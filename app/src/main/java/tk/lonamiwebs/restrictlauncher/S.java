package tk.lonamiwebs.QuickLauncher;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageManager;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by Lonami on 16/12/2014.
 */
public class S {

    //region Static variables

    static final int ACTIVITY_SELECT_IMAGE = 690;
    static final int PIC_CROP = 691;

    final static String pfk_users = "tk.lonamiwebs.restrictlaunhcer.USERS_ET_SECRET_PASSWORDS_LMAO";
    final static String pfk_locked = "tk.lonamiwebs.restrictlaunhcer.IS_SCREEN_LOCKED";
    final static String pfk_showTitleBar = "tk.lonamiwebs.QuickLauncher.SHOW_TITLE_BAR";
    final static String pfk_timeInMs = "tk.lonamiwebs.QuickLauncher.TIME_IN_MS";

    static Context context;
    static PackageManager manager;

    static SharedPreferences sharedPref_users;
    static SharedPreferences sharedPref_locked;
    static SharedPreferences sharedPref_showTitleBar;
    static SharedPreferences sharedPref_timeInMs;
    static Set<UserDetail> users = new HashSet<UserDetail>();

    private static UserDetail loggedUser = null;
    private static boolean isLogged = false;

    static List<AppDetail> apps = null;

    //endregion

    //region Interfaces

    static OnAppsRefreshedListener aListener = new OnAppsRefreshedListener() {
        @Override
        public void onAppsRefreshed() { }
    };

    public static interface OnAppsRefreshedListener {
        public void onAppsRefreshed();
    }

    public static void setAppsRefreshedListener(OnAppsRefreshedListener eventListener) {
        aListener = eventListener;
    }

    //endregion

    //region Title bar

    static boolean getShowTitleBar() {
        return sharedPref_showTitleBar.getBoolean("SHOW_TITLE_BAR", false);
    }

    static void setShowTitleBar(boolean show) {
        sharedPref_showTitleBar.edit().putBoolean("SHOW_TITLE_BAR", show).apply();
    }

    //endregion

    //region User management by name / by password

    public static void removeUserByName(String username) {
        for (UserDetail user : users)
            if (user.username.equals(username)) {
                users.remove(user);
                saveUsers();
                break;
            }
    }

    public static void removeUserByPassword(String password) {
        for (UserDetail user : users)
            if (user.password.equals(password)) {
                users.remove(user);
                saveUsers();
                break;
            }
    }

    static UserDetail getUserByName(String username) {
        for (UserDetail user : users)
            if (user.username.equals(username))
                return user;

        return null;
    }

    static UserDetail getUserByPassword(String password) {
        for (UserDetail user : users)
            if (user.password.equals(password))
                return user;

        return null;
    }

    //endregion

    //region User management

    public static int getUsersCount() { return users.size(); }

    static void saveUsers() {
        Editor editor = sharedPref_users.edit();
        Set<String> parsedUsers = new HashSet<>();
        for (UserDetail user : users)
            parsedUsers.add(user.parse());

        editor.putStringSet(pfk_users, parsedUsers);

        editor.apply();
    }

    static void addUser(UserDetail userDetail) { addUser(userDetail, true); }

    private static void addUser(UserDetail userDetail, boolean save) {
        removeUserByName(userDetail.username);
        removeUserByPassword(userDetail.password);

        users.add(userDetail);

        if (save)
            saveUsers();
    }

    static void refreshUsers() {
        users.clear();

        Set<String> parsedUsers = sharedPref_users.getStringSet(pfk_users, new HashSet<String>());

        for (String parsedUser : parsedUsers)
            addUser(UserDetail.unparse(parsedUser), false);

        if (getUsersCount() == 0)
            addUser(new UserDetail("root"));
    }

    static boolean onlyRootNoPassword() {
        return getUserByName("root").password.equals("") && users.size() == 1;
    }

    //endregion

    //region Password management

    static UserDetail getLoggedUser() {
        return onlyRootNoPassword() ? getUserByName("root") : loggedUser;
    }

    static boolean unlock(String password) {
        loggedUser = getUserByPassword(password);
        if (loggedUser == null)
            return false;

        isLogged = true;

        sharedPref_locked.edit().putBoolean(pfk_locked, false).apply();
        return true;
    }

    static void lock() {
        isLogged = false;
        loggedUser = null;
        sharedPref_locked.edit().putBoolean(pfk_locked, true).apply();
    }

    static boolean getIsLogged() {
        return onlyRootNoPassword() || isLogged;
    }

    static void showLockScreen() {
        if (onlyRootNoPassword() || isLogged)
            return;

        try {
            context.startActivity(new Intent(context, PasswordActivity.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
        } catch (RuntimeException re) { }
    }

    //endregion

    //region Run screen off service

    static void resetPasswordService(boolean running) {
        Intent service = new Intent(context, ScreenOffService.class);

        try {
            context.stopService(service);
        } catch (Exception e) { }

        if (running)
            context.startService(service);
    }

    //endregion

    //region Get time in MS

    static int getTimeInMs() {
        return sharedPref_timeInMs.getInt("MILLISECONDS", 100);
    }

    static void setTimeInMs(int ms) {
        sharedPref_timeInMs.edit().putInt("MILLISECONDS", ms).apply();
    }

    //endregion

    //Get apps

    static void refreshApps() {
        new RefreshAppsTask().execute();
    }

    static AppDetail getAppByPackageName(String packageName) {
        if (apps == null)
            return null;

        for (AppDetail app : apps)
            if (app.packagename.equals(packageName))
                return app;

        return null;
    }

    static int getAppsCount() {
        return apps == null ? 0 : apps.size();
    }

    //endregion
}