package tk.lonamiwebs.QuickLauncher;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.text.InputType;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Toast;

import java.util.ArrayList;


public class EditAccountActivity extends ActionBarActivity {

    UserDetail editingUser = null;

    boolean isRoot = false;
    boolean exists = true;

    ArrayList selectedItems = null;

    //region Setup

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_account);

        if (getIntent().getBooleanExtra("ISNEW", false)) {
            exists = false;
            setTitle(getResources().getString(R.string.addAccount));
        } else {
            editingUser = S.getUserByName(getIntent().getStringExtra("USERNAME"));
            refreshDetails();
        }

        ((CheckBox)findViewById(R.id.showPassCB)).setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                ((EditText) findViewById(R.id.passwordET))
                    .setInputType(isChecked ? InputType.TYPE_CLASS_NUMBER :
                            InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_VARIATION_PASSWORD);
            }
        });

        new RefreshAppsTask().execute();

        selectedItems = new ArrayList();

        if (exists)
            for (int i = 0; i < S.getAppsCount(); i++)
                if (editingUser.allowedPackagenames.contains(S.apps.get(i).packagename))
                    selectedItems.add(i);
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (S.getLoggedUser() == null || !S.getLoggedUser().username.equals("root"))
            finish();
    }

    //endregion

    //region Refresh fields

    void refreshDetails() {
        if (editingUser == null)
            return;

        ((EditText)findViewById(R.id.usernameET)).setText(editingUser.username);
        ((EditText)findViewById(R.id.passwordET)).setText(editingUser.password);

        if (editingUser.username.equals("root")) {
            findViewById(R.id.usernameET).setEnabled(false);
            findViewById(R.id.editAppsB).setEnabled(false);
            isRoot = true;
        }
    }

    //endregion

    //region Apps dialog

    public void showAllowed(View v) {
        if (S.apps == null) {
            S.refreshApps();
            return;
        }

        buildDialog().show();
    }

    public Dialog buildDialog() {
        final ArrayList dSelectedItems = selectedItems;
        boolean[] checkedItems = new boolean[S.getAppsCount()];

        for (int i = 0; i < checkedItems.length; i++)
            checkedItems[i] = dSelectedItems.contains(i);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        CharSequence[] appnames = new CharSequence[S.getAppsCount()];

        for (int i = 0; i < S.getAppsCount(); i++)
            appnames[i] = S.apps.get(i).label;

        builder.setTitle(getResources().getString(R.string.allowApps))
                .setMultiChoiceItems(appnames, checkedItems,
                        new DialogInterface.OnMultiChoiceClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                                if (isChecked)
                                    dSelectedItems.add(which);
                                else if (dSelectedItems.contains(which))
                                    dSelectedItems.remove(Integer.valueOf(which));
                            }
                        })
                .setPositiveButton(getResources().getString(R.string.accept), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        selectedItems = dSelectedItems;
                    }
                })
                .setNegativeButton(getResources().getString(R.string.cancel), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) { /* do nothing */ }
                });


        return builder.create();
    }


    //endregion

    //region Save changes

    public void saveAccount(View view) {
        if (isEmpty(findViewById(R.id.usernameET)) || isEmpty(findViewById(R.id.passwordET))) {
            Toast.makeText(getApplicationContext(), getResources().getString(R.string.emptyFields), Toast.LENGTH_LONG).show();
            return;
        }

        String username = ((EditText)findViewById(R.id.usernameET)).getText().toString();
        if (username.equals("root") && !isRoot) {
            Toast.makeText(getApplicationContext(), getResources().getString(R.string.cannotRoot), Toast.LENGTH_LONG).show();
            return;
        }

        String password = ((EditText)findViewById(R.id.passwordET)).getText().toString();
        ArrayList<String> allowedPackages = new ArrayList<>();

        for (Object i : selectedItems)
            allowedPackages.add(S.apps.get((int)i).packagename);

        if (editingUser != null)
            S.removeUserByName(editingUser.username);

        S.addUser(new UserDetail(username, password, allowedPackages));

        finish();
    }

    //endregion

    //region Utils

    private boolean isEmpty(View editText) {
        return ((EditText)editText).getText().toString().trim().length() == 0;
    }

    //endregion
}
