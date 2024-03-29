package com.merkado.merkadoclient.Fragments;

import android.app.ActivityOptions;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.widget.ContentLoadingProgressBar;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.merkado.merkadoclient.Adapters.ProductOrdersAdapter;
import com.merkado.merkadoclient.Database.OrderCost;
import com.merkado.merkadoclient.Database.ProductOrder;
import com.merkado.merkadoclient.Interfaces.OrderProductInterface;
import com.merkado.merkadoclient.Model.Cart;
import com.merkado.merkadoclient.Model.OverTotalMoneyDiscount;
import com.merkado.merkadoclient.Model.PharmacyOrder;
import com.merkado.merkadoclient.Model.Product;
import com.merkado.merkadoclient.Model.Shipping;
import com.merkado.merkadoclient.Model.User;
import com.merkado.merkadoclient.MyMethods;
import com.merkado.merkadoclient.R;
import com.merkado.merkadoclient.SwipeToDeleteCallback;
import com.merkado.merkadoclient.ViewModel.HomeViewModel;
import com.merkado.merkadoclient.Views.MainActivity;
import com.merkado.merkadoclient.Views.PharmacyActivity;
import com.merkado.merkadoclient.Views.UserData;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.shashank.sony.fancytoastlib.FancyToast;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ExecutionException;
import java.util.function.ObjIntConsumer;


public class CartFragment extends Fragment {

    Context context;
    RecyclerView cartRec;
    TextView sum, discount, overAllDiscountTextView, shipping, total, noConnection, useYourPoints1,useYourPoints2,totalCostTV,numberOfPharmacyItems;
    HomeViewModel homeViewModel;
    List<ProductOrder> orders = new ArrayList<>();
    List<Product> allProducts = new ArrayList<>();
    List<Cart> productInCart = new ArrayList<>();
    List<PharmacyOrder> mPharmacyOrders = new ArrayList<>();
    ProductOrdersAdapter productOrdersAdapter;
    OverTotalMoneyDiscount overAllDiscount;
    MaterialButton discount_option1,discount_option2,pharmacyCart;
    ContentLoadingProgressBar shippingProgress1,shippingProgress2;
    LinearLayout redeemDiscount,discountOptions1,discountOptions2,calculations;

    Shipping shippingFee ;
    RelativeLayout cartLayout;
    CoordinatorLayout fullLayout;
    ConstraintLayout pharmacyContainer;
    MaterialButton complete;
    ShimmerFrameLayout shimmerFrameLayout;
    int minPoints,minPoints2;
    boolean freeShipping = false;
    boolean freeShippingUsed = false;
    boolean pointsFreeShipping1 = false;
    boolean pointsFreeShipping2 = false;
    BigDecimal sumValue = BigDecimal.ZERO;
    BigDecimal discountValue = BigDecimal.ZERO;
    BigDecimal overAllDiscountValue =BigDecimal.ZERO;
    BigDecimal totalCostValue = BigDecimal.ZERO;
    BigDecimal shippingValue = BigDecimal.valueOf(10);
    BigDecimal defaultPointsDiscountValue = BigDecimal.ZERO;
    BigDecimal pointsDiscountValue = BigDecimal.ZERO;
    BigDecimal pointsDiscountValue2 = BigDecimal.ZERO;
    boolean isClicked = false;
    int subtractedPoints = 0;
    List<User> allUsers = new ArrayList<>();
    String myId;
    int myPoints;
    boolean allProductsAvailable = true;
    String pharmacyCost;

    public CartFragment() {
    }

    public CartFragment(Context context) {
        this.context = context;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_cart, container, false);
        cartRec = view.findViewById(R.id.cart);
        sum = view.findViewById(R.id.sum);
        discount = view.findViewById(R.id.discount);
        overAllDiscountTextView = view.findViewById(R.id.overall_money_dicount);
        shipping = view.findViewById(R.id.shipping_fee);
        total = view.findViewById(R.id.total);
        cartLayout = view.findViewById(R.id.cart_layout);
        noConnection = view.findViewById(R.id.no_connection);
        fullLayout = view.findViewById(R.id.full_layout);
        complete = view.findViewById(R.id.complete);
        shimmerFrameLayout = view.findViewById(R.id.shimmer_product_order);
        useYourPoints1 = view.findViewById(R.id.use_your_points1);
        useYourPoints2 = view.findViewById(R.id.use_your_points2);
        redeemDiscount = view.findViewById(R.id.points_redeem);
        discountOptions1 = view.findViewById(R.id.discount_options1);
        discountOptions2 = view.findViewById(R.id.discount_options2);
        discount_option1 = view.findViewById(R.id.discount_option1);
        discount_option2 = view.findViewById(R.id.discount_option2);
        pharmacyContainer = view.findViewById(R.id.pharmacy_container);
        pharmacyCart      = view.findViewById(R.id.pharmacy);
        calculations      = view.findViewById(R.id.calculations);
        totalCostTV       = view.findViewById(R.id.total_sum);
        numberOfPharmacyItems = view.findViewById(R.id.number_of_pharmacy_items);
        shippingProgress1 = view.findViewById(R.id.shipping_fee_progress1);
        shippingProgress2 = view.findViewById(R.id.shipping_fee_progress2);
        MainActivity.dataBase.myDao().deleteAllOrders();
        MainActivity.dataBase.myDao().deleteAllOrderCost();
        if (FirebaseAuth.getInstance().getCurrentUser() != null)
            myId = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
        try {
            initViewModel();
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }

        complete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                for (ProductOrder orderProduct : orders) {
                    if (new BigDecimal(orderProduct.getAvailable()).compareTo(BigDecimal.ZERO) <= 0) {
                        removeFromCart(orderProduct.getProductName());
                        allProductsAvailable = false;
                    }

                }

                if (!allProductsAvailable) {
                    new AlertDialog.Builder(context)
                            .setMessage("بعض المنتجات المطلوبة اصبحت غير متاحة \n هل تريد ارسال الطلب علي أي حال؟")

                            // Specifying a listener allows you to take an action before dismissing the dialog.
                            // The dialog is automatically dismissed when a dialog button is clicked.
                            .setPositiveButton("نعم", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    if (!productInCart.isEmpty()) {
                                        for (ProductOrder orderProduct : orders) {
                                            MainActivity.dataBase.myDao().addOrder(orderProduct);
                                        }
                                        OrderCost cost = new OrderCost(sumValue.toString(), discountValue.toString(), overAllDiscountValue.toString(), shippingValue.toString(), totalCostValue.toString());
                                        MainActivity.dataBase.myDao().addTotalCost(cost);
                                        for (ProductOrder orderProduct:orders){
                                            MainActivity.dataBase.myDao().addOrder(orderProduct);
                                        }
                                        Intent intent = new Intent(getContext(), UserData.class);
                                        intent.putExtra("subtractedPoints", subtractedPoints);
                                        intent.putExtra("pharmacyCost",pharmacyCost);
                                        context.startActivity(intent);
                                    } else {
                                        FancyToast.makeText(getContext(), "لا يمكن ارسال الطلبات حيث أن العربة فارغة", FancyToast.LENGTH_SHORT, FancyToast.WARNING, false).show();
                                    }
                                }
                            })

                            // A null listener allows the button to dismiss the dialog and take no further action.
                            .setNegativeButton("مراجعة الطلبات", null)
                            .setIcon(android.R.drawable.ic_dialog_alert)
                            .show();


                } else {
                    if (!productInCart.isEmpty()) {
                        OrderCost cost = new OrderCost(sumValue.toString(), discountValue.toString(), overAllDiscountValue.toString(), shippingValue.toString(), totalCostValue.toString());
                        MainActivity.dataBase.myDao().addTotalCost(cost);
                        for (ProductOrder orderProduct : orders) {
                            MainActivity.dataBase.myDao().addOrder(orderProduct);
                        }
                    }
                    Intent intent = new Intent(getContext(), UserData.class);
                    intent.putExtra("subtractedPoints", subtractedPoints);
                    intent.putExtra("pharmacyCost",pharmacyCost);
//                    Log.d("cost",pharmacyCost);
                    context.startActivity(intent);
                }
            }
        });

        if (MyMethods.isConnected(requireContext())) {
            getAllProducts();
            getAllProductsInCart();
            initProductsRec();
            getOverTotalMoneyDiscount();
            getShippingFee();
            getPointsDiscount1();
            getPointsDiscount2();
            getAllUsers();
            getPharmacyCart();
            shimmerFrameLayout.startShimmer();
            shimmerFrameLayout.setVisibility(View.VISIBLE);
        } else {
            cartLayout.setVisibility(View.GONE);
            noConnection.setText("تأكد من الاتصال بالانترنت");
            noConnection.setVisibility(View.VISIBLE);
            shimmerFrameLayout.setVisibility(View.GONE);

        }



        discount_option1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (myPoints < minPoints) {
                    FancyToast.makeText(getContext(), "يجب أن يكون لديك " + minPoints + " للحصول على الخصم", FancyToast.LENGTH_SHORT, FancyToast.INFO, false).show();
                } else {
                    if (isClicked) {
                        FancyToast.makeText(getContext(), "لا يمكنك استخدام الخصم أكثر من مرة", FancyToast.LENGTH_SHORT, FancyToast.WARNING, false).show();
                        discount_option2.setEnabled(false);
                        discount_option1.setEnabled(false);
                    } else {
                        if (pointsFreeShipping1){
                            freeShippingUsed = pointsFreeShipping1;
                        } else {
                            defaultPointsDiscountValue = pointsDiscountValue;
                        }
                        subtractedPoints = minPoints;
                        redeemDiscount.setEnabled(false);
                        calculateCosts();
                        isClicked = true;
                        discount_option2.setEnabled(false);
                        discount_option1.setEnabled(false);
                        if ( !mPharmacyOrders.isEmpty() && productInCart.isEmpty() ){
                            if(pointsFreeShipping1){
                                pharmacyCost = "0";
                            } else {
                                BigDecimal finalPharmacyCost = shippingValue.subtract(pointsDiscountValue);
                                if (finalPharmacyCost.compareTo(BigDecimal.ZERO)<=0)
                                    pharmacyCost = "0";
                                else
                                    pharmacyCost = String.valueOf(finalPharmacyCost);
                            }
                            total.setText(pharmacyCost);
                        }
                    }
                }
            }
        });
        discount_option2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (myPoints < minPoints) {
                    FancyToast.makeText(getContext(), "يجب أن يكون لديك " + minPoints + " للحصول على الخصم", FancyToast.LENGTH_SHORT, FancyToast.INFO, false).show();
                } else {
                    if (isClicked) {
                        FancyToast.makeText(getContext(), "لا يمكنك استخدام الخصم أكثر من مرة", FancyToast.LENGTH_SHORT, FancyToast.WARNING, false).show();
                        discount_option2.setEnabled(false);
                        discount_option1.setEnabled(false);
                    } else {
                        if (pointsFreeShipping2){
                            freeShippingUsed = pointsFreeShipping2;
                        } else {

                            defaultPointsDiscountValue = pointsDiscountValue2;

                        }
                        subtractedPoints = minPoints;
                        redeemDiscount.setEnabled(false);
                        calculateCosts();
                        isClicked = true;
                        discount_option2.setEnabled(false);
                        discount_option1.setEnabled(false);
                        if ( !mPharmacyOrders.isEmpty() && productInCart.isEmpty() ){
                            if(pointsFreeShipping2){
                                pharmacyCost = "0";
                            } else {
                                BigDecimal finalPharmacyCost = shippingValue.subtract(pointsDiscountValue2);
                                if (finalPharmacyCost.compareTo(BigDecimal.ZERO)<=0)
                                    pharmacyCost = "0";
                                else
                                    pharmacyCost = String.valueOf(finalPharmacyCost);
                            }
                            total.setText(pharmacyCost);
                        }
                    }
                }
            }
        });
        pharmacyCart.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), PharmacyActivity.class);
            ActivityOptions options =
                    ActivityOptions.makeCustomAnimation(requireContext(), android.R.anim.slide_in_left, android.R.anim.slide_out_right);
            startActivity(intent, options.toBundle());
        });
        return view;
    }

    private void getPharmacyCart() {
        homeViewModel.getMyPharmacyCart().observe(requireActivity(),pharmacyOrders -> {
            mPharmacyOrders.clear();
            mPharmacyOrders.addAll(pharmacyOrders);
            if (mPharmacyOrders.isEmpty()){
                pharmacyContainer.setVisibility(View.GONE);
            } else {
                pharmacyContainer.setVisibility(View.VISIBLE);
                numberOfPharmacyItems.setText(mPharmacyOrders.size()+"");
            }
            handleCartSections();
        });
    }

    private void handleCartSections() {
        int pharmacyCartNumberOfItems = mPharmacyOrders.size();
        int cartNumberOfProducts      = productInCart.size();
        Log.d("phCart",pharmacyCartNumberOfItems+"");
        Log.d("cart",cartNumberOfProducts+"");
        if (pharmacyCartNumberOfItems != 0 && cartNumberOfProducts != 0){
            cartLayout.setVisibility(View.VISIBLE);
            noConnection.setVisibility(View.GONE);
            noConnection.setText("العربة فارغة");
        } else if (pharmacyCartNumberOfItems == 0 && cartNumberOfProducts != 0) {
            cartLayout.setVisibility(View.VISIBLE);
//            pharmacyContainer.setVisibility(View.GONE);
            calculations.setVisibility(View.VISIBLE);
            totalCostTV.setText("الحساب الإجمالي:");
            noConnection.setVisibility(View.GONE);
        } else if ( pharmacyCartNumberOfItems != 0 ) {
            cartLayout.setVisibility(View.VISIBLE);
            pharmacyContainer.setVisibility(View.VISIBLE);
            calculations.setVisibility(View.GONE);
            totalCostTV.setText("خدمة التوصيل:");
            if (shippingFee != null)
                pharmacyCost = shippingFee.getShippingFee();
                total.setText(pharmacyCost);
            noConnection.setVisibility(View.GONE);
            numberOfPharmacyItems.setText(String.valueOf(pharmacyCartNumberOfItems));
        } else {
            cartLayout.setVisibility(View.GONE);
            noConnection.setVisibility(View.VISIBLE);
            noConnection.setText("العربة فارغة");
        }
    }

    private void getPointsDiscount1() {
        homeViewModel.getPointsDiscountMutableLiveData1().observe(requireActivity(),
                pointsDiscount -> {
                if (pointsDiscount != null) {
                    pointsFreeShipping1 = pointsDiscount.isFreeShipping();
                    minPoints = pointsDiscount.getNumberOfPoints();
                    pointsDiscountValue = new BigDecimal(pointsDiscount.getDiscountValue());
                    discountOptions1.setVisibility(View.VISIBLE);

                    String title = "استخدم " + minPoints + " نقاط للحصول على:";
                    useYourPoints1.setText(title);

                    if (!pointsFreeShipping1) {
                        String discountText = "خصم " + pointsDiscountValue + " جنيه";
                        discount_option1.setText(discountText);
                    } else {
                        discount_option1.setText("شحن مجاني");
                    }
                    if (minPoints == 0)
                        discountOptions1.setVisibility(View.GONE);
                    if (myPoints < minPoints) {
                        discount_option1.setEnabled(false);
                    }
                } else
                    discountOptions1.setVisibility(View.GONE);
                });
    }
    private void getPointsDiscount2() {
        homeViewModel.getPointsDiscountMutableLiveData2().observe(requireActivity(),
                pointsDiscount -> {
                    if (pointsDiscount != null) {
                        pointsFreeShipping2 = pointsDiscount.isFreeShipping();
                        minPoints2 = pointsDiscount.getNumberOfPoints();
                        pointsDiscountValue2 = new BigDecimal(pointsDiscount.getDiscountValue());
                        discountOptions2.setVisibility(View.VISIBLE);
                        Log.d("pointsShipping", pointsFreeShipping1 + "");
                        Log.d("pointsShipping", minPoints + "");
                        Log.d("pointsShipping", pointsDiscountValue + "");
                        String title = "استخدم " + minPoints2 + " نقاط للحصول على:";
                        useYourPoints2.setText(title);

                        if (!pointsFreeShipping2) {
                            String discountText = "خصم " + pointsDiscountValue2 + " جنيه";
                            discount_option2.setText(discountText);
                        } else {
                            discount_option2.setText("شحن مجاني");
                        }
                        if (minPoints == 0)
                            discountOptions2.setVisibility(View.GONE);
                        if (myPoints < minPoints2) {
                            discount_option2.setEnabled(false);
                        }
                    } else
                        discountOptions2.setVisibility(View.GONE);
                });
    }
    private void initProductsRec() {
        productOrdersAdapter = new ProductOrdersAdapter(getContext(), orders);
        cartRec.setAdapter(productOrdersAdapter);
        cartRec.setNestedScrollingEnabled(true);
        LinearLayoutManager manager = new LinearLayoutManager(context);
        cartRec.setLayoutManager(manager);
        ItemTouchHelper itemTouchHelper = new
                ItemTouchHelper(new SwipeToDeleteCallback(productOrdersAdapter, new OrderProductInterface() {
            @Override
            public void onSwiped(int position) {
                showUndoSnackbar(position);
            }
        }));
        itemTouchHelper.attachToRecyclerView(cartRec);
    }
    private void getAllUsers() {
        homeViewModel.getAllUsers().observe(requireActivity(), new Observer<List<User>>() {
            @Override
            public void onChanged(List<User> users) {
                allUsers.clear();
                allUsers.addAll(users);
                getMyPoints();
            }
        });
    }

    private void getMyPoints() {
        for (User user : allUsers) {
            if (user.getUserId().equals(myId)) {
                myPoints = user.getCount();
            }
        }
    }

    private void fillCart() {
        orders.clear();
        for (Product product : allProducts) {
            for (Cart cart : productInCart) {
                if (cart.getProductName().equals(product.getProductName())) {
                    String imageUrl, unitWeight, productName, originalPrice, discount, discountType;
                    BigDecimal orderedAmount, availableAmount;
                    BigDecimal minimumOrderAmount;
                    imageUrl = product.getImageUrl();
                    productName = cart.getProductName();
                    orderedAmount = new BigDecimal(cart.getNumberOfProducts());
                    originalPrice = product.getPrice();
                    discount = product.getDiscount();
                    discountType = product.getDiscountUnit();
                    availableAmount = new BigDecimal(product.getAvailableAmount());
                    unitWeight = product.getUnitWeight();
                    minimumOrderAmount = new BigDecimal(product.getMinimumOrderAmount());
                    ProductOrder orderProduct = new ProductOrder(imageUrl, productName, orderedAmount.toString(), originalPrice, discount, discountType, availableAmount.toString(), unitWeight,minimumOrderAmount.toString());
                    orders.add(orderProduct);
                    productOrdersAdapter.notifyDataSetChanged();
                    break;
                }
            }

        }
        calculateCosts();

    }

    private void removeFromCart(String productName) {
        for (final User user:allUsers){
            DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
            reference.child("Cart").child(user.getUserId()).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    for (DataSnapshot dataSnapshot:snapshot.getChildren()){
                        Cart cart = dataSnapshot.getValue(Cart.class);
                        assert cart != null;
                        if (cart.getProductName().equals(productName)) {
                            dataSnapshot.getRef().removeValue();
                            allProductsAvailable= false;
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
    }

    public void initViewModel() throws ExecutionException, InterruptedException {
        homeViewModel = ViewModelProvider.AndroidViewModelFactory.getInstance(requireActivity().getApplication()).create(HomeViewModel.class);
        homeViewModel.init();
    }

    private void getAllProducts() {
        homeViewModel.getAllProducts().observe(requireActivity(), new Observer<List<Product>>() {
            @Override
            public void onChanged(List<Product> products) {
                allProducts.clear();
                allProducts.addAll(products);
                fillCart();

            }
        });
    }

    private void getAllProductsInCart() {
        homeViewModel.getProductsInCartLiveData().observe(requireActivity(), new Observer<List<Cart>>() {
            @Override
            public void onChanged(List<Cart> carts) {
                shimmerFrameLayout.stopShimmer();
                shimmerFrameLayout.setVisibility(View.GONE);
                cartLayout.setVisibility(View.VISIBLE);
                productInCart.clear();
                productInCart.addAll(carts);
                fillCart();
            }
        });
    }

    public void calculateCosts() {
        sumValue = BigDecimal.ZERO;
        discountValue = BigDecimal.ZERO;
        overAllDiscountValue = BigDecimal.ZERO;
        totalCostValue = BigDecimal.ZERO;
        shippingValue = BigDecimal.valueOf(10);
        if (!productInCart.isEmpty()) {
            handleCartSections();
            noConnection.setVisibility(View.GONE);
            if (shippingFee != null) {
                shippingValue = new BigDecimal(shippingFee.getShippingFee());
            }
            Log.d("free shipping", freeShipping + "");
            if (freeShippingUsed)
                shippingValue =BigDecimal.ZERO;
            shipping.setText("+ " + String.valueOf(shippingValue));
            for (ProductOrder orderProduct : orders) {
                BigDecimal orderedAmount = new BigDecimal(orderProduct.getOrdered());
                BigDecimal originalPriceValue = new BigDecimal(orderProduct.getOriginalPrice());
                sumValue = sumValue.add( orderedAmount.multiply(originalPriceValue));
                BigDecimal discountForOne = BigDecimal.ZERO;
                if (!orderProduct.getDiscount().isEmpty()) {
                    discountForOne = new BigDecimal(orderProduct.getDiscount());

                    if (orderProduct.getDiscountType().equals("%"))
                        discountForOne = originalPriceValue.multiply(discountForOne.divide(BigDecimal.valueOf(100)));

                    discountValue =discountValue.add( discountForOne .multiply(orderedAmount));
                }
            }
            sum.setText(String.valueOf(sumValue));
            discount.setText("- " +String.valueOf(discountValue));
            totalCostValue = sumValue.subtract(discountValue);
            Log.d("totalCost", totalCostValue + "");
            Log.d("shipping", shippingValue + "");
            Log.d("overall", overAllDiscountValue + "");
            Log.d("points", defaultPointsDiscountValue + "");
            if (overAllDiscount != null) {
                BigDecimal minimumForDiscount = new BigDecimal(overAllDiscount.getMinimum());
                overAllDiscountValue =new  BigDecimal(overAllDiscount.getDiscount());
                if (totalCostValue.compareTo(  minimumForDiscount)<0) {
                    overAllDiscountValue = BigDecimal.ZERO;
                } else {
                    if (overAllDiscount.getDiscount_unit().equals("%"))
                        overAllDiscountValue = totalCostValue.multiply(overAllDiscountValue.divide(BigDecimal.valueOf(100)));
                }
                overAllDiscountTextView.setText("- " +overAllDiscountValue);
            }
            totalCostValue = totalCostValue.add(shippingValue).subtract( overAllDiscountValue).subtract(defaultPointsDiscountValue);
            if (totalCostValue.compareTo(BigDecimal.ZERO)<=0)
                totalCostValue = BigDecimal.ZERO ;
            total.setText(String.valueOf(totalCostValue));

        } else {
            handleCartSections();
            noConnection.setVisibility(View.VISIBLE);
            noConnection.setText("العربة فارغة");
        }

    }

    public void getOverTotalMoneyDiscount() {
        homeViewModel.getDiscountMutableLiveData().observe(requireActivity(), new Observer<OverTotalMoneyDiscount>() {
            @Override
            public void onChanged(OverTotalMoneyDiscount overTotalMoneyDiscount) {
                overAllDiscount = overTotalMoneyDiscount;
            }
        });
    }

    public void getShippingFee() {
        homeViewModel.getShippingMutableLiveData().observe(requireActivity(), new Observer<Shipping>() {
            @Override
            public void onChanged(Shipping shipping) {
                shippingFee = shipping;
                shippingProgress1.hide();
                shippingProgress2.hide();
                complete.setEnabled(true);
                if (mPharmacyOrders.size() !=0 && productInCart.size()==0){
                    pharmacyCost = shippingFee.getShippingFee();
                    Log.d("shcost",pharmacyCost);
                    total.setText(pharmacyCost);

                }
            }
        });
    }


    private void showUndoSnackbar(int position) {
        ProductOrder product = orders.get(position);
        Snackbar snackbar = Snackbar.make(fullLayout, "تم إزالة العنصر", Snackbar.LENGTH_LONG);
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

    @Override
    public void onDestroy() {
        super.onDestroy();

    }

}