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
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AlertDialog.Builder;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.OnScrollListener;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

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
import hemal.t.shah.expensetracker.utils.SharedConstants;

/**
 * Created by hemal on 26/12/16.
 */
public class SharedExpensesFragment extends Fragment implements
        LoaderManager.LoaderCallbacks<Cursor>, OnExpense {

    private static final String TAG = "SharedExpensesFrag";

    @BindView(R.id.rv_shared_expenses_fragment)
    RecyclerView mRecyclerView;


    @BindString(R.string.are_you_sure)
    String ARE_YOU_SURE;

    @BindString(R.string.cancel)
    String CANCEL;

    @BindString(R.string.delete_confirm)
    String DELETE_CONFIRM;

    ClusterParcelable sharedCluster = null;

    Context context = null;

    DatabaseReference reference = null;

    SharedExpensesAdapter adapter = null;

    String selection = ExpenseEntry.FIREBASE_CLUSTER_KEY + " = ?";
    String[] selectionArgs;

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

        initializeLoader(SharedConstants.CURSOR_EXPENSES_SHARED);
        return rootView;

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
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        this.adapter.swapCursor(null);
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
        dialog.setMessage("Hold on a minute");
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

    private void showCodeToUser(final String code) {
        AlertDialog.Builder builder = new Builder(context);
        builder.setTitle("Here is your code!")
                .setMessage("Ask other people to enter this code to join this cluster : " + code)
                .setCancelable(true)
                .setPositiveButton("Share", new OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent shareIntent = new Intent();
                        shareIntent.setAction(Intent.ACTION_SEND);
                        shareIntent.putExtra(Intent.EXTRA_TEXT,
                                "Join my shared cluster on expense tracker using code " + code);
                        shareIntent.setType("text/plain");
                        startActivity(Intent.createChooser(shareIntent, "Share code."));
                    }
                }).create().show();
    }
}
