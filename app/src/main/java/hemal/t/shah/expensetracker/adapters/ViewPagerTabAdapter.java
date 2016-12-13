package hemal.t.shah.expensetracker.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import hemal.t.shah.expensetracker.fragment.PersonalExpensesFragment;

/**
 * View Pager Adapter for "Personal" & "Shared" fragments.
 * Created by hemal on 13/12/16.
 */
public class ViewPagerTabAdapter extends FragmentPagerAdapter {

    //Titles of tabs.
    private static String[] tabTitles = {"Personal", "Shared"};

    public ViewPagerTabAdapter(FragmentManager fm) {
        super(fm);
    }

    /**
     * Returns apt fragment object based on tab position.
     * @param position equals to $tabs_count - 1
     * @return
     */
    @Override
    public Fragment getItem(int position) {
        switch (position){
            case 0:
                return new PersonalExpensesFragment();
            case 1:
                return new PersonalExpensesFragment();
        }
        return null;
    }

    @Override
    public int getCount() {
        return tabTitles.length;
    }

    /**
     * Indicates title inside tabs.
     * @param position Tab Position
     * @return Title
     */
    @Override
    public CharSequence getPageTitle(int position) {
        return tabTitles[position];
    }
}
