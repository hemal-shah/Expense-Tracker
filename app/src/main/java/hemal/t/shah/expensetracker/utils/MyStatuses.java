package hemal.t.shah.expensetracker.utils;

import android.content.Context;
import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Created by hemal on 24/1/17.
 */

public class MyStatuses {

    @Retention(RetentionPolicy.SOURCE)
    @IntDef({STATUS_ERROR_NO_NETWORK, STATUS_OK, STATUS_UNKNOWN})
    public @interface Statuses {
    }

    public static final int STATUS_OK = 0;
    public static final int STATUS_ERROR_NO_NETWORK = 1;
    public static final int STATUS_UNKNOWN = 2;

    //Shared Preferences access strings...
    public static final String STATUS_ACCESS_PC = "personal_cluster_status";
    public static final String STATUS_ACCESS_SC = "shared_cluster_status";
    public static final String STATUS_ACCESS_PE = "personal_expense_status";
    public static final String STATUS_ACCESS_SE = "shared_expense_status";

    public static void setPersonalClusterStatus(Context context,
            @Statuses int status) {
        TinyDB db = new TinyDB(context);
        db.putInt(STATUS_ACCESS_PC, status);
    }


    public static void setSharedClusterStatus(Context context,
            @Statuses int status) {
        TinyDB db = new TinyDB(context);
        db.putInt(STATUS_ACCESS_SC, status);
    }


    public static void setPersonalExpenseStatus(Context context,
            @Statuses int status) {
        TinyDB db = new TinyDB(context);
        db.putInt(STATUS_ACCESS_PE, status);
    }


    public static void setSharedExpenseStatus(Context context,
            @Statuses int status) {
        TinyDB db = new TinyDB(context);
        db.putInt(STATUS_ACCESS_SE, status);
    }

    public static int getStatus(Context context, String accessString) {
        TinyDB db = new TinyDB(context);
        return db.getInt(accessString);
    }
}
