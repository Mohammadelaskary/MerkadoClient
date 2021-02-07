package com.merkado.merkadoclient.Views;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
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
import com.merkado.merkadoclient.R;
import com.merkado.merkadoclient.ViewModel.HomeViewModel;
import com.merkado.merkadoclient.databinding.ActivityUserDataBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.shashank.sony.fancytoastlib.FancyToast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ExecutionException;

public class UserData extends AppCompatActivity implements View.OnClickListener, AdapterView.OnItemSelectedListener {

    private static final String TAG = "Userdata";
    ActivityUserDataBinding binding;
    HomeViewModel homeViewModel;
    User user;
    FirebaseDatabase database;
    List<Neighborhood> neighborhoods = new ArrayList<>();
    List<String> governoratesNames = new ArrayList<>();
    List<String> citiesNames       = new ArrayList<>();
    List<String> neighborhoodsNames = new ArrayList<>();
    ArrayAdapter<String> governoratesAdapter;
    ArrayAdapter<String> citiesAdapter;
    ArrayAdapter<String> neighborhoodsAdapter;
    String neighborhood,governorate, city,streetName,buildingNo,appartmentNo;
    boolean isGovernorateChanged = false;
    boolean isNeighborhoodChanged = false;
    boolean isStreetNameChanged = false;
    boolean isBuildingNoChanged = false;
    boolean isAppartmentNoChanged = false;
    boolean isFamousMarkChanged = false;
    boolean isCityChanged = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityUserDataBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        database = FirebaseDatabase.getInstance();

        try {
            initViewModel();
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }

        attachAdaptersToSpinners();
        getNeighborhoods();
        getUserData();
        setTextWatchers();
        connectViewsToOnClick();

        MainActivity.dataBase.myDao().deleteAllShippingData();
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("بيانات الطلب");

        binding.governorateNameSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String governorateName = binding.governorateNameSpinner.getSelectedItem().toString();
                Toast.makeText(UserData.this, governorateName+"selected", Toast.LENGTH_SHORT).show();
                getCitiesNames(governorateName);
                getNeighborhoodsNames(citiesNames.get(0));
                isCityChanged = true;
                isGovernorateChanged = true;
                isNeighborhoodChanged = true;
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        binding.cityNameSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String city = binding.cityNameSpinner.getSelectedItem().toString();
                getNeighborhoodsNames(city);
                isCityChanged = true;
                isNeighborhoodChanged = true;
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        binding.neighborhoodNameSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                isNeighborhoodChanged = true;
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }
    private void getNeighborhoods() {
        binding.governorateNamesProgress.show();
        homeViewModel.getNeighborhoodLiveData().observe(this,neighborhoods1 -> {
            binding.governorateNamesProgress.hide();
            neighborhoods.clear();
            governoratesNames.clear();
            neighborhoods.addAll(neighborhoods1);



            for (Neighborhood neighborhood:neighborhoods){
                Log.d("neiGov",neighborhood.getGovernorate());
                Log.d("neicity",neighborhood.getCity());
                Log.d("neinei",neighborhood.getNeighborhood());
                if (!governoratesNames.contains(neighborhood.getGovernorate()))
                    governoratesNames.add(neighborhood.getGovernorate());
                governoratesAdapter.notifyDataSetChanged();
                Log.d("gover",neighborhood.getGovernorate());
                if (governorate!=null){
                    binding.governorateNameSpinner.setSelection(governoratesAdapter.getPosition(governorate));
                    getCitiesNames(governorate);
                    getNeighborhoodsNames(city);
                }


            }
            for (String governorate : governoratesNames)
                Log.d("goverNames",governorate);
        });
    }


    private void attachAdaptersToSpinners() {
        governoratesAdapter  = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item,governoratesNames);
        citiesAdapter        = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item,citiesNames);
        neighborhoodsAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item,neighborhoodsNames);
        binding.governorateNameSpinner.setAdapter(governoratesAdapter);
        binding.cityNameSpinner.setAdapter(citiesAdapter);
        binding.neighborhoodNameSpinner.setAdapter(neighborhoodsAdapter);
    }

    private void connectViewsToOnClick() {
        binding.summery.setOnClickListener(this);
//        binding.governorateNameSpinner.setOnItemSelectedListener(this);
//        binding.cityNameSpinner.setOnItemSelectedListener(this);
//        binding.neighborhoodNameSpinner.setOnItemSelectedListener(this);
    }

    private void setTextWatchers() {
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



    }

    private void updateValue(String value,String type) {
        Map<String,Object> newValue = new HashMap<>();
        newValue.put(type,value);
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
        Query query = reference.orderByKey().equalTo(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid());
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    dataSnapshot.getRef().updateChildren(newValue);
                }

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
                city = user.getCity();
                neighborhood = user.getNeighborhood();
                streetName = user.getStreetName();
                buildingNo = user.getBuildingNo();
                appartmentNo = user.getAppartmentNo();
                binding.governorateNameSpinner.setSelection(governoratesAdapter.getPosition(governorate));
                binding.cityNameSpinner.setSelection(citiesAdapter.getPosition(city));
                Log.d("neigh",neighborhood);
                binding.neighborhoodNameSpinner.setSelection(neighborhoodsAdapter.getPosition(neighborhood));
                Objects.requireNonNull(binding.streetName.getEditText()).setText(streetName);
                Objects.requireNonNull(binding.buildingNo.getEditText()).setText(buildingNo);
                Objects.requireNonNull(binding.appartmentNo.getEditText()).setText(appartmentNo);
                if (user.getFamousMark()!= null)
                    binding.famousMark.getEditText().setText(user.getFamousMark());
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

    private void getNeighborhoodsNames(String cityName) {
        neighborhoodsNames.clear();
        for (Neighborhood neighborhood:neighborhoods){
            if (neighborhood.getCity().equals(cityName)&&!neighborhoodsNames.contains(neighborhood.getNeighborhood())){
                neighborhoodsNames.add(neighborhood.getNeighborhood());
                neighborhoodsAdapter.notifyDataSetChanged();
            }
        }
    }

    private void getCitiesNames(String governorateName) {
        citiesNames.clear();
        for (Neighborhood neighborhood:neighborhoods){
            if (neighborhood.getGovernorate().equals(governorateName)&&!citiesNames.contains(neighborhood.getCity())){
                citiesNames.add(neighborhood.getCity());
                Log.d(TAG, "getCitiesNames: "+neighborhood.getCity());
                citiesAdapter.notifyDataSetChanged();
            }
        }
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.summery:{
                String username = binding.username.getEditText().getText().toString().trim();
                String phoneNumber = Objects.requireNonNull(binding.phoneNumber.getEditText()).getText().toString().trim();
                String mobileNumber = binding.mobileNumber.getEditText().getText().toString().trim();
                governorate = binding.governorateNameSpinner.getSelectedItem().toString();
                streetName = binding.streetName.getEditText().getText().toString().trim();
                buildingNo = binding.buildingNo.getEditText().getText().toString().trim();
                appartmentNo = binding.appartmentNo.getEditText().getText().toString().trim();
                String promoCode = Objects.requireNonNull(binding.promoCode.getEditText()).getText().toString().trim();
                neighborhood = binding.neighborhoodNameSpinner.getSelectedItem().toString();
                city = binding.cityNameSpinner.getSelectedItem().toString();
                String famousMark = binding.famousMark.getEditText().getText().toString().trim();
                if (username.isEmpty())
                    binding.username.setError("ادخل اسم العميل");
                if (mobileNumber.isEmpty())
                    binding.mobileNumber.setError("ادخل رقم المحمول");
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
                        && !city.isEmpty()
                        && !streetName.isEmpty()
                        && !buildingNo.isEmpty()) {
                    String address;
                    if (famousMark==null)
                        address = buildingNo + " " + streetName +" ، "+ city +" ، "+ city +" ، "+governorate+" شقة رقم "+ appartmentNo;
                    else
                        address = buildingNo + " " + streetName +" ، "+ city +" ، "+ city +" ، "+governorate+" بالقرب من "+famousMark+" شقة رقم "+ appartmentNo;
                    ShippingData shippingData = new ShippingData(username, mobileNumber, phoneNumber, address);
                    shippingData.setCity(city);
                    if (!famousMark.isEmpty())
                        shippingData.setFamousMark(famousMark);

                    if (isNeighborhoodChanged)
                        updateValue(neighborhood, "neighborhood ");
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
                    if (isCityChanged)
                        updateValue(city,"city");
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
            } break;
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        switch (view.getId()){
            case R.id.governorate_name_spinner:{
                String governorateName = binding.governorateNameSpinner.getSelectedItem().toString();
                Toast.makeText(this, governorateName+"selected", Toast.LENGTH_SHORT).show();
                getCitiesNames(governorateName);
                getNeighborhoodsNames(citiesNames.get(0));
                isCityChanged = true;
                isGovernorateChanged = true;
                isNeighborhoodChanged = true;
            } break;
            case R.id.city_name_spinner:{
                String city = binding.cityNameSpinner.getSelectedItem().toString();
                getNeighborhoodsNames(city);
                isCityChanged = true;
                isNeighborhoodChanged = true;
            } break;
            case R.id.neighborhood_name_spinner:{
                isNeighborhoodChanged = true;
            } break;
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}