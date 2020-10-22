package com.jogjaraya.id.activity;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.JsonRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.jogjaraya.id.R;
import com.jogjaraya.id.adapter.AgendaAdapter;
import com.jogjaraya.id.adapter.JelajahArtikelAdapter;
import com.jogjaraya.id.adapter.WilayahAdapter;
import com.jogjaraya.id.model.AgendaModel;
import com.jogjaraya.id.model.JelajahArtikelModel;
import com.jogjaraya.id.model.WilayahModel;
import com.jogjaraya.id.network.Constant;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import static com.jogjaraya.id.view.GridSpanCount.getGridSpanCount;

public class ListAgendaActivity extends AppCompatActivity implements AgendaAdapter.OnItemClickListener, SwipeRefreshLayout.OnRefreshListener {
    private static final String TAG = "ListAgendaAct";
    private SwipeRefreshLayout refreshLayout;
    private AgendaAdapter adapter;
    private List<AgendaModel> list = new ArrayList<>();
    private String url;
    private int page = 1;
    private RecyclerView recyclerView;
    private int previousTotal = 1;
    public boolean load = true;
    private int totalItemCount;
    private int visibleItemCount;
    private NestedScrollView scrollView;
    private View lyt_no_item;

    String kode_wilayah;
    String key;
    String akhir;
    String awal;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cari_jelajah);

        initalToolbar();
        inital();
        setNodata();
    }

    private void initalToolbar() {
        ImageView back = findViewById(R.id.iv_back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        TextView title = findViewById(R.id.title);
        key = getIntent().getStringExtra("key");
        if (!key.equalsIgnoreCase("")) {
            title.setText(getResources().getString(R.string.title_agenda) + " - " + key.toUpperCase());
        } else {
            title.setText(getResources().getString(R.string.title_agenda));
        }
    }

    private void inital() {
        lyt_no_item = findViewById(R.id.lyt_no_item);
        TextView text_refresh = findViewById(R.id.text_refresh);
        text_refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                startActivity(getIntent());
            }
        });
        refreshLayout = findViewById(R.id.refresh);
        refreshLayout.setOnRefreshListener(this);
        refreshLayout.post(new Runnable() {
            @Override
            public void run() {
                refreshLayout.setRefreshing(true);
            }
        });

        scrollView = findViewById(R.id.scroll_view);
        recyclerView = findViewById(R.id.list_item);

        LinearLayoutManager layoutManagerKategori = new LinearLayoutManager(this);
        layoutManagerKategori.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutManagerKategori);
//        recyclerView.setLayoutManager(new GridLayoutManager(this, getGridSpanCount(this)));
        recyclerView.setHasFixedSize(true);

        adapter = new AgendaAdapter(this, this);
        recyclerView.setAdapter(adapter);
    }


    @Override
    public void onRefresh() {
        setNodata();
    }

    private void setNodata() {
        // load data nya
        page = 1;
        previousTotal = 1;
        kode_wilayah = getIntent().getStringExtra("kode_wilayah");
        key = getIntent().getStringExtra("key");
        awal = getIntent().getStringExtra("awal");
        akhir = getIntent().getStringExtra("akhir");
        loadDataCari(kode_wilayah, key, awal, akhir);
    }

    private void loadDataCari(String kode_wilayah, String cari, String awal, String akhir) {
        try {
            list.clear();
            url = Constant.URL.CARI_AGENDA + page;
            RequestQueue requestQueue = Volley.newRequestQueue(this);
            JSONObject json = new JSONObject();
            json.put("wilayah_kode", kode_wilayah);
            json.put("cari", cari);
            json.put("tanggal_mulai", awal);
            json.put("tanggal_selesai", akhir);
            JsonRequest request = new JsonObjectRequest(Request.Method.POST, url, json, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    refreshLayout.setRefreshing(false);
                    Log.e(TAG, "onResponse CARI_AGENDA" + response);
                    try {
                        int status = response.getInt("status");
                        String data = response.getString("data");
                        String message = response.getString("message");
                        if (status == 200 && !message.equalsIgnoreCase("")) {
                            JSONArray array = new JSONArray(data);
                            if (array.length() == 0) {
                                lyt_no_item.setVisibility(View.VISIBLE);
                                recyclerView.setVisibility(View.GONE);
                            } else {
                                for (int i = 0; i < array.length(); i++) {
                                    JSONObject obj = array.getJSONObject(i);
                                    String event_id = obj.getString("event_id");
                                    String event_judul = obj.getString("event_judul");
                                    String event_tanggal_mulai = obj.getString("event_tanggal_mulai");
                                    String event_tanggal_selesai = obj.getString("event_tanggal_selesai");
                                    String event_deskripsi = obj.getString("event_deskripsi");
                                    String event_lokasi = obj.getString("event_lokasi");
                                    String event_wilayah_kode = obj.getString("event_wilayah_kode");
                                    String event_gambar = obj.getString("event_gambar");
                                    AgendaModel item = new AgendaModel(event_id, event_judul, event_tanggal_mulai,
                                            event_tanggal_selesai,
                                            event_deskripsi, event_lokasi, event_wilayah_kode,
                                            event_gambar);
                                    list.add(item);
                                }
                                adapter.addList(list);
                                adapter.notifyDataSetChanged();
                                recyclerView.setVisibility(View.VISIBLE);
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.d(TAG, "onErrorResponse: " + error);
                    Constant.parseError(ListAgendaActivity.this, error);
                }
            }) {
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    return Constant.getHeaders();
                }

            };
            request.setRetryPolicy(Constant.getDefaultRetryPolicy());
            requestQueue.add(request);
            pagination(cari);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void pagination(final String c) {
        final int visibleThreshold = 1;
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                visibleItemCount = recyclerView.getChildCount();
                totalItemCount = recyclerView.getLayoutManager().getItemCount();
                if (load) {
                    if (totalItemCount > previousTotal) {
                        previousTotal = totalItemCount;
                        load = false;
                        Log.e("pagination", "load Load load:" + load);
                    }
                }

            }
        });

        if (scrollView != null) {
            scrollView.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
                @Override
                public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                    if (scrollY == v.getChildAt(0).getMeasuredHeight() - v.getMeasuredHeight()) {
                        Log.e("Gesture", "BOTTOM SCROLL");
                        if (!load && (visibleItemCount) >= totalItemCount) {
                            page += visibleThreshold;
                            load = true;
                            Log.e("Gesture", "BOTTOM Load more:" + page);
                            getNext(page, c);
                        }
                    }
                }
            });
        }

    }

    private void getNext(int page, final String c) {
        try {
            url = Constant.URL.CARI_AGENDA + page;
            RequestQueue requestQueue = Volley.newRequestQueue(this);
            JSONObject json = new JSONObject();
            json.put("cari", c);
            JsonRequest request = new JsonObjectRequest(Request.Method.POST, url, json, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
//                Log.e(TAG, "onResponse" + response);
                    try {
                        int status = response.getInt("status");
                        String data = response.getString("data");
                        String message = response.getString("message");
                        if (status == 200 && !message.equalsIgnoreCase("")) {
                            JSONArray array = new JSONArray(data);
                            for (int i = 0; i < array.length(); i++) {
                                JSONObject obj = array.getJSONObject(i);
                                String event_id = obj.getString("event_id");
                                String event_judul = obj.getString("event_judul");
                                String event_tanggal_mulai = obj.getString("event_tanggal_mulai");
                                String event_tanggal_selesai = obj.getString("event_tanggal_selesai");
                                String event_deskripsi = obj.getString("event_deskripsi");
                                String event_lokasi = obj.getString("event_lokasi");
                                String event_wilayah_kode = obj.getString("event_wilayah_kode");
                                String event_gambar = obj.getString("event_gambar");
                                AgendaModel item = new AgendaModel(event_id, event_judul, event_tanggal_mulai,
                                        event_tanggal_selesai,
                                        event_deskripsi, event_lokasi, event_wilayah_kode,
                                        event_gambar);
                                list.add(item);
                            }
                            adapter.addList(list);
                            adapter.notifyDataSetChanged();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.d(TAG, "onErrorResponse: " + error);
                    Constant.parseError(ListAgendaActivity.this, error);
                }
            }) {
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    return Constant.getHeaders();
                }
            };
            request.setRetryPolicy(Constant.getDefaultRetryPolicy());
            requestQueue.add(request);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private ProgressDialog progressDialog;

    @Override
    public void onItemClicked(int position, AgendaModel item) {
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Preparing data..");
        progressDialog.show();

        loadDetailAgenda(item.getEvent_id());
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
                Constant.parseError(ListAgendaActivity.this, error);
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