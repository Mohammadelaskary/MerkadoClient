package com.merkado.merkadoclient.Views;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.merkado.merkadoclient.Adapters.FullOrderAdapter;
import com.merkado.merkadoclient.Model.FullOrder;
import com.merkado.merkadoclient.ViewModel.HomeViewModel;
import com.merkado.merkadoclient.databinding.ActivityPreviousOrdersBinding;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ExecutionException;

public class PreviousOrders extends AppCompatActivity {
    ActivityPreviousOrdersBinding binding;
    List<FullOrder> list;
    FullOrderAdapter adapter;
    HomeViewModel homeViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPreviousOrdersBinding.inflate(getLayoutInflater());
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        setContentView(binding.getRoot());
        Objects.requireNonNull(getSupportActionBar()).setTitle("الطلبـــــات السابقة");


        list = new ArrayList<>();
        if (isConnected()) {
            try {
                initViewModel();
            } catch (ExecutionException | InterruptedException e) {
                e.printStackTrace();
            }
            isFinishedLoading();
            getData();


            adapter = new FullOrderAdapter(this, list, false);
            binding.fullOrderRecycler.setAdapter(adapter);
            binding.fullOrderRecycler.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, false));
            binding.fullOrderRecycler.setHasFixedSize(true);
        } else {
            binding.progressBar.hide();
            binding.noPreOrdersText.setVisibility(View.VISIBLE);
            binding.noPreOrdersText.setText("تأكد من الاتصال بالانترنت...");
        }

    }


    void getData() {
        homeViewModel.getAllPreOrdersLiveData().observe(PreviousOrders.this, new Observer<List<FullOrder>>() {
            @Override
            public void onChanged(List<FullOrder> fullOrders) {
                binding.progressBar.hide();
                list.clear();
                for (FullOrder order : fullOrders) {
                    if (order.getUserId().equals(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid())) {
                        list.add(order);
                        adapter.notifyDataSetChanged();
                    }
                }

                if (list.isEmpty()) {
                    binding.noPreOrdersText.setVisibility(View.VISIBLE);
                    binding.noPreOrdersText.setText("لا يوجد طلبات سابقة");
                } else {
                    binding.fullOrderRecycler.setVisibility(View.VISIBLE);
                }

            }
        });
    }

    private void isFinishedLoading() {
        homeViewModel.getPreOrdersFinishLoading().observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                if (aBoolean) {
                    if (list.isEmpty()) {
                        binding.progressBar.hide();
                        binding.noPreOrdersText.setVisibility(View.VISIBLE);
                        binding.noPreOrdersText.setText("لا يوجد طلبات سابقة...");
                        binding.noPreOrders.setVisibility(View.VISIBLE);
                    }
                }
            }
        });
    }

    public boolean isConnected() {
        boolean connected = false;
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
                connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED) {
            //we are connected to a network
            connected = true;
        }
        return connected;
    }

    public void initViewModel() throws ExecutionException, InterruptedException {
        homeViewModel = ViewModelProvider.AndroidViewModelFactory
                .getInstance(PreviousOrders.this.getApplication()).create(HomeViewModel.class);
        homeViewModel.init();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();


    }
}