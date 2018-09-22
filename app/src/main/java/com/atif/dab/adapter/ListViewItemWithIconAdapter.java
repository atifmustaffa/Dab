package com.atif.dab.adapter;

import android.content.Context;
import android.content.res.TypedArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.atif.dab.R;

/**
 * Created by Atif Mustaffa on 23/9/2018.
 */
public class ListViewItemWithIconAdapter extends BaseAdapter {

    LayoutInflater inflater;
    Context context;
    String[] title;
    TypedArray icons;

    public ListViewItemWithIconAdapter(Context context, String[] title, TypedArray icons) {
        this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.context = context;
        this.title = title;
        this.icons = icons;
    }

    @Override
    public int getCount() {
        return title.length;
    }

    @Override
    public Object getItem(int i) {
        return i;
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        View v = inflater.inflate(R.layout.listview_item_withicon_textview, viewGroup, false);
        TextView textView = (TextView) v.findViewById(R.id.textView);
        ImageView imageView = (ImageView) v.findViewById(R.id.imageView);
        textView.setText(this.title[i]);
        imageView.setImageResource(this.icons.getResourceId(i, -1));
        return v;
    }
}
