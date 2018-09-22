package com.atif.dab;

import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class ImportExportActivity extends AppCompatActivity {

    int ieType;
    String TAG = "DAB_APP_MSG";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_import_export);
        setTitle(R.string.importexport_lbl);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ieType = getIntent().getExtras().getInt("com.atif.dab.ieType");

        String ieTypeString = "";
        for (String opt: getResources().getStringArray(R.array.ie_options))
            if(opt.equals(getResources().getStringArray(R.array.ie_options)[ieType]))
                ieTypeString = opt;
        Log.i(TAG, "Import export type: " +ieTypeString);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

}
