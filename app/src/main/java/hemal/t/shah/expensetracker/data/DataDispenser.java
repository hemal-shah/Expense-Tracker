package hemal.t.shah.expensetracker.data;

import android.content.AsyncQueryHandler;
import android.content.ContentResolver;
import android.content.Context;
import android.widget.Toast;

import hemal.t.shah.expensetracker.utils.SharedConstants;

/**
 * Use this query handler to delete clusters.
 * Created by hemal on 21/12/16.
 */
public class DataDispenser extends AsyncQueryHandler {

    Context context = null;

    public DataDispenser(ContentResolver cr, Context context) {
        super(cr);
        this.context = context;
    }

    @Override
    protected void onDeleteComplete(int token, Object cookie, int result) {
        super.onDeleteComplete(token, cookie, result);
        switch (token) {
            case SharedConstants.TOKEN_DELETE_CLUSTER:
                Toast.makeText(this.context, "Deleted the cluster!", Toast.LENGTH_SHORT).show();
                break;
            case SharedConstants.TOKEN_DELETE_EXPENSES:
                Toast.makeText(this.context, "Deleted expenses", Toast.LENGTH_SHORT).show();
        }
    }
}

