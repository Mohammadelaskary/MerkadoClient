package com.merkado.merkadoclient.Adapters;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.merkado.merkadoclient.Database.ProductOrder;
import com.merkado.merkadoclient.Model.CanceledOrder;
import com.merkado.merkadoclient.Model.FullOrder;
import com.merkado.merkadoclient.Model.Order;
import com.merkado.merkadoclient.Model.PharmacyOrder;
import com.merkado.merkadoclient.R;
import com.kofigyan.stateprogressbar.StateProgressBar;
import com.kofigyan.stateprogressbar.StateProgressBar.StateNumber;
import com.merkado.merkadoclient.Views.MainActivity;
import com.merkado.merkadoclient.Views.PharmacyItemsDialog;
import com.shashank.sony.fancytoastlib.FancyToast;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FullOrderAdapter extends RecyclerView.Adapter<FullOrderAdapter.FullOrderViewHolder> {
    Context context;
    boolean newOrder;
    ProgressDialog progressDialog;
    List<Order> orders;

    public FullOrderAdapter(Context context, List<Order> orders, boolean newOrder) {
        this.context = context;
        this.newOrder = newOrder;
        this.orders = orders;
    }

    @NonNull
    @Override
    public FullOrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.full_order_item, parent, false);
        return new FullOrderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final FullOrderViewHolder holder, final int position) {
        final String customerName,time,date,address,mobileNumber,phoneNumber;
        final BigDecimal sum,discount,overAllDiscount,netCost,shipping;
        final boolean isDone,isSeen,isShiped;
        FullOrder fullOrder = orders.get(position).getFullOrder();
        List<PharmacyOrder> pharmacyOrders = orders.get(position).getPharmacyOrders();
        final int id;
        id = orders.get(position).getId();
        int numberOfPharmacyItems = 0;
        if (!pharmacyOrders.isEmpty()) {
            numberOfPharmacyItems = pharmacyOrders.size();
            holder.pharmacyContainer.setVisibility(View.VISIBLE);
        } else {
            holder.pharmacyContainer.setVisibility(View.GONE);
        }
        holder.numberOfPharmacyItems.setText(String.valueOf(numberOfPharmacyItems));
        if (fullOrder!=null){
            customerName = fullOrder.getUsername();
            time = fullOrder.getTime();
            date = fullOrder.getDate();
            address = fullOrder.getAddress();
            mobileNumber = fullOrder.getMobilePhone();
            sum = new BigDecimal(fullOrder.getSum());
            discount = new BigDecimal(fullOrder.getDiscount());
            overAllDiscount = new BigDecimal(fullOrder.getOverAllDiscount());
            phoneNumber = fullOrder.getPhoneNumber();
            netCost = new BigDecimal(fullOrder.getTotalCost());
            final List<ProductOrder> list = fullOrder.getOrders();
            shipping = new BigDecimal(fullOrder.getShipping());
            isDone = fullOrder.isDone();
            isSeen = fullOrder.isSeen();
            isShiped = fullOrder.isShiped();
            OrdersAdapter adapter = new OrdersAdapter(context, list);
            holder.orders.setAdapter(adapter);
            holder.orders.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false));
            holder.orders.setHasFixedSize(true);
            holder.totalCost.setText(String.valueOf(netCost));
            holder.shipping.setText(String.valueOf(shipping));
            holder.sumText.setText(String.valueOf(sum));
            holder.discountText.setText(String.valueOf(discount));
            holder.overallDiscount.setText(String.valueOf(overAllDiscount));
            holder.calculations.setVisibility(View.VISIBLE);

        } else {
            customerName = pharmacyOrders.get(0).getShippingData().getUsername();
            time = pharmacyOrders.get(0).getTime();
            date = pharmacyOrders.get(0).getDate();
            address = pharmacyOrders.get(0).getShippingData().getAddress();
            mobileNumber = pharmacyOrders.get(0).getShippingData().getMobileNumber();
            phoneNumber = pharmacyOrders.get(0).getShippingData().getPhoneNumber();
            isDone = pharmacyOrders.get(0).isDone();
            isSeen = pharmacyOrders.get(0).isSeen();
            isShiped = pharmacyOrders.get(0).isShiped();
            String pharmacyCost = pharmacyOrders.get(0).getPharmacyCost();
            holder.calculations.setVisibility(View.GONE);
            holder.paymentTitle.setText("التوصيــــــــــــــــــــــــل:");
            holder.totalCost.setText(pharmacyCost);
        }
        holder.customerName.setText(customerName);
        holder.time.setText(time);
        holder.date.setText(date);
        holder.address.setText(address);
        holder.phoneNumber.setText(phoneNumber);
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
                    cancelOrder(id, position,holder.pharmacyContainer,holder.orders,holder.calculations,holder.paymentTitle, holder.totalCost);
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

        holder.openPharmacy.setOnClickListener(v -> {
            PharmacyItemsDialog dialog = new PharmacyItemsDialog(context,android.R.style.Theme_Light,pharmacyOrders);
            dialog.show();
        });



    }

    private void pushNotication(String customerName) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("CanceledOrder");
        String key = reference.getKey();
        CanceledOrder canceledOrder = new CanceledOrder(customerName,key);
        reference.push().setValue(canceledOrder);
        Query query = reference.orderByChild("id").equalTo(key);
        query.getRef().setValue(null);
    }

    private void cancelOrder(int id,int position,View pharmacy,View ordersRecycler,View calculations
            ,TextView totalTv, TextView totalCost) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Orders");
        Query query = reference.orderByChild("id").equalTo(id);
        Map<String,Object> idMap = new HashMap<>();
        idMap.put("userId"," ");
        Order order = orders.get(position);
        FullOrder fullOrder = order.getFullOrder();
        List<PharmacyOrder> pharmacyOrders = order.getPharmacyOrders();
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("اختار الذي تريد حذفه");
        builder.setItems(R.array.items_to_delete,
                 (dialog, which) -> {
            switch (which){
                case 0:{
                    dialog.dismiss();
                    progressDialog.show();
                    if (fullOrder!=null){
                        String shippingFee = fullOrder.getShipping();
                        query.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                    snapshot.getRef().updateChildren(idMap);
                                    progressDialog.hide();
                                }
                            }


                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {
                                progressDialog.hide();
                                FancyToast.makeText(context,"حدث خطأ",FancyToast.LENGTH_SHORT,FancyToast.ERROR,false);
                            }
                        });
                        if (!pharmacyOrders.isEmpty()){
                            ordersRecycler.setVisibility(View.GONE);
                            calculations.setVisibility(View.GONE);
                            totalTv.setText("التوصيــــــل:");
                            totalCost.setText(shippingFee);
                        } else {
                            removeAt(position);
                        }
                    } else {
                        progressDialog.dismiss();
                        FancyToast.makeText(context,"لا يوجد منتجات بالفعل..",FancyToast.LENGTH_SHORT,FancyToast.CONFUSING,false).show();
                    }


                } break;
                case 1:{
                    dialog.dismiss();
                    progressDialog.show();
                    if (!pharmacyOrders.isEmpty()){
                        removePharmacy(String.valueOf(id));
                        if (fullOrder!=null){
                            pharmacy.setVisibility(View.GONE);
                        } else {
                            removeAt(position);
                        }
                    } else {
                        progressDialog.dismiss();
                        FancyToast.makeText(context,"لا يوجد صيدلية بالفعل..",FancyToast.LENGTH_SHORT,FancyToast.CONFUSING,false).show();
                    }

                } break;
                case 2:{
                    dialog.dismiss();
                    progressDialog.show();
                    if (fullOrder!=null) {
                        query.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                    snapshot.getRef().updateChildren(idMap);

                                    progressDialog.hide();
                                }
                            }


                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {
                                progressDialog.hide();
                                FancyToast.makeText(context, "حدث خطأ", FancyToast.LENGTH_SHORT, FancyToast.ERROR, false);
                            }
                        });
                    }
                    if (!orders.isEmpty()){
                        removePharmacy(String.valueOf(id));
                    }
                    removeAt(position);
                } break;
                case 3:{
                    dialog.dismiss();
                } break;
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    @Override
    public int getItemCount() {
        return orders.size();
    }


    private void removePharmacy(String id){
        DatabaseReference reference2 = FirebaseDatabase.getInstance().getReference("PharmacyOrders");
        Query query2 = reference2.orderByChild("orderId").equalTo(id);
        Map<String,Object> idMap2 = new HashMap<>();
        idMap2.put("userId"," ");
        query2.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    snapshot.getRef().updateChildren(idMap2);
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
    static class FullOrderViewHolder extends RecyclerView.ViewHolder {
        TextView numberOfPharmacyItems, time, mobileNumber, date, customerName, address, phoneNumber, totalCost, sumText, discountText, overallDiscount, shipping,paymentTitle;
        LinearLayout calculations;
        RecyclerView orders;
        StateProgressBar stateProgressBar;
        ImageButton shareLocation, call;
        String[] descriptionData = {"تم الارسال", "جاري التحضير", "شحن", "تم التسليم"};
        CheckBox doneCheckBox;
        Button cancelOrder;
        MaterialButton openPharmacy;
        ConstraintLayout pharmacyContainer;

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
            calculations = itemView.findViewById(R.id.calculations);
            paymentTitle = itemView.findViewById(R.id.payment_title);
            openPharmacy = itemView.findViewById(R.id.pharmacy);
            pharmacyContainer = itemView.findViewById(R.id.pharmacy_container);
            numberOfPharmacyItems = itemView.findViewById(R.id.number_of_pharmacy_items);
        }
    }

    private void removeAt(int position) {
        orders.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, orders.size());
    }
}
