package hemal.t.shah.expensetracker.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import butterknife.BindView;
import butterknife.ButterKnife;
import hemal.t.shah.expensetracker.R;
import hemal.t.shah.expensetracker.adapters.ViewPagerTabAdapter;

/**
 * Fragment that contains the "Personal" & "Shared" tabs for the MainActivity.
 * Created by hemal on 13/12/16.
 */
public class TabContainerFragment extends Fragment {

    @BindView(R.id.tl_activity_tab_container)
    TabLayout tabLayout;

    @BindView(R.id.vp_activity_tab_container)
    ViewPager viewPager;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {

        View base_view = inflater.inflate(R.layout.activity_tab_container,
                container, false);

        ButterKnife.bind(this, base_view);

        viewPager.setAdapter(new ViewPagerTabAdapter(getChildFragmentManager()));
        //Workaround for proper functioning of NavigationView and TabLayout
        tabLayout.post(new Runnable() {
            @Override
            public void run() {
                tabLayout.setupWithViewPager(viewPager);
            }
        });

        return base_view;
    }
}
