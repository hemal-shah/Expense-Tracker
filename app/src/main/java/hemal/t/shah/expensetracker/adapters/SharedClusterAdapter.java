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
 * Adapter for shared clusters tabs.
 * Created by hemal on 20/12/16.
 */
public class SharedClusterAdapter
    extends CursorRecyclerViewAdapter<SharedClusterAdapter.ViewHolder> {

  private static final String TAG = "SharedClusterAdapter";
  Cursor cursor = null;
  Context context = null;
  OnCluster onCluster = null;

  public SharedClusterAdapter(Context context, Cursor cursor, OnCluster onCluster) {
    super(context, cursor);
    this.context = context;
    this.cursor = cursor;
    this.onCluster = onCluster;
  }

  @Override public void onBindViewHolder(ViewHolder viewHolder, Cursor cursor, int position) {

    if (cursor.moveToPosition(position)) {
      String title =
          cursor.getString(cursor.getColumnIndex(ExpenseContract.ClusterEntry.COLUMN_TITLE));
      String timeStamp =
          cursor.getString(cursor.getColumnIndex(ExpenseContract.ClusterEntry.COLUMN_TIMESTAMP));

      int cluster_id = cursor.getInt(cursor.getColumnIndex(ExpenseContract.ClusterEntry._ID));

      final ClusterParcelable cluster = new ClusterParcelable(title, timeStamp, 1, cluster_id);

      String s = "Title = " + cluster.getTitle() +
          "\nTimeStamp = " + cluster.getTimestamp();

      viewHolder.tv.setText(s);
      viewHolder.delete.setOnClickListener(new View.OnClickListener() {
        @Override public void onClick(View v) {
          if (onCluster != null) {
            onCluster.onDelete(cluster);
          }
        }
      });

      viewHolder.open.setOnClickListener(new View.OnClickListener() {
        @Override public void onClick(View v) {
          if (onCluster != null) {
            onCluster.onTouch(cluster);
          }
        }
      });
    }
  }

  @Override public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    View itemView =
        LayoutInflater.from(this.context).inflate(R.layout.row_shared_clusters, parent, false);
    return new ViewHolder(itemView);
  }

  class ViewHolder extends RecyclerView.ViewHolder {

    @BindView(R.id.tv_single_shared_clusters_row) TextView tv;

    @BindView(R.id.bt_delete_shared_clusters) Button delete;

    @BindView(R.id.bt_open_shared_clusters) Button open;

    ViewHolder(View itemView) {
      super(itemView);
      ButterKnife.bind(this, itemView);
    }
  }
}
