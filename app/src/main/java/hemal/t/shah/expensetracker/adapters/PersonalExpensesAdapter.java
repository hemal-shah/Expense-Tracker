package hemal.t.shah.expensetracker.adapters;

import android.content.Context;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.hemal.shah.TimeTravel;
import com.hemal.shah.TimeTravelException;

import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;
import hemal.t.shah.expensetracker.R;
import hemal.t.shah.expensetracker.data.ExpenseContract.ExpenseEntry;
import hemal.t.shah.expensetracker.interfaces.OnExpense;
import hemal.t.shah.expensetracker.pojo.ExpenseParcelable;
import hemal.t.shah.expensetracker.utils.PreferenceManager;

/**
 * Adapter for expenses in personal clusters.
 * Created by hemal on 29/12/16.
 */
public class PersonalExpensesAdapter
        extends CursorRecyclerViewAdapter<PersonalExpensesAdapter.ViewHolder> {

    private Context mContext;
    private OnExpense mExpense;

    private String currencySymbol;

    public PersonalExpensesAdapter(Context context, Cursor cursor, OnExpense mExpense) {
        super(context, cursor);
        this.mContext = context;
        this.mExpense = mExpense;
        this.currencySymbol = PreferenceManager.getCurrency(context);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, Cursor cursor, int position) {
        int index_about, index_amount, index_timestamp, index_cluster_key,
                index_expense_key, index_description;

        index_about = cursor.getColumnIndex(ExpenseEntry.COLUMN_ABOUT);
        index_amount = cursor.getColumnIndex(ExpenseEntry.COLUMN_AMOUNT);
        index_timestamp = cursor.getColumnIndex(ExpenseEntry.COLUMN_TIMESTAMP);
        index_description = cursor.getColumnIndex(ExpenseEntry.COLUMN_DESCRIBE);
        index_cluster_key = cursor.getColumnIndex(ExpenseEntry.FIREBASE_CLUSTER_KEY);
        index_expense_key = cursor.getColumnIndex(ExpenseEntry.COLUMN_FIREBASE_EXPENSE_KEY);

        if (cursor.moveToPosition(position)) {
            String about = cursor.getString(index_about);
            double amount = cursor.getDouble(index_amount);
            long startTime = cursor.getLong(index_timestamp);
            String cluster_key = cursor.getString(index_cluster_key);
            String expense_key = cursor.getString(index_expense_key);
            String description = cursor.getString(index_description);

            String timeStamp;
            try {
                timeStamp = TimeTravel.getTimeElapsed(startTime, System.currentTimeMillis());
            } catch (TimeTravelException e) {
                timeStamp = viewHolder.WRONG_TIME;
            }


            final ExpenseParcelable expense =
                    new ExpenseParcelable(about, cluster_key, "", amount, expense_key,
                            description);

            viewHolder.about.setText(about);
            viewHolder.amount.setText(currencySymbol + " " + amount);
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

        @BindString(R.string.inappropriate_time)
        String WRONG_TIME;

        ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
