package com.merkado.merkadoclient.Adapters;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.AnimationUtils;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.merkado.merkadoclient.Model.Cart;
import com.merkado.merkadoclient.Model.Product;
import com.merkado.merkadoclient.R;
import com.merkado.merkadoclient.SoundService;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.merkado.merkadoclient.Views.MainActivity;
import com.shashank.sony.fancytoastlib.FancyToast;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class ProductsSearchAdapter extends RecyclerView.Adapter<ProductsSearchAdapter.ProductsSearchViewHolder> implements Filterable, OnCompleteListener {
    private final Context context;
    private final List<Product> products;
    private final List<Cart> cart;
    private final List<Product> productsFull = new ArrayList<>();


    public ProductsSearchAdapter(Context context, List<Product> products, List<Cart> cart) {
        this.context = context;
        this.products = products;
        this.cart = cart;
        productsFull.addAll(products);
        for (Product product : products)
            Log.d("products", product.getProductName());
    }

    @NonNull
    @Override
    public ProductsSearchViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.product_item, parent, false);
        return new ProductsSearchViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductsSearchViewHolder holder, int position) {

        String imageUrl = products.get(position).getImageUrl();
        String productName = products.get(position).getProductName();
        String originalPrice = products.get(position).getPrice();
        String discount = products.get(position).getDiscount();
        String discountUnit = products.get(position).getDiscountUnit();
        String unitWeight = products.get(position).getUnitWeight();
        BigDecimal availableAmount =new BigDecimal(products.get(position).getAvailableAmount());
        boolean mostSold = products.get(position).isMostSold();
        boolean isAvailable = products.get(position).isAvailable();
        BigDecimal mininumOrderAmount = new BigDecimal(products.get(position).getMinimumOrderAmount());
        String finalPrice = originalPrice;
        if (!discount.isEmpty())
            finalPrice = calculateFinalPrice(originalPrice, discount, discountUnit);
        String fullDiscountText = "خصم " + discount + " " + discountUnit;
        String originalPriceText = "بدلا من " + originalPrice + "جنيه";
        String finalPriceText = finalPrice + "جنيه / " + unitWeight;
        if (!isAvailable){
            holder.unavailableLayout.setVisibility(View.VISIBLE);
            holder.unavailableText.setText("غير متاح مؤقتا");
            holder.addCart.setClickable(false);
        } else {
            holder.unavailableLayout.setVisibility(View.GONE);
        }
        if (imageUrl.isEmpty())
            holder.productImage.setImageResource(R.drawable.no_image);
        else
            Glide.with(context)
                    .load(imageUrl)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(holder.productImage);

        holder.productName.setText(productName);

        holder.discount.setText(String.valueOf(fullDiscountText));

        holder.originalPrice.setText(String.valueOf(originalPriceText));

        if (discount.isEmpty()) {
            holder.originalPrice.setVisibility(View.INVISIBLE);
            holder.discount.setVisibility(View.GONE);
        } else {
            holder.originalPrice.setVisibility(View.VISIBLE);
            holder.discount.setVisibility(View.VISIBLE);
        }

        holder.finalPrice.setText(String.valueOf(finalPriceText));

        if (mostSold)
            holder.mostSold.setVisibility(View.VISIBLE);
        else
            holder.mostSold.setVisibility(View.GONE);

        if (availableAmount.equals(0) ) {
            holder.unavailableLayout.setVisibility(View.VISIBLE);
            holder.addCart.setClickable(false);
        } else {
            holder.unavailableLayout.setVisibility(View.GONE);
        }

        if (productIsInCart(productName)) {
            holder.addCart.setVisibility(View.GONE);
            holder.addRemoveCart.setVisibility(View.VISIBLE);
                holder.orderedAmount.setText(String.valueOf(getOrderedAmount(productName)));
        } else {
            holder.addCart.setVisibility(View.VISIBLE);
            holder.addRemoveCart.setVisibility(View.GONE);
        }
        holder.addCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (FirebaseAuth.getInstance().getCurrentUser() != null && availableAmount.compareTo(BigDecimal.ZERO)>0) {
                    context.startService(new Intent(context, SoundService.class));
                    if (!productsInCart().contains(productName)) {
                        addCart(productName,mininumOrderAmount);
                        holder.addCart.setVisibility(View.GONE);
                        holder.addRemoveCart.setVisibility(View.VISIBLE);
                        holder.orderedAmount.setText("1");
//                        FancyToast.makeText(context,"تم إضافة السلعة إلى العربة",FancyToast.LENGTH_SHORT, FancyToast.SUCCESS,false).show();

                    }
//                    else {
//                        for (Cart cart:cart){
//                            if (cart.getProductName().equals(productName)){
//                                int newValue = cart.getNumberOfProducts()+1;
//                                if (newValue<=availableAmount) {
//                                    updateCart(productName, newValue);
//                                    holder.unavailableText.setText(String.valueOf(newValue));
//                                    animateNumber(holder.unavailableLayout);
//                                } else
//                                    FancyToast.makeText(context,"الكمية المطلوبة أكبر من الكمية المتاحة",FancyToast.LENGTH_SHORT, FancyToast.WARNING,false).show();
//
//                            }
//                        }
//                    }
                }

            }
        });
        holder.addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                increamentCart(productName, holder.orderedAmount, true, availableAmount,mininumOrderAmount);
            }
        });
        holder.removeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                increamentCart(productName, holder.orderedAmount, false, availableAmount,mininumOrderAmount);
                if (getOrderedAmount(productName).subtract(mininumOrderAmount).compareTo(BigDecimal.ZERO) <= 0) {
                    holder.addCart.setVisibility(View.VISIBLE);
                    holder.addRemoveCart.setVisibility(View.GONE);
                }
            }
        });

    }

    private void updateCart(String productName, int i) {
        Map<String, Object> map = new HashMap<>();
        map.put("numberOfProducts", i);
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Cart").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
        Query query = reference.orderByChild("productName").equalTo(productName);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren())
                    dataSnapshot.getRef().updateChildren(map);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void addCart(String productName,BigDecimal minimumOrderAmount) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Cart").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
        Cart cart = new Cart(productName, minimumOrderAmount.toString());
        reference.push().setValue(cart).addOnCompleteListener(this);

    }

    private void animateNumber(View layout) {
        layout.setVisibility(View.VISIBLE);
        Animation fade_in = AnimationUtils.loadAnimation(context, R.anim.fadein);
        Animation fade_out = AnimationUtils.loadAnimation(context, R.anim.fadeout);
        AnimationSet s = new AnimationSet(false);
        s.addAnimation(fade_in);
        fade_out.setStartOffset(fade_in.getDuration());
        s.addAnimation(fade_out);
        s.setFillAfter(true);
        layout.startAnimation(s);
    }

    private List<String> productsInCart() {
        List<String> productsInCart = new ArrayList<>();
        for (Cart cart : cart) {
            productsInCart.add(cart.getProductName());

        }
        return productsInCart;
    }

    private BigDecimal getOrderedAmount(String productName) {
        BigDecimal orderedAmount = BigDecimal.ZERO;
        for (Cart cart : cart) {
            if (cart.getProductName().equals(productName)) {
                orderedAmount = new BigDecimal(cart.getNumberOfProducts());
                break;
            }
        }
        return orderedAmount;
    }

    private boolean productIsInCart(String productName) {
        boolean isExist = false;
        for (Cart cart : cart) {
            if (cart.getProductName().equals(productName)) {
                isExist = true;
                break;
            }
        }
        return isExist;
    }

    private void increamentCart(String productName, TextView orderedAmount, boolean increament, BigDecimal availableAmount,BigDecimal minimumOrderAmount) {

        BigDecimal newValue;
        if (increament)
            newValue =  getOrderedAmount(productName).add(minimumOrderAmount);
        else
            newValue = getOrderedAmount(productName).subtract(minimumOrderAmount);
        if (newValue.compareTo(availableAmount) <= 0) {
            Map<String, Object> map = new HashMap<>();
            map.put("numberOfProducts", newValue.toString());
            DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Cart").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
            Query query = reference.orderByChild("productName").equalTo(productName);
            query.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    for (DataSnapshot dataSnapshot : snapshot.getChildren())
                        dataSnapshot.getRef().updateChildren(map);
                    orderedAmount.setText(newValue.toString());
                    if (newValue.compareTo(BigDecimal.ZERO) <= 0 )
                        removeFromCart(productName);
                    Log.d("newValue", newValue + "");
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Log.d("addToCart", error.getMessage());
                }
            });
        } else {
            FancyToast.makeText(context, "الكمية المطلوبة أكبر من الكمية المتاحة", FancyToast.LENGTH_SHORT, FancyToast.WARNING, false).show();
        }
    }

    private void removeFromCart(String productName) {
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

    @Override
    public int getItemCount() {
        return products.size();
    }

    private String calculateFinalPrice(String originalPrice, String discount, String discountUnit) {
        BigDecimal originalPriceNum = new BigDecimal(originalPrice);
        BigDecimal discountNum = new BigDecimal(discount);
        if (discountUnit.equals("جنيه")) {
            return String.valueOf(originalPriceNum.subtract(discountNum));
        } else {
            return String.valueOf(originalPriceNum.multiply((BigDecimal.ONE.subtract(discountNum.divide(BigDecimal.valueOf(100))))));
        }
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                List<Product> filteredList = new ArrayList<>();
                if (charSequence.toString().isEmpty() || charSequence.length() == 0) {
                    filteredList.addAll(productsFull);
                } else {
                    for (Product product : productsFull) {
                        if (product.getProductName().contains(charSequence.toString())) {
                            filteredList.add(product);
                            Log.d("filter", product.getProductName());
                        }
                    }
                }
                FilterResults filterResults = new FilterResults();
                filterResults.values = filteredList;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                products.clear();
                products.addAll((Collection<? extends Product>) filterResults.values);
                notifyDataSetChanged();
            }
        };
    }

    @Override
    public void onComplete(@NonNull Task task) {

    }


    static class ProductsSearchViewHolder extends RecyclerView.ViewHolder {
        ImageView productImage, mostSold;
        TextView discount, finalPrice, originalPrice, productName, unavailableText, orderedAmount;
        LinearLayout unavailableLayout, addRemoveCart;
        MaterialButton addCart, addBtn, removeBtn;

        public ProductsSearchViewHolder(@NonNull View itemView) {
            super(itemView);
            productImage = itemView.findViewById(R.id.product_image);
            mostSold = itemView.findViewById(R.id.most_sold);
            discount = itemView.findViewById(R.id.discount);
            finalPrice = itemView.findViewById(R.id.final_price);
            originalPrice = itemView.findViewById(R.id.original_price);
            productName = itemView.findViewById(R.id.product_name);
            unavailableLayout = itemView.findViewById(R.id.unavailable_layout);
            addCart = itemView.findViewById(R.id.add_to_cart);
            unavailableText = itemView.findViewById(R.id.unavailable_text);
            addRemoveCart = itemView.findViewById(R.id.add_remove_from_cart);
            addBtn = itemView.findViewById(R.id.add_btn);
            removeBtn = itemView.findViewById(R.id.remove_btn);
            orderedAmount = itemView.findViewById(R.id.order_amount);
        }
    }
}
