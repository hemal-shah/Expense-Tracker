package hemal.t.shah.expensetracker;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.EditText;
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

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import hemal.t.shah.expensetracker.utils.SharedConstants;

/**
 * Activity that helps user to connect to any existing cluster...
 * Created by hemal on 12/1/17.
 */

public class JoinSharedCluster extends AppCompatActivity {

    private static final String TAG = "JoinSharedCluster";

    @BindView(R.id.toolbar_activity_join_cluster)
    Toolbar toolbar;

    @BindView(R.id.et_join_cluster)
    EditText mEditText;

    // TODO: 12/1/17 cancel button on top
    ActionBar mActionBar;
    FirebaseUser user;
    DatabaseReference reference;
    private String cluster_key = "";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join_cluster);
        ButterKnife.bind(this);

        setSupportActionBar(toolbar);

        user = FirebaseAuth.getInstance().getCurrentUser();
        reference = FirebaseDatabase.getInstance().getReference();

    }


    @OnClick(R.id.bt_join_cluster)
    public void joinCluster() {
        final String code = mEditText.getText().toString();
        if (code.length() != 6) {
            Toast.makeText(JoinSharedCluster.this, "Enter 6 digit code.",
                    Toast.LENGTH_SHORT).show();
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
                                    Log.i(TAG, "onDataChange: cluster key = " + cluster_key);
                                    break;
                                }
                            }
                        }
                        addUserToCluster();

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Toast.makeText(JoinSharedCluster.this, "Error!", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void addUserToCluster() {
        /**
         * If the cluster_key is empty, then we need to warn user
         * that the key is not correct.
         */

        if (cluster_key.length() == 0 || cluster_key.equalsIgnoreCase("")) {
            Toast.makeText(this, "The code you entered is not valid.", Toast.LENGTH_SHORT).show();
            //empty the edit text.
            mEditText.setText("");
            return;
        }


        /**
         * Now that we have found the cluster key,
         * we should add the current user to the shared cluster
         * For that we need to modify two nodes.
         * 1. clusters_of_users
         * 2. users_in_clusters
         */


        /**
         * If user is not signed in, take to sign in screen
         * Mostly this would be not possible, but as a safety precaution.
         */

        if (user != null) {
            //starting with "clusters_of_users" node
            Map<String, Object> map = new HashMap<>();
            map.put(SharedConstants.FIREBASE_PATH_SHARED_CLUSTERS, cluster_key);

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

            Toast.makeText(this, "You are successfully added to the said cluster",
                    Toast.LENGTH_SHORT).show();
            this.finish();
        } else {
            // TODO: 13/1/17 redirect user to sign in screen
            Toast.makeText(this, "You are not signed in!", Toast.LENGTH_SHORT).show();
        }
    }
}