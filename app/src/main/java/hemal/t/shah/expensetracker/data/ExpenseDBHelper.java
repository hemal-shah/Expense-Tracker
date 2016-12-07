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
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "ExpenseDB.db";

    public ExpenseDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        StringBuilder sb = new StringBuilder();
        sb.append("CREATE TABLE ")
                .append(ExpenseContract.ExpenseEntry.TABLE_NAME)
                .append("(")
                .append(ExpenseContract.ExpenseEntry._ID).append(
                " INTEGER PRIMARY KEY AUTOINCREMENT,")
                .append(ExpenseContract.ExpenseEntry.COLUMN_ABOUT).append(" TEXT NOT NULL,")
                .append(ExpenseContract.ExpenseEntry.COLUMN_AMOUNT).append(" REAL NOT NULL);");

        db.execSQL(sb.toString());

        for (int i = 0; i < 10; i++) {
            Log.i(TAG, "onCreate: values inserted! ");
            String addDataForFirstTime = "INSERT INTO " + ExpenseContract.ExpenseEntry.TABLE_NAME +
                    "("+ ExpenseContract.ExpenseEntry.COLUMN_ABOUT+ "," + ExpenseContract
                    .ExpenseEntry.COLUMN_AMOUNT + ")" +
                    " VALUES (\"hemal\"," + (i * 10) + ")";
            db.execSQL(addDataForFirstTime);
        }


    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + ExpenseContract.ExpenseEntry.TABLE_NAME);
        onCreate(db);
    }
}
