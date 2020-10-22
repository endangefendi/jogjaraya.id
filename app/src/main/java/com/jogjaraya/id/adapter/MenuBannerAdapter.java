package com.jogjaraya.id.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.balysv.materialripple.MaterialRippleLayout;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.jogjaraya.id.R;
import com.jogjaraya.id.model.MenuBannerModel;

import java.util.ArrayList;
import java.util.List;

public class MenuBannerAdapter extends RecyclerView.Adapter<MenuBannerAdapter.Holder> {
    private static final String TAG = "MenuBannerAdapter";
    private Context context;
    private List<MenuBannerModel> list;
    private MenuBannerAdapter.OnItemClickListener listener;

    public MenuBannerAdapter(Context context, MenuBannerAdapter.OnItemClickListener listener) {
        this.context = context;
        this.listener = listener;
        list = new ArrayList<>();
    }
    public void addList(List<MenuBannerModel> list) {
        this.list = list;
        notifyDataSetChanged();
    }

    static class Holder extends RecyclerView.ViewHolder {

        private ImageView image;
        private MaterialRippleLayout lyt_parent;

        private Holder(View v) {
            super(v);
            image = v.findViewById(R.id.image);
            lyt_parent = v.findViewById(R.id.lyt_parent);
        }
    }

    @NonNull
    @Override
    public MenuBannerAdapter.Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_menu_banner, parent, false);
        MenuBannerAdapter.Holder sh = new Holder(view);

        return sh;
    }

    @Override
    public void onBindViewHolder(@NonNull final MenuBannerAdapter.Holder holder, final int position) {
        final MenuBannerModel item = list.get(position);

        Glide.with(context).load(item.getBanner())
                .placeholder(R.drawable.loading_placeholder)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .into(holder.image);

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

    public void setOnItemClickListener(MenuBannerAdapter.OnItemClickListener listener){
        this.listener = listener;
    }

    public interface OnItemClickListener {
        void onItemClicked(int position, MenuBannerModel item);
    }

}
