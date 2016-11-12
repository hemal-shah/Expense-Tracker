package hemal.t.shah.expensetracker;

/**
 * Created by hemal on 13/11/16.
 */
public interface ItemTouchHelperAdapter {
    boolean onItemMove(int fromPosition, int toPosition);
    void onItemDismiss(int position);
}
