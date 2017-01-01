package hemal.t.shah.expensetracker.data;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Contract describing the all tables in database, and columns contained in them.
 */
public class ExpenseContract {

    //The authority for ContentProvider
    public static final String CONTENT_AUTHORITY = "hemal.t.shah.expensetracker";

    public static final Uri BASE_URI = Uri.parse("content://" + CONTENT_AUTHORITY);


    public static final String PATH_EXPENSE = "expenses";
    public static final String PATH_CLUSTER = "clusters";

    public static final class ExpenseEntry implements BaseColumns{
        public static final Uri CONTENT_URI = BASE_URI.buildUpon()
                .appendPath(PATH_EXPENSE)
                .build();

        public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/"
                + CONTENT_AUTHORITY + "/" + PATH_EXPENSE;
        public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/"
                + CONTENT_AUTHORITY + "/" + PATH_EXPENSE;

        //table name
        public static final String TABLE_NAME = "expense_table";

        //regarding what aspect expense was made.
        public static final String COLUMN_ABOUT = "about"; //varchar

        //the amount paid
        public static final String COLUMN_AMOUNT = "amount"; //int

        //timestamp when event occured.
        public static final String COLUMN_TIMESTAMP = "timestamp";

        //indicating which user made the entry...
        public static final String COLUMN_BY_USER = "by_user";

        //reference to the cluster it belongs to
        public static final String COLUMN_FOREIGN_CLUSTER_ID = "cluster_id";

        public static Uri buildExpenseUri(long _id){
            return ContentUris.withAppendedId(CONTENT_URI, _id);
        }
    }

    /**
     * Clusters denote both shared and personal clusters.
     * User array would be stored for shared clusters.
     * is_shared(integer) would be 1 if shared cluster, else 0.
     */
    public static final class ClusterEntry implements BaseColumns {
        public static final Uri CONTENT_URI = BASE_URI.buildUpon()
                .appendPath(PATH_CLUSTER)
                .build();


        public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/"
                + CONTENT_AUTHORITY + "/" + PATH_CLUSTER;

        public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/"
                + CONTENT_AUTHORITY + "/" + PATH_CLUSTER;


        //name of the table
        public static final String TABLE_NAME = "clusters";

        //title of the cluster. Type: String
        public static final String COLUMN_TITLE = "title";

        //list of all users included in this table.
        public static final String COLUMN_USERS_LIST = "users_list";


        //0 = personal; 1 = shared
        public static final String COLUMN_IS_SHARED = "is_shared";

        //timestamp on when the cluster was created.
        public static final String COLUMN_TIMESTAMP = "timestamp";

        public static Uri buildClusterUri(long _id) {
            return ContentUris.withAppendedId(CONTENT_URI, _id);
        }
    }

}
