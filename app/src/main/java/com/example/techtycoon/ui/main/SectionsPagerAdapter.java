package com.example.techtycoon.ui.main;

import android.content.Context;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.example.techtycoon.FragmentAllCompanies;
import com.example.techtycoon.FragmentAllDevices;
import com.example.techtycoon.FragmentDeviceCreator;

import org.jetbrains.annotations.NotNull;

/**
 * A [FragmentPagerAdapter] that returns a fragment corresponding to
 * one of the sections/tabs/pages.
 */
public class SectionsPagerAdapter extends FragmentPagerAdapter {

    private static final String[] TAB_TITLES = new String[]{"Device Creator", "All Device","All Company"};

    public SectionsPagerAdapter(Context context, FragmentManager fm) {
        super(fm);
    }

    @NotNull
    @Override
    public Fragment getItem(int position) {
        // getItem is called to instantiate the fragment for the given page.
        Fragment fr;
        switch (position){
            default:fr=PlaceholderFragment.newInstance(position + 1);break;
            case 0:fr= FragmentDeviceCreator.newInstance();break;
            case 1:fr= FragmentAllDevices.newInstance();break;
            case 2:fr= FragmentAllCompanies.newInstance();break;
        }
        return fr;
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return TAB_TITLES[position];
    }

    @Override
    public int getCount() {
        return TAB_TITLES.length;
    }
}