package hemal.t.shah.expensetracker.fragment;

import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.OnScrollListener;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.DatabaseReference.CompletionListener;
import com.google.firebase.database.FirebaseDatabase;

import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;
import hemal.t.shah.expensetracker.R;
import hemal.t.shah.expensetracker.adapters.PersonalExpensesAdapter;
import hemal.t.shah.expensetracker.data.DataDispenser;
import hemal.t.shah.expensetracker.data.ExpenseContract.ExpenseEntry;
import hemal.t.shah.expensetracker.interfaces.OnExpense;
import hemal.t.shah.expensetracker.pojo.ClusterParcelable;
import hemal.t.shah.expensetracker.pojo.ExpenseParcelable;
import hemal.t.shah.expensetracker.utils.MyStatuses;
import hemal.t.shah.expensetracker.utils.PreferenceManager;
import hemal.t.shah.expensetracker.utils.SharedConstants;

/**
 * Shows personal expenses of a particular cluster.
 * Created by hemal on 23/12/16.
 */
public class PersonalExpensesFragment extends Fragment implements
        LoaderManager.LoaderCallbacks<Cursor>, OnExpense {

    private static final String TAG = "PersonalExpensesFrag";

    DatabaseReference reference;

    @BindView(R.id.rv_personal_expenses_fragment)
    RecyclerView recyclerView;

    @BindString(R.string.are_you_sure)
    String ARE_YOU_SURE;


    @BindString(R.string.status_ok_pe)
    String STATUS_OK;
    @BindString(R.string.status_internet_error)
    String STATUS_INTERNET_ERROR;
    @BindString(R.string.data_not_available)
    String DATA_NOT_AVAILABLE;
    @BindString(R.string.status_unknown_error)
    String UNKNOWN_ERROR;

    @BindString(R.string.cancel)
    String CANCEL;

    @BindString(R.string.delete_confirm)
    String DELETE_CONFIRM;

    @BindView(R.id.tv_empty_personal_expenses)
    TextView emptyTextView;

    private ActionBar actionBar;

    private PersonalExpensesAdapter adapter = null;

    private boolean mTwoPane;

    private Context context;
    private ClusterParcelable personalCluster;

    private String[] selectionArgs;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {


        Bundle arguments = getArguments();
        if (arguments != null) {
            personalCluster = arguments.getParcelable(SharedConstants.SHARE_CLUSTER_PARCEL);
        }

        this.selectionArgs = new String[]{personalCluster.getFirebase_cluster_id()};
        this.context = getContext();

        mTwoPane = PreferenceManager.getTwoPaneMode(context);
        //getting action bar reference from parent activity
        actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();

        reference = FirebaseDatabase.getInstance().getReference();

        View rootView = inflater.inflate(R.layout.personal_expenses_fragment, container, false);
        ButterKnife.bind(this, rootView);

        adapter = new PersonalExpensesAdapter(this.context, null, this);
        recyclerView.setLayoutManager(
                new LinearLayoutManager(this.context, LinearLayoutManager.VERTICAL, false));
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(adapter);

        emptyViewBehavior();

        recyclerView.addOnScrollListener(new OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                if (dy > 0) {
                    ((FloatingActionButton) getActivity().findViewById(
                            R.id.fab_activity_expenses_loader)).hide();
                } else if (dy < 0) {
                    ((FloatingActionButton) getActivity().findViewById(
                            R.id.fab_activity_expenses_loader)).show();
                }
            }
        });


        emptyViewBehavior();
        initializeLoader(SharedConstants.CURSOR_EXPENSES_PERSONAL);

        return rootView;
    }

    private void emptyViewBehavior() {
        if (adapter.getItemCount() <= 0) {

            /**
             * Data is not shown to the user, set some message here...
             */

            String message = DATA_NOT_AVAILABLE;

            @MyStatuses.Statuses int status =
                    MyStatuses.getStatus(context, MyStatuses.STATUS_ACCESS_PE);

            switch (status) {
                case MyStatuses.STATUS_OK:
                    message += STATUS_OK;
                    break;
                case MyStatuses.STATUS_ERROR_NO_NETWORK:
                    message += STATUS_INTERNET_ERROR;
                    break;
                case MyStatuses.STATUS_UNKNOWN:
                    message += UNKNOWN_ERROR;
                    break;
            }

            emptyTextView.setText(message);

            recyclerView.setVisibility(View.INVISIBLE);
            emptyTextView.setVisibility(View.VISIBLE);
        } else {
            recyclerView.setVisibility(View.VISIBLE);
            emptyTextView.setVisibility(View.INVISIBLE);
        }
    }

    /**
     * Common code to remove redundancy.
     * Initializes the loader with provided token.
     *
     * @param token id in the initLoader() function.
     */
    private void initializeLoader(int token) {

        if (mTwoPane) {
            getActivity().getSupportLoaderManager()
                    .restartLoader(token, null, this);
        }
        getActivity().getSupportLoaderManager().initLoader(
                token, null, this
        );
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {

        String[] projection = {
                ExpenseEntry._ID, //Always include _ID column for CursorRecyclerViewAdapter
                ExpenseEntry.COLUMN_ABOUT,
                ExpenseEntry.COLUMN_AMOUNT,
                ExpenseEntry.COLUMN_DESCRIBE,
                ExpenseEntry.COLUMN_TIMESTAMP,
                ExpenseEntry.FIREBASE_CLUSTER_KEY,
                ExpenseEntry.COLUMN_FIREBASE_EXPENSE_KEY
        };

        String sortOrder = null;
        switch (id) {
            case SharedConstants.CURSOR_EXPENSES_PERSONAL:
                break;
            case SharedConstants.CURSOR_EXPENSES_PERSONAL_A_Z:
                sortOrder = ExpenseEntry.COLUMN_ABOUT + " COLLATE NOCASE ASC";
                break;
            case SharedConstants.CURSOR_EXPENSES_PERSONAL_Z_A:
                sortOrder = ExpenseEntry.COLUMN_ABOUT + " COLLATE NOCASE DESC";
                break;
            case SharedConstants.CURSOR_EXPENSES_PERSONAL_H_L:
                sortOrder = ExpenseEntry.COLUMN_AMOUNT + " DESC";
                break;
            case SharedConstants.CURSOR_EXPENSES_PERSONAL_L_H:
                sortOrder = ExpenseEntry.COLUMN_AMOUNT + " ASC";
                break;
            default:
                return null;
        }
        String selection = ExpenseEntry.FIREBASE_CLUSTER_KEY + " = ?";
        return new CursorLoader(
                this.context,
                ExpenseEntry.CONTENT_URI,
                projection,
                selection,
                selectionArgs,
                sortOrder
        );
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        adapter.swapCursor(data);

        MyStatuses.setPersonalExpenseStatus(context, MyStatuses.STATUS_OK);
        emptyViewBehavior();

        /**
         * No calculation needed if length of data is zero.
         */
        if (data.getCount() == 0) return;

        new CalculateTotalTask().execute(data);
    }

    public class CalculateTotalTask extends AsyncTask<Cursor, Void, Double>{

        @Override
        protected Double doInBackground(Cursor... params) {
            Cursor data = params[0];

            /**
             * Load & Calculate the sum and display it in the action bar
             * subtitle.
             */
            int index_amount = data.getColumnIndex(
                    ExpenseEntry.COLUMN_AMOUNT
            );

            double total = 0;
            for (int i = 0; i < data.getCount(); i++) {
                data.moveToPosition(i);
                double amount = data.getDouble(index_amount);
                total += amount;
            }
            return total;
        }

        @Override
        protected void onPostExecute(Double total) {

            if (actionBar != null && total != 0) {

                actionBar.setSubtitle(
                        "Total : " + PreferenceManager.getCurrency(context) + " " + total);
            }

        }
    }


    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        adapter.swapCursor(null);
        MyStatuses.setPersonalExpenseStatus(context, MyStatuses.STATUS_UNKNOWN);
        emptyViewBehavior();
    }

    @Override
    public void delete(final ExpenseParcelable expenseParcelable) {


        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(ARE_YOU_SURE)
                .setCancelable(true)
                .setNegativeButton(CANCEL, new OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .setPositiveButton(DELETE_CONFIRM, new OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        DataDispenser dispenser = new DataDispenser(context.getContentResolver(),
                                context);

                        dispenser.startDelete(
                                SharedConstants.TOKEN_DELETE_EXPENSES,
                                null,
                                ExpenseEntry.CONTENT_URI,
                                ExpenseEntry.COLUMN_FIREBASE_EXPENSE_KEY + " = ?",
                                new String[]{
                                        String.valueOf(expenseParcelable.getFirebase_expense_key())}
                        );

                        reference.child(SharedConstants.FIREBASE_PATH_PERSONAL_CLUSTERS)
                                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                .child(expenseParcelable.getFirebase_cluster_ref_key())
                                .child(SharedConstants.FIREBASE_EXPENSES)
                                .child(expenseParcelable.getFirebase_expense_key())
                                .removeValue(new CompletionListener() {
                                    @Override
                                    public void onComplete(DatabaseError databaseError,
                                            DatabaseReference databaseReference) {
                                        Toast.makeText(context, "Deleted!",
                                                Toast.LENGTH_SHORT).show();
                                    }
                                });
                    }
                });

        builder.create().show();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        if (mTwoPane) {
            menu.clear();
        }
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_personal_expenses, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int TOKEN;
        switch (item.getItemId()) {
            case R.id.menu_p_expense_a_z:
                TOKEN = SharedConstants.CURSOR_EXPENSES_PERSONAL_A_Z;
                break;
            case R.id.menu_p_expense_h_l:
                TOKEN = SharedConstants.CURSOR_EXPENSES_PERSONAL_H_L;
                break;
            case R.id.menu_p_expense_l_h:
                TOKEN = SharedConstants.CURSOR_EXPENSES_PERSONAL_L_H;
                break;
            case R.id.menu_p_expense_z_a:
                TOKEN = SharedConstants.CURSOR_EXPENSES_PERSONAL_Z_A;
                break;
            default:
                return false;
        }
        initializeLoader(TOKEN);
        return true;
    }
}
