package hemal.t.shah.expensetracker;

import android.database.Cursor;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import hemal.t.shah.expensetracker.Data.ExpenseContract;

public class MainActivity extends AppCompatActivity implements
        LoaderManager.LoaderCallbacks<Cursor> {
    private static final String TAG = "MainActivity";

    RecyclerView recyclerView;
    ExpenseAdapter adapter;
    private static final int LOADER_FOR_EXPENSE = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        recyclerView = (RecyclerView) findViewById(R.id.rv_activity_main);
        recyclerView.setLayoutManager(new LinearLayoutManager(this,
                LinearLayoutManager.VERTICAL,
                false));
        recyclerView.setHasFixedSize(true);

        adapter = new ExpenseAdapter(this, null);

        recyclerView.setAdapter(adapter);

        getSupportLoaderManager().initLoader(LOADER_FOR_EXPENSE,
                null,
                this);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String sortOrder = ExpenseContract.ExpenseEntry.COLUMN_AMOUNT + " DESC";

        Log.i(TAG, "onCreateLoader: loader is now being created");

        return new CursorLoader(
                this,
                ExpenseContract.ExpenseEntry.CONTENT_URI,
                new String[]{ExpenseContract.ExpenseEntry._ID,
                        ExpenseContract.ExpenseEntry.COLUMN_ABOUT,
                        ExpenseContract.ExpenseEntry.COLUMN_AMOUNT},
                null,
                null,
                sortOrder
        );
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (data != null) {
            Log.i(TAG, "onLoadFinished: the cursor has returned values!");
        }
        adapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        Log.i(TAG, "onLoaderReset: ");
        adapter.swapCursor(null);
    }
}
