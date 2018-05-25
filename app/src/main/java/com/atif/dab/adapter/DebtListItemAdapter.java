package com.atif.dab.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.atif.dab.Debt;
import com.atif.dab.R;
import java.util.List;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by Atif Mustaffa on 13/4/2018.
 */
public class DebtListItemAdapter extends BaseAdapter {

    LayoutInflater inflater;
    Context context;
    List<Debt> debtArray;

    public DebtListItemAdapter(Context c, List<Debt> debtArray) {
        inflater = (LayoutInflater) c.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.context = c;
        this.debtArray = debtArray;
    }

    @Override
    public int getCount() {
        return debtArray.size()+1;
    }

    @Override
    public Object getItem(int i) {
        return R.layout.debt_list_item;
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        View v = inflater.inflate(R.layout.debt_list_item, null);
        TextView debtorNameTV = (TextView) v.findViewById(R.id.debtorNameTV);
        TextView debtTitleTV = (TextView) v.findViewById(R.id.debtTitleTV);
        TextView creditorNameTV = (TextView) v.findViewById(R.id.creditorNameTV);
        TextView amountTV = (TextView) v.findViewById(R.id.amountTV);
        TextView statusTV = (TextView) v.findViewById(R.id.statusTV);
        ImageView imageView = (ImageView) v.findViewById(R.id.imageView);

        // To avoid printing last element (empty element because of the floating button)
        if(i < getCount()-1) {
            v.setTag(debtArray.get(i).getId());
            debtorNameTV.setText(debtArray.get(i).getDebtorName());
            debtTitleTV.setText(debtArray.get(i).getDebtTitle());
            creditorNameTV.setText(getText(R.string.creditor_lbl) + ": " + debtArray.get(i).getCreditorName());
            amountTV.setText(getCurrency() + " " + String.format("%.2f", debtArray.get(i).getAmount()));
            if (debtArray.get(i).isOngoing()) {
//                statusTV.setText(getText(R.string.created_on_lbl) + " " + debtArray.get(i).getCreationDate());
                statusTV.setText(getText(R.string.ongoing_lbl));
                imageView.setImageResource(R.drawable.ic_ongoing);
            }
            else {
//                statusTV.setText(getText(R.string.settled_on_lbl) + " " + debtArray.get(i).getSettleDate());;
                statusTV.setText(getText(R.string.settled_lbl));
                imageView.setImageResource(R.drawable.ic_settled);
            }
        }
        else {
            debtorNameTV.setText("");
            debtTitleTV.setText("");
            creditorNameTV.setText("");
            amountTV.setText("");
            statusTV.setText("");
            v.setOnClickListener(null);
        }
        return v;
    }

    private String getText(int resId) {
        return (String) this.context.getText(resId);
    }
    private String getCurrency() {
        return (String) this.context.getSharedPreferences("com.atif.dab",MODE_PRIVATE).getString("currency",null);
    }
}
