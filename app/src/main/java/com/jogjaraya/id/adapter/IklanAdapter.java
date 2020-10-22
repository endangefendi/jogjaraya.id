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
import com.jogjaraya.id.model.IklanModel;

import java.util.ArrayList;
import java.util.List;

public class IklanAdapter extends RecyclerView.Adapter<IklanAdapter.Holder> {
    private static final String TAG = "MenuBannerAdapter";
    private Context context;
    private List<IklanModel> list;
    private IklanAdapter.OnItemClickListener listener;

    public IklanAdapter(Context context, IklanAdapter.OnItemClickListener listener) {
        this.context = context;
        this.listener = listener;
        list = new ArrayList<>();
    }
    public void addList(List<IklanModel> list) {
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
    public IklanAdapter.Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_iklan, parent, false);
        IklanAdapter.Holder sh = new Holder(view);

        return sh;
    }

    @Override
    public void onBindViewHolder(@NonNull final IklanAdapter.Holder holder, final int position) {
        final IklanModel item = list.get(position);

        Glide.with(context).load(item.getIklan_gambar())
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

    public void setOnItemClickListener(IklanAdapter.OnItemClickListener listener){
        this.listener = listener;
    }

    public interface OnItemClickListener {
        void onItemClicked(int position, IklanModel item);
    }

}
