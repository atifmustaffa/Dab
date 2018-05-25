package com.atif.dab;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.atif.dab.adapter.DebtListItemAdapter;

import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class PreviousFragment extends Fragment {

    ListView listView;
    List<Debt> previousDebts;
    DebtListItemAdapter adapter;
    String TAG = "DAB_APP_MSG";

    public PreviousFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_previous, container, false);
        listView = (ListView) v.findViewById(R.id.prevDebtListView);
        previousDebts = new DebtDatabaseHandler(getContext()).getAllDebt(false);
        adapter = new DebtListItemAdapter(getContext(), previousDebts);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(getContext(), ViewDebtActivity.class);
                intent.putExtra("com.atif.dab.viewDebtId", "" + view.getTag());
                startActivity(intent);
            }
        });

        int count = listView.getAdapter().getCount()-1;
        Log.i(TAG, "(Previous Fragment) Data Rows: " + count);
        if(count < 1) {
            TextView emptyTV = (TextView) v.findViewById(R.id.emptyTV);
            emptyTV.setVisibility(View.VISIBLE);
        }
        return v;
    }
}
