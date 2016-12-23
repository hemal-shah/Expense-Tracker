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
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioGroup;
import android.widget.Toast;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import hemal.t.shah.expensetracker.R;
import hemal.t.shah.expensetracker.adapters.ViewPagerTabAdapter;
import hemal.t.shah.expensetracker.data.ExpenseContract;
import hemal.t.shah.expensetracker.data.NewClusterGenerator;
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

    Context context;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {

        View base_view = inflater.inflate(R.layout.activity_tab_container,
                container, false);

        ButterKnife.bind(this, base_view);

        this.context = getContext();

        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);

        viewPager.setAdapter(new ViewPagerTabAdapter(getChildFragmentManager()));
        //Workaround for proper functioning of NavigationView and TabLayout
        tabLayout.post(new Runnable() {
            @Override
            public void run() {
                tabLayout.setupWithViewPager(viewPager);
            }
        });

        return base_view;
    }


    @OnClick(R.id.fab_activity_tab_container)
    public void fabNewPersonalCluster() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this.context);
        builder.setCancelable(true);
        builder.setTitle("Add new Cluster");

        View dialogView = getLayoutInflater(null).inflate(R.layout.dialog_new_cluster, null);
        final TextInputEditText et_new_personal_cluster =
                (TextInputEditText) dialogView.findViewById(
                        R.id.tiet_dialog_new_cluster);

        final RadioGroup radioGroup = (RadioGroup) dialogView.findViewById(
                R.id.rg_dialog_new_cluster);
        /*final RadioButton pRadioButton = (RadioButton) dialogView.findViewById(
                R.id.rb_personal_dialog_new_cluster);
        final RadioButton sRadioButton = (RadioButton) dialogView.findViewById(
                R.id.rb_shared_dialog_new_cluster);*/

        builder.setPositiveButton("Create", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String title = et_new_personal_cluster.getText().toString();

                if (title.length() <= 3 || title.length() >= 15) {
                    // TODO: 17/12/16 add snackbar here
                    Toast.makeText(context, "Length should be between 3 to 15 characters.",
                            Toast.LENGTH_SHORT).show();
                    return;
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

                //// TODO: 17/12/16 generate timestamp here...
                String timestamp = "new time here..";

                ContentValues contentValues = new ContentValues();
                contentValues.put(ExpenseContract.ClusterEntry.COLUMN_TITLE, title);
                contentValues.put(ExpenseContract.ClusterEntry.COLUMN_IS_SHARED, is_shared);
                contentValues.put(ExpenseContract.ClusterEntry.COLUMN_SUM, 0.0);
                contentValues.put(ExpenseContract.ClusterEntry.COLUMN_USERS_LIST, "hemal");
                contentValues.put(ExpenseContract.ClusterEntry.COLUMN_TIMESTAMP, timestamp);

                NewClusterGenerator newClusterGenerator = new NewClusterGenerator(
                        context.getContentResolver(), context, contentValues);

                /**
                 * Now, we want unique names for personal clusters, but for shared clusters,
                 * the names could be redundant.
                 */

                if (is_shared == 0) { //personal query
                    String selection = ExpenseContract.ClusterEntry.COLUMN_TITLE + " = ?" + " AND "
                            + ExpenseContract.ClusterEntry.COLUMN_IS_SHARED + " = 0";

                    newClusterGenerator.startQuery(
                            SharedConstants.TOKEN_CHECK_FOR_CLUSTER_TITLE,
                            null, ExpenseContract.ClusterEntry.CONTENT_URI,
                            null, selection,
                            new String[]{title}, null
                    );
                } else if (is_shared == 1) { //shared, should be available.
                    newClusterGenerator.startInsert(
                            SharedConstants.TOKEN_ADD_NEW_CLUSTER,
                            null, ExpenseContract.ClusterEntry.CONTENT_URI, contentValues
                    );
                }
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

}
