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

@SuppressWarnings("ConstantConditions")
public class ExpenseProvider extends ContentProvider {

    private static final String TAG = "ExpenseProvider";

    //declaring constants for matchUri function.
    static final int USER_DETAILS = 101, CLUSTER = 102, EXPENSE = 103, CLUSTER_USERS = 104;

    private ExpenseDBHelper dbHelper;

    //queryBuilder object to help generate sql queries.
    private static SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();

    static {

        String tables = ExpenseContract.UserDetailsEntry.TABLE_NAME + ", "
                + ExpenseContract.ClusterEntry.TABLE_NAME + ", "
                + ExpenseContract.ExpenseEntry.TABLE_NAME + ", "
                + ExpenseContract.ClusterUserEntry.TABLE_NAME;

        queryBuilder.setTables(tables);
    }

    private static UriMatcher matcher = buildUriMatcher();

    private static UriMatcher buildUriMatcher() {
        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        matcher.addURI(ExpenseContract.CONTENT_AUTHORITY, ExpenseContract.PATH_EXPENSE, EXPENSE);
        matcher.addURI(ExpenseContract.CONTENT_AUTHORITY, ExpenseContract.PATH_CLUSTER, CLUSTER);
        matcher.addURI(ExpenseContract.CONTENT_AUTHORITY, ExpenseContract.PATH_CLUSTER_USER,
                CLUSTER_USERS);
        matcher.addURI(ExpenseContract.CONTENT_AUTHORITY, ExpenseContract.PATH_USER_DETAILS,
                USER_DETAILS);
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

                cursor = queryBuilder.query(dbHelper.getReadableDatabase(),
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            case USER_DETAILS:
                cursor = queryBuilder.query(dbHelper.getReadableDatabase(),
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;
            case CLUSTER:
                cursor = queryBuilder.query(dbHelper.getReadableDatabase(),
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;
            case CLUSTER_USERS:
                cursor = queryBuilder.query(dbHelper.getReadableDatabase(),
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
            case USER_DETAILS:
                return ExpenseContract.UserDetailsEntry.CONTENT_TYPE;
            case CLUSTER:
                return ExpenseContract.ClusterEntry.CONTENT_TYPE;
            case EXPENSE:
                return ExpenseContract.ExpenseEntry.CONTENT_TYPE;
            case CLUSTER_USERS:
                return ExpenseContract.ClusterUserEntry.CONTENT_TYPE;
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

            case USER_DETAILS:
                _id = db.insert(ExpenseContract.UserDetailsEntry.TABLE_NAME,
                        null,
                        values);
                if (_id > 0) {
                    updatedUri = ExpenseContract.UserDetailsEntry.buildUserUri(_id);
                }
                break;

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
            case CLUSTER_USERS:
                _id = db.insert(ExpenseContract.ClusterUserEntry.TABLE_NAME,
                        null,
                        values);
                if (_id > 0) {
                    updatedUri = ExpenseContract.UserDetailsEntry.buildUserUri(_id);
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

            case USER_DETAILS:
                rows_deleted = db.delete(ExpenseContract.UserDetailsEntry.TABLE_NAME,
                        selection,
                        selectionArgs
                );
                break;
            case CLUSTER:
                rows_deleted = db.delete(ExpenseContract.ClusterEntry.TABLE_NAME,
                        selection,
                        selectionArgs
                );
                break;
            case EXPENSE:
                rows_deleted = db.delete(ExpenseContract.ExpenseEntry.TABLE_NAME,
                        selection,
                        selectionArgs);
                break;
            case CLUSTER_USERS:
                rows_deleted = db.delete(ExpenseContract.ClusterUserEntry.TABLE_NAME,
                        selection,
                        selectionArgs);
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
