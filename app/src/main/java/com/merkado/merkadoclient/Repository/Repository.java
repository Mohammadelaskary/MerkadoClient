package com.merkado.merkadoclient.Repository;

import android.os.AsyncTask;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import com.google.firebase.database.Query;
import com.merkado.merkadoclient.Model.AdImages;
import com.merkado.merkadoclient.Model.Cart;
import com.merkado.merkadoclient.Model.Contact;
import com.merkado.merkadoclient.Model.DepartmentNames;
import com.merkado.merkadoclient.Model.FullOrder;
import com.merkado.merkadoclient.Model.Neighborhood;
import com.merkado.merkadoclient.Model.OverTotalMoneyDiscount;
import com.merkado.merkadoclient.Model.PharmacyOrder;
import com.merkado.merkadoclient.Model.PointsDiscount;
import com.merkado.merkadoclient.Model.Product;
import com.merkado.merkadoclient.Model.Shipping;
import com.merkado.merkadoclient.Model.SubDeparment;
import com.merkado.merkadoclient.Model.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ExecutionException;

import static android.content.ContentValues.TAG;

public class Repository {
    private static Repository instance;
    private final MutableLiveData<Boolean> allProductsFinishLoading = new MutableLiveData<>();
    private final MutableLiveData<Boolean> adImagesFinishLoading = new MutableLiveData<>();
    private final MutableLiveData<Integer> numberOfProductsInCart = new MutableLiveData<>();
    private final MutableLiveData<Boolean> currentOrdersFinishLoading = new MutableLiveData<>();
    private final MutableLiveData<Boolean> preOrdersFinishLoading = new MutableLiveData<>();
    private final MutableLiveData<Boolean> pointsDiscountExist1 = new MutableLiveData<>();
    private final MutableLiveData<Boolean> pointsDiscountExist2 = new MutableLiveData<>();
    private final MutableLiveData<Boolean> allUsersFinishedLoading = new MutableLiveData<>();

    public MutableLiveData<Boolean> getAllUsersFinishedLoading() {
        return allUsersFinishedLoading;
    }

    public MutableLiveData<Boolean> getPointsDiscountExist1() {
        return pointsDiscountExist1;
    }
    public MutableLiveData<Boolean> getPointsDiscountExist2() {
        return pointsDiscountExist2;
    }
    public MutableLiveData<Boolean> getCurrentOrdersFinishLoading() {
        return currentOrdersFinishLoading;
    }

    public MutableLiveData<Boolean> getPreOrdersFinishLoading() {
        return preOrdersFinishLoading;
    }

    public MutableLiveData<Integer> getNumberOfProductsInCart() {
        return numberOfProductsInCart;
    }


    public MutableLiveData<Boolean> getSpecialOfferFinishLoading() {
        return specialOfferFinishLoading;
    }

    private final MutableLiveData<Boolean> specialOfferFinishLoading = new MutableLiveData<>();


    public static Repository getInstance() {
        if (instance == null)
            instance = new Repository();
        return instance;
    }

    public MutableLiveData<List<AdImages>> getAds() throws ExecutionException, InterruptedException {
        return new AdsAsynk().execute().get();
    }

    public MutableLiveData<List<Product>> getAllProducts() throws ExecutionException, InterruptedException {
        final MutableLiveData<List<Product>> allProductsLiveData = new MutableLiveData<>();
        final List<Product> allProducts = new ArrayList<>();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Products");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                allProductsFinishLoading.setValue(true);
                if (snapshot.exists()) {
                    allProducts.clear();
                    for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                        Product product = snapshot1.getValue(Product.class);
                        allProducts.add(product);
                        assert product != null;
                        Log.d(TAG, "repository: " + product.getProductName());
                    }
                }

                allProductsLiveData.postValue(allProducts);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        return allProductsLiveData;
    }

    public MutableLiveData<OverTotalMoneyDiscount> getOverTotalMoneyDiscount() {
        final MutableLiveData<OverTotalMoneyDiscount> discountMutableLiveData = new MutableLiveData<>();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("OverTotalMoneyDiscount");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                specialOfferFinishLoading.setValue(true);
                if (snapshot.exists()) {
                    for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                        OverTotalMoneyDiscount discount = snapshot1.getValue(OverTotalMoneyDiscount.class);
                        discountMutableLiveData.setValue(discount);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        return discountMutableLiveData;
    }
    public MutableLiveData<List<PharmacyOrder>> getMyPharmacyOrders(){
        MutableLiveData<List<PharmacyOrder>> pharmacyOrdersLiveData = new MutableLiveData<>();
        List<PharmacyOrder> pharmacyOrders = new ArrayList<>();
        if (FirebaseAuth.getInstance().getCurrentUser()!=null) {
            String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
            DatabaseReference reference = FirebaseDatabase.getInstance().getReference("PharmacyOrders");
            Query query = reference.orderByChild("userId").equalTo(userId);
            query.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    pharmacyOrders.clear();
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        PharmacyOrder order = dataSnapshot.getValue(PharmacyOrder.class);
                        pharmacyOrders.add(order);
                    }
                    pharmacyOrdersLiveData.postValue(pharmacyOrders);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
        return pharmacyOrdersLiveData;
    }
    public MutableLiveData<List<DepartmentNames>> getDepartmentsNames() {
        final MutableLiveData<List<DepartmentNames>> depNamesLiveData = new MutableLiveData<>();
        final List<DepartmentNames> departmentNames = new ArrayList<>();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("DepartmentsNames");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    departmentNames.clear();
                    for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                        DepartmentNames depName = snapshot1.getValue(DepartmentNames.class);
                        departmentNames.add(depName);
                    }
                }
                depNamesLiveData.setValue(departmentNames);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        return depNamesLiveData;
    }

    public MutableLiveData<List<SubDeparment>> getSubDepartments() {
        MutableLiveData<List<SubDeparment>> subdeps = new MutableLiveData<>();
        List<SubDeparment> subDeparmentList = new ArrayList<>();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("SubDeps");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                subDeparmentList.clear();
                for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                    SubDeparment subDeparment = snapshot1.getValue(SubDeparment.class);
                    subDeparmentList.add(subDeparment);
                }
                subdeps.setValue(subDeparmentList);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        return subdeps;
    }

    public MutableLiveData<Boolean> isAllProductsFinishLoading() {
        Log.d(TAG, "isAllProductsFinishLoading: " + allProductsFinishLoading);
        return allProductsFinishLoading;
    }

    public MutableLiveData<Boolean> isAdImagesFinishLoading() {
        return adImagesFinishLoading;
    }

    public MutableLiveData<List<Cart>> getAllProductsInCart() {
        final MutableLiveData<List<Cart>> productsInCartLiveData = new MutableLiveData<>();
        final List<Cart> productsInCart = new ArrayList<>();
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Cart").child(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid());
            reference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        productsInCart.clear();
                        for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                            Cart cart = dataSnapshot.getValue(Cart.class);
                            productsInCart.add(cart);

                        }
                        productsInCartLiveData.setValue(productsInCart);
                        numberOfProductsInCart.setValue(productsInCart.size());
                    } else {
                        numberOfProductsInCart.setValue(0);
                        productsInCart.clear();
                        productsInCartLiveData.setValue(productsInCart);
                    }

                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
        return productsInCartLiveData;
    }

    public MutableLiveData<List<User>> getAllUsers() {
        MutableLiveData<List<User>> allUsersLiveData = new MutableLiveData<>();
        List<User> allUsers = new ArrayList<>();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                allUsersFinishedLoading.postValue(true);
                if (snapshot.exists()) {
                    allUsers.clear();
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        User user = dataSnapshot.getValue(User.class);
                        allUsers.add(user);
                    }
                    allUsersLiveData.setValue(allUsers);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        return allUsersLiveData;
    }

    public MutableLiveData<Shipping> getShippingFee() {
        final MutableLiveData<Shipping> shippingLiveData = new MutableLiveData<>();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Shipping");
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    Shipping shipping = snapshot.getValue(Shipping.class);
                    shippingLiveData.setValue(shipping);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        return shippingLiveData;
    }

    public MutableLiveData<List<Neighborhood>> getNeighborhoods() {
        final MutableLiveData<List<Neighborhood>> neighborhoodLiveData = new MutableLiveData<>();
        final List<Neighborhood> neighborhoodList = new ArrayList<>();
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Neighborhood");
            reference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        neighborhoodList.clear();
                        for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                            Neighborhood neighborhood = dataSnapshot.getValue(Neighborhood.class);
                            neighborhoodList.add(neighborhood);

                        }
                        neighborhoodLiveData.setValue(neighborhoodList);
                    }

                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
        return neighborhoodLiveData;
    }

    public MutableLiveData<List<FullOrder>> getAllCurrentOrders() {
        final MutableLiveData<List<FullOrder>> fullOrderLiveData = new MutableLiveData<>();
        final List<FullOrder> fullOrderList = new ArrayList<>();
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Orders");
            reference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {

                    if (snapshot.exists()) {
                        fullOrderList.clear();
                        for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                            FullOrder fullOrder = dataSnapshot.getValue(FullOrder.class);
                            fullOrderList.add(fullOrder);
                            Log.d(TAG, "onDataChange: orders" + fullOrder.getMobilePhone());
                        }
                        fullOrderLiveData.setValue(fullOrderList);

                    }
                    currentOrdersFinishLoading.setValue(true);

                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
        return fullOrderLiveData;
    }

    public MutableLiveData<List<FullOrder>> getAllPreOrders() {
        final MutableLiveData<List<FullOrder>> fullOrderLiveData = new MutableLiveData<>();
        final List<FullOrder> fullOrderList = new ArrayList<>();
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Done orders");
            reference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        Log.d(TAG, "onDataChange: done");
                        fullOrderList.clear();
                        for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                            FullOrder fullOrder = dataSnapshot.getValue(FullOrder.class);
                            fullOrderList.add(fullOrder);
                        }
                        fullOrderLiveData.setValue(fullOrderList);
                    }
                    preOrdersFinishLoading.setValue(true);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
        return fullOrderLiveData;
    }

    public static class AdsAsynk extends AsyncTask<Void, Void, MutableLiveData<List<AdImages>>> {

        @Override
        protected MutableLiveData<List<AdImages>> doInBackground(Void... voids) {
            final MutableLiveData<List<AdImages>> adsLiveData = new MutableLiveData<>();
            final List<AdImages> ads = new ArrayList<>();
            DatabaseReference reference = FirebaseDatabase.getInstance().getReference("AdImagesUrl");
            reference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        ads.clear();
                        for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                            AdImages adImage = snapshot1.getValue(AdImages.class);
                            ads.add(adImage);
                        }
                    }

                    adsLiveData.setValue(ads);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
            return adsLiveData;
        }
    }



    public MutableLiveData<PointsDiscount> getPointsDiscount1() throws ExecutionException, InterruptedException {
        final MutableLiveData<PointsDiscount> pointsDiscountLiveData = new MutableLiveData<>();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("PointsDiscount");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    PointsDiscount pointsDiscount = snapshot.getValue(PointsDiscount.class);
                    pointsDiscountLiveData.postValue(pointsDiscount);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        return pointsDiscountLiveData;
    }
    public MutableLiveData<PointsDiscount> getPointsDiscount2() throws ExecutionException, InterruptedException {
        final MutableLiveData<PointsDiscount> pointsDiscountLiveData = new MutableLiveData<>();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("PointsDiscount2");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    PointsDiscount pointsDiscount = snapshot.getValue(PointsDiscount.class);
                    pointsDiscountLiveData.postValue(pointsDiscount);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        return pointsDiscountLiveData;
    }
    public MutableLiveData<List<String>> getAllSerials() {
        MutableLiveData<List<String>> allSerialsLiveData = new MutableLiveData<>();
        List<String> allSerials = new ArrayList<>();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Serials");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                allSerials.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    String serial = dataSnapshot.getValue(String.class);
                    allSerials.add(serial);
                }
                allSerialsLiveData.postValue(allSerials);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        return allSerialsLiveData;
    }

    public MutableLiveData<String> getContactNumber() {
        MutableLiveData<String> phoneNumberLivData = new MutableLiveData<>();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Contact");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Contact contact = snapshot.getValue(Contact.class);
                if (contact != null)
                    phoneNumberLivData.postValue(contact.getPhoneNumber());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        return phoneNumberLivData;
    }
    public MutableLiveData<List<PharmacyOrder>> getMyPharmacyCart (){
        MutableLiveData<List<PharmacyOrder>> myPharmacyOrdersLiveData = new MutableLiveData<>();
        List<PharmacyOrder> myPharmacyOrders = new ArrayList<>();
        if (FirebaseAuth.getInstance().getCurrentUser()!=null) {
            String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
            DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Pharmacy Cart").child(userId);
            reference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    myPharmacyOrders.clear();
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        PharmacyOrder order = dataSnapshot.getValue(PharmacyOrder.class);
                        myPharmacyOrders.add(order);
                    }
                    myPharmacyOrdersLiveData.setValue(myPharmacyOrders);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
        return myPharmacyOrdersLiveData;
    }
}
