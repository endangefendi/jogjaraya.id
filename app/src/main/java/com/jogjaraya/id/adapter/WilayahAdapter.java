package com.jogjaraya.id.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.jogjaraya.id.R;
import com.jogjaraya.id.model.WilayahModel;

import java.util.ArrayList;
import java.util.List;


public class WilayahAdapter extends BaseAdapter {
    private Activity activity;
    private LayoutInflater inflater;
    private List<WilayahModel> item;
    private ArrayList<WilayahModel> listlokasiasli;


    public WilayahAdapter(Activity activity, List<WilayahModel> item) {
        this.activity = activity;
        this.item = item;

        listlokasiasli = new ArrayList<>();
        listlokasiasli.addAll(item);
    }

    @Override
    public int getCount() {
        return item.size();
    }

    @Override
    public Object getItem(int location) {
        return item.get(location);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @SuppressLint("InflateParams")
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (inflater == null)
            inflater = (LayoutInflater) activity
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (convertView == null) {
            assert inflater != null;
            convertView = inflater.inflate(R.layout.item_wilayah, null);
        }

        TextView nama = convertView.findViewById(R.id.tv_nama);

        WilayahModel m = item.get(position);

        nama.setText(m.getWilayah_nama());

        return convertView;
    }


    public void setList(List<WilayahModel> movieItems){
        this.listlokasiasli.addAll(item);
        notifyDataSetChanged();
    }
}
