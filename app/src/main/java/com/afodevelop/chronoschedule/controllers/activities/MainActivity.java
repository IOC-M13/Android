package com.afodevelop.chronoschedule.controllers.activities;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

import com.afodevelop.chronoschedule.R;
import com.afodevelop.chronoschedule.controllers.adapters.PagerAdapter;
import com.afodevelop.chronoschedule.controllers.mysqlControllers.MySQLAssistant;
import com.afodevelop.chronoschedule.controllers.mysqlControllers.MySQLConnectorFactory;
import com.afodevelop.chronoschedule.controllers.ormControllers.ORMAssistant;
import com.afodevelop.chronoschedule.controllers.sqliteControllers.SQLiteAssistant;
import com.afodevelop.chronoschedule.controllers.sqliteControllers.SQLiteException;
import com.afodevelop.chronoschedule.model.User;


public class MainActivity extends AppCompatActivity {

    // CONSTANTS

    // CLASS-WIDE VARIABLES
    private MySQLAssistant mySQLAssistant;
    private SQLiteAssistant sqLiteAssistant;
    private ORMAssistant ormAssistant;

    private boolean isAdmin;
    private User user;

    // INTERNAL CLASS DEFINITIONS



    // LOGIC

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mySQLAssistant = MySQLAssistant.getInstance();
        sqLiteAssistant = SQLiteAssistant.getInstance();
        ormAssistant = ORMAssistant.getInstance();

        try {
            user = sqLiteAssistant.getUserByUserName(getIntent().getExtras().getString("user"));
        } catch (SQLiteException e) {
            e.printStackTrace();
        }

        isAdmin = user.isAdmin();


        TabLayout tabLayout = (TabLayout) findViewById(R.id.tab_layout);
        TabLayout.Tab tmpTab;
        tmpTab = tabLayout.newTab().setText("Calendar");
        tmpTab.setIcon(R.drawable.ic_menu_month);
        tabLayout.addTab(tmpTab);
        if (isAdmin) {
            tmpTab = tabLayout.newTab().setText("Users");
            tmpTab.setIcon(R.drawable.ic_menu_allfriends);
            tabLayout.addTab(tmpTab);
            tmpTab = tabLayout.newTab().setText("Shifts");
            tmpTab.setIcon(R.drawable.ic_menu_recent_history);
            tabLayout.addTab(tmpTab);
        }
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

        final ViewPager viewPager = (ViewPager) findViewById(R.id.pager);
        final PagerAdapter adapter = new PagerAdapter
                (getSupportFragmentManager(), tabLayout.getTabCount());
        viewPager.setAdapter(adapter);
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();


        return super.onOptionsItemSelected(item);
    }

    public boolean isAdmin(){
        return isAdmin;
    }
}