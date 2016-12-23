package hemal.t.shah.expensetracker;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;

import hemal.t.shah.expensetracker.fragment.PersonalExpensesFragment;

/**
 * Loads expenses based on the is_shared properties of intent.
 * Created by hemal on 23/12/16.
 */
public class ExpensesActivity extends AppCompatActivity {

    FragmentManager manager;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_expenses_loader);

        Intent intent = getIntent();
        int is_shared = intent.getIntExtra("is_shared", -1);
        String title = intent.getStringExtra("title");


        manager = getSupportFragmentManager();
        manager.beginTransaction()
                .replace(R.id.fl_activity_expenses_loader, new PersonalExpensesFragment())
                .commit();

    }
}
