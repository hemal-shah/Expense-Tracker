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
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;
import hemal.t.shah.expensetracker.activities.ExpensesActivity;
import hemal.t.shah.expensetracker.activities.MainActivity;
import hemal.t.shah.expensetracker.R;
import hemal.t.shah.expensetracker.adapters.SharedClusterAdapter;
import hemal.t.shah.expensetracker.data.DataDispenser;
import hemal.t.shah.expensetracker.data.ExpenseContract;
import hemal.t.shah.expensetracker.data.ExpenseContract.ClusterEntry;
import hemal.t.shah.expensetracker.data.ExpenseContract.ExpenseEntry;
import hemal.t.shah.expensetracker.interfaces.CallbackTwoPaneMode;
import hemal.t.shah.expensetracker.interfaces.OnCluster;
import hemal.t.shah.expensetracker.pojo.ClusterParcelable;
import hemal.t.shah.expensetracker.utils.MyStatuses;
import hemal.t.shah.expensetracker.utils.PreferenceManager;
import hemal.t.shah.expensetracker.utils.SharedConstants;

/**
 * Displays the clusters which are shared between users.
 * Created by hemal on 17/12/16.
 */
public class SharedClustersFragment extends Fragment implements
        LoaderManager.LoaderCallbacks<Cursor>, OnCluster {

    private static final String TAG = "SharedClustersFrag";
    private static boolean mTwoPane;
    @BindView(R.id.rv_activity_shared_clusters)
    RecyclerView recyclerView;
    @BindString(R.string.you_will_exit_group)
    String YOU_WILL_EXIT_GROUP;
    @BindString(R.string.share)
    String SHARE;
    @BindString(R.string.status_ok_sc)
    String STATUS_OK;
    @BindString(R.string.status_internet_error)
    String STATUS_INTERNET_ERROR;
    @BindString(R.string.data_not_available)
    String DATA_NOT_AVAILABLE;
    @BindString(R.string.status_unknown_error)
    String UNKNOWN_ERROR;
    @BindString(R.string.other_people)
    String OTHER_PEOPLE;
    @BindString(R.string.here_is_your_code)
    String HERE_IS_YOUR_CODE;
    @BindString(R.string.hold_minute)
    String HOLD_MINUTE;
    @BindString(R.string.join_my_share_cluster)
    String JOIN;
    @BindString(R.string.cancel)
    String CANCEL;
    @BindString(R.string.connect_to_internet)
    String CONNECT_INTERNET;
    @BindView(R.id.tv_empty_shared_clusters)
    TextView emptyTextView;
    @BindString(R.string.exit_confirm)
    String EXIT_CONFIRM;
    private SharedClusterAdapter adapter = null;
    private FirebaseUser user;
    private DatabaseReference reference;
    private Context context = null;

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


        mTwoPane = PreferenceManager.getTwoPaneMode(context);

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

        emptyViewBehavior();
        recyclerView.addOnScrollListener(new OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                if (dy > 0) {
                    ((FloatingActionButton) getActivity().findViewById(
                            R.id.fab_activity_tab_container)).hide();
                } else if (dy < 0) {
                    ((FloatingActionButton) getActivity().findViewById(
                            R.id.fab_activity_tab_container)).show();
                }
            }
        });

        initializeLoader(SharedConstants.CURSOR_SHARED);

        return baseView;
    }


    private void emptyViewBehavior() {
        if (adapter.getItemCount() <= 0) {

            /**
             * Data is not shown to the user, set some message here...
             */

            String message = DATA_NOT_AVAILABLE;

            @MyStatuses.Statuses int status =
                    MyStatuses.getStatus(context, MyStatuses.STATUS_ACCESS_SC);

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


    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {

        String selection = ExpenseContract.ClusterEntry.COLUMN_IS_SHARED + " = 1";
        String sortOrder = null;
        switch (id) {
            case SharedConstants.CURSOR_SHARED:
                //do no change to the sortOrder.
                break;
            case SharedConstants.CURSOR_SHARED_A_Z:
                sortOrder = ClusterEntry.COLUMN_TITLE + " ASC";
                break;
            case SharedConstants.CURSOR_SHARED_Z_A:
                sortOrder = ClusterEntry.COLUMN_TITLE + " DESC";
                break;
            default:
                return null;
        }

        return new CursorLoader(
                context,
                ClusterEntry.CONTENT_URI,
                SharedConstants.PROJECTION_CLUSTER,
                selection,
                null,
                sortOrder
        );
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        adapter.swapCursor(data);
        MyStatuses.setSharedClusterStatus(context, MyStatuses.STATUS_OK);
        emptyViewBehavior();
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        adapter.swapCursor(null);
        MyStatuses.setSharedClusterStatus(context, MyStatuses.STATUS_UNKNOWN);
        emptyViewBehavior();
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_shared_clusters, menu);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.sort_a_z_shared:
                initializeLoader(SharedConstants.CURSOR_SHARED_A_Z);
                break;
            case R.id.sort_z_a_shared:
                initializeLoader(SharedConstants.CURSOR_SHARED_Z_A);
                break;
        }

        return super.onOptionsItemSelected(item);
    }


    /**
     * Common code to remove redundancy.
     * Initializes the loader with provided token.
     *
     * @param token id in the initLoader() function.
     */
    private void initializeLoader(int token) {
        getActivity().getSupportLoaderManager().initLoader(
                token, null, this
        );
    }

    @Override
    public void onDelete(final ClusterParcelable cluster) {

        /**
         * We would actually perform the action of leaving the group, instead of
         * deleting the group, as other users would be part of this group.
         */

        //Creating a dialog to confirm deletion of cluster!
        AlertDialog.Builder builder = new AlertDialog.Builder(this.context);

        builder.setTitle(YOU_WILL_EXIT_GROUP)
                .setCancelable(true)
                .setPositiveButton(EXIT_CONFIRM, new OnClickListener() {
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

                        //Now, let's remove user from the online database...we don't want to
                        // delete the shared cluster
                        //1. deleting entries from "clusters_of_users"
                        Query queryToRemoveClusterId = reference.child(
                                SharedConstants.FIREBASE_CLUSTERS_OF_USERS)
                                .child(user.getUid())
                                .orderByChild(SharedConstants.FIREBASE_PATH_SHARED_CLUSTERS)
                                .equalTo(cluster.getFirebase_cluster_id());

                        queryToRemoveClusterId.addListenerForSingleValueEvent(
                                new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                            snapshot.getRef().removeValue();
                                        }
                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {
                                        Log.i(TAG, "onCancelled: error occured!"
                                                + databaseError.getDetails());
                                    }
                                });

                        //2. remove reference from "users_in_clusters"
                        Query queryToRemoveUserId = reference.child(
                                SharedConstants.FIREBASE_USERS_IN_CLUSTERS)
                                .child(cluster.getFirebase_cluster_id())
                                .orderByChild(SharedConstants.FIREBASE_USER_UID)
                                .equalTo(user.getUid());

                        queryToRemoveUserId.addListenerForSingleValueEvent(
                                new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                            snapshot.getRef().removeValue();
                                        }
                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {
                                        Log.i(TAG, "onCancelled: error occured!"
                                                + databaseError.getDetails());
                                    }
                                });

                        //3. Remove the clusterkey from tinydb
                        PreferenceManager.removeKeyFromAdded(context,
                                cluster.getFirebase_cluster_id());
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
    public void onAddParticipant(final ClusterParcelable cluster) {


        if (!PreferenceManager.isNetworkConnected(context)) {
            Toast.makeText(context, CONNECT_INTERNET,
                    Toast.LENGTH_SHORT).show();
            return;
        }

        //Will be called.
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

                                if (cluster_key.equals(cluster.getFirebase_cluster_id())) {
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

    @Override
    public void onTouch(ClusterParcelable cluster) {

        if (mTwoPane) {
            if (getParentFragment().getActivity() instanceof MainActivity) {
                Bundle args = new Bundle();
                args.putParcelable(SharedConstants.SHARE_CLUSTER_PARCEL, cluster);
                ((CallbackTwoPaneMode) getParentFragment().getActivity()).openCluster(args);
            }
        } else {
            Intent intent = new Intent(context, ExpensesActivity.class);
            intent.putExtra(SharedConstants.SHARE_CLUSTER_PARCEL, cluster);
            startActivity(intent);
        }
    }
}
