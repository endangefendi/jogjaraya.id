package com.jogjaraya.id.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.JsonRequest;
import com.android.volley.toolbox.Volley;
import com.jogjaraya.id.R;
import com.jogjaraya.id.adapter.ArtikelAdapter;
import com.jogjaraya.id.model.ArtikelModel;
import com.jogjaraya.id.network.Constant;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

public class CariArtikelActivity extends AppCompatActivity implements ArtikelAdapter.OnItemClickListener, SwipeRefreshLayout.OnRefreshListener {
    private static final String TAG = "CariArtikelAct";
    private ImageButton bt_clear;
    private EditText et_search;
    private List<ArtikelModel> list;
    private ArtikelAdapter adapter;
    private View lyt_no_item;
    private SwipeRefreshLayout refreshLayout;
    private String url;
    private int page = 1;
    private RecyclerView recyclerView;
    private int previousTotal = 1;
    public boolean load = true;
    private int totalItemCount;
    private int visibleItemCount;
    private NestedScrollView scrollView;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cari_artikel);
        initial();
        initalData();
        setNodata();
    }

    private void initalData() {
        progressBar = findViewById(R.id.progress_bar);
        progressBar.setVisibility(View.VISIBLE);
        refreshLayout = findViewById(R.id.swipe_refresh_layout);
        refreshLayout.setOnRefreshListener(this);
        list = new ArrayList<>();
        scrollView = findViewById(R.id.scroll_view);
        recyclerView = findViewById(R.id.recyclerView);
        LinearLayoutManager layoutManagerKategori = new LinearLayoutManager(this);
        layoutManagerKategori.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutManagerKategori);
        recyclerView.setHasFixedSize(true);
        adapter = new ArtikelAdapter(this, this);
        recyclerView.setAdapter(adapter);
    }

    private void initial() {
        lyt_no_item = findViewById(R.id.lyt_no_item);
        TextView text_refresh = findViewById(R.id.text_refresh);
        text_refresh.setText("Wangsuli dateng ensiklopedia");
        text_refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                et_search.setText("");
                setNodata();
            }
        });

        et_search = findViewById(R.id.et_search);
        bt_clear = findViewById(R.id.bt_clear);
        bt_clear.setVisibility(View.GONE);
        bt_clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                et_search.setText("");
                setNodata();
            }
        });

        et_search.addTextChangedListener(
                new TextWatcher() {
                    @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                        if (s.toString().trim().equalsIgnoreCase("")) {
                            list.clear();
                            bt_clear.setVisibility(View.GONE);
                            lyt_no_item.setVisibility(View.GONE);
                        } else {
                            list.clear();
                            bt_clear.setVisibility(View.VISIBLE);
                        }
                    }

                    @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

                    private Timer timer=new Timer();
                    private final long DELAY = 1000; // milliseconds

                    @Override
                    public void afterTextChanged(final Editable s) {
                        timer.cancel();
                        timer = new Timer();
                        timer.schedule(
                                new TimerTask() {
                                    @Override
                                    public void run() {
                                        Cari(s.toString());
                                    }
                                },
                                DELAY
                        );
                    }
                }
        );

        ImageButton back = findViewById(R.id.iv_back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        et_search.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    page = 1;
                    previousTotal = 1;
                    list.clear();
                    Cari(v.getText().toString());
                }
                return false;
            }
        });
    }

    private void Cari(final String c) {
        try {
        list.clear();
        url = Constant.URL.CARI_ARTIKEL+page;
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        JSONObject json = new JSONObject();
        json.put("cari", c);
        JsonRequest request = new JsonObjectRequest(Request.Method.POST, url, json, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.e(TAG, "onResponse CARI_ARTIKEL" + response);
                try {
                    int status = response.getInt("status");
                    String data = response.getString("data");
                    String message = response.getString("message");
                    if (status == 200 && !message.equalsIgnoreCase("") ) {
                        JSONArray array = new JSONArray(data);
                        if (array.length()==0) {
                            lyt_no_item.setVisibility(View.VISIBLE);
                            refreshLayout.setRefreshing(false);
                            recyclerView.setVisibility(View.GONE);
                            progressBar.setVisibility(View.GONE);
                        }else {
                            for (int i = 0; i < array.length(); i++) {
                                JSONObject obj = array.getJSONObject(i);
                                int tulisan_views = obj.getInt("tulisan_views");
                                int tulisan_rating = obj.getInt("tulisan_rating");
                                String tulisan_slug = obj.getString("tulisan_slug");
                                String tulisan_judul = obj.getString("tulisan_judul");
                                String tulisan_isi = obj.getString("tulisan_isi");
                                String tulisan_gambar = obj.getString("tulisan_gambar");
                                String tulisan_kategori_nama = obj.getString("tulisan_kategori_nama");
                                String tulisan_kategori_slug = obj.getString("tulisan_kategori_slug");
                                String tulisan_tanggal = obj.getString("tulisan_tanggal");
                                String tulisan_author = obj.getString("tulisan_author");
                                ArtikelModel item = new ArtikelModel(tulisan_slug, tulisan_judul, tulisan_isi, tulisan_gambar, tulisan_tanggal,
                                        tulisan_views, tulisan_rating, tulisan_kategori_slug, tulisan_kategori_nama, tulisan_author);
                                list.add(item);
                            }
                            adapter.addList(list);
                            adapter.notifyDataSetChanged();
                            refreshLayout.setRefreshing(false);
                            recyclerView.setVisibility(View.VISIBLE);
                            lyt_no_item.setVisibility(View.GONE);
                            progressBar.setVisibility(View.GONE);
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
                Constant.parseError(CariArtikelActivity.this, error);
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                return Constant.getHeaders();
            }

        };
        request.setRetryPolicy(Constant.getDefaultRetryPolicy());
        requestQueue.add(request);
        pagination(c);
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
                            getNext(page,c);
                        }
                    }
                }
            });
        }

    }

    private void getNext(int page, final String c) {
        try {
            url = Constant.URL.CARI_ARTIKEL+page;
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
                        if (status == 200 && !message.equalsIgnoreCase("") ) {
                            JSONArray array = new JSONArray(data);
                            for (int i = 0; i < array.length(); i++) {
                                JSONObject obj = array.getJSONObject(i);
                                int tulisan_views = obj.getInt("tulisan_views");
                                int tulisan_rating = obj.getInt("tulisan_rating");
                                String tulisan_slug = obj.getString("tulisan_slug");
                                String tulisan_judul = obj.getString("tulisan_judul");
                                String tulisan_isi = obj.getString("tulisan_isi");
                                String tulisan_gambar = obj.getString("tulisan_gambar");
                                String tulisan_kategori_nama = obj.getString("tulisan_kategori_nama");
                                String tulisan_kategori_slug = obj.getString("tulisan_kategori_slug");
                                String tulisan_tanggal = obj.getString("tulisan_tanggal");
                                String tulisan_author = obj.getString("tulisan_author");
                                ArtikelModel item = new ArtikelModel(tulisan_slug, tulisan_judul, tulisan_isi, tulisan_gambar, tulisan_tanggal,
                                        tulisan_views, tulisan_rating, tulisan_kategori_slug, tulisan_kategori_nama, tulisan_author);
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
                    Constant.parseError(CariArtikelActivity.this, error);
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

    @Override
    public void onItemClicked(int position, ArtikelModel item) {
        Intent intent = new Intent(CariArtikelActivity.this, DetailArtikelActivity.class);
        intent.putExtra("slug",item.getTulisan_slug());
        intent.putExtra("from", "artikel");
        startActivity(intent);
    }

    @Override
    public void onRefresh() {
//        et_search.addTextChangedListener(textWatcher);
        setNodata();
    }

    private void setNodata() {
        page = 1;
        previousTotal = 1;
        Cari(et_search.getText().toString());
    }
}
