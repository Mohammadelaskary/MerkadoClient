package com.merkado.merkadoclient.Fragments;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.merkado.merkadoclient.Adapters.ProductsAdapter;
import com.merkado.merkadoclient.Model.Cart;
import com.merkado.merkadoclient.Model.Product;
import com.merkado.merkadoclient.MyMethods;
import com.merkado.merkadoclient.R;
import com.merkado.merkadoclient.ViewModel.HomeViewModel;
import com.facebook.shimmer.ShimmerFrameLayout;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ExecutionException;


public class OffersFragment extends Fragment {

    Context context;
    HomeViewModel homeViewModel;
    List<Product> offers = new ArrayList<>();
    RecyclerView recyclerView;
    ProductsAdapter adapter;
    LinearLayout offersLayout;
    ShimmerFrameLayout offersShimmer;
    TextView noConnection;
    List<Cart> productsInCart = new ArrayList<>();

    public OffersFragment(Context context) {
        this.context = context;
    }

    public OffersFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_offers, container, false);

        recyclerView = view.findViewById(R.id.offers_rec);
        offersLayout = view.findViewById(R.id.offers_layout);
        offersShimmer = view.findViewById(R.id.offers_shimmer);
        noConnection = view.findViewById(R.id.no_connection);
        if (MyMethods.isConnected(Objects.requireNonNull(getContext()))) {
            try {
                initViewModel();

            } catch (ExecutionException | InterruptedException e) {
                e.printStackTrace();
            }
            getProductsInCart();
            initOffersRecycler();
            noConnection.setVisibility(View.GONE);
            offersLayout.setVisibility(View.VISIBLE);
            offersShimmer.startShimmer();
        } else {
            noConnection.setVisibility(View.VISIBLE);
            offersLayout.setVisibility(View.GONE);
        }


        return view;
    }

    private void getProductsInCart() {
        homeViewModel.getProductsInCartLiveData().observe(Objects.requireNonNull(getActivity()), new Observer<List<Cart>>() {
            @Override
            public void onChanged(List<Cart> carts) {
                productsInCart.clear();
                productsInCart.addAll(carts);

            }
        });
    }

    public void initViewModel() throws ExecutionException, InterruptedException {
        homeViewModel = ViewModelProvider.AndroidViewModelFactory.getInstance(Objects.requireNonNull(getActivity()).getApplication()).create(HomeViewModel.class);
        homeViewModel.init();
    }

    public void getOffers() {
        homeViewModel.getAllProducts().observe(Objects.requireNonNull(getActivity()), new Observer<List<Product>>() {
            @Override
            public void onChanged(List<Product> products) {
                offers.clear();
                for (Product product : products) {
                    Log.d("offers", product.getDep());
                    if (!product.getDiscount().equals("")) {
                        offers.add(product);
                        adapter.notifyDataSetChanged();
                    }
                }
                if (offers.isEmpty()) {
                    noConnection.setVisibility(View.VISIBLE);
                    noConnection.setText("لا يوجد عروض");
                }
                offersShimmer.stopShimmer();
                offersShimmer.setVisibility(View.GONE);
            }
        });
    }

    public void initOffersRecycler() {
        getOffers();
        adapter = new ProductsAdapter(getContext(), offers, productsInCart);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));
        recyclerView.setHasFixedSize(true);
    }
}