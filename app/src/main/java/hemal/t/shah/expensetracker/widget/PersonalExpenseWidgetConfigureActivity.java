package hemal.t.shah.expensetracker.widget;

import android.app.Activity;
import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CursorAdapter;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

import hemal.t.shah.expensetracker.R;
import hemal.t.shah.expensetracker.data.ExpenseContract;

/**
 * The configuration screen for the {@link PersonalExpenseWidget PersonalExpenseWidget} AppWidget.
 */
public class PersonalExpenseWidgetConfigureActivity extends Activity {

    private static final String PREFS_NAME =
            "hemal.t.shah.expensetracker.widget.PersonalExpenseWidget";

    private static final String PREF_PREFIX_KEY = "appwidget_";
    int mAppWidgetId = AppWidgetManager.INVALID_APPWIDGET_ID;

    ListView mListView;

    public PersonalExpenseWidgetConfigureActivity() {
        super();
    }

    /**
     * Write the key of selected cluster to shared preferences under the title
     * appwidget_{@literal appWidgetId}
     *
     * @param context     Context
     * @param appWidgetId Unique id generated for each widget.
     * @param key         the key to store
     */
    static void saveTitleKey(Context context, int appWidgetId, String key) {
        SharedPreferences.Editor prefs = context.getSharedPreferences(PREFS_NAME, 0).edit();
        prefs.putString(PREF_PREFIX_KEY + appWidgetId, key);
        prefs.apply();
    }

    /**
     * Get the stored key for particular widget.
     *
     * @param appWidgetId widget id for which cluster key is to be retrieved.
     */
    static String loadTitleKey(Context context, int appWidgetId) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, 0);
        String titleValue = prefs.getString(PREF_PREFIX_KEY + appWidgetId, null);
        if (titleValue != null) {
            return titleValue;
        } else {
            return "";
        }
    }

    static void deleteTitleKey(Context context, int appWidgetId) {
        SharedPreferences.Editor prefs = context.getSharedPreferences(PREFS_NAME, 0).edit();
        prefs.remove(PREF_PREFIX_KEY + appWidgetId);
        prefs.apply();
    }

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);

        // Set the result to CANCELED.  This will cause the widget host to cancel
        // out of the widget placement if the user presses the back button.
        setResult(RESULT_CANCELED);

        setContentView(R.layout.personal_cluster_widget_configure);
        mListView = (ListView) findViewById(R.id.listView_widget);


        final Cursor cursor = getContentResolver().query(
                ExpenseContract.ClusterEntry.CONTENT_URI,
                null,
                ExpenseContract.ClusterEntry.COLUMN_IS_SHARED + " = 0",
                null,
                null
        );

        SimpleCursorAdapter adapter = new SimpleCursorAdapter(
                this,
                R.layout.widget_single_row,
                cursor,
                new String[]{ExpenseContract.ClusterEntry.COLUMN_TITLE},
                new int[]{R.id.tv_title_widget},
                CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER
        );

        mListView.setAdapter(adapter);

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                final Context context = PersonalExpenseWidgetConfigureActivity.this;

                String key = "";
                if (cursor != null && cursor.moveToPosition(position)) {
                    key += cursor.getString(cursor.getColumnIndex(
                            ExpenseContract.ClusterEntry.COLUMN_FIREBASE_CLUSTER_KEY));
                }


                saveTitleKey(context, mAppWidgetId, key);

                // It is the responsibility of the configuration activity to update the app widget
                AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
                PersonalExpenseWidget.updateAppWidget(context, appWidgetManager, mAppWidgetId);

                // Make sure we pass back the original appWidgetId
                Intent resultValue = new Intent();
                resultValue.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, mAppWidgetId);
                setResult(RESULT_OK, resultValue);
                finish();
            }
        });

        // Find the widget id from the intent.
        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        if (extras != null) {
            mAppWidgetId = extras.getInt(
                    AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);
        }

        // If this activity was started with an intent without an app widget ID, finish with an
        // error.
        if (mAppWidgetId == AppWidgetManager.INVALID_APPWIDGET_ID) {
            finish();
        }
    }
}

