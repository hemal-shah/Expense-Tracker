package hemal.t.shah.expensetracker;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
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
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.DatabaseReference.CompletionListener;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import hemal.t.shah.expensetracker.data.DataInsertionTask;
import hemal.t.shah.expensetracker.data.ExpenseContract;
import hemal.t.shah.expensetracker.data.ExpenseContract.ExpenseEntry;
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

    @BindString(R.string.add)
    String ADD;

    @BindString(R.string.error_string_length)
    String ERROR_STRING;

    @BindString(R.string.enter_amount)
    String ENTER_AMOUNT;

    @BindString(R.string.add_expense)
    String ADD_EXPENSE;

    @BindString(R.string.cancel)
    String CANCEL;

    DatabaseReference reference;
    FirebaseUser user;

    //stores the cluster_key from where it is derived!
    String cluster_key = "";
    int is_shared;

    ClusterParcelable clusterParcelable;

    FragmentManager manager;
    FragmentTransaction transaction;

    @SuppressLint("CommitTransaction")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_expenses_loader);

        ButterKnife.bind(this);

        setSupportActionBar(toolbar);

        reference = FirebaseDatabase.getInstance().getReference();
        user = FirebaseAuth.getInstance().getCurrentUser();

        Intent intent = getIntent();
        clusterParcelable = intent.getExtras().getParcelable(SharedConstants.SHARE_CLUSTER_PARCEL);

        mActionBar = getSupportActionBar();
        if (mActionBar != null) {
            mActionBar.setHomeAsUpIndicator(R.drawable.ic_clear_white_24dp);
            mActionBar.setDisplayHomeAsUpEnabled(true);
            mActionBar.setTitle(clusterParcelable.getTitle());
        }

        manager = getSupportFragmentManager();
        transaction = manager.beginTransaction();

        Bundle bundle = new Bundle();
        bundle.putParcelable(SharedConstants.SHARE_CLUSTER_PARCEL, clusterParcelable);

        cluster_key = clusterParcelable.getFirebase_cluster_id();
        is_shared = clusterParcelable.getIs_shared();

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

    @OnClick(R.id.fab_activity_expenses_loader)
    public void addNewExpense() {


        View dialogView = getLayoutInflater().inflate(R.layout.dialog_new_expense, null);
        final TextInputEditText et_about =
                (TextInputEditText) dialogView.findViewById(R.id.tiet_about_dialog);
        final TextInputEditText et_amount =
                (TextInputEditText) dialogView.findViewById(R.id.tiet_amount_dialog);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(ADD_EXPENSE)
                .setCancelable(true)
                .setPositiveButton(ADD, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String about = et_about.getText().toString();
                        if (about.length() < 3 || about.length() > 15) {
                            // TODO: 17/12/16 add snackbar here
                            Toast.makeText(ExpensesActivity.this, ERROR_STRING,
                                    Toast.LENGTH_SHORT).show();
                            dialog.dismiss();
                        }

                        String amount_string = et_amount.getText().toString();
                        if (amount_string.length() < 0) {
                            // TODO: 17/12/16 add snackbar here
                            Toast.makeText(ExpensesActivity.this, ENTER_AMOUNT, Toast.LENGTH_SHORT).show();
                            dialog.dismiss();
                        }

                        double amount = Double.parseDouble(amount_string);

                        addExpense(about, amount);
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

    private void addExpense(String about, double amount) {

        long timeStamp = System.currentTimeMillis();

        String expense_key = reference.push().getKey();

        ContentValues contentValues = new ContentValues();
        contentValues.put(ExpenseEntry.COLUMN_ABOUT, about);
        contentValues.put(ExpenseEntry.COLUMN_AMOUNT, amount);
        contentValues.put(ExpenseEntry.COLUMN_FOREIGN_CLUSTER_ID,
                clusterParcelable.getOffline_id());
        contentValues.put(ExpenseEntry.FIREBASE_CLUSTER_KEY, cluster_key);
        contentValues.put(ExpenseEntry.COLUMN_FIREBASE_EXPENSE_KEY, expense_key);
        contentValues.put(ExpenseEntry.COLUMN_BY_FIREBASE_USER_UID, user.getUid());
        contentValues.put(ExpenseEntry.COLUMN_FIREBASE_USER_EMAIL, user.getEmail());
        contentValues.put(ExpenseEntry.COLUMN_FIREBASE_USER_NAME, user.getDisplayName());
        contentValues.put(ExpenseEntry.COLUMN_FIREBASE_USER_URL, String.valueOf(user.getPhotoUrl()));
        contentValues.put(ExpenseContract.ExpenseEntry.COLUMN_TIMESTAMP, timeStamp);

        DataInsertionTask task = new DataInsertionTask(getContentResolver(), ExpensesActivity.this);
        task.startInsert(SharedConstants.TOKEN_ADD_NEW_EXPENSE, null,
                ExpenseContract.ExpenseEntry.CONTENT_URI, contentValues);


        Map<String, Object> expense = new HashMap<>();
        expense.put(SharedConstants.FIREBASE_ABOUT, about);
        expense.put(SharedConstants.FIREBASE_AMOUNT, amount);
//        expense.put(SharedConstants.FIREBASE_BY_USER, user);
        // TODO: 7/1/17 add user details here
        expense.put(SharedConstants.FIREBASE_TIME_STAMP, timeStamp);

        if (is_shared == 0) {
            reference.child(SharedConstants.FIREBASE_PATH_PERSONAL_CLUSTERS)
                    .child(user.getUid())
                    .child(cluster_key)
                    .child(SharedConstants.FIREBASE_EXPENSES)
                    .child(expense_key)
                    .updateChildren(expense, new CompletionListener() {
                        // TODO: 7/1/17 remove
                        @Override
                        public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                            Toast.makeText(ExpensesActivity.this, "Added!", Toast.LENGTH_SHORT).show();
                        }
                    });
        } else if (is_shared == 1) {
            reference.child(SharedConstants.FIREBASE_PATH_SHARED_CLUSTERS)
                    .child(cluster_key)
                    .child(SharedConstants.FIREBASE_EXPENSES)
                    .child(reference.push().getKey())
                    .updateChildren(expense, new CompletionListener() {
                        // TODO: 7/1/17 remove
                        @Override
                        public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                            Toast.makeText(ExpensesActivity.this, "Added!", Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish(); // close this activity as oppose to navigating up
        return false;
    }
}
