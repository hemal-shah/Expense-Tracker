package hemal.t.shah.expensetracker.activities;

import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.ui.ResultCodes;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuth.AuthStateListener;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;
import java.util.Vector;

import butterknife.BindString;
import butterknife.ButterKnife;
import hemal.t.shah.expensetracker.BuildConfig;
import hemal.t.shah.expensetracker.R;
import hemal.t.shah.expensetracker.data.DataDispenser;
import hemal.t.shah.expensetracker.data.DataInsertionTask;
import hemal.t.shah.expensetracker.data.ExpenseContract.ClusterEntry;
import hemal.t.shah.expensetracker.data.ExpenseContract.ExpenseEntry;
import hemal.t.shah.expensetracker.fragment.ExpensesFragment;
import hemal.t.shah.expensetracker.fragment.TabContainerFragment;
import hemal.t.shah.expensetracker.interfaces.CallbackTwoPaneMode;
import hemal.t.shah.expensetracker.pojo.ClusterParcelable;
import hemal.t.shah.expensetracker.pojo.ExpenseParcelable;
import hemal.t.shah.expensetracker.pojo.FirebaseUserDetails;
import hemal.t.shah.expensetracker.settings.SettingsActivity;
import hemal.t.shah.expensetracker.utils.MyStatuses;
import hemal.t.shah.expensetracker.utils.PreferenceManager;
import hemal.t.shah.expensetracker.utils.SharedConstants;

/**
 * Entry point for the application for now.
 */
public class MainActivity extends AppCompatActivity implements CallbackTwoPaneMode {

    private static final String TAG = "MainActivity";
    private static final int RC_SIGN_IN = 123;
    private static int loadCount = 0;
    @BindString(R.string.not_connected_to_internet)
    String NOT_CONNECTED;
    @BindString(R.string.ad_unit_id)
    String ad_unit_id;
    @BindString(R.string.restoring_data)
    String RESTORING_DATA;
    @BindString(R.string.hang_on_tight)
    String HANG_ON_TIGHT;
    private FirebaseAuth mFirebaseAuth;
    private AuthStateListener mAuthStateListener;
    private FirebaseUser user;
    private Context context;
    private ValueEventListener personalClusterEventListener;
    private DatabaseReference reference;
    private ChildEventListener loadKeysOfSharedClusters;
    private ProgressDialog mProgressDialog;
    private InterstitialAd mInterstitialAd;


    private static FragmentManager manager;

    private static boolean mTwoPaneMode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);

        context = this;

        //getting instance of user.
        mFirebaseAuth = FirebaseAuth.getInstance();

        manager = getSupportFragmentManager();


        /**
         * Checking if two pane mode is enabled or not...
         */
        mTwoPaneMode = findViewById(R.id.fl_activity_expenses_loader) != null;


        PreferenceManager.setTwoPaneMode(context, mTwoPaneMode);

        //generating auth state listener for signing in...
        mAuthStateListener = new AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                user = firebaseAuth.getCurrentUser();

                if (user != null) {
                    //User is signed in...
                    reference = FirebaseDatabase.getInstance().getReference();

                    /**
                     * Load initial backup only once, it's not required everytime...
                     */
                    if (!PreferenceManager.isFirstTimeAppOpened(context)) {

                        if (PreferenceManager.isNetworkConnected(context)) {
                            mProgressDialog = new ProgressDialog(context);
                            mProgressDialog.setTitle(RESTORING_DATA);
                            mProgressDialog.setMessage(HANG_ON_TIGHT);
                            mProgressDialog.setIndeterminate(true);
                            mProgressDialog.show();

                            loadInitialPersonalDataOnSignIn(user);
                            loadInitialSharedDataOnSignIn(user);
                            PreferenceManager.setFirstTimeOpened(context, true);
                        } else {
                            MyStatuses.setPersonalClusterStatus(context,
                                    MyStatuses.STATUS_ERROR_NO_NETWORK);


                            MyStatuses.setSharedClusterStatus(context,
                                    MyStatuses.STATUS_ERROR_NO_NETWORK);


                            MyStatuses.setPersonalExpenseStatus(context,
                                    MyStatuses.STATUS_ERROR_NO_NETWORK);

                            MyStatuses.setSharedExpenseStatus(context,
                                    MyStatuses.STATUS_ERROR_NO_NETWORK);
                        }
                    }

                    manager.beginTransaction()
                            .replace(R.id.fragment_activity_main, new TabContainerFragment())
                            .commit();

                } else {

                    if (mTwoPaneMode) {
                        MainActivity.this.finish();
                    }

                    //User not signed in.
                    dataCleanUpOnSignOut();
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


        //Admob initialization here
        mInterstitialAd = new InterstitialAd(context);
        mInterstitialAd.setAdUnitId(ad_unit_id);
        mInterstitialAd.setAdListener(new AdListener() {
            @Override
            public void onAdLoaded() {
                super.onAdLoaded();
                mInterstitialAd.show();
            }
        });

        requestNewInterstitial();


    }

    /**
     * Loads an interstitial ad based on some conditions.
     */
    private void requestNewInterstitial() {

        /**
         * Generate a random number and if it's divisible by 10 then and then
         * only show the add...
         */

        Random random = new Random();
        int number = random.nextInt(999) + 1;
        // TODO: 21/1/17 remove the if condition for testing purposes
        if (number % 10 == 0) {
            // TODO: 21/1/17 generate your own test device id for testing of ad.
            AdRequest request = new AdRequest.Builder()
                    .addTestDevice("1B9E645F6AEB9B725AACE600DC298402") //Generated from log
                    .build();

            mInterstitialAd.loadAd(request);
        }
    }

    /**
     * Load data from SharedClusters.
     */
    private void loadInitialSharedDataOnSignIn(final FirebaseUser user) {
        /**
         * First we need to retrieve list of all shared clusters,
         * user is a part of, for which, we would concern the node
         * "clusters_of_users"
         */
        loadKeysOfSharedClusters = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {


                ArrayList<String> clusterKeys = new ArrayList<>();

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    clusterKeys.add(snapshot.getValue().toString());
                }

                loadSharedClusterData(clusterKeys);
                clusterKeys.clear();
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                //do nothing
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                //never going to happen
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {
                //do nothing...
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                //do nothing...
            }
        };

        reference.child(SharedConstants.FIREBASE_CLUSTERS_OF_USERS)
                .child(user.getUid())
                .addChildEventListener(loadKeysOfSharedClusters);
    }

    /**
     * Loads data from firebase based on the cluster key's provided.
     * Only adds those data which is currently not present in offline database.
     *
     * @param clusterKeys ArrayList of cluster keys.
     */
    private void loadSharedClusterData(ArrayList<String> clusterKeys) {

        for (final String clusterKey : clusterKeys) {

            if (PreferenceManager.checkKeyAlreadyAdded(context, clusterKey)) {
                //check for next key.
                continue;
            }

            //key was not present, but now add it
            PreferenceManager.addClusterKeyToTinyDB(context, clusterKey);

            reference.child(SharedConstants.FIREBASE_PATH_SHARED_CLUSTERS)
                    .child(clusterKey)
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {

                            ArrayList<ExpenseParcelable> expenses =
                                    new ArrayList<>();

                            String createdByUserEmail = dataSnapshot
                                    .child(SharedConstants.FIREBASE_EMAIL).getValue().toString();
                            String createdByUserName = dataSnapshot
                                    .child(SharedConstants.FIREBASE_USER_NAME).getValue()
                                    .toString();
                            String createdByUserURL = dataSnapshot
                                    .child(SharedConstants.FIREBASE_PROFILE_URL).getValue()
                                    .toString();
                            long timeStampCreated = Long.parseLong(dataSnapshot
                                    .child(SharedConstants.FIREBASE_TIME_STAMP).getValue()
                                    .toString());
                            String clusterTitle = dataSnapshot
                                    .child(SharedConstants.FIREBASE_TITLE).getValue().toString();

                            ClusterParcelable clusterParcelable = new ClusterParcelable(
                                    clusterTitle, createdByUserName, clusterKey, createdByUserEmail,
                                    createdByUserURL, 1, timeStampCreated
                            );

                            for (DataSnapshot sharedExpenses : dataSnapshot.child(
                                    SharedConstants.FIREBASE_EXPENSES).getChildren()) {
                                String expenseKey = sharedExpenses.getKey();

                                String about = sharedExpenses.child(
                                        SharedConstants.FIREBASE_ABOUT).getValue().toString();
                                double amount = Double.parseDouble(
                                        sharedExpenses.child(
                                                SharedConstants.FIREBASE_AMOUNT).getValue()
                                                .toString());
                                String description = sharedExpenses.child(
                                        SharedConstants.FIREBASE_DESCRIPTION).getValue().toString();
                                String expenseByUserName = sharedExpenses.child(
                                        SharedConstants.FIREBASE_USER_NAME).getValue().toString();
                                String expensesByUserEmail = sharedExpenses.child(
                                        SharedConstants.FIREBASE_EMAIL).getValue().toString();
                                String expenseByUserURL = sharedExpenses.child(
                                        SharedConstants.FIREBASE_PROFILE_URL).getValue().toString();
                                long timeStamp = Long.parseLong(
                                        sharedExpenses.child(
                                                SharedConstants.FIREBASE_TIME_STAMP).getValue()
                                                .toString());

                                FirebaseUserDetails details = new FirebaseUserDetails(
                                        expenseByUserName, expensesByUserEmail, "", expenseByUserURL
                                );
                                expenses.add(new ExpenseParcelable(
                                        about, clusterKey, "", details, amount, expenseKey,
                                        description, timeStamp
                                ));
                            }

                            addInitialDataToDatabase(clusterParcelable, expenses);
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
        }

        incrementLoadCountAndDismiss();

    }

    @Override
    protected void onPause() {
        super.onPause();

        //remove listeners
        mFirebaseAuth.removeAuthStateListener(mAuthStateListener);

        if (reference != null && user != null) {
            /**
             * Remove the personal cluster listener..as no need to load data again
             */
            if (personalClusterEventListener != null) {
                reference.child(SharedConstants.FIREBASE_PATH_PERSONAL_CLUSTERS)
                        .child(user.getUid())
                        .removeEventListener(personalClusterEventListener);
                personalClusterEventListener = null;
            }

            /**
             * Remove shared cluster listener once out, as it will load data again and again
             */
            if (loadKeysOfSharedClusters != null) {
                reference.child(SharedConstants.FIREBASE_CLUSTERS_OF_USERS)
                        .child(user.getUid())
                        .removeEventListener(loadKeysOfSharedClusters);
            }
        }

        if (mProgressDialog != null) {
            mProgressDialog.dismiss();
        }

        if (mInterstitialAd != null) {
            //Stop loading the app on onPause....
            mInterstitialAd.setAdListener(null);
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
                manager.beginTransaction()
                        .replace(R.id.fragment_activity_main, new TabContainerFragment())
                        .commit();
            } else if (resultCode == RESULT_CANCELED) {
                this.finish();
            } else if (resultCode == ResultCodes.RESULT_NO_NETWORK) {
                Toast.makeText(context, NOT_CONNECTED,
                        Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_settings:
                Intent intent = new Intent(MainActivity.this,
                        SettingsActivity.class);
                startActivity(intent);
                return true;

            case R.id.sign_out:
                mFirebaseAuth.signOut();
                dataCleanUpOnSignOut();
                return true;
        }

        return false;
    }


    public void loadInitialPersonalDataOnSignIn(final FirebaseUser user) {

        personalClusterEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String cluster_key = snapshot.getKey();

                    //First check if it is already added or not!
                    if (PreferenceManager.checkKeyAlreadyAdded(context, cluster_key)) {
                        continue;
                    }

                    ArrayList<ExpenseParcelable> expensesList = new ArrayList<>();

                    String title = snapshot.child(
                            SharedConstants.FIREBASE_TITLE).getValue().toString();
                    long timeStamp = Long.valueOf(snapshot.child(
                            SharedConstants.FIREBASE_TIME_STAMP).getValue().toString());

                    for (DataSnapshot expenses : snapshot.child(
                            SharedConstants.FIREBASE_EXPENSES).getChildren()) {
                        //expense key of the expense in given cluster #{cluster_key}
                        String expense_key = expenses.getKey();

                        //What the expense is about.
                        String about =
                                expenses.child(SharedConstants.FIREBASE_ABOUT)
                                        .getValue().toString();

                        //the amount in the expense
                        double amount = Double.valueOf(
                                expenses.child(SharedConstants.FIREBASE_AMOUNT)
                                        .getValue().toString()
                        );

                        //When the expense happened
                        long expenseTimeStamp = Long.valueOf(
                                expenses.child(SharedConstants.FIREBASE_TIME_STAMP)
                                        .getValue().toString()
                        );

                        String description =
                                expenses.child(SharedConstants.FIREBASE_DESCRIPTION)
                                        .getValue().toString();

                        //Adding those values to the expenses array list
                        expensesList.add(new ExpenseParcelable(
                                about, cluster_key, user.getUid(), amount, expense_key,
                                expenseTimeStamp, description
                        ));
                    }

                    ClusterParcelable parcel = new ClusterParcelable(
                            title, user.getUid(), cluster_key, 0, timeStamp
                    );

                    addInitialDataToDatabase(parcel, expensesList);
                }

                incrementLoadCountAndDismiss();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        };

        //add value event listener for data related to personal clusters.
        reference.child(SharedConstants.FIREBASE_PATH_PERSONAL_CLUSTERS)
                .child(user.getUid())
                .addListenerForSingleValueEvent(personalClusterEventListener);
    }

    /**
     * Delete all the data from table, once the user has signed out of the application.
     */
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

        /**
         * Clearing the TinyDB contents.
         */

        PreferenceManager.removeAllKeys(context);
        loadCount = 0;
        PreferenceManager.setFirstTimeOpened(context, false);
    }

    /**
     * Adds cluster and expenses in that cluster to offline database.
     */
    public void addInitialDataToDatabase(ClusterParcelable cluster,
            ArrayList<ExpenseParcelable> expenses) {

        boolean isShared = (cluster.getIs_shared() == 1);

        //first add cluster in database.
        ContentValues values = new ContentValues();
        values.put(ClusterEntry.COLUMN_TIMESTAMP, cluster.getTimeStamp());
        values.put(ClusterEntry.COLUMN_IS_SHARED, cluster.getIs_shared());
        values.put(ClusterEntry.COLUMN_TITLE, cluster.getTitle());
        values.put(ClusterEntry.COLUMN_FIREBASE_CLUSTER_KEY, cluster.getFirebase_cluster_id());

        DataInsertionTask task = new DataInsertionTask(getContentResolver(), context);

        task.startInsert(
                SharedConstants.TOKEN_DELETE_CLUSTER,
                null,
                ClusterEntry.CONTENT_URI,
                values
        );

        //creating a vector for bulkInsert
        Vector<ContentValues> contentValuesVector = new Vector<>(expenses.size());


        /**
         * Now for personal expenses, we don't need much information except
         * about, amount, timestamp, description.
         */
        for (ExpenseParcelable expense : expenses) {
            ContentValues value = new ContentValues();
            value.put(ExpenseEntry.FIREBASE_CLUSTER_KEY, expense.getFirebase_cluster_ref_key());
            value.put(ExpenseEntry.COLUMN_FIREBASE_EXPENSE_KEY, expense.getFirebase_expense_key());
            value.put(ExpenseEntry.COLUMN_ABOUT, expense.getAbout());
            value.put(ExpenseEntry.COLUMN_DESCRIBE, expense.getDescription());
            value.put(ExpenseEntry.COLUMN_AMOUNT, expense.getAmount());
            value.put(ExpenseEntry.COLUMN_TIMESTAMP, expense.getTimeStamp());

            if (isShared) {
                value.put(ExpenseEntry.COLUMN_FIREBASE_USER_EMAIL,
                        expense.getUserDetails().getUser_email());
                value.put(ExpenseEntry.COLUMN_FIREBASE_USER_NAME,
                        expense.getUserDetails().getUser_name());
                value.put(ExpenseEntry.COLUMN_FIREBASE_USER_URL,
                        expense.getUserDetails().getUser_photo_url());
            }

            contentValuesVector.add(value);
        }

        if (contentValuesVector.size() > 0) {
            ContentValues[] contentValues = new ContentValues[contentValuesVector.size()];
            contentValuesVector.toArray(contentValues);
            getContentResolver().bulkInsert(ExpenseEntry.CONTENT_URI, contentValues);
        }
        PreferenceManager.setInitialDataLoad(context, true);
    }


    /**
     * Increments the load count, and if both
     * shared and personal data are loaded, dismiss the progress dialog...
     */
    private void incrementLoadCountAndDismiss() {
        loadCount++;
        if (loadCount == 2) {
            if (mProgressDialog != null) {
                mProgressDialog.dismiss();
            }
        }
    }

    @Override
    public void openCluster(Bundle args) {

        String FRAGMENT_TAG = "fragment_tag_two_pane_mode";
        if (manager == null) {
            manager = getSupportFragmentManager();
        }
        if (mTwoPaneMode) {
            Fragment oldFragment = manager.findFragmentByTag(FRAGMENT_TAG);
            if (oldFragment != null) {
                manager.beginTransaction().remove(oldFragment).commit();
            }

            Fragment newFragment = new ExpensesFragment();
            newFragment.setArguments(args);
            manager.beginTransaction()
                    .replace(R.id.fl_activity_expenses_loader, newFragment, FRAGMENT_TAG)
                    .commit();
        }
    }
}