package hemal.t.shah.expensetracker.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import hemal.t.shah.expensetracker.R;
import hemal.t.shah.expensetracker.utils.SharedConstants;

/**
 * Created by hemal on 26/12/16.
 */
public class SharedExpensesFragment extends Fragment {

    @BindView(R.id.tv_shared_expenses_fragment)
    TextView mTextView;

    String title;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {

        Bundle arguments = getArguments();
        if (arguments != null) {
            title = arguments.getString(SharedConstants.SHARE_TITLE);
        }


        View rootView = inflater.inflate(R.layout.shared_expenses_fragment, container, false);
        ButterKnife.bind(this, rootView);


        mTextView.setText(title);

        return rootView;

    }
}
