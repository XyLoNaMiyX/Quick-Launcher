package tk.lonamiwebs.QuickLauncher;

import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.os.AsyncTask;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Lonami on 21/12/2014.
 */


class RefreshAppsTask extends AsyncTask<Void, Integer, ArrayList<AppDetail>> {

    protected ArrayList<AppDetail> doInBackground(Void... args) {
        ArrayList<AppDetail> apps = new ArrayList<>();

        List<ResolveInfo> availableActivities = S.manager.queryIntentActivities
                (new Intent(Intent.ACTION_MAIN, null).addCategory(Intent.CATEGORY_LAUNCHER), 0);

        for (ResolveInfo ri : availableActivities)
            apps.add(new AppDetail(
                    ri.loadLabel((S.manager)).toString(),
                    ri.activityInfo.packageName,
                    ri.activityInfo.loadIcon(S.manager)));

        return apps;
    }

    protected void onPostExecute(ArrayList<AppDetail> result) {
        S.apps = result;
        S.aListener.onAppsRefreshed();
    }
}