package com.merkado.merkadoclient.Views;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;

import com.merkado.merkadoclient.Adapters.ProductsAdapter;
import com.merkado.merkadoclient.Model.Cart;
import com.merkado.merkadoclient.Model.Product;
import com.merkado.merkadoclient.R;
import com.merkado.merkadoclient.ViewModel.HomeViewModel;
import com.merkado.merkadoclient.databinding.ActivityProductsBinding;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ExecutionException;

public class ProductsActivity extends AppCompatActivity {
    HomeViewModel homeViewModel;
    List<Product> productsList = new ArrayList<>();
    ProductsAdapter adapter;
    String depName, subdep;
    ActivityProductsBinding binding;
    List<Cart> productsInCart = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityProductsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setSupportActionBar(binding.toolbar);
        Objects.requireNonNull(getSupportActionBar()).setTitle("");
        try {
            initViewModel();
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }
        initProductsRecycler();
        getProductsInCart();
        getNumberOfProductsInCartProducts();

        Intent intent = getIntent();
        depName = intent.getStringExtra("depName");
        subdep = intent.getStringExtra("subdep");
        Log.d("depName",depName);
        Log.d("subdepName",subdep);
        binding.depName.setText(depName);
        binding.subdepName.setText(subdep);
        binding.bigLogo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ProductsActivity.this, MainActivity.class);
                intent.putExtra("cart", "cart");
                startActivity(intent);
            }
        });
        Animation anim = AnimationUtils.loadAnimation(this, R.anim.bounce);
        binding.bigLogo.startAnimation(anim);

    }

    private void getProductsInCart() {
        homeViewModel.getProductsInCartLiveData().observe(this, new Observer<List<Cart>>() {
            @Override
            public void onChanged(List<Cart> carts) {
                productsInCart.clear();
                productsInCart.addAll(carts);
//                adapter.notifyDataSetChanged();
            }
        });
    }


    public void initViewModel() throws ExecutionException, InterruptedException {
        homeViewModel = ViewModelProvider.AndroidViewModelFactory.getInstance(this.getApplication()).create(HomeViewModel.class);
        homeViewModel.init();
    }

    public void getProducts() {


        homeViewModel.getAllProducts().observe(this, new Observer<List<Product>>() {
            @Override
            public void onChanged(List<Product> products) {
                productsList.clear();
                for (Product product : products) {
                    if (product.getDep().equals(depName) && product.getSubDep().equals(subdep)) {
                        productsList.add(product);
                        adapter.notifyDataSetChanged();
                    }
                }
                if (productsList.isEmpty())
                    binding.noProducts.setVisibility(View.VISIBLE);
                else
                    binding.noProducts.setVisibility(View.GONE);
            }
        });
    }

    public void initProductsRecycler() {
        getProducts();
        adapter = new ProductsAdapter(this, productsList, productsInCart);
        binding.productsRec.setAdapter(adapter);
        binding.productsRec.setLayoutManager(new GridLayoutManager(this, 2));
        binding.productsRec.setHasFixedSize(true);
    }

    public void getNumberOfProductsInCartProducts() {
        homeViewModel.getNumberOfProductsInCart().observe(this, new Observer<Integer>() {
            @Override
            public void onChanged(Integer integer) {
                if (integer == 0) {
                    binding.cartCount.setVisibility(View.GONE);
                } else {
                    binding.cartCount.setText(String.valueOf(integer));
                    binding.cartCount.setVisibility(View.VISIBLE);

                }

            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

    }
}