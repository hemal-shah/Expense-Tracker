package hemal.t.shah.expensetracker.data;

import android.content.AsyncQueryHandler;
import android.content.ContentResolver;
import android.content.Context;
import android.util.Log;

import hemal.t.shah.expensetracker.utils.SharedConstants;

/**
 * Use this query handler to delete clusters.
 * Created by hemal on 21/12/16.
 */
public class DataDispenser extends AsyncQueryHandler {

    private static final String TAG = "DataDispenser";

    private Context context = null;

    public DataDispenser(ContentResolver cr, Context context) {
        super(cr);
        this.context = context;
    }

    @Override
    protected void onDeleteComplete(int token, Object cookie, int result) {
        super.onDeleteComplete(token, cookie, result);
        switch (token) {
            case SharedConstants.TOKEN_DELETE_CLUSTER:
                // TODO: 20/1/17 try to change to snackbar
                Log.i(TAG, "onDeleteComplete: ");
                break;
            case SharedConstants.TOKEN_DELETE_EXPENSES:
                Log.i(TAG, "onDeleteComplete: complete");
                break;
            case SharedConstants.TOKEN_CLEAR_TABLE_CLUSTER:
                //do nothing
                break;
            case SharedConstants.TOKEN_CLEAR_TABLE_EXPENSE:
                // do nothing...
                break;
        }
    }
}

