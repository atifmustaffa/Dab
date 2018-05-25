package com.atif.dab;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.atif.dab.adapter.DebtListItemAdapter;
import com.atif.dab.adapter.ViewPagerAdapter;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    BottomNavigationView bottomNavigationView;
    ViewPager viewPager;
    ViewPagerAdapter viewPagerAdapter;
    MenuItem prevMenuItem, searchItem;
    SearchView searchView;
    FloatingActionButton floatingActionButton;
    String TAG = "DAB_APP_MSG";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottomNavigationView);
        viewPager = (ViewPager) findViewById(R.id.viewPager);
        viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(viewPagerAdapter);
        floatingActionButton = (FloatingActionButton) findViewById(R.id.floatingActionButton);

        // Currency first time setup
        if(getSharedPreferences("com.atif.dab",MODE_PRIVATE).getString("currency",null) == null)
            getSharedPreferences("com.atif.dab",MODE_PRIVATE).edit().putString("currency", ""+getText(R.string.RM)).apply();

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                switch (item.getItemId()) {
                    case R.id.action_home:
                        viewPager.setCurrentItem(0);
                        break;
                    case R.id.action_previous:
                        viewPager.setCurrentItem(1);
                        break;
                }

                return false;
            }
        });
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                // Check if has previous (if not, set homepage unchecked)
                if (prevMenuItem != null) {
                    prevMenuItem.setChecked(false);
                } else {
                    bottomNavigationView.getMenu().getItem(0).setChecked(false);
                }
                bottomNavigationView.getMenu().getItem(position).setChecked(true);
                prevMenuItem = bottomNavigationView.getMenu().getItem(position);

                // Hide floating action button add on other pages
                if (position != 0)
                    floatingActionButton.hide();
                else if (!floatingActionButton.isShown() && searchView.isIconified()){
                    floatingActionButton.show();
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDialogBox();
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        getMenuInflater().inflate(R.menu.actionbar_menu_items, menu);
        searchItem = menu.findItem(R.id.app_bar_search);
        searchView = (SearchView) searchItem.getActionView();
        searchView.setMaxWidth(Integer.MAX_VALUE);

        searchItem.setOnActionExpandListener(new MenuItem.OnActionExpandListener() {

            @Override
            public boolean onMenuItemActionExpand(MenuItem item) {
                menu.findItem(R.id.app_bar_feedback).setVisible(false);
                menu.findItem(R.id.app_bar_settings).setVisible(false);
                bottomNavigationView.setVisibility(View.GONE);
                floatingActionButton.hide();
                return true;
            }

            @Override
            public boolean onMenuItemActionCollapse(MenuItem menuItem) {
                menu.findItem(R.id.app_bar_feedback).setVisible(true);
                menu.findItem(R.id.app_bar_settings).setVisible(true);
                bottomNavigationView.setVisibility(View.VISIBLE);
                if(viewPager.getCurrentItem() == 0)
                    floatingActionButton.show();

                // Reset item list
                HomeFragment homeFragment = (HomeFragment) viewPagerAdapter.getItem(0);
                PreviousFragment previousFragment = (PreviousFragment) viewPagerAdapter.getItem(1);
                List<Debt> ongoingDebts, previousDebts;
                DebtListItemAdapter adapter1 = null, adapter2 = null;
                ongoingDebts = new DebtDatabaseHandler(MainActivity.this).getAllDebt(true);
                adapter1 = new DebtListItemAdapter(MainActivity.this, ongoingDebts);
                previousDebts = new DebtDatabaseHandler(MainActivity.this).getAllDebt(false);
                adapter2 = new DebtListItemAdapter(MainActivity.this, previousDebts);
                homeFragment.listView.setAdapter(adapter1);
                previousFragment.listView.setAdapter(adapter2);
                return true;
            }
        });

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                runSearch(query.toLowerCase());
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                runSearch(newText.toLowerCase());
                return true;
            }
        });

        return true;
    }

    private void runSearch(String query) {
        HomeFragment homeFragment = (HomeFragment) viewPagerAdapter.getItem(0);
        PreviousFragment previousFragment = (PreviousFragment) viewPagerAdapter.getItem(1);
        List<Debt> ongoingDebts, previousDebts;
        DebtListItemAdapter adapter1 = null, adapter2 = null;
        if(query.equals("")) {
            ongoingDebts = new DebtDatabaseHandler(MainActivity.this).getAllDebt(true);
            adapter1 = new DebtListItemAdapter(MainActivity.this, ongoingDebts);
            previousDebts = new DebtDatabaseHandler(MainActivity.this).getAllDebt(false);
            adapter2 = new DebtListItemAdapter(MainActivity.this, previousDebts);
        }
        else {
            ongoingDebts = new DebtDatabaseHandler(MainActivity.this).getSearchResult(query, true);
            adapter1 = new DebtListItemAdapter(MainActivity.this, ongoingDebts);
            previousDebts = new DebtDatabaseHandler(MainActivity.this).getSearchResult(query, false);
            adapter2 = new DebtListItemAdapter(MainActivity.this, previousDebts);
        }
        homeFragment.listView.setAdapter(adapter1);
        previousFragment.listView.setAdapter(adapter2);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.app_bar_settings:
                startActivity(new Intent(getApplicationContext(), SettingsActivity.class));
                return true;
            case R.id.app_bar_feedback:
                startActivity(new Intent(getApplicationContext(), FeedbackActivity.class));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.i(TAG, "Resume MainActivity");
        viewPagerAdapter.notifyDataSetChanged();
        if(searchView != null && !searchView.getQuery().equals("")) runSearch(searchView.getQuery().toString());
    }
    private void showDialogBox() {
        View view = getLayoutInflater().inflate(R.layout.alertdialog_box_choose, null);
        final AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this).create();
        alertDialog.setTitle(getText(R.string.you_are_lbl));
        alertDialog.setView(view);
        ListView lv = (ListView) view.findViewById(R.id.lv);
        lv.setAdapter(new ArrayAdapter<String>(this, R.layout.listview_item_textview,
                getResources().getStringArray(R.array.add_options)));
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                switch (i) {
                    case 0: executeOption("creditor"); break;
                    case 1: executeOption("debtor"); break;
                }
                alertDialog.dismiss();
            }
        });
        alertDialog.show();

    }

    private void executeOption(String key) {
        Intent i = new Intent(getApplicationContext(), AddEditActivity.class);
        i.putExtra("com.atif.dab.addEdit", "add");
        i.putExtra("com.atif.dab.addEditOption", key);
        startActivity(i);
    }

}