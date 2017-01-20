package hemal.t.shah.expensetracker.adapters;

import android.content.Context;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.hemal.shah.TimeTravel;
import com.hemal.shah.TimeTravelException;

import butterknife.BindString;
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

        if (cursor.moveToPosition(position)) {
            String title = cursor.getString(index_title);

            long startTime = cursor.getLong(index_timestamp);

            String timeStamp;
            try {
                timeStamp = TimeTravel.getTimeElapsed(startTime, System.currentTimeMillis());
            } catch (TimeTravelException e) {
                timeStamp = viewHolder.WRONG_TIME;
            }

            String firebase_cluster_key = cursor.getString(index_firebase_key);

            final ClusterParcelable cluster = new ClusterParcelable(
                    title, null, firebase_cluster_key, 1, startTime
            );


            viewHolder.title.setText(title.toUpperCase());

            viewHolder.timeStamp.setText(timeStamp);

            viewHolder.exit.setOnClickListener(new View.OnClickListener() {
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

            viewHolder.share.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (onCluster != null) {
                        onCluster.onShare(cluster);
                    }
                }
            });
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView =
                LayoutInflater.from(this.context).inflate(R.layout.row_shared_clusters, parent,
                        false);
        return new ViewHolder(itemView);
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.tv_s_cluster_timestamp)
        TextView timeStamp;

        @BindView(R.id.tv_s_cluster_title)
        TextView title;

        @BindView(R.id.ib_exit_s_cluster)
        ImageButton exit;

        @BindView(R.id.share_cluster)
        ImageButton share;

        @BindString(R.string.inappropriate_time)
        String WRONG_TIME;

        @BindView(R.id.ll_s_cluster)
        LinearLayout open;

        ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
