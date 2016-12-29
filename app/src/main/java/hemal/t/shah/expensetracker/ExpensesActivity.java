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

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import hemal.t.shah.expensetracker.data.DataInsertionTask;
import hemal.t.shah.expensetracker.data.ExpenseContract;
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

        Intent intent = getIntent();
        clusterParcelable = intent.getExtras().getParcelable(
                SharedConstants.SHARE_CLUSTER_PARCEL);


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

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Add new Expense");
        builder.setCancelable(true);
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_new_expense, null);
        final TextInputEditText et_about = (TextInputEditText) dialogView.findViewById(
                R.id.tiet_about_dialog);
        final TextInputEditText et_amount = (TextInputEditText) dialogView.findViewById(
                R.id.tiet_amount_dialog);

        builder.setPositiveButton(
                "Add!",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String about = et_about.getText().toString();
                        if (about.length() < 3 || about.length() > 15) {
                            // TODO: 17/12/16 add snackbar here
                            Toast.makeText(ExpensesActivity.this,
                                    "Length should be between 3 to 15 characters.",
                                    Toast.LENGTH_SHORT).show();
                            return;
                        }

                        String amount_string = et_amount.getText().toString();
                        if (amount_string.length() < 0) {
                            // TODO: 17/12/16 add snackbar here
                            Toast.makeText(ExpensesActivity.this,
                                    "Enter amount",
                                    Toast.LENGTH_SHORT).show();
                            return;
                        }

                        double amount = Double.parseDouble(amount_string);

                        // TODO: 30/12/16 handle time
                        String time = "time is new...";

                        ContentValues contentValues = new ContentValues();
                        contentValues.put(
                                ExpenseContract.ExpenseEntry.COLUMN_ABOUT, about
                        );
                        contentValues.put(ExpenseContract.ExpenseEntry.COLUMN_AMOUNT, amount);
                        // TODO: 30/12/16 change to user id here.
                        contentValues.put(ExpenseContract.ExpenseEntry.COLUMN_FOREIGN_BY_USER, 102);
                        contentValues.put(ExpenseContract.ExpenseEntry.COLUMN_FOREIGN_CLUSTER_ID,
                                clusterParcelable.getId());
                        contentValues.put(ExpenseContract.ExpenseEntry.COLUMN_TIMESTAMP, time);

                        DataInsertionTask task = new DataInsertionTask(getContentResolver(),
                                ExpensesActivity.this);
                        task.startInsert(
                                SharedConstants.TOKEN_ADD_NEW_EXPENSE,
                                null, ExpenseContract.ExpenseEntry.CONTENT_URI,
                                contentValues
                        );
                    }
                }
        );

        builder.setNegativeButton(
                "Cancel",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }
        );
        builder.setView(dialogView);
        builder.create().show();

    }

    @Override
    public boolean onSupportNavigateUp() {
        finish(); // close this activity as oppose to navigating up
        return false;
    }
}
