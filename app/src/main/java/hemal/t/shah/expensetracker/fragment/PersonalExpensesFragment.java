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
import hemal.t.shah.expensetracker.adapters.PersonalExpensesAdapter;
import hemal.t.shah.expensetracker.data.ExpenseContract;
import hemal.t.shah.expensetracker.interfaces.OnExpense;
import hemal.t.shah.expensetracker.pojo.ClusterParcelable;
import hemal.t.shah.expensetracker.pojo.ExpenseParcelable;
import hemal.t.shah.expensetracker.utils.SharedConstants;

/**
 * Shows personal expenses of a particular cluster.
 * Created by hemal on 23/12/16.
 */
public class PersonalExpensesFragment extends Fragment implements
        LoaderManager.LoaderCallbacks<Cursor>, OnExpense {

    private static final String TAG = "PersonalExpensesFrag";


    @BindView(R.id.rv_personal_expenses_fragment)
    RecyclerView recyclerView;

    PersonalExpensesAdapter adapter = null;

    Context mContext;
    ClusterParcelable personalCluster;

    String selection = ExpenseContract.ExpenseEntry.TABLE_NAME + "."
            + ExpenseContract.ExpenseEntry.COLUMN_FOREIGN_CLUSTER_ID + " = ?";
    String[] selectionArgs;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {


        Bundle arguments = getArguments();
        if (arguments != null) {
            personalCluster = arguments.getParcelable(SharedConstants.SHARE_CLUSTER_PARCEL);
        }

        this.selectionArgs = new String[]{String.valueOf(personalCluster.getId())};
        this.mContext = getContext();

        View rootView = inflater.inflate(R.layout.personal_expenses_fragment, container, false);
        ButterKnife.bind(this, rootView);

        adapter = new PersonalExpensesAdapter(this.mContext, null, this);
        recyclerView.setLayoutManager(
                new LinearLayoutManager(this.mContext, LinearLayoutManager.VERTICAL, false));
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(adapter);

        getActivity().getSupportLoaderManager().initLoader(
                SharedConstants.CURSOR_EXPENSES_PERSONAL,
                null,
                this
        );

        return rootView;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {

        switch (id) {
            case SharedConstants.CURSOR_EXPENSES_PERSONAL:
                return new CursorLoader(
                        this.mContext,
                        ExpenseContract.ExpenseEntry.CONTENT_URI,
                        null,
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
    public void delete(ExpenseParcelable expenseParcelable) {

        String where = ExpenseContract.ExpenseEntry.COLUMN_ABOUT + "=? AND "
                + ExpenseContract.ExpenseEntry.COLUMN_AMOUNT + " = ? AND "
                + ExpenseContract.ExpenseEntry.COLUMN_FOREIGN_CLUSTER_ID + " = ?";

        getActivity().getContentResolver().delete(
                ExpenseContract.ExpenseEntry.CONTENT_URI,
                where,
                new String[]{expenseParcelable.getAbout(),
                        String.valueOf(expenseParcelable.getAmount()),
                        String.valueOf(expenseParcelable.getCluster_id())}
        );
    }
}
