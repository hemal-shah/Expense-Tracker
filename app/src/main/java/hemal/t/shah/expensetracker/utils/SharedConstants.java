package hemal.t.shah.expensetracker.utils;

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

    //To load shared clusters for recyclerView
    public static final int CURSOR_SHARED = 103;

    //to delete existing clusters.
    public static final int TOKEN_DELETE_CLUSTER = 104;

    //to delete expenses...
    public static final int TOKEN_DELETE_EXPENSES = 108;

    //to load expenses from personal clusters.
    public static final int CURSOR_EXPENSES_PERSONAL = 105;

    //to load expenses from shared clusters.
    public static final int CURSOR_EXPENSES_SHARED = 106;

    public static final int TOKEN_ADD_NEW_EXPENSE = 107;

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
    public static final String FIREBASE_BY_USER = "by_user";
    public static final String FIREBASE_EMAIL = "email";
    public static final String FIREBASE_PROFILE_URL = "profile_url";
    public static final String FIREBASE_TIME_STAMP = "timestamp";
}
