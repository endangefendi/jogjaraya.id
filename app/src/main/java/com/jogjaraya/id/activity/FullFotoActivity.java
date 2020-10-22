package com.jogjaraya.id.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.jogjaraya.id.R;
import com.jogjaraya.id.view.TouchImageView;


public class FullFotoActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_full_foto);
        init();
    }

    private void init() {
        TouchImageView imgDisplay = findViewById(R.id.imgDisplay);
        if (getIntent()!=null){
            String foto = getIntent().getStringExtra("foto");
            String title = getIntent().getStringExtra("title");
            Glide.with(this).load(foto)
                    .placeholder(R.drawable.loading_placeholder)
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .into(imgDisplay);
            TextView tvtitle = findViewById(R.id.title);
            tvtitle.setText(title);
        }

    findViewById(R.id.btnClose).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }
}
