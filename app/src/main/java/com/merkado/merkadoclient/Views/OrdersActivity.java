package com.merkado.merkadoclient.Views;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.merkado.merkadoclient.Adapters.FullOrderAdapter;
import com.merkado.merkadoclient.Model.FullOrder;
import com.merkado.merkadoclient.Model.Order;
import com.merkado.merkadoclient.Model.PharmacyOrder;
import com.merkado.merkadoclient.ViewModel.HomeViewModel;
import com.merkado.merkadoclient.databinding.ActivityOrdersBinding;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ExecutionException;

import static android.content.ContentValues.TAG;

public class OrdersActivity extends AppCompatActivity {
    ActivityOrdersBinding binding;
    List<Order> ordersList = new ArrayList<>();
    FullOrderAdapter adapter;
    HomeViewModel homeViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityOrdersBinding.inflate(getLayoutInflater());
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        setContentView(binding.getRoot());

        Objects.requireNonNull(getSupportActionBar()).setTitle("الطلبـــــات الحالية");


        if (isConnected()) {
            try {
                initViewModel();
            } catch (ExecutionException | InterruptedException e) {
                e.printStackTrace();
            }
            getCurrentOrders();
            adapter = new FullOrderAdapter(this, ordersList, true);
            binding.fullOrderRecycler.setAdapter(adapter);
            binding.fullOrderRecycler.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, true));
            binding.fullOrderRecycler.setHasFixedSize(true);
        } else {
            binding.progressBar.hide();
            binding.noOrdersText.setVisibility(View.VISIBLE);
            binding.noOrdersText.setText("تأكد من الاتصال بالانترنت...");
        }

    }

    void getCurrentOrders() {
        homeViewModel.getAllCurrentOrdersLiveData().observe(OrdersActivity.this, new Observer<List<Order>>() {
            @Override
            public void onChanged(List<Order> orders) {
                if (!orders.isEmpty()) {
                    ordersList.clear();
                    for (Order order : orders) {
                            ordersList.add(order);
                            Log.d("orderUserId",order.getUserId());
                    }
                    adapter.notifyDataSetChanged();
                    binding.noOrdersText.setVisibility(View.GONE);
                    binding.noOrdersText.setText("لا يوجد طلبات ...");
                    binding.noOrders.setVisibility(View.GONE);
                } else {
                    binding.noOrdersText.setVisibility(View.VISIBLE);
                    binding.noOrdersText.setText("لا يوجد طلبات ...");
                    binding.noOrders.setVisibility(View.VISIBLE);
                }
                binding.progressBar.hide();

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
                .getInstance(OrdersActivity.this.getApplication()).create(HomeViewModel.class);
        homeViewModel.init();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}