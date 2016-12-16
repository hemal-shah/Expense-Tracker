package hemal.t.shah.expensetracker.data;


import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Create, Update or Delete tables from this class.
 */
public class ExpenseDBHelper extends SQLiteOpenHelper {

    private static final String TAG = "ExpenseDBHelper";
    private static final int DATABASE_VERSION = 6;
    private static final String DATABASE_NAME = "ExpenseDB.db";

    public ExpenseDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        /**
         * CREATE TABLE user_details(
         * _id INTEGER PRIMARY KEY ,
         * name TEXT NOT NULL,
         * email TEXT NOT NULL,
         * user_id TEXT UNIQUE NOT NULL);
         */
        String sql_create_user_table = "CREATE TABLE " + ExpenseContract.UserDetailsEntry.TABLE_NAME
                + "(" + ExpenseContract.UserDetailsEntry._ID
                + " INTEGER PRIMARY KEY , "
                + ExpenseContract.UserDetailsEntry.COLUMN_NAME + " TEXT NOT NULL, "
                + ExpenseContract.UserDetailsEntry.COLUMN_EMAIL + " TEXT NOT NULL, "
                + ExpenseContract.UserDetailsEntry.COLUMN_USER_ID + " TEXT UNIQUE NOT NULL);";

//        Log.i(TAG, "onCreate: user table: " + sql_create_user_table);
        db.execSQL(sql_create_user_table);


        /**
         * CREATE TABLE clusters(
         * _id INTEGER PRIMARY KEY ,
         * title TEXT UNIQUE NOT NULL,
         * sum REAL,
         * is_shared INTEGER NOT NULL,
         * timestamp TEXT);
         */
        String sql_create_cluster_table = "CREATE TABLE " + ExpenseContract.ClusterEntry.TABLE_NAME
                + "(" + ExpenseContract.ClusterEntry._ID + " INTEGER PRIMARY KEY , "
                + ExpenseContract.ClusterEntry.COLUMN_TITLE + " TEXT UNIQUE NOT NULL, "
                + ExpenseContract.ClusterEntry.COLUMN_SUM + " REAL, "
                + ExpenseContract.ClusterEntry.COLUMN_IS_SHARED + " INTEGER NOT NULL, "
                + ExpenseContract.ClusterEntry.COLUMN_TIMESTAMP + " TEXT);";

//        Log.i(TAG, "onCreate: cluster table: " + sql_create_cluster_table);
        db.execSQL(sql_create_cluster_table);

        for (int i = 0; i < 20; i++) {
            String title = "title " + i;
            String sql_cluster = "insert into " + ExpenseContract.ClusterEntry.TABLE_NAME
                    + "(title, sum, is_shared, timestamp)"
                    + " values(\"" + title + "\", 100, 0 , \"time is now\")";
            db.execSQL(sql_cluster);
            Log.i(TAG, "onCreate: inserted 1 row into cluster table");
        }


        /**
         * CREATE TABLE expense_table(
         * _id INTEGER PRIMARY KEY ,
         * about TEXT NOT NULL, amount REAL NOT NULL,
         * timestamp TEXT, cluster_id INTEGER,
         * FOREIGN KEY(cluster_id) REFERENCES clusters(_id) );
         */
        String sql_create_expenses_table = "CREATE TABLE " + ExpenseContract.ExpenseEntry.TABLE_NAME
                + "(" + ExpenseContract.ExpenseEntry._ID + " INTEGER PRIMARY KEY , "
                + ExpenseContract.ExpenseEntry.COLUMN_ABOUT + " TEXT NOT NULL, "
                + ExpenseContract.ExpenseEntry.COLUMN_AMOUNT + " REAL NOT NULL, "
                + ExpenseContract.ExpenseEntry.COLUMN_TIMESTAMP + " TEXT, "
                + ExpenseContract.ExpenseEntry.COLUMN_FOREIGN_CLUSTER_ID + " INTEGER, "
                + " FOREIGN KEY(" + ExpenseContract.ExpenseEntry.COLUMN_FOREIGN_CLUSTER_ID + ") "
                + "REFERENCES " + ExpenseContract.ClusterEntry.TABLE_NAME + "("
                + ExpenseContract.ClusterEntry._ID + ") );";

//        Log.i(TAG, "onCreate: expenses table: " + sql_create_expenses_table);
        db.execSQL(sql_create_expenses_table);

        /**
         * CREATE TABLE cluster_users(
         * _id INTEGER PRIMARY KEY ,
         * cluster_id INTEGER NOT NULL,
         * user_id INTEGER NOT NULL,
         * FOREIGN KEY(cluster_id) REFERENCES clusters(_id),
         * FOREIGN KEY(user_id) REFERENCES user_details(_id) );
         */
        String sql_create_cluster_users_table =
                "CREATE TABLE " + ExpenseContract.ClusterUserEntry.TABLE_NAME
                        + "(" + ExpenseContract.ClusterUserEntry._ID
                        + " INTEGER PRIMARY KEY , "
                        + ExpenseContract.ClusterUserEntry.COLUMN_FOREIGN_CLUSTER_ID
                        + " INTEGER NOT NULL, "
                        + ExpenseContract.ClusterUserEntry.COLUMN_FOREIGN_USER_ID
                        + " INTEGER NOT NULL, "
                        + " FOREIGN KEY("
                        + ExpenseContract.ClusterUserEntry.COLUMN_FOREIGN_CLUSTER_ID + ")"
                        + " REFERENCES " + ExpenseContract.ClusterEntry.TABLE_NAME + "("
                        + ExpenseContract.ClusterEntry._ID + "), "
                        + " FOREIGN KEY(" + ExpenseContract.ClusterUserEntry.COLUMN_FOREIGN_USER_ID
                        + ")"
                        + " REFERENCES " + ExpenseContract.UserDetailsEntry.TABLE_NAME + "("
                        + ExpenseContract.UserDetailsEntry._ID + ") );";

//        Log.i(TAG, "onCreate: cluster user table: " + sql_create_cluster_users_table);

        db.execSQL(sql_create_cluster_users_table);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        //TODO Don't push to production.

        db.execSQL("DROP TABLE IF EXISTS " + ExpenseContract.UserDetailsEntry.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + ExpenseContract.ClusterEntry.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + ExpenseContract.ExpenseEntry.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + ExpenseContract.ClusterUserEntry.TABLE_NAME);
        onCreate(db);
    }
}
