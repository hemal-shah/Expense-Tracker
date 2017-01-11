package hemal.t.shah.expensetracker.utils;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Handles managing of Shared Preferences.
 * Created by hemal on 11/1/17.
 */

public class PreferenceManager {

    private static final String PREFERENCE_FIREBASE = "fire_base_preferences";
    private static final String INITIAL_DATA_LOAD = "initial_data_load";

    public static boolean checkInitialDataLoaded(Context context) {
        boolean isLoaded = false;
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
}
