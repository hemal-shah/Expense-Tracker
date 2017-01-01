package hemal.t.shah.expensetracker;

import android.app.Application;

import com.facebook.stetho.Stetho;

/**
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
