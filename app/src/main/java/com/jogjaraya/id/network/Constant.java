package com.jogjaraya.id.network;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkError;
import com.android.volley.NoConnectionError;
import com.android.volley.ParseError;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.jogjaraya.id.activity.MainActivity;

import java.util.HashMap;
import java.util.Map;

public class Constant {

    public static class URL{
        private static final String URL              = "https://jogjaraya.id/api/";

        public static final String open_html        = "<!DOCTYPE html><html><head><style type=\"text/css\">@font-face {font-family: MyFont;src: url(\"file:///android_res/font/helvetica.ttf\")}body {font-family: MyFont;font-size: 14px;text-align: justify; color: #404040; line-height: 26px;}</style></head><body>";
        public static final String close_html =     "</body></html>";

        public static final String GET_MENU_BANNER   = URL+"home";
        public static final String GET_MENU_ICON     = URL+"home/menu_icon";
        public static final String HOME_ARTIKEL      = URL+"home/artikel";
        public static final String DETAIL_ARTIKEL    = URL+"ensiklo/detail/";
        public static final String LIST_ARTIKEL      = URL+"ensiklo/lists/";
        public static final String ARTIKEL_BY_KATEGORI = URL+"ensiklo/artikel_by_kategori/";
        public static final String CARI_ARTIKEL      = URL+"ensiklo/cari/";
        public static final String LIST_WILAYAH      = URL+"jelajah/wilayah";
        public static final String JELAJAH_IKLAN     = URL+"jelajah/iklan";
        public static final String JELAJAH_CARI      = URL+"jelajah/cari/";
        public static final String DETAIL_JELAJAH    = URL+"jelajah/detail/";
        public static final String INFO_API          = URL+"info";
        public static final String INFO_PROFILE      = URL+"info/profil";
        public static final String INFO_DARURAT      = URL+"info/darurat";
        public static final String DETAIL_DARURAT    = URL+"info/detail/";
        public static final String CARI_AGENDA       = URL+"agenda/cari/";
        public static final String DETAIL_AGENDA     = URL+"agenda/detail/";
        public static final String UPDATE_TOKEN      = URL+"fcm";
        public static final String SUGEST_JELAJAH    = URL+"jelajah/suggestion";
        public static final String SUGEST_AGENDA     = URL+"agenda/suggestion";


        private static final String AUTH_KEY         = "J0gj4k1t4.id";
        private static final String CLIENT_SERVICE   = "frontend-client";
    }


    public static DefaultRetryPolicy getDefaultRetryPolicy(){
        return new DefaultRetryPolicy(30000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
    }

    public static Map<String,String> getHeaders() {
        Map<String,String> headers = new HashMap<>();
        headers.put("Auth-Key", URL.AUTH_KEY);
        headers.put("Client-Service", URL.CLIENT_SERVICE);
        return headers;
    }
    public static void parseError(Context context, VolleyError error){
        if (context == null || error == null)return;
        if (error instanceof NetworkError){
//            Log.e(String.valueOf(context), "Network issue, tejadi kesalahan jaringan, periksa koneksi internet anda");
            Toast.makeText(context, "Network issue, tejadi kesalahan jaringan, periksa koneksi internet anda", Toast.LENGTH_LONG).show();
        }else if (error instanceof NoConnectionError){
//            Log.e(String.valueOf(context), "Network issue, tidak ada koneksi internet, periksa koneksi internet anda");
            Toast.makeText(context, "Network issue, tidak ada koneksi internet, periksa koneksi internet anda", Toast.LENGTH_LONG).show();
        }else if (error instanceof ServerError){
//            Log.e(String.valueOf(context), "Server issue, Kesalahan Merespon");
            Toast.makeText(context, "Server issue, Kesalahan Merespon", Toast.LENGTH_LONG).show();
        }else if (error instanceof TimeoutError){
//            Log.e(String.valueOf(context), "Network issue, Connections timeout");
            Toast.makeText(context, "Network issue, Connections timeout", Toast.LENGTH_LONG).show();
        }else if (error instanceof ParseError){
//            Log.e(String.valueOf(context), "Kesalahan Parsing");
            Toast.makeText(context, "Kesalahan Parsing", Toast.LENGTH_LONG).show();
        }else if (error instanceof AuthFailureError){
//            Log.e(String.valueOf(context), "kesalahan autentikasi");
            Toast.makeText(context, "kesalahan autentikasi", Toast.LENGTH_LONG).show();
        }else {
            Toast.makeText(context, "Opps, Something wrong!", Toast.LENGTH_LONG).show();
        }
    }
}
