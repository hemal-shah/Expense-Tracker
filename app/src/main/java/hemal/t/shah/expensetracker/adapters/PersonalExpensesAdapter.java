package hemal.t.shah.expensetracker.adapters;

import android.content.Context;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.hemal.shah.TimeTravel;
import com.hemal.shah.TimeTravelException;

import butterknife.BindView;
import butterknife.ButterKnife;
import hemal.t.shah.expensetracker.R;
import hemal.t.shah.expensetracker.data.ExpenseContract.ExpenseEntry;
import hemal.t.shah.expensetracker.interfaces.OnExpense;
import hemal.t.shah.expensetracker.pojo.ExpenseParcelable;

/**
 * Adapter for expenses in personal clusters.
 * Created by hemal on 29/12/16.
 */
public class PersonalExpensesAdapter
        extends CursorRecyclerViewAdapter<PersonalExpensesAdapter.ViewHolder> {

    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    private Context mContext;
    private OnExpense mExpense;

    public PersonalExpensesAdapter(Context context, Cursor cursor, OnExpense mExpense) {
        super(context, cursor);
        this.mContext = context;
        this.mExpense = mExpense;
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, Cursor cursor, int position) {
        int index_about, index_amount, index_timestamp, index_cluster_key,
                index_user_key, index_user_name, index_user_email, index_user_url,
                index_expense_key, index_description;

        index_about = cursor.getColumnIndex(ExpenseEntry.COLUMN_ABOUT);
        index_amount = cursor.getColumnIndex(ExpenseEntry.COLUMN_AMOUNT);
        index_timestamp = cursor.getColumnIndex(ExpenseEntry.COLUMN_TIMESTAMP);
        index_user_key = cursor.getColumnIndex(ExpenseEntry.COLUMN_BY_FIREBASE_USER_UID);
        index_cluster_key = cursor.getColumnIndex(ExpenseEntry.FIREBASE_CLUSTER_KEY);
        index_description = cursor.getColumnIndex(ExpenseEntry.COLUMN_DESCRIBE);
        index_user_name = cursor.getColumnIndex(ExpenseEntry.COLUMN_FIREBASE_USER_NAME);
        index_user_url = cursor.getColumnIndex(ExpenseEntry.COLUMN_FIREBASE_USER_URL);
        index_user_email = cursor.getColumnIndex(ExpenseEntry.COLUMN_FIREBASE_USER_EMAIL);
        index_cluster_key = cursor.getColumnIndex(ExpenseEntry.FIREBASE_CLUSTER_KEY);
        index_expense_key = cursor.getColumnIndex(ExpenseEntry.COLUMN_FIREBASE_EXPENSE_KEY);

        if (cursor.moveToPosition(position)) {
            String about = cursor.getString(index_about);
            double amount = cursor.getDouble(index_amount);
            long starttime = cursor.getLong(index_timestamp);
            String user_id = cursor.getString(index_user_key);
            String name = cursor.getString(index_user_name);
            String cluster_key = cursor.getString(index_cluster_key);
            String email = cursor.getString(index_user_email);
            String expense_key = cursor.getString(index_expense_key);
            String url = cursor.getString(index_user_url);
            String description = cursor.getString(index_description);

            String timeStamp = "";
            try {
                timeStamp = TimeTravel.getTimeElapsed(starttime, System.currentTimeMillis());
            } catch (TimeTravelException e) {
                e.printStackTrace();
            }


            final ExpenseParcelable expense =
                    new ExpenseParcelable(about, cluster_key, user_id, amount, expense_key,
                            description);

            viewHolder.about.setText(about);
            viewHolder.amount.setText(String.valueOf(amount));
            viewHolder.time.setText(timeStamp);
            viewHolder.description.setText(description);
            viewHolder.delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mExpense != null) {
                        mExpense.delete(expense);
                    }
                }
            });
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View itemView =
                LayoutInflater.from(this.mContext).inflate(R.layout.row_personal_expenses, parent,
                        false);

        return new ViewHolder(itemView);
    }

    class ViewHolder extends RecyclerView.ViewHolder {


        @BindView(R.id.ib_delete_p_expenses)
        ImageButton delete;

        @BindView(R.id.tv_time_p_expenses)
        TextView time;

        @BindView(R.id.tv_about_p_expenses)
        TextView about;

        @BindView(R.id.tv_amount_p_expenses)
        TextView amount;

        @BindView(R.id.tv_description_p_expenses)
        TextView description;

        ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
