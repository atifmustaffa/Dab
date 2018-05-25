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
public class HomeFragment extends Fragment {

    ListView listView;
    List<Debt> ongoingDebts;
    DebtListItemAdapter adapter;
    String TAG = "DAB_APP_MSG";

    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_home, container, false);
        listView = (ListView) v.findViewById(R.id.debtListView);
        ongoingDebts = new DebtDatabaseHandler(getContext()).getAllDebt(true);
        adapter = new DebtListItemAdapter(getContext(), ongoingDebts);
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
        Log.i(TAG, "(Home Fragment) Data Rows: " + count);
        if(count < 1) {
            TextView emptyTV = (TextView) v.findViewById(R.id.emptyTV);
            emptyTV.setVisibility(View.VISIBLE);
        }
        return v;
    }
}
