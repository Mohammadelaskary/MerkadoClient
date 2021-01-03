package com.merkado.merkadoclient.Fragments;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.widget.ContentLoadingProgressBar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.merkado.merkadoclient.Model.User;
import com.merkado.merkadoclient.MyMethods;
import com.merkado.merkadoclient.R;
import com.merkado.merkadoclient.ViewModel.HomeViewModel;
import com.merkado.merkadoclient.Views.AddComplaint;
import com.merkado.merkadoclient.Views.OrdersActivity;
import com.merkado.merkadoclient.Views.PreviousOrders;
import com.google.android.material.textview.MaterialTextView;
import com.google.firebase.auth.FirebaseAuth;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ExecutionException;


public class ProfileFragment extends Fragment {

    private static final int REQUEST_CODE = 100;
    Context context;
    HomeViewModel homeViewModel;
    List<User> allUsers = new ArrayList<>();
    String username;
    String promoCode;
    int count;
    String myId = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();

    public ProfileFragment(Context context) {
        this.context = context;
    }

    public ProfileFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    MaterialTextView usernameText, promoCodeText, mPoints, noConnection,
            currentOrders, preOrders, logout, sendComplaint;
    LinearLayout profileLayout;
    ContentLoadingProgressBar progressBar;
    String mobileNumber;
    ImageButton callUs, facebook,whatsApp;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        usernameText = view.findViewById(R.id.username);
        promoCodeText = view.findViewById(R.id.promo_code);
        mPoints = view.findViewById(R.id.my_points);
        currentOrders = view.findViewById(R.id.current_orders);
        preOrders = view.findViewById(R.id.pre_orders);
        logout = view.findViewById(R.id.logout);
        noConnection = view.findViewById(R.id.no_connection);
        profileLayout = view.findViewById(R.id.profile_layout);
        progressBar = view.findViewById(R.id.progress_bar);
        callUs = view.findViewById(R.id.call);
        sendComplaint = view.findViewById(R.id.send_complaint);
        facebook = view.findViewById(R.id.facebook);
        whatsApp = view.findViewById(R.id.whats_app);
        if (MyMethods.isConnected(Objects.requireNonNull(getContext()))) {
            try {
                initViewModel();
            } catch (ExecutionException | InterruptedException e) {
                e.printStackTrace();
            }
            getAllUsers();
            getContactNumber();
            noConnection.setVisibility(View.GONE);
            profileLayout.setVisibility(View.VISIBLE);
        } else {
            noConnection.setVisibility(View.VISIBLE);
            profileLayout.setVisibility(View.GONE);
        }
        currentOrders.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getActivity(), OrdersActivity.class));
            }
        });
        preOrders.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getActivity(), PreviousOrders.class));
            }
        });
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseAuth.getInstance().signOut();
                switchToLoginFragment();
            }
        });
        callUs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (context.checkSelfPermission(Manifest.permission.CALL_PHONE)
                            == PackageManager.PERMISSION_GRANTED) {
                        if (!mobileNumber.isEmpty())
                            makeCall(mobileNumber);

                    } else {
                        ActivityCompat.requestPermissions(Objects.requireNonNull(getActivity()), new String[]{Manifest.permission.CALL_PHONE}, REQUEST_CODE);
                    }
                }
            }
        });
        whatsApp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                try {
                    Intent sendMsg = new Intent(Intent.ACTION_VIEW);
                    String url = "https://api.whatsapp.com/send?phone=" + "+2"+ mobileNumber + "&text=" + URLEncoder.encode("", "UTF-8");
                    sendMsg.setPackage("com.whatsapp");
                    sendMsg.setData(Uri.parse(url));
                    if (context.getPackageManager()!=null)
                        startActivity(sendMsg);
                    else
                        Toast.makeText(context, "تطبيق الواتس اب غير موجود", Toast.LENGTH_SHORT).show();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        facebook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent;
                String pageId = "106281414591396";
                try {
                    intent = new Intent(Intent.ACTION_VIEW, Uri.parse("fb://page/" + pageId));
                } catch (Exception e) {
                    intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.facebook.com/" + pageId));
                }
                startActivity(intent);
            }
        });
        sendComplaint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getActivity(), AddComplaint.class));
            }
        });
        return view;
    }

    private void getContactNumber() {
        homeViewModel.getGetPhoneNumber().observe(Objects.requireNonNull(getActivity()), new Observer<String>() {
            @Override
            public void onChanged(String s) {
                mobileNumber = s;
            }
        });
    }

    private void makeCall(String s) {
        Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + s));
        startActivity(intent);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode,
                permissions,
                grantResults);
        if (requestCode == REQUEST_CODE) {
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                makeCall(mobileNumber);
            }
        }


    }

    private void getAllUsers() {
        homeViewModel.getAllUsers().observe(Objects.requireNonNull(getActivity()), new Observer<List<User>>() {
            @Override
            public void onChanged(List<User> users) {
                allUsers.clear();
                allUsers.addAll(users);
                progressBar.hide();
                getMyUsernameAndPoints();
            }
        });
    }

    private void getMyUsernameAndPoints() {
        for (User user : allUsers) {
            if (user.getUserId().equals(myId)) {
                username = user.getCustomerName();
                count = user.getCount();
                promoCode = user.getPromoCode();
                usernameText.setText(username);
                mPoints.setText(String.valueOf(count));
                promoCodeText.setText(promoCode);
            }
        }
    }

    public void initViewModel() throws ExecutionException, InterruptedException {
        homeViewModel = ViewModelProvider.AndroidViewModelFactory.getInstance(Objects.requireNonNull(getActivity()).getApplication()).create(HomeViewModel.class);
        homeViewModel.init();
    }

    public void switchToLoginFragment() {
        FragmentManager manager = ((AppCompatActivity) Objects.requireNonNull(getContext())).getSupportFragmentManager();
        manager.beginTransaction().replace(R.id.fragment_container, new LoginFragment(getContext())).commit();
    }

}