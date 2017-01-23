package hemal.t.shah.expensetracker.fragment;

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

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;
import hemal.t.shah.expensetracker.activities.ExpensesActivity;
import hemal.t.shah.expensetracker.activities.MainActivity;
import hemal.t.shah.expensetracker.R;
import hemal.t.shah.expensetracker.adapters.PersonalClusterAdapter;
import hemal.t.shah.expensetracker.data.DataDispenser;
import hemal.t.shah.expensetracker.data.ExpenseContract.ClusterEntry;
import hemal.t.shah.expensetracker.data.ExpenseContract.ExpenseEntry;
import hemal.t.shah.expensetracker.interfaces.OnCluster;
import hemal.t.shah.expensetracker.pojo.ClusterParcelable;
import hemal.t.shah.expensetracker.utils.MyStatuses;
import hemal.t.shah.expensetracker.utils.PreferenceManager;
import hemal.t.shah.expensetracker.utils.SharedConstants;

/**
 * Fragment showing clusters which are personal.
 * Created by hemal on 13/12/16.
 */
public class PersonalClustersFragment extends Fragment implements
        LoaderManager.LoaderCallbacks<Cursor>, OnCluster {

    private static final String TAG = "PersonalExpenseFrag";
    private static boolean mTwoPane;
    @BindString(R.string.are_you_sure)
    String ARE_YOU_SURE;
    @BindString(R.string.cancel)
    String CANCEL;
    @BindString(R.string.status_ok_pc)
    String STATUS_OK;
    @BindString(R.string.status_internet_error)
    String STATUS_INTERNET_ERROR;
    @BindString(R.string.data_not_available)
    String DATA_NOT_AVAILABLE;
    @BindString(R.string.status_unknown_error)
    String UNKNOWN_ERROR;
    @BindString(R.string.delete_confirm)
    String DELETE_CONFIRM;
    @BindView(R.id.rv_activity_personal_clusters)
    RecyclerView recyclerView;
    @BindView(R.id.tv_empty_personal_clusters)
    TextView emptyTextView;
    private Context context;
    private FirebaseUser user;
    private DatabaseReference reference;
    private PersonalClusterAdapter personalClusterAdapter;

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

        mTwoPane = PreferenceManager.getTwoPaneMode(context);

        //Getting firebase ready
        reference = FirebaseDatabase.getInstance().getReference();
        user = FirebaseAuth.getInstance().getCurrentUser();

        LinearLayoutManager manager = new LinearLayoutManager(context,
                LinearLayoutManager.VERTICAL, false) {
            @Override
            public boolean supportsPredictiveItemAnimations() {
                return true;
            }
        };

        recyclerView.setLayoutManager(manager);

        recyclerView.hasFixedSize();

        personalClusterAdapter = new PersonalClusterAdapter(context, null, this);

        emptyViewBehavior();

        recyclerView.setAdapter(personalClusterAdapter);

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

        initializeLoader(SharedConstants.CURSOR_PERSONAL);
        return baseView;
    }

    @Override
    public void onLoadFinished(Loader loader, Cursor data) {
        personalClusterAdapter.swapCursor(data);
        MyStatuses.setPersonalClusterStatus(context, MyStatuses.STATUS_OK);
        emptyViewBehavior();
    }

    private void emptyViewBehavior() {
        if (personalClusterAdapter.getItemCount() <= 0) {

            /**
             * Data is not shown to the user, set some message here...
             */

            String message = DATA_NOT_AVAILABLE;

            @MyStatuses.Statuses int status =
                    MyStatuses.getStatus(context, MyStatuses.STATUS_ACCESS_PC);

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
    public void onLoaderReset(Loader loader) {
        personalClusterAdapter.swapCursor(null);
        MyStatuses.setPersonalClusterStatus(context, MyStatuses.STATUS_UNKNOWN);
        emptyViewBehavior();
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {

        String selection = ClusterEntry.COLUMN_IS_SHARED + " = 0";
        String sortOrder = null;
        switch (id) {
            case SharedConstants.CURSOR_PERSONAL:
                break;
            case SharedConstants.CURSOR_PERSONAL_A_Z:
                sortOrder = ClusterEntry.COLUMN_TITLE + " ASC";
                break;
            case SharedConstants.CURSOR_PERSONAL_Z_A:
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
                                    .removeValue();

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
    public void onAddParticipant(ClusterParcelable clusterParcelable) {
        //Will never be called!...
    }

    @Override
    public void onTouch(ClusterParcelable cluster) {

        if (mTwoPane) {
            Log.i(TAG, "onTouch: two pane mode enabled...going to mainactivity");
            Bundle bundle = new Bundle();
            bundle.putParcelable(SharedConstants.SHARE_CLUSTER_PARCEL, cluster);
            MainActivity.onClusterSelected(bundle);
        } else {
            Log.i(TAG, "onTouch: two pane mode looks erroneous...");
            Intent intent = new Intent(this.context, ExpensesActivity.class);
            intent.putExtra(SharedConstants.SHARE_CLUSTER_PARCEL, cluster);
            startActivity(intent);
        }
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
                initializeLoader(SharedConstants.CURSOR_PERSONAL_A_Z);
                break;
            case R.id.sort_z_a:
                initializeLoader(SharedConstants.CURSOR_PERSONAL_Z_A);
                break;
            default:
                return false;
        }

        return true;
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
}
