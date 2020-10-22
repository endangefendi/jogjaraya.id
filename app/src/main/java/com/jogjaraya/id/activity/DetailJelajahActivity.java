package com.jogjaraya.id.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.viewpager.widget.ViewPager;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.jogjaraya.id.R;
import com.jogjaraya.id.adapter.GambarAdapter;
import com.jogjaraya.id.adapter.HomeArtikelAdapter;
import com.jogjaraya.id.model.GambarModel;
import com.jogjaraya.id.model.HomeArtikelModel;
import com.jogjaraya.id.network.Constant;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.jogjaraya.id.network.Constant.URL.close_html;
import static com.jogjaraya.id.network.Constant.URL.open_html;

public class DetailJelajahActivity extends AppCompatActivity implements HomeArtikelAdapter.OnItemClickListener, SwipeRefreshLayout.OnRefreshListener {
    private final static String TAG= "DetailJelajahAct";

    private TextView judul,email, alamat, telp, web, daerah;
    private ImageView iv_email, iv_alamat, iv_telp, iv_web, iv_daerah;
    private WebView isi,frameMap;
    private SwipeRefreshLayout refreshLayout;

    // Home Artikel
    private HomeArtikelAdapter home_artikel_adapter;
    private List<HomeArtikelModel> home_artikel_list;
    private RecyclerView recyclerView_home_artikel;


    //Slider
    private List<GambarModel> list_banner;
    private ViewPager viewPager;
    private Handler handler = new Handler();
    private Runnable runnableCode = null;
    private GambarAdapter adapter_banner;
    private View lyt_main_content;
    private LinearLayout layout_dots;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_jelajah);
        initalToolbar();
        inital();
        initialIconGambar();
        init_artikel();

        initialSlider();
        String listing_id = getIntent().getStringExtra("listing_id");
        load_detail_artikel(listing_id);
    }

    private void initialSlider() {
        viewPager = findViewById(R.id.pager);
        lyt_main_content = findViewById(R.id.lyt_cart);
        layout_dots = findViewById(R.id.layout_dots);

        adapter_banner = new GambarAdapter(this, new ArrayList<GambarModel>());
        lyt_main_content.setVisibility(View.GONE);
    }

    private void initialIconGambar() {
        iv_alamat = findViewById(R.id.iv_alamat);
        iv_email = findViewById(R.id.iv_email);
        iv_web = findViewById(R.id.iv_web);
        iv_telp = findViewById(R.id.iv_telp);
        iv_daerah = findViewById(R.id.iv_daerah);
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
        title.setText(getResources().getString(R.string.title_detail_jelajah));

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
        judul = findViewById(R.id.judul);
        alamat = findViewById(R.id.alamat);
        web = findViewById(R.id.tv_web);
        isi = findViewById(R.id.isi);
        frameMap = findViewById(R.id.frameMap);
        telp = findViewById(R.id.telp);
        email = findViewById(R.id.email);
        daerah = findViewById(R.id.daerah);

        refreshLayout = findViewById(R.id.refresh);
        refreshLayout.setOnRefreshListener(this);
        refreshLayout.post(new Runnable() {
            @Override
            public void run() {
                refreshLayout.setRefreshing(true);
            }
        });
    }

    private static int getFeaturedNewsImageHeight(Activity activity) {
        float w_ratio = 2, h_ratio = 1; // we use 2:1 ratio
        Display display = activity.getWindowManager().getDefaultDisplay();
        DisplayMetrics displayMetrics = new DisplayMetrics();
        display.getMetrics(displayMetrics);
        float screenWidth = displayMetrics.widthPixels - 10;
        float resHeight = (screenWidth * h_ratio) / w_ratio;
        return Math.round(resHeight);
    }

    private void addBottomDots(LinearLayout layout_dots, int size, int current) {
        ImageView[] dots = new ImageView[size];

        layout_dots.removeAllViews();
        for (int i = 0; i < dots.length; i++) {
            dots[i] = new ImageView(this);
            int width_height = 10;
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(new ViewGroup.LayoutParams(width_height, width_height));
            params.setMargins(10, 10, 10, 10);
            dots[i].setLayoutParams(params);
            dots[i].setImageResource(R.drawable.bg_button);
            dots[i].setColorFilter(ContextCompat.getColor(this, R.color.bg_abu));
            layout_dots.addView(dots[i]);
        }

        if (dots.length > 0) {
            dots[current].setColorFilter(ContextCompat.getColor(this, R.color.colorPrimary));
        }
    }

    private void startAutoSlider(final int count) {
        runnableCode = new Runnable() {
            @Override
            public void run() {
                int pos = viewPager.getCurrentItem();
                pos = pos + 1;
                if (pos >= count) pos = 0;
                viewPager.setCurrentItem(pos);
                handler.postDelayed(runnableCode, 10000);
            }
        };
        handler.postDelayed(runnableCode, 5000);
    }

    private void displayResultData(List<GambarModel> items) {
        adapter_banner.setItems(items);
        viewPager.setAdapter(adapter_banner);

        ViewGroup.LayoutParams params = viewPager.getLayoutParams();
        params.height = getFeaturedNewsImageHeight(this);
        viewPager.setLayoutParams(params);

        // displaying selected image first
        viewPager.setCurrentItem(0);
        addBottomDots(layout_dots, adapter_banner.getCount(), 0);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int pos, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int pos) {
                GambarModel cur = adapter_banner.getItem(pos);
                addBottomDots(layout_dots, adapter_banner.getCount(), pos);
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
        final ArrayList<String> images_list = new ArrayList<>();
        for (GambarModel img : list_banner) {
            images_list.add(img.getGambar());
        }
        startAutoSlider(adapter_banner.getCount());
        adapter_banner.setOnItemClickListener(new GambarAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, GambarModel obj, int pos) {
                Intent i = new Intent(DetailJelajahActivity.this, FullGambarSlideActivity.class);
                i.putExtra(FullGambarSlideActivity.EXTRA_POS, pos);
                    i.putStringArrayListExtra(FullGambarSlideActivity.EXTRA_IMGS, images_list);
                startActivity(i);
            }

        });
        lyt_main_content.setVisibility(View.VISIBLE);
    }

    private void load_detail_artikel(String listing_id) {
        list_banner = new ArrayList<>();
        list_banner.clear();
        final RequestQueue requestQueue = Volley.newRequestQueue(this);
        StringRequest request = new StringRequest(Request.Method.GET, Constant.URL.DETAIL_JELAJAH+listing_id, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.e(TAG, "onResponse DETAIL_JELAJAH" + response);
                try {
                    JSONObject object = new JSONObject(response);
                    int status = object.getInt("status");
                    String message = object.getString("message");
                    if (status == 200 && !message.equalsIgnoreCase("") ) {
                        String field_data = object.getString("data");
                        JSONObject data = new JSONObject(field_data);
                        String listing_id = data.getString("listing_id");
                        String listing_nama = data.getString("listing_nama");
                        String listing_alamat = data.getString("listing_alamat");
                        String listing_telp = data.getString("listing_telp");
                        String listing_website = data.getString("listing_website");
                        String listing_email = data.getString("listing_email");
                        String listing_kategori = data.getString("listing_kategori");
                        String listing_wilayah_nama = data.getString("listing_wilayah_nama");
                        String listing_deskripsi = data.getString("listing_deskripsi");
                        String frame_map_alamat = data.getString("frame_map_alamat");
                        setViewDetail(listing_id, listing_nama, listing_alamat, listing_telp, listing_website, listing_email,
                                listing_kategori, listing_deskripsi,frame_map_alamat,listing_wilayah_nama);

                        String field_gambar = data.getString("listing_gambar");
                        JSONArray array = new JSONArray(field_gambar);
                        for (int i = 0; i < array.length(); i++) {
                            JSONObject obj = array.getJSONObject(i);
                            int no = obj.getInt("no");
                            String gambar = obj.getString("gambar");
                            GambarModel item = new GambarModel(no, gambar);
                            list_banner.add(item);
                        }
                        displayResultData(list_banner);


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
                Constant.parseError(DetailJelajahActivity.this, error);
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

    private void setViewDetail(String listing_id, String listing_nama, String listing_alamat,String listing_telp,
                               String listing_website, String listing_email, String listing_kategori,
                               String listing_deskripsi, String frame_map_alamat, String listing_wilayah_nama){
        refreshLayout.setRefreshing(false);

        frameMap.setWebChromeClient(new WebChromeClient());
        frameMap.getSettings().setJavaScriptEnabled(true);
        frameMap.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
        frameMap.setWebViewClient(new WebViewClient(){
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }
            @Override
            public void onPageFinished(WebView view, final String url) {
            }
        });
        frameMap.loadData(frame_map_alamat,"text/html", "utf-8");

        judul.setText(listing_nama);
        alamat.setText(listing_alamat);
        web.setText(listing_website);
        email.setText(listing_email);
        daerah.setText(listing_wilayah_nama);
        telp.setText(listing_telp);

        isi.loadDataWithBaseURL(null, open_html+listing_deskripsi+close_html, "text/html", "UTF-8","about:blank");

        if (listing_wilayah_nama.equalsIgnoreCase("")){
            daerah.setVisibility(View.GONE);
            daerah.setVisibility(View.GONE);
        }
        if (listing_email.equalsIgnoreCase("")){
            iv_email.setVisibility(View.GONE);
            email.setVisibility(View.GONE);
        }
        if (listing_alamat.equalsIgnoreCase("")){
            iv_alamat.setVisibility(View.GONE);
            alamat.setVisibility(View.GONE);
        }
        if (listing_telp.equalsIgnoreCase("")){
            iv_telp.setVisibility(View.GONE);
            telp.setVisibility(View.GONE);
        }

        if (listing_website.equalsIgnoreCase("")){
            iv_web.setVisibility(View.GONE);
            web.setVisibility(View.GONE);
        }
        if (frame_map_alamat.equalsIgnoreCase("")){
            frameMap.setVisibility(View.GONE);
        }
        if (listing_deskripsi.equalsIgnoreCase("")){
            isi.setVisibility(View.GONE);
        }

    }

    @Override
    public void onRefresh() {
        String listing_id = getIntent().getStringExtra("listing_id");
        load_detail_artikel(listing_id);
    }

    @Override
    public void onItemClicked(int position, HomeArtikelModel item) {
        Intent intent = new Intent(this, DetailArtikelActivity.class);
        intent.putExtra("from", "artikel");
        intent.putExtra("slug", item.getTulisan_slug());
        startActivity(intent);
    }
}
