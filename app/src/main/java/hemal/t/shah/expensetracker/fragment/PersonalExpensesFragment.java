package hemal.t.shah.expensetracker.fragment;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import butterknife.BindView;
import butterknife.ButterKnife;
import hemal.t.shah.expensetracker.R;
import hemal.t.shah.expensetracker.adapters.ClusterAdapter;
import hemal.t.shah.expensetracker.data.ExpenseContract;

/**
 * Fragment showing clusters which are personal.
 * Created by hemal on 13/12/16.
 */
public class PersonalExpensesFragment extends Fragment implements
        LoaderManager.LoaderCallbacks<Cursor> {

    private static final String TAG = "PersonalExpenseFragment";

    @BindView(R.id.rv_activity_personal_clusters)
    RecyclerView recyclerView;

    @BindView(R.id.fab_activity_personal_clusters)
    FloatingActionButton fabAddNew;

    private static final int CURSOR_PERSONAL = 101; //id for cursor loader

    Context context;

    ClusterAdapter clusterAdapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        View baseView = inflater.inflate(R.layout.activity_personal_clusters, container, false);
        ButterKnife.bind(this, baseView);

        context = getContext();

        recyclerView.setLayoutManager(new LinearLayoutManager(
                context,
                LinearLayoutManager.VERTICAL,
                false
        ));

        recyclerView.hasFixedSize();

        clusterAdapter = new ClusterAdapter(context, null);

        recyclerView.setAdapter(clusterAdapter);

        getActivity().getSupportLoaderManager().initLoader(
                CURSOR_PERSONAL,
                null,
                this
        );

        return baseView;
    }

    @Override
    public void onLoadFinished(Loader loader, Cursor data) {
        clusterAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader loader) {
        clusterAdapter.swapCursor(null);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {

        String[] projection = {
                ExpenseContract.ClusterEntry._ID,
                ExpenseContract.ClusterEntry.COLUMN_TITLE,
                ExpenseContract.ClusterEntry.COLUMN_SUM,
                ExpenseContract.ClusterEntry.COLUMN_TIMESTAMP};

        String selection = ExpenseContract.ClusterEntry.COLUMN_IS_SHARED + " = ?";
        String[] selectionArgs = {"0"};
        return new CursorLoader(
                context,
                ExpenseContract.ClusterEntry.CONTENT_URI,
                projection,
                selection,
                selectionArgs,
                null
        );

        /*String sortOrder = ExpenseContract.ExpenseEntry.COLUMN_AMOUNT + " DESC";

        return new CursorLoader(context,
                ExpenseContract.ExpenseEntry.CONTENT_URI,
                new String[]{ExpenseContract.ExpenseEntry.TABLE_NAME + "."
                        + ExpenseContract.ExpenseEntry._ID,
                        ExpenseContract.ClusterEntry.TABLE_NAME + "."
                                + ExpenseContract.ClusterEntry._ID,
                        ExpenseContract.ExpenseEntry.COLUMN_ABOUT,
                        ExpenseContract.ExpenseEntry.COLUMN_AMOUNT},
                null,
                null,
                sortOrder
        );*/
    }
}
