package tk.lonamiwebs.QuickLauncher;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * Created by Lonami on 21/12/2014.
 */
public class AppIcon extends AppDetail {

    int marginTop = 10;
    int marginLeft = 10;

    //boolean moving = false;

    AppIcon(AppDetail details) {
        label = details.label;
        packagename = details.packagename;
        icon = details.icon;
    }

    RelativeLayout getView(final Context context) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        RelativeLayout layout = (RelativeLayout)inflater.inflate(R.layout.app_icon, null);

        ImageButton ib = (ImageButton)layout.findViewById(R.id.app_image);
        setMargins(ib, marginLeft, marginTop, 0, 0);

        ib.setImageDrawable(icon);
        ib.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                context.startActivity(S.manager.getLaunchIntentForPackage(packagename));
            }
        });

        /*

        ib.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                setMoving(true);
                return true;
            }
        });

        ib.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    setMoving(false);
                    return true;
                }

                return false;
            }
        });

        */

        ((TextView)layout.findViewById(R.id.app_label)).setText(label);

        return layout;
    }


    //region Interfaces

    /* Working but... I don't need it for the moment.
    It's hard to make a view refresh or even detect when it's moving... Meh!

    OnPositionChangedListener pListener;

    public interface OnPositionChangedListener {
        public void onPositionChanged();
    }

    public void setCustomEventListener(OnPositionChangedListener eventListener) {
        pListener = eventListener;
    }

     */

    //endregion

    // private void setMoving(boolean moving) { this.moving = moving; }

    static void setMargins (View v, int l, int t, int r, int b) {
        if (v.getLayoutParams() instanceof ViewGroup.MarginLayoutParams) {
            ViewGroup.MarginLayoutParams p = (ViewGroup.MarginLayoutParams) v.getLayoutParams();
            p.setMargins(l, t, r, b);
            v.requestLayout();
        }
    }
}
