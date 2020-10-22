package com.jogjaraya.id.fragment;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import android.text.util.Linkify;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.balysv.materialripple.MaterialRippleLayout;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.jogjaraya.id.BuildConfig;
import com.jogjaraya.id.R;
import com.jogjaraya.id.activity.DetailArtikelActivity;
import com.jogjaraya.id.adapter.DaruratAdapter;
import com.jogjaraya.id.adapter.ProfileAdapter;
import com.jogjaraya.id.model.DaruratModel;
import com.jogjaraya.id.model.ProfileModel;
import com.jogjaraya.id.network.Constant;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.jogjaraya.id.network.Constant.URL.INFO_API;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link InfoFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class InfoFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private static final String TAG = "InfoFragment";

    private ImageView banner;
    private TextView telp,email,web,alamat,copyright;
    private Context context;


    //klik
    private MaterialRippleLayout rv_darurat;
    private MaterialRippleLayout rv_info;



    //Darurat
    private List<ProfileModel> list_profile ;
    private ProfileAdapter adapter_profil;

    //Darurat
    private List<DaruratModel> list_ ;
    private DaruratAdapter adapter_;
    private ListView mListView;

    public InfoFragment() {
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
    public static InfoFragment newInstance(String param1, String param2) {
        InfoFragment fragment = new InfoFragment();
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
        View v =  inflater.inflate(R.layout.fragment_info, container, false);
        context=getActivity().getApplicationContext();
        copyright = v.findViewById(R.id.copyright);
        banner = v.findViewById(R.id.banner);
        telp = v.findViewById(R.id.telp);
        email = v.findViewById(R.id.email);
        alamat = v.findViewById(R.id.alamat);
        web = v.findViewById(R.id.tv_web);

        rv_darurat = v.findViewById(R.id.rv_darurat);
        rv_info = v.findViewById(R.id.rv_info);
        TextView versi = v.findViewById(R.id.text_version);
        versi.setText(getResources().getString(R.string.versi)+BuildConfig.VERSION_NAME);
        return v;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        loadDataInfo();

        rv_darurat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popUpDarurat();
            }
        });

        rv_info.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popUpProfile();
            }
        });

    }

    private void loadDataInfo() {
        final RequestQueue requestQueue = Volley.newRequestQueue(getContext());
        StringRequest request = new StringRequest(Request.Method.GET, INFO_API, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
//                Log.e(TAG, "onResponse" + response);
                try {
                    JSONObject object = new JSONObject(response);
                    int status = object.getInt("status");
                    String message = object.getString("message");
                    if (status == 200 && !message.equalsIgnoreCase("") ) {
                        String data = object.getString("data");
                        JSONObject obj = new JSONObject(data);
                        String info_alamat = obj.getString("info_alamat");
                        String info_email = obj.getString("info_email");
                        String info_telp = obj.getString("info_telp");
                        String info_link_nama = obj.getString("info_link_nama");
                        String info_link_url = obj.getString("info_link_url");
                        String info_gambar = obj.getString("info_gambar");
                        String info_copyright = obj.getString("info_copyright");
                        String info_website = obj.getString("info_website");

                        setViewInfo(info_alamat,info_email,info_telp,info_link_nama,info_link_url,info_gambar,info_copyright,info_website);


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
    }

    private void setViewInfo(String info_alamat, String info_email, String info_telp,
                             String info_link_nama, String info_link_url, String info_gambar,
                             String info_copyright,String info_website) {

        Glide.with(context).load(info_gambar)
                .placeholder(R.drawable.loading_placeholder)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .into(banner);

        web.setText(info_website);
        alamat.setText(info_alamat);
        telp.setAutoLinkMask(Linkify.PHONE_NUMBERS);
        telp.setText(info_telp);
        email.setText(info_email);
        copyright.setText(context.getResources().getString(R.string.copyright)+ " " +info_copyright);
    }


    private void popUpDarurat() {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getLayoutInflater();
        @SuppressLint("InflateParams")
        final View dialogView = inflater.inflate(R.layout.dialog_darurat, null);
        dialogBuilder.setView(dialogView);

        final AlertDialog b = dialogBuilder.create();
        b.setCanceledOnTouchOutside(true);
        b.setCancelable(false);
        b.show();

        ImageView iv_back = dialogView.findViewById(R.id.iv_back);
        iv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                b.dismiss();
            }
        });
        TextView title = dialogView.findViewById(R.id.title);
        title.setText("INFO DARURAT");
        mListView = dialogView.findViewById(R.id.listItem);
        list_=new ArrayList<>();
        list_.clear();
        adapter_ = new DaruratAdapter(getActivity(), list_);

        mListView.setClickable(true);

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Object o = mListView.getItemAtPosition(i);
                DaruratModel cn = (DaruratModel) o;
                Intent intent = new Intent(getContext(), DetailArtikelActivity.class);
                intent.putExtra("slug", cn.getDarurat_slug());
                intent.putExtra("from", "info");
                startActivity(intent);
//                b.dismiss();
            }
        });

        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage("Preparing data..");
        progressDialog.show();

        load_darurat();
    }


    private ProgressDialog progressDialog;
    private void load_darurat() {
        list_.clear();
        final RequestQueue requestQueue = Volley.newRequestQueue(context);
        StringRequest request = new StringRequest(Request.Method.GET, Constant.URL.INFO_DARURAT, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.e(TAG, "onResponse INFO_DARURAT" + response);
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
                            String darurat_nama = obj.getString("darurat_nama");
                            String darurat_slug = obj.getString("darurat_slug");
                            DaruratModel item = new DaruratModel(darurat_nama, darurat_slug);
                            list_.add(item);
                            mListView.setAdapter(adapter_);
                        }
                        adapter_.setList(list_);
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


    private void popUpProfile() {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getLayoutInflater();
        @SuppressLint("InflateParams")
        final View dialogView = inflater.inflate(R.layout.dialog_darurat, null);
        dialogBuilder.setView(dialogView);

        final AlertDialog b = dialogBuilder.create();
        b.setCanceledOnTouchOutside(true);
        b.setCancelable(false);
        b.show();

        ImageView iv_back = dialogView.findViewById(R.id.iv_back);
        iv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                b.dismiss();
            }
        });
        TextView title = dialogView.findViewById(R.id.title);
        title.setText("INFO PROFIL");
        mListView = dialogView.findViewById(R.id.listItem);
        list_profile =new ArrayList<>();
        list_profile.clear();
        adapter_profil= new ProfileAdapter(getActivity(), list_profile);

        mListView.setClickable(true);

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Object o = mListView.getItemAtPosition(i);
                ProfileModel cn = (ProfileModel) o;
                Intent intent = new Intent(getContext(), DetailArtikelActivity.class);
                intent.putExtra("slug", cn.getProfil_slug());
                intent.putExtra("from", "info");
                startActivity(intent);
//                b.dismiss();
            }
        });

        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage("Preparing data..");
        progressDialog.show();

        load_profile();
    }


    private void load_profile() {
        list_profile.clear();
        final RequestQueue requestQueue = Volley.newRequestQueue(context);
        StringRequest request = new StringRequest(Request.Method.GET, Constant.URL.INFO_PROFILE, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.e(TAG, "onResponse INFO_PROFILE" + response);
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
                            String profil_nama = obj.getString("profil_nama");
                            String profil_slug = obj.getString("profil_slug");
                            ProfileModel item = new ProfileModel(profil_nama, profil_slug);
                            list_profile.add(item);
                            mListView.setAdapter(adapter_profil);
                        }
                        adapter_profil.setList(list_profile);
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
}
