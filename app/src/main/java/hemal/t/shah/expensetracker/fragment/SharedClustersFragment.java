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
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import butterknife.BindView;
import butterknife.ButterKnife;
import hemal.t.shah.expensetracker.R;
import hemal.t.shah.expensetracker.adapters.SharedClusterAdapter;
import hemal.t.shah.expensetracker.data.ClusterDispenser;
import hemal.t.shah.expensetracker.data.ExpenseContract;
import hemal.t.shah.expensetracker.interfaces.OnCluster;
import hemal.t.shah.expensetracker.utils.SharedConstants;

/**
 * Displays the clusters which are shared between users.
 * Created by hemal on 17/12/16.
 */
public class SharedClustersFragment extends Fragment implements
        LoaderManager.LoaderCallbacks<Cursor>, OnCluster {

    @BindView(R.id.rv_activity_shared_clusters)
    RecyclerView recyclerView;

    SharedClusterAdapter adapter = null;

    Context context = null;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        View baseView = inflater.inflate(R.layout.activity_shared_clusters, container, false);

        ButterKnife.bind(this, baseView);

        this.context = getContext();

        adapter = new SharedClusterAdapter(this.context, null, this);


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


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_shared_clusters, menu);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.sort_1_s:
                Toast.makeText(context, "sort 1 shared", Toast.LENGTH_SHORT).show();
                return true;

            case R.id.sort_2_s:
                Toast.makeText(context, "sort 2 shared", Toast.LENGTH_SHORT).show();
                return true;

            case R.id.sort_3_s:
                Toast.makeText(context, "sort 3 shared", Toast.LENGTH_SHORT).show();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onDelete(int is_shared, String title) {
        ClusterDispenser dispenser = new ClusterDispenser(this.context.getContentResolver(),
                this.context);

        // TODO: 23/12/16 this can delete multiple shared fragments, take care! Later introduce
        // firebase id.

        dispenser.startDelete(SharedConstants.TOKEN_DELETE_CLUSTER,
                null,
                ExpenseContract.ClusterEntry.CONTENT_URI,
                ExpenseContract.ClusterEntry.COLUMN_TITLE + "= ? AND " + ExpenseContract
                        .ClusterEntry.COLUMN_IS_SHARED + " = " + is_shared,
                new String[]{title});
    }

    @Override
    public void onTouch(String title) {
        Toast.makeText(this.context, "clicked!" + title, Toast.LENGTH_SHORT).show();
    }
}
