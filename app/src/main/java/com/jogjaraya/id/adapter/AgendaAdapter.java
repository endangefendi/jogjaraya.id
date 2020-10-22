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
import com.jogjaraya.id.model.AgendaModel;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class AgendaAdapter extends RecyclerView.Adapter<AgendaAdapter.Holder> {
    private static final String TAG = "AgendaAdapter";
    private Context context;
    private List<AgendaModel> list;
    private OnItemClickListener listener;

    public AgendaAdapter(Context context, OnItemClickListener listener) {
        this.context = context;
        this.listener = listener;
        list = new ArrayList<>();
    }
    public void addList(List<AgendaModel> list) {
        this.list = list;
        notifyDataSetChanged();
    }

    static class Holder extends RecyclerView.ViewHolder {

        private TextView alamat;
        private TextView judul;
        private TextView tanggal;
        private TextView selengkapnya;
        private ImageView gambar;
        private MaterialRippleLayout lyt_parent;

        private Holder(View v) {
            super(v);
            tanggal = v.findViewById(R.id.tanggal);
            alamat = v.findViewById(R.id.lokasi);
            judul = v.findViewById(R.id.judul);
            gambar = v.findViewById(R.id.gambar);
            selengkapnya = v.findViewById(R.id.selengkapnya);
//            lyt_parent = v.findViewById(R.id.lyt_parent);

        }
    }

    @NonNull
    @Override
    public AgendaAdapter.Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_agenda1, parent, false);
//        View view = LayoutInflater.from(context).inflate(R.layout.item_agenda, parent, false);
        AgendaAdapter.Holder sh = new Holder(view);

        return sh;
    }

    @Override
    public void onBindViewHolder(@NonNull final AgendaAdapter.Holder holder, final int position) {
        final AgendaModel item = list.get(position);

        Glide.with(context).load(item.getEvent_gambar())
                .placeholder(R.drawable.loading_placeholder)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .into(holder.gambar);
        SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        SimpleDateFormat fmtOut = new SimpleDateFormat("dd MMM yyyy",Locale.getDefault());
        Date dateawal = null;
        Date dateakhir = null;
        try {
            dateawal = fmt.parse(item.getEvent_tanggal_mulai());
            dateakhir = fmt.parse(item.getEvent_tanggal_selesai());
        } catch (ParseException e) {
            e.printStackTrace();
        }
        String tgl_mulai = fmtOut.format(dateawal);
        String tgl_selesai = fmtOut.format(dateakhir);
        holder.alamat.setText(item.getEvent_lokasi()+"...");

        holder.tanggal.setText(tgl_mulai+" s/d "+tgl_selesai);

        holder.judul.setText(item.getEvent_judul());

        holder.gambar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, FullFotoActivity.class);
                intent.putExtra("foto", item.getEvent_gambar());
                intent.putExtra("title", item.getEvent_judul());
                context.startActivity(intent);
            }
        });

        holder.tanggal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) listener.onItemClicked(position, item);
            }
        });
        holder.alamat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) listener.onItemClicked(position, item);
            }
        });

        holder.selengkapnya.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) listener.onItemClicked(position, item);
            }
        });
        holder.judul.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) listener.onItemClicked(position, item);
            }
        });

    }


    @Override
    public int getItemCount() {
        return list.size();
    }

    public void setOnItemClickListener(AgendaAdapter.OnItemClickListener listener){
        this.listener = listener;
    }

    public interface OnItemClickListener {
        void onItemClicked(int position, AgendaModel item);
    }

}
