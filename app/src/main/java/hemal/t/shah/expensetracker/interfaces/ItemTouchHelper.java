package hemal.t.shah.expensetracker.interfaces;

/**
 * Created by hemal on 13/11/16.
 */
public interface ItemTouchHelper {
    boolean onItemMove(int fromPosition, int toPosition);
    void onItemDismiss(int position);
}
