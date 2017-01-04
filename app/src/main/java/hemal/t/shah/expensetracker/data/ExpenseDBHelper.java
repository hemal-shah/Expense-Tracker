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

    private static void dummyInserts(SQLiteDatabase db) {
        //todo delete the sample insertions
        for (int i = 0; i < 10; i++) {
            long time = System.currentTimeMillis();
            String title = "title " + i;
            String sql_cluster = "insert into "
                + ExpenseContract.ClusterEntry.TABLE_NAME
                + "(title, is_shared, timestamp, users_list)"
                + " values(\""
                + title
                + "\", "
                + (i % 2)
                + " , "
                + time
                + ", \"{\'102\'}\")";
            db.execSQL(sql_cluster);
            Log.i(TAG, "onCreate: inserted 1 row into cluster table");

            double amount = 102.36 * i;

            for (int k = 0; k < 2; k++) {
                long TIME = System.currentTimeMillis();
                String hemal = "hemal " + k;
                String sql_expenses = "insert into "
                    + ExpenseContract.ExpenseEntry.TABLE_NAME
                    + "(about, amount, timestamp, cluster_id, by_user) VALUES (\" "
                    + hemal
                    + "\","
                    + amount
                    + ", "
                    + TIME
                    + ",  "
                    + (i + 1)
                    + ", 102);";
                db.execSQL(sql_expenses);
                Log.i(TAG, "onCreate: one row inserted into expenses table");
            }
        }
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        /**
         * CREATE TABLE clusters(
         * _id INTEGER PRIMARY KEY ,
         * title TEXT NOT NULL,
         * users_list TEXT NOT NULL,
         * is_shared INTEGER NOT NULL,
         * timestamp TEXT);
         */

        String sql_create_cluster_table = "CREATE TABLE " + ExpenseContract.ClusterEntry.TABLE_NAME
                + "(" + ExpenseContract.ClusterEntry._ID + " INTEGER PRIMARY KEY , "
                + ExpenseContract.ClusterEntry.COLUMN_TITLE + " TEXT NOT NULL, "
                + ExpenseContract.ClusterEntry.COLUMN_USERS_LIST + " TEXT NOT NULL, "
                + ExpenseContract.ClusterEntry.COLUMN_IS_SHARED + " INTEGER NOT NULL, "
            + ExpenseContract.ClusterEntry.COLUMN_TIMESTAMP
            + " LONG);";

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
         * FOREIGN KEY(cluster_id) REFERENCES clusters(_id) );
         */

        String sql_create_expenses_table = "CREATE TABLE " + ExpenseContract.ExpenseEntry.TABLE_NAME
                + "(" + ExpenseContract.ExpenseEntry._ID + " INTEGER PRIMARY KEY , "
                + ExpenseContract.ExpenseEntry.COLUMN_ABOUT + " TEXT NOT NULL, "
                + ExpenseContract.ExpenseEntry.COLUMN_AMOUNT + " REAL NOT NULL, "
            + ExpenseContract.ExpenseEntry.COLUMN_TIMESTAMP
            + " LONG, "
                + ExpenseContract.ExpenseEntry.COLUMN_BY_USER + " INTEGER NOT NULL, "
                + ExpenseContract.ExpenseEntry.COLUMN_FOREIGN_CLUSTER_ID + " INTEGER, "
                + " FOREIGN KEY(" + ExpenseContract.ExpenseEntry.COLUMN_FOREIGN_CLUSTER_ID + ") "
                + "REFERENCES " + ExpenseContract.ClusterEntry.TABLE_NAME + "("
                + ExpenseContract.ClusterEntry._ID + ") );";

        Log.i(TAG, "onCreate: expenses table: " + sql_create_expenses_table);
        db.execSQL(sql_create_expenses_table);

        dummyInserts(db);


    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        //TODO Don't push to production.
        db.execSQL("DROP TABLE IF EXISTS " + ExpenseContract.ClusterEntry.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + ExpenseContract.ExpenseEntry.TABLE_NAME);
        onCreate(db);
    }
}
