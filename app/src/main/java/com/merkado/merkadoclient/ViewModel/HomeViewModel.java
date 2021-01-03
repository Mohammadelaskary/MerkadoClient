package com.merkado.merkadoclient.ViewModel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.merkado.merkadoclient.Model.AdImages;
import com.merkado.merkadoclient.Model.Cart;
import com.merkado.merkadoclient.Model.DepartmentNames;
import com.merkado.merkadoclient.Model.FullOrder;
import com.merkado.merkadoclient.Model.Neighborhood;
import com.merkado.merkadoclient.Model.OverTotalMoneyDiscount;
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
    private MutableLiveData<PointsDiscount> pointsDiscountMutableLiveData;
    private MutableLiveData<Boolean> ispointsDiscountExist;
    private MutableLiveData<Boolean> specialOfferFinishLoading;
    private MutableLiveData<List<DepartmentNames>> depNamesLiveData;
    private MutableLiveData<List<SubDeparment>> subDepsLiveData;
    private MutableLiveData<List<Cart>> productsInCartLiveData;
    private MutableLiveData<List<User>> allUsersLiveData;
    private MutableLiveData<Shipping> shippingMutableLiveData;
    private MutableLiveData<List<Neighborhood>> neighborhoodLiveData;
    private MutableLiveData<List<FullOrder>> allCurrentOrdersLiveData;
    private MutableLiveData<List<FullOrder>> allPreOrdersLiveData;
    private MutableLiveData<Boolean> currentOrdersFinishLoading;
    private MutableLiveData<Boolean> preOrdersFinishLoading;
    private MutableLiveData<List<String>> allSerialsLiveData;
    private MutableLiveData<String> getPhoneNumber;
    private MutableLiveData<Boolean> allUsersFinishLoading;

    public LiveData<Boolean> getAllUsersFinishLoading() {
        return allUsersFinishLoading;
    }

    public LiveData<String> getGetPhoneNumber() {
        return getPhoneNumber;
    }

    public LiveData<List<String>> getAllSerialsLiveData() {
        return allSerialsLiveData;
    }

    public LiveData<PointsDiscount> getPointsDiscountMutableLiveData() {
        return pointsDiscountMutableLiveData;
    }

    public LiveData<Boolean> getIspointsDiscountExist() {
        return ispointsDiscountExist;
    }

    public LiveData<Boolean> getPreOrdersFinishLoading() {
        return preOrdersFinishLoading;
    }

    public LiveData<Boolean> getCurrentOrdersFinishLoading() {
        return currentOrdersFinishLoading;
    }

    public LiveData<List<FullOrder>> getAllCurrentOrdersLiveData() {
        return allCurrentOrdersLiveData;
    }

    public LiveData<List<FullOrder>> getAllPreOrdersLiveData() {
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

    public LiveData<Boolean> getSpecialOfferFinishLoading() {
        return specialOfferFinishLoading;
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
        if (pointsDiscountMutableLiveData != null)
            return;
        if (ispointsDiscountExist != null)
            return;
        if (allSerialsLiveData != null)
            return;
        if (getPhoneNumber != null)
            return;
        if (allUsersFinishLoading != null)
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
        pointsDiscountMutableLiveData = mRepository.getPointsDiscount();
        ispointsDiscountExist = mRepository.getPointsDiscountExist();
        allSerialsLiveData = mRepository.getAllSerials();
        getPhoneNumber = mRepository.getContactNumber();
        allUsersFinishLoading = mRepository.getAllUsersFinishedLoading();
    }


}
