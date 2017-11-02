package tk.lonamiwebs.QuickLauncher;

import android.app.WallpaperManager;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Display;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Toast;

import java.io.IOException;
import java.net.MalformedURLException;


public class SettingsActivity extends ActionBarActivity {

    //region Setup

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        CheckBox showBar = (CheckBox)findViewById(R.id.toggleBar);
        showBar.setChecked(S.getShowTitleBar());

        showBar.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                S.setShowTitleBar(isChecked);
            }
        });

        ((EditText)findViewById(R.id.millisecondsET)).setText(S.getTimeInMs() + "");
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (S.getLoggedUser() == null || !S.getLoggedUser().username.equals("root"))
            finish();
    }

    //endregion

    //region On Activity Result

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);

        switch(requestCode) {
            case S.ACTIVITY_SELECT_IMAGE:
                if (resultCode == RESULT_OK) {
                    if (data == null)
                        return;

                    Uri selectedImage = data.getData();
                    cropImage(selectedImage);
                }
                break;
            case S.PIC_CROP:
                Bundle extras = data.getExtras();
                Bitmap thePic = extras.getParcelable("data");
                setBackground(thePic);
                break;
        }
    }

    //endregion

    //region Set wallpaper

    public void changeWallpaper(View v) {
        startActivityForResult(new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.INTERNAL_CONTENT_URI), S.ACTIVITY_SELECT_IMAGE);
    }

    public void setBackground(Bitmap image) {
        try {
            WallpaperManager wpm = WallpaperManager.getInstance(getApplicationContext());
            Point size = getScreenSize();
            wpm.suggestDesiredDimensions(size.x, size.y);
            wpm.setBitmap(image);

            showError(R.string.wallpaperChangedSuccess);
        } catch (MalformedURLException e) {
            showError(R.string.MalformedURLException);
        }
        catch (IOException e) {
            showError(R.string.IOException);
        }
    }

    //endregion

    //region Cropping

    public void cropImage(Uri picUri) {
        try {
            Point size = getScreenSize();
            startActivityForResult(new Intent("com.android.camera.action.CROP")
                    .setDataAndType(picUri, "image/*")
                    .putExtra("crop", "true")
                    .putExtra("aspectX", size.x)
                    .putExtra("aspectY", size.y)
                    .putExtra("outputX", size.x)
                    .putExtra("outputY", size.y)
                    .putExtra("scale", true)
                    .putExtra("return-data", true), S.PIC_CROP);
        }

        catch(ActivityNotFoundException anfe){ showError(R.string.IOException); }
    }


    public Point getScreenSize() {
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();

        if (android.os.Build.VERSION.SDK_INT >= 13)
            display.getSize(size);
        else
            size = new Point(display.getWidth(), display.getHeight());

        return size;
    }

    //endregion

    //region Set MS

    public void updateMs(View v) {
        EditText et = (EditText)findViewById(R.id.millisecondsET);
        if (et.getText().toString().equals(""))
            et.setText("100");

        S.setTimeInMs(Integer.parseInt(et.getText().toString()));
        Toast.makeText(getApplicationContext(), getResources().getString(R.string.msUpdated), Toast.LENGTH_SHORT).show();
    }

    //endregion

    //region Message handling

    void showError(int id) {
        showError(getResources().getString(id));
    }
    void showError(String error) {
        Toast.makeText(getApplicationContext(), error, Toast.LENGTH_LONG).show();
    }

    //endregion

    //region Other settings

    public void manageAccounts(View v) {
        startActivity(new Intent(this, AccountsListActivity.class));
    }

    //endregion
}
