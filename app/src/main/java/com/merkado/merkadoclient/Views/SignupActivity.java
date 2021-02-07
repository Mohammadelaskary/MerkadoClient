package com.merkado.merkadoclient.Views;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.merkado.merkadoclient.Model.Neighborhood;
import com.merkado.merkadoclient.Model.User;
import com.merkado.merkadoclient.MyMethods;
import com.merkado.merkadoclient.R;
import com.merkado.merkadoclient.ViewModel.HomeViewModel;
import com.merkado.merkadoclient.databinding.ActivitySignupBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.shashank.sony.fancytoastlib.FancyToast;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Random;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

public class SignupActivity extends AppCompatActivity implements View.OnClickListener, AdapterView.OnItemSelectedListener {
    ActivitySignupBinding binding;
    HomeViewModel homeViewModel;
    List<String> promoCodes = new ArrayList<>();
    FirebaseAuth mAuth;
    FirebaseDatabase database;
    List<Neighborhood> neighborhoods = new ArrayList<>();
    List<String> governoratesNames = new ArrayList<>();
    List<String> citiesNames       = new ArrayList<>();
    List<String> neighborhoodsNames = new ArrayList<>();
    ArrayAdapter<String> governoratesAdapter;
    ArrayAdapter<String> citiesAdapter;
    ArrayAdapter<String> neighborhoodsAdapter;
    ProgressDialog progressDialog;
    boolean isUploaded = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySignupBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        mAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("رجاء الانتظار ..");

        try {
            initViewModel();
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }
        getUsersPromoCode();
        attachSpinnersToAdapters();
        Objects.requireNonNull(getSupportActionBar()).setTitle("إنشاء حساب جديد");
        setTextWatchers();
        getNeighborhoods();
        connectViewsToOnClick();



    }

    private void connectViewsToOnClick() {
        binding.signup.setOnClickListener(this);
        binding.governorateNameSpinner.setOnItemSelectedListener(this);
        binding.cityNameSpinner.setOnItemSelectedListener(this);
    }

    private void getNeighborhoods() {
        binding.governorateNamesProgress.show();
        DatabaseReference reference = database.getReference("Neighborhood");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                binding.governorateNamesProgress.hide();
                neighborhoods.clear();
                governoratesNames.clear();
                for (DataSnapshot dataSnapshot:snapshot.getChildren()){
                    Neighborhood neighborhood = dataSnapshot.getValue(Neighborhood.class);
                    neighborhoods.add(neighborhood);
                    governoratesNames.add(neighborhood.getGovernorate());
                    governoratesAdapter.notifyDataSetChanged();

                }
                getCitiesNames(governoratesNames.get(0));
                getNeighborhoodsNames(neighborhoods.get(0).getCity());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                FancyToast.makeText(SignupActivity.this,"حدث خطأ ما أثناء إيجاد أسماء المحافظات المتاحة..", FancyToast.LENGTH_SHORT,FancyToast.ERROR,false).show();
                Log.d("errorGettingGovernorate", error.getMessage());
            }
        });
    }

    private void setTextWatchers() {
        Objects.requireNonNull(binding.customerName.getEditText()).addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                binding.customerName.setError(null);
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                binding.customerName.setError(null);
            }

            @Override
            public void afterTextChanged(Editable editable) {
                String customerName = binding.customerName.getEditText().getText().toString().trim();
                if (customerName.isEmpty())
                    binding.customerName.setError("من فضلك ادخل اسم المستخدم");
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
                    binding.mobileNumber.setError("من فضلك رقم التليفون المحمول");
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
                String address = binding.streetName.getEditText().getText().toString().trim();
                if (address.isEmpty())
                    binding.streetName.setError("من فضلك ادخل اسم الشارع/المنطقة");
            }
        });


        Objects.requireNonNull(binding.email.getEditText()).addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                binding.email.setError(null);
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                binding.email.setError(null);
            }

            @Override
            public void afterTextChanged(Editable editable) {
                String email = binding.email.getEditText().getText().toString().trim();
                if (email.isEmpty())
                    binding.email.setError("من فضلك ادخل البريد الالكتروني");
            }
        });
        Objects.requireNonNull(binding.password.getEditText()).addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                binding.password.setError(null);
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                binding.password.setError(null);
            }

            @Override
            public void afterTextChanged(Editable editable) {
                String password = binding.password.getEditText().getText().toString().trim();
                if (password.isEmpty())
                    binding.email.setError("من فضلك ادخل كلمة المرور");
            }
        });
        Objects.requireNonNull(binding.confirmPassword.getEditText()).addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                binding.confirmPassword.setError(null);
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                binding.confirmPassword.setError(null);
            }

            @Override
            public void afterTextChanged(Editable editable) {
                String confirmPassword = binding.confirmPassword.getEditText().getText().toString().trim();
                if (confirmPassword.isEmpty())
                    binding.confirmPassword.setError("من فضلك ادخل كلمة المرور مرة أخري للتأكيد");
            }
        });
    }

    private void attachSpinnersToAdapters() {
        governoratesAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item,governoratesNames);
        citiesAdapter       = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item,citiesNames);
        neighborhoodsAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item,neighborhoodsNames);
        binding.governorateNameSpinner.setAdapter(governoratesAdapter);
        binding.cityNameSpinner.setAdapter(citiesAdapter);
        binding.neighborhoodNameSpinner.setAdapter(neighborhoodsAdapter);
    }

    private void storeData(String customerName, String mobileNumber, String phoneNumber, String streetName, String neighborhood, String email, String governorate, String city) {

        ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.show();
        progressDialog.setCancelable(false);
        progressDialog.setMessage("رجاء الانتظار...");
        User user = new User(customerName,
                mobileNumber,
                phoneNumber,
                streetName,
                neighborhood,
                city,
                governorate,
        getNewPromoCode(), Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid(),
                email);

        user.setCount(0);
        String promoCode = getNewPromoCode();
        if (promoCodes.contains(promoCode))
            promoCode = getNewPromoCode();
        user.setPromoCode(promoCode);
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
        reference.setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    isUploaded = true;
                    Intent intent = new Intent(SignupActivity.this, MainActivity.class);
                    intent.putExtra("cart", "");
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                } else {
                    mAuth.signOut();
                }

            }
        });
    }


    private String getNewPromoCode() {
        final int min = 100001;
        final int max = 999999;
        final int random = new Random().nextInt((max - min) + 1) + min;
        return  String.valueOf(random);
    }

    public void initViewModel() throws ExecutionException, InterruptedException {
        homeViewModel = ViewModelProvider.AndroidViewModelFactory.getInstance((SignupActivity.this).getApplication()).create(HomeViewModel.class);
        homeViewModel.init();
    }



    public void getUsersPromoCode() {
        homeViewModel.getAllUsers().observe(SignupActivity.this, new Observer<List<User>>() {
            @Override
            public void onChanged(List<User> users) {
                promoCodes.clear();
                for (User user : users) {
                    promoCodes.add(user.getPromoCode());
                }
            }
        });
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (!isUploaded)
            mAuth.signOut();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.signup:{

                String customerName = binding.customerName.getEditText().getText().toString().trim();
                String streetName = Objects.requireNonNull(binding.streetName.getEditText()).getText().toString().trim();
                String governorate = binding.governorateNameSpinner.getSelectedItem().toString();
                String city = binding.cityNameSpinner.getSelectedItem().toString();
                String neighborhood = binding.neighborhoodNameSpinner.getSelectedItem().toString();
                String phoneNumber = binding.phoneNumber.getEditText().getText().toString().trim();
                String mobileNumber = binding.mobileNumber.getEditText().getText().toString().trim();
                String email = binding.email.getEditText().getText().toString().trim();
                String password = binding.password.getEditText().getText().toString().trim();
                String confirmPassword = binding.confirmPassword.getEditText().getText().toString().trim();
                if (customerName.isEmpty()) {
                    binding.customerName.setError("من فضلك ادخل الاسم الأول");
                }
                if (streetName.isEmpty()){
                    binding.streetName.setError("من فضلك ادخل اسم الشارع أو المنطقة");
                }
                if (mobileNumber.isEmpty())
                    binding.mobileNumber.setError("من فضلك ادخل رقم التليفون المحمول");
                if (mobileNumber.length()!=11)
                    binding.mobileNumber.setError("من فضلك ادخل رقم تليفون صحيح");
                if (email.isEmpty()){
                    binding.customerName.setError("من فضلك ادخل البريد الالكتروني");
                }
                if (!MyMethods.isValidEmail(email)){
                    binding.customerName.setError("من فضلك ادخل بريد الكتروني صحيح");
                }
                if (password.isEmpty()){
                    binding.customerName.setError("من فضلك ادخل كلمة المرور");
                }
                if (confirmPassword.isEmpty())
                    binding.confirmPassword.setError("من فضلك ادخل كلمة المرور مرة أخري للتأكيد");
                if (!password.equals(confirmPassword))
                    binding.confirmPassword.setError("كلمتي المرور غير متطابقتين");
                if (!customerName.isEmpty()
                        && !streetName.isEmpty()
                        && !neighborhood.isEmpty()
                        && mobileNumber.length() == 11
                        && password.equals(confirmPassword)
                        &&!email.isEmpty()
                        && MyMethods.isValidEmail(email)
                        && !password.isEmpty()
                        && !governorate.isEmpty()
                        && !city.isEmpty()) {
                    if (!isUploaded)
                        progressDialog.show();
                    mAuth.createUserWithEmailAndPassword(email,password)
                            .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()){
                                        storeData(customerName,mobileNumber,phoneNumber,streetName,neighborhood,email,governorate,city);
                                    } else{
                                        FancyToast.makeText(SignupActivity.this,"حدث خطأ ما!",FancyToast.LENGTH_LONG,FancyToast.ERROR,false).show();
                                        Log.e("sign_up_error",task.getException().getMessage());
                                    }

                                }
                            });

                }
            } break;
        }
    }


    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        switch (view.getId()){
            case R.id.governorate_name_spinner:{
                String governorateName = binding.governorateNameSpinner.getSelectedItem().toString();
                getCitiesNames(governorateName);
            } break;
            case R.id.city_name_spinner:{
                String cityName = binding.cityNameSpinner.getSelectedItem().toString();
                getNeighborhoodsNames(cityName);
            } break;
        }
    }

    private void getNeighborhoodsNames(String cityName) {
        neighborhoodsNames.clear();
        for (Neighborhood neighborhood:neighborhoods){
            if (neighborhood.getCity().equals(cityName)){
                neighborhoodsNames.add(neighborhood.getNeighborhood());
                neighborhoodsAdapter.notifyDataSetChanged();
            }
        }
    }

    private void getCitiesNames(String governorateName) {
        citiesNames.clear();
        for (Neighborhood neighborhood:neighborhoods){
            if (neighborhood.getGovernorate().equals(governorateName)){
                citiesNames.add(neighborhood.getCity());
                citiesAdapter.notifyDataSetChanged();
            }
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}