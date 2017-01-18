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

import butterknife.BindView;
import butterknife.ButterKnife;
import hemal.t.shah.expensetracker.R;
import hemal.t.shah.expensetracker.data.ExpenseContract.ClusterEntry;
import hemal.t.shah.expensetracker.interfaces.OnCluster;
import hemal.t.shah.expensetracker.pojo.ClusterParcelable;

/**
 * Adapter to show personal clusters in the main screen of the tabs.
 * Created by Hemal Shah on 10/11/16.
 */
public class PersonalClusterAdapter
        extends CursorRecyclerViewAdapter<PersonalClusterAdapter.ViewHolder> {

    private static final String TAG = "PersonalClusterAdapter";
    private Context context = null;
    private OnCluster onCluster = null;

    public PersonalClusterAdapter(Context context, Cursor cursor, OnCluster onCluster) {
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
            String title =
                    cursor.getString(index_title);

            long startTime = cursor.getLong(index_timestamp);
            String timeStamp = "";
            try {
                timeStamp = TimeTravel.getTimeElapsed(startTime, System.currentTimeMillis());
            } catch (TimeTravelException e) {
                e.printStackTrace();
            }

            int id = cursor.getInt(index_id);
            String cluster_key = cursor.getString(index_firebase_key);
            final ClusterParcelable cluster = new ClusterParcelable(
                    title, null, cluster_key, 0, startTime
            );

            //setting the title
            viewHolder.title.setText(cluster.getTitle());

            //setting the timestamp
            viewHolder.timeStamp.setText(timeStamp);

            //setting action to perform on delete click..
            viewHolder.delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (onCluster != null) {
                        onCluster.onDelete(cluster);
                    }
                }
            });

            viewHolder.card.setOnClickListener(new OnClickListener() {
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
                LayoutInflater.from(this.context).inflate(R.layout.row_personal_clusters, parent,
                        false);
        return new ViewHolder(itemView);
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.ll_p_cluster)
        LinearLayout card;

        @BindView(R.id.tv_p_cluster_title)
        TextView title;

        @BindView(R.id.tv_p_cluster_timestamp)
        TextView timeStamp;

        @BindView(R.id.ib_delete_p_cluster)
        ImageButton delete;

        ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
