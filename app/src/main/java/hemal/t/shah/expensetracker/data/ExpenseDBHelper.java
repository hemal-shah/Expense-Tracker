package hemal.t.shah.expensetracker.data;


import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import hemal.t.shah.expensetracker.pojo.UserDetailsParcelable;

/**
 * Create, Update or Delete tables from this class.
 */
public class ExpenseDBHelper extends SQLiteOpenHelper {

    private static final String TAG = "ExpenseDBHelper";
    private static final int DATABASE_VERSION = 2;
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

        String userQuery = "INSERT INTO " + ExpenseContract.UserDetailsEntry.TABLE_NAME
                + " (name, email, user_id) VALUES(\'hemal\',\'hemal.shah1996\', 102)";

        db.execSQL(userQuery);


        /**
         * CREATE TABLE clusters(
         * _id INTEGER PRIMARY KEY ,
         * title TEXT NOT NULL,
         * sum REAL,
         * users_list TEXT NOT NULL,
         * is_shared INTEGER NOT NULL,
         * timestamp TEXT);
         */

        String sql_create_cluster_table = "CREATE TABLE " + ExpenseContract.ClusterEntry.TABLE_NAME
                + "(" + ExpenseContract.ClusterEntry._ID + " INTEGER PRIMARY KEY , "
                + ExpenseContract.ClusterEntry.COLUMN_TITLE + " TEXT NOT NULL, "
                + ExpenseContract.ClusterEntry.COLUMN_SUM + " REAL, "
                + ExpenseContract.ClusterEntry.COLUMN_USERS_LIST + " TEXT NOT NULL, "
                + ExpenseContract.ClusterEntry.COLUMN_IS_SHARED + " INTEGER NOT NULL, "
                + ExpenseContract.ClusterEntry.COLUMN_TIMESTAMP + " TEXT);";

        Log.i(TAG, "onCreate: cluster table: " + sql_create_cluster_table);
        db.execSQL(sql_create_cluster_table);


        /**
         * CREATE TABLE expense_table(
         * _id INTEGER PRIMARY KEY,
         * about TEXT NOT NULL,
         * amount REAL NOT NULL,
         * timestamp TEXT,
         * by_user INTEGER NOT NULL,
         * cluster_id INTEGER,
         * FOREIGN KEY(by_user) REFERENCES user_details(_id),
         * FOREIGN KEY(cluster_id) REFERENCES clusters(_id) );
         */

        String sql_create_expenses_table = "CREATE TABLE " + ExpenseContract.ExpenseEntry.TABLE_NAME
                + "(" + ExpenseContract.ExpenseEntry._ID + " INTEGER PRIMARY KEY , "
                + ExpenseContract.ExpenseEntry.COLUMN_ABOUT + " TEXT NOT NULL, "
                + ExpenseContract.ExpenseEntry.COLUMN_AMOUNT + " REAL NOT NULL, "
                + ExpenseContract.ExpenseEntry.COLUMN_TIMESTAMP + " TEXT, "
                + ExpenseContract.ExpenseEntry.COLUMN_FOREIGN_BY_USER + " INTEGER NOT NULL, "
                + ExpenseContract.ExpenseEntry.COLUMN_FOREIGN_CLUSTER_ID + " INTEGER, "
                + "FOREIGN KEY(" + ExpenseContract.ExpenseEntry.COLUMN_FOREIGN_BY_USER + ") "
                + "REFERENCES " + ExpenseContract.UserDetailsEntry.TABLE_NAME + "("
                + ExpenseContract.UserDetailsEntry._ID + "),"
                + " FOREIGN KEY(" + ExpenseContract.ExpenseEntry.COLUMN_FOREIGN_CLUSTER_ID + ") "
                + "REFERENCES " + ExpenseContract.ClusterEntry.TABLE_NAME + "("
                + ExpenseContract.ClusterEntry._ID + ") );";

        Log.i(TAG, "onCreate: expenses table: " + sql_create_expenses_table);
        db.execSQL(sql_create_expenses_table);

        //todo delete the sample insertions
        for (int i = 0; i < 20; i++) {
            String title = "title " + i;
            String sql_cluster = "insert into " + ExpenseContract.ClusterEntry.TABLE_NAME
                    + "(title, sum, is_shared, timestamp, users_list)"
                    + " values(\"" + title + "\", 100, " + (i % 2)
                    + " , \"time is now\", \"{\'102\'}\")";
            db.execSQL(sql_cluster);
//            Log.i(TAG, "onCreate: inserted 1 row into cluster table");

            double amount = 102.36 * i;

            for (int k = 0; k < 5; k++) {
                String hemal = "hemal " + k;
                String sql_expenses = "insert into " + ExpenseContract.ExpenseEntry.TABLE_NAME
                        + "(about, amount, timestamp, cluster_id, by_user) VALUES (\" " + hemal
                        + "\","
                        + amount
                        + ", \"time "
                        + "is now\",  "
                        + (i + 1) + ", 102);";
                db.execSQL(sql_expenses);
//            Log.i(TAG, "onCreate: one row inserted into expenses table");
            }


        }

    }


    public UserDetailsParcelable getUserDetails(int user_id) {

        Cursor cursor = getReadableDatabase().query(ExpenseContract.UserDetailsEntry.TABLE_NAME,
                null,
                ExpenseContract.UserDetailsEntry.COLUMN_USER_ID + "=?",
                new String[]{String.valueOf(user_id)},
                null,
                null,
                null);
        UserDetailsParcelable parcel = null;

        if (cursor.moveToFirst()) {
            parcel = new UserDetailsParcelable(cursor.getString(
                    cursor.getColumnIndex(ExpenseContract.UserDetailsEntry.COLUMN_NAME)),
                    cursor.getString(
                            cursor.getColumnIndex(ExpenseContract.UserDetailsEntry.COLUMN_EMAIL)),
                    cursor.getInt(cursor.getColumnIndex(ExpenseContract.UserDetailsEntry._ID))
            );
        }
        return parcel;
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        //TODO Don't push to production.

        db.execSQL("DROP TABLE IF EXISTS " + ExpenseContract.UserDetailsEntry.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + ExpenseContract.ClusterEntry.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + ExpenseContract.ExpenseEntry.TABLE_NAME);
        onCreate(db);
    }
}
