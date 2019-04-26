package com.example.myimdb.helpers;


import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.google.gson.Gson;

import static android.text.TextUtils.indexOf;

public class SharedPreferencesHelper {

    /* Constants */
    private static final String TAG = SharedPreferencesHelper.class.getCanonicalName();

    /* Properties */
    private Context context = null;

    /* Variables */
    private static SharedPreferencesHelper instance = null; // There is no memory leak since the context is passed by Application. Ignore Lint Error.

    /* Constructor */
    private SharedPreferencesHelper() {
    }

    public static SharedPreferencesHelper getInstance() {
        if (instance == null) {
            instance = new SharedPreferencesHelper();
        }
        return instance;
    }


    /* Methods */

    public void initialize(Context context) {
        setContext(context);
    }

    private SharedPreferences getPreferences(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context);
    }

    private void setContext(Context context) {
        this.context = context;
    }





    /* String */

    public void setPreferences(String key, String value) {

        SharedPreferences.Editor editor = getPreferences(context).edit();
        editor.putString(key, value);
        editor.apply();
    }

    public String getPreferences(String key, String defaultValue) {

        return getPreferences(context).getString(key, defaultValue);
    }

    /* Boolean */

    /*public void setPreferences(String key, boolean value) {

        SharedPreferences.Editor editor = getPreferences(context).edit();
        editor.putBoolean(key, value);
        editor.apply();
    }

    public boolean getPreferences(String key, boolean defaultValue) {

        return getPreferences(context).getBoolean(key, defaultValue);
    }
*/

    /* Abstract Class */

    public <T> void setPreferences(String key, T object) {

        SharedPreferences.Editor editor = getPreferences(context).edit();
        Gson gson = new Gson();
        String json = gson.toJson(object);
        editor.putString(key, json);
        editor.apply();
    }

    public <T> T getPreferences(Class<T> clazz, String key) {

        Gson gson = new Gson();
        String json = getPreferences(context).getString(key, null);

        if (json == null) return null;

        return gson.fromJson(json, clazz);
    }

    /* Delete */

    public void removePreferences(String key) {
        SharedPreferences.Editor editor = getPreferences(context).edit();
        editor.remove(key);
        editor.apply();
    }


}
