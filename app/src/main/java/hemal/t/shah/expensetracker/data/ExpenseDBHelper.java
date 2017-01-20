package hemal.t.shah.expensetracker.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import hemal.t.shah.expensetracker.data.ExpenseContract.ClusterEntry;
import hemal.t.shah.expensetracker.data.ExpenseContract.ExpenseEntry;

/**
 * Create, Update or Delete tables from this class.
 */
class ExpenseDBHelper extends SQLiteOpenHelper {

    private static final String TAG = "ExpenseDBHelper";
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "ExpenseDB.db";

    ExpenseDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        /**
         * CREATE TABLE clusters(
         * _id INTEGER PRIMARY KEY ,
         * title TEXT NOT NULL,
         * firebase_cluster_key TEXT NOT NULL,
         * is_shared INTEGER NOT NULL,
         * timestamp LONG);
         */

        String sql_create_cluster_table = "CREATE TABLE "
                + ClusterEntry.TABLE_NAME
                + "(" + ClusterEntry._ID + " INTEGER PRIMARY KEY , "
                + ClusterEntry.COLUMN_TITLE + " TEXT NOT NULL, "
                + ClusterEntry.COLUMN_FIREBASE_CLUSTER_KEY + " TEXT NOT NULL, "
                + ClusterEntry.COLUMN_IS_SHARED + " INTEGER NOT NULL, "
                + ClusterEntry.COLUMN_TIMESTAMP + " LONG);";

        db.execSQL(sql_create_cluster_table);

        /**
         * CREATE TABLE expense_table(
         * _id INTEGER PRIMARY KEY ,
         * about TEXT NOT NULL,
         * amount REAL NOT NULL,
         * timestamp LONG,
         * expense_key_firebase TEXT NOT NULL,
         * by_user TEXT,
         * description TEXT,
         * firebase_cluster_key TEXT NOT NULL,
         * firebaes_uname TEXT,
         * firebase_u_email TEXT,
         * firebase_profile_photo_url TEXT);
         */
        String sql_create_expenses_table = "CREATE TABLE "
                + ExpenseContract.ExpenseEntry.TABLE_NAME
                + "(" + ExpenseEntry._ID + " INTEGER PRIMARY KEY , "
                + ExpenseEntry.COLUMN_ABOUT + " TEXT NOT NULL, "
                + ExpenseEntry.COLUMN_AMOUNT + " REAL NOT NULL, "
                + ExpenseEntry.COLUMN_TIMESTAMP + " LONG, "
                + ExpenseEntry.COLUMN_DESCRIBE + " TEXT, "
                + ExpenseEntry.COLUMN_BY_FIREBASE_USER_UID + " TEXT,"
                + ExpenseEntry.FIREBASE_CLUSTER_KEY + " TEXT NOT NULL,"
                + ExpenseEntry.COLUMN_FIREBASE_EXPENSE_KEY + " TEXT NOT NULL,"
                + ExpenseEntry.COLUMN_FIREBASE_USER_NAME + " TEXT,"
                + ExpenseEntry.COLUMN_FIREBASE_USER_EMAIL + " TEX,"
                + ExpenseEntry.COLUMN_FIREBASE_USER_URL + " TEXT);";

        db.execSQL(sql_create_expenses_table);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + ExpenseContract.ClusterEntry.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + ExpenseContract.ExpenseEntry.TABLE_NAME);
        onCreate(db);
    }
}
