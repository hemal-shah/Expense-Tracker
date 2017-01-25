package hemal.t.shah.expensetracker.widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.widget.RemoteViews;

import hemal.t.shah.expensetracker.R;
import hemal.t.shah.expensetracker.activities.ExpensesActivity;
import hemal.t.shah.expensetracker.data.ExpenseContract;
import hemal.t.shah.expensetracker.pojo.ClusterParcelable;
import hemal.t.shah.expensetracker.utils.SharedConstants;

/**
 * Implementation of App Widget functionality.
 * App Widget Configuration implemented in {@link PersonalExpenseWidgetConfigureActivity}
 */
public class PersonalExpenseWidget extends AppWidgetProvider {

    private static final String TAG = "PersonalExpenseWidget";
    public static final String INTENT_ACTION =
            "hemal.t.shah.expenseprovider.widget.PersonalExpenseWidget.INTENT_ACTION";

    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
            int appWidgetId) {

        Intent intent = new Intent(context, ExpenseWidgetService.class);
        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
        intent.setData(Uri.parse(intent.toUri(Intent.URI_INTENT_SCHEME)));

        // Construct the RemoteViews object
        RemoteViews views = new RemoteViews(context.getPackageName(),
                R.layout.personal_expenses_widget);


        views.setRemoteAdapter(R.id.lv_personal_expenses_widget, intent);
        views.setEmptyView(R.id.lv_personal_expenses_widget, R.id.tv_empty_expenses_widget_layout);

        Intent openApp = new Intent(context, PersonalExpenseWidget.class);
        openApp.setAction(INTENT_ACTION);
        openApp.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                context, 0, openApp, PendingIntent.FLAG_UPDATE_CURRENT
        );

        views.setPendingIntentTemplate(R.id.lv_personal_expenses_widget, pendingIntent);

        // Instruct the widget manager to update the widget
        appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetId,
                R.id.lv_personal_expenses_widget);
        appWidgetManager.updateAppWidget(appWidgetId, views);
    }


    @Override
    public void onReceive(Context context, Intent intent) {


        if (intent.getAction().equals(INTENT_ACTION)) {
            int appWidgetId = intent.getExtras().getInt(AppWidgetManager.EXTRA_APPWIDGET_ID);
            String key = PersonalExpenseWidgetConfigureActivity.loadTitleKey(context, appWidgetId);

            Cursor cursor = context.getContentResolver().query(
                    ExpenseContract.ClusterEntry.CONTENT_URI,
                    null,
                    ExpenseContract.ClusterEntry.COLUMN_FIREBASE_CLUSTER_KEY + "= ?",
                    new String[]{key},
                    null
            );
            Intent newIntent = new Intent(context, ExpensesActivity.class);
            newIntent.putExtra(SharedConstants.SHARE_CLUSTER_PARCEL, formParcel(cursor, key));
            newIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(newIntent);
        }
        super.onReceive(context, intent);
    }

    private ClusterParcelable formParcel(Cursor cursor, String key) {
        if (cursor.moveToFirst()) {
            return new ClusterParcelable(
                    cursor.getString(
                            cursor.getColumnIndex(ExpenseContract.ClusterEntry.COLUMN_TITLE)),
                    "",
                    key,
                    0, //only personal clusters are shown...
                    0
            );
        }
        return null;
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // There may be multiple widgets active, so update all of them
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId);
        }

        super.onUpdate(context, appWidgetManager, appWidgetIds);
    }

    @Override
    public void onDeleted(Context context, int[] appWidgetIds) {
        // When the user deletes the widget, delete the preference associated with it.
        for (int appWidgetId : appWidgetIds) {
            PersonalExpenseWidgetConfigureActivity.deleteTitleKey(context, appWidgetId);
        }

        super.onDeleted(context, appWidgetIds);
    }
}

