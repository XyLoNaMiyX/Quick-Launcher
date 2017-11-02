package tk.lonamiwebs.QuickLauncher;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.widget.Toast;


public class PasswordActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (S.getIsLogged())
            finish();

        setContentView(R.layout.activity_password);

        ((EditText) findViewById(R.id.pwField)).setOnEditorActionListener(new OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    checkPass();
                    return true;
                }
                return false;
            }
        });
    }

    //region Prevent changing app

    @Override
    public void onPause() {
        super.onPause();
        S.showLockScreen();
        finish();
    }

    @Override
    public void onBackPressed() { return; }

    // This works likes a charm... almost!
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);

        if(!hasFocus) {
            sendBroadcast(new Intent(Intent.ACTION_CLOSE_SYSTEM_DIALOGS));
        }
    }

    //endregion

    public void login(View v) {
        checkPass();
    }

    void checkPass() {
        if (S.unlock(((EditText)findViewById(R.id.pwField)).getText().toString())) {
            startActivity(new Intent(this, HomeActivity.class));
            finish();
        }
        else
            Toast.makeText(getApplicationContext(), getResources().getString(R.string.invalidPass), Toast.LENGTH_SHORT).show();
    }

}
