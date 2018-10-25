package de.codebucket.mkkm.database.converter;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;

import androidx.room.TypeConverter;

import java.io.ByteArrayOutputStream;

public class BitmapBase64Converter {

    @TypeConverter
    public static Bitmap toBitmap(String value) {
        byte[] decodedString = Base64.decode(value, Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
    }

    @TypeConverter
    public static String toString(Bitmap value) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        value.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
        byte[] byteArray = byteArrayOutputStream.toByteArray();
        return Base64.encodeToString(byteArray, Base64.DEFAULT);
    }
}
