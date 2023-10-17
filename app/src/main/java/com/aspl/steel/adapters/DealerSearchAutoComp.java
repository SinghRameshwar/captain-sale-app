package com.aspl.steel.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import com.aspl.steel.R;

import java.util.ArrayList;
import java.util.HashMap;

public class DealerSearchAutoComp extends ArrayAdapter<HashMap<String,String>> implements Filterable {

    private ArrayList<HashMap<String,String>> fullList;
    private ArrayList<HashMap<String,String>> mOriginalValues;
    Context context;
    private ArrayFilter mFilter;

    public DealerSearchAutoComp(Context context, int resource, ArrayList<HashMap<String,String>> objects) {
        super(context, resource, objects);
        this.fullList = objects;
        this.mOriginalValues = fullList;
        this.context=context;

    }

    @Override
    public int getCount() {
        return fullList.size();
    }

    @Override
    public HashMap<String, String> getItem(int position) {
        return fullList.get(position);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(android.R.layout.simple_dropdown_item_1line, parent,
                false);

        TextView textView=(TextView)rowView.findViewById(android.R.id.text1);

        HashMap<String,String> hashMap=fullList.get(position);
        textView.setText(hashMap.get("party_name"));

        return rowView;

    }

    @Override
    public Filter getFilter() {
        if (mFilter == null) {
            mFilter = new ArrayFilter();
        }
        return mFilter;
    }


    private class ArrayFilter extends Filter {
        private Object lock;

        @Override
        protected FilterResults performFiltering(CharSequence prefix) {
            FilterResults results = new FilterResults();

            if (mOriginalValues == null) {
                synchronized (lock) {
                    mOriginalValues = fullList;
                }
            }

            if (prefix == null || prefix.length() == 0) {
                synchronized (lock) {
                    ArrayList<HashMap<String,String>> list = mOriginalValues;
                    results.values = list;
                    results.count = list.size();
                }
            } else {
                final String prefixString = prefix.toString().toLowerCase();

                ArrayList<HashMap<String,String>> values = mOriginalValues;
                int count = values.size();

                ArrayList<HashMap<String,String>> newValues = new ArrayList<HashMap<String,String>>(count);
                for (int i = 0; i < count; i++) {
                    HashMap<String,String> itemMap = values.get(i);
                    String item= itemMap.get("party_name");
                    if (item.toLowerCase().contains(prefixString)) {
                        newValues.add(itemMap);
                    }

                }

                results.values = newValues;
                results.count = newValues.size();
            }

            return results;
        }

        @SuppressWarnings("unchecked")
        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {

            if(results.values!=null){
                fullList = (ArrayList<HashMap<String, String>>) results.values;
            }else{
                fullList = new ArrayList<HashMap<String, String>>();
            }
            if (results.count > 0) {
                notifyDataSetChanged();
            } else {
                notifyDataSetInvalidated();
            }
        }
    }
}
