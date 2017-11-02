package tk.lonamiwebs.QuickLauncher;

import android.graphics.drawable.Drawable;

/**
 * Created by Lonami on 16/12/2014.
 */
public class AppDetail {
    public String label = "";
    public String packagename = "";
    public Drawable icon = null;

    public AppDetail() { }

    public AppDetail (String label, String packagename, Drawable icon) {
        this.label = label;
        this.packagename = packagename;
        this.icon = icon;
    }
}
