package de.codebucket.mkkm.database.converter;

import android.text.TextUtils;

import androidx.room.TypeConverter;

public class StringArrayConverter {

    @TypeConverter
    public static String toString(String[] value) {
        return TextUtils.join(",", value);
    }

    @TypeConverter
    public static String[] toArray(String value) {
        return value.split(",");
    }
}
