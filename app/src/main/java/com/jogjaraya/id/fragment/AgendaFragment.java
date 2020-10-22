package com.jogjaraya.id.fragment;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
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
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.JsonRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.jogjaraya.id.R;
import com.jogjaraya.id.adapter.AgendaAdapter;
import com.jogjaraya.id.adapter.SugestAdapter;
import com.jogjaraya.id.adapter.WilayahAdapter;
import com.jogjaraya.id.model.AgendaModel;
import com.jogjaraya.id.model.Sugest;
import com.jogjaraya.id.model.WilayahModel;
import com.jogjaraya.id.network.Constant;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import static com.jogjaraya.id.network.Constant.URL.SUGEST_AGENDA;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link AgendaFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AgendaFragment extends Fragment implements SugestAdapter.OnItemClickListener, AgendaAdapter.OnItemClickListener{
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;


    private static final String TAG = "AgendaFragment";

    private Context context;

    //tgl
    private String awal="", akhir="";
    private EditText tgl_akhir, tgl_awal;
    private DatePickerDialog datePickerDialog;
    private SimpleDateFormat dateFormatter;


    //wilayah
    private String kode_wilayah = "semua";
    private List<WilayahModel> list_wilayah ;
    private WilayahAdapter adapter_wilayah;
    private ListView mListView;
    private ImageView iv_wilayah;
    private EditText et_nama_wilayah;


    //sugestion
    private List<Sugest> list_sugest ;
    private SugestAdapter adapter_sugest;
    private RecyclerView mListView_sugest;
    private EditText key;

    private TextView button_jelajah;

    //list agenda
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

    private SwipeRefreshLayout refreshLayout;

    private TextView refresh;
    public AgendaFragment() {
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
    public static AgendaFragment newInstance(String param1, String param2) {
        AgendaFragment fragment = new AgendaFragment();
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
        View v = inflater.inflate(R.layout.fragment_agenda, container, false);
        dateFormatter = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
        tgl_akhir = v.findViewById(R.id.tgl_akhir);
        tgl_awal = v.findViewById(R.id.tgl_awal);
        et_nama_wilayah = v.findViewById(R.id.et_nama_wilayah);
        iv_wilayah = v.findViewById(R.id.iv_wilayah);
        key = v.findViewById(R.id.key);

        refreshLayout = v.findViewById(R.id.refresh);
        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage("Preparing data..");

        lyt_no_item = v.findViewById(R.id.lyt_no_item);

        button_jelajah = v.findViewById(R.id.button_jelajah);

        recyclerView = v.findViewById(R.id.list_item);
        scrollView = v.findViewById(R.id.scroll_view);

        context=getActivity().getApplicationContext();
        mListView_sugest = v.findViewById(R.id.list_sugest);


        refresh = v.findViewById(R.id.text_refresh);

        return v;
    }

    private void setNodata() {
        // load data nya
        page = 1;
        previousTotal = 1;
        list.clear();
        loadDataCari(kode_wilayah, key.getText().toString(), awal, akhir);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        list.clear();
        loadDataCari(kode_wilayah, key.getText().toString(), awal, akhir);
        list_wilayah=new ArrayList<>();

        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                setNodata();
            }
        });

        refreshLayout.post(new Runnable() {
            @Override
            public void run() {
                refreshLayout.setRefreshing(true);
            }
        });
        adapter = new AgendaAdapter(getActivity(), this);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);
        recyclerView.setNestedScrollingEnabled(false);
        recyclerView.setAdapter(adapter);
        refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                page = 1;
                previousTotal = 1;
                list.clear();
                key.setText("");
                loadDataCari("", "", "", "");
            }
        });
        tgl_awal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDateDialog(tgl_awal, "awal");
            }
        });

        tgl_akhir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDateDialog(tgl_akhir, "akhir");
            }
        });

        iv_wilayah.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                list_wilayah.clear();
                popUpWilayah(et_nama_wilayah);
            }
        });

        et_nama_wilayah.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                list_wilayah.clear();popUpWilayah(et_nama_wilayah);
            }
        });

        key.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    page = 1;
                    previousTotal = 1;
                    list.clear();
                    loadDataCari(kode_wilayah,key.getText().toString(),awal,akhir);
                }
                return false;
            }
        });

        button_jelajah.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                akhir = newCalendar.get(Calendar.DAY_OF_MONTH)+"-"+newCalendar.get(Calendar.MONTH+1)+"-"+newCalendar.get(Calendar.YEAR)  ;
//                Log.e(TAG,awal +" awal\n"+akhir+" akhir\n"+kode_wilayah+" kode wilayah\n"+key.getText().toString().trim()+" key");
//                Intent intent = new Intent(getContext(), ListAgendaActivity.class);
//                intent.putExtra("kode_wilayah", kode_wilayah);
//                intent.putExtra("key", key.getText().toString().trim());
//                intent.putExtra("awal", awal);
//                intent.putExtra("akhir", akhir);
//                getContext().startActivity(intent);
                progressDialog.show();
                list.clear();
                loadDataCari(kode_wilayah,key.getText().toString(),awal,akhir);
            }
        });

        list_sugest=new ArrayList<>();
        mListView_sugest.setVisibility(View.GONE);
        adapter_sugest = new SugestAdapter(getActivity(), this);
        LinearLayoutManager layoutManagerKategori = new LinearLayoutManager(context);
        layoutManagerKategori.setOrientation(LinearLayoutManager.VERTICAL);
        mListView_sugest.setLayoutManager(layoutManagerKategori);
        mListView_sugest.setHasFixedSize(true);
        mListView_sugest.setNestedScrollingEnabled(false);
        mListView_sugest.setAdapter(adapter_sugest);
        key.addTextChangedListener(textWatcher);
    }

    private void CariSugest(String cari) {
        try {
            list_sugest.clear();
            RequestQueue requestQueue = Volley.newRequestQueue(context);
            JSONObject json = new JSONObject();
            json.put("keyword", cari);
            JsonRequest request = new JsonObjectRequest(Request.Method.POST, SUGEST_AGENDA, json, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    Log.e(TAG, "onResponse SUGEST_JELAJAH" + response);
                    try {
                        int status = response.getInt("status");
                        String data = response.getString("data");
                        String message = response.getString("message");
                        if (status == 200 && !message.equalsIgnoreCase("")) {
                            JSONArray array = new JSONArray(data);
                            if (array.length()==1){
                                mListView_sugest.setVisibility(View.GONE);
                            }
                            for (int i = 0; i < array.length(); i++) {
                                JSONObject obj = array.getJSONObject(i);
                                String suggestion = obj.getString("suggestion");
                                String value = obj.getString("value");
                                Sugest item = new Sugest(suggestion, value);
                                list_sugest.add(item);
                            }
                            adapter_sugest.addList(list_sugest);
                            adapter_sugest.notifyDataSetChanged();
                            mListView_sugest.setVisibility(View.VISIBLE);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.d(TAG, "onErrorResponse: " + error);
                    Constant.parseError(context, error);
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


    private TextWatcher textWatcher=new TextWatcher() {

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            // TODO Auto-generated method stub
            list_sugest.clear();
            mListView_sugest.setVisibility(View.GONE);
            CariSugest(s.toString());
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count,
                                      int after) {
            // TODO Auto-generated method stub

        }

        @Override
        public void afterTextChanged(Editable s) {
            // TODO Auto-generated method stub
        }
    };

    private void loadDataCari(String kode_wilayah, String cari, String awal, String akhir) {
        try {
            list.clear();
            url = Constant.URL.CARI_AGENDA + page;
            RequestQueue requestQueue = Volley.newRequestQueue(getContext());
            JSONObject json = new JSONObject();
            json.put("wilayah_kode", kode_wilayah);
            json.put("cari", cari);
            json.put("tanggal_mulai", awal);
            json.put("tanggal_selesai", akhir);
            Log.e(TAG, "json CARI_AGENDA" + json.toString());

            JsonRequest request = new JsonObjectRequest(Request.Method.POST, url, json, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    progressDialog.dismiss();
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
                    Constant.parseError(context, error);
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
                            getNext(page, c, kode_wilayah, awal, akhir);
                        }
                    }
                }
            });
        }

    }

    private void getNext(int page, final String cari, String kode_wilayah, String awal,String akhir) {
        try {
            url = Constant.URL.CARI_AGENDA + page;
            RequestQueue requestQueue = Volley.newRequestQueue(getContext());
            JSONObject json = new JSONObject();
            json.put("wilayah_kode", kode_wilayah);
            json.put("cari", cari);
            json.put("tanggal_mulai", awal);
            json.put("tanggal_selesai", akhir);
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
                    Constant.parseError(context, error);
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

    private void popUpWilayah(final EditText et_nama_wilayah) {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getLayoutInflater();
        @SuppressLint("InflateParams")
        final View dialogView = inflater.inflate(R.layout.dialog_wilayah, null);
        dialogBuilder.setView(dialogView);

        final AlertDialog b = dialogBuilder.create();
        b.setCanceledOnTouchOutside(true);
        b.setCancelable(false);
        b.show();

        mListView = dialogView.findViewById(R.id.listItem);
        list_wilayah=new ArrayList<>();
        list_wilayah.clear();
        adapter_wilayah = new WilayahAdapter(getActivity(), list_wilayah);

        mListView.setClickable(true);

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Object o = mListView.getItemAtPosition(i);
                WilayahModel cn = (WilayahModel) o;
                et_nama_wilayah.setError(null);
                et_nama_wilayah.setText(cn.getWilayah_nama());
                kode_wilayah = cn.getWilayah_kode();
                b.dismiss();
            }
        });


        progressDialog.show();

        load_wilayah();
    }

    private ProgressDialog progressDialog;

    private void load_wilayah() {
        list_wilayah.clear();
        final RequestQueue requestQueue = Volley.newRequestQueue(context);
        StringRequest request = new StringRequest(Request.Method.GET, Constant.URL.LIST_WILAYAH, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.e(TAG, "onResponse LIST_WILAYAH" + response);
                try {
                    JSONObject object = new JSONObject(response);
                    int status = object.getInt("status");
                    String message = object.getString("message");
                    if (status == 200 && !message.equalsIgnoreCase("") ) {
                        progressDialog.dismiss();
                        String data = object.getString("data");
                        JSONArray array = new JSONArray(data);
                        for (int i = 0; i < array.length(); i++) {
                            JSONObject obj = array.getJSONObject(i);
                            String wilayah_kode = obj.getString("wilayah_kode");
                            String wilayah_nama = obj.getString("wilayah_nama");
                            WilayahModel item = new WilayahModel(wilayah_kode, wilayah_nama);
                            list_wilayah.add(item);
                            mListView.setAdapter(adapter_wilayah);
                        }
                        adapter_wilayah.setList(list_wilayah);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "onErrorResponse: " + error);
                Constant.parseError(context, error);
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

    private Calendar newCalendar = Calendar.getInstance();

    private void showDateDialog(final EditText editText, final String stringawal){

        datePickerDialog = new DatePickerDialog(getContext(), new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {

                Calendar newDate = Calendar.getInstance();
                newDate.set(year, monthOfYear, dayOfMonth);
                editText.setText(dateFormatter.format(newDate.getTime()));
                editText.setError(null);

                if (stringawal.equalsIgnoreCase("awal")){
                    awal = editText.getText().toString();
                }else {
                    akhir = editText.getText().toString();
                }
            }

        },newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.DAY_OF_MONTH));

        datePickerDialog.show();
    }


    @Override
    public void onItemClicked(int position, AgendaModel item) {
        loadDetailAgenda(item.getEvent_id());
    }
    
    private void loadDetailAgenda(String event_id) {
        final RequestQueue requestQueue = Volley.newRequestQueue(getContext());
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
                        String event_wilayah_nama = data.getString("event_wilayah_nama");
                        viewDetailAgenda(event_judul, event_tanggal_mulai, event_tanggal_selesai, event_jam, event_lokasi, event_deskripsi, event_penyelenggara, event_kontak, event_tiket, event_wilayah_kode, event_gambar,event_wilayah_nama);

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "onErrorResponse: " + error);
                Constant.parseError(context, error);
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


    private void viewDetailAgenda(String event_judul, String event_tanggal_mulai, String event_tanggal_selesai, String event_jam, String event_lokasi, String event_deskripsi, String event_penyelenggara, String event_kontak, String event_tiket, String event_wilayah_kode, String event_gambar,String event_wilayah_nama) {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getContext());
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

        TextView daerah = dialogView.findViewById(R.id.daerah);
        daerah.setText(event_wilayah_nama);

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
        ImageView img_daerah = dialogView.findViewById(R.id.daerah_img);

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

        if (event_wilayah_nama.equalsIgnoreCase("") || event_wilayah_nama.equalsIgnoreCase(" ")){
            daerah.setVisibility(View.GONE);
            img_daerah.setVisibility(View.GONE);
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

    @Override
    public void onItemClicked(int position, Sugest item) {
        list_sugest.clear();
        mListView_sugest.setVisibility(View.GONE);
        key.setError(null);
        key.setText(item.getValue());
        key.setSelection(key.getText().length());
        mListView_sugest.setVisibility(View.GONE);
        setNodata();
    }

    @Override
    public void onResume() {
        super.onResume();
        mListView_sugest.setVisibility(View.GONE);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mListView_sugest.setVisibility(View.GONE);
    }
}
