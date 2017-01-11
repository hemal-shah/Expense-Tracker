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
import hemal.t.shah.expensetracker.adapters.SharedClusterAdapter;
import hemal.t.shah.expensetracker.data.DataDispenser;
import hemal.t.shah.expensetracker.data.ExpenseContract;
import hemal.t.shah.expensetracker.data.ExpenseContract.ClusterEntry;
import hemal.t.shah.expensetracker.data.ExpenseContract.ExpenseEntry;
import hemal.t.shah.expensetracker.interfaces.OnCluster;
import hemal.t.shah.expensetracker.pojo.ClusterParcelable;
import hemal.t.shah.expensetracker.utils.SharedConstants;

/**
 * Displays the clusters which are shared between users.
 * Created by hemal on 17/12/16.
 */
public class SharedClustersFragment extends Fragment implements
        LoaderManager.LoaderCallbacks<Cursor>, OnCluster {

    private static final String TAG = "SharedClustersFragment";
    @BindView(R.id.rv_activity_shared_clusters)
    RecyclerView recyclerView;
    @BindString(R.string.are_you_sure)
    String ARE_YOU_SURE;
    @BindString(R.string.cancel)
    String CANCEL;
    @BindString(R.string.delete_confirm)
    String DELETE_CONFIRM;
    SharedClusterAdapter adapter = null;

    FirebaseUser user;
    DatabaseReference reference;

    Context context = null;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        View baseView = inflater.inflate(R.layout.activity_shared_clusters, container, false);

        ButterKnife.bind(this, baseView);

        this.context = getContext();

        this.reference = FirebaseDatabase.getInstance().getReference();
        this.user = FirebaseAuth.getInstance().getCurrentUser();

        adapter = new SharedClusterAdapter(this.context, null, this);

        recyclerView.setLayoutManager(new LinearLayoutManager(
                context,
                LinearLayoutManager.VERTICAL,
                false
        ));


        recyclerView.hasFixedSize();
        recyclerView.setAdapter(adapter);

        getActivity().getSupportLoaderManager().initLoader(
                SharedConstants.CURSOR_SHARED, null, this
        );

        return baseView;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {

        switch (id) {
            case SharedConstants.CURSOR_SHARED:

                String selection = ExpenseContract.ClusterEntry.COLUMN_IS_SHARED + " = 1";
                return new CursorLoader(
                        context,
                        ExpenseContract.ClusterEntry.CONTENT_URI,
                        null,
                        selection,
                        null,
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
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_shared_clusters, menu);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.sort_1_s:
                Toast.makeText(context, "sort 1 shared", Toast.LENGTH_SHORT).show();
                return true;

            case R.id.sort_2_s:
                Toast.makeText(context, "sort 2 shared", Toast.LENGTH_SHORT).show();
                return true;

            case R.id.sort_3_s:
                Toast.makeText(context, "sort 3 shared", Toast.LENGTH_SHORT).show();
                return true;
        }

        return super.onOptionsItemSelected(item);
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
                        if (cluster.getIs_shared() == 1) {
                            //Delete from Shared clusters branch
                            reference.child(SharedConstants.FIREBASE_PATH_SHARED_CLUSTERS)
                                    .child(cluster.getFirebase_cluster_id())
                                    .removeValue(new CompletionListener() {
                                        @Override
                                        public void onComplete(DatabaseError databaseError,
                                                DatabaseReference databaseReference) {
                                            Toast.makeText(context, "Removed Shared Cluster",
                                                    Toast.LENGTH_SHORT).show();
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
        Intent intent = new Intent(context, ExpensesActivity.class);
        intent.putExtra(SharedConstants.SHARE_CLUSTER_PARCEL, cluster);
        startActivity(intent);
    }
}
