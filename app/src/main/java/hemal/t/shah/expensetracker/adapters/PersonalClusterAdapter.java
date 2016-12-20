package hemal.t.shah.expensetracker.adapters;

import android.content.Context;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import hemal.t.shah.expensetracker.R;
import hemal.t.shah.expensetracker.data.ExpenseContract;
import hemal.t.shah.expensetracker.pojo.ClusterParcelable;

/**
 * Adapter to show personal clusters in the main screen of the tabs.
 * Created by Hemal Shah on 10/11/16.
 */
public class PersonalClusterAdapter extends
        CursorRecyclerViewAdapter<PersonalClusterAdapter.ViewHolder> {

    Cursor cursor = null;
    Context context = null;
    ArrayList<ClusterParcelable> personalClusters = null;

    private static final String TAG = "PersonalClusterAdapter";

    public PersonalClusterAdapter(Context context, Cursor cursor) {
        super(context, cursor);
        this.context = context;
        this.cursor = cursor;
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, Cursor cursor, int position) {

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
                .inflate(R.layout.single_personal_clusters_row, parent, false);
        return new ViewHolder(itemView);
    }
/*
    @Override
    public boolean onItemMove(int fromPosition, int toPosition) {
        if(fromPosition < toPosition){
            for(int i = fromPosition ; i < toPosition; i++){
                Collections.swap(this.personalClusters, i, i+1);
            }
        } else {
            for(int i = fromPosition; i > toPosition; i--){
                Collections.swap(personalClusters, i , i-1);
            }
        }
        notifyItemMoved(fromPosition, toPosition);
        return true;
    }

    @Override
    public void onItemDismiss(int position) {
        this.personalClusters.remove(position);
        notifyItemRemoved(position);
    }*/

    public class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.tv_single_row_expense)
        TextView tv;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
