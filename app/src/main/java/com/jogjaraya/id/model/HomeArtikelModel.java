package com.jogjaraya.id.model;

public class HomeArtikelModel {

    private String tulisan_slug, tulisan_judul,tulisan_isi,tulisan_gambar,tulisan_tanggal,tulisan_views;

    public HomeArtikelModel(String tulisan_slug, String tulisan_judul, String tulisan_isi, String tulisan_gambar, String tulisan_tanggal,String tulisan_views) {
        this.tulisan_slug=tulisan_slug;
        this.tulisan_views=tulisan_views;
        this.tulisan_judul=tulisan_judul;
        this.tulisan_isi=tulisan_isi;
        this.tulisan_gambar=tulisan_gambar;
        this.tulisan_tanggal=tulisan_tanggal;
    }

    public String getTulisan_slug() {
        return tulisan_slug;
    }

    public void setTulisan_slug(String tulisan_slug) {
        this.tulisan_slug = tulisan_slug;
    }

    public String getTulisan_views() {
        return tulisan_views;
    }

    public void setTulisan_views(String tulisan_views) {
        this.tulisan_views = tulisan_views;
    }

    public String getTulisan_judul() {
        return tulisan_judul;
    }

    public void setTulisan_judul(String tulisan_judul) {
        this.tulisan_judul = tulisan_judul;
    }

    public String getTulisan_isi() {
        return tulisan_isi;
    }

    public void setTulisan_isi(String tulisan_isi) {
        this.tulisan_isi = tulisan_isi;
    }

    public String getTulisan_gambar() {
        return tulisan_gambar;
    }

    public void setTulisan_gambar(String tulisan_gambar) {
        this.tulisan_gambar = tulisan_gambar;
    }

    public String getTulisan_tanggal() {
        return tulisan_tanggal;
    }

    public void setTulisan_tanggal(String tulisan_tanggal) {
        this.tulisan_tanggal = tulisan_tanggal;
    }
}
