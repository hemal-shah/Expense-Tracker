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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.hemal.shah.TimeTravel;
import com.hemal.shah.TimeTravelException;
import hemal.t.shah.expensetracker.R;
import hemal.t.shah.expensetracker.data.ExpenseContract;
import hemal.t.shah.expensetracker.interfaces.OnExpense;
import hemal.t.shah.expensetracker.pojo.ExpenseParcelable;

/**
 * Adapter for expenses in personal clusters.
 * Created by hemal on 29/12/16.
 */
public class PersonalExpensesAdapter
    extends CursorRecyclerViewAdapter<PersonalExpensesAdapter.ViewHolder> {

  Context mContext;
  Cursor mCursor;
  FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
  OnExpense mExpense;

  public PersonalExpensesAdapter(Context context, Cursor cursor, OnExpense mExpense) {
    super(context, cursor);
    this.mContext = context;
    this.mCursor = cursor;
    this.mExpense = mExpense;
  }

  @Override public void onBindViewHolder(ViewHolder viewHolder, Cursor cursor, int position) {
    int index_about, index_amount, index_timestamp, index_cluster_id, index_user_id;

    index_about = cursor.getColumnIndex(ExpenseContract.ExpenseEntry.COLUMN_ABOUT);
    index_amount = cursor.getColumnIndex(ExpenseContract.ExpenseEntry.COLUMN_AMOUNT);
    index_timestamp = cursor.getColumnIndex(ExpenseContract.ExpenseEntry.COLUMN_TIMESTAMP);
    index_cluster_id =
        cursor.getColumnIndex(ExpenseContract.ExpenseEntry.COLUMN_FOREIGN_CLUSTER_ID);
    index_user_id = cursor.getColumnIndex(ExpenseContract.ExpenseEntry.COLUMN_BY_USER);

    if (cursor.moveToPosition(position)) {
      String about = cursor.getString(index_about);
      double amount = cursor.getDouble(index_amount);
      long starttime = cursor.getLong(index_timestamp);
      int cluster_id = cursor.getInt(index_cluster_id);
      int user_id = cursor.getInt(index_user_id);

      String timeStamp = "";
      try {
        timeStamp = TimeTravel.getTimeElapsed(starttime, System.currentTimeMillis());
      } catch (TimeTravelException e) {
        e.printStackTrace();
      }

      final ExpenseParcelable expense =
          new ExpenseParcelable(about, timeStamp, amount, cluster_id, user_id);

      String text = about + "\n" + amount + "\n" + timeStamp + "\n" + user_id + "\n";
      if (user != null) {
        text += "Name = " + user.getDisplayName() + "\n email = " + user.getEmail();
      }
      viewHolder.tv.setText(text);

      viewHolder.delete.setOnClickListener(new View.OnClickListener() {
        @Override public void onClick(View v) {
          if (mExpense != null) {
            mExpense.delete(expense);
          }
        }
      });
    }
  }

  @Override public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

    View itemView =
        LayoutInflater.from(this.mContext).inflate(R.layout.row_personal_expenses, parent, false);

    return new ViewHolder(itemView);
  }

  public class ViewHolder extends RecyclerView.ViewHolder {

    @BindView(R.id.bt_delete_p_expense) Button delete;

    @BindView(R.id.tv_sample) TextView tv;

    public ViewHolder(View itemView) {
      super(itemView);
      ButterKnife.bind(this, itemView);
    }
  }
}
