package com.atif.dab;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

public class SettingsActivity extends AppCompatActivity {

    ListView listView;
    String TAG = "DAB_APP_MSG";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        setTitle(R.string.settings_lbl);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        listView = (ListView)findViewById(R.id.settingListView);
        listView.setAdapter(new ArrayAdapter<String>(this,R.layout.listview_item_textview,
                getResources().getStringArray(R.array.setting_options) ));
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                switch (i){
                    case 0: showDeleteAllDialogBox(); break;
                    case 1: showCurrencyDialogBox(); break;
                    case 2: showAboutUsDialogBox(); break;
                }
            }
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    private void showCurrencyDialogBox() {
        View view = getLayoutInflater().inflate(R.layout.alertdialog_box_choose, null);
        final AlertDialog alertDialog = new AlertDialog.Builder(SettingsActivity.this).create();
        alertDialog.setTitle(getText(R.string.you_are_lbl));
        alertDialog.setView(view);
        ListView lv = (ListView) view.findViewById(R.id.lv);
        lv.setAdapter(new ArrayAdapter<String>(this, R.layout.listview_item_textview,
                getResources().getStringArray(R.array.currency_options)));
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                SharedPreferences.Editor sp = getSharedPreferences("com.atif.dab", MODE_PRIVATE).edit();
                switch (i) {
                    case 0: sp.putString("currency", ""+getText(R.string.RM)); break;
                    case 1: sp.putString("currency", ""+getText(R.string.USD)); break;
                }
                sp.apply();
                alertDialog.dismiss();
            }
        });
        alertDialog.show();

    }

    private void showDeleteAllDialogBox() {
        AlertDialog alertDialog = new AlertDialog.Builder(SettingsActivity.this).create();
        alertDialog.setTitle(getText(R.string.delete_all_data_lbl));
        alertDialog.setMessage(getText(R.string.del_all_msg));
        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, getText(R.string.delete_lbl).toString().toUpperCase(),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // Database operation
                        new DebtDatabaseHandler(SettingsActivity.this).deleteAllDebt();
                        Log.i(TAG, "Successfully delete all debts");
                        Toast.makeText(getApplicationContext(), getText(R.string.deleted_lbl), Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                        finish();
                    }
                });
        alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, getText(R.string.cancel_lbl).toString().toUpperCase(),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        alertDialog.show();
    }

    private void showAboutUsDialogBox() {
        AlertDialog alertDialog = new AlertDialog.Builder(SettingsActivity.this).create();
        alertDialog.setTitle(getText(R.string.about_us_lbl));
        alertDialog.setMessage(getText(R.string.app_version) +"\n\n" +getText(R.string.about_us_msg));
        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, getText(android.R.string.ok),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, getText(R.string.more_on_lbl).toString().toUpperCase(),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(getText(R.string.website_url).toString())));
                    }
                });
        alertDialog.show();
    }
}
