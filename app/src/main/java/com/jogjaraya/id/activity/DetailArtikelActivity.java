package com.jogjaraya.id.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
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
import com.jogjaraya.id.adapter.HomeArtikelAdapter;
import com.jogjaraya.id.model.HomeArtikelModel;
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

import static com.jogjaraya.id.network.Constant.URL.close_html;
import static com.jogjaraya.id.network.Constant.URL.open_html;

public class DetailArtikelActivity extends AppCompatActivity implements HomeArtikelAdapter.OnItemClickListener, SwipeRefreshLayout.OnRefreshListener {
    private final static String TAG= "DetailArtikelAct";

    private TextView judul,tanggal, view, author, authornya;
    private WebView isi;
    private ImageView gambar, iv_img, iv_date;
    private SwipeRefreshLayout refreshLayout;

    private String from;String slug;
    private RelativeLayout frameTanggalartikel,frameTanggal;

    // Home Artikel
    private HomeArtikelAdapter home_artikel_adapter;
    private List<HomeArtikelModel> home_artikel_list;
    private RecyclerView recyclerView_home_artikel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_artikel);
        from = getIntent().getStringExtra("from");
        initalToolbar();
        inital();
        init_artikel();
        slug = getIntent().getStringExtra("slug");
        from = getIntent().getStringExtra("from");
        if (from.equalsIgnoreCase("artikel")){
            load_detail_artikel(slug);
        }else{
            load_detail_info(slug);
        }
    }

    private void load_detail_artikel(String slug) {
        TextView title = findViewById(R.id.title);
        title.setText(getResources().getString(R.string.title_detail_artikel));
        final RequestQueue requestQueue = Volley.newRequestQueue(this);
        StringRequest request = new StringRequest(Request.Method.GET, Constant.URL.DETAIL_ARTIKEL+slug, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.e(TAG, "onResponse" + response);
                try {
                    JSONObject object = new JSONObject(response);
                    int status = object.getInt("status");
                    String message = object.getString("message");
                    if (status == 200 && !message.equalsIgnoreCase("") ) {
                        String field_data = object.getString("data");
                        JSONObject data = new JSONObject(field_data);
                        String tulisan_slug = data.getString("tulisan_slug");
                        String tulisan_judul = data.getString("tulisan_judul");
                        String tulisan_isi = data.getString("tulisan_isi");
                        String tulisan_gambar = data.getString("tulisan_gambar");
                        String tulisan_tanggal = data.getString("tulisan_tanggal");
                        String tulisan_views = data.getString("tulisan_views");
                        String tulisan_author = data.getString("tulisan_author");
                        setViewDetail(tulisan_slug, tulisan_judul, tulisan_isi, tulisan_gambar, tulisan_tanggal,tulisan_views,tulisan_author);
                        load_home_artikel();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "onErrorResponse: " + error);
                Constant.parseError(DetailArtikelActivity.this, error);
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

    private void initalToolbar() {
        ImageView back = findViewById(R.id.iv_back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    private void init_artikel() {
        recyclerView_home_artikel = findViewById(R.id.list_artikel);
        home_artikel_adapter = new HomeArtikelAdapter(this, this);
        LinearLayoutManager layoutManagerKategori = new LinearLayoutManager(this);
        layoutManagerKategori.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView_home_artikel.setLayoutManager(layoutManagerKategori);
        recyclerView_home_artikel.setHasFixedSize(true);
        recyclerView_home_artikel.setNestedScrollingEnabled(false);
        recyclerView_home_artikel.setAdapter(home_artikel_adapter);
    }

    private void inital() {
        frameTanggalartikel = findViewById(R.id.frameTanggalartikel);
        frameTanggal = findViewById(R.id.frameTanggal);
        iv_img = findViewById(R.id.img);
        iv_date = findViewById(R.id.date);
        author = findViewById(R.id.author);
        authornya = findViewById(R.id.authornya);
        view = findViewById(R.id.views);
        judul = findViewById(R.id.judul);
        isi = findViewById(R.id.isi);
        refreshLayout = findViewById(R.id.refresh);
        gambar = findViewById(R.id.gambar);
        tanggal = findViewById(R.id.tanggal);
        refreshLayout.setOnRefreshListener(this);
        refreshLayout.post(new Runnable() {
            @Override
            public void run() {
                refreshLayout.setRefreshing(true);
            }
        });
    }

    private void load_detail_info(String slug) {
        TextView title = findViewById(R.id.title);
        title.setText(getResources().getString(R.string.title_detail_info));
        final RequestQueue requestQueue = Volley.newRequestQueue(this);
        StringRequest request = new StringRequest(Request.Method.GET, Constant.URL.DETAIL_DARURAT+slug, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.e(TAG, "onResponse DETAIL_ARTIKEL" + response);
                try {
                    JSONObject object = new JSONObject(response);
                    int status = object.getInt("status");
                    String message = object.getString("message");
                    if (status == 200 && !message.equalsIgnoreCase("") ) {
                        String field_data = object.getString("data");
                        JSONObject data = new JSONObject(field_data);
                        String tulisan_slug = data.getString("slug");
                        String tulisan_judul = data.getString("judul");
                        String tulisan_isi = data.getString("isi");
                        String tulisan_gambar = data.getString("gambar");
                        String tulisan_author = data.getString("author");
                        setViewDetail(tulisan_slug, tulisan_judul, tulisan_isi, tulisan_gambar,tulisan_author);
                        load_home_artikel();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "onErrorResponse: " + error);
                Constant.parseError(DetailArtikelActivity.this, error);
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

    private void setViewDetail(String tulisan_slug, final String tulisan_judul, String tulisan_isi, final String tulisan_gambar, String tulisan_author) {
        refreshLayout.setRefreshing(false);
        frameTanggalartikel.setVisibility(View.GONE);
        frameTanggal.setVisibility(View.VISIBLE);
        authornya.setText(tulisan_author);
        judul.setText(tulisan_judul);

        Glide.with(this).load(tulisan_gambar)
                .placeholder(R.drawable.loading_placeholder)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .into(gambar);

        isi.loadDataWithBaseURL(null, open_html+tulisan_isi+close_html, "text/html", "UTF-8","about:blank");
        gambar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DetailArtikelActivity.this, FullFotoActivity.class);
                intent.putExtra("foto", tulisan_gambar);
                intent.putExtra("title", tulisan_judul);
                startActivity(intent);
            }
        });
    }

    private void load_home_artikel() {
        refreshLayout.setRefreshing(true);
        home_artikel_list=new ArrayList<>();
        home_artikel_list.clear();
        final RequestQueue requestQueue = Volley.newRequestQueue(this);
        StringRequest request = new StringRequest(Request.Method.GET, Constant.URL.HOME_ARTIKEL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.e(TAG, "onResponse" + response);
                try {
                    JSONObject object = new JSONObject(response);
                    int status = object.getInt("status");
                    String message = object.getString("message");
                    if (status == 200 && !message.equalsIgnoreCase("") ) {
                        String data = object.getString("data");
                        JSONArray array = new JSONArray(data);
                        for (int i = 0; i < array.length(); i++) {
                            JSONObject obj = array.getJSONObject(i);
                            String tulisan_slug = obj.getString("tulisan_slug");
                            String tulisan_judul = obj.getString("tulisan_judul");
                            String tulisan_isi = obj.getString("tulisan_isi");
                            String tulisan_gambar = obj.getString("tulisan_gambar");
                            String tulisan_tanggal = obj.getString("tulisan_tanggal");
                            String tulisan_views = obj.getString("tulisan_views");

                            HomeArtikelModel item = new HomeArtikelModel(tulisan_slug, tulisan_judul, tulisan_isi, tulisan_gambar, tulisan_tanggal,tulisan_views);
                            home_artikel_list.add(item);
                        }
                        home_artikel_adapter.addList(home_artikel_list);
                        home_artikel_adapter.notifyDataSetChanged();
                        refreshLayout.setRefreshing(false);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "onErrorResponse: " + error);
                Constant.parseError(DetailArtikelActivity.this, error);
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

    private void setViewDetail(String tulisan_slug, final String tulisan_judul, String tulisan_isi, final String tulisan_gambar, String tulisan_tanggal, String tulisan_views, String tulisan_author) {
        refreshLayout.setRefreshing(false);
        frameTanggalartikel.setVisibility(View.VISIBLE);
        frameTanggal.setVisibility(View.GONE);
        author.setText(tulisan_author);
        judul.setText(tulisan_judul);
        view.setText(tulisan_views);
        Glide.with(this).load(tulisan_gambar)
                .placeholder(R.drawable.loading_placeholder)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .into(gambar);
        // view format tanggal
        SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        SimpleDateFormat fmtOut = new SimpleDateFormat("dd/MM/yyyy",Locale.getDefault());
        Date date = null;
        try {
            date = fmt.parse(tulisan_tanggal);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        String tgl_ = fmtOut.format(date);
        tanggal.setText(tgl_);
        isi.loadDataWithBaseURL(null, open_html+tulisan_isi+close_html, "text/html", "UTF-8","about:blank");
        gambar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DetailArtikelActivity.this, FullFotoActivity.class);
                intent.putExtra("foto", tulisan_gambar);
                intent.putExtra("title", tulisan_judul);
                startActivity(intent);
            }
        });
    }

    @Override
    public void onRefresh() {
        if (from.equalsIgnoreCase("artikel")){
            load_detail_artikel(slug);
        }else{
            load_detail_info(slug);
        }
    }

    @Override
    public void onItemClicked(int position, HomeArtikelModel item) {
        Intent intent = new Intent(this, DetailArtikelActivity.class);
        intent.putExtra("slug", item.getTulisan_slug());
        intent.putExtra("from", "artikel");
        startActivity(intent);
    }
}
