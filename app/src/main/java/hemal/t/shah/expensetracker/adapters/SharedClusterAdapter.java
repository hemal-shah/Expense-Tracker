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
import hemal.t.shah.expensetracker.pojo.ClusterParcelable;

/**
 * Adapter for shared clusters tabs.
 * Created by hemal on 20/12/16.
 */
public class SharedClusterAdapter extends
        CursorRecyclerViewAdapter<SharedClusterAdapter.ViewHolder> {

    Cursor cursor = null;
    Context context = null;

    private static final String TAG = "SharedClusterAdapter";

    public SharedClusterAdapter(Context context, Cursor cursor) {
        super(context, cursor);
        this.context = context;
        this.cursor = cursor;
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, Cursor cursor,
            int position) {

        ClusterParcelable cluster = null;

        if (cursor.moveToPosition(position)) {
            String title = cursor.getString(
                    cursor.getColumnIndex(ExpenseContract.ClusterEntry.COLUMN_TITLE));
            String timeStamp = cursor.getString(
                    cursor.getColumnIndex(ExpenseContract.ClusterEntry.COLUMN_TIMESTAMP));
            double sum = cursor.getDouble(
                    cursor.getColumnIndex(ExpenseContract.ClusterEntry.COLUMN_SUM));

            cluster = new ClusterParcelable(title, timeStamp, sum);
        }


        String s = null;
        if (cluster != null) {
            s = "Title = " + cluster.getTitle() +
                    "\nTimeStamp = " + cluster.getTimestamp() +
                    "\n sum = " + cluster.getSum();
        }

        viewHolder.tv.setText(s);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(this.context)
                .inflate(R.layout.single_shared_clusters_row, parent, false);
        return new ViewHolder(itemView);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {


        @BindView(R.id.tv_single_shared_clusters_row)
        TextView tv;

        @BindView(R.id.bt_delete_shared_clusters)
        Button delete;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}