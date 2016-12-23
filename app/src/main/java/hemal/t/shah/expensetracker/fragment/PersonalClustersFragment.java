package hemal.t.shah.expensetracker.fragment;

import android.content.Context;
import android.content.Intent;
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
import hemal.t.shah.expensetracker.ExpensesActivity;
import hemal.t.shah.expensetracker.R;
import hemal.t.shah.expensetracker.adapters.PersonalClusterAdapter;
import hemal.t.shah.expensetracker.data.ClusterDispenser;
import hemal.t.shah.expensetracker.data.ExpenseContract;
import hemal.t.shah.expensetracker.interfaces.OnCluster;
import hemal.t.shah.expensetracker.utils.SharedConstants;

/**
 * Fragment showing clusters which are personal.
 * Created by hemal on 13/12/16.
 */
public class PersonalClustersFragment extends Fragment implements
        LoaderManager.LoaderCallbacks<Cursor>, OnCluster {

    private static final String TAG = "PersonalExpenseFragment";

    @BindView(R.id.rv_activity_personal_clusters)
    RecyclerView recyclerView;

    Context context;

    PersonalClusterAdapter personalClusterAdapter;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

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

        personalClusterAdapter = new PersonalClusterAdapter(context, null, this);

        recyclerView.setAdapter(personalClusterAdapter);

        getActivity().getSupportLoaderManager().initLoader(
                SharedConstants.CURSOR_PERSONAL, null, this
        );

        return baseView;
    }

    @Override
    public void onLoadFinished(Loader loader, Cursor data) {
        personalClusterAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader loader) {
        personalClusterAdapter.swapCursor(null);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {


        switch (id) {
            case SharedConstants.CURSOR_PERSONAL:

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

            default:
                return null;
        }

    }

    @Override
    public void onDelete(int is_shared, String title) {
        ClusterDispenser dispenser = new ClusterDispenser(context.getContentResolver(), context);
        dispenser.startDelete(
                SharedConstants.TOKEN_DELETE_CLUSTER,
                null, ExpenseContract.ClusterEntry.CONTENT_URI,
                ExpenseContract.ClusterEntry.COLUMN_TITLE + " = ? AND " + ExpenseContract
                        .ClusterEntry.COLUMN_IS_SHARED + " = " + is_shared,
                new String[]{title}
        );
    }

    @Override
    public void onTouch(String title) {
        Intent intent = new Intent(context, ExpensesActivity.class);
        intent.putExtra("is_shared", 0);
        intent.putExtra("title", title);
        startActivity(intent);
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_personal_clusters, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.sort_1:
                Toast.makeText(context, "sort 1", Toast.LENGTH_SHORT).show();
                return true;

            case R.id.sort_2:
                Toast.makeText(context, "sort 2", Toast.LENGTH_SHORT).show();
                return true;

            case R.id.sort_3:
                Toast.makeText(context, "sort 3", Toast.LENGTH_SHORT).show();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
