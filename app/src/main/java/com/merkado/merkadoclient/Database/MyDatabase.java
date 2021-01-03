package com.merkado.merkadoclient.Database;

import androidx.room.Database;
import androidx.room.RoomDatabase;

@Database(entities = {ProductOrder.class, ShippingData.class, OrderCost.class}, version = 12, exportSchema = false)
public abstract class MyDatabase extends RoomDatabase {
    public abstract OrderDao myDao();
}
