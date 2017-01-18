package hemal.t.shah.expensetracker.data;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import hemal.t.shah.expensetracker.data.ExpenseContract.ClusterEntry;
import hemal.t.shah.expensetracker.data.ExpenseContract.ExpenseEntry;

@SuppressWarnings("ConstantConditions")
public class ExpenseProvider extends ContentProvider {

    //declaring constants for matchUri function.
    static final int CLUSTER = 102, EXPENSE = 103;
    private static final String TAG = "ExpenseProvider";
    //queryBuilder object to query clusters
    private static SQLiteQueryBuilder clustersQueryBuilder = new SQLiteQueryBuilder();
    //queryBuilder to know expenses in a cluster
    private static SQLiteQueryBuilder expensesFromClusterQueryBuilder = new SQLiteQueryBuilder();
    private static UriMatcher matcher = buildUriMatcher();

    static {

        //setting normal query to invoke the clusters table.
        clustersQueryBuilder.setTables(ExpenseContract.ClusterEntry.TABLE_NAME);
        expensesFromClusterQueryBuilder.setTables(ExpenseContract.ExpenseEntry.TABLE_NAME);

    }

    private ExpenseDBHelper dbHelper;

    private static UriMatcher buildUriMatcher() {
        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        matcher.addURI(ExpenseContract.CONTENT_AUTHORITY, ExpenseContract.PATH_EXPENSE, EXPENSE);
        matcher.addURI(ExpenseContract.CONTENT_AUTHORITY, ExpenseContract.PATH_CLUSTER, CLUSTER);

        return matcher;
    }

    @Override
    public boolean onCreate() {
        dbHelper = new ExpenseDBHelper(getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, String[] projection, String selection,
            String[] selectionArgs,
            String sortOrder) {

        Cursor cursor;
        switch (matcher.match(uri)) {
            case EXPENSE:

                cursor = expensesFromClusterQueryBuilder.query(dbHelper.getReadableDatabase(),
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            case CLUSTER:
                cursor = clustersQueryBuilder.query(dbHelper.getReadableDatabase(),
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;
            default:
                throw new UnsupportedOperationException("Check query, it's not operational");
        }
        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        switch (matcher.match(uri)) {
            case CLUSTER:
                return ExpenseContract.ClusterEntry.CONTENT_TYPE;
            case EXPENSE:
                return ExpenseContract.ExpenseEntry.CONTENT_TYPE;
            default:
                throw new UnsupportedOperationException("Unknown Uri: " + uri);
        }
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, ContentValues values) {
        //get the writable database
        final SQLiteDatabase db = dbHelper.getWritableDatabase();

        Uri updatedUri = null;
        long _id;
        switch (matcher.match(uri)) {

            case CLUSTER:
                _id = db.insert(ExpenseContract.ClusterEntry.TABLE_NAME,
                        null,
                        values);
                if (_id > 0) {
                    updatedUri = ExpenseContract.ClusterEntry.buildClusterUri(_id);
                }
                break;
            case EXPENSE:
                _id = db.insert(ExpenseContract.ExpenseEntry.TABLE_NAME,
                        null,
                        values);
                if (_id > 0) {
                    updatedUri = ExpenseContract.ExpenseEntry.buildExpenseUri(_id);
                }
                break;
            default:
                throw new UnsupportedOperationException("No such Uri: " + uri);
        }

        getContext().getContentResolver().notifyChange(updatedUri, null);

        return updatedUri;
    }

    @Override
    public int delete(@NonNull Uri uri, String selection, String[] selectionArgs) {

        final SQLiteDatabase db = dbHelper.getReadableDatabase();
        int rows_deleted;

        //Delete everything if selection is not passed.
        if (selection == null) {
            selection = "1";
        }

        switch (matcher.match(uri)) {

            case CLUSTER:
                rows_deleted = db.delete(
                        ClusterEntry.TABLE_NAME,
                        selection,
                        selectionArgs
                );
                break;
            case EXPENSE:
                rows_deleted = db.delete(
                        ExpenseEntry.TABLE_NAME,
                        selection,
                        selectionArgs
                );
                break;
            default:
                throw new UnsupportedOperationException("Check query, not operational!");
        }

        if (rows_deleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return rows_deleted;
    }

    @Override
    public int update(@NonNull Uri uri, ContentValues values, String selection,
            String[] selectionArgs) {
        return 0;
    }

    @Override
    public void shutdown() {
        dbHelper.close();
        super.shutdown();
    }
}
