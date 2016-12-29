package hemal.t.shah.expensetracker;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import butterknife.BindView;
import butterknife.ButterKnife;
import hemal.t.shah.expensetracker.fragment.PersonalExpensesFragment;
import hemal.t.shah.expensetracker.fragment.SharedExpensesFragment;
import hemal.t.shah.expensetracker.pojo.ClusterParcelable;
import hemal.t.shah.expensetracker.utils.SharedConstants;

/**
 * Loads expenses based on the is_shared properties of intent.
 * Created by hemal on 23/12/16.
 */
public class ExpensesActivity extends AppCompatActivity {

    private static final String TAG = "ExpensesActivity";

    @BindView(R.id.toolbar_activity_expenses_loader)
    Toolbar toolbar;

    ActionBar mActionBar;

    FragmentManager manager;
    FragmentTransaction transaction;

    @SuppressLint("CommitTransaction")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_expenses_loader);

        ButterKnife.bind(this);
        setSupportActionBar(toolbar);

        Intent intent = getIntent();
        ClusterParcelable clusterParcelable = intent.getExtras().getParcelable(
                SharedConstants.SHARE_CLUSTER_PARCEL);


        mActionBar = getSupportActionBar();
        if(mActionBar != null){
            mActionBar.setHomeAsUpIndicator(R.drawable.ic_clear_white_24dp);
            mActionBar.setDisplayHomeAsUpEnabled(true);
            mActionBar.setTitle(clusterParcelable.getTitle());
        }


        manager = getSupportFragmentManager();
        transaction = manager.beginTransaction();


        Bundle bundle = new Bundle();
        bundle.putParcelable(SharedConstants.SHARE_CLUSTER_PARCEL, clusterParcelable);

        if (clusterParcelable != null && clusterParcelable.getIs_shared() == 0) {

            //It's a personal fragment.
            PersonalExpensesFragment fragment = new PersonalExpensesFragment();
            fragment.setArguments(bundle);
            transaction.replace(R.id.fl_activity_expenses_loader, fragment);

        } else if (clusterParcelable != null && clusterParcelable.getIs_shared() == 1) {

            //it's a shared fragment
            SharedExpensesFragment fragment = new SharedExpensesFragment();
            fragment.setArguments(bundle);
            transaction.replace(R.id.fl_activity_expenses_loader, fragment);

        } else {
            //Error
            this.finish();
        }
        transaction.commit();
    }


    @Override
    public boolean onSupportNavigateUp() {
        finish(); // close this activity as oppose to navigating up
        return false;
    }
}
