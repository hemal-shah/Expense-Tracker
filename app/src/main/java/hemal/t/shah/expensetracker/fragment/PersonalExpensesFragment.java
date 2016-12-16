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
import hemal.t.shah.expensetracker.adapters.ExpenseAdapter;
import hemal.t.shah.expensetracker.data.ExpenseContract;

/**
 * Fragment showing clusters which are personal.
 * Created by hemal on 13/12/16.
 */
public class PersonalExpensesFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    @BindView(R.id.rv_activity_personal_clusters)
    RecyclerView recyclerView;

    private static final int CURSOR_PERSONAL = 101; //id for cursor loader

    Context context;

    ExpenseAdapter expenseAdapter;

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

        expenseAdapter = new ExpenseAdapter(context, null);

        recyclerView.setAdapter(expenseAdapter);

        getActivity().getSupportLoaderManager().initLoader(
                CURSOR_PERSONAL,
                null,
                this
        );

        return baseView;
    }

    @Override
    public Loader onCreateLoader(int id, Bundle args) {

        String sortOrder = ExpenseContract.ExpenseEntry.COLUMN_AMOUNT + " DESC";

        return new CursorLoader(
                context,
                ExpenseContract.ExpenseEntry.CONTENT_URI,
                new String[]{ExpenseContract.ExpenseEntry.TABLE_NAME + "."
                        + ExpenseContract.ExpenseEntry._ID,
                        ExpenseContract.ExpenseEntry.COLUMN_ABOUT,
                        ExpenseContract.ExpenseEntry.COLUMN_AMOUNT},
                null,
                null,
                sortOrder
        );
    }

    @Override
    public void onLoadFinished(Loader loader, Cursor data) {
        expenseAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader loader) {
        expenseAdapter.swapCursor(null);
    }
}
