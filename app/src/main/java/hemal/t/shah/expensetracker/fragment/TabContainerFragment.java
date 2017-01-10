package hemal.t.shah.expensetracker.fragment;

import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioGroup;
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
import hemal.t.shah.expensetracker.adapters.ViewPagerTabAdapter;
import hemal.t.shah.expensetracker.data.DataInsertionTask;
import hemal.t.shah.expensetracker.data.ExpenseContract;
import hemal.t.shah.expensetracker.data.ExpenseContract.ClusterEntry;
import hemal.t.shah.expensetracker.utils.SharedConstants;

/**
 * Fragment that contains the "Personal" & "Shared" tabs for the MainActivity.
 * Created by hemal on 13/12/16.
 */
public class TabContainerFragment extends Fragment {

    private static final String TAG = "TabContainerFragment";

    @BindView(R.id.tl_activity_tab_container)
    TabLayout tabLayout;

    @BindView(R.id.vp_activity_tab_container)
    ViewPager viewPager;

    @BindView(R.id.toolbar_activity_tab_container)
    Toolbar toolbar;

    @BindString(R.string.create_cluster)
    String CREATE_CLUSTER;

    ActionBar mActionBar;
    FirebaseUser user;

    DatabaseReference reference;

    AppCompatActivity activity;

    Context context;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {

        View base_view = inflater.inflate(R.layout.activity_tab_container, container, false);

        ButterKnife.bind(this, base_view);

        this.context = getContext();

        //Getting firebase references.
        user = FirebaseAuth.getInstance().getCurrentUser();
        reference = FirebaseDatabase.getInstance().getReference();

        activity = (AppCompatActivity) getActivity();
        activity.setSupportActionBar(toolbar);
        mActionBar = activity.getSupportActionBar();

        //set the name of user in title and email in subtitle
        if (user != null) {
            if (mActionBar != null) {
                mActionBar.setTitle(user.getDisplayName());
                mActionBar.setSubtitle(user.getEmail());
            }
        }

        viewPager.setAdapter(new ViewPagerTabAdapter(getChildFragmentManager()));

        tabLayout.setupWithViewPager(viewPager);

        return base_view;
    }

    @OnClick(R.id.fab_activity_tab_container)
    public void fabNewCluster() {
        //Show dialog to generate new cluster.

        AlertDialog.Builder builder = new AlertDialog.Builder(this.context);
        builder.setCancelable(true);
        builder.setTitle(CREATE_CLUSTER);

        View dialogView = getLayoutInflater(null).inflate(R.layout.dialog_new_cluster, null);
        final TextInputEditText et_new_personal_cluster =
                (TextInputEditText) dialogView.findViewById(R.id.tiet_dialog_new_cluster);

        final RadioGroup radioGroup = (RadioGroup) dialogView.findViewById(R.id.rg_dialog_new_cluster);

        builder.setPositiveButton("Create", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                String title = et_new_personal_cluster.getText().toString();

                if (title.length() <= 3 || title.length() >= 15) {
                    // TODO: 17/12/16 add snackbar here
                    Toast.makeText(context, "Length should be between 3 to 15 characters.",
                            Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                }

                int is_shared = -1;
                switch (radioGroup.getCheckedRadioButtonId()) {
                    case R.id.rb_personal_dialog_new_cluster:
                        is_shared = 0; //for personal is_shared = 0
                        break;
                    case R.id.rb_shared_dialog_new_cluster:
                        is_shared = 1;
                        break;
                }
                addNewCluster(title, is_shared);
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.setView(dialogView);
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    /**
     * Generate new cluster from the dialog interface, and also push to firebase.
     *
     * @param title Title of the cluster to be created.
     */
    private void addNewCluster(String title, int is_shared) {
        long timeStamp = System.currentTimeMillis();
        if (user != null) {
            //We need to add data to offline and online database both.

            //unique generated by firebase.
            String cluster_key = reference.push().getKey();

            ContentValues contentValues = new ContentValues();
            contentValues.put(ClusterEntry.COLUMN_TITLE, title);
            contentValues.put(ClusterEntry.COLUMN_FIREBASE_CLUSTER_KEY, cluster_key);
            contentValues.put(ClusterEntry.COLUMN_IS_SHARED, is_shared);
            contentValues.put(ClusterEntry.COLUMN_TIMESTAMP, timeStamp);

            DataInsertionTask dataInsertionTask =
                    new DataInsertionTask(context.getContentResolver(), context, contentValues);


            /**
             * Now, we want unique names for personal clusters, but for shared clusters,
             * the names could be redundant.
             */
            Map<String, Object> cluster = new HashMap<>();
            cluster.put(SharedConstants.FIREBASE_TITLE, title);
            cluster.put(SharedConstants.FIREBASE_TIME_STAMP, timeStamp);
            cluster.put(SharedConstants.FIREBASE_EXPENSES, "SAMPLE TEXT");

            if (is_shared == 0) { //personal query
                /**
                 * This will check for existing personal clusters with same name,
                 * if available, new cluster will not be created.
                 */
                String selection = ExpenseContract.ClusterEntry.COLUMN_TITLE
                        + " = ?"
                        + " AND "
                        + ExpenseContract.ClusterEntry.COLUMN_IS_SHARED
                        + " = 0";

                dataInsertionTask.startQuery(
                        SharedConstants.TOKEN_CHECK_FOR_CLUSTER_TITLE,
                        null,
                        ClusterEntry.CONTENT_URI,
                        null,
                        selection,
                        new String[]{title},
                        null);

                reference.child(SharedConstants.FIREBASE_PATH_PERSONAL_CLUSTERS)
                        .child(user.getUid())
                        .child(cluster_key)
                        .updateChildren(cluster);


            } else if (is_shared == 1) { //shared, should be available.
                /**
                 * Directly start the insert into offline database.
                 */
                dataInsertionTask.startInsert(
                        SharedConstants.TOKEN_ADD_NEW_CLUSTER,
                        null,
                        ExpenseContract.ClusterEntry.CONTENT_URI,
                        contentValues);

                cluster.put(SharedConstants.FIREBASE_EMAIL, user.getEmail());
                cluster.put(SharedConstants.FIREBASE_USER_NAME, user.getDisplayName());
                if (user.getPhotoUrl() != null) {
                    cluster.put(SharedConstants.FIREBASE_PROFILE_URL,
                            user.getPhotoUrl().toString());
                } else {
                    cluster.put(SharedConstants.FIREBASE_PROFILE_URL, "");
                }


                reference.child(SharedConstants.FIREBASE_PATH_SHARED_CLUSTERS)
                        .child(cluster_key)
                        .updateChildren(cluster);
            }

        } else {
            // TODO: 6/1/17 Add intent to redirect to sign in
            Toast.makeText(context, "You are not signed in. Sign in to continue", Toast.LENGTH_SHORT).show();
        }

    }
}
