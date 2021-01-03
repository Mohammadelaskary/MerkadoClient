package com.merkado.merkadoclient.Fragments;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager2.widget.ViewPager2;

import com.merkado.merkadoclient.Adapters.DepsViewPagerAdapter;
import com.merkado.merkadoclient.Model.DepartmentNames;
import com.merkado.merkadoclient.R;
import com.merkado.merkadoclient.ViewModel.HomeViewModel;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ExecutionException;


public class DepartmentsFragment extends Fragment {

    Context context;
    HomeViewModel homeViewModel;
    List<DepartmentNames> depNames = new ArrayList<>();
    TabLayout tabLayout;
    ViewPager2 viewPager;
    int numberOfCategories = -1;
    DepsViewPagerAdapter adapter;

    public DepartmentsFragment() {
    }

    public DepartmentsFragment(Context context) {
        this.context = context;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_departments, container, false);
        try {
            initViewModel();
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }
        getDepartmentsNames();
        tabLayout = view.findViewById(R.id.tab_layout);
        viewPager = view.findViewById(R.id.deps_view_pager);

        return view;
    }

    public void initViewModel() throws ExecutionException, InterruptedException {
        homeViewModel = ViewModelProvider.AndroidViewModelFactory.getInstance(Objects.requireNonNull(getActivity()).getApplication()).create(HomeViewModel.class);
        homeViewModel.init();
    }

    public void getDepartmentsNames() {
        homeViewModel.getDepNamesLiveData().observe(Objects.requireNonNull(getActivity()), new Observer<List<DepartmentNames>>() {
            @Override
            public void onChanged(List<DepartmentNames> departmentNames) {
                depNames.clear();
                numberOfCategories = 0;
                numberOfCategories = departmentNames.size();
                depNames.addAll(departmentNames);

                adapter = new DepsViewPagerAdapter(DepartmentsFragment.this,
                        depNames.size(), depNames);
                viewPager.setAdapter(adapter);
                new TabLayoutMediator(tabLayout, viewPager,
                        new TabLayoutMediator.TabConfigurationStrategy() {
                            @Override
                            public void onConfigureTab(@NonNull TabLayout.Tab tab, int position) {
                                tab.setText(depNames.get(position).getDepName());
                            }
                        }).attach();
            }
        });
    }

    public void switchContent(int id, Fragment fragment) {
        FragmentTransaction ft = Objects.requireNonNull(getActivity()).getSupportFragmentManager().beginTransaction();
        ft.replace(id, fragment, fragment.toString());
        ft.addToBackStack(null);
        ft.commit();
    }
}