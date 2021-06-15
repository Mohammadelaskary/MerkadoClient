package com.merkado.merkadoclient.Views;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import com.merkado.merkadoclient.Model.Complaint;
import com.merkado.merkadoclient.databinding.ActivityAddComplaintBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.shashank.sony.fancytoastlib.FancyToast;

import java.util.Objects;

public class AddComplaint extends AppCompatActivity {
    ActivityAddComplaintBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAddComplaintBinding.inflate(getLayoutInflater());
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        setContentView(binding.getRoot());
        Objects.requireNonNull(binding.complaint.getEditText()).addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                binding.complaint.setError(null);
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                binding.complaint.setError(null);
            }

            @Override
            public void afterTextChanged(Editable editable) {
                String complaintText = binding.complaint.getEditText().getText().toString().trim();
                if (complaintText.isEmpty())
                    binding.complaint.setError("من فضلك أدخل المقترح أو الشكوى");
                else
                    binding.complaint.setError(null);
            }
        });

        binding.sendComplaint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String complaintText = binding.complaint.getEditText().getText().toString().trim();
                String customerName = Objects.requireNonNull(binding.customerName.getEditText()).getText().toString().trim();
                String mobileNumber = Objects.requireNonNull(binding.mobileNumber.getEditText()).getText().toString().trim();
                Complaint complaint = new Complaint(customerName, mobileNumber, complaintText, false);
                if (complaintText.isEmpty())
                    binding.complaint.setError("من فضلك أدخل المقترح أو الشكوى");
                else {
                    sendComplaint(complaint);
                }
            }
        });
    }

    private void sendComplaint(Complaint complaint) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Complaints");
        reference.push().setValue(complaint).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                FancyToast.makeText(AddComplaint.this, "شكرا لاهتمامكم وسنسعى جاهدين للتطوير لارضاء حضراتكم", FancyToast.LENGTH_LONG, FancyToast.SUCCESS, false).show();
                Intent intent = new Intent(AddComplaint.this, MainActivity.class);
                intent.putExtra("cart", "");
                startActivity(intent);
                finish();
            }
        });
    }
}