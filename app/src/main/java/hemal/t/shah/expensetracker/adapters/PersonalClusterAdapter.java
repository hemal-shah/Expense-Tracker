package hemal.t.shah.expensetracker.adapters;

import android.content.Context;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import hemal.t.shah.expensetracker.R;
import hemal.t.shah.expensetracker.data.ExpenseContract;
import hemal.t.shah.expensetracker.interfaces.OnCluster;
import hemal.t.shah.expensetracker.pojo.ClusterParcelable;

/**
 * Adapter to show personal clusters in the main screen of the tabs.
 * Created by Hemal Shah on 10/11/16.
 */
public class PersonalClusterAdapter extends
        CursorRecyclerViewAdapter<PersonalClusterAdapter.ViewHolder> {

    Cursor cursor = null;
    Context context = null;
    OnCluster onCluster = null;

    private static final String TAG = "PersonalClusterAdapter";

    public PersonalClusterAdapter(Context context, Cursor cursor, OnCluster onCluster) {
        super(context, cursor);
        this.context = context;
        this.cursor = cursor;
        this.onCluster = onCluster;
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, Cursor cursor, int position) {

        if (cursor.moveToPosition(position)) {
            String title = cursor.getString(
                    cursor.getColumnIndex(ExpenseContract.ClusterEntry.COLUMN_TITLE));
            String timeStamp = cursor.getString(
                    cursor.getColumnIndex(ExpenseContract.ClusterEntry.COLUMN_TIMESTAMP));
            double sum = cursor.getDouble(
                    cursor.getColumnIndex(ExpenseContract.ClusterEntry.COLUMN_SUM));

            int id = cursor.getInt(cursor.getColumnIndex(ExpenseContract.ClusterEntry._ID));
            final ClusterParcelable cluster = new ClusterParcelable(title, timeStamp, 0, sum, id);

            String text =
                    "Title = " + cluster.getTitle() + "\nTimeStamp = " + cluster.getTimestamp()
                            + "\n sum = " + cluster.getSum();

            viewHolder.tv.setText(text);

            viewHolder.delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (onCluster != null) {
                        onCluster.onDelete(0, cluster.getTitle());
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
        View itemView = LayoutInflater.from(this.context)
                .inflate(R.layout.single_personal_clusters_row, parent, false);
        return new ViewHolder(itemView);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.tv_single_personal_clusters_row)
        TextView tv;

        @BindView(R.id.bt_delete_personal_clusters)
        Button delete;

        @BindView(R.id.bt_open_personal_clusters)
        Button open;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
