package com.merkado.merkadoclient.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import com.merkado.merkadoclient.Fragments.ProductsFragment;
import com.merkado.merkadoclient.Interfaces.SubDepInterface;
import com.merkado.merkadoclient.Model.SubDeparment;
import com.merkado.merkadoclient.R;

import java.util.List;

public class SubDepAdapter extends RecyclerView.Adapter<SubDepAdapter.SubDepViewHolder> {
    private final Context context;
    private final List<SubDeparment> subDeparments;
    private final String depName;
    private final SubDepInterface onClickListener;

    public SubDepAdapter(Context context, List<SubDeparment> subDeparments, String depName, SubDepInterface onClickListener) {
        this.context = context;
        this.subDeparments = subDeparments;
        this.depName = depName;
        this.onClickListener = onClickListener;
    }

    @NonNull
    @Override
    public SubDepViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.subdep_item, parent, false);
        return new SubDepViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SubDepViewHolder holder, int position) {
        SubDeparment subDeparment = subDeparments.get(position);
        String subDepName = subDeparment.getSubdepName();
        holder.subDepText.setText(subDepName);
        holder.subDepButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onClickListener.onItemClicked(subDeparment);
            }
        });


    }

    @Override
    public int getItemCount() {
        return subDeparments.size();
    }

    static class SubDepViewHolder extends RecyclerView.ViewHolder {
        TextView subDepText;
        LinearLayout subDepButton;

        public SubDepViewHolder(@NonNull View itemView) {
            super(itemView);
            subDepText = itemView.findViewById(R.id.subdep_name);
            subDepButton = itemView.findViewById(R.id.subdep_button);
        }
    }

    public void switchToProductFragment(String depName, String subDepName) {

        final FragmentTransaction ft = ((AppCompatActivity) context).getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.product_container, new ProductsFragment(depName, subDepName), "NewFragmentTag");
        ft.setCustomAnimations(android.R.animator.fade_in, android.R.animator.fade_out);
        ft.commit();

    }


}
