package com.jogjaraya.id.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
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

public class ArtikelByKategoriActivity extends AppCompatActivity implements ArtikelAdapter.OnItemClickListener, SwipeRefreshLayout.OnRefreshListener {
    private static final String TAG = "ArtikelByslug";
    private SwipeRefreshLayout refreshLayout;
    private ArtikelAdapter adapter;
    private List<ArtikelModel> list= new ArrayList<>();
    private String url;
    private int page = 1;
    private RecyclerView recyclerView;
    private int previousTotal = 1;
    public boolean load = true;
    private int totalItemCount;
    private int visibleItemCount;
    private NestedScrollView scrollView;
    private View lyt_no_item;

    private String slug="";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_artikel_by_kategori);

        slug = getIntent().getStringExtra("slug");
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
        title.setText(getResources().getString(R.string.title_ensiklopedia)+ " - "+slug.toUpperCase());
    }

    private void inital() {
        lyt_no_item = findViewById(R.id.lyt_no_item);
        TextView text_refresh = findViewById(R.id.text_refresh);
        text_refresh.setText("Wangsuli dateng ensiklopedia");
        text_refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
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
        recyclerView.setHasFixedSize(true);

        adapter = new ArtikelAdapter(this, this);
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
        getdatalist();
    }

    private void getdatalist() {
        list.clear();
        url = Constant.URL.ARTIKEL_BY_KATEGORI+slug+"/"+page;
        final RequestQueue requestQueue = Volley.newRequestQueue(this);
        StringRequest request = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
//                Log.e(TAG, "onResponse" + response);
                try {
                    JSONObject object = new JSONObject(response);
                    int status = object.getInt("status");
                    String data = object.getString("data");
                    String message = object.getString("message");
                    if (status == 200 && !message.equalsIgnoreCase("")) {
                        JSONArray array = new JSONArray(data);
                        if (array.length() == 0) {
                            lyt_no_item.setVisibility(View.VISIBLE);
                            refreshLayout.setRefreshing(false);
                            recyclerView.setVisibility(View.GONE);
                        } else {
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
                Constant.parseError(ArtikelByKategoriActivity.this, error);
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                return Constant.getHeaders();
            }
        };
        request.setRetryPolicy(Constant.getDefaultRetryPolicy());
        requestQueue.add(request);
        pagination();
    }

    private void pagination() {
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
                            getNext(page);
                        }
                    }
                }
            });
        }

    }

    private void getNext(int page) {
        url = Constant.URL.ARTIKEL_BY_KATEGORI+slug+"/"+page;
        final RequestQueue requestQueue = Volley.newRequestQueue(this);
        StringRequest request = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
//                Log.e(TAG, "onResponse" + response);
                try {
                    JSONObject object = new JSONObject(response);
                    int status = object.getInt("status");
                    String data = object.getString("data");
                    String message = object.getString("message");
                    if (status == 200 && !message.equalsIgnoreCase("")) {
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
                Constant.parseError(ArtikelByKategoriActivity.this, error);
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

    @Override
    public void onItemClicked(int position, ArtikelModel item) {
    }
}
