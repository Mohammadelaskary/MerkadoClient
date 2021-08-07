package com.merkado.merkadoclient.Views;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;

import androidx.annotation.NonNull;

import com.merkado.merkadoclient.Adapters.ShowPharmacyItemsAdapter;
import com.merkado.merkadoclient.Model.PharmacyOrder;
import com.merkado.merkadoclient.databinding.ShowPharmacyItemsDialogBinding;

import java.util.List;

import static android.content.ContentValues.TAG;

public class PharmacyItemsDialog extends Dialog {
    List<PharmacyOrder> orders ;
    ShowPharmacyItemsAdapter adapter;

    public PharmacyItemsDialog(@NonNull Context context, int themeResId, List<PharmacyOrder> orders) {
        super(context, themeResId);
        this.orders = orders;
    }

    ShowPharmacyItemsDialogBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        binding = ShowPharmacyItemsDialogBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        adapter = new ShowPharmacyItemsAdapter(getContext(),orders);
        binding.pharmacyItems.setAdapter(adapter);

            Log.d(TAG, "onCreate: "+"dialog"+orders.size());

    }
}
