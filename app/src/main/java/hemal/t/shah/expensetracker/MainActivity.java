package hemal.t.shah.expensetracker;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;

import hemal.t.shah.expensetracker.fragment.TabContainerFragment;

/**
 * Entry point for the application for now.
 */
public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    FragmentManager fragmentManager;
    FragmentTransaction fragmentTransaction;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        fragmentManager = getSupportFragmentManager();
        fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(
                R.id.fl_activity_main,
                new TabContainerFragment()
        );
        fragmentTransaction.commit();
    }

}
