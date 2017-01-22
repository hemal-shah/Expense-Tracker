package hemal.t.shah.expensetracker.activities;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import hemal.t.shah.expensetracker.R;
import hemal.t.shah.expensetracker.data.DataInsertionTask;
import hemal.t.shah.expensetracker.data.ExpenseContract;
import hemal.t.shah.expensetracker.data.ExpenseContract.ExpenseEntry;
import hemal.t.shah.expensetracker.fragment.ExpensesFragment;
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

    private ClusterParcelable mClusterParcelable;
    private FragmentManager manager;

    @SuppressLint("CommitTransaction")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_expenses_loader);

        Intent intent = getIntent();
        mClusterParcelable = intent.getExtras().getParcelable(SharedConstants.SHARE_CLUSTER_PARCEL);

        Bundle args = new Bundle();
        args.putParcelable(SharedConstants.SHARE_CLUSTER_PARCEL, mClusterParcelable);

        Fragment fragment = new ExpensesFragment();
        fragment.setArguments(args);

        manager = getSupportFragmentManager();
        manager.beginTransaction().replace(R.id.fl_activity_expenses_loader, fragment).commit();
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return false;
    }
}
