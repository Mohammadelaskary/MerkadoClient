package com.merkado.merkadoclient.Database;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface OrderDao {
    @Insert
    void addOrder(ProductOrder productOrder);

    @Delete
    void deleteOrder(ProductOrder productOrder);

    @Query("select * from `Orders`")
    List<ProductOrder> getAllOrders();

    @Query("delete from `Orders`")
    void deleteAllOrders();

    @Insert
    void addShippingData(ShippingData shippingData);

    @Delete
    void deleteShippingData(ShippingData shippingData);

    @Query("select * from ShippingData")
    List<ShippingData> getAllShippingData();

    @Query("delete from ShippingData")
    void deleteAllShippingData();

    @Insert
    void addTotalCost(OrderCost orderCost);

    @Delete
    void deleteTotalCost(OrderCost orderCost);

    @Query("select * from `total cost`")
    List<OrderCost> getAllOrderCost();

    @Query("delete from `total cost`")
    void deleteAllOrderCost();

}
