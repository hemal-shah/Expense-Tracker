package hemal.t.shah.expensetracker;

import android.app.Application;

import com.facebook.stetho.Stetho;

/**
 * Created by hemal on 16/12/16.
 */
public class AppClass extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        Stetho.initializeWithDefaults(this);
    }
}
