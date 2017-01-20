package hemal.t.shah.expensetracker.adapters;

import android.content.Context;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.hemal.shah.TimeTravel;
import com.hemal.shah.TimeTravelException;
import com.squareup.picasso.Picasso;

import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;
import hemal.t.shah.expensetracker.R;
import hemal.t.shah.expensetracker.data.ExpenseContract.ExpenseEntry;
import hemal.t.shah.expensetracker.interfaces.OnExpense;
import hemal.t.shah.expensetracker.pojo.ExpenseParcelable;
import hemal.t.shah.expensetracker.pojo.FirebaseUserDetails;

/**
 * Created by hemal on 29/12/16.
 */
public class SharedExpensesAdapter
        extends CursorRecyclerViewAdapter<SharedExpensesAdapter.ViewHolder> {

    private Context mContext;
    private OnExpense mExpense;

    private FirebaseUser mFirebaseUser;

    public SharedExpensesAdapter(Context context, Cursor cursor, OnExpense mExpense) {
        super(context, cursor);
        this.mContext = context;
        this.mExpense = mExpense;
        mFirebaseUser = FirebaseAuth.getInstance().getCurrentUser();
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, Cursor cursor, int position) {

        int index_about = cursor.getColumnIndex(ExpenseEntry.COLUMN_ABOUT);
        int index_amount = cursor.getColumnIndex(ExpenseEntry.COLUMN_AMOUNT);
        int index_timestamp = cursor.getColumnIndex(ExpenseEntry.COLUMN_TIMESTAMP);
        int index_user_key = cursor.getColumnIndex(ExpenseEntry.COLUMN_BY_FIREBASE_USER_UID);
        int index_cluster_key = cursor.getColumnIndex(ExpenseEntry.FIREBASE_CLUSTER_KEY);
        int index_user_name = cursor.getColumnIndex(ExpenseEntry.COLUMN_FIREBASE_USER_NAME);
        int index_user_url = cursor.getColumnIndex(ExpenseEntry.COLUMN_FIREBASE_USER_URL);
        int index_user_email = cursor.getColumnIndex(ExpenseEntry.COLUMN_FIREBASE_USER_EMAIL);
        int index_expense_key = cursor.getColumnIndex(ExpenseEntry.COLUMN_FIREBASE_EXPENSE_KEY);
        int index_description = cursor.getColumnIndex(ExpenseEntry.COLUMN_DESCRIBE);

        if (cursor.moveToPosition(position)) {
            String about = cursor.getString(index_about);
            double amount = cursor.getDouble(index_amount);

            long startTime = cursor.getLong(index_timestamp);
            String cluster_key = cursor.getString(index_cluster_key);
            String user_key = cursor.getString(index_user_key);
            String name = cursor.getString(index_user_name);
            String email = cursor.getString(index_user_email);
            String expense_key = cursor.getString(index_expense_key);
            String url = cursor.getString(index_user_url);
            String description = cursor.getString(index_description);

            String timeStamp;
            try {
                timeStamp = TimeTravel.getTimeElapsed(startTime, System.currentTimeMillis());
            } catch (TimeTravelException e) {
                timeStamp = viewHolder.WRONG_TIME;
            }

            FirebaseUserDetails userDetails = new FirebaseUserDetails(
                    name, email, user_key, url
            );

            final ExpenseParcelable expense =
                    new ExpenseParcelable(about, cluster_key, user_key, userDetails, amount,
                            expense_key, description, startTime);


            viewHolder.about.setText(about);
            viewHolder.amount.setText(String.valueOf(amount));
            viewHolder.description.setText(description);
            viewHolder.timeStamp.setText(timeStamp);
            viewHolder.user_name.setText(name);
            Picasso.with(mContext)
                    .load(url)
                    .placeholder(R.mipmap.ic_launcher)
                    .error(R.mipmap.ic_launcher)
                    .into(viewHolder.user_photo);

            /**
             * We want to enable the delete button only when the expense
             * is created by the signed in user only.
             * I.E. No one should be able to delete other users' expenses.
             */
            if (mFirebaseUser != null && mFirebaseUser.getEmail().equals(email)) {
                viewHolder.delete.setVisibility(View.VISIBLE);
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
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView =
                LayoutInflater.from(this.mContext).inflate(R.layout.row_shared_expenses, parent,
                        false);

        return new ViewHolder(itemView);
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.tv_user_name)
        TextView user_name;

        @BindView(R.id.iv_user_profile)
        ImageView user_photo;

        @BindView(R.id.tv_about_s_expenses)
        TextView about;

        @BindView(R.id.tv_description_s_expenses)
        TextView description;

        @BindView(R.id.tv_amount_s_expenses)
        TextView amount;

        @BindView(R.id.tv_time_s_expenses)
        TextView timeStamp;

        @BindView(R.id.ib_delete_s_expenses)
        ImageView delete;

        @BindString(R.string.inappropriate_time)
        String WRONG_TIME;

        ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
