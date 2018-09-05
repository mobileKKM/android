package de.codebucket.mkkm.webview;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;
import android.webkit.JavascriptInterface;

public class LocalStorageAdapter {

    public static final String TAG = "LocalStorageAdapter";

    private Context mContext;
    private SharedPreferences preferences;

    public LocalStorageAdapter(Context context) {
        mContext = context;
        preferences = PreferenceManager.getDefaultSharedPreferences(context);
    }

    /**
     * This method allows to get an item for the given key
     * @param key: the key to look for in the local storage
     * @return the item having the given key
     */
    @JavascriptInterface
    public String getItem(String key) {
        Log.d(TAG, "Reading key '" + key + "' from preferences");
        return preferences.getString(key, null);
    }

    /**
     * set the value for the given key, or create the set of data if the key does not exist already.
     * @param key
     * @param value
     */
    @JavascriptInterface
    public void setItem(String key, String value) {
        Log.d(TAG, "Writing key '" + key + "' with value '" + value + "' to preferences");
        preferences.edit().putString(key, value).apply();
    }

    /**
     * removes the item corresponding to the given key
     * @param key
     */
    @JavascriptInterface
    public void removeItem(String key) {
        Log.d(TAG, "Removing key '" + key + "' from preferences");
        preferences.edit().remove(key).apply();
    }

    /**
     * clears all the local storage.
     */
    @JavascriptInterface
    public void clear() {
        throw new UnsupportedOperationException("Not implemented yet");
    }
}
