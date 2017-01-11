package hemal.t.shah.expensetracker;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.ui.ResultCodes;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuth.AuthStateListener;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Arrays;

import hemal.t.shah.expensetracker.data.DataDispenser;
import hemal.t.shah.expensetracker.data.DataInsertionTask;
import hemal.t.shah.expensetracker.data.ExpenseContract.ClusterEntry;
import hemal.t.shah.expensetracker.data.ExpenseContract.ExpenseEntry;
import hemal.t.shah.expensetracker.fragment.TabContainerFragment;
import hemal.t.shah.expensetracker.pojo.ClusterParcelable;
import hemal.t.shah.expensetracker.pojo.ExpenseParcelable;
import hemal.t.shah.expensetracker.utils.PreferenceManager;
import hemal.t.shah.expensetracker.utils.SharedConstants;

/**
 * Entry point for the application for now.
 */
public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private static final int RC_SIGN_IN = 123;
    private FirebaseAuth mFirebaseAuth;
    private AuthStateListener mAuthStateListener;
    private FirebaseUser user;

    private Context mContext;

    private ValueEventListener personalClusterEventListener;
    private DatabaseReference reference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mContext = this;


        mFirebaseAuth = FirebaseAuth.getInstance();
        mAuthStateListener = new AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                user = firebaseAuth.getCurrentUser();

                if (user != null) {
                    //User signed in...

                    reference = FirebaseDatabase.getInstance().getReference();
                    loadInitialDataOnSignIn(user);

                    FragmentManager manager = getSupportFragmentManager();
                    manager.beginTransaction()
                            .replace(R.id.fragment_activity_main, new TabContainerFragment())
                            .commit();
                } else {
                    //User not signed in.
                    startActivityForResult(AuthUI.getInstance()
                                    .createSignInIntentBuilder()
                                    .setTheme(R.style.FirebaseUITheme)
                                    .setIsSmartLockEnabled(!BuildConfig.DEBUG)
                                    .setProviders(Arrays.asList(
                                            new AuthUI.IdpConfig.Builder(AuthUI.EMAIL_PROVIDER)
                                                    .build(),
                                            new AuthUI.IdpConfig.Builder(AuthUI.GOOGLE_PROVIDER)
                                                    .build())
                                    )
                                    .setLogo(R.mipmap.ic_launcher)
                                    .build()
                            , RC_SIGN_IN);
                }
            }
        };
    }


    @Override
    protected void onPause() {
        super.onPause();
        mFirebaseAuth.removeAuthStateListener(mAuthStateListener);
        if (personalClusterEventListener != null && reference != null && user != null) {
            reference.child(SharedConstants.FIREBASE_PATH_PERSONAL_CLUSTERS)
                    .child(user.getUid())
                    .removeEventListener(personalClusterEventListener);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        mFirebaseAuth.addAuthStateListener(mAuthStateListener);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main_activity, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            if (resultCode == RESULT_OK) {

                FragmentManager manager = getSupportFragmentManager();
                manager.beginTransaction()
                        .replace(R.id.fragment_activity_main, new TabContainerFragment())
                        .commit();

            } else if (resultCode == RESULT_CANCELED) {

                Toast.makeText(MainActivity.this, "Error!", Toast.LENGTH_SHORT).show();

            } else if (resultCode == ResultCodes.RESULT_NO_NETWORK) {

                Toast.makeText(MainActivity.this, "No network!", Toast.LENGTH_SHORT).show();

            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_settings:
                // TODO: 22/12/16 redirect to settings menu
                return true;

            case R.id.sign_out:
                mFirebaseAuth.signOut();
                dataCleanUpOnSignOut();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }


    public void loadInitialDataOnSignIn(final FirebaseUser user) {

        personalClusterEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if (PreferenceManager.checkInitialDataLoaded(mContext)) {
                    //We don't want to load the initial data again and again.
                    //hence if once we have loaded it, we return from here.
                    return;
                }


                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String cluster_key = snapshot.getKey();
                    String title = "";
                    long timeStamp = 0;
                    ArrayList<ExpenseParcelable> expensesList = new ArrayList<>();

                    for (DataSnapshot personalCluster : snapshot.getChildren()) {

                        //Key will be either timestamp, expenses, or title
                        String keyTerm = personalCluster.getKey();

                        if (keyTerm.equalsIgnoreCase(
                                SharedConstants.FIREBASE_TITLE)) {

                            //title of the personal cluster!
                            title = personalCluster.getValue().toString();

                        } else if (keyTerm.equalsIgnoreCase(SharedConstants.FIREBASE_TIME_STAMP)) {

                            //timestamp when personal cluster was created!
                            timeStamp = Long.valueOf(personalCluster.getValue().toString());

                        } else if (keyTerm.equalsIgnoreCase(SharedConstants.FIREBASE_EXPENSES)) {
                            for (DataSnapshot expenses : personalCluster.getChildren()) {

                                //expense key of the expense in given cluster #{cluster_key}
                                String expense_key = expenses.getKey();

                                //What the expense is about.
                                String about = expenses
                                        .child(SharedConstants.FIREBASE_ABOUT).getValue()
                                        .toString();

                                //the amount in the expense
                                double amount = Double.valueOf(
                                        expenses
                                                .child(SharedConstants.FIREBASE_AMOUNT)
                                                .getValue().toString()
                                );

                                //When the expense happened
                                long expenseTimeStamp = Long.valueOf(
                                        expenses
                                                .child(SharedConstants.FIREBASE_TIME_STAMP)
                                                .getValue().toString()
                                );

                                //Adding those values to the expenses array list
                                expensesList.add(new ExpenseParcelable(
                                        about, cluster_key, user.getUid(), amount, expense_key,
                                        expenseTimeStamp
                                ));
                            }
                        }
                    }

                    ClusterParcelable parcel = new ClusterParcelable(
                            title, user.getUid(), cluster_key, 0, timeStamp
                    );

                    addInitialDataToDatabase(parcel, expensesList);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(MainActivity.this,
                        "Error happened while syncing data",
                        Toast.LENGTH_SHORT).show();
            }
        };


        reference.child(SharedConstants.FIREBASE_PATH_PERSONAL_CLUSTERS)
                .child(user.getUid())
                .addValueEventListener(personalClusterEventListener);
    }


    private void dataCleanUpOnSignOut() {

        DataDispenser dispenser = new DataDispenser(getContentResolver(),
                this);
        dispenser.startDelete(
                SharedConstants.TOKEN_CLEAR_TABLE_CLUSTER,
                null,
                ClusterEntry.CONTENT_URI,
                null,
                null
        );

        dispenser.startDelete(
                SharedConstants.TOKEN_CLEAR_TABLE_EXPENSE,
                null,
                ExpenseEntry.CONTENT_URI,
                null,
                null
        );

        PreferenceManager.setInitialDataLoad(mContext, false);
    }


    /**
     * Adds cluster and expenses in that cluster to offline database.
     */
    public void addInitialDataToDatabase(ClusterParcelable cluster,
            ArrayList<ExpenseParcelable> expenses) {

        ContentValues values = new ContentValues();
        values.put(ClusterEntry.COLUMN_TIMESTAMP, cluster.getTimeStamp());
        values.put(ClusterEntry.COLUMN_IS_SHARED, cluster.getIs_shared());
        values.put(ClusterEntry.COLUMN_TITLE, cluster.getTitle());
        values.put(ClusterEntry.COLUMN_FIREBASE_CLUSTER_KEY, cluster.getFirebase_cluster_id());

        DataInsertionTask task = new DataInsertionTask(getContentResolver(), this);

        task.startInsert(
                SharedConstants.TOKEN_DELETE_CLUSTER,
                null,
                ClusterEntry.CONTENT_URI,
                values
        );

        for (ExpenseParcelable expense : expenses) {

            ContentValues value = new ContentValues();
            value.put(ExpenseEntry.FIREBASE_CLUSTER_KEY, expense.getFirebase_cluster_ref_key());
            if (user.getPhotoUrl() != null) {
                value.put(ExpenseEntry.COLUMN_FIREBASE_USER_URL, user.getPhotoUrl().toString());
            } else {
                value.put(ExpenseEntry.COLUMN_FIREBASE_USER_URL, "");
            }
            value.put(ExpenseEntry.COLUMN_FIREBASE_EXPENSE_KEY, expense.getFirebase_expense_key());
            value.put(ExpenseEntry.COLUMN_ABOUT, expense.getAbout());
            value.put(ExpenseEntry.COLUMN_FIREBASE_USER_NAME, user.getDisplayName());

            value.put(ExpenseEntry.COLUMN_AMOUNT, expense.getAmount());
            value.put(ExpenseEntry.COLUMN_FIREBASE_USER_EMAIL, user.getEmail());
            value.put(ExpenseEntry.COLUMN_BY_FIREBASE_USER_UID, user.getUid());
            value.put(ExpenseEntry.COLUMN_TIMESTAMP, expense.getTimeStamp());

            task.startInsert(
                    SharedConstants.TOKEN_ADD_NEW_EXPENSE,
                    null,
                    ExpenseEntry.CONTENT_URI,
                    value
            );
        }

        PreferenceManager.setInitialDataLoad(mContext, true);
    }

}
