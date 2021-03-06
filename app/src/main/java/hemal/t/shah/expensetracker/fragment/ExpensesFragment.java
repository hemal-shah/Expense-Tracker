package hemal.t.shah.expensetracker.fragment;

import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import hemal.t.shah.expensetracker.pojo.ClusterParcelable;
import hemal.t.shah.expensetracker.utils.PreferenceManager;
import hemal.t.shah.expensetracker.utils.SharedConstants;

/**
 * Created by hemal on 22/1/17.
 */

public class ExpensesFragment extends Fragment {


    private static final String TAG = "ExpensesFragment";

    @BindView(R.id.toolbar_activity_expenses_loader)
    Toolbar toolbar;

    @BindString(R.string.add)
    String ADD;

    @BindString(R.string.no_description_provided)
    String NO_DESCRIPTION_PROVIDED;

    @BindString(R.string.error_string_length)
    String ERROR_STRING;

    @BindString(R.string.enter_amount)
    String ENTER_AMOUNT;

    @BindString(R.string.add_expense)
    String ADD_EXPENSE;

    @BindString(R.string.cancel)
    String CANCEL;

    private DatabaseReference reference;
    private FirebaseUser user;

    //stores the cluster_key from where it is derived!
    private String cluster_key = "";
    private int is_shared;

    private Context context;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        View baseView = inflater.inflate(R.layout.fragment_expenses_loader, container, false);
        ButterKnife.bind(this, baseView);

        context = getContext();
        AppCompatActivity appCompatActivity = (AppCompatActivity) getActivity();

        appCompatActivity.setSupportActionBar(toolbar);

        reference = FirebaseDatabase.getInstance().getReference();
        user = FirebaseAuth.getInstance().getCurrentUser();

        Bundle args = getArguments();
        ClusterParcelable clusterParcelable = args.getParcelable(
                SharedConstants.SHARE_CLUSTER_PARCEL);

        ActionBar actionBar = appCompatActivity.getSupportActionBar();
        if (actionBar != null) {
            if (!PreferenceManager.getTwoPaneMode(context)) {
                actionBar.setHomeAsUpIndicator(R.drawable.ic_arrow_back_white_24dp);
                actionBar.setDisplayHomeAsUpEnabled(true);
            }
            actionBar.setTitle(clusterParcelable.getTitle().toUpperCase());
        }

        FragmentManager manager = appCompatActivity.getSupportFragmentManager();

        Bundle bundle = new Bundle();
        bundle.putParcelable(SharedConstants.SHARE_CLUSTER_PARCEL, clusterParcelable);

        cluster_key = clusterParcelable.getFirebase_cluster_id();
        is_shared = clusterParcelable.getIs_shared();

        Fragment fragment = null;

        if (clusterParcelable.getIs_shared() == 0) {
            //It's a personal fragment.
            fragment = new PersonalExpensesFragment();
        } else if (clusterParcelable.getIs_shared() == 1) {
            //it's a shared fragment
            fragment = new SharedExpensesFragment();
        }
        String FRAGMENT_TAG = "fragment_tag_expenses";

        Fragment oldFragment = manager.findFragmentByTag(FRAGMENT_TAG);
        if (oldFragment != null) {
            manager.beginTransaction().remove(oldFragment).commit();
        }

        if (fragment != null) {
            fragment.setArguments(bundle);
            manager.beginTransaction().replace(R.id.fl_fragment_expenses_loader, fragment).commit();
        }

        return baseView;
    }

    @OnClick(R.id.fab_activity_expenses_loader)
    public void addNewExpense() {


        View dialogView = getLayoutInflater(null).inflate(R.layout.dialog_new_expense, null);

        final TextInputEditText et_about =
                (TextInputEditText) dialogView.findViewById(R.id.tiet_about_dialog);
        final TextInputEditText et_amount =
                (TextInputEditText) dialogView.findViewById(R.id.tiet_amount_dialog);
        final TextInputEditText et_description =
                (TextInputEditText) dialogView.findViewById(R.id.tiet_describe_dialog);

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(ADD_EXPENSE)
                .setCancelable(true)
                .setPositiveButton(ADD, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String about = et_about.getText().toString();
                        if (about.length() < 3 || about.length() > 15) {
                            Toast.makeText(context, ERROR_STRING,
                                    Toast.LENGTH_SHORT).show();
                            dialog.dismiss();
                        }

                        String amount_string = et_amount.getText().toString();
                        if (amount_string.length() < 0) {
                            Toast.makeText(context, ENTER_AMOUNT,
                                    Toast.LENGTH_SHORT).show();
                            dialog.dismiss();
                        }

                        double amount = Double.parseDouble(amount_string);

                        String description = et_description.getText().toString();
                        if (description.length() == 0) {
                            description = NO_DESCRIPTION_PROVIDED;
                        }

                        addExpense(about, amount, description);
                    }
                })
                .setNegativeButton(CANCEL, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .setView(dialogView)
                .create().show();
    }

    private void addExpense(String about, double amount, String description) {

        long timeStamp = System.currentTimeMillis();

        String expense_key = reference.push().getKey();

        ContentValues contentValues = new ContentValues();
        contentValues.put(ExpenseContract.ExpenseEntry.COLUMN_ABOUT, about);
        contentValues.put(ExpenseContract.ExpenseEntry.COLUMN_AMOUNT, amount);
        contentValues.put(ExpenseContract.ExpenseEntry.FIREBASE_CLUSTER_KEY, cluster_key);
        contentValues.put(ExpenseContract.ExpenseEntry.COLUMN_FIREBASE_EXPENSE_KEY, expense_key);
        contentValues.put(ExpenseContract.ExpenseEntry.COLUMN_DESCRIBE, description);
        contentValues.put(ExpenseContract.ExpenseEntry.COLUMN_BY_FIREBASE_USER_UID, user.getUid());
        contentValues.put(ExpenseContract.ExpenseEntry.COLUMN_FIREBASE_USER_EMAIL, user.getEmail());
        contentValues.put(ExpenseContract.ExpenseEntry.COLUMN_FIREBASE_USER_NAME,
                user.getDisplayName());
        contentValues.put(ExpenseContract.ExpenseEntry.COLUMN_FIREBASE_USER_URL,
                String.valueOf(user.getPhotoUrl()));
        contentValues.put(ExpenseContract.ExpenseEntry.COLUMN_TIMESTAMP, timeStamp);

        DataInsertionTask task = new DataInsertionTask(context.getContentResolver(), context);
        task.startInsert(SharedConstants.TOKEN_ADD_NEW_EXPENSE, null,
                ExpenseContract.ExpenseEntry.CONTENT_URI, contentValues);


        Map<String, Object> expense = new HashMap<>();
        expense.put(SharedConstants.FIREBASE_ABOUT, about);
        expense.put(SharedConstants.FIREBASE_AMOUNT, amount);
        expense.put(SharedConstants.FIREBASE_DESCRIPTION, description);
        expense.put(SharedConstants.FIREBASE_TIME_STAMP, timeStamp);

        if (is_shared == 0) {
            reference.child(SharedConstants.FIREBASE_PATH_PERSONAL_CLUSTERS)
                    .child(user.getUid())
                    .child(cluster_key)
                    .child(SharedConstants.FIREBASE_EXPENSES)
                    .child(expense_key)
                    .updateChildren(expense);
        } else if (is_shared == 1) {
            expense.put(SharedConstants.FIREBASE_USER_NAME, user.getDisplayName());
            expense.put(SharedConstants.FIREBASE_EMAIL, user.getEmail());
            if (user.getPhotoUrl() != null) {
                expense.put(SharedConstants.FIREBASE_PROFILE_URL, user.getPhotoUrl().toString());
            } else {
                expense.put(SharedConstants.FIREBASE_PROFILE_URL, "");
            }
            reference.child(SharedConstants.FIREBASE_PATH_SHARED_CLUSTERS)
                    .child(cluster_key)
                    .child(SharedConstants.FIREBASE_EXPENSES)
                    .child(expense_key)
                    .updateChildren(expense);
        }
    }
}
