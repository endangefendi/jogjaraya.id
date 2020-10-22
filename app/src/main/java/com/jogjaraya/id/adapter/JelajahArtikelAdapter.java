package com.jogjaraya.id.adapter;

import android.content.Context;
import android.content.Intent;
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
import com.jogjaraya.id.activity.DetailJelajahActivity;
import com.jogjaraya.id.activity.FullFotoActivity;
import com.jogjaraya.id.model.JelajahArtikelModel;

import java.util.ArrayList;
import java.util.List;

public class JelajahArtikelAdapter extends RecyclerView.Adapter<JelajahArtikelAdapter.Holder> {
    private static final String TAG = "MenuIconAdapter";
    private Context context;
    private List<JelajahArtikelModel> list;
    private JelajahArtikelAdapter.OnItemClickListener listener;

    public JelajahArtikelAdapter(Context context, JelajahArtikelAdapter.OnItemClickListener listener) {
        this.context = context;
        this.listener = listener;
        list = new ArrayList<>();
    }
    public void addList(List<JelajahArtikelModel> list) {
        this.list = list;
        notifyDataSetChanged();
    }

    static class Holder extends RecyclerView.ViewHolder {

        private TextView alamat;
        private TextView judul;
        private TextView views;
        private TextView selengkapnya;
        private TextView isi;
        private ImageView gambar;
        private MaterialRippleLayout lyt_parent;

        private Holder(View v) {
            super(v);
            views = v.findViewById(R.id.views);
            alamat = v.findViewById(R.id.alamat);
            judul = v.findViewById(R.id.judul);
            gambar = v.findViewById(R.id.gambar);
            isi = v.findViewById(R.id.isi);
            selengkapnya = v.findViewById(R.id.selengkapnya);
            lyt_parent = v.findViewById(R.id.lyt_parent);

        }
    }

    @NonNull
    @Override
    public JelajahArtikelAdapter.Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_jelajah_artikel, parent, false);
        JelajahArtikelAdapter.Holder sh = new Holder(view);

        return sh;
    }

    @Override
    public void onBindViewHolder(@NonNull final JelajahArtikelAdapter.Holder holder, final int position) {
        final JelajahArtikelModel item = list.get(position);

        Glide.with(context).load(item.getListing_gambar())
                .placeholder(R.drawable.loading_placeholder)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .into(holder.gambar);

        holder.isi.setText(item.getListing_deskripsi()+"...");
        holder.alamat.setText(item.getListing_alamat()+"...");

        holder.judul.setText(item.getListing_nama());
//        holder.lyt_parent.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                if (listener != null) listener.onItemClicked(position, item);
//            }
//        });

        holder.gambar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, FullFotoActivity.class);
                intent.putExtra("foto", item.getListing_gambar());
                intent.putExtra("title", item.getListing_nama());
                context.startActivity(intent);
            }
        });

        holder.isi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, DetailJelajahActivity.class);
                intent.putExtra("listing_id", item.getListing_id());
                context.startActivity(intent);
            }
        });
        holder.selengkapnya.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, DetailJelajahActivity.class);
                intent.putExtra("listing_id", item.getListing_id());
                context.startActivity(intent);
            }
        });

    }


    @Override
    public int getItemCount() {
        return list.size();
    }

    public void setOnItemClickListener(JelajahArtikelAdapter.OnItemClickListener listener){
        this.listener = listener;
    }

    public interface OnItemClickListener {
        void onItemClicked(int position, JelajahArtikelModel item);
    }

}
