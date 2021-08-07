package com.merkado.merkadoclient.Adapters;

import android.content.Context;
import android.os.CountDownTimer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.merkado.merkadoclient.Model.PharmacyOrder;
import com.merkado.merkadoclient.R;
import com.merkado.merkadoclient.databinding.PharmacyShowOrderedItemBinding;

import java.util.List;

public class ShowPharmacyItemsAdapter extends RecyclerView.Adapter<ShowPharmacyItemsAdapter.ShowPharmacyItemsViewHolder> {
    Context context;
    List<PharmacyOrder> orders;

    public ShowPharmacyItemsAdapter(Context context, List<PharmacyOrder> orders) {
        this.context = context;
        this.orders = orders;
    }

    @NonNull
    @Override
    public ShowPharmacyItemsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ShowPharmacyItemsViewHolder(PharmacyShowOrderedItemBinding.inflate(LayoutInflater.from(context),parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull ShowPharmacyItemsViewHolder holder, int position) {
        String imageUrl = orders.get(position).getImageUrl();
        String therapyName = orders.get(position).getTherapyName();
        String numberOfItems = orders.get(position).getNumberOfItems();
        String typeOfItem    = orders.get(position).getTypeOfItem();
        String acceptAlternative = orders.get(position).getAcceptAlternative();
        String decription        = orders.get(position).getDescribtion();
        if (!imageUrl.isEmpty()) {
            startTimer(holder.binding.bodyContainer);
            Glide.with(context).load(imageUrl).into(holder.binding.image);
        } else {
            holder.binding.bodyContainer.setVisibility(View.VISIBLE);
            holder.binding.image.setImageResource(R.drawable.ic_medicine);
        }

        if (therapyName.isEmpty()){
            holder.binding.productNameContainer.setVisibility(View.GONE);
        } else {
            holder.binding.therapyName.setText(therapyName);
        }

        if (numberOfItems.isEmpty()){
            holder.binding.therapyNumberContainer.setVisibility(View.GONE);
        } else {
            holder.binding.therapyNum.setText(numberOfItems);
            holder.binding.therapyItemType.setText(typeOfItem);
        }

        if (acceptAlternative.isEmpty()){
            holder.binding.acceptAlternativeContainer.setVisibility(View.GONE);
        } else {
            if (acceptAlternative.equals("true"))
                holder.binding.acceptAlternative.setText("نعم");
            else
                holder.binding.acceptAlternative.setText("لا");
        }

        if (decription.isEmpty()){
            holder.binding.descriptionContainer.setVisibility(View.GONE);
        } else {
            holder.binding.description.setText(decription);
        }

        holder.itemView.setOnClickListener(v -> {
            if (!imageUrl.isEmpty())
                startTimer(holder.binding.bodyContainer);
            else
                holder.binding.bodyContainer.setVisibility(View.VISIBLE);
        });
    }

    private void startTimer(View view) {
        view.setVisibility(View.VISIBLE);
        new CountDownTimer(2000, 1000) {
            public void onTick(long millisUntilFinished) {

            }

            public void onFinish() {
                view.setVisibility(View.GONE);
            }

        }.start();
    }

    @Override
    public int getItemCount() {
        return orders==null?0:orders.size();
    }

    static class ShowPharmacyItemsViewHolder extends RecyclerView.ViewHolder {
       PharmacyShowOrderedItemBinding binding;
       public ShowPharmacyItemsViewHolder(@NonNull PharmacyShowOrderedItemBinding binding) {
           super(binding.getRoot());
           this.binding = binding;
       }
   }
}
