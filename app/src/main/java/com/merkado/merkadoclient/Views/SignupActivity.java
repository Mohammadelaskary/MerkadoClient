package com.merkado.merkadoclient.Views;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.merkado.merkadoclient.Model.User;
import com.merkado.merkadoclient.MyMethods;
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

public class SignupActivity extends AppCompatActivity {
    ActivitySignupBinding binding;
    HomeViewModel homeViewModel;
    String mobileNumber;
    List<String> promoCodes = new ArrayList<>();
    FirebaseAuth mAuth;
    ProgressDialog progressDialog;
    boolean isUploaded = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySignupBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        mAuth = FirebaseAuth.getInstance();
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("رجاء الانتظار ..");

        try {
            initViewModel();
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }
        getUsersPromoCode();
        Objects.requireNonNull(getSupportActionBar()).setTitle("إنشاء حساب جديد");

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
            }
        });
        Objects.requireNonNull(binding.city.getEditText()).addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                binding.city.setError(null);
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                binding.city.setError(null);
            }

            @Override
            public void afterTextChanged(Editable editable) {
                String city = binding.city.getEditText().getText().toString().trim();
                if (city.isEmpty())
                    binding.city.setError("من فضلك ادخل اسم المدينة");
            }
        });
        Objects.requireNonNull(binding.neighborhood.getEditText()).addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                binding.neighborhood.setError(null);
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                binding.neighborhood.setError(null);
            }

            @Override
            public void afterTextChanged(Editable editable) {
                String neighborhood = binding.neighborhood.getEditText().getText().toString().trim();
                if (neighborhood.isEmpty())
                    binding.neighborhood.setError("من فضلك ادخل اسم الحي");
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
                String email = binding.neighborhood.getEditText().getText().toString().trim();
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


        binding.signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String customerName = binding.customerName.getEditText().getText().toString().trim();
                String streetName = Objects.requireNonNull(binding.streetName.getEditText()).getText().toString().trim();
                String governorate = Objects.requireNonNull(binding.governorate.getEditText()).getText().toString().trim();
                String city = Objects.requireNonNull(binding.city.getEditText()).getText().toString().trim();
                String neighborhood = Objects.requireNonNull(binding.neighborhood.getEditText()).getText().toString().trim();
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
                if (neighborhood.isEmpty())
                    binding.neighborhood.setError("من فضلك ادخل اسم الحي");
                if (governorate.isEmpty())
                    binding.governorate.setError("من فضلك ادخل اسم الحي");
                if (city.isEmpty())
                    binding.city.setError("من فضلك ادخل اسم الحي");
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
            }
        });
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
}