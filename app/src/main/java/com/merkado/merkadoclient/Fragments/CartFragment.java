package com.merkado.merkadoclient.Fragments;

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
import androidx.coordinatorlayout.widget.CoordinatorLayout;
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
import com.merkado.merkadoclient.Model.Product;
import com.merkado.merkadoclient.Model.Shipping;
import com.merkado.merkadoclient.Model.User;
import com.merkado.merkadoclient.MyMethods;
import com.merkado.merkadoclient.R;
import com.merkado.merkadoclient.SwipeToDeleteCallback;
import com.merkado.merkadoclient.ViewModel.HomeViewModel;
import com.merkado.merkadoclient.Views.MainActivity;
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

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ExecutionException;


public class CartFragment extends Fragment {

    Context context;
    RecyclerView cartRec;
    TextView sum, discount, overAllDiscountTextView, shipping, total, noConnection, useYourPoints;
    LinearLayout discountOptions;
    HomeViewModel homeViewModel;
    List<ProductOrder> orders = new ArrayList<>();
    List<Product> allProducts = new ArrayList<>();
    List<Cart> productInCart = new ArrayList<>();
    ProductOrdersAdapter productOrdersAdapter;
    OverTotalMoneyDiscount overAllDiscount;
    MaterialButton getFreeShipping, getDiscount;
    boolean isPointsDiscountExist;
    Shipping shippingFee;
    RelativeLayout cartLayout;
    CoordinatorLayout fullLayout;
    MaterialButton complete;
    ShimmerFrameLayout shimmerFrameLayout;
    int minPoints;
    boolean freeShipping = false;
    boolean freeShippingUsed = false;
    float sumValue = 0;
    float discountValue = 0;
    float overAllDiscountValue = 0;
    float totalCostValue = 0;
    float shippingValue = 10;
    float defaultPointsDiscountValue = 0;
    float pointsDiscountValue = 0;
    boolean isClicked = false;
    int subtractedPoints = 0;
    List<User> allUsers = new ArrayList<>();
    String myId;
    int myPoints;
    boolean allProductsAvailable = true;

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
        useYourPoints = view.findViewById(R.id.use_your_points);
        discountOptions = view.findViewById(R.id.discount_options);
        getFreeShipping = view.findViewById(R.id.free_shipping);
        getDiscount = view.findViewById(R.id.discount_value);
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
                    if (orderProduct.getAvailable() <= 0) {
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
                                        OrderCost cost = new OrderCost(sumValue, discountValue, overAllDiscountValue, shippingValue, totalCostValue);
                                        MainActivity.dataBase.myDao().addTotalCost(cost);
                                        for (ProductOrder orderProduct:orders){
                                            MainActivity.dataBase.myDao().addOrder(orderProduct);
                                        }
                                        Intent intent = new Intent(getContext(), UserData.class);
                                        intent.putExtra("subtractedPoints", subtractedPoints);
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
                    OrderCost cost = new OrderCost(sumValue, discountValue, overAllDiscountValue, shippingValue, totalCostValue);
                    MainActivity.dataBase.myDao().addTotalCost(cost);
                    for (ProductOrder orderProduct:orders){
                        MainActivity.dataBase.myDao().addOrder(orderProduct);
                    }
                    Intent intent = new Intent(getContext(), UserData.class);
                    intent.putExtra("subtractedPoints", subtractedPoints);
                    context.startActivity(intent);
                }
            }
        });

        if (MyMethods.isConnected(Objects.requireNonNull(getContext()))) {
            getAllProducts();
            getAllProductsInCart();
            initProductsRec();
            getOverTotalMoneyDiscount();
            getShippingFee();
            getPointsDiscountExist();
            getAllUsers();
            if (isPointsDiscountExist) {
                useYourPoints.setVisibility(View.VISIBLE);
                discountOptions.setVisibility(View.VISIBLE);
                getPointsDiscount();
            }
            shimmerFrameLayout.startShimmer();
            shimmerFrameLayout.setVisibility(View.VISIBLE);
        } else {
            cartLayout.setVisibility(View.GONE);
            noConnection.setText("تأكد من الاتصال بالانترنت");
            noConnection.setVisibility(View.VISIBLE);
            shimmerFrameLayout.setVisibility(View.GONE);

        }

        getFreeShipping.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (myPoints < minPoints) {
                    FancyToast.makeText(getContext(), "يجب أن يكون لديك " + minPoints + " للحصول على الخصم", FancyToast.LENGTH_SHORT, FancyToast.INFO, false).show();
                } else {
                    if (isClicked)
                        FancyToast.makeText(getContext(), "لا يمكنك استخدام الخصم أكثر من مرة", FancyToast.LENGTH_SHORT, FancyToast.WARNING, false).show();
                    else {
                        subtractedPoints = minPoints;

                        freeShippingUsed = freeShipping;
                        getFreeShipping.setEnabled(false);
                        getDiscount.setEnabled(false);
                        calculateCosts();
                        isClicked = true;
                    }
                }
            }
        });

        getDiscount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (myPoints < minPoints) {
                    FancyToast.makeText(getContext(), "يجب أن يكون لديك " + minPoints + " للحصول على الخصم", FancyToast.LENGTH_SHORT, FancyToast.INFO, false).show();
                } else {
                    if (isClicked) {
                        FancyToast.makeText(getContext(), "لا يمكنك استخدام الخصم أكثر من مرة", FancyToast.LENGTH_SHORT, FancyToast.WARNING, false).show();
                    } else {
                        defaultPointsDiscountValue = pointsDiscountValue;
                        getFreeShipping.setEnabled(false);
                        getDiscount.setEnabled(false);
                        subtractedPoints = minPoints;

                        calculateCosts();
                        isClicked = true;
                    }
                }
            }
        });

        return view;
    }

    private void getPointsDiscount() {
        homeViewModel.getPointsDiscountMutableLiveData().observe(Objects.requireNonNull(getActivity()),
                pointsDiscount -> {
                    freeShipping = pointsDiscount.isFreeShipping();
                    minPoints = pointsDiscount.getNumberOfPoints();

                    pointsDiscountValue = pointsDiscount.getDiscountValue();
                    if (!freeShipping)
                        getFreeShipping.setVisibility(View.GONE);
                    if (pointsDiscountValue == 0)
                        getDiscount.setVisibility(View.GONE);
                    if (myPoints < minPoints) {
                        getFreeShipping.setEnabled(false);
                        getDiscount.setEnabled(false);
                    }
                    String discountText = "خصم " + pointsDiscountValue + " جنيه";
                    getDiscount.setText(discountText);
                });
    }

    private void getPointsDiscountExist() {
        homeViewModel.getIspointsDiscountExist().observe(Objects.requireNonNull(getActivity()),
                new Observer<Boolean>() {
                    @Override
                    public void onChanged(Boolean aBoolean) {
                        isPointsDiscountExist = aBoolean;
                    }
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
        homeViewModel.getAllUsers().observe(Objects.requireNonNull(getActivity()), new Observer<List<User>>() {
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
                    float orderedAmount, availableAmount;
                    float minimumOrderAmount;
                    imageUrl = product.getImageUrl();
                    productName = cart.getProductName();
                    orderedAmount = cart.getNumberOfProducts();
                    originalPrice = product.getPrice();
                    discount = product.getDiscount();
                    discountType = product.getDiscountUnit();
                    availableAmount = product.getAvailableAmount();
                    unitWeight = product.getUnitWeight();
                    minimumOrderAmount = product.getMinimumOrderAmount();
                    ProductOrder orderProduct = new ProductOrder(imageUrl, productName, orderedAmount, originalPrice, discount, discountType, availableAmount, unitWeight,minimumOrderAmount);
                    orders.add(orderProduct);
                    productOrdersAdapter.notifyDataSetChanged();
                    break;
                }
            }

        }
        calculateCosts();
//          for (Cart cart:productInCart){
//              for (int i = 0; i <orders.size() ; i++) {
//                  if (cart.getProductName().equals(orders.get(i).getProductName())) {
//                      break;
//                  }  else {
//                      if (i == orders.size()-1||orders.get(i).getAvailableAmount()<=0) {
//                          removeFromCart(cart.getProductName());
//                          Log.d("removedProduct", cart.getProductName());
//                      }
//                  }
//              }
//          }
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
        homeViewModel = ViewModelProvider.AndroidViewModelFactory.getInstance(Objects.requireNonNull(getActivity()).getApplication()).create(HomeViewModel.class);
        homeViewModel.init();
    }

    private void getAllProducts() {
        homeViewModel.getAllProducts().observe(Objects.requireNonNull(getActivity()), new Observer<List<Product>>() {
            @Override
            public void onChanged(List<Product> products) {
                allProducts.clear();
                allProducts.addAll(products);
                fillCart();

            }
        });
    }

    private void getAllProductsInCart() {
        homeViewModel.getProductsInCartLiveData().observe(Objects.requireNonNull(getActivity()), new Observer<List<Cart>>() {
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
        sumValue = 0;
        discountValue = 0;
        overAllDiscountValue = 0;
        totalCostValue = 0;
        shippingValue = 10;
        if (!productInCart.isEmpty()) {
            cartLayout.setVisibility(View.VISIBLE);
            noConnection.setVisibility(View.GONE);
            if (shippingFee != null) {
                shippingValue = shippingFee.getShippingFee();
            }
            Log.d("free shipping", freeShipping + "");
            if (freeShippingUsed)
                shippingValue = 0;
            shipping.setText("+ " + shippingValue);
            for (ProductOrder orderProduct : orders) {
                float orderedAmount = orderProduct.getOrdered();
                float originalPriceValue = Float.parseFloat(orderProduct.getOriginalPrice());
                sumValue += orderedAmount * originalPriceValue;
                float discountForOne = 0;
                if (!orderProduct.getDiscount().isEmpty()) {
                    discountForOne = Float.parseFloat(orderProduct.getDiscount());

                    if (orderProduct.getDiscountType().equals("%"))
                        discountForOne = originalPriceValue * (discountForOne / 100);

                    discountValue += discountForOne * orderedAmount;
                }
            }
            sum.setText(String.valueOf(sumValue));
            discount.setText("- " + discountValue);
            totalCostValue = sumValue - discountValue;
            Log.d("totalCost", totalCostValue + "");
            Log.d("shipping", shippingValue + "");
            Log.d("overall", overAllDiscountValue + "");
            Log.d("points", defaultPointsDiscountValue + "");
            if (overAllDiscount != null) {
                float minimumForDiscount = Float.parseFloat(overAllDiscount.getMinimum());
                overAllDiscountValue = Float.parseFloat(overAllDiscount.getDiscount());
                if (totalCostValue < minimumForDiscount) {
                    overAllDiscountValue = 0;
                } else {
                    if (overAllDiscount.getDiscount_unit().equals("%"))
                        overAllDiscountValue = totalCostValue * (overAllDiscountValue / 100);
                }
                overAllDiscountTextView.setText("- " + overAllDiscountValue);
            }
            totalCostValue = totalCostValue + shippingValue - overAllDiscountValue - defaultPointsDiscountValue;
            total.setText(String.valueOf(totalCostValue));

        } else {
            cartLayout.setVisibility(View.GONE);
            noConnection.setVisibility(View.VISIBLE);
            noConnection.setText("العربة فارغة");
        }

    }

    public void getOverTotalMoneyDiscount() {
        homeViewModel.getDiscountMutableLiveData().observe(Objects.requireNonNull(getActivity()), new Observer<OverTotalMoneyDiscount>() {
            @Override
            public void onChanged(OverTotalMoneyDiscount overTotalMoneyDiscount) {
                overAllDiscount = overTotalMoneyDiscount;
            }
        });
    }

    public void getShippingFee() {
        homeViewModel.getShippingMutableLiveData().observe(Objects.requireNonNull(getActivity()), new Observer<Shipping>() {
            @Override
            public void onChanged(Shipping shipping) {
                shippingFee = shipping;
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