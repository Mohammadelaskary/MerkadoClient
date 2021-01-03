package com.merkado.merkadoclient.Adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.merkado.merkadoclient.Database.ProductOrder;
import com.merkado.merkadoclient.Model.Cart;
import com.merkado.merkadoclient.R;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.shashank.sony.fancytoastlib.FancyToast;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class ProductOrdersAdapter extends RecyclerView.Adapter<ProductOrdersAdapter.ProductOrdersViewHolder> {
    private final Context context;
    private final List<ProductOrder> orderProducts;


    public ProductOrdersAdapter(Context context, List<ProductOrder> orderProducts) {
        this.context = context;
        this.orderProducts = orderProducts;

    }

    @NonNull
    @Override
    public ProductOrdersViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.cart_recycler_item, parent, false);
        return new ProductOrdersViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductOrdersViewHolder holder, int position) {
        String imageUrl = orderProducts.get(position).getImageURL();
        String productName = orderProducts.get(position).getProductName();
        float orderedAmount = orderProducts.get(position).getOrdered();
        String originalPrice = orderProducts.get(position).getOriginalPrice();
        String finalPrice = orderProducts.get(position).getFinalPrice();
        String discount = orderProducts.get(position).getDiscount();
        String discountType = orderProducts.get(position).getDiscountType();
        String totalCost = orderProducts.get(position).getTotalCost();
        String unitWeight = orderProducts.get(position).getUnitWeight();
        float minnimumOrderAmount = orderProducts.get(position).getMinimumOrderAmount();
        float availablaAmount;
        if (orderProducts.get(position).getAvailable() != 0) {
            availablaAmount = orderProducts.get(position).getAvailable();
        } else {
            availablaAmount = 0;
        }
        if (imageUrl.isEmpty())
            holder.productImage.setImageResource(R.drawable.no_image);
        else
            Glide.with(context).load(imageUrl).into(holder.productImage);
        holder.productName.setText(productName);

            holder.orderedAmount.setText(String.valueOf(orderedAmount));
        holder.add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Map<String, Object> num = new HashMap<>();
                float newValue = orderedAmount +minnimumOrderAmount;
                num.put("numberOfProducts", newValue);
                if (newValue <= availablaAmount) {
                    DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Cart").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
                    Query query = reference.orderByChild("productName").equalTo(productName);
                    query.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            for (DataSnapshot dataSnapshot : snapshot.getChildren())
                                dataSnapshot.getRef().updateChildren(num);
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            Log.d("addToCartError", error.getMessage());
                        }
                    });

                    holder.orderedAmount.setText(String.valueOf(newValue));

                } else {
                    newValue = availablaAmount;
                    holder.orderedAmount.setText(String.valueOf(newValue));
                    FancyToast.makeText(context, "الكمية المطلوبة أكبر من الكمية المتاحة", FancyToast.LENGTH_SHORT, FancyToast.WARNING, false).show();

                }
            }

        });
        holder.remove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Map<String, Object> num = new HashMap<>();
                float newValue = orderedAmount - minnimumOrderAmount;
                num.put("numberOfProducts", newValue);
                if (newValue <= availablaAmount && newValue > 0) {
                    DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Cart").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
                    Query query = reference.orderByChild("productName").equalTo(productName);
                    query.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            for (DataSnapshot dataSnapshot : snapshot.getChildren())
                                dataSnapshot.getRef().updateChildren(num);
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            Log.d("removeFromCartError", error.getMessage());
                        }
                    });

                    holder.orderedAmount.setText(String.valueOf(newValue));

                } else if (newValue == 0) {
                    removeItem(position, view);
                }
            }

        });
        holder.unitWeight.setText(unitWeight);
        holder.originalPrice.setText(originalPrice);
//        Animation anim = AnimationUtils.loadAnimation(context,R.anim.sample_anim);
//        holder.itemView.startAnimation(anim);
    }

    @Override
    public int getItemCount() {
        return orderProducts.size();
    }


    static class ProductOrdersViewHolder extends RecyclerView.ViewHolder {
        ImageView productImage;
        TextView productName, unitWeight, originalPrice, finalPrice, orderedAmount;
        MaterialButton add, remove;

        public ProductOrdersViewHolder(@NonNull View itemView) {
            super(itemView);
            productImage = itemView.findViewById(R.id.product_image);
            productName = itemView.findViewById(R.id.product_name);
            originalPrice = itemView.findViewById(R.id.original_price);
            finalPrice = itemView.findViewById(R.id.final_price);
            unitWeight = itemView.findViewById(R.id.unit_weight);
            orderedAmount = itemView.findViewById(R.id.order_amount);
            add = itemView.findViewById(R.id.add_btn);
            remove = itemView.findViewById(R.id.remove_btn);
        }


    }

    public Context getContext() {
        return context;
    }

    public void removeItem(int position) {
        String productName = orderProducts.get(position).getProductName();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Cart").child(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid());
        Query query = reference.orderByChild("productName").equalTo(productName);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren())
                    dataSnapshot.getRef().removeValue();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }

    public void removeItem(int position, View view) {
        String productName = orderProducts.get(position).getProductName();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Cart").child(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid());
        Query query = reference.orderByChild("productName").equalTo(productName);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren())
                    dataSnapshot.getRef().removeValue();
                showUndoSnackbar(position, view);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }

    private void showUndoSnackbar(int position, View view) {
        ProductOrder product = orderProducts.get(position);
        Snackbar snackbar = Snackbar.make(view, "تم إزالة العنصر", Snackbar.LENGTH_LONG);
        snackbar.setAction("استرجاع", new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                restoreItem(product);
            }
        });
        snackbar.show();
    }

    public void restoreItem(ProductOrder orderProduct) {
        Cart cart = new Cart(orderProduct.getProductName(), orderProduct.getOrdered());
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Cart").child(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid());
        reference.push().setValue(cart);
    }

}
