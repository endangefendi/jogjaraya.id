package com.jogjaraya.id.activity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.widget.NestedScrollView;
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
import com.android.volley.toolbox.Volley;
import com.jogjaraya.id.R;
import com.jogjaraya.id.adapter.JelajahArtikelAdapter;
import com.jogjaraya.id.model.JelajahArtikelModel;
import com.jogjaraya.id.network.Constant;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ListJelajahActivity extends AppCompatActivity implements JelajahArtikelAdapter.OnItemClickListener, SwipeRefreshLayout.OnRefreshListener {
    private static final String TAG = "CariJelajahAct";
    private SwipeRefreshLayout refreshLayout;
    private JelajahArtikelAdapter adapter;
    private List<JelajahArtikelModel> list= new ArrayList<>();
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
    String key_value;

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
        key_value = getIntent().getStringExtra("key_value");
        if (!key.equalsIgnoreCase("")) {
            title.setText(key.toUpperCase());
        }else{
            title.setText(getResources().getString(R.string.title_jelajah));
        }
    }

    private void inital() {
        lyt_no_item = findViewById(R.id.lyt_no_item);
        TextView text_refresh = findViewById(R.id.text_refresh);
        text_refresh.setText("Wangsuli dateng jelajah");
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

        adapter = new JelajahArtikelAdapter(this, this);
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
        key_value = getIntent().getStringExtra("key_value");
        loadDataCari(kode_wilayah,key_value);
    }
    private void loadDataCari(String kode_wilayah, String cari) {
        try {
            list.clear();
            url = Constant.URL.JELAJAH_CARI + page;
            RequestQueue requestQueue = Volley.newRequestQueue(this);
            JSONObject json = new JSONObject();
            json.put("wilayah_kode", kode_wilayah);
            json.put("cari", cari);
            JsonRequest request = new JsonObjectRequest(Request.Method.POST, url, json, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    refreshLayout.setRefreshing(false);
                    Log.e(TAG, "onResponse JELAJAH_CARI" + response);
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
                                    String listing_id = obj.getString("listing_id");
                                    String listing_nama = obj.getString("listing_nama");
                                    String listing_alamat = obj.getString("listing_alamat");
                                    String listing_kategori = obj.getString("listing_kategori");
                                    String listing_deskripsi = obj.getString("listing_deskripsi");
                                    String listing_wilayah_kode = obj.getString("listing_wilayah_kode");
                                    String listing_wilayah_nama = obj.getString("listing_wilayah_nama");
                                    String listing_gambar = obj.getString("listing_gambar");
                                    JelajahArtikelModel item = new JelajahArtikelModel(listing_id, listing_nama, listing_alamat,
                                            listing_kategori, listing_deskripsi, listing_wilayah_kode,
                                            listing_wilayah_nama, listing_gambar);
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

                    Constant.parseError(ListJelajahActivity.this, error);
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
                            getNext(page,c);
                        }
                    }
                }
            });
        }

    }

    private void getNext(int page, final String c) {
        try {
            url = Constant.URL.JELAJAH_CARI+page;
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
                                String listing_id = obj.getString("listing_id");
                                String listing_nama = obj.getString("listing_nama");
                                String listing_alamat = obj.getString("listing_alamat");
                                String listing_kategori = obj.getString("listing_kategori");
                                String listing_deskripsi = obj.getString("listing_deskripsi");
                                String listing_wilayah_kode = obj.getString("listing_wilayah_kode");
                                String listing_wilayah_nama = obj.getString("listing_wilayah_nama");
                                String listing_gambar = obj.getString("listing_gambar");
                                JelajahArtikelModel item = new JelajahArtikelModel(listing_id, listing_nama, listing_alamat,
                                        listing_kategori, listing_deskripsi, listing_wilayah_kode,
                                        listing_wilayah_nama, listing_gambar);
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
                    if (error.networkResponse!=null) {
                        Toast.makeText(ListJelajahActivity.this, "Opps,\nSomething wrong!", Toast.LENGTH_SHORT).show();
                    }
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
    public void onItemClicked(int position, JelajahArtikelModel item) {
    }
}
