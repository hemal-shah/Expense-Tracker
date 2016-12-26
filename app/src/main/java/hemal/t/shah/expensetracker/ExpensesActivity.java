package hemal.t.shah.expensetracker;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

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

    FragmentManager manager;
    FragmentTransaction transaction;

    @SuppressLint("CommitTransaction")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_expenses_loader);

        Intent intent = getIntent();
        ClusterParcelable clusterParcelable = intent.getExtras().getParcelable(
                SharedConstants.SHARE_CLUSTER_PARCEL);

        Log.i(TAG, "onCreate: the id received is : " + clusterParcelable.getId());
        Log.i(TAG, "onCreate: the title is " + clusterParcelable.getTitle());
        manager = getSupportFragmentManager();
        transaction = manager.beginTransaction();


        Bundle bundle = new Bundle();
        bundle.putParcelable(SharedConstants.SHARE_CLUSTER_PARCEL, clusterParcelable);

        if (clusterParcelable.getIs_shared() == 0) {

            //It's a personal fragment.
            PersonalExpensesFragment fragment = new PersonalExpensesFragment();
            fragment.setArguments(bundle);
            transaction.replace(R.id.fl_activity_expenses_loader, fragment);

        } else if (clusterParcelable.getIs_shared() == 1) {

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
}
