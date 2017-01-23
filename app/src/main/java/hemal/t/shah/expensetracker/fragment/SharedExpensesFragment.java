package hemal.t.shah.expensetracker.fragment;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AlertDialog.Builder;
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

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import butterknife.BindString;
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
import hemal.t.shah.expensetracker.utils.MyStatuses;
import hemal.t.shah.expensetracker.utils.PreferenceManager;
import hemal.t.shah.expensetracker.utils.SharedConstants;

/**
 * Shared expenses are shown through this place...
 * Created by hemal on 26/12/16.
 */
public class SharedExpensesFragment extends Fragment implements
        LoaderManager.LoaderCallbacks<Cursor>, OnExpense {

    private static final String TAG = "SharedExpensesFrag";

    @BindView(R.id.rv_shared_expenses_fragment)
    RecyclerView mRecyclerView;

    @BindView(R.id.tv_empty_shared_expenses)
    TextView emptyTextView;

    @BindString(R.string.are_you_sure)
    String ARE_YOU_SURE;

    @BindString(R.string.cancel)
    String CANCEL;


    @BindString(R.string.status_ok_se)
    String STATUS_OK;
    @BindString(R.string.status_internet_error)
    String STATUS_INTERNET_ERROR;
    @BindString(R.string.data_not_available)
    String DATA_NOT_AVAILABLE;
    @BindString(R.string.status_unknown_error)
    String UNKNOWN_ERROR;

    @BindString(R.string.delete_confirm)
    String DELETE_CONFIRM;

    @BindString(R.string.share)
    String SHARE;

    @BindString(R.string.other_people)
    String OTHER_PEOPLE;

    @BindString(R.string.here_is_your_code)
    String HERE_IS_YOUR_CODE;

    @BindString(R.string.hold_minute)
    String HOLD_MINUTE;

    @BindString(R.string.join_my_share_cluster)
    String JOIN;

    private ClusterParcelable sharedCluster = null;

    private Context context = null;
    private ActionBar actionBar;

    private DatabaseReference reference = null;

    private SharedExpensesAdapter adapter = null;

    private String selection = ExpenseEntry.FIREBASE_CLUSTER_KEY + " = ?";
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
            sharedCluster = arguments.getParcelable(SharedConstants.SHARE_CLUSTER_PARCEL);
        }

        this.selectionArgs = new String[]{String.valueOf(sharedCluster.getFirebase_cluster_id())};
        this.context = getContext();

        reference = FirebaseDatabase.getInstance().getReference();

        View rootView = inflater.inflate(R.layout.shared_expenses_fragment, container, false);
        ButterKnife.bind(this, rootView);


        this.mRecyclerView.setLayoutManager(new LinearLayoutManager(
                this.context,
                LinearLayoutManager.VERTICAL,
                false
        ));

        this.actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();

        this.adapter = new SharedExpensesAdapter(this.context, null, this);

        this.mRecyclerView.setAdapter(adapter);

        this.mRecyclerView.setHasFixedSize(true);

        this.mRecyclerView.addOnScrollListener(new OnScrollListener() {
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
        initializeLoader(SharedConstants.CURSOR_EXPENSES_SHARED);
        return rootView;

    }



    private void emptyViewBehavior() {
        if (adapter.getItemCount() <= 0) {

            /**
             * Data is not shown to the user, set some message here...
             */

            String message = DATA_NOT_AVAILABLE;

            @MyStatuses.Statuses int status =
                    MyStatuses.getStatus(context, MyStatuses.STATUS_ACCESS_SE);

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

            mRecyclerView.setVisibility(View.INVISIBLE);
            emptyTextView.setVisibility(View.VISIBLE);
        } else {
            mRecyclerView.setVisibility(View.VISIBLE);
            emptyTextView.setVisibility(View.INVISIBLE);
        }
    }


    private void initializeLoader(int token) {
        getActivity().getSupportLoaderManager().initLoader(
                token, null, this
        );
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String sortOrder = null;
        switch (id) {
            case SharedConstants.CURSOR_EXPENSES_SHARED:
                //change nothing..
                break;
            case SharedConstants.CURSOR_S_EXPENSES_A_Z:
                sortOrder = ExpenseEntry.COLUMN_ABOUT + " COLLATE NOCASE ASC";
                break;
            case SharedConstants.CURSOR_S_EXPENSES_Z_A:
                sortOrder = ExpenseEntry.COLUMN_ABOUT + " COLLATE NOCASE DESC";
                break;
            case SharedConstants.CURSOR_S_EXPENSES_NAME:
                sortOrder = ExpenseEntry.COLUMN_FIREBASE_USER_NAME + " COLLATE NOCASE ASC";
                break;
            case SharedConstants.CURSOR_S_EXPENSES_H_L:
                sortOrder = ExpenseEntry.COLUMN_AMOUNT + " DESC";
                break;
            case SharedConstants.CURSOR_S_EXPENSES_L_H:
                sortOrder = ExpenseEntry.COLUMN_AMOUNT + " ASC";
                break;
            default:
                return null;

        }
        return new CursorLoader(
                this.context,
                ExpenseContract.ExpenseEntry.CONTENT_URI,
                null,
                selection,
                selectionArgs,
                sortOrder
        );
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        this.adapter.swapCursor(data);
        MyStatuses.setSharedExpenseStatus(context, MyStatuses.STATUS_OK);
        emptyViewBehavior();

        /**
         * No calculation needed if length of data is zero.
         */
        if (data.getCount() == 0) return;

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

        if (actionBar != null && total != 0) {

            actionBar.setSubtitle(
                    "Total : " + PreferenceManager.getCurrency(context) + " " + total);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        this.adapter.swapCursor(null);
        MyStatuses.setSharedExpenseStatus(context, MyStatuses.STATUS_UNKNOWN);
        emptyViewBehavior();
    }

    @Override
    public void delete(final ExpenseParcelable expenseParcelable) {


        AlertDialog.Builder builder = new AlertDialog.Builder(this.context);
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
                                ExpenseContract.ExpenseEntry.CONTENT_URI,
                                ExpenseEntry.COLUMN_FIREBASE_EXPENSE_KEY + " = ? ",
                                new String[]{
                                        String.valueOf(expenseParcelable.getFirebase_expense_key())}
                        );

                        reference.child(SharedConstants.FIREBASE_PATH_SHARED_CLUSTERS)
                                .child(expenseParcelable.getFirebase_cluster_ref_key())
                                .child(SharedConstants.FIREBASE_EXPENSES)
                                .child(expenseParcelable.getFirebase_expense_key())
                                .removeValue();

                    }
                });

        builder.create().show();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_shared_expenses, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int TOKEN;
        switch (item.getItemId()) {
            case R.id.menu_s_expense_a_z:
                TOKEN = SharedConstants.CURSOR_S_EXPENSES_A_Z;
                break;
            case R.id.menu_s_expense_z_a:
                TOKEN = SharedConstants.CURSOR_S_EXPENSES_Z_A;
                break;
            case R.id.menu_s_expense_name:
                TOKEN = SharedConstants.CURSOR_S_EXPENSES_NAME;
                break;
            case R.id.menu_s_expense_h_l:
                TOKEN = SharedConstants.CURSOR_S_EXPENSES_H_L;
                break;
            case R.id.menu_s_expense_l_h:
                TOKEN = SharedConstants.CURSOR_S_EXPENSES_L_H;
                break;
            case R.id.menu_s_expense_add_participant:
                shareClusterCode();
                return true;
            default:
                return false;
        }
        initializeLoader(TOKEN);
        return true;
    }

    /**
     * Retrieve the code for this cluster,
     * and share it to others.
     */
    private void shareClusterCode() {
        final ProgressDialog dialog = new ProgressDialog(context);
        dialog.setIndeterminate(true);
        dialog.setMessage(HOLD_MINUTE);
        dialog.show();

        reference.child(SharedConstants.FIREBASE_PATH_CLUSTER_ID)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        String code = "";
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            for (DataSnapshot container : snapshot.getChildren()) {
                                String cluster_key = container.getValue().toString();

                                if (cluster_key.equals(sharedCluster.getFirebase_cluster_id())) {
                                    code = container.getKey();
                                    break;
                                }
                            }
                        }

                        if (code.length() != 0) {
                            showCodeToUser(code);
                        }
                        dialog.dismiss();
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                    }
                });
    }

    /**
     * Shows a code to user using AlertDialog
     *
     * @param code code to display
     */
    private void showCodeToUser(final String code) {
        AlertDialog.Builder builder = new Builder(context);
        builder.setTitle(HERE_IS_YOUR_CODE)
                .setMessage(OTHER_PEOPLE + code)
                .setCancelable(true)
                .setPositiveButton(SHARE, new OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent shareIntent = new Intent();
                        shareIntent.setAction(Intent.ACTION_SEND);
                        shareIntent.putExtra(Intent.EXTRA_TEXT,
                                JOIN + code);
                        shareIntent.setType("text/plain");
                        startActivity(Intent.createChooser(shareIntent, SHARE));
                    }
                }).create().show();
    }
}
