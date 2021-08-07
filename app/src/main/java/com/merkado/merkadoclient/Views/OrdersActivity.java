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
    List<FullOrder> list = new ArrayList<>();
    List<PharmacyOrder> myPharmacyOrders = new ArrayList<>();
    List<Order> orders = new ArrayList<>();
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
            isFinishedLoading();
            getCurrentOrders();
            adapter = new FullOrderAdapter(this, orders, true);
            binding.fullOrderRecycler.setAdapter(adapter);
            binding.fullOrderRecycler.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, true));
            binding.fullOrderRecycler.setHasFixedSize(true);
        } else {
            binding.progressBar.hide();
            binding.noOrdersText.setVisibility(View.VISIBLE);
            binding.noOrdersText.setText("تأكد من الاتصال بالانترنت...");
        }

    }

    private void addOrdersToList() {
        List<Integer> ordersIds = new ArrayList<>();
        for (FullOrder order:list){
            int id = order.getId();
            if (!ordersIds.contains(id)) {
                ordersIds.add(id);
            }
        }

        if (!myPharmacyOrders.isEmpty()) {
            for (PharmacyOrder pharmacyOrder : myPharmacyOrders) {
                int id = Integer.parseInt(pharmacyOrder.getOrderId());
                if (!ordersIds.contains(id)) {
                    ordersIds.add(id);
                }
            }
        }
        for (int id:ordersIds){
            Order order = new Order(id);
                for (FullOrder fullOrder : list) {
                    int orderId = fullOrder.getId();
                    if (orderId == id) {
                        order.setFullOrder(fullOrder);
                        break;
                    }
                }
            List<PharmacyOrder> pharmacyOrders = new ArrayList<>();
            if (!myPharmacyOrders.isEmpty()) {
                for (PharmacyOrder pharmacyOrder : myPharmacyOrders) {
                    int orderId = Integer.parseInt(pharmacyOrder.getOrderId());
                    if (orderId == id) {
                        pharmacyOrders.add(pharmacyOrder);
                    }
                }
            }
            order.setPharmacyOrders(pharmacyOrders);
            orders.add(order);
            adapter.notifyDataSetChanged();
        }

    }


    void  getMyPharmacyOrders(){
        homeViewModel.getMyPharmacyOrders().observe(this,pharmacyOrders -> {
            if (!pharmacyOrders.isEmpty()) {
                myPharmacyOrders.clear();
                myPharmacyOrders.addAll(pharmacyOrders);
                binding.noOrdersText.setVisibility(View.GONE);
                binding.noOrdersText.setText("لا يوجد طلبات ...");
                binding.noOrders.setVisibility(View.GONE);
            } else {
                binding.noOrdersText.setVisibility(View.VISIBLE);
                binding.noOrdersText.setText("لا يوجد طلبات ...");
                binding.noOrders.setVisibility(View.VISIBLE);
            }
            addOrdersToList();
        });
    }


    void getCurrentOrders() {
        homeViewModel.getAllCurrentOrdersLiveData().observe(OrdersActivity.this, new Observer<List<FullOrder>>() {
            @Override
            public void onChanged(List<FullOrder> fullOrders) {
                if (!fullOrders.isEmpty()) {
                    list.clear();
                    for (FullOrder order : fullOrders) {
                        if (order.getUserId().equals(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid())) {
                            list.add(order);

                        }
                    }
                    Log.d(TAG, "ordersSize "+list.size());
                    getMyPharmacyOrders();
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

    private void isFinishedLoading() {
        homeViewModel.getCurrentOrdersFinishLoading().observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                if (aBoolean) {
                    if (list.isEmpty()) {
                        binding.progressBar.hide();
                        binding.noOrdersText.setVisibility(View.VISIBLE);
                        binding.noOrdersText.setText("لا يوجد طلبات ...");
                        binding.noOrders.setVisibility(View.VISIBLE);
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
                .getInstance(OrdersActivity.this.getApplication()).create(HomeViewModel.class);
        homeViewModel.init();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}