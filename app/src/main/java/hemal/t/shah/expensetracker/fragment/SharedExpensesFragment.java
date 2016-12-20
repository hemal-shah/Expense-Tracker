package hemal.t.shah.expensetracker.fragment;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
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
import hemal.t.shah.expensetracker.adapters.SharedClusterAdapter;
import hemal.t.shah.expensetracker.data.ExpenseContract;
import hemal.t.shah.expensetracker.utils.SharedConstants;

/**
 * Displays the clusters which are shared between users.
 * Created by hemal on 17/12/16.
 */
public class SharedExpensesFragment extends Fragment implements
        LoaderManager.LoaderCallbacks<Cursor> {

    @BindView(R.id.rv_activity_shared_clusters)
    RecyclerView recyclerView;

    SharedClusterAdapter adapter = null;

    Context context = null;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        View baseView = inflater.inflate(R.layout.activity_shared_clusters, container, false);

        ButterKnife.bind(this, baseView);

        this.context = getContext();

        adapter = new SharedClusterAdapter(this.context, null);


        recyclerView.setLayoutManager(new LinearLayoutManager(
                context,
                LinearLayoutManager.VERTICAL,
                false
        ));


        recyclerView.hasFixedSize();
        recyclerView.setAdapter(adapter);

        getActivity().getSupportLoaderManager().initLoader(
                SharedConstants.CURSOR_SHARED, null, this
        );

        return baseView;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {

        switch (id) {
            case SharedConstants.CURSOR_SHARED:

                String[] projection = {
                        ExpenseContract.ClusterEntry._ID,
                        ExpenseContract.ClusterEntry.COLUMN_TITLE,
                        ExpenseContract.ClusterEntry.COLUMN_SUM,
                        ExpenseContract.ClusterEntry.COLUMN_TIMESTAMP};

                String selection = ExpenseContract.ClusterEntry.COLUMN_IS_SHARED + " = ?";
                String[] selectionArgs = {"1"};
                return new CursorLoader(
                        context,
                        ExpenseContract.ClusterEntry.CONTENT_URI,
                        projection,
                        selection,
                        selectionArgs,
                        null
                );

            default:
                return null;
        }

    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        adapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        adapter.swapCursor(null);
    }
}
