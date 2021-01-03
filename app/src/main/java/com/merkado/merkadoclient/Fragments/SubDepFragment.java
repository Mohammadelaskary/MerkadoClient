package com.merkado.merkadoclient.Fragments;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.merkado.merkadoclient.Adapters.SubDepAdapter;
import com.merkado.merkadoclient.Interfaces.SubDepInterface;
import com.merkado.merkadoclient.Model.SubDeparment;
import com.merkado.merkadoclient.R;
import com.merkado.merkadoclient.ViewModel.HomeViewModel;
import com.merkado.merkadoclient.Views.ProductsActivity;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ExecutionException;

import static android.content.ContentValues.TAG;


public class SubDepFragment extends Fragment {


    private String depName;
    private RecyclerView subDepRecycler;
    List<SubDeparment> subDeparmentsList = new ArrayList<>();
    HomeViewModel homeViewModel;
    SubDepAdapter adapter;

    public SubDepFragment(String depName) {
        this.depName = depName;
    }

    public SubDepFragment() {
    }

    public static SubDepFragment newInstance(String depName) {
        return new SubDepFragment(depName);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_sub_dep, container, false);
        subDepRecycler = view.findViewById(R.id.subdep_rec);
        try {
            initViewModel();
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }
        initSubDepRecycler();
        return view;
    }

    public void initSubDepRecycler() {
        getSubDeps();
        adapter = new SubDepAdapter(getContext(), subDeparmentsList, depName, new SubDepInterface() {
            @Override
            public void onItemClicked(SubDeparment item) {

                Intent intent = new Intent(getActivity(), ProductsActivity.class);
                intent.putExtra("depName", item.getDepName());
                intent.putExtra("subdep", item.getSubdepName());
                Log.d("depName_fragment", item.getDepName());
                Log.d("subdepName_fragment", item.getSubdepName());
                Objects.requireNonNull(getActivity()).startActivity(intent);
            }
        });
        subDepRecycler.setAdapter(adapter);
        subDepRecycler.setLayoutManager(new GridLayoutManager(getContext(), 3));
        subDepRecycler.setHasFixedSize(true);
    }

    public void getSubDeps() {
        homeViewModel.getSubDepsLiveData().observe(Objects.requireNonNull(getActivity()), new Observer<List<SubDeparment>>() {
            @Override
            public void onChanged(List<SubDeparment> subDeparments) {
                subDeparmentsList.clear();
                for (SubDeparment subDeparment : subDeparments) {
                    Log.d(TAG, "onChanged:subdep " + depName);
                    if (subDeparment.getDepName().equals(depName)) {
                        subDeparmentsList.add(subDeparment);
                        adapter.notifyDataSetChanged();
                    }
                }
            }
        });
    }

    public void initViewModel() throws ExecutionException, InterruptedException {
        homeViewModel = ViewModelProvider.AndroidViewModelFactory.getInstance(Objects.requireNonNull(getActivity()).getApplication()).create(HomeViewModel.class);
        homeViewModel.init();
    }
}