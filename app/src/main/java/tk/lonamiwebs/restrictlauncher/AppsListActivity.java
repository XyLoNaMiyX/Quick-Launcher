package tk.lonamiwebs.QuickLauncher;


import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class AppsListActivity extends ActionBarActivity {

    String filter = "";
    UserDetail loadedUser;

    //region Setup

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_apps_list);

        if (!S.getIsLogged()) {
            S.showLockScreen();
            finish();
        }
        loadedUser = S.getLoggedUser();

        loadApps();
        loadListView();
        addClickListener();

        handleIntent(getIntent());

        S.setAppsRefreshedListener(new S.OnAppsRefreshedListener() {
            @Override
            public void onAppsRefreshed() {
                loadListView();
            }
        });

        registerForContextMenu(findViewById(R.id.apps_list));
    }

    @Override
    protected void onResume() {
        super.onResume();


        if (!S.getIsLogged())
            finish();

        if (!S.getLoggedUser().equals(loadedUser)) {
            S.apps = null;
            loadListView();
            loadApps();
        }
    }

    //endregion

    //region Menu

    SearchView searchView;
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_apps_list, menu);

        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        searchView = (SearchView) menu.findItem(R.id.action_search).getActionView();
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        searchView.setIconifiedByDefault(false);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                searchQuery(newText);
                return true;
            }
        });

        return true;
    }

    //endregion

    //region Context menu

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        if (v.getId() == R.id.apps_list) {
            MenuInflater inflater = getMenuInflater();
            inflater.inflate(R.menu.menu_apps_cms, menu);
        }
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        ListView lv = (ListView) findViewById(R.id.apps_list);
        AppDetail app = (AppDetail)lv.getItemAtPosition(info.position);

        switch(item.getItemId()) {
            case R.id.uninstall:
                Intent intent = new Intent(Intent.ACTION_DELETE);
                intent.setData(Uri.parse("package:" + app.packagename));
                startActivity(intent);
                return true;
            default:
                return super.onContextItemSelected(item);
        }
    }

    //endregion

    //region Search

    @Override
    protected void onNewIntent(Intent intent) {
        handleIntent(intent);
    }

    private void handleIntent(Intent intent) {
        if (Intent.ACTION_SEARCH.equals(intent.getAction()))
            searchQuery(intent.getStringExtra(SearchManager.QUERY));
    }

    void searchQuery(String query) {
        filter = query;
        loadListView();
    }

    //endregion

    //region Load apps

    List<AppDetail> filteredApps;

    void loadApps() {
        S.refreshApps();

        if (S.apps == null)
            Toast.makeText(getApplicationContext(), getResources().getString(R.string.loading), Toast.LENGTH_SHORT).show();
    }

    //endregion

    //region Apps list

    ListView list;
    void loadListView() {
        list = (ListView)findViewById(R.id.apps_list);

        if (!S.getIsLogged() || S.apps == null) {
            list.setAdapter(null);
            return;
        }

        filteredApps = new ArrayList<AppDetail>();
        filteredApps.addAll(S.apps);

        if (!filter.equals(""))
            for (AppDetail app : S.apps)
                if (!app.label.toLowerCase().contains(filter.toLowerCase()))
                    filteredApps.remove(app);

        for (AppDetail app : S.apps)
            if (S.getLoggedUser().username.equals("root"))
                break;
            else if (!S.getLoggedUser().allowedPackagenames.contains(app.packagename))
                filteredApps.remove(app);

        if (filteredApps.size() == 1) {
            startActivity(S.manager.getLaunchIntentForPackage(filteredApps.get(0).packagename));
            searchView.setQuery("", false);
            return;
        }

        ArrayAdapter<AppDetail> adapter = new ArrayAdapter<AppDetail>(this, R.layout.list_item, filteredApps) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                if (convertView == null)
                    convertView = getLayoutInflater().inflate(R.layout.list_item, null);

                try {
                    ImageView appIcon = (ImageView) convertView.findViewById(R.id.app_icon);
                    appIcon.setImageDrawable(filteredApps.get(position).icon);

                    TextView appLabel = (TextView) convertView.findViewById(R.id.app_label);
                    appLabel.setText(filteredApps.get(position).label);

                    TextView appName = (TextView) convertView.findViewById(R.id.app_name);
                    appName.setText(filteredApps.get(position).packagename);

                } catch (IndexOutOfBoundsException ioobe) { // it goes too fast i've got to do this
                    convertView = getLayoutInflater().inflate(R.layout.list_item, null);
                }

                return convertView;
            }
        };

        list.setAdapter(adapter);
    }

    void addClickListener() {
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> av, View v, int pos, long id) {
                try {
                    startActivity(S.manager.getLaunchIntentForPackage(filteredApps.get(pos).packagename));
                } catch (Exception ex) {
                    Toast.makeText(getApplicationContext(), getResources().getString(R.string.appNotInstalled),
                            Toast.LENGTH_SHORT).show();

                    loadApps();
                }
            }
        });
    }

    //endregion

}
