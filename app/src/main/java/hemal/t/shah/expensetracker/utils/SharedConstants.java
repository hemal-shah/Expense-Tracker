package hemal.t.shah.expensetracker.utils;

import hemal.t.shah.expensetracker.data.ExpenseContract.ClusterEntry;

/**
 * Simple utility class that stores all necessary constants.
 * Created by hemal on 17/12/16.
 */
@SuppressWarnings("SpellCheckingInspection")
public class SharedConstants {

    //To add new cluster.
    public static final int TOKEN_ADD_NEW_CLUSTER = 100;

    //to check if title of cursor already exist.
    public static final int TOKEN_CHECK_FOR_CLUSTER_TITLE = 101;

    //To load all personal clusters for recyclerView
    public static final int CURSOR_PERSONAL = 102;

    //sort cursor a to z
    public static final int CURSOR_PERSONAL_A_Z = 111;

    //sort cursor z to a
    public static final int CURSOR_PERSONAL_Z_A = 112;

    //To load shared clusters for recyclerView
    public static final int CURSOR_SHARED = 103;
    public static final int CURSOR_SHARED_A_Z = 117;
    public static final int CURSOR_SHARED_Z_A = 118;

    //to delete existing clusters.
    public static final int TOKEN_DELETE_CLUSTER = 104;

    //to load expenses from personal clusters.
    public static final int CURSOR_EXPENSES_PERSONAL = 105;

    //sort p expenses a to z
    public static final int CURSOR_EXPENSES_PERSONAL_A_Z = 113;

    //sort p expenses z to a
    public static final int CURSOR_EXPENSES_PERSONAL_Z_A = 114;

    //sort p expenses highest to lowest
    public static final int CURSOR_EXPENSES_PERSONAL_H_L = 115;

    //sort p expenses lowest to highest
    public static final int CURSOR_EXPENSES_PERSONAL_L_H = 116;


    //sort s expenses a to z
    public static final int CURSOR_S_EXPENSES_A_Z = 117;

    //sort s expenses z to a
    public static final int CURSOR_S_EXPENSES_Z_A = 118;

    //sort s expenses high to low
    public static final int CURSOR_S_EXPENSES_H_L = 119;

    //sort s expenses low to high
    public static final int CURSOR_S_EXPENSES_L_H = 120;

    //sort s expenses based on user name
    public static final int CURSOR_S_EXPENSES_NAME = 121;

    //to load expenses from shared clusters.
    public static final int CURSOR_EXPENSES_SHARED = 106;

    public static final int TOKEN_ADD_NEW_EXPENSE = 107;

    //to delete expenses...
    public static final int TOKEN_DELETE_EXPENSES = 108;

    //clear entire cluster table on sign out
    public static final int TOKEN_CLEAR_TABLE_CLUSTER = 109;

    //clear entire expense table on sign out
    public static final int TOKEN_CLEAR_TABLE_EXPENSE = 110;

    public static final String TOKEN_TWO_PANE = "two_pane_mode";

    public static final String SHARE_CLUSTER_PARCEL = "parcel";

    //Firebase Constants start here...
    public static final String FIREBASE_PATH_SHARED_CLUSTERS = "shared_clusters";
    public static final String FIREBASE_PATH_PERSONAL_CLUSTERS = "personal_clusters";
    public static final String FIREBASE_TITLE = "title";
    public static final String FIREBASE_USER_UID = "uid";
    public static final String FIREBASE_USER_NAME = "name";
    public static final String FIREBASE_ABOUT = "about";
    public static final String FIREBASE_AMOUNT = "amount";
    public static final String FIREBASE_CREATED_BY = "user_id";
    public static final String FIREBASE_EXPENSES = "expenses";
    public static final String FIREBASE_DESCRIPTION = "description";
    public static final String FIREBASE_BY_USER = "by_user";
    public static final String FIREBASE_EMAIL = "email";
    public static final String FIREBASE_PROFILE_URL = "profile_url";
    public static final String FIREBASE_TIME_STAMP = "timestamp";
    public static final String FIREBASE_PATH_CLUSTER_ID = "shared_clusters_join_id";
    public static final String FIREBASE_USERS_IN_CLUSTERS = "users_in_clusters";
    public static final String FIREBASE_CLUSTERS_OF_USERS = "clusters_of_users";

    //projection strings...
    public static final String[] PROJECTION_CLUSTER = {
            ClusterEntry._ID,
            ClusterEntry.COLUMN_TITLE,
            ClusterEntry.COLUMN_TIMESTAMP,
            ClusterEntry.COLUMN_FIREBASE_CLUSTER_KEY
    };
}
