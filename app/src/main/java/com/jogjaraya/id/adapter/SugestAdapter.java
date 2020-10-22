package com.jogjaraya.id.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.balysv.materialripple.MaterialRippleLayout;
import com.jogjaraya.id.R;
import com.jogjaraya.id.model.Sugest;

import java.util.ArrayList;
import java.util.List;


public class SugestAdapter extends RecyclerView.Adapter<SugestAdapter.Holder> {
       private static final String TAG = "SugestAdapter";
    private Context context;
    private List<Sugest> list;
    private OnItemClickListener listener;

    public SugestAdapter(Context context, OnItemClickListener listener) {
        this.context = context;
        this.listener = listener;
        list = new ArrayList<>();
    }
    public void addList(List<Sugest> list) {
        this.list = list;
        notifyDataSetChanged();
    }

    static class Holder extends RecyclerView.ViewHolder {

        private TextView nama;
        private MaterialRippleLayout lyt_parent;

        private Holder(View v) {
            super(v);
            nama = v.findViewById(R.id.tv_nama);
            lyt_parent = v.findViewById(R.id.lyt_parent);
        }
    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_sugest, parent, false);
        Holder sh = new Holder(view);

        return sh;
    }

    @Override
    public void onBindViewHolder(@NonNull Holder holder, final int position) {
        final Sugest item = list.get(position);
        holder.nama.setText(item.getSuggestion());

        holder.lyt_parent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (listener != null) listener.onItemClicked(position, item);
            }
        });
    }


    @Override
    public int getItemCount() {
        return list.size();
    }

    public void setOnItemClickListener(OnItemClickListener listener){
        this.listener = listener;
    }

    public interface OnItemClickListener {
        void onItemClicked(int position, Sugest item);
    }

}
