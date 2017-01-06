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
import hemal.t.shah.expensetracker.adapters.SharedExpensesAdapter;
import hemal.t.shah.expensetracker.data.DataDispenser;
import hemal.t.shah.expensetracker.data.ExpenseContract;
import hemal.t.shah.expensetracker.data.ExpenseContract.ExpenseEntry;
import hemal.t.shah.expensetracker.interfaces.OnExpense;
import hemal.t.shah.expensetracker.pojo.ClusterParcelable;
import hemal.t.shah.expensetracker.pojo.ExpenseParcelable;
import hemal.t.shah.expensetracker.utils.SharedConstants;

/**
 * Created by hemal on 26/12/16.
 */
public class SharedExpensesFragment extends Fragment implements
        LoaderManager.LoaderCallbacks<Cursor>, OnExpense {

    private static final String TAG = "SharedExpensesFrag";

    @BindView(R.id.rv_shared_expenses_fragment)
    RecyclerView mRecyclerView;

    ClusterParcelable sharedCluster = null;

    Context mContext = null;

    SharedExpensesAdapter adapter;

    String selection = ExpenseContract.ExpenseEntry.COLUMN_FOREIGN_CLUSTER_ID + " = ?";
    String[] selectionArgs;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {

        Bundle arguments = getArguments();
        if (arguments != null) {
            sharedCluster = arguments.getParcelable(SharedConstants.SHARE_CLUSTER_PARCEL);
        }

        this.selectionArgs = new String[]{String.valueOf(sharedCluster.getOffline_id())};
        this.mContext = getContext();

        View rootView = inflater.inflate(R.layout.shared_expenses_fragment, container, false);
        ButterKnife.bind(this, rootView);

        this.mRecyclerView.setLayoutManager(new LinearLayoutManager(
                this.mContext,
                LinearLayoutManager.VERTICAL,
                false
        ));

        this.adapter = new SharedExpensesAdapter(this.mContext, null, this);

        this.mRecyclerView.setAdapter(adapter);

        this.mRecyclerView.setHasFixedSize(true);

        getActivity().getSupportLoaderManager().initLoader(
                SharedConstants.CURSOR_EXPENSES_SHARED,
                null, this
        );

        return rootView;

    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        switch (id) {
            case SharedConstants.CURSOR_EXPENSES_SHARED:
                return new CursorLoader(
                        this.mContext,
                        ExpenseContract.ExpenseEntry.CONTENT_URI,
                        null,
                        selection,
                        selectionArgs,
                        null
                );
        }
        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        this.adapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        this.adapter.swapCursor(null);
    }

    @Override
    public void delete(ExpenseParcelable expenseParcelable) {
        DataDispenser dispenser = new DataDispenser(this.mContext.getContentResolver(),
                this.mContext);
        dispenser.startDelete(
                SharedConstants.TOKEN_DELETE_EXPENSES,
                null,
                ExpenseContract.ExpenseEntry.CONTENT_URI,
                ExpenseEntry.COLUMN_FIREBASE_EXPENSE_KEY + " = ? AND "
                        + ExpenseEntry.FIREBASE_CLUSTER_KEY + "= ?",
                new String[]{expenseParcelable.getAbout(),
                        String.valueOf(expenseParcelable.getFirebase_expense_key()),
                        String.valueOf(expenseParcelable.getFirebase_cluster_ref_key())}
        );
    }
}
