package com.jogjaraya.id.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import androidx.viewpager.widget.PagerAdapter;

import com.balysv.materialripple.MaterialRippleLayout;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.jogjaraya.id.R;
import com.jogjaraya.id.model.GambarModel;

import java.util.List;



public class GambarAdapter extends PagerAdapter {

    private static final String TAG = "GambarAdapter";
    private Context context;
    private List<GambarModel> items;

    private OnItemClickListener onItemClickListener;

    public interface OnItemClickListener {
        void onItemClick(View view, GambarModel obj, int position);
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    // constructor
    public GambarAdapter(Context context, List<GambarModel> items) {
        this.context = context;
        this.items = items;
    }

    @Override
    public int getCount() {
        return this.items.size();
    }

    public GambarModel getItem(int pos) {
        return items.get(pos);
    }

    public void setItems(List<GambarModel> items) {
        this.items = items;
        notifyDataSetChanged();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public Object instantiateItem(ViewGroup container, final int position) {
        final GambarModel o = items.get(position);
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View v = inflater.inflate(R.layout.item_gambar, container, false);
        ImageView image = v.findViewById(R.id.image);
        MaterialRippleLayout lyt_parent = v.findViewById(R.id.lyt_parent);

        Glide.with(context).load(o.getGambar())
                .placeholder(R.drawable.loading_placeholder)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .thumbnail(0.5f)
                .into(image);

        lyt_parent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                if (onItemClickListener != null) {
                    onItemClickListener.onItemClick(v, o,position);
                }
            }
        });

        container.addView(v);

        return v;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((RelativeLayout) object);

    }

}
