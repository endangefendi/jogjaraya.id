package com.jogjaraya.id.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
import com.facebook.shimmer.ShimmerFrameLayout;
import com.jogjaraya.id.R;
import com.jogjaraya.id.activity.ArtikelByKategoriActivity;
import com.jogjaraya.id.activity.DetailArtikelActivity;
import com.jogjaraya.id.activity.FullFotoActivity;
import com.jogjaraya.id.activity.MainActivity;
import com.jogjaraya.id.adapter.HomeArtikelAdapter;
import com.jogjaraya.id.adapter.MenuBannerAdapter;
import com.jogjaraya.id.adapter.MenuIconAdapter;
import com.jogjaraya.id.model.HomeArtikelModel;
import com.jogjaraya.id.model.MenuBannerModel;
import com.jogjaraya.id.model.MenuIconModel;
import com.jogjaraya.id.network.Constant;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.jogjaraya.id.view.GridSpanCount.getGridSpanCount;
import static com.jogjaraya.id.view.GridSpanCount.getGridSpanCountIcon;

public class HomeFragment extends Fragment implements  SwipeRefreshLayout.OnRefreshListener, MenuBannerAdapter.OnItemClickListener, HomeArtikelAdapter.OnItemClickListener , MenuIconAdapter.OnItemClickListener {
    private static final String TAG = "HomeFragment";

    private SwipeRefreshLayout refreshLayout;

    private Context context;
    //menu banner
    private MenuBannerAdapter menu_banner_adapter;
    private List<MenuBannerModel> menu_banner_list;
    private RecyclerView recyclerView_banner;

    //menu icon
    private MenuIconAdapter menu_icon_adapter;
    private List<MenuIconModel> menu_icon_list;
    private RecyclerView recyclerView_icon;

    // backgorund atas
    private TextView tv_title_atas;
    private TextView tv_deskripsi_atas;
    private ImageView imageViewBackground;

    // Home Artikel
    private HomeArtikelAdapter home_artikel_adapter;
    private List<HomeArtikelModel> home_artikel_list;
    private RecyclerView recyclerView_home_artikel;


    //poster
    private ImageView imageViewPoster;

    //frame
    private LinearLayout framaartikel;
    private RelativeLayout frameBackground;
    private ShimmerFrameLayout shimmerbackground;

    public HomeFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        menu_banner_adapter = new MenuBannerAdapter(getActivity(), this);
        recyclerView_banner.setLayoutManager(new GridLayoutManager(getContext(), getGridSpanCount(getActivity())));
        recyclerView_banner.setHasFixedSize(true);
        recyclerView_banner.setNestedScrollingEnabled(false);
        recyclerView_banner.setAdapter(menu_banner_adapter);


        menu_icon_adapter = new MenuIconAdapter(getActivity(), this);
        recyclerView_icon.setLayoutManager(new GridLayoutManager(getContext(), getGridSpanCountIcon(getActivity())));
        recyclerView_icon.setHasFixedSize(true);
        recyclerView_icon.setNestedScrollingEnabled(false);
        recyclerView_icon.setAdapter(menu_icon_adapter);


        home_artikel_adapter = new HomeArtikelAdapter(getActivity(), this);
        LinearLayoutManager layoutManagerKategori = new LinearLayoutManager(getContext());
        layoutManagerKategori.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView_home_artikel.setLayoutManager(layoutManagerKategori);
        recyclerView_home_artikel.setHasFixedSize(true);
        recyclerView_home_artikel.setNestedScrollingEnabled(false);
        recyclerView_home_artikel.setAdapter(home_artikel_adapter);

        TextView button_jelajah = view.findViewById(R.id.button_jelajah);
        button_jelajah.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activity.bottomNavigationView.setSelectedItemId(R.id.navigation_jelajah);
            }
        });
        load_menu_banner();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_home, container, false);

        shimmerbackground = v.findViewById(R.id.shimmerbackground);
        shimmerbackground.setVisibility(View.VISIBLE);
        shimmerbackground.startShimmerAnimation();
        context=getActivity().getApplicationContext();
        frameBackground = v.findViewById(R.id.frameBackground);
        frameBackground.setVisibility(View.GONE);

        framaartikel = v.findViewById(R.id.frameArtikelTerbaru);
        framaartikel.setVisibility(View.GONE);
        recyclerView_home_artikel = v.findViewById(R.id.list_artikel);


        recyclerView_banner = v.findViewById(R.id.list_menu_banner);

        recyclerView_icon = v.findViewById(R.id.list_menu_icon);

        imageViewPoster = v.findViewById(R.id.poster);

        imageViewBackground = v.findViewById(R.id.bg_top);
        tv_title_atas = v.findViewById(R.id.title_atas);
        tv_deskripsi_atas = v.findViewById(R.id.deskripsi_atas);

        refreshLayout = v.findViewById(R.id.refresh);
        refreshLayout.setOnRefreshListener(this);
        refreshLayout.post(new Runnable() {
            @Override
            public void run() {
                refreshLayout.setRefreshing(true);
            }
        });

        return v;
    }

    private void load_menu_banner() {
        refreshLayout.setRefreshing(true);
        menu_banner_list=new ArrayList<>();
        menu_banner_list.clear();
        final RequestQueue requestQueue = Volley.newRequestQueue(context);
        StringRequest request = new StringRequest(Request.Method.GET, Constant.URL.GET_MENU_BANNER, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.e(TAG, "onResponse" + response);
                try {
                    JSONObject object = new JSONObject(response);
                    int status = object.getInt("status");
                    String message = object.getString("message");
                    if (status == 200 && !message.equalsIgnoreCase("") ) {
                        shimmerbackground.stopShimmerAnimation();
                        shimmerbackground.setVisibility(View.GONE);
                        frameBackground.setVisibility(View.VISIBLE);
                        String fiel_poster = object.getString("poster");
                        JSONObject poster = new JSONObject(fiel_poster);
                        String foto_poster = poster.getString("foto_poster");
                        String slug_poster = poster.getString("slug_poster");
                        setViewPoster(foto_poster,slug_poster);

                        String fiel_datas = object.getString("atas");
                        JSONObject atas = new JSONObject(fiel_datas);
                        String background = atas.getString("background");
                        String title_atas = atas.getString("title_atas");
                        String deskripsi_atas = atas.getString("deskripsi_atas");
                        setViewBacground(background,title_atas, deskripsi_atas);

                        String menu_banner = object.getString("menu_banner");
                        JSONArray array = new JSONArray(menu_banner);
                        for (int i = 0; i < array.length(); i++) {
                            JSONObject obj = array.getJSONObject(i);
                            String banner = obj.getString("banner");
                            String url = obj.getString("url");
                            String tipe = obj.getString("tipe");

                            MenuBannerModel item = new MenuBannerModel(banner,url,tipe);
                            menu_banner_list.add(item);
                        }
                        menu_banner_adapter.addList(menu_banner_list);
                        menu_banner_adapter.notifyDataSetChanged();
                        refreshLayout.setRefreshing(false);
                        load_menu_icon();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "onErrorResponse: " + error);
                Constant.parseError(activity, error);
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

    private void load_menu_icon() {
        refreshLayout.setRefreshing(true);
        menu_icon_list=new ArrayList<>();
        menu_icon_list.clear();
        final RequestQueue requestQueue = Volley.newRequestQueue(context);
        StringRequest request = new StringRequest(Request.Method.GET, Constant.URL.GET_MENU_ICON, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.e(TAG, "onResponse" + response);
                try {
                    JSONObject object = new JSONObject(response);
                    int status = object.getInt("status");
                    String message = object.getString("message");
                    if (status == 200 && !message.equalsIgnoreCase("") ) {
                        String menu_icon = object.getString("menu_icon");
                        JSONArray array = new JSONArray(menu_icon);
                        for (int i = 0; i < array.length(); i++) {
                            JSONObject obj = array.getJSONObject(i);
                            String judul = obj.getString("judul");
                            String icon = obj.getString("icon");
                            String slug = obj.getString("slug");

                            MenuIconModel item = new MenuIconModel(judul,icon,slug);
                            menu_icon_list.add(item);
                        }
                        menu_icon_adapter.addList(menu_icon_list);
                        menu_icon_adapter.notifyDataSetChanged();
                        refreshLayout.setRefreshing(false);
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
                Constant.parseError(activity, error);
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
        final RequestQueue requestQueue = Volley.newRequestQueue(context);
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
                        if (array.length()<=0) {
                            framaartikel.setVisibility(View.GONE);
                        }else{
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
                            framaartikel.setVisibility(View.VISIBLE);
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
                Constant.parseError(activity, error);
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

    private void setViewPoster(final String foto_poster, final String slug_poster) {
        Glide.with(context).load(foto_poster)
                .placeholder(R.drawable.loading_placeholder)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .into(imageViewPoster);
        imageViewPoster.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (slug_poster.equalsIgnoreCase("")) {
                    Intent intent = new Intent(activity, FullFotoActivity.class);
                    intent.putExtra("foto", foto_poster);
                    intent.putExtra("title", "");
                    activity.startActivity(intent);
                }else {
                    Intent intent = new Intent(getContext(), DetailArtikelActivity.class);
                    intent.putExtra("slug", slug_poster);
                    intent.putExtra("from", "artikel");
                    activity.startActivity(intent);
                }
            }
        });
    }

    private void setViewBacground(final String background, String title_atas, String deskripsi_atas) {
        tv_title_atas.setText(title_atas.toUpperCase());
        tv_deskripsi_atas.setText(deskripsi_atas.toUpperCase());
        Glide.with(context).load(background)
                .placeholder(R.drawable.loading_placeholder)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .into(imageViewBackground);

        imageViewBackground.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(activity, FullFotoActivity.class);
                intent.putExtra("foto", background);
                intent.putExtra("title", "");
                activity.startActivity(intent);
            }
        });

    }

    @Override
    public void onItemClicked(int position, MenuBannerModel item) {
        String tipe = item.getTipe();
        if (tipe.equalsIgnoreCase("whatsapp")){
            Intent intentWhatsapp = new Intent(Intent.ACTION_VIEW);
            String url = item.getUrl();
            intentWhatsapp.setData(Uri.parse(url));
            intentWhatsapp.setPackage("com.whatsapp");
            activity.startActivity(intentWhatsapp);
        }else if (tipe.equalsIgnoreCase("jelajah")){
            activity.bottomNavigationView.setSelectedItemId(R.id.navigation_jelajah);
        }else if (tipe.equalsIgnoreCase("ensiklo")){
            activity.bottomNavigationView.setSelectedItemId(R.id.navigation_ensiklo);
        }else if (tipe.equalsIgnoreCase("agenda")){
            activity.bottomNavigationView.setSelectedItemId(R.id.navigation_agenda);
        }

    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onItemClicked(int position, MenuIconModel item) {
        Intent intent = new Intent(activity, ArtikelByKategoriActivity.class);
        intent.putExtra("slug", item.getSlug());
        activity.startActivity(intent);
    }

    public MainActivity activity;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.activity = (MainActivity) activity;
    }
    @Override
    public void onItemClicked(int position, HomeArtikelModel item) {
        Intent intent = new Intent(activity, DetailArtikelActivity.class);
        intent.putExtra("slug", item.getTulisan_slug());
        intent.putExtra("from", "artikel");
        activity.startActivity(intent);
    }

    @Override
    public void onRefresh() {
        load_menu_banner();
    }
}
