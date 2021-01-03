package com.merkado.merkadoclient.Adapters;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.merkado.merkadoclient.Fragments.SubDepFragment;
import com.merkado.merkadoclient.Model.DepartmentNames;

import java.util.List;

import static android.content.ContentValues.TAG;

public class DepsViewPagerAdapter extends FragmentStateAdapter {
    private final int numOfTabs;
    private Fragment fragment = null;
    private final List<DepartmentNames> departmentNames;

    public DepsViewPagerAdapter(@NonNull Fragment fragment, int numOfTabs, List<DepartmentNames> departmentNames) {
        super(fragment);
        this.numOfTabs = numOfTabs;
        this.departmentNames = departmentNames;
    }


    @NonNull
    @Override
    public Fragment createFragment(int position) {

        fragment = SubDepFragment.newInstance(departmentNames.get(position).getDepName());
        Log.d(TAG, "createFragment: viewPager " + departmentNames.get(position).getDepName());

        return fragment;
    }

    @Override
    public int getItemCount() {
        return numOfTabs;
    }
}
