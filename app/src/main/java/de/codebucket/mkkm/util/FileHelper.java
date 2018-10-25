package de.codebucket.mkkm.util;

import android.content.Context;
import android.net.Uri;
import android.os.Environment;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class FileHelper {

    public static boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        return Environment.MEDIA_MOUNTED.equals(state);
    }

    public static boolean isExternalStorageReadable() {
        String state = Environment.getExternalStorageState();
        return Environment.MEDIA_MOUNTED.equals(state) || Environment.MEDIA_MOUNTED_READ_ONLY.equals(state);
    }

    public static String readFileToString(Context context, Uri file) {
        StringBuilder stringBuilder = new StringBuilder();

        try {
            InputStream inputStream = context.getContentResolver().openInputStream(file);
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));

            String line;
            while ((line = reader.readLine()) != null) {
                stringBuilder.append(line).append("\n");
            }

            reader.close();
            inputStream.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return stringBuilder.toString();
    }

    public static boolean writeStringToFile(Context context, Uri file, String content) {
        try {
            OutputStream outputStream = context.getContentResolver().openOutputStream(file);
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(outputStream));

            writer.write(content);
            writer.close();
            outputStream.close();
            return true;
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return false;
    }

    public static String generateBackupFilename() {
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
        Date now = Calendar.getInstance().getTime();
        return String.format(Const.BACKUP_FILENAME_JSON, df.format(now));
    }
}
