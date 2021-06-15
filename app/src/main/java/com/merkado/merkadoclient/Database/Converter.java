package com.merkado.merkadoclient.Database;

import androidx.room.TypeConverter;

import java.math.BigDecimal;

public class Converter {
    @TypeConverter
    public BigDecimal floatToBigDecimal (Float num){
        return BigDecimal.valueOf(num);
    }
    @TypeConverter
    public float BigDecimalToFloat (BigDecimal bigDecimal){
        return bigDecimal.floatValue();
    }
}
