package com.apkstory.com.vichild.file;

import android.app.Fragment;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.apkstory.R;

/**
 * Created by v on 13-12-6.
 */
public class MenuFragment extends Fragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.add(0, 0, 0, getString(R.string.listname)).setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
        menu.add(0, 1, 1, getString(R.string.gridname)).setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
    }
}