package com.jogjaraya.id.fragment;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
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
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.JsonRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.jogjaraya.id.R;
import com.jogjaraya.id.activity.ListJelajahActivity;
import com.jogjaraya.id.adapter.IklanAdapter;
import com.jogjaraya.id.adapter.JelajahArtikelAdapter;
import com.jogjaraya.id.adapter.SugestAdapter;
import com.jogjaraya.id.adapter.WilayahAdapter;
import com.jogjaraya.id.model.IklanModel;
import com.jogjaraya.id.model.JelajahArtikelModel;
import com.jogjaraya.id.model.Sugest;
import com.jogjaraya.id.model.WilayahModel;
import com.jogjaraya.id.network.Constant;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import static com.jogjaraya.id.network.Constant.URL.SUGEST_JELAJAH;
import static com.jogjaraya.id.view.GridSpanCount.getGridSpanCount;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link JelajahFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class JelajahFragment extends Fragment implements SugestAdapter.OnItemClickListener,IklanAdapter.OnItemClickListener, JelajahArtikelAdapter.OnItemClickListener {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private static final String TAG = "JelajahFragment";

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
    private String key_value="";

    private Context context;
    
    private TextView button_jelajah;

    //menu banner
    private IklanAdapter iklan_adapter;
    private List<IklanModel> iklan_list;
    private RecyclerView recyclerView_iklan;


    private RecyclerView recyclerView;

    private NestedScrollView scrollView;


    public JelajahFragment() {
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
    public static JelajahFragment newInstance(String param1, String param2) {
        JelajahFragment fragment = new JelajahFragment();
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
        View v = inflater.inflate(R.layout.fragment_jelajah, container, false);
        key = v.findViewById(R.id.key);
        et_nama_wilayah = v.findViewById(R.id.et_nama_wilayah);
        iv_wilayah = v.findViewById(R.id.iv_wilayah);

        button_jelajah = v.findViewById(R.id.button_jelajah);

        recyclerView = v.findViewById(R.id.recyclerView);
        scrollView = v.findViewById(R.id.scroll_view);

        recyclerView_iklan = v.findViewById(R.id.list_iklan);
        context=getActivity().getApplicationContext();

        mListView_sugest = v.findViewById(R.id.list_sugest);
        return  v;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        iklan_adapter = new IklanAdapter(getActivity(), this);
        list_wilayah=new ArrayList<>();

        recyclerView_iklan.setLayoutManager(new GridLayoutManager(getContext(), getGridSpanCount(getActivity())));
        recyclerView_iklan.setHasFixedSize(true);
        recyclerView_iklan.setNestedScrollingEnabled(false);
        recyclerView_iklan.setAdapter(iklan_adapter);
        load_iklan();

        et_nama_wilayah.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                list_wilayah.clear();
                popUpWilayah(et_nama_wilayah);
            }
        });

        iv_wilayah.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                list_wilayah.clear();
                popUpWilayah(et_nama_wilayah);
            }
        });

        button_jelajah.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validasi();
            }
        });

        mListView_sugest.setVisibility(View.GONE);
        adapter_sugest = new SugestAdapter(getActivity(), this);

        list_sugest=new ArrayList<>();

        LinearLayoutManager layoutManagerKategori = new LinearLayoutManager(context);
        layoutManagerKategori.setOrientation(LinearLayoutManager.VERTICAL);
        mListView_sugest.setLayoutManager(layoutManagerKategori);
        mListView_sugest.setHasFixedSize(true);
        mListView_sugest.setNestedScrollingEnabled(false);
        mListView_sugest.setAdapter(adapter_sugest);
        key.addTextChangedListener(textWatcher);
    }

    private TextWatcher textWatcher=new TextWatcher() {

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            // TODO Auto-generated method stub
            list_sugest.clear();
            mListView_sugest.setVisibility(View.GONE);
//            CariSugest(s.toString());

        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count,
                                      int after) {
            // TODO Auto-generated method stub

        }

        private Timer timer=new Timer();
        private final long DELAY = 750; // milliseconds

        @Override
        public void afterTextChanged(final Editable s) {
            timer.cancel();
            timer = new Timer();
            timer.schedule(
                    new TimerTask() {
                        @Override
                        public void run() {
                            CariSugest(s.toString());
                        }
                    },
                    DELAY
            );
        }
    };

    private void CariSugest(String cari) {
        try {
            list_sugest.clear();
            RequestQueue requestQueue = Volley.newRequestQueue(context);
            JSONObject json = new JSONObject();
            json.put("keyword", cari);
            JsonRequest request = new JsonObjectRequest(Request.Method.POST, SUGEST_JELAJAH, json, new Response.Listener<JSONObject>() {
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

    private void validasi() {
        if (kode_wilayah.equalsIgnoreCase("")){
            et_nama_wilayah.setError("Pilih daerah");
        } else {
            Intent intent = new Intent(getContext(), ListJelajahActivity.class);
            intent.putExtra("kode_wilayah", kode_wilayah);
            intent.putExtra("key", key.getText().toString().trim());
            if (key_value.equalsIgnoreCase("")){
                intent.putExtra("key_value", key.getText().toString().trim());
            }else {
                intent.putExtra("key_value", key_value);
            }
            getContext().startActivity(intent);
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

        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage("Preparing data..");
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

    private void load_iklan() {
        iklan_list=new ArrayList<>();
        iklan_list.clear();
        final RequestQueue requestQueue = Volley.newRequestQueue(context);
        StringRequest request = new StringRequest(Request.Method.GET, Constant.URL.JELAJAH_IKLAN, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.e(TAG, "onResponse JELAJAH_IKLAN" + response);
                try {
                    JSONObject object = new JSONObject(response);
                    int status = object.getInt("status");
                    String message = object.getString("message");
                    if (status == 200 && !message.equalsIgnoreCase("") ) {
                        String data = object.getString("data");
                        JSONArray array = new JSONArray(data);
                        for (int i = 0; i < array.length(); i++) {
                            JSONObject obj = array.getJSONObject(i);
                            String iklan_url = obj.getString("iklan_url");
                            String iklan_gambar = obj.getString("iklan_gambar");

                            IklanModel item = new IklanModel(iklan_url, iklan_gambar);
                            iklan_list.add(item);
                        }
                        iklan_adapter.addList(iklan_list);
                        iklan_adapter.notifyDataSetChanged();
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

    @Override
    public void onItemClicked(int position, IklanModel item) {
        String url = item.getIklan_url();

        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setData(Uri.parse(url));
        startActivity(i);
    }

    @Override
    public void onItemClicked(int position, JelajahArtikelModel item) {

    }

    @Override
    public void onItemClicked(int position, Sugest item) {
        list_sugest.clear();
        mListView_sugest.setVisibility(View.GONE);

        key.setError(null);
        key.setText(item.getSuggestion());
        key_value=item.getValue();
        key.setSelection(key.getText().length());
        validasi();
    }

    @Override
    public void onResume() {
        super.onResume();
        key_value="";
        mListView_sugest.setVisibility(View.GONE);
    }
}
