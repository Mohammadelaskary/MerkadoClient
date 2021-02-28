package com.merkado.merkadoclient.Adapters;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.merkado.merkadoclient.Database.ProductOrder;
import com.merkado.merkadoclient.Model.CanceledOrder;
import com.merkado.merkadoclient.Model.FullOrder;
import com.merkado.merkadoclient.R;
import com.kofigyan.stateprogressbar.StateProgressBar;
import com.kofigyan.stateprogressbar.StateProgressBar.StateNumber;
import com.shashank.sony.fancytoastlib.FancyToast;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FullOrderAdapter extends RecyclerView.Adapter<FullOrderAdapter.FullOrderViewHolder> {
    private static final int REQUEST_CODE = 100;
    Context context;
    List<FullOrder> ordersList;
    boolean newOrder;
    ProgressDialog progressDialog;



    public FullOrderAdapter(Context context, List<FullOrder> ordersList, boolean newOrder) {
        this.context = context;
        this.ordersList = ordersList;
        this.newOrder = newOrder;
    }


    @NonNull
    @Override
    public FullOrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.full_order_item, parent, false);
        return new FullOrderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final FullOrderViewHolder holder, final int position) {
        final String customerName = ordersList.get(position).getUsername();
        final String time = ordersList.get(position).getTime();
        final String date = ordersList.get(position).getDate();
        final String address = ordersList.get(position).getAddress();
        final String mobileNumber = ordersList.get(position).getMobilePhone();
        final float sum = ordersList.get(position).getSum();
        final float discount = ordersList.get(position).getDiscount();
        final float overAllDiscount = ordersList.get(position).getOverAllDiscount();
        final String phoneNumber = ordersList.get(position).getPhoneNumber();
        final float netCost = ordersList.get(position).getTotalCost();
        final List<ProductOrder> list = ordersList.get(position).getOrders();
        final boolean isDone = ordersList.get(position).isDone();
        final boolean isSeen = ordersList.get(position).isSeen();
        final boolean isShiped = ordersList.get(position).isShiped();
        final float shipping = ordersList.get(position).getShipping();
        final String id = ordersList.get(position).getId();
        final String userId = ordersList.get(position).getUserId();
        holder.customerName.setText(customerName);
        holder.time.setText(time);
        holder.date.setText(date);
        holder.address.setText(address);
        holder.phoneNumber.setText(phoneNumber);
        holder.totalCost.setText(String.valueOf(netCost));
        OrdersAdapter adapter = new OrdersAdapter(context, list);
        holder.orders.setAdapter(adapter);
        holder.orders.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false));
        holder.orders.setHasFixedSize(true);
        holder.shipping.setText(String.valueOf(shipping));
        holder.sumText.setText(String.valueOf(sum));
        holder.discountText.setText(String.valueOf(discount));
        holder.overallDiscount.setText(String.valueOf(overAllDiscount));
        holder.mobileNumber.setText(mobileNumber);
        progressDialog = new ProgressDialog(context);

        if (isSeen)
            holder.stateProgressBar.setCurrentStateNumber(StateNumber.TWO);


        if (isShiped)
            holder.stateProgressBar.setCurrentStateNumber(StateNumber.THREE);

        if (isDone) {
            holder.stateProgressBar.setCurrentStateNumber(StateNumber.FOUR);
            holder.stateProgressBar.checkStateCompleted(true);

        }

        holder.cancelOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isShiped) {
                    cancelOrder(id, position);
                    pushNotication(customerName);
                }else
                    FancyToast.makeText(context,"لا يمكنك إلغاء الطلب في مرحلة التوصيل!!",FancyToast.LENGTH_LONG,FancyToast.ERROR,false).show();
            }
        });
        if(newOrder){
            holder.cancelOrder.setVisibility(View.VISIBLE);
        }else {
            holder.cancelOrder.setVisibility(View.GONE);
        }




    }

    private void pushNotication(String customerName) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("CanceledOrder");
        String key = reference.getKey();
        CanceledOrder canceledOrder = new CanceledOrder(customerName,key);
        reference.push().setValue(canceledOrder);
        Query query = reference.orderByChild("id").equalTo(key);
        query.getRef().setValue(null);
    }

    private void cancelOrder(String id,int position) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Orders");
        Query query = reference.orderByChild("id").equalTo(id);
        Map<String,Object> isAvailableMap = new HashMap<>();
        isAvailableMap.put("stillAvailable",false);
        Map<String,Object> idMap = new HashMap<>();
        idMap.put("userId"," ");
        new AlertDialog.Builder(context)
                .setTitle("تأكيد الحذف")
                .setMessage("هل أنت متأكد أنك تريد حذف هذا الطلب؟")

                // Specifying a listener allows you to take an action before dismissing the dialog.
                // The dialog is automatically dismissed when a dialog button is clicked.
                .setPositiveButton("نعم", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        progressDialog.show();
                        query.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                    snapshot.getRef().updateChildren(isAvailableMap);
                                    snapshot.getRef().updateChildren(idMap);
                                    removeAt(position);
                                    progressDialog.hide();
                                }
                            }


                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {
                                progressDialog.hide();
                                FancyToast.makeText(context,"حدث خطأ",FancyToast.LENGTH_SHORT,FancyToast.ERROR,false);
                            }
                        });

                    }
                })
                .setNegativeButton("لا", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                // A null listener allows the button to dismiss the dialog and take no further action.
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();

    }

    @Override
    public int getItemCount() {
        return ordersList.size();
    }

    static class FullOrderViewHolder extends RecyclerView.ViewHolder {
        TextView done, time, mobileNumber, date, customerName, address, phoneNumber, totalCost, sumText, discountText, overallDiscount, shipping;
        RecyclerView orders;
        StateProgressBar stateProgressBar;
        ImageButton shareLocation, call;
        String[] descriptionData = {"تم الارسال", "جاري التحضير", "شحن", "تم التسليم"};
        CheckBox doneCheckBox;
        Button cancelOrder;

        public FullOrderViewHolder(@NonNull View itemView) {
            super(itemView);
            time = itemView.findViewById(R.id.order_time);
            date = itemView.findViewById(R.id.order_date);
            customerName = itemView.findViewById(R.id.customer_name);
            address = itemView.findViewById(R.id.customer_address);
            phoneNumber = itemView.findViewById(R.id.phone_number);
            orders = itemView.findViewById(R.id.orders);
            totalCost = itemView.findViewById(R.id.net_cost);
            mobileNumber = itemView.findViewById(R.id.mobile_phone);
            sumText = itemView.findViewById(R.id.sum);
            discountText = itemView.findViewById(R.id.discount);
            overallDiscount = itemView.findViewById(R.id.over_all_discount);
            shipping = itemView.findViewById(R.id.shipping_fee);
            stateProgressBar = itemView.findViewById(R.id.state_progress_bar);
            stateProgressBar.setStateDescriptionData(descriptionData);
            cancelOrder = itemView.findViewById(R.id.cancel_order);

        }
    }

    private void removeAt(int position) {
        ordersList.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, ordersList.size());
    }
}
