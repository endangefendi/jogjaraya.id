package com.jogjaraya.id.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.jogjaraya.id.R;
import com.jogjaraya.id.activity.CariArtikelActivity;
import com.jogjaraya.id.activity.DetailArtikelActivity;
import com.jogjaraya.id.adapter.ArtikelAdapter;
import com.jogjaraya.id.model.ArtikelModel;
import com.jogjaraya.id.network.Constant;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link EnsikloFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class EnsikloFragment extends Fragment implements  SwipeRefreshLayout.OnRefreshListener, ArtikelAdapter.OnItemClickListener{
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;


    private static final String TAG = "EnsikloFragment";
    private SwipeRefreshLayout refreshLayout;
    private ArtikelAdapter adapter;
    private List<ArtikelModel> list= new ArrayList<>();
    private String url;
    private int page = 1;
    private RecyclerView recyclerView;
    private int previousTotal = 1;
    private boolean load = true;
    private int totalItemCount;
    private int visibleItemCount;
    private NestedScrollView scrollView;
    private ImageButton searching;

    public EnsikloFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment InfoFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static EnsikloFragment newInstance(String param1, String param2) {
        EnsikloFragment fragment = new EnsikloFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_ensiklo, container, false);

        refreshLayout = v.findViewById(R.id.refresh);
        refreshLayout.setOnRefreshListener(this);
        refreshLayout.post(new Runnable() {
            @Override
            public void run() {
                refreshLayout.setRefreshing(true);
            }
        });

        scrollView = v.findViewById(R.id.scroll_view);
        recyclerView = v.findViewById(R.id.list_item);
        searching = v.findViewById(R.id.action_search);

        return v;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        LinearLayoutManager layoutManagerKategori = new LinearLayoutManager(getContext());
        layoutManagerKategori.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutManagerKategori);
        recyclerView.setHasFixedSize(true);

        adapter = new ArtikelAdapter(getContext(), this);
        recyclerView.setAdapter(adapter);

        setNodata();
        searching.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), CariArtikelActivity.class);
                getContext().startActivity(intent);
            }
        });
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
        url = Constant.URL.LIST_ARTIKEL+page;
        final RequestQueue requestQueue = Volley.newRequestQueue(getContext());
        StringRequest request = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
//                Log.e(TAG, "onResponse" + response);
                try {
                    JSONObject object = new JSONObject(response);
                    int status = object.getInt("status");
                    String data = object.getString("data");
                    String message = object.getString("message");
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
                            tulisan_views, tulisan_rating, tulisan_kategori_slug, tulisan_kategori_nama,tulisan_author);
                            list.add(item);
                        }
                        adapter.addList(list);
                        adapter.notifyDataSetChanged();
                        refreshLayout.setRefreshing(false);
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
                    Toast.makeText(getContext(), "Opps,\nSomething wrong!", Toast.LENGTH_SHORT).show();
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
        url = Constant.URL.LIST_ARTIKEL+page;
        final RequestQueue requestQueue = Volley.newRequestQueue(getContext());
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
                                    tulisan_views, tulisan_rating, tulisan_kategori_slug, tulisan_kategori_nama,tulisan_author);
                            list.add(item);
                        }
                        adapter.addList(list);
                        adapter.notifyDataSetChanged();
                        refreshLayout.setRefreshing(false);
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
                    Toast.makeText(getContext(), "Opps,\nSomething wrong!", Toast.LENGTH_SHORT).show();
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
    }

    @Override
    public void onItemClicked(int position, ArtikelModel item) {
        Intent intent = new Intent(getContext(), DetailArtikelActivity.class);
        intent.putExtra("slug", item.getTulisan_slug());
        getContext().startActivity(intent);
    }
}
