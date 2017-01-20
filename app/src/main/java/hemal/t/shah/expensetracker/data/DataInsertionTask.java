package hemal.t.shah.expensetracker.data;

import android.content.AsyncQueryHandler;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.widget.Toast;

import hemal.t.shah.expensetracker.R;
import hemal.t.shah.expensetracker.data.ExpenseContract.ClusterEntry;
import hemal.t.shah.expensetracker.utils.PreferenceManager;
import hemal.t.shah.expensetracker.utils.SharedConstants;

/**
 * Call startQuery() method to start process to create new cluster.
 * It will first check if the cluster already exist by the user,
 * and if does, the process is terminated, otherwise cluster is created.
 * Created by hemal on 17/12/16.
 */


public class DataInsertionTask extends AsyncQueryHandler {

    private static final String TAG = "DataInsertionTask";
    private Context context;
    private String alreadyPresent, successCluster, successExpense;
    private ContentValues contentValues;

    public DataInsertionTask(ContentResolver cr, Context context, ContentValues contentValues) {
        super(cr);
        this.context = context;
        this.contentValues = contentValues;
        this.alreadyPresent = context.getString(R.string.title_already_present);
        this.successCluster = context.getString(R.string.success_cluster);
        this.successExpense = context.getString(R.string.success_expense);
    }

    public DataInsertionTask(ContentResolver cr, Context context) {
        super(cr);
        this.context = context;
    }

    @Override
    protected void onQueryComplete(int token, Object cookie, Cursor cursor) {
        super.onQueryComplete(token, cookie, cursor);
        if (token == SharedConstants.TOKEN_CHECK_FOR_CLUSTER_TITLE) {

            if (cursor != null && cursor.moveToPosition(0)) {
                /**
                 * If code execution enters this block, it indicates that the
                 * title is already in use, abort insertion of data.
                 */
                // TODO: 6/1/17 try to make a snackbar
                Toast.makeText(this.context, alreadyPresent, Toast.LENGTH_LONG).show();
            } else {

                /**
                 * It's safe to create new cluster with the provided title.
                 */
                this.startInsert(
                        SharedConstants.TOKEN_ADD_NEW_CLUSTER,
                        null,
                        ExpenseContract.ClusterEntry.CONTENT_URI,
                        contentValues
                );

                //Also add that key to tinyDB
                PreferenceManager.addClusterKeyToTinyDB(context,
                        contentValues.getAsString(ClusterEntry.COLUMN_FIREBASE_CLUSTER_KEY));
            }
        }
    }

    @Override
    protected void onInsertComplete(int token, Object cookie, Uri uri) {
        super.onInsertComplete(token, cookie, uri);

        // TODO: 30/12/16 Add snackbar here...
        switch (token) {
            case SharedConstants.TOKEN_ADD_NEW_CLUSTER:
                Toast.makeText(context, successCluster,
                        Toast.LENGTH_SHORT).show();
                break;
            case SharedConstants.TOKEN_ADD_NEW_EXPENSE:
                Toast.makeText(context, successExpense,
                        Toast.LENGTH_SHORT).show();
                break;
        }

    }
}
