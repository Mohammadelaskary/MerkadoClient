package com.merkado.merkadoclient.Adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.merkado.merkadoclient.Fragments.ProductsFragment;
import com.merkado.merkadoclient.Interfaces.SubDepInterface;
import com.merkado.merkadoclient.Model.SubDeparment;
import com.merkado.merkadoclient.R;
import com.merkado.merkadoclient.Views.ProductsActivity;

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
        String subdepName = subDeparments.get(position).getSubdepName();
        String imageUrl = subDeparments.get(position).getImageUrl();

        holder.subDepName.setText(subdepName);
        if (!imageUrl.isEmpty())
            Glide.with(context).load(imageUrl).into(holder.subdepBackground);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, ProductsActivity.class);
                intent.putExtra("depName",depName);
                intent.putExtra("subdep",subdepName);
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return subDeparments.size();
    }

    static class SubDepViewHolder extends RecyclerView.ViewHolder {
        TextView subDepName,discountTextView;
        RelativeLayout subdepDiscountLayout;
        ImageView subdepBackground;

        public SubDepViewHolder(@NonNull View itemView) {
            super(itemView);
            subDepName = itemView.findViewById(R.id.subdep_name);
            discountTextView = itemView.findViewById(R.id.subdep_discount);
            subdepDiscountLayout = itemView.findViewById(R.id.discount_layout);
            subdepBackground = itemView.findViewById(R.id.subdep_background);
        }
    }

    public void switchToProductFragment(String depName, String subDepName) {

        final FragmentTransaction ft = ((AppCompatActivity) context).getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.product_container, new ProductsFragment(depName, subDepName), "NewFragmentTag");
        ft.setCustomAnimations(android.R.animator.fade_in, android.R.animator.fade_out);
        ft.commit();

    }


}
