package tk.lonamiwebs.QuickLauncher;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;


public class AccountsListActivity extends ActionBarActivity {

    //region Setup

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_accounts_list);

        registerForContextMenu(findViewById(R.id.accounts_list));
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (S.getLoggedUser() == null || !S.getLoggedUser().username.equals("root")) {
            finish();
            return;
        }

        loadListView();
    }

    //endregion

    //region Menu

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_accounts_list, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_add_account) {
            if (S.onlyRootNoPassword()) {
                Toast.makeText(getApplicationContext(), getResources().getString(R.string.cannotAddUntil), Toast.LENGTH_LONG).show();
                return false;
            }

            start("", true);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        if (v.getId() == R.id.accounts_list) {
            MenuInflater inflater = getMenuInflater();
            inflater.inflate(R.menu.menu_accounts_cms, menu);
        }
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();
        ListView lv = (ListView) findViewById(R.id.accounts_list);
        String username = (String)lv.getItemAtPosition(info.position);

        switch(item.getItemId()) {
            case R.id.edit:
                start(username, false);
                return true;
            case R.id.delete:
                if (username.equals("root")) {
                    Toast.makeText(getApplicationContext(), getResources().getString(R.string.deleteRoot), Toast.LENGTH_LONG).show();
                    return false;
                }

                S.removeUserByName(username);
                loadListView();
                return true;
            default:
                return super.onContextItemSelected(item);
        }
    }


    //endregion

    //region Load accounts

    void loadListView() {
        ListView lv = (ListView)findViewById(R.id.accounts_list);

        final ArrayList<String> usernames = new ArrayList<String>();
        for (UserDetail user : S.users)
            usernames.add(user.username);

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, usernames);

        lv.setAdapter(adapter);

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                start(((TextView) view).getText().toString(), false);
            }
        });
    }

    void start(String username, boolean isNew) {
        startActivity(new Intent(getApplication(), EditAccountActivity.class)
                .putExtra("USERNAME", username)
                .putExtra("ISNEW", isNew));
    }

    //endregion
}
