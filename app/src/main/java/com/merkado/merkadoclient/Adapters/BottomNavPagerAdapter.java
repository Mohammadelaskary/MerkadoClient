package com.merkado.merkadoclient.Adapters;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.merkado.merkadoclient.Fragments.CartFragment;
import com.merkado.merkadoclient.Fragments.DepartmentsFragment;
import com.merkado.merkadoclient.Fragments.HomeFragment;
import com.merkado.merkadoclient.Fragments.OffersFragment;
import com.merkado.merkadoclient.Fragments.ProfileFragment;


public class BottomNavPagerAdapter extends FragmentStateAdapter {
    private static final int HOME_ID = 0;
    private static final int DEPS_ID = 1;
    private static final int CART_ID = 2;
    private static final int OFFERS_ID = 3;
    private static final int PROFILE_ID = 4;
    Context context;

    public BottomNavPagerAdapter(@NonNull FragmentActivity fragment, Context context) {
        super(fragment);
        this.context = context;
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        Fragment fragment = null;
        switch (position) {
            case HOME_ID:
                fragment = new HomeFragment(context);
                break;
            case DEPS_ID:
                fragment = new DepartmentsFragment(context);
                break;
            case CART_ID:
                fragment = new CartFragment(context);
                break;
            case OFFERS_ID:
                fragment = new OffersFragment(context);
                break;
            case PROFILE_ID:
                fragment = new ProfileFragment(context);
                break;
        }
        assert fragment != null;
        return fragment;
    }

    @Override
    public int getItemCount() {
        return 5;
    }
}
