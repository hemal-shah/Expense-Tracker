package hemal.t.shah.expensetracker.fragment;

import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import hemal.t.shah.expensetracker.data.ExpenseContract;
import hemal.t.shah.expensetracker.data.ExpenseContract.ExpenseEntry;
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

    DatabaseReference reference;

    @BindView(R.id.rv_personal_expenses_fragment)
    RecyclerView recyclerView;


    @BindString(R.string.are_you_sure)
    String ARE_YOU_SURE;

    @BindString(R.string.cancel)
    String CANCEL;

    @BindString(R.string.delete_confirm)
    String DELETE_CONFIRM;

    PersonalExpensesAdapter adapter = null;

    Context mContext;
    ClusterParcelable personalCluster;

    String selection = ExpenseEntry.FIREBASE_CLUSTER_KEY + " = ?";
    String[] selectionArgs;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {


        Bundle arguments = getArguments();
        if (arguments != null) {
            personalCluster = arguments.getParcelable(SharedConstants.SHARE_CLUSTER_PARCEL);
        }

        this.selectionArgs = new String[]{String.valueOf(personalCluster.getFirebase_cluster_id())};
        this.mContext = getContext();


        reference = FirebaseDatabase.getInstance().getReference();

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
    public void delete(final ExpenseParcelable expenseParcelable) {


        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
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
                        DataDispenser dispenser = new DataDispenser(mContext.getContentResolver(),
                                mContext);

                        dispenser.startDelete(
                                SharedConstants.TOKEN_DELETE_EXPENSES,
                                null,
                                ExpenseEntry.CONTENT_URI,
                                ExpenseEntry.COLUMN_FIREBASE_EXPENSE_KEY + " = ?",
                                new String[]{String.valueOf(expenseParcelable.getFirebase_expense_key())}
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
                                        Toast.makeText(mContext, "Deleted!", Toast.LENGTH_SHORT).show();
                                    }
                                });
                    }
                });

        builder.create().show();
    }
}
