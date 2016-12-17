package hemal.t.shah.expensetracker.data;

import android.content.AsyncQueryHandler;
import android.content.ContentResolver;
import android.content.Context;
import android.net.Uri;
import android.widget.Toast;

import hemal.t.shah.expensetracker.utils.SharedConstants;

/**
 * Use this class to create new clusters, both personal and shared, by specifying
 * is_shared property.
 * Created by hemal on 17/12/16.
 */


public class NewClusterCreater extends AsyncQueryHandler {

    Context context;

    public NewClusterCreater(ContentResolver cr, Context context) {
        super(cr);
        this.context = context;
    }

    @Override
    protected void onInsertComplete(int token, Object cookie, Uri uri) {
        super.onInsertComplete(token, cookie, uri);

        if (token == SharedConstants.TOKEN_ADD_NEW_PERSONAL_CLUSTER) {
            if (uri.toString().startsWith(ExpenseContract.ClusterEntry.CONTENT_URI.toString())) {
                Toast.makeText(context, "Successfully added new cluster",
                        Toast.LENGTH_SHORT).show();
            }
        }

    }
}
