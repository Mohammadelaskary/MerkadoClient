package com.merkado.merkadoclient.ViewModel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.merkado.merkadoclient.Model.AdImages;
import com.merkado.merkadoclient.Model.Cart;
import com.merkado.merkadoclient.Model.DepartmentNames;
import com.merkado.merkadoclient.Model.FullOrder;
import com.merkado.merkadoclient.Model.Neighborhood;
import com.merkado.merkadoclient.Model.Order;
import com.merkado.merkadoclient.Model.OverTotalMoneyDiscount;
import com.merkado.merkadoclient.Model.PharmacyOrder;
import com.merkado.merkadoclient.Model.PointsDiscount;
import com.merkado.merkadoclient.Model.Product;
import com.merkado.merkadoclient.Model.Shipping;
import com.merkado.merkadoclient.Model.SubDeparment;
import com.merkado.merkadoclient.Model.User;
import com.merkado.merkadoclient.Repository.Repository;

import java.util.List;
import java.util.concurrent.ExecutionException;

public class HomeViewModel extends ViewModel {

    private MutableLiveData<List<Product>> allProducts;
    private MutableLiveData<Boolean> allProductsFinishLoading;
    private MutableLiveData<List<AdImages>> adImages;
    private MutableLiveData<Boolean> adImagesFinishLoading;
    private MutableLiveData<OverTotalMoneyDiscount> discountMutableLiveData;
    private MutableLiveData<PointsDiscount> pointsDiscountMutableLiveData1;
    private MutableLiveData<Boolean> ispointsDiscountExist1;
    private MutableLiveData<PointsDiscount> pointsDiscountMutableLiveData2;
    private MutableLiveData<Boolean> ispointsDiscountExist2;
    private MutableLiveData<Boolean> specialOfferFinishLoading;
    private MutableLiveData<List<DepartmentNames>> depNamesLiveData;
    private MutableLiveData<List<SubDeparment>> subDepsLiveData;
    private MutableLiveData<List<Cart>> productsInCartLiveData;
    private MutableLiveData<List<User>> allUsersLiveData;
    private MutableLiveData<Shipping> shippingMutableLiveData;
    private MutableLiveData<List<Neighborhood>> neighborhoodLiveData;
    private MutableLiveData<List<Order>> allCurrentOrdersLiveData;
    private MutableLiveData<List<Order>> allPreOrdersLiveData;
    private MutableLiveData<Boolean> currentOrdersFinishLoading;
    private MutableLiveData<Boolean> preOrdersFinishLoading;
    private MutableLiveData<List<String>> allSerialsLiveData;
    private MutableLiveData<String> getPhoneNumber;
    private MutableLiveData<Boolean> allUsersFinishLoading;
    private MutableLiveData<List<PharmacyOrder>> myPharmacyCart;
    private MutableLiveData<List<PharmacyOrder>> myPharmacyOrders;

    public LiveData<List<PharmacyOrder>> getMyPharmacyOrders() {
        return myPharmacyOrders;
    }

    public LiveData<Boolean> getAllUsersFinishLoading() {
        return allUsersFinishLoading;
    }

    public LiveData<String> getGetPhoneNumber() {
        return getPhoneNumber;
    }

    public LiveData<List<String>> getAllSerialsLiveData() {
        return allSerialsLiveData;
    }

    public LiveData<PointsDiscount> getPointsDiscountMutableLiveData1() {
        return pointsDiscountMutableLiveData1;
    }
    public LiveData<PointsDiscount> getPointsDiscountMutableLiveData2() {
        return pointsDiscountMutableLiveData2;
    }
    public LiveData<Boolean> getIspointsDiscountExist1() {
        return ispointsDiscountExist1;
    }
    public LiveData<Boolean> getIspointsDiscountExist2() {
        return ispointsDiscountExist2;
    }
    public LiveData<Boolean> getPreOrdersFinishLoading() {
        return preOrdersFinishLoading;
    }

    public LiveData<Boolean> getCurrentOrdersFinishLoading() {
        return currentOrdersFinishLoading;
    }

    public LiveData<List<Order>> getAllCurrentOrdersLiveData() {
        return allCurrentOrdersLiveData;
    }

    public LiveData<List<Order>> getAllPreOrdersLiveData() {
        return allPreOrdersLiveData;
    }

    public LiveData<List<Neighborhood>> getNeighborhoodLiveData() {
        return neighborhoodLiveData;
    }

    public LiveData<Shipping> getShippingMutableLiveData() {
        return shippingMutableLiveData;
    }

    public LiveData<Integer> getNumberOfProductsInCart() {
        return numberOfProductsInCart;
    }

    private MutableLiveData<Integer> numberOfProductsInCart;
    Repository mRepository;

    public LiveData<List<User>> getAllUsers() {
        return allUsersLiveData;
    }

    public LiveData<List<SubDeparment>> getSubDepsLiveData() {
        return subDepsLiveData;
    }

    public MutableLiveData<List<DepartmentNames>> getDepNamesLiveData() {
        return depNamesLiveData;
    }

    public LiveData<OverTotalMoneyDiscount> getDiscountMutableLiveData() {
        return discountMutableLiveData;
    }

    public LiveData<List<AdImages>> getAdImages() {
        return adImages;
    }


    public LiveData<Boolean> getAdImagesFinishLoading() {
        return adImagesFinishLoading;
    }


    public LiveData<List<Product>> getAllProducts() {
        return allProducts;
    }

    public LiveData<Boolean> isAllProductsFinishLoading() {
        return allProductsFinishLoading;
    }


    public LiveData<List<Cart>> getProductsInCartLiveData() {
        return productsInCartLiveData;
    }

    public LiveData<List<PharmacyOrder>> getMyPharmacyCart() {
        return myPharmacyCart;
    }

    public void init() throws ExecutionException, InterruptedException {
        if (adImages != null)
            return;
        if (adImagesFinishLoading != null)
            return;
        if (allProducts != null)
            return;
        if (allProductsFinishLoading != null)
            return;
        if (discountMutableLiveData != null)
            return;
        if (specialOfferFinishLoading != null)
            return;
        if (depNamesLiveData != null)
            return;
        if (subDepsLiveData != null)
            return;
        if (productsInCartLiveData != null)
            return;
        if (allUsersLiveData != null)
            return;
        if (numberOfProductsInCart != null)
            return;
        if (shippingMutableLiveData != null)
            return;
        if (neighborhoodLiveData != null)
            return;
        if (allCurrentOrdersLiveData != null)
            return;
        if (allPreOrdersLiveData != null)
            return;
        if (currentOrdersFinishLoading != null)
            return;
        if (preOrdersFinishLoading != null)
            return;
        if (pointsDiscountMutableLiveData1 != null)
            return;
        if (ispointsDiscountExist1 != null)
            return;
        if (pointsDiscountMutableLiveData2 != null)
            return;
        if (ispointsDiscountExist2 != null)
            return;
        if (allSerialsLiveData != null)
            return;
        if (getPhoneNumber != null)
            return;
        if (allUsersFinishLoading != null)
            return;
        if (myPharmacyCart != null)
            return;
        if (myPharmacyOrders != null)
            return;

        mRepository = Repository.getInstance();
        allProducts = mRepository.getAllProducts();
        allProductsFinishLoading = mRepository.isAllProductsFinishLoading();
        adImages = mRepository.getAds();
        adImagesFinishLoading = mRepository.isAdImagesFinishLoading();
        discountMutableLiveData = mRepository.getOverTotalMoneyDiscount();
        specialOfferFinishLoading = mRepository.getSpecialOfferFinishLoading();
        depNamesLiveData = mRepository.getDepartmentsNames();
        subDepsLiveData = mRepository.getSubDepartments();
        productsInCartLiveData = mRepository.getAllProductsInCart();
        allUsersLiveData = mRepository.getAllUsers();
        numberOfProductsInCart = mRepository.getNumberOfProductsInCart();
        shippingMutableLiveData = mRepository.getShippingFee();
        neighborhoodLiveData = mRepository.getNeighborhoods();
        allCurrentOrdersLiveData = mRepository.getAllCurrentOrders();
        allPreOrdersLiveData = mRepository.getAllPreOrders();
        currentOrdersFinishLoading = mRepository.getCurrentOrdersFinishLoading();
        preOrdersFinishLoading = mRepository.getPreOrdersFinishLoading();
        pointsDiscountMutableLiveData1 = mRepository.getPointsDiscount1();
        ispointsDiscountExist1 = mRepository.getPointsDiscountExist1();
        pointsDiscountMutableLiveData2 = mRepository.getPointsDiscount2();
        ispointsDiscountExist2 = mRepository.getPointsDiscountExist2();
        allSerialsLiveData = mRepository.getAllSerials();
        getPhoneNumber = mRepository.getContactNumber();
        allUsersFinishLoading = mRepository.getAllUsersFinishedLoading();
        myPharmacyCart = mRepository.getMyPharmacyCart();
        myPharmacyOrders = mRepository.getMyPharmacyOrders();
    }


}
