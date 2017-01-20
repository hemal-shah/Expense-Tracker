package hemal.t.shah.expensetracker.interfaces;

import hemal.t.shah.expensetracker.pojo.ClusterParcelable;

/**
 * Interface providing functionality to react to cluster delete action.
 * Created by hemal on 21/12/16.
 */
public interface OnCluster {

    void onDelete(ClusterParcelable cluster);

    void onAddParticipant(ClusterParcelable clusterParcelable);
    void onTouch(ClusterParcelable cluster);
}
