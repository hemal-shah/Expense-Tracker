package hemal.t.shah.expensetracker.utils;

/**
 * Simple utility class that stores all necessary constants.
 * Created by hemal on 17/12/16.
 */
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

    //to load expenses from personal clusters.
    public static final int CURSOR_EXPENSES_PERSONAL = 105;

    //to load expenses from shared clusters.
    public static final int CURSOR_EXPENSES_SHARED = 106;

    public static final int TOKEN_ADD_NEW_EXPENSE = 107;

    public static final String SHARE_CLUSTER_PARCEL = "parcel";
}
