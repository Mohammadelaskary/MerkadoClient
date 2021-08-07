package com.merkado.merkadoclient.Views;

import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Query;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;
import com.merkado.merkadoclient.Database.OrderCost;
import com.merkado.merkadoclient.Database.ProductOrder;
import com.merkado.merkadoclient.Database.ShippingData;
import com.merkado.merkadoclient.Model.FullOrder;
import com.merkado.merkadoclient.Model.Neighborhood;
import com.merkado.merkadoclient.Model.PharmacyOrder;
import com.merkado.merkadoclient.Model.User;
import com.merkado.merkadoclient.ViewModel.HomeViewModel;
import com.merkado.merkadoclient.databinding.ActivityOrderSentBinding;
import com.shashank.sony.fancytoastlib.FancyToast;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ExecutionException;

public class OrderSent extends AppCompatActivity {
    List<ProductOrder> orderProducts = new ArrayList<>();
    List<ShippingData> shippingData = new ArrayList<>();
    List<OrderCost> orderCost = new ArrayList<>();
    List<User> allUsers = new ArrayList<>();
    List<String> neighborhoodsList = new ArrayList<>();
    List<String> promoCodes = new ArrayList<>();
    List<String> allSerials = new ArrayList<>();
    List<PharmacyOrder> mPharmacyCart = new ArrayList<>();
    String myPromoCode;
    boolean deserveDiscount;
    User myData;
    int id;
    HomeViewModel homeViewModel;
    ActivityOrderSentBinding binding;
    String promoCodeId;
    String promoCode,pharmacyCost;
    int promoCodeHolderCount;
    int subtractedPoints, myPoints;
    FullOrder fullOrder = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityOrderSentBinding.inflate(getLayoutInflater());
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        setContentView(binding.getRoot());
        Intent intent = getIntent();
        promoCode = intent.getStringExtra("promoCode");
        subtractedPoints = intent.getIntExtra("subtractedPoints", 0);
        pharmacyCost = intent.getStringExtra("pharmacyCost");
        Log.d("pointsSent", subtractedPoints + "");
        try {
            initViewModel();
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }
        getAllUserData();
        getAllNeighborhoods();
        getPharmacyCart();
        orderProducts = MainActivity.dataBase.myDao().getAllOrders();
        shippingData = MainActivity.dataBase.myDao().getAllShippingData();
        orderCost = MainActivity.dataBase.myDao().getAllOrderCost();

        deleteMyCart();



    }

    private void getPharmacyCart() {
        homeViewModel.getMyPharmacyCart().observe(this,pharmacyOrders -> {
            mPharmacyCart.clear();
            mPharmacyCart.addAll(pharmacyOrders);
        });
    }

    private void deletePharmacyCart() {
        String myId = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Pharmacy Cart").child(myId);
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    dataSnapshot.getRef().removeValue();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void deleteMyCart() {
        String myId = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Cart");
        Query query = reference.orderByKey().equalTo(myId);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    dataSnapshot.getRef().removeValue();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }



    private void uploadOrder(FullOrder order) {

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Orders");
        reference.push().setValue(order);
        decrementProductsAmount();
        changeDeserveDiscountStatus();
        addPointToCurrentUser();
        getAllSerials();
        if (subtractedPoints != 0)
            subtractPoints(subtractedPoints);
        MainActivity.dataBase.myDao().deleteAllShippingData();
        MainActivity.dataBase.myDao().deleteAllOrderCost();
        MainActivity.dataBase.myDao().deleteAllOrders();


    }

    private void addPointToCurrentUser() {
        int currentUserPoints = getCurrentUserPoints();
        increamentCurrentUserPoints(currentUserPoints);
    }

    private void increamentCurrentUserPoints(int currentUserPoints) {
        Map<String, Object> count = new HashMap<>();
        count.put("count", currentUserPoints + 1);
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
        Query query = reference.orderByKey().equalTo(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid());
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    dataSnapshot.getRef().updateChildren(count);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private int getCurrentUserPoints() {
        int myPoints = 0;
        for (User user : allUsers) {
            if (user.getUserId().equals(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid())) {
                myPoints = user.getCount();
                break;
            }
        }
        return myPoints;
    }

    private void getAllSerials() {
        homeViewModel.getAllSerialsLiveData().observe(this, strings -> {
            allSerials.clear();
            allSerials.addAll(strings);
        });
    }

    private void subtractPoints(int subtractedPoints) {
        int remainingPoints = myPoints - subtractedPoints;
        Log.d("subtractedPoints", subtractedPoints + "");
        Log.d("remainingPoints", remainingPoints + "");
        Map<String, Object> points = new HashMap<>();
        points.put("count", remainingPoints);
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
        Query query = reference.orderByKey().equalTo(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid());
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren())
                    dataSnapshot.getRef().updateChildren(points);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


    private void decrementProductsAmount() {
        for (ProductOrder orderProduct : orderProducts) {
            BigDecimal orderedAmount = new BigDecimal(orderProduct.getOrdered());
            BigDecimal availableAmount = new BigDecimal(orderProduct.getAvailable());
            String productName = orderProduct.getProductName();

            availableAmount = availableAmount.subtract(orderedAmount);
            Map<String, Object> newAvailableAmount = new HashMap<>();
            newAvailableAmount.put("availableAmount", availableAmount.toString());
            DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Products");
            Query query = reference.orderByChild("productName").equalTo(productName);
            query.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    for (DataSnapshot dataSnapshot : snapshot.getChildren())
                        dataSnapshot.getRef().updateChildren(newAvailableAmount);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });

        }

    }

    String username,mobileNumber,phoneNumber,address,userCity,formattedDate,formattedTime;
    BigDecimal sumValue,discountValue,overAllDiscount,shippingFee,totalCost;

    boolean promoCodeUsedBefore;
    private void getAllNeighborhoods() {
        //            }
        homeViewModel.getNeighborhoodLiveData().observe(this, neighborhoods -> {
            neighborhoodsList.clear();
            for (Neighborhood neighborhood : neighborhoods) {
                neighborhoodsList.add(neighborhood.getNeighborhood());
                Log.d("orderSent neifromdb", "onChanged: " + "'" + neighborhood.getNeighborhood() + "'");
            }
            Date c = Calendar.getInstance().getTime();
            SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH);
            formattedDate = df.format(c);
            SimpleDateFormat df1 = new SimpleDateFormat("hh:mm a", Locale.ENGLISH);
            formattedTime = df1.format(c);
            username = shippingData.get(shippingData.size() - 1).getUsername();
            mobileNumber = shippingData.get(shippingData.size() - 1).getMobileNumber();
            phoneNumber = shippingData.get(shippingData.size() - 1).getPhoneNumber();
            address = shippingData.get(shippingData.size() - 1).getAddress();
            userCity = shippingData.get(shippingData.size() - 1).getCity();

            if (!orderCost.isEmpty()) {
                sumValue = new BigDecimal(orderCost.get(orderCost.size() - 1).getSum());
                discountValue = new BigDecimal(orderCost.get(orderCost.size() - 1).getDiscount());
                overAllDiscount = new BigDecimal(orderCost.get(orderCost.size() - 1).getOverAllDiscount());
                shippingFee = new BigDecimal(orderCost.get(orderCost.size() - 1).getShippingFee());
                totalCost = new BigDecimal(orderCost.get(orderCost.size() - 1).getTotalCost());
                promoCodeUsedBefore = allSerials.contains(getSerialNumber());

                String finalCostText = "حسابك " + totalCost +" جنيه";
                binding.finalCost.setText(finalCostText);


            binding.orderSent.setVisibility(View.VISIBLE);






            String userId = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
            fullOrder = new FullOrder(formattedDate,
                    formattedTime,
                    false,
                    false,
                    false,
                    username,
                    mobileNumber,
                    phoneNumber,
                    address,
                    orderProducts,
                    sumValue.toString(),
                    discountValue.toString(),
                    overAllDiscount.toString(),
                    shippingFee.toString(),
                    totalCost.toString(),
                    userId,
                    true);
            }

            getOrderId();

        });

    }

    private void getOrderId() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Last Id");
        reference.runTransaction(new Transaction.Handler() {
            @Override
            public Transaction.Result doTransaction(final MutableData currentData) {
                if (currentData.getValue() == null) {
                    currentData.setValue(1);
                } else {
                    currentData.setValue((Long) currentData.getValue() + 1);
                }

                return Transaction.success(currentData);
            }

            @Override
            public void onComplete(@Nullable DatabaseError error, boolean committed, @Nullable DataSnapshot currentData) {
               if (currentData!=null) {
                   id = currentData.getValue(Integer.class);
                   if (fullOrder != null){
                       fullOrder.setId(id);
                       uploadOrder(fullOrder);
                       if (!mPharmacyCart.isEmpty() ){
                           ShippingData data = new ShippingData(username,mobileNumber,phoneNumber,address);
                           for (PharmacyOrder order:mPharmacyCart){
                               order.setOrderId(String.valueOf(id));
                               order.setShippingData(data);
                               order.setPharmacyCost(pharmacyCost);
                               order.setTime(formattedTime);
                               order.setDate(formattedDate);
                           }
                           uploadPharmacyOrder (mPharmacyCart);
                       }
                   } else {
                       if (!mPharmacyCart.isEmpty() ){
                           String finalCostText = "حسابك " + pharmacyCost + " جنيه";
                           binding.finalCost.setText(finalCostText);
                           ShippingData data = new ShippingData(username,mobileNumber,phoneNumber,address);
                           for (PharmacyOrder order:mPharmacyCart){
                               order.setOrderId(String.valueOf(id));
                               order.setShippingData(data);
                               order.setPharmacyCost(pharmacyCost);
                           }
                           uploadPharmacyOrder (mPharmacyCart);
                       }
                   }

               }
                if (promoCodes.contains(promoCode) && !promoCode.equals(myPromoCode) && deserveDiscount && !promoCodeUsedBefore) {
                    binding.congrat.setVisibility(View.VISIBLE);
                    if (totalCost.compareTo(BigDecimal.ZERO)>0) {
                        totalCost = totalCost.subtract(shippingFee);
                        shippingFee = BigDecimal.ZERO;
                    }
                    incremantCount();
                    String serialNumber = getSerialNumber();
                    addSerialNumber(serialNumber);
                } else {
                    binding.congrat.setVisibility(View.GONE);
                }
                if (promoCodeUsedBefore)
                    FancyToast.makeText(OrderSent.this, "تم استخدام كود الأصدقاء على هذا الجهاز من قبل !", FancyToast.LENGTH_LONG, FancyToast.ERROR, false).show();

            }
        });
    }

    private void uploadPharmacyOrder(List<PharmacyOrder> mPharmacyCart) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("PharmacyOrders");
        for (PharmacyOrder order:mPharmacyCart)
            reference.push().setValue(order).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    deletePharmacyCart();
                    changeDeserveDiscountStatus();
                    addPointToCurrentUser();
                    getAllSerials();
                    if (subtractedPoints != 0)
                        subtractPoints(subtractedPoints);
                }
            });

    }


    private void addSerialNumber(String serialNumber) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Serials");
        reference.push().setValue(serialNumber);
    }

    private void incremantCount() {
        Map<String, Object> count = new HashMap<>();
        count.put("count", promoCodeHolderCount + 1);
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
        Query query = reference.orderByKey().equalTo(promoCodeId);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    dataSnapshot.getRef().updateChildren(count);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void changeDeserveDiscountStatus() {
        Map<String, Object> discountStatus = new HashMap<>();
        discountStatus.put("discount", false);
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
        Query query = reference.orderByKey().equalTo(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid());
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren())
                    dataSnapshot.getRef().updateChildren(discountStatus);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void getAllUserData() {
        homeViewModel.getAllUsers().observe(this, users -> {
            allUsers.clear();
            allUsers.addAll(users);
            getMyData();
            getPromoCodeId();
            getAllPromoCodes();
        });
    }

    private void getAllPromoCodes() {
        promoCodes.clear();
        for (User user : allUsers) {
            promoCodes.add(user.getPromoCode());
        }
    }

    private void getPromoCodeId() {
        for (User user : allUsers) {
            if (user.getPromoCode().equals(promoCode)) {
                promoCodeId = user.getUserId();
                promoCodeHolderCount = user.getCount();
            }
        }
    }

    private void getMyData() {
        String myId = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
        for (User user : allUsers) {
            if (user.getUserId().equals(myId)) {
                myData = user;
                myPromoCode = myData.getPromoCode();
                myPoints = myData.getCount();
            }
        }
    }

    public void initViewModel() throws ExecutionException, InterruptedException {
        homeViewModel = ViewModelProvider
                .AndroidViewModelFactory
                .getInstance(OrderSent.this.getApplication()).create(HomeViewModel.class);
        homeViewModel.init();
    }

    public String getSerialNumber() {
        return Settings.Secure.getString(getContentResolver(),
                Settings.Secure.ANDROID_ID);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}