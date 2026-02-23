// java
package edu.vassar.cmpu203.vassareats.view;

import android.app.Activity;
import android.view.ViewGroup;

import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.navigation.NavigationView;

import edu.vassar.cmpu203.vassareats.R;

public class NavigationDrawer {

    /**
     * Set the NavigationView width to half the current screen width.
     * Usage: NavigationDrawer.setHalfWidth(this) from an Activity.
     */
    public static void setHalfWidth(Activity activity) {
        if (activity == null) return;
        NavigationView navView = activity.findViewById(R.id.nav_view);
        if (navView == null) return;

        int screenWidth = activity.getResources().getDisplayMetrics().widthPixels;
        int drawerWidth = screenWidth / 2; // half the screen

        ViewGroup.LayoutParams lp = navView.getLayoutParams();
        if (lp instanceof DrawerLayout.LayoutParams) {
            DrawerLayout.LayoutParams dlp = (DrawerLayout.LayoutParams) lp;
            dlp.width = drawerWidth;
            navView.setLayoutParams(dlp);
        } else {
            lp.width = drawerWidth;
            navView.setLayoutParams(lp);
        }
    }
}

