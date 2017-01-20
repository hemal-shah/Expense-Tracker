package hemal.t.shah.expensetracker;

import android.app.Application;

import com.facebook.stetho.Stetho;

/**
 * Check stetho only if in debugging mode.
 * Created by hemal on 29/12/16.
 */
public class AppClass extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        if (BuildConfig.DEBUG) {
            Stetho.initializeWithDefaults(this);
        }
    }
}
