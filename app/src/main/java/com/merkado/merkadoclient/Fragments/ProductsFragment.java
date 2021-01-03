package com.merkado.merkadoclient.Fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.merkado.merkadoclient.R;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ProductsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ProductsFragment extends Fragment {
    private String depName;
    private String subDepName;
    TextView textView;

    public ProductsFragment(String depName, String subDepName) {
        this.depName = depName;
        this.subDepName = subDepName;
    }

    public ProductsFragment() {
    }

    public static ProductsFragment newInstance(String depName, String subDepName) {
        return new ProductsFragment(depName, subDepName);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_products, container, false);
        textView = view.findViewById(R.id.text);
        textView.setText(depName + " " + subDepName);
        return view;
    }
}