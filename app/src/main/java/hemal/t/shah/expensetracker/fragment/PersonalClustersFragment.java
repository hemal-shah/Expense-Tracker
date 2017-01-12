package hemal.t.shah.expensetracker.fragment;

import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
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
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.DatabaseReference.CompletionListener;
import com.google.firebase.database.FirebaseDatabase;

import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;
import hemal.t.shah.expensetracker.ExpensesActivity;
import hemal.t.shah.expensetracker.R;
import hemal.t.shah.expensetracker.adapters.PersonalClusterAdapter;
import hemal.t.shah.expensetracker.data.DataDispenser;
import hemal.t.shah.expensetracker.data.ExpenseContract.ClusterEntry;
import hemal.t.shah.expensetracker.data.ExpenseContract.ExpenseEntry;
import hemal.t.shah.expensetracker.interfaces.OnCluster;
import hemal.t.shah.expensetracker.pojo.ClusterParcelable;
import hemal.t.shah.expensetracker.utils.SharedConstants;

/**
 * Fragment showing clusters which are personal.
 * Created by hemal on 13/12/16.
 */
public class PersonalClustersFragment extends Fragment implements
        LoaderManager.LoaderCallbacks<Cursor>, OnCluster {

    private static final String TAG = "PersonalExpenseFragment";


    @BindString(R.string.are_you_sure)
    String ARE_YOU_SURE;

    @BindString(R.string.cancel)
    String CANCEL;

    @BindString(R.string.delete_confirm)
    String DELETE_CONFIRM;

    @BindView(R.id.rv_activity_personal_clusters)
    RecyclerView recyclerView;

    Context context;

    FirebaseUser user;
    DatabaseReference reference;

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

        //Gettings firebase ready
        reference = FirebaseDatabase.getInstance().getReference();
        user = FirebaseAuth.getInstance().getCurrentUser();


        recyclerView.setLayoutManager(
                new LinearLayoutManager(
                        context, LinearLayoutManager.VERTICAL, false
                )
        );

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

        String selection = ClusterEntry.COLUMN_IS_SHARED + " = 0";
        switch (id) {
            case SharedConstants.CURSOR_PERSONAL:
                return new CursorLoader(
                        context,
                        ClusterEntry.CONTENT_URI,
                        null,
                        selection,
                        null,
                        null
                );

            case SharedConstants.CURSOR_PERSONAL_A_Z:
                return new CursorLoader(
                        context,
                        ClusterEntry.CONTENT_URI,
                        null,
                        selection,
                        null,
                        ClusterEntry.COLUMN_TITLE + " ASC"
                );

            case SharedConstants.CURSOR_PERSONAL_Z_A:
                return new CursorLoader(
                        context,
                        ClusterEntry.CONTENT_URI,
                        null,
                        selection,
                        null,
                        ClusterEntry.COLUMN_TITLE + " DESC"
                );

            default:
                return null;
        }

    }

    @Override
    public void onDelete(final ClusterParcelable cluster) {

        AlertDialog.Builder builder = new AlertDialog.Builder(this.context);


        builder.setTitle(ARE_YOU_SURE)
                .setCancelable(true)
                .setPositiveButton(DELETE_CONFIRM, new OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        //First deleting cluster offline!
                        DataDispenser dispenser = new DataDispenser(context.getContentResolver(),
                                context);
                        dispenser.startDelete(
                                SharedConstants.TOKEN_DELETE_CLUSTER,
                                null,
                                ClusterEntry.CONTENT_URI,
                                ClusterEntry.COLUMN_FIREBASE_CLUSTER_KEY + " = ?",
                                new String[]{
                                        cluster.getFirebase_cluster_id()}
                        );

                        //Remove all expenses from expenses table as well!
                        dispenser.startDelete(
                                SharedConstants.TOKEN_DELETE_EXPENSES,
                                null,
                                ExpenseEntry.CONTENT_URI,
                                ExpenseEntry.FIREBASE_CLUSTER_KEY + "= ?",
                                new String[]{String.valueOf(cluster.getFirebase_cluster_id())}
                        );

                        //Now, let's remove data from firebase.
                        if (cluster.getIs_shared() == 0) {
                            //Delete from personal clusters branch
                            reference.child(SharedConstants.FIREBASE_PATH_PERSONAL_CLUSTERS)
                                    .child(user.getUid())
                                    .child(cluster.getFirebase_cluster_id())
                                    .removeValue(new CompletionListener() {
                                        // TODO: 7/1/17 maybe remove completion listener
                                        @Override
                                        public void onComplete(DatabaseError databaseError,
                                                DatabaseReference databaseReference) {
                                            if (databaseError == null) {
                                                Toast.makeText(context, "Removed Successfully!",
                                                        Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });

                        }
                    }
                })
                .setNegativeButton(CANCEL, new OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    @Override
    public void onTouch(ClusterParcelable cluster) {

        Intent intent = new Intent(this.context, ExpensesActivity.class);
        intent.putExtra(SharedConstants.SHARE_CLUSTER_PARCEL, cluster);
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
            case R.id.sort_a_z:
                getActivity().getSupportLoaderManager()
                        .initLoader(
                                SharedConstants.CURSOR_PERSONAL_A_Z,
                                null,
                                this
                        );
                return true;

            case R.id.sort_z_a:
                getActivity().getSupportLoaderManager()
                        .initLoader(
                                SharedConstants.CURSOR_PERSONAL_Z_A,
                                null,
                                this
                        );
                return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
