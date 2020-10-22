package com.jogjaraya.id.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.balysv.materialripple.MaterialRippleLayout;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.jogjaraya.id.R;
import com.jogjaraya.id.model.MenuIconModel;

import java.util.ArrayList;
import java.util.List;

public class MenuIconAdapter extends RecyclerView.Adapter<MenuIconAdapter.Holder> {
    private static final String TAG = "MenuIconAdapter";
    private Context context;
    private List<MenuIconModel> list;
    private MenuIconAdapter.OnItemClickListener listener;

    public MenuIconAdapter(Context context, MenuIconAdapter.OnItemClickListener listener) {
        this.context = context;
        this.listener = listener;
        list = new ArrayList<>();
    }
    public void addList(List<MenuIconModel> list) {
        this.list = list;
        notifyDataSetChanged();
    }

    static class Holder extends RecyclerView.ViewHolder {

        private ImageView image;
        private TextView judul;
        private MaterialRippleLayout lyt_parent;

        private Holder(View v) {
            super(v);
            judul = v.findViewById(R.id.judul);
            image = v.findViewById(R.id.image);
            lyt_parent = v.findViewById(R.id.lyt_parent);
        }
    }

    @NonNull
    @Override
    public MenuIconAdapter.Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_menu_icon, parent, false);
        MenuIconAdapter.Holder sh = new Holder(view);

        return sh;
    }

    @Override
    public void onBindViewHolder(@NonNull final MenuIconAdapter.Holder holder, final int position) {
        final MenuIconModel item = list.get(position);

        Glide.with(context).load(item.getIcon())
                .placeholder(R.drawable.loading_placeholder)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .into(holder.image);
        holder.judul.setText(item.getJudul());
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

    public void setOnItemClickListener(MenuIconAdapter.OnItemClickListener listener){
        this.listener = listener;
    }

    public interface OnItemClickListener {
        void onItemClicked(int position, MenuIconModel item);
    }

}
