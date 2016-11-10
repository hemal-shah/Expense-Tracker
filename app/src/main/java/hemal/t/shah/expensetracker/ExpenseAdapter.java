package hemal.t.shah.expensetracker;

import android.content.Context;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import hemal.t.shah.expensetracker.Data.ExpenseContract;

/**
 * Created by hemal on 10/11/16.
 */
public class ExpenseAdapter extends CursorRecyclerViewAdapter<ExpenseAdapter.ViewHolder> {

    Cursor cursor = null;
    Context context = null;
    ArrayList<ExpenseParcelable> expenses = null;

    private static final String TAG = "ExpenseAdapter";

    public ExpenseAdapter(Context context, Cursor cursor) {
        super(context, cursor);
        this.context = context;
        this.cursor = cursor;
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, Cursor cursor, int position) {
        this.expenses = getExpenseDetails(cursor);

        Log.i(TAG, "onBindViewHolder: ");
        if(this.expenses == null || this.expenses.size() == 0) {
            Log.i(TAG, "onBindViewHolder: returning");
            return;
        }

        Log.i(TAG, "onBindViewHolder: the expenses are loaded");
        ExpenseParcelable expense = expenses.get(position);
        String sb = "About = " + expense.getAbout() +
                "\n" +
                "Amount = " + expense.getAmount();
        Log.i(TAG, "onBindViewHolder: setting text on viewholder");
        viewHolder.tv.setText(sb);
    }

    private static ArrayList<ExpenseParcelable> getExpenseDetails(Cursor cursor){

        if(cursor == null || cursor.getCount() == 0)
            return null;

        ArrayList<ExpenseParcelable> expenses = new ArrayList<>();

        cursor.moveToFirst();
        Log.i(TAG, "getExpenseDetails: cursor has data");
        do {
            String about = cursor.getString(
                    cursor.getColumnIndex(ExpenseContract.ExpenseEntry.COLUMN_ABOUT));
            int amount = cursor.getInt(
                    cursor.getColumnIndex(ExpenseContract.ExpenseEntry.COLUMN_AMOUNT));
            expenses.add(new ExpenseParcelable(about, amount));
        }while(cursor.moveToNext());
        Log.i(TAG, "getExpenseDetails: we are returning total this many items: " + expenses.size());

        return expenses;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(this.context)
                .inflate(R.layout.single_row_expense, parent, false);
        Log.i(TAG, "onCreateViewHolder: viewholder created");
        return new ViewHolder(itemView);
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        TextView tv;

        public ViewHolder(View itemView) {
            super(itemView);
            Log.i(TAG, "ViewHolder: findviewbyid...nothing important");
            tv = (TextView) itemView.findViewById(R.id.tv_single_row_expense);
        }
    }
}
