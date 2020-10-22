package com.jogjaraya.id.activity;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.jogjaraya.id.R;
import com.jogjaraya.id.model.AgendaModel;
import com.jogjaraya.id.network.Constant;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Map;

public class DetailAgendaActivity extends AppCompatActivity {
    private static final String TAG = "DetailAgendaAct";
    private ProgressDialog progressDialog;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_agenda);
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Preparing data..");
        progressDialog.show();
        String event_id = getIntent().getStringExtra("event_id");
        loadDetailAgenda(event_id);
    }

    private void loadDetailAgenda(String event_id) {
        final RequestQueue requestQueue = Volley.newRequestQueue(this);
        StringRequest request = new StringRequest(Request.Method.GET, Constant.URL.DETAIL_AGENDA + event_id, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.e(TAG, "onResponse DETAIL_AGENDA" + response);
                try {
                    JSONObject object = new JSONObject(response);
                    int status = object.getInt("status");
                    String message = object.getString("message");
                    if (status == 200 && !message.equalsIgnoreCase("")) {

                        String field_data = object.getString("data");
                        JSONObject data = new JSONObject(field_data);
                        String event_judul = data.getString("event_judul");
                        String event_tanggal_mulai = data.getString("event_tanggal_mulai");
                        String event_tanggal_selesai = data.getString("event_tanggal_selesai");
                        String event_jam = data.getString("event_jam");
                        String event_lokasi = data.getString("event_lokasi");
                        String event_deskripsi = data.getString("event_deskripsi");
                        String event_penyelenggara = data.getString("event_penyelenggara");
                        String event_kontak = data.getString("event_kontak");
                        String event_tiket = data.getString("event_tiket");
                        String event_gambar = data.getString("event_gambar");
                        String event_wilayah_kode = data.getString("event_wilayah_kode");
                        viewDetailAgenda(event_judul, event_tanggal_mulai, event_tanggal_selesai, event_jam, event_lokasi, event_deskripsi, event_penyelenggara, event_kontak, event_tiket, event_wilayah_kode, event_gambar);

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "onErrorResponse: " + error);
                Constant.parseError(DetailAgendaActivity.this, error);
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                return Constant.getHeaders();
            }
        };
        request.setRetryPolicy(Constant.getDefaultRetryPolicy());
        requestQueue.add(request);
    }


    private void viewDetailAgenda(String event_judul, String event_tanggal_mulai, String event_tanggal_selesai, String event_jam, String event_lokasi, String event_deskripsi, String event_penyelenggara, String event_kontak, String event_tiket, String event_wilayah_kode, String event_gambar) {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        @SuppressLint("InflateParams") final View dialogView = inflater.inflate(R.layout.dialog_detail_agenda, null);
        dialogBuilder.setView(dialogView);
        progressDialog.dismiss();
        final AlertDialog b = dialogBuilder.create();
        b.setCanceledOnTouchOutside(true);
        b.setCancelable(false);
        b.show();

        TextView btnClose = dialogView.findViewById(R.id.btnClose);
        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                b.dismiss();
                startActivity(new Intent(DetailAgendaActivity.this,MainActivity.class));
                finish();
            }
        });
        SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        SimpleDateFormat fmtOut = new SimpleDateFormat("dd MMM yyyy",Locale.getDefault());
        Date dateawal = null;
        Date dateakhir = null;
        try {
            dateawal = fmt.parse(event_tanggal_mulai);
            dateakhir = fmt.parse(event_tanggal_selesai);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        String tgl_mulai = fmtOut.format(dateawal);
        String tgl_selesai = fmtOut.format(dateakhir);

        TextView tanggal = dialogView.findViewById(R.id.tanggal);
        tanggal.setText(tgl_mulai+" s/d "+tgl_selesai);

        TextView alamat = dialogView.findViewById(R.id.lokasi);
        alamat.setText(event_lokasi);

        TextView jam = dialogView.findViewById(R.id.jam);
        jam.setText(event_jam);

        TextView penyelenggara = dialogView.findViewById(R.id.penyelenggara);
        penyelenggara.setText(event_penyelenggara);

        TextView kontak = dialogView.findViewById(R.id.kontak);
        kontak.setText(event_kontak);

        TextView tiket = dialogView.findViewById(R.id.tiket);
        tiket.setText(event_tiket);

        TextView judul = dialogView.findViewById(R.id.judul);
        judul.setText(event_judul);

        TextView des = dialogView.findViewById(R.id.des);
        des.setText(event_deskripsi);

        ImageView gambar = dialogView.findViewById(R.id.gambar);
        Glide.with(this).load(event_gambar)
                .placeholder(R.drawable.loading_placeholder)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .into(gambar);

        ImageView img_date = dialogView.findViewById(R.id.date);
        ImageView img_jam = dialogView.findViewById(R.id.jam_img);
        ImageView img_lokasi = dialogView.findViewById(R.id.lokasi_img);
        ImageView img_tiket = dialogView.findViewById(R.id.tiket_img);
        ImageView img_kontak = dialogView.findViewById(R.id.kontak_img);
        ImageView img_penyelenggara = dialogView.findViewById(R.id.penyelenggara_img);

        if (event_tanggal_mulai.equalsIgnoreCase("") || event_tanggal_mulai.equalsIgnoreCase(" ")){
            if (event_tanggal_selesai.equalsIgnoreCase("") || event_tanggal_selesai.equalsIgnoreCase(" ")) {
                img_date.setVisibility(View.GONE);
                tanggal.setVisibility(View.GONE);
            }
        }

        if (event_jam.equalsIgnoreCase("") || event_jam.equalsIgnoreCase(" ")){
            jam.setVisibility(View.GONE);
            img_jam.setVisibility(View.GONE);
        }

        if (event_penyelenggara.equalsIgnoreCase("") || event_penyelenggara.equalsIgnoreCase(" ")){
            penyelenggara.setVisibility(View.GONE);
            img_penyelenggara.setVisibility(View.GONE);
        }
        if (event_kontak.equalsIgnoreCase("") || event_kontak.equalsIgnoreCase(" ")){
            kontak.setVisibility(View.GONE);
            img_kontak.setVisibility(View.GONE);
        }

        if (event_tiket.equalsIgnoreCase("") || event_tiket.equalsIgnoreCase(" ")){
            img_tiket.setVisibility(View.GONE);
            tiket.setVisibility(View.GONE);
        }
        if (event_lokasi.equalsIgnoreCase("") || event_lokasi.equalsIgnoreCase(" ")){
            img_lokasi.setVisibility(View.GONE);
            alamat.setVisibility(View.GONE);
        }

        if (event_deskripsi.equalsIgnoreCase("") || event_deskripsi.equalsIgnoreCase(" ")){
            des.setText("-");
        }



    }

}
