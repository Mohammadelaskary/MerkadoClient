package com.merkado.merkadoclient.Views;

import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;

import com.merkado.merkadoclient.Adapters.ProductsSearchAdapter;
import com.merkado.merkadoclient.Model.Cart;
import com.merkado.merkadoclient.Model.Product;
import com.merkado.merkadoclient.MyMethods;
import com.merkado.merkadoclient.ViewModel.HomeViewModel;
import com.merkado.merkadoclient.databinding.ActivitySearchBinding;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class SearchActivity extends AppCompatActivity {
    ActivitySearchBinding binding;
    List<Product> allProducts = new ArrayList<>();
    List<Cart> productsInCart = new ArrayList<>();

    ProductsSearchAdapter adapter;
    HomeViewModel homeViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySearchBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setSupportActionBar(binding.toolbar);
        binding.searchProductsText.setIconifiedByDefault(false);
        binding.searchProductsText.requestFocus();
        binding.searchProducts.setVisibility(View.VISIBLE);
        if (MyMethods.isConnected(this)) {
            try {
                initViewModel();
            } catch (ExecutionException | InterruptedException e) {
                e.printStackTrace();
            }


            getAllProducts();

            getProductsInCart();
            configurateSearch();

        } else {
            binding.progressBar.hide();
            binding.noConnection.setVisibility(View.VISIBLE);
        }

    }

    private void configurateSearch() {
        binding.searchProductsText.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                adapter.getFilter().filter(newText);
                return true;
            }
        });
    }

    private void initProductsResult() {
        adapter = new ProductsSearchAdapter(SearchActivity.this, allProducts, productsInCart);
        binding.searchProducts.setAdapter(adapter);
        binding.searchProducts.setLayoutManager(new GridLayoutManager(this, 2));
        binding.searchProducts.setHasFixedSize(false);
    }

    private void getProductsInCart() {
        homeViewModel.getProductsInCartLiveData().observe(this, new Observer<List<Cart>>() {
            @Override
            public void onChanged(List<Cart> carts) {
                productsInCart.clear();
                productsInCart.addAll(carts);

            }
        });
    }

    private void getAllProducts() {
        homeViewModel.getAllProducts().observe(this, new Observer<List<Product>>() {
            @Override
            public void onChanged(List<Product> products) {
                allProducts.clear();
                binding.progressBar.hide();
                allProducts.addAll(products);
                initProductsResult();
                adapter.notifyDataSetChanged();
            }
        });
    }

    public void initViewModel() throws ExecutionException, InterruptedException {
        homeViewModel = ViewModelProvider.AndroidViewModelFactory
                .getInstance(SearchActivity.this.getApplication()).create(HomeViewModel.class);
        homeViewModel.init();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}