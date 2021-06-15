package com.merkado.merkadoclient.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.merkado.merkadoclient.Database.ProductOrder;
import com.merkado.merkadoclient.R;
import com.merkado.merkadoclient.Views.MainActivity;

import java.math.BigDecimal;
import java.util.List;

public class OrdersAdapter extends RecyclerView.Adapter<OrdersAdapter.OrdersViewHolder> {
    Context context;
    List<ProductOrder> orderList;

    public OrdersAdapter(Context context, List<ProductOrder> orderList) {
        this.context = context;
        this.orderList = orderList;
    }

    @NonNull
    @Override
    public OrdersViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.order_item, parent, false);
        return new OrdersViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OrdersViewHolder holder, int position) {
        String imageURL = orderList.get(position).getImageURL();
        String product_name = orderList.get(position).getProductName();
        BigDecimal orderAmount = new BigDecimal(orderList.get(position).getOrdered());
        String finalPrice = orderList.get(position).getFinalPrice();
        String totalCost = orderList.get(position).getTotalCost();
        String unitWeight = orderList.get(position).getUnitWeight();
        if (imageURL.isEmpty())
            holder.imageView.setImageResource(R.drawable.no_image);
        else
            Glide.with(context).load(imageURL).into(holder.imageView);

        holder.productName.setText(product_name);
        holder.orderAmount.setText(String.valueOf(orderAmount));
        holder.unitPrice.setText(finalPrice);
        holder.unitWeight.setText(unitWeight);
    }

    @Override
    public int getItemCount() {
        return orderList.size();
    }

    static class OrdersViewHolder extends RecyclerView.ViewHolder {
        TextView productName, orderAmount, unitWeight, unitPrice;
        ImageView imageView;

        public OrdersViewHolder(@NonNull View itemView) {
            super(itemView);
            productName = itemView.findViewById(R.id.product_name);
            orderAmount = itemView.findViewById(R.id.order_amount);
            unitWeight = itemView.findViewById(R.id.unit_weight);
            unitPrice = itemView.findViewById(R.id.unit_price);
            imageView = itemView.findViewById(R.id.product_image);
        }
    }

}
