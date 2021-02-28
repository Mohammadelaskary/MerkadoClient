package com.merkado.merkadoclient.Views;

import android.content.Intent;
import android.content.IntentSender;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.room.Room;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.merkado.merkadoclient.Adapters.BottomNavPagerAdapter;
import com.merkado.merkadoclient.Database.MyDatabase;
import com.merkado.merkadoclient.Fragments.CartFragment;
import com.merkado.merkadoclient.Fragments.DepartmentsFragment;
import com.merkado.merkadoclient.Fragments.HomeFragment;
import com.merkado.merkadoclient.Fragments.LoginFragment;
import com.merkado.merkadoclient.Fragments.OffersFragment;
import com.merkado.merkadoclient.Fragments.ProfileFragment;
import com.merkado.merkadoclient.R;
import com.merkado.merkadoclient.ViewModel.HomeViewModel;
import com.merkado.merkadoclient.databinding.ActivityMainBinding;
import com.google.android.material.badge.BadgeDrawable;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.play.core.appupdate.AppUpdateInfo;
import com.google.android.play.core.appupdate.AppUpdateManager;
import com.google.android.play.core.appupdate.AppUpdateManagerFactory;
import com.google.android.play.core.install.model.AppUpdateType;
import com.google.android.play.core.install.model.UpdateAvailability;
import com.google.android.play.core.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.shashank.sony.fancytoastlib.FancyToast;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ExecutionException;


public class MainActivity extends AppCompatActivity {
    private static final String TAG = "main";
    private static final int MY_REQUEST_CODE = 100;
    ActivityMainBinding binding;
    private static final int HOME_ID = R.id.home;
    private static final int DEPS_ID = R.id.deps;
    private static final int CART_ID = R.id.cart;
    private static final int OFFERS_ID = R.id.offers;
    private static final int PROFILE_ID = R.id.profile;
    BottomNavPagerAdapter adapter;
    HomeViewModel homeViewModel;
    BadgeDrawable badge;
    public static MyDatabase dataBase;
    boolean allProductsFinishedLoading;
    private Menu menu;


    private final BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = item -> {
        switch (item.getItemId()) {
            case HOME_ID:
                switchToHomeFragment();
                return true;
            case DEPS_ID:
                switchToDepartmentsFragment();
                return true;
            case CART_ID: {
                if (FirebaseAuth.getInstance().getCurrentUser() == null) {
                    switchToLoginFragment();
                } else {
                    switchToCartFragment();
                }
            }
            return true;
            case OFFERS_ID:
                switchToOffersFragment();
                return true;
            case PROFILE_ID:
                if (FirebaseAuth.getInstance().getCurrentUser() == null) {
                    switchToLoginFragment();
                } else {
                    switchToProfileFragment();
                }
                return true;
        }
        return false;
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        deleteDatabase("Database");
        dataBase = Room.databaseBuilder(this, MyDatabase.class, "Database")
                .fallbackToDestructiveMigration()
                .allowMainThreadQueries().build();

        checkForUpdates();
        if (FirebaseAuth.getInstance().getCurrentUser() != null)
            uploadToken();

        Intent intent = getIntent();
        initMain();
        String cart = intent.getStringExtra("cart");
        Log.d(TAG, "onCreate: " + cart);
        assert cart != null;
        if (cart.equals("cart")) {
            if (FirebaseAuth.getInstance().getCurrentUser() != null) {
                binding.bottomNav.setSelectedItemId(CART_ID);
                switchToCartFragment();
            } else {
                binding.bottomNav.setSelectedItemId(CART_ID);
                switchToLoginFragment();
            }
        }

        setSupportActionBar(binding.toolbar);
        Objects.requireNonNull(getSupportActionBar()).setTitle("");
        try {
            initViewModel();
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }
        getNumberOfProductsInCartProducts();
        badge = binding.bottomNav.getOrCreateBadge(CART_ID);
        binding.bottomNav.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        adapter = new BottomNavPagerAdapter(this, this);
        binding.bigLogo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (FirebaseAuth.getInstance().getCurrentUser() != null) {
                    binding.bottomNav.setSelectedItemId(CART_ID);
                    switchToCartFragment();
                } else {
                    binding.bottomNav.setSelectedItemId(CART_ID);
                    switchToLoginFragment();
                }
            }
        });
        isAllProductsFinishedLoading();

    }

    private void uploadToken() {
        FirebaseInstanceId.getInstance().getInstanceId().addOnSuccessListener(instanceIdResult -> {
            String token = instanceIdResult.getToken();
            Map<String, Object> map = new HashMap<>();
            map.put("messagingToken", token);
            DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
            Query query = reference.orderByKey().equalTo(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid());
            query.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    for (DataSnapshot dataSnapshot : snapshot.getChildren())
                        dataSnapshot.getRef().updateChildren(map);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        });
    }

    private void checkForUpdates() {
        // Creates instance of the manager.
        AppUpdateManager appUpdateManager = AppUpdateManagerFactory.create(this);

// Returns an intent object that you use to check for an update.
        Task<AppUpdateInfo> appUpdateInfoTask = appUpdateManager.getAppUpdateInfo();

// Checks that the platform will allow the specified type of update.
        appUpdateInfoTask.addOnSuccessListener(appUpdateInfo -> {
            if (appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE
                    // For a flexible update, use AppUpdateType.FLEXIBLE
                    && appUpdateInfo.isUpdateTypeAllowed(AppUpdateType.IMMEDIATE)) {
                try {
                    appUpdateManager.startUpdateFlowForResult(
                            // Pass the intent that is returned by 'getAppUpdateInfo()'.
                            appUpdateInfo,
                            // Or 'AppUpdateType.FLEXIBLE' for flexible updates.
                            AppUpdateType.IMMEDIATE,
                            // The current activity making the update request.
                            this,
                            // Include a request code to later monitor this update request.
                            MY_REQUEST_CODE);
                } catch (IntentSender.SendIntentException e) {
                    e.printStackTrace();
                }

            }
        });

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == MY_REQUEST_CODE) {
            if (resultCode != RESULT_OK) {
                checkForUpdates();
                // If the update is cancelled or fails,
                // you can request to start the update again.
            } else {
                FancyToast.makeText(MainActivity.this, "تم تحديث التطبيق بنجاح ...", FancyToast.LENGTH_SHORT, FancyToast.SUCCESS, false).show();

            }
        }
    }

    private void initMain() {
        binding.bottomNav.setSelectedItemId(HOME_ID);
        switchToHomeFragment();
    }

    public void switchToHomeFragment() {
        FragmentManager manager = getSupportFragmentManager();
        manager.beginTransaction().replace(R.id.fragment_container, new HomeFragment(MainActivity.this)).addToBackStack(null).commit();
    }

    public void switchToDepartmentsFragment() {
        FragmentManager manager = getSupportFragmentManager();
        manager.beginTransaction().replace(R.id.fragment_container, new DepartmentsFragment(MainActivity.this)).addToBackStack(null).commit();
    }

    public void switchToCartFragment() {
        FragmentManager manager = getSupportFragmentManager();
        manager.beginTransaction().replace(R.id.fragment_container, new CartFragment(MainActivity.this)).addToBackStack(null).commit();
    }

    public void switchToOffersFragment() {
        FragmentManager manager = getSupportFragmentManager();
        manager.beginTransaction().replace(R.id.fragment_container, new OffersFragment(MainActivity.this)).addToBackStack(null).commit();
    }

    public void switchToProfileFragment() {
        FragmentManager manager = getSupportFragmentManager();
        manager.beginTransaction()

                .replace(R.id.fragment_container, new ProfileFragment(MainActivity.this)).addToBackStack(null).commit();
    }

    public void switchToLoginFragment() {
        FragmentManager manager = getSupportFragmentManager();
        manager.beginTransaction().addToBackStack(null).replace(R.id.fragment_container, new LoginFragment(MainActivity.this)).commit();
    }


    public void initViewModel() throws ExecutionException, InterruptedException {
        homeViewModel = ViewModelProvider.AndroidViewModelFactory
                .getInstance(MainActivity.this.getApplication()).create(HomeViewModel.class);
        homeViewModel.init();
    }

    public void getNumberOfProductsInCartProducts() {
        homeViewModel.getNumberOfProductsInCart().observe(this, integer -> {

            if (integer == 0) {
                binding.cartCount.setVisibility(View.GONE);
                badge.setVisible(false);
            } else {
                binding.cartCount.setText(String.valueOf(integer));
                binding.cartCount.setVisibility(View.VISIBLE);
                badge.setNumber(integer);
            }

        });
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        this.menu = menu;
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            getMenuInflater().inflate(R.menu.logout, menu);
        } else {
            getMenuInflater().inflate(R.menu.login, menu);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.login:
                switchToLoginFragment();
                return true;
            case R.id.logout:
                logout();
                return true;
            case R.id.profile:
                goToProfile();
                return true;
            case R.id.search:
                goToSearch();
                return true;
            case R.id.sign_up:
                goToSignUp();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    private void goToSearch() {
        if (allProductsFinishedLoading) {
            Intent intent = new Intent(MainActivity.this, SearchActivity.class);
            startActivity(intent);
        } else
            FancyToast.makeText(this, "رجاء الانتظار", FancyToast.LENGTH_SHORT, FancyToast.DEFAULT, false).show();

    }

    private void goToSignUp() {
        Intent intent = new Intent(this, SignupActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    private void goToProfile() {
        switchToProfileFragment();
        binding.bottomNav.setSelectedItemId(PROFILE_ID);
    }

    private void logout() {
        menu.clear();
        getMenuInflater().inflate(R.menu.login, menu);
        FirebaseAuth.getInstance().signOut();
        binding.cartCount.setVisibility(View.GONE);
        badge.setVisible(false);
        initMain();
    }

    boolean doubleBackToExitPressedOnce = false;

    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            finish();
            super.onBackPressed();
            return;
        }

                if (binding.bottomNav.getSelectedItemId()!=HOME_ID){
                    initMain();

                } else {
                    this.doubleBackToExitPressedOnce = true;
                    Toast.makeText(this, "اضغط رجوع مرة أخري للخروج", Toast.LENGTH_SHORT).show();

                    new Handler().postDelayed(new Runnable() {

                        @Override
                        public void run() {
                            doubleBackToExitPressedOnce = false;
                        }
                    }, 2000);
                }
    }

    public void isAllProductsFinishedLoading() {
        homeViewModel.isAllProductsFinishLoading().observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                allProductsFinishedLoading = aBoolean;
            }
        });
    }
}