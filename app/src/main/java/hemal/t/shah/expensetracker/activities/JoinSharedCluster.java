package hemal.t.shah.expensetracker.activities;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import hemal.t.shah.expensetracker.R;
import hemal.t.shah.expensetracker.utils.SharedConstants;

/**
 * Activity that helps user to connect to any existing cluster...
 * Created by hemal on 12/1/17.
 */

public class JoinSharedCluster extends AppCompatActivity {

    private static final String TAG = "JoinSharedCluster";

    @BindView(R.id.toolbar_activity_join_cluster)
    Toolbar toolbar;

    @BindView(R.id.cl_activity_join_cluster)
    CoordinatorLayout mCoordinatorLayout;

    @BindView(R.id.tiet_activity_join_cluster)
    TextInputEditText mEditText;

    @BindView(R.id.til_activity_join_cluster)
    TextInputLayout mTextInputLayout;

    @BindString(R.string.enter_code)
    String ENTER_CODE;

    @BindString(R.string.proper_code)
    String PROPER_CODE;
    @BindString(R.string.successfully_added)
    String SUCCESS_ADDED;
    @BindString(R.string.already_joined)
    String ALREADY_JOINED;

    ActionBar actionBar;
    FirebaseUser user;
    DatabaseReference reference;
    private String cluster_key = "";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join_cluster);
        ButterKnife.bind(this);

        setSupportActionBar(toolbar);
        actionBar = getSupportActionBar();

        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayShowHomeEnabled(true);
            actionBar.setHomeAsUpIndicator(R.drawable.ic_arrow_back_white_24dp);
        }

        user = FirebaseAuth.getInstance().getCurrentUser();
        reference = FirebaseDatabase.getInstance().getReference();

    }


    @OnClick(R.id.fab_join_cluster)
    public void joinCluster() {
        final String code = mEditText.getText().toString();
        if (code.length() != 6) {
            showSnackBar(ENTER_CODE);
            return;
        }

        reference.child(SharedConstants.FIREBASE_PATH_CLUSTER_ID)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            for (DataSnapshot dataShot : snapshot.getChildren()) {
                                String key = dataShot.getKey();
                                if (key.equalsIgnoreCase(code)) {
                                    cluster_key = dataShot.getValue().toString();
                                    break;
                                }
                            }
                        }
                        addUserToCluster();
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                    }
                });
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return false;
    }

    private void addUserToCluster() {
        /**
         * If the cluster_key is empty, then we need to warn user
         * that the key is not correct.
         */
        if (cluster_key.length() == 0 || cluster_key.equalsIgnoreCase("")) {
            mEditText.setError(PROPER_CODE);
            mEditText.setText("");
            return;
        }


        /**
         * We first need to check if user is already a part of this cluster!
         * If user already is, simply return from here.
         */
        if (user != null) {

            reference.child(SharedConstants.FIREBASE_CLUSTERS_OF_USERS)
                    .child(user.getUid())
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {

                            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                String key = snapshot
                                        .child(SharedConstants.FIREBASE_PATH_SHARED_CLUSTERS)
                                        .getValue().toString();

                                if (key.equals(cluster_key)) {
                                    Toast.makeText(JoinSharedCluster.this, ALREADY_JOINED,
                                            Toast.LENGTH_LONG).show();
                                    JoinSharedCluster.this.finish();
                                } else {
                                    /**
                                     * Now that we have found the cluster key,
                                     * we should add the current user to the shared cluster
                                     * For that we need to modify two nodes.
                                     * 1. clusters_of_users
                                     * 2. users_in_clusters
                                     */

                                    //starting with "clusters_of_users" node
                                    Map<String, Object> map = new HashMap<>();
                                    map.put(SharedConstants.FIREBASE_PATH_SHARED_CLUSTERS,
                                            cluster_key);

                                    reference.child(SharedConstants.FIREBASE_CLUSTERS_OF_USERS)
                                            .child(user.getUid())
                                            .push()
                                            .updateChildren(map);

                                    //now updating the "users_in_clusters" node
                                    Map<String, Object> map1 = new HashMap<>();
                                    map1.put(SharedConstants.FIREBASE_USER_UID, user.getUid());
                                    reference.child(SharedConstants.FIREBASE_USERS_IN_CLUSTERS)
                                            .child(cluster_key)
                                            .push()
                                            .updateChildren(map1);

                                    showSnackBar(SUCCESS_ADDED);
                                    new Thread(new Runnable() {
                                        @Override
                                        public void run() {
                                            try {
                                                Thread.sleep(Snackbar.LENGTH_LONG);
                                            } catch (InterruptedException e) {
                                                //can't do anything...
                                            }

                                            JoinSharedCluster.this.finish();
                                        }
                                    }).start();
                                }
                            }

                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
        }
    }

    public void showSnackBar(String msg) {
        Snackbar.make(mCoordinatorLayout, msg, Snackbar.LENGTH_LONG).show();
    }
}
