package com.merkado.merkadoclient.Views;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.merkado.merkadoclient.Database.ShippingData;
import com.merkado.merkadoclient.Model.Neighborhood;
import com.merkado.merkadoclient.Model.User;
import com.merkado.merkadoclient.MyApp;
import com.merkado.merkadoclient.R;
import com.merkado.merkadoclient.ViewModel.HomeViewModel;
import com.merkado.merkadoclient.databinding.ActivityUserDataBinding;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ExecutionException;

public class UserData extends AppCompatActivity {

    ActivityUserDataBinding binding;
    HomeViewModel homeViewModel;
    User user;
    List<String> citiesList = new ArrayList<>();
    ArrayAdapter<String> citiesAdapter;
    String city,governorate,neighborhood,streetName,buildingNo,appartmentNo;
    boolean isGovernorateChanged = false;
    boolean isCityChanged = false;
    boolean isStreetNameChanged = false;
    boolean isBuildingNoChanged = false;
    boolean isAppartmentNoChanged = false;
    boolean isFamousMarkChanged = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityUserDataBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());


        try {
            initViewModel();
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }

        getUserData();
        getAllCities();
        citiesList.add("اختر المدينة");
        citiesAdapter = new ArrayAdapter<>(UserData.this, android.R.layout.simple_spinner_dropdown_item, citiesList);
        binding.city.setAdapter(citiesAdapter);
        MainActivity.dataBase.myDao().deleteAllShippingData();
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("بيانات الطلب");
//        updateValue("mohamed","myname");
        Objects.requireNonNull(binding.username.getEditText()).addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                binding.username.setError(null);
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                binding.username.setError(null);
            }

            @Override
            public void afterTextChanged(Editable editable) {
                String username = binding.username.getEditText().getText().toString().trim();
                if (username.isEmpty())
                    binding.username.setError("من فضلك ادخل اسم العميل");
                else
                    binding.username.setError(null);
            }
        });
        Objects.requireNonNull(binding.mobileNumber.getEditText()).addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                binding.mobileNumber.setError(null);
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                binding.mobileNumber.setError(null);
            }

            @Override
            public void afterTextChanged(Editable editable) {
                String mobileNumber = binding.mobileNumber.getEditText().getText().toString().trim();
                if (mobileNumber.isEmpty())
                    binding.mobileNumber.setError("من فضلك ادخل رقم المحمول");
                else
                    binding.mobileNumber.setError(null);
            }
        });
        Objects.requireNonNull(binding.governorate.getEditText()).addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                binding.governorate.setError(null);
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                binding.governorate.setError(null);
            }

            @Override
            public void afterTextChanged(Editable editable) {
                String governorate = binding.governorate.getEditText().getText().toString().trim();

                if (governorate.isEmpty())
                    binding.governorate.setError("من فضلك ادخل اسم المحافظة");
                else{
                    binding.governorate.setError(null);
                    isGovernorateChanged = true;
                }
            }
        });

        Objects.requireNonNull(binding.streetName.getEditText()).addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                binding.streetName.setError(null);
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                binding.streetName.setError(null);
            }

            @Override
            public void afterTextChanged(Editable editable) {
                String streetName = binding.streetName.getEditText().getText().toString().trim();
                if (streetName.isEmpty())
                    binding.streetName.setError("من فضلك ادخل اسم الشارع");
                else{
                    binding.streetName.setError(null);
                    isStreetNameChanged = true;
                }
            }
        });
        Objects.requireNonNull(binding.buildingNo.getEditText()).addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                binding.buildingNo.setError(null);
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                binding.buildingNo.setError(null);
            }

            @Override
            public void afterTextChanged(Editable editable) {
                String buildingNo = binding.buildingNo.getEditText().getText().toString().trim();

                if (buildingNo.isEmpty())
                    binding.buildingNo.setError("من فضلك ادخل رقم المنزل");
                else{
                    binding.streetName.setError(null);
                    isBuildingNoChanged = true;
                }
            }
        });
        Objects.requireNonNull(binding.appartmentNo.getEditText()).addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                binding.appartmentNo.setError(null);
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                binding.appartmentNo.setError(null);
            }

            @Override
            public void afterTextChanged(Editable editable) {
                String appartmentNo = binding.appartmentNo.getEditText().getText().toString().trim();
                if (appartmentNo.isEmpty())
                    binding.appartmentNo.setError("من فضلك ادخل رقم الشقة");
                else{
                    binding.appartmentNo.setError(null);
                    isAppartmentNoChanged = true;
                }
            }

        });



        binding.famousMark.getEditText().addTextChangedListener(
                new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {

                    }

                    @Override
                    public void afterTextChanged(Editable s) {
                        isFamousMarkChanged = true;
                    }
                });

                binding.city.setOnItemSelectedListener(
                        new AdapterView.OnItemSelectedListener() {
                               @Override
                               public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                   isCityChanged = true;
                               }

                               @Override
                               public void onNothingSelected(AdapterView<?> parent) {

                               }
                           });

                        binding.summery.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                String username = binding.username.getEditText().getText().toString().trim();
                                String phoneNumber = Objects.requireNonNull(binding.phoneNumber.getEditText()).getText().toString().trim();
                                String mobileNumber = binding.mobileNumber.getEditText().getText().toString().trim();
                                governorate = binding.governorate.getEditText().getText().toString().trim();
                                streetName = binding.streetName.getEditText().getText().toString().trim();
                                buildingNo = binding.buildingNo.getEditText().getText().toString().trim();
                                appartmentNo = binding.appartmentNo.getEditText().getText().toString().trim();
                                String promoCode = Objects.requireNonNull(binding.promoCode.getEditText()).getText().toString().trim();
                                city = binding.city.getSelectedItem().toString();
                                neighborhood = Objects.requireNonNull(binding.neighborhood.getEditText()).getText().toString().trim();
                                String famousMark = binding.famousMark.getEditText().getText().toString().trim();
                                if (username.isEmpty())
                                    binding.username.setError("ادخل اسم العميل");
                                if (mobileNumber.isEmpty())
                                    binding.mobileNumber.setError("ادخل رقم المحمول");
                                if (governorate.isEmpty())
                                    binding.governorate.setError("ادخل المحافظة");
                                if (neighborhood.isEmpty())
                                    binding.neighborhood.setError("ادخل اسم الحي");
                                if (streetName.isEmpty())
                                    binding.streetName.setError("ادخل اسم الشارع/المنطقة");
                                if (buildingNo.isEmpty())
                                    binding.buildingNo.setError("ادخل رقم المنزل");
                                if (appartmentNo.isEmpty())
                                    binding.appartmentNo.setError("ادخل رقم الشقة");
                                if (!username.isEmpty()
                                        && !mobileNumber.isEmpty()
                                        && !appartmentNo.isEmpty()
                                        && !governorate.isEmpty()
                                        && !neighborhood.isEmpty()
                                        && !streetName.isEmpty()
                                        && !buildingNo.isEmpty()) {

                                    String address = buildingNo + streetName + " ، " + neighborhood + " ، " + city + " ، " + governorate + " ، شقة رقم " + appartmentNo;
                                    ShippingData shippingData = new ShippingData(username, mobileNumber, phoneNumber, address);
                                    shippingData.setCity(city);
                                    if (!famousMark.isEmpty())
                                        shippingData.setFamousMark(famousMark);

                                    if (isCityChanged)
                                        updateValue(city, "city");
                                    if (isAppartmentNoChanged)
                                        updateValue(appartmentNo, "appartmentNo");
                                    if (isBuildingNoChanged)
                                        updateValue(buildingNo, "buildingNo");
                                    if (isFamousMarkChanged)
                                        updateValue(famousMark, "famousMark");
                                    if (isGovernorateChanged)
                                        updateValue(governorate, "governorate");
                                    if (isStreetNameChanged)
                                        updateValue(streetName, "streetName");
                                    MainActivity.dataBase.myDao().addShippingData(shippingData);
                                    Intent intent1 = getIntent();
                                    int subtractedPoints = intent1.getIntExtra("subtractedPoints", 0);
                                    Intent intent = new Intent(UserData.this, OrderSent.class);
                                    intent.putExtra("subtractedPoints", subtractedPoints);
                                    if (!promoCode.isEmpty())
                                        intent.putExtra("promoCode", promoCode);
                                    startActivity(intent);
                                    finish();
                                }
                            }
                        });

    }

    private void updateValue(String value,String type) {
        Map<String,Object> newValue = new HashMap<>();
        newValue.put(type,value);
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
        Query query = reference.orderByKey().equalTo(FirebaseAuth.getInstance().getCurrentUser().getUid());
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot:snapshot.getChildren())
                    dataSnapshot.getRef().updateChildren(newValue);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


    public void initViewModel() throws ExecutionException, InterruptedException {
        homeViewModel = ViewModelProvider.AndroidViewModelFactory
                .getInstance(UserData.this.getApplication()).create(HomeViewModel.class);
        homeViewModel.init();
    }

    public void getUserData() {
        homeViewModel.getAllUsers().observe(this, new Observer<List<User>>() {
            @Override
            public void onChanged(List<User> users) {
                String userId = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
                for (User user1 : users) {
                    if (userId.equals(user1.getUserId()))
                        user = user1;
                }
                binding.form.setVisibility(View.VISIBLE);
                binding.loading.hide();
                String customerName = user.getCustomerName();
                Objects.requireNonNull(binding.username.getEditText()).setText(customerName);
                String mobileNumber = user.getMobileNumber();
                Objects.requireNonNull(binding.mobileNumber.getEditText()).setText(mobileNumber);
                String phoneNumber = user.getPhoneNumber();
                if (!phoneNumber.isEmpty())
                    Objects.requireNonNull(binding.phoneNumber.getEditText()).setText(phoneNumber);
                city = user.getCity();
                governorate = user.getGovernorate();
                neighborhood = user.getNeighborhood();
                streetName = user.getStreetName();
                buildingNo = user.getBuildingNo();
                appartmentNo = user.getAppartmentNo();
                Objects.requireNonNull(binding.governorate.getEditText()).setText(governorate);
                Objects.requireNonNull(binding.neighborhood.getEditText()).setText(neighborhood);
                Objects.requireNonNull(binding.streetName.getEditText()).setText(streetName);
                Objects.requireNonNull(binding.buildingNo.getEditText()).setText(buildingNo);
                Objects.requireNonNull(binding.appartmentNo.getEditText()).setText(appartmentNo);
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // todo: goto back activity from here

                Intent intent = new Intent(UserData.this, MainActivity.class);
                intent.putExtra("cart", "cart");
                startActivity(intent);
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }


    private void getAllCities() {
        homeViewModel.getNeighborhoodLiveData().observe(this, new Observer<List<Neighborhood>>() {
            @Override
            public void onChanged(List<Neighborhood> neighborhoods) {
                citiesList.clear();
                for (Neighborhood neighborhood : neighborhoods) {
                    citiesList.add(neighborhood.getNeighborhood());
                }
                citiesAdapter.notifyDataSetChanged();
                binding.neighborhoodLoading.hide();

                if (citiesList.contains(city)) {
                    int position = citiesAdapter.getPosition(city);
                    binding.city.setSelection(position);
                } else {
                    new AlertDialog.Builder(UserData.this)
                            .setMessage(getString(R.string.apologize))

                            // Specifying a listener allows you to take an action before dismissing the dialog.
                            // The dialog is automatically dismissed when a dialog button is clicked.
                            .setPositiveButton("خروج", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                    Intent intent = new Intent(UserData.this, MainActivity.class);
                                    intent.putExtra("cart", "");
                                    startActivity(intent);
                                    finish();
                                }
                            })
                            .setNegativeButton("تغيير المدينة", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    dialogInterface.dismiss();
                                }
                            })

                            // A null listener allows the button to dismiss the dialog and take no further action.
                            .setIcon(android.R.drawable.ic_dialog_alert)
                            .show();
                }

            }
        });
    }

}