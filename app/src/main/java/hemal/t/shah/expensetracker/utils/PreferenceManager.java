package hemal.t.shah.expensetracker.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import java.util.ArrayList;

/**
 * Handles managing of Shared Preferences.
 * Created by hemal on 11/1/17.
 */

public class PreferenceManager {

    private static final String TAG = "PreferenceManager";

    private static final String PREFERENCE_FIREBASE = "fire_base_preferences";
    private static final String INITIAL_DATA_LOAD = "initial_data_load";
    private static final String IS_FIRST_TIME = "is_first_time";

    private static final String CLUSTER_KEYS = "cluster_keys";

    public static boolean checkInitialDataLoaded(Context context) {
        boolean isLoaded;
        SharedPreferences preferences = context.getSharedPreferences(PREFERENCE_FIREBASE,
                Context.MODE_PRIVATE);

        isLoaded = preferences.getBoolean(INITIAL_DATA_LOAD, false);
        return isLoaded;
    }

    public static void setInitialDataLoad(Context context, boolean value) {
        SharedPreferences preferences = context.getSharedPreferences(PREFERENCE_FIREBASE,
                Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean(INITIAL_DATA_LOAD, value);
        editor.apply();
    }

    /**
     * Add cluster keys to shared preferences so that it can be later checked if they are already
     * added or not.
     *
     * @param key the key to be added.
     */
    public static void addClusterKeyToTinyDB(Context context, String key) {
        TinyDB tinyDB = new TinyDB(context);

        ArrayList<String> keys = tinyDB.getListString(CLUSTER_KEYS);
        if (keys == null) {
            keys = new ArrayList<>();
        }
        keys.add(key);
        tinyDB.putListString(CLUSTER_KEYS, keys);
    }

    /**
     * Returns true if the key is already added to offline database.
     *
     * @param key clusterKey to be checked
     */
    public static boolean checkKeyAlreadyAdded(Context context, String key) {
        TinyDB tinyDB = new TinyDB(context);
        ArrayList<String> keys = tinyDB.getListString(CLUSTER_KEYS);
        return keys != null && keys.contains(key);
    }

    /**
     * Removes key if removed, exited by user.
     */
    public static boolean removeKeyFromAdded(Context context, String key) {
        TinyDB db = new TinyDB(context);
        ArrayList<String> list = db.getListString(CLUSTER_KEYS);
        if (list == null) return false;
        list.remove(key);
        db.putListString(CLUSTER_KEYS, list);
        Log.i(TAG, "removeKeyFromAdded: the key is removed successfully! " + key);
        return true;
    }

    /**
     * Removes all content from TinyDB
     */
    public static void removeAllKeys(Context context) {
        TinyDB db = new TinyDB(context);
        db.clear();
    }

    /**
     * To check if the application is opening for the first time or not.
     */
    public static boolean isFirstTimeAppOpened(Context context) {
        TinyDB db = new TinyDB(context);
        return db.getBoolean(IS_FIRST_TIME);
    }

    /**
     * Call this helper function if application is loaded for the first time.
     */
    public static void setFirstTimeOpened(Context context, boolean value) {
        TinyDB db = new TinyDB(context);
        db.putBoolean(IS_FIRST_TIME, value);
    }
}
