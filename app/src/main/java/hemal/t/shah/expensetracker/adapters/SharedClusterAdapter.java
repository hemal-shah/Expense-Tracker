package hemal.t.shah.expensetracker.adapters;

import android.content.Context;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.hemal.shah.TimeTravel;
import com.hemal.shah.TimeTravelException;

import butterknife.BindView;
import butterknife.ButterKnife;
import hemal.t.shah.expensetracker.R;
import hemal.t.shah.expensetracker.data.ExpenseContract.ClusterEntry;
import hemal.t.shah.expensetracker.interfaces.OnCluster;
import hemal.t.shah.expensetracker.pojo.ClusterParcelable;

/**
 * Adapter for shared clusters tabs.
 * Created by hemal on 20/12/16.
 */
public class SharedClusterAdapter
        extends CursorRecyclerViewAdapter<SharedClusterAdapter.ViewHolder> {

    private static final String TAG = "SharedClusterAdapter";
    private Context context = null;
    private OnCluster onCluster = null;

    public SharedClusterAdapter(Context context, Cursor cursor, OnCluster onCluster) {
        super(context, cursor);
        this.context = context;
        this.onCluster = onCluster;
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, Cursor cursor, int position) {


        int index_title = cursor.getColumnIndex(ClusterEntry.COLUMN_TITLE);
        int index_timestamp = cursor.getColumnIndex(ClusterEntry.COLUMN_TIMESTAMP);
        int index_firebase_key = cursor.getColumnIndex(ClusterEntry.COLUMN_FIREBASE_CLUSTER_KEY);
        int index_id = cursor.getColumnIndex(ClusterEntry._ID);

        if (cursor.moveToPosition(position)) {
            String title = cursor.getString(index_title);
            String timeStamp = "";
            long startTime = cursor.getLong(index_timestamp);

            try {
                timeStamp = TimeTravel.getTimeElapsed(startTime, System.currentTimeMillis());
            } catch (TimeTravelException e) {
                e.printStackTrace();
            }

            int cluster_id = cursor.getInt(index_id);
            String firebase_cluster_key = cursor.getString(index_firebase_key);

            final ClusterParcelable cluster = new ClusterParcelable(
                    title, firebase_cluster_key, 1, startTime, cluster_id
            );

            String s = "Title = " + cluster.getTitle() +
                    "\nTimeStamp = " + timeStamp;

            viewHolder.tv.setText(s);
            viewHolder.delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (onCluster != null) {
                        onCluster.onDelete(cluster);
                    }
                }
            });

            viewHolder.open.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (onCluster != null) {
                        onCluster.onTouch(cluster);
                    }
                }
            });
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView =
                LayoutInflater.from(this.context).inflate(R.layout.row_shared_clusters, parent, false);
        return new ViewHolder(itemView);
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.tv_single_shared_clusters_row)
        TextView tv;

        @BindView(R.id.bt_delete_shared_clusters)
        Button delete;

        @BindView(R.id.bt_open_shared_clusters)
        Button open;

        ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
