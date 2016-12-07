package hemal.t.shah.expensetracker.data;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.support.annotation.Nullable;

public class ExpenseProvider extends ContentProvider {

    static final int EXPENSE = 100;
    private ExpenseDBHelper dbHelper;
    private static SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();

    static {
        queryBuilder.setTables(ExpenseContract.ExpenseEntry.TABLE_NAME);
    }

    private static UriMatcher matcher = buildUriMatcher();

    private static UriMatcher buildUriMatcher() {
        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        matcher.addURI(ExpenseContract.CONTENT_AUTHORITY,
                ExpenseContract.PATH_EXPENSE,
                EXPENSE);
        return matcher;
    }

    @Override
    public boolean onCreate() {
        dbHelper = new ExpenseDBHelper(getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs,
            String sortOrder) {

        Cursor cursor = null;
        switch (matcher.match(uri)) {
            case EXPENSE:
                cursor = queryBuilder.query(
                        dbHelper.getReadableDatabase(),
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            default:
                throw new UnsupportedOperationException("Check query, it's not operational");
        }
        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

    @Nullable
    @Override
    public String getType(Uri uri) {
        switch (matcher.match(uri)) {
            case EXPENSE:
                return ExpenseContract.ExpenseEntry.CONTENT_TYPE;
            default:
                throw new UnsupportedOperationException("Unknown Uri: " + uri);
        }
    }

    @Nullable
    @Override
    public Uri insert(Uri uri, ContentValues values) {
        final SQLiteDatabase db = dbHelper.getWritableDatabase();
        Uri updatedUri = null;
        switch (matcher.match(uri)) {
            case EXPENSE:
                long _id = db.insert(ExpenseContract.ExpenseEntry.TABLE_NAME,
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
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = dbHelper.getReadableDatabase();
        int rows_deleted;
        switch (matcher.match(uri)) {
            case EXPENSE:
                if (selection == null) {
                    selection = "1";
                }

                rows_deleted = db.delete(ExpenseContract.ExpenseEntry.TABLE_NAME,
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
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        return 0;
    }

    @Override
    public void shutdown() {
        dbHelper.close();
        super.shutdown();
    }
}
