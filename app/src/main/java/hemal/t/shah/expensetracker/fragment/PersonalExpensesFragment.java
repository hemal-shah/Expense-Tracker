package hemal.t.shah.expensetracker.fragment;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import hemal.t.shah.expensetracker.R;
import hemal.t.shah.expensetracker.data.ExpenseContract;
import hemal.t.shah.expensetracker.utils.SharedConstants;

/**
 * Shows personal expenses of a particular cluster.
 * Created by hemal on 23/12/16.
 */
public class PersonalExpensesFragment extends Fragment implements
        LoaderManager.LoaderCallbacks<Cursor> {


    @BindView(R.id.tv_personal_expenses_fragment)
    TextView mTextView;

    String title;
    Context mContext;
    int cluster_id;

    String[] projection = {
            ExpenseContract.ExpenseEntry.TABLE_NAME + "." + ExpenseContract.ExpenseEntry._ID,
            ExpenseContract.ExpenseEntry.TABLE_NAME + "."
                    + ExpenseContract.ExpenseEntry.COLUMN_ABOUT,
            ExpenseContract.ExpenseEntry.TABLE_NAME + "."
                    + ExpenseContract.ExpenseEntry.COLUMN_AMOUNT,
            ExpenseContract.ExpenseEntry.TABLE_NAME + "."
                    + ExpenseContract.ExpenseEntry.COLUMN_TIMESTAMP,
            ExpenseContract.ExpenseEntry.TABLE_NAME + "."
                    + ExpenseContract.ExpenseEntry.COLUMN_FOREIGN_BY_USER
    };

    String selection = ExpenseContract.ExpenseEntry.COLUMN_FOREIGN_CLUSTER_ID + " = ?";
    String[] selectionArgs;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {


        Bundle arguments = getArguments();
        if (arguments != null) {
            title = arguments.getString(SharedConstants.SHARE_TITLE);
            cluster_id = arguments.getInt(SharedConstants.SHARE_CLUSTER_ID);
        }

        this.selectionArgs = new String[]{String.valueOf(cluster_id)};
        this.mContext = getContext();

        View rootView = inflater.inflate(R.layout.personal_expenses_fragment, container, false);
        ButterKnife.bind(this, rootView);

        getActivity().getSupportLoaderManager().initLoader(
                SharedConstants.CURSOR_EXPENSES_PERSONAL,
                null,
                this
        );

        mTextView.setText(title);
        return rootView;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {

        switch (id) {
            case SharedConstants.CURSOR_EXPENSES_PERSONAL:
                return new CursorLoader(
                        this.mContext,
                        ExpenseContract.ExpenseEntry.CONTENT_URI,
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
        String output = mTextView.getText().toString();
        data.moveToFirst();
        do {
            output += "\n";
            output += data.getString(
                    data.getColumnIndex(ExpenseContract.ExpenseEntry.COLUMN_ABOUT));
        } while (data.moveToNext());
        mTextView.setText(output);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }
}
