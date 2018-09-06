package de.codebucket.mkkm.webview;

import android.util.Log;
import android.webkit.JavascriptInterface;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;

public class UserProfileStorage {

    public static final String TAG = "UserProfileStorage";

    private File mFile;
    private JSONObject json;

    public UserProfileStorage(File file) {
        mFile = file;
        load();
    }

    private void load() {
        try {
            FileInputStream input = new FileInputStream(mFile);
            byte[] buffer = new byte[(int) mFile.length()];
            input.read(buffer);
            input.close();
            json = new JSONObject(new String(buffer, "UTF-8"));
        } catch (IOException | JSONException ex) {
            Log.e(TAG, "Unable to load file: " + ex);
        }
    }

    @JavascriptInterface
    public String getItem(String key) {
        Log.d(TAG, "Reading key '" + key + "' from storage");

        try {
            Object value = json.get(key);
            if (value instanceof  JSONObject) {
                JSONObject obj = (JSONObject) value;
                return obj.toString();
            }

            return (String) value;
        } catch (JSONException ex) {
            return null;
        }
    }

    @JavascriptInterface
    public void setItem(String key, String value) {
        Log.d(TAG, "Writing key '" + key + "' with value '" + value + "' to storage");

        try {
            if (isJsonObject(value)) {
                JSONObject obj = new JSONObject(value);
                json.put(key, obj);
            } else {
                json.put(key, value);
            }

            save();
        } catch (IOException | JSONException ex) {
            Log.e(TAG, "Unable to save file: " + ex);
        }
    }

    @JavascriptInterface
    public void removeItem(String key) {
        Log.d(TAG, "Removing key '" + key + "' from preferences");

        try {
            json.remove(key);
            save();
        } catch (IOException ex) {
            Log.e(TAG, "Unable to save file: " + ex);
        }
    }

    @JavascriptInterface
    public void clear() {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    private boolean isJsonObject(String json) {
        try {
            new JSONObject(json);
        } catch (JSONException ex) {
            return false;
        }

        return true;
    }

    private void save() throws IOException {
        Writer output = new BufferedWriter(new FileWriter(mFile));
        output.write(json.toString());
        output.close();
    }

    public static void init(File file) {
        try {
            if (!file.exists()) {
                file.createNewFile();
            }

            Writer output = new BufferedWriter(new FileWriter(file));
            output.write("{}");
            output.close();
        } catch (IOException ex) {
            Log.e(TAG, "Unable to init userprofile storage on device: " + ex);
        }
    }
}
