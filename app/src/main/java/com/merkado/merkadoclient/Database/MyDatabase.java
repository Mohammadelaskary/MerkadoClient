package com.merkado.merkadoclient.Database;

import androidx.room.Database;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

@Database(entities = {ProductOrder.class, ShippingData.class, OrderCost.class}, version = 14, exportSchema = false)
@TypeConverters(Converter.class)
public abstract class MyDatabase extends RoomDatabase {
    public abstract OrderDao myDao();
}
