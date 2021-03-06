package hemal.t.shah.expensetracker.activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;

import hemal.t.shah.expensetracker.R;
import hemal.t.shah.expensetracker.fragment.ExpensesFragment;
import hemal.t.shah.expensetracker.pojo.ClusterParcelable;
import hemal.t.shah.expensetracker.utils.SharedConstants;

/**
 * Loads expenses based on the is_shared properties of intent.
 * Created by hemal on 23/12/16.
 */
public class ExpensesActivity extends AppCompatActivity {

    private static final String TAG = "ExpensesActivity";

    @SuppressLint("CommitTransaction")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_expenses_loader);

        Intent intent = getIntent();
        ClusterParcelable clusterParcelable = intent.getExtras().getParcelable(
                SharedConstants.SHARE_CLUSTER_PARCEL);

        Bundle args = new Bundle();
        args.putParcelable(SharedConstants.SHARE_CLUSTER_PARCEL, clusterParcelable);

        Fragment fragment = new ExpensesFragment();
        fragment.setArguments(args);

        FragmentManager manager = getSupportFragmentManager();
        manager.beginTransaction().replace(R.id.fl_activity_expenses_loader, fragment).commit();
    }

    @Override
    public void onBackPressed() {
        finish();
        super.onBackPressed();
    }
}
