package hemal.t.shah.expensetracker.fragment;

import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import hemal.t.shah.expensetracker.R;
import hemal.t.shah.expensetracker.adapters.ClusterAdapter;
import hemal.t.shah.expensetracker.data.ExpenseContract;

/**
 * Fragment showing clusters which are personal.
 * Created by hemal on 13/12/16.
 */
public class PersonalExpensesFragment extends Fragment implements
        LoaderManager.LoaderCallbacks<Cursor> {

    private static final String TAG = "PersonalExpenseFragment";

    @BindView(R.id.rv_activity_personal_clusters)
    RecyclerView recyclerView;

    private static final int CURSOR_PERSONAL = 101; //id for cursor loader

    Context context;

    ClusterAdapter clusterAdapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        View baseView = inflater.inflate(R.layout.activity_personal_clusters, container, false);
        ButterKnife.bind(this, baseView);

        context = getContext();

        recyclerView.setLayoutManager(new LinearLayoutManager(
                context,
                LinearLayoutManager.VERTICAL,
                false
        ));

        recyclerView.hasFixedSize();

        clusterAdapter = new ClusterAdapter(context, null);

        recyclerView.setAdapter(clusterAdapter);

        getActivity().getSupportLoaderManager().initLoader(
                CURSOR_PERSONAL,
                null,
                this
        );

        return baseView;
    }

    @OnClick(R.id.fab_activity_personal_clusters)
    public void fabNewPersonalCluster() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this.context);
        builder.setCancelable(true);
        builder.setTitle("Add new Cluster");

        View etView = getLayoutInflater(null).inflate(R.layout.dialog_new_cluster, null);
        final TextInputEditText et_new_personal_cluster = (TextInputEditText) etView.findViewById(
                R.id.tiet_dialog_new_cluster);

        builder.setPositiveButton("Create", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String title = et_new_personal_cluster.getText().toString();

                if (title.length() >= 15) {
                    // TODO: 17/12/16 add snackbar here
                    Toast.makeText(context, "Length should be between 3 to 15 characters.",
                            Toast.LENGTH_SHORT).show();
                    return;
                }

                Log.i(TAG, "onClick: " + title);
                int is_shared = 0;
                //// TODO: 17/12/16 generate timestamp here...
                String timestamp = "new time here..";

                // TODO: 17/12/16 asyntask to add new cluster
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.setView(etView);
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    @Override
    public void onLoadFinished(Loader loader, Cursor data) {
        clusterAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader loader) {
        clusterAdapter.swapCursor(null);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {

        String[] projection = {
                ExpenseContract.ClusterEntry._ID,
                ExpenseContract.ClusterEntry.COLUMN_TITLE,
                ExpenseContract.ClusterEntry.COLUMN_SUM,
                ExpenseContract.ClusterEntry.COLUMN_TIMESTAMP};

        String selection = ExpenseContract.ClusterEntry.COLUMN_IS_SHARED + " = ?";
        String[] selectionArgs = {"0"};
        return new CursorLoader(
                context,
                ExpenseContract.ClusterEntry.CONTENT_URI,
                projection,
                selection,
                selectionArgs,
                null
        );
    }
}
