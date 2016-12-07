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

    public static final class ExpenseEntry implements BaseColumns{
        public static final Uri CONTENT_URI = BASE_URI.buildUpon()
                .appendPath(PATH_EXPENSE)
                .build();

        public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/"
                + CONTENT_AUTHORITY + "/" + PATH_EXPENSE;
        public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/"
                + CONTENT_AUTHORITY + "/" + PATH_EXPENSE;

        public static final String TABLE_NAME = "expense_table";
        public static final String COLUMN_ABOUT = "about"; //varchar
        public static final String COLUMN_AMOUNT = "amount"; //int

        public static Uri buildExpenseUri(long _id){
            return ContentUris.withAppendedId(CONTENT_URI, _id);
        }
    }
}
