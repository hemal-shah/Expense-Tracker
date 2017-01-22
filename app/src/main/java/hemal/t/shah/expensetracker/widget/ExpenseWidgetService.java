package hemal.t.shah.expensetracker.widget;

import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import hemal.t.shah.expensetracker.R;
import hemal.t.shah.expensetracker.data.ExpenseContract;
import hemal.t.shah.expensetracker.utils.PreferenceManager;

/**
 * Created by hemal on 23/1/17.
 */

public class ExpenseWidgetService extends RemoteViewsService {
    private static final String TAG = "ExpenseWidgetService";

    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return new ExpensesRVFactory(this.getApplicationContext(), intent);
    }

    public class ExpensesRVFactory implements RemoteViewsFactory {

        private static final String TAG = "ExpensesRVFactory";
        private Cursor cursor;
        private Context context;
        private int appWidgetId;
        private String clusterKey;

        ExpensesRVFactory(Context context, Intent intent) {
            this.context = context;
            this.appWidgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID,
                    AppWidgetManager.INVALID_APPWIDGET_ID);

            this.clusterKey = PersonalExpenseWidgetConfigureActivity.loadTitleKey(
                    context,
                    appWidgetId
            );

        }

        @Override
        public void onCreate() {
            cursor = getContentResolver().query(
                    ExpenseContract.ExpenseEntry.CONTENT_URI,
                    null,
                    ExpenseContract.ExpenseEntry.FIREBASE_CLUSTER_KEY + " = ?",
                    new String[]{clusterKey},
                    null
            );
        }

        @Override
        public void onDataSetChanged() {
            cursor = getContentResolver().query(
                    ExpenseContract.ExpenseEntry.CONTENT_URI,
                    null,
                    ExpenseContract.ExpenseEntry.FIREBASE_CLUSTER_KEY + " = ?",
                    new String[]{clusterKey},
                    null
            );
        }

        @Override
        public void onDestroy() {
            if (cursor != null) {
                cursor.close();
            }
        }

        @Override
        public int getCount() {
            return (cursor != null) ? cursor.getCount() : 0;
        }

        @Override
        public RemoteViews getViewAt(int position) {
            RemoteViews remoteViews = new RemoteViews(
                    this.context.getPackageName(), R.layout.list_item_expense);

            if (cursor != null && cursor.moveToPosition(position)) {
                String about = cursor.getString(
                        cursor.getColumnIndex(ExpenseContract.ExpenseEntry.COLUMN_ABOUT));

                String amount = PreferenceManager.getCurrency(context) + " " +
                        cursor.getDouble(
                                cursor.getColumnIndex(ExpenseContract.ExpenseEntry.COLUMN_AMOUNT));

                remoteViews.setTextViewText(R.id.about_expense_list, about);
                remoteViews.setTextViewText(R.id.amount_expense_list, amount);

                Intent fillInIntent = new Intent();
                remoteViews.setOnClickFillInIntent(R.id.ll_list_item_expense, fillInIntent);
            }

            return remoteViews;
        }


        @Override
        public RemoteViews getLoadingView() {
            return new RemoteViews(this.context.getPackageName(), R.layout.loading_view);
        }

        @Override
        public int getViewTypeCount() {
            return 1;
        }

        @Override
        public long getItemId(int position) {
            return this.cursor.getInt(
                    cursor.getColumnIndex(
                            ExpenseContract.ExpenseEntry._ID
                    )
            );
        }

        @Override
        public boolean hasStableIds() {
            return true;
        }
    }
}
