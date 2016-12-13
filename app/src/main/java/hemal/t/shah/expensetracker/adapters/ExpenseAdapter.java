package hemal.t.shah.expensetracker.adapters;

import android.content.Context;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;

import butterknife.BindView;
import butterknife.ButterKnife;
import hemal.t.shah.expensetracker.pojo.ExpenseParcelable;
import hemal.t.shah.expensetracker.interfaces.ItemTouchHelper;
import hemal.t.shah.expensetracker.R;
import hemal.t.shah.expensetracker.data.ExpenseContract;

/**
 * Created by hemal on 10/11/16.
 */
public class ExpenseAdapter extends CursorRecyclerViewAdapter<ExpenseAdapter.ViewHolder> implements
        ItemTouchHelper {

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

        if(this.expenses == null || this.expenses.size() == 0) {
            return;
        }

        ExpenseParcelable expense = expenses.get(position);
        String sb = "About = " + expense.getAbout() +
                "\n" +
                "Amount = " + expense.getAmount();
        viewHolder.tv.setText(sb);
    }

    private static ArrayList<ExpenseParcelable> getExpenseDetails(Cursor cursor){

        if(cursor == null || cursor.getCount() == 0)
            return null;

        ArrayList<ExpenseParcelable> expenses = new ArrayList<>();

        cursor.moveToFirst();
        do {
            String about = cursor.getString(
                    cursor.getColumnIndex(ExpenseContract.ExpenseEntry.COLUMN_ABOUT));
            int amount = cursor.getInt(
                    cursor.getColumnIndex(ExpenseContract.ExpenseEntry.COLUMN_AMOUNT));
            expenses.add(new ExpenseParcelable(about, amount));
        }while(cursor.moveToNext());

        return expenses;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(this.context)
                .inflate(R.layout.single_row_expense, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public boolean onItemMove(int fromPosition, int toPosition) {
        if(fromPosition < toPosition){
            for(int i = fromPosition ; i < toPosition; i++){
                Collections.swap(this.expenses, i, i+1);
            }
        } else {
            for(int i = fromPosition; i > toPosition; i--){
                Collections.swap(expenses, i , i-1);
            }
        }
        notifyItemMoved(fromPosition, toPosition);
        return true;
    }

    @Override
    public void onItemDismiss(int position) {
        this.expenses.remove(position);
        notifyItemRemoved(position);
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        @BindView(R.id.tv_single_row_expense)
        TextView tv;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
