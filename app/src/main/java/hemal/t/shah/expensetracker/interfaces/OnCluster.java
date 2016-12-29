package hemal.t.shah.expensetracker.interfaces;

import hemal.t.shah.expensetracker.pojo.ClusterParcelable;

/**
 * Interface providing functionality to react to cluster delete action.
 * Created by hemal on 21/12/16.
 */
public interface OnCluster {

    // TODO: 21/12/16 Later add firebase cluster id for more unique identification.
    void onDelete(int is_shared, String title);

    void onTouch(ClusterParcelable cluster);
}
