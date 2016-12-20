package hemal.t.shah.expensetracker.data;

import android.content.AsyncQueryHandler;
import android.content.ContentResolver;
import android.content.Context;
import android.widget.Toast;

/**
 * Use this query handler to delete clusters.
 * Created by hemal on 21/12/16.
 */
public class ClusterDispenser extends AsyncQueryHandler {

    Context context = null;

    public ClusterDispenser(ContentResolver cr, Context context) {
        super(cr);
        this.context = context;
    }

    @Override
    protected void onDeleteComplete(int token, Object cookie, int result) {
        super.onDeleteComplete(token, cookie, result);
        Toast.makeText(context, "Successfully deleted!", Toast.LENGTH_SHORT).show();
    }
}
