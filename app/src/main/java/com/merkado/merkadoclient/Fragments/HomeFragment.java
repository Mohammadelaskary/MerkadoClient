package com.merkado.merkadoclient.Fragments;

import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.LinearSnapHelper;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SnapHelper;

import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.merkado.merkadoclient.Adapters.AdImagesAdapter;
import com.merkado.merkadoclient.Adapters.ProductsAdapter;
import com.merkado.merkadoclient.Model.AdImages;
import com.merkado.merkadoclient.Model.Cart;
import com.merkado.merkadoclient.Model.OverTotalMoneyDiscount;
import com.merkado.merkadoclient.Model.Product;
import com.merkado.merkadoclient.MyMethods;
import com.merkado.merkadoclient.R;
import com.merkado.merkadoclient.ViewModel.HomeViewModel;
import com.merkado.merkadoclient.Views.PharmacyActivity;
import com.shashank.sony.fancytoastlib.FancyToast;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import static android.content.ContentValues.TAG;

public class HomeFragment extends Fragment {

    Context context;
    TextView mostSoldTitle, minimumText, discountText, discountTypeText, noConnection, todayOfferTitle;
    RecyclerView adsRecycler, mostSoldRecycler, todayOfferRecycler;
    ImageView openPharmacy;
    ShimmerFrameLayout mostSoldShimmer, todayOfferShimmer, adShimmer;
    AdImagesAdapter adImagesAdapter;
    ProductsAdapter mostSoldAdapter;
    ProductsAdapter toDayOfferAdapter;
    List<AdImages> adImagesList = new ArrayList<>();
    List<Product> mostSoldProducts = new ArrayList<>();
    List<Product> todayOfferProducts = new ArrayList<>();
    List<Product> allProducts = new ArrayList<>();
    List<Cart> productsInCart = new ArrayList<>();
    boolean allProductsFinishedLoading;
    int position;
    HomeViewModel homeViewModel;
    LinearLayout homeLayout, specialOffer, goShopping, mostSoldLayout;

    public HomeFragment(Context context) {
        this.context = context;
    }

    public HomeFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        try {
            initViewModel();
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }
        initAdsRecyclerView(view);


        Log.d(TAG, "onCreateView: " + allProductsFinishedLoading);
        initAdsRecyclerView(view);
        todayOfferShimmer = view.findViewById(R.id.today_offer_shimmer);
        mostSoldShimmer = view.findViewById(R.id.most_sold_shimmer);
        mostSoldTitle = view.findViewById(R.id.most_sold_title);
        adShimmer = view.findViewById(R.id.ad_shimmer);
        specialOffer = view.findViewById(R.id.special_offer);
        noConnection = view.findViewById(R.id.no_connection);
        mostSoldShimmer.showShimmer(true);
        todayOfferShimmer.showShimmer(true);
        goShopping = view.findViewById(R.id.go_shopping);
        homeLayout = view.findViewById(R.id.home_layout);
        minimumText = view.findViewById(R.id.minimum);
        discountText = view.findViewById(R.id.discount);
        discountTypeText = view.findViewById(R.id.discount_type);
        mostSoldLayout = view.findViewById(R.id.most_sold_layout);
        openPharmacy = view.findViewById(R.id.open_pharmacy);
//        Animation specialOfferanim = AnimationUtils.loadAnimation(context, R.anim.blink_anim);
//        specialOffer.startAnimation(specialOfferanim);
        if (MyMethods.isConnected(requireContext())) {
            getAllProducts();
            getAdImages();
            getSpecialOffer();
            getProductsInCart();
            noConnection.setVisibility(View.GONE);
            homeLayout.setVisibility(View.VISIBLE);
        } else {
            noConnection.setVisibility(View.VISIBLE);
            homeLayout.setVisibility(View.GONE);
        }
        initMostSoldRecyclerView(view);
        initTodayOfferRecyclerView(view);
        goShopping.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switchToDepartmentsFragment();
            }
        });

        openPharmacy.setOnClickListener(v -> {
            if (FirebaseAuth.getInstance().getCurrentUser()!=null) {
                Intent intent = new Intent(getContext(), PharmacyActivity.class);
                ActivityOptions options =
                        ActivityOptions.makeCustomAnimation(context, android.R.anim.slide_in_left, android.R.anim.slide_out_right);
                startActivity(intent, options.toBundle());
            } else {
                FancyToast.makeText(getContext(),"يجب تسجيل الدخول أولا..",FancyToast.LENGTH_LONG,FancyToast.WARNING,false).show();
            }
        });


        return view;
    }

    private void getProductsInCart() {
        homeViewModel.getProductsInCartLiveData().observe(requireActivity(), new Observer<List<Cart>>() {
            @Override
            public void onChanged(List<Cart> carts) {
                productsInCart.clear();
                productsInCart.addAll(carts);

            }
        });
    }

    private void initAdsRecyclerView(View view) {
        adsRecycler = view.findViewById(R.id.ads_recycler);
        adImagesList = new ArrayList<>();
        adImagesAdapter = new AdImagesAdapter(context, adImagesList);
        adsRecycler.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false));
        adsRecycler.setAdapter(adImagesAdapter);
        if (!adImagesList.isEmpty()) {
            position = adImagesList.size() / 2;
            adsRecycler.scrollToPosition(position);
        }
        adsRecycler.setOnFlingListener(null);
        SnapHelper snapHelper = new LinearSnapHelper();
        snapHelper.attachToRecyclerView(adsRecycler);
        adsRecycler.smoothScrollBy(5, 0);
    }

    private void getAdImages() {
        homeViewModel.getAdImages().observe(requireActivity(), new Observer<List<AdImages>>() {
            @Override
            public void onChanged(List<AdImages> adImages) {
                if (!adImages.isEmpty()) {
                    adShimmer.stopShimmer();
                    adShimmer.setVisibility(View.GONE);
                    adImagesList.clear();
                    adImagesList.addAll(adImages);
                    adImagesAdapter.notifyDataSetChanged();
                }
            }
        });
    }

    private void initMostSoldRecyclerView(View view) {
        mostSoldRecycler = view.findViewById(R.id.most_sold_recycler);
        mostSoldAdapter = new ProductsAdapter(context, mostSoldProducts, productsInCart);
        mostSoldRecycler.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false));
        mostSoldRecycler.setAdapter(mostSoldAdapter);
        mostSoldRecycler.setHasFixedSize(true);
    }

    private void initTodayOfferRecyclerView(View view) {
        todayOfferRecycler = view.findViewById(R.id.today_offer_recycler);
        toDayOfferAdapter = new ProductsAdapter(context, todayOfferProducts, productsInCart);
        todayOfferRecycler.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false));
        todayOfferRecycler.setAdapter(toDayOfferAdapter);
        todayOfferRecycler.setHasFixedSize(true);

    }


    private void autoScroll(final RecyclerView recyclerView) {

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (position == adImagesList.size()) {
                    position = 0;
                    recyclerView.smoothScrollToPosition(position);
                    recyclerView.smoothScrollBy(5, 0);

                } else {
                    position++;
                    recyclerView.smoothScrollToPosition(position);
                    recyclerView.smoothScrollBy(5, 0);

                }
                autoScroll(recyclerView);
            }
        }, 10000);

    }


    private void getMostSoldProducts() {
        for (Product product : allProducts) {
            if (product.isMostSold()&&product.isVisible()) {
                if (!mostSoldProducts.contains(product)) {
                    mostSoldProducts.add(product);
                    mostSoldAdapter.notifyDataSetChanged();
                }
            }
            if (mostSoldProducts.isEmpty()) {
                mostSoldTitle.setVisibility(View.GONE);
                mostSoldLayout.setVisibility(View.GONE);
            } else {
                mostSoldTitle.setVisibility(View.VISIBLE);
                mostSoldLayout.setVisibility(View.VISIBLE);
            }
        }
    }

    private void getTodayOfferProducts() {
        for (Product product : allProducts) {
            Log.d(TAG, "getTodayOfferProducts: " + product.isTodaysOffer());
            if (product.isTodaysOffer()&&product.isVisible()) {
                if (!todayOfferProducts.contains(product)) {
                    todayOfferProducts.add(product);
                    toDayOfferAdapter.notifyDataSetChanged();
                }
            }


        }
    }

    public void initViewModel() throws ExecutionException, InterruptedException {
        homeViewModel = ViewModelProvider.AndroidViewModelFactory.getInstance(requireActivity().getApplication()).create(HomeViewModel.class);
        homeViewModel.init();
    }

    public void getAllProducts() {
        homeViewModel.getAllProducts().observe(requireActivity(), new Observer<List<Product>>() {
            @Override
            public void onChanged(List<Product> products) {
                allProducts.clear();
                mostSoldProducts.clear();
                todayOfferProducts.clear();
                if (!products.isEmpty()) {
                    mostSoldShimmer.stopShimmer();
                    mostSoldShimmer.setVisibility(View.GONE);
                    todayOfferShimmer.stopShimmer();
                    todayOfferShimmer.setVisibility(View.GONE);
                    allProducts.addAll(products);
                    getMostSoldProducts();
                    getTodayOfferProducts();

                }
            }
        });
    }

    public void getSpecialOffer() {
        homeViewModel.getDiscountMutableLiveData().observe(requireActivity(), new Observer<OverTotalMoneyDiscount>() {
            @Override
            public void onChanged(OverTotalMoneyDiscount overTotalMoneyDiscount) {
                if (overTotalMoneyDiscount == null)
                    specialOffer.setVisibility(View.GONE);
                else {
                    specialOffer.setVisibility(View.VISIBLE);
                    String minimum = overTotalMoneyDiscount.getMinimum();
                    String discount = overTotalMoneyDiscount.getDiscount();
                    String discountType = overTotalMoneyDiscount.getDiscount_unit();
                    minimumText.setText(minimum);
                    discountText.setText(discount);
                    discountTypeText.setText(discountType);
                }
            }
        });

    }

    public void switchToDepartmentsFragment() {
        FragmentManager manager = ((AppCompatActivity) requireContext()).getSupportFragmentManager();
        manager.beginTransaction().replace(R.id.fragment_container, new DepartmentsFragment(getContext())).commit();
    }

    @Override
    public void onResume() {
        super.onResume();
        autoScroll(adsRecycler);

    }

    @Override
    public void onPause() {
        super.onPause();

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

    }
}