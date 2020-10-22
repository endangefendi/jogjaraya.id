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
import com.jogjaraya.id.activity.FullFotoActivity;
import com.jogjaraya.id.model.HomeArtikelModel;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;


public class HomeArtikelAdapter extends RecyclerView.Adapter<HomeArtikelAdapter.Holder> {
    private static final String TAG = "MenuIconAdapter";
    private Context context;
    private List<HomeArtikelModel> list;
    private HomeArtikelAdapter.OnItemClickListener listener;

    public HomeArtikelAdapter(Context context, HomeArtikelAdapter.OnItemClickListener listener) {
        this.context = context;
        this.listener = listener;
        list = new ArrayList<>();
    }
    public void addList(List<HomeArtikelModel> list) {
        this.list = list;
        notifyDataSetChanged();
    }

    static class Holder extends RecyclerView.ViewHolder {

        private TextView views;
        private TextView judul;
        private TextView tanggal;
        private ImageView gambar;
        private TextView isi;
        private MaterialRippleLayout lyt_parent;

        private Holder(View v) {
            super(v);
            views = v.findViewById(R.id.views);
            judul = v.findViewById(R.id.judul);
            gambar = v.findViewById(R.id.gambar);
            isi = v.findViewById(R.id.isi);
            tanggal = v.findViewById(R.id.tanggal);
            lyt_parent = v.findViewById(R.id.lyt_parent);
        }
    }

    @NonNull
    @Override
    public HomeArtikelAdapter.Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_home_artikel, parent, false);
        HomeArtikelAdapter.Holder sh = new Holder(view);

        return sh;
    }

    @Override
    public void onBindViewHolder(@NonNull final HomeArtikelAdapter.Holder holder, final int position) {
        final HomeArtikelModel item = list.get(position);

        // view format tanggal
        SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        SimpleDateFormat fmtOut = new SimpleDateFormat("dd/MM/yyyy",Locale.getDefault());
        Date date = null;
        try {
            date = fmt.parse(item.getTulisan_tanggal());
        } catch (ParseException e) {
            e.printStackTrace();
        }
        String tgl_ = fmtOut.format(date);
        holder.tanggal.setText(tgl_);
        holder.views.setText(item.getTulisan_views());

        holder.isi.setText(item.getTulisan_isi()+"...");

        holder.judul.setText(item.getTulisan_judul());
        holder.lyt_parent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (listener != null) listener.onItemClicked(position, item);
            }
        });
        Glide.with(context).load(item.getTulisan_gambar())
                .placeholder(R.drawable.loading_placeholder)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .into(holder.gambar);
        holder.gambar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, FullFotoActivity.class);
                intent.putExtra("foto", item.getTulisan_gambar());
                intent.putExtra("title", item.getTulisan_judul());
                context.startActivity(intent);
            }
        });
    }


    @Override
    public int getItemCount() {
        return list.size();
    }

    public void setOnItemClickListener(HomeArtikelAdapter.OnItemClickListener listener){
        this.listener = listener;
    }

    public interface OnItemClickListener {
        void onItemClicked(int position, HomeArtikelModel item);
    }

}
